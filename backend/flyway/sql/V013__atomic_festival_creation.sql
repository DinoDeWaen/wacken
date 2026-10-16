-- TASK-197: creation is one authorized, retryable transaction.
CREATE TABLE public.festival_creation_requests (
    request_id uuid PRIMARY KEY,
    group_id uuid NOT NULL REFERENCES public.groups(id),
    user_id uuid NOT NULL REFERENCES public.profiles(id),
    festival_id text NOT NULL REFERENCES public.festivals(id) ON DELETE CASCADE,
    payload jsonb NOT NULL,
    reused_bands integer NOT NULL,
    created_bands integer NOT NULL,
    prefilled_ratings integer NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now()
);
CREATE INDEX idx_festival_creation_requests_festival ON public.festival_creation_requests(festival_id);
ALTER TABLE public.festival_creation_requests ENABLE ROW LEVEL SECURITY;
CREATE POLICY festival_creation_requests_own_select ON public.festival_creation_requests
FOR SELECT TO authenticated USING (user_id=(SELECT auth.uid()) AND public.is_group_member(group_id));
CREATE POLICY festival_creation_requests_own_insert ON public.festival_creation_requests
FOR INSERT TO authenticated WITH CHECK (user_id=(SELECT auth.uid()) AND public.is_group_member(group_id));
REVOKE ALL ON public.festival_creation_requests FROM anon,authenticated;
GRANT SELECT, INSERT ON public.festival_creation_requests TO authenticated;

CREATE FUNCTION public.create_festival_from_bands(
    p_request_id uuid, p_group_id uuid, p_festival_id text,
    p_festival_name text, p_band_names text[]
) RETURNS jsonb
LANGUAGE plpgsql SECURITY INVOKER SET search_path=''
AS $function$
DECLARE
    actor uuid := auth.uid();
    names text[];
    command jsonb;
    previous public.festival_creation_requests%ROWTYPE;
    band_name text;
    selected_band_id text;
    latest_rating integer;
    reused integer := 0;
    created integer := 0;
    prefilled integer := 0;
    response jsonb;
BEGIN
    IF actor IS NULL OR p_group_id IS DISTINCT FROM '00000000-0000-0000-0000-000000000001'::uuid
       OR NOT public.is_group_member(p_group_id) THEN
        RAISE EXCEPTION 'Sign in as a group member before adding a festival.' USING ERRCODE='42501';
    END IF;
    IF p_request_id IS NULL OR nullif(btrim(p_festival_id),'') IS NULL
       OR nullif(btrim(p_festival_name),'') IS NULL THEN
        RAISE EXCEPTION 'Enter a festival name before adding the festival.';
    END IF;
    IF coalesce(cardinality(p_band_names),0)=0 OR cardinality(p_band_names)>5000
       OR EXISTS (SELECT 1 FROM unnest(p_band_names) n WHERE nullif(btrim(n),'') IS NULL) THEN
        RAISE EXCEPTION 'Select a valid bands CSV with at least one band.';
    END IF;
    SELECT array_agg(n ORDER BY n COLLATE "C") INTO names
    FROM (SELECT DISTINCT btrim(value) COLLATE "C" AS n FROM unnest(p_band_names) value) unique_names;
    command := jsonb_build_object('festival_id',btrim(p_festival_id),
        'festival_name',btrim(p_festival_name),'bands',to_jsonb(names));

    -- One shared group/active festival: serialize checks and writes, including retries.
    PERFORM pg_advisory_xact_lock(hashtextextended('wacken:festival:create:'||p_group_id::text,0));
    SELECT * INTO previous FROM public.festival_creation_requests WHERE request_id=p_request_id;
    IF FOUND THEN
        IF previous.group_id<>p_group_id OR previous.user_id<>actor OR previous.payload<>command THEN
            RAISE EXCEPTION 'This creation request belongs to different festival data.';
        END IF;
        IF NOT EXISTS (SELECT 1 FROM public.festivals WHERE id=previous.festival_id AND status='ACTIVE') THEN
            RAISE EXCEPTION 'This festival is archived. Use a different festival name for a new festival.';
        END IF;
        reused:=previous.reused_bands;
        created:=previous.created_bands;
        prefilled:=previous.prefilled_ratings;
    ELSE
        IF EXISTS (SELECT 1 FROM public.festivals WHERE id=btrim(p_festival_id)) THEN
            RAISE EXCEPTION 'A festival with this identity already exists. Use a different festival name.';
        END IF;
        IF EXISTS (SELECT 1 FROM public.festivals WHERE status='ACTIVE') THEN
            RAISE EXCEPTION 'Archive the active festival before adding the next one.';
        END IF;
        INSERT INTO public.festivals(id,name,status) VALUES(btrim(p_festival_id),btrim(p_festival_name),'ACTIVE');
        FOREACH band_name IN ARRAY names LOOP
            SELECT b.id INTO selected_band_id FROM public.bands b
            WHERE b.name COLLATE "C"=band_name COLLATE "C" ORDER BY b.created_at,b.id LIMIT 1;
            IF FOUND THEN
                reused:=reused+1;
            ELSE
                selected_band_id:='band-'||gen_random_uuid()::text;
                INSERT INTO public.bands(id,name,active) VALUES(selected_band_id,band_name,true);
                created:=created+1;
            END IF;
            INSERT INTO public.festival_lineup_entries(festival_id,band_id,uploaded_display_name)
            VALUES(btrim(p_festival_id),selected_band_id,band_name);
            SELECT e.rating INTO latest_rating FROM public.personal_band_rating_events e
            WHERE e.user_id=actor AND e.band_id=selected_band_id
            ORDER BY e.created_at DESC,e.id DESC LIMIT 1;
            IF FOUND THEN
                INSERT INTO public.festival_planning_ratings(group_id,user_id,festival_id,band_id,rating)
                VALUES(p_group_id,actor,btrim(p_festival_id),selected_band_id,latest_rating);
                prefilled:=prefilled+1;
            END IF;
        END LOOP;
        INSERT INTO public.festival_creation_requests
            (request_id,group_id,user_id,festival_id,payload,reused_bands,created_bands,prefilled_ratings)
        VALUES(p_request_id,p_group_id,actor,btrim(p_festival_id),command,reused,created,prefilled);
    END IF;
    -- Return the complete committed view, including metadata and ratings needed by Room.
    SELECT jsonb_build_object(
        'group_id',p_group_id,'user_id',actor,
        'reused_bands',reused,'created_bands',created,'prefilled_ratings',prefilled,
        'festivals',(SELECT coalesce(jsonb_agg(to_jsonb(f) ORDER BY f.id),'[]'::jsonb) FROM public.festivals f),
        'festival',to_jsonb(festival),
        'bands',(SELECT coalesce(jsonb_agg(to_jsonb(b) ORDER BY b.name),'[]'::jsonb)
                 FROM public.bands b JOIN public.festival_lineup_entries l ON l.band_id=b.id
                 WHERE l.festival_id=festival.id),
        'lineup',(SELECT coalesce(jsonb_agg(jsonb_build_object('festival_id',l.festival_id,
                  'band_name',b.name,'uploaded_display_name',l.uploaded_display_name) ORDER BY b.name),'[]'::jsonb)
                  FROM public.festival_lineup_entries l JOIN public.bands b ON b.id=l.band_id WHERE l.festival_id=festival.id),
        'planning_ratings',(SELECT coalesce(jsonb_agg(jsonb_build_object('band_name',b.name,'rating',r.rating)),'[]'::jsonb)
                  FROM public.festival_planning_ratings r JOIN public.bands b ON b.id=r.band_id
                  WHERE r.festival_id=festival.id AND r.group_id=p_group_id AND r.user_id=actor)
    ) INTO response FROM public.festivals festival WHERE festival.id=btrim(p_festival_id);
    RETURN response;
END
$function$;
REVOKE ALL ON FUNCTION public.create_festival_from_bands(uuid,uuid,text,text,text[]) FROM PUBLIC,anon;
GRANT EXECUTE ON FUNCTION public.create_festival_from_bands(uuid,uuid,text,text,text[]) TO authenticated;
NOTIFY pgrst,'reload schema';

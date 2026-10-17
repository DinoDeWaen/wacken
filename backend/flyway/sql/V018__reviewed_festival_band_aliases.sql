-- Accepted TASK156/TASK215 architecture: confirmed aliases and reviewed atomic creation.
CREATE FUNCTION wacken_private.band_alias_key(value text) RETURNS text
LANGUAGE sql IMMUTABLE STRICT SET search_path='' AS $$
 SELECT translate(btrim(regexp_replace(value,E'[ \\t\\r\\n\\f]+',' ','g')),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')
$$;
REVOKE ALL ON FUNCTION wacken_private.band_alias_key(text) FROM PUBLIC,anon;
GRANT EXECUTE ON FUNCTION wacken_private.band_alias_key(text) TO authenticated;

CREATE TABLE public.band_aliases (
 alias_key text PRIMARY KEY,
 alias_name text NOT NULL CHECK(length(btrim(alias_name))>0),
 band_id text NOT NULL REFERENCES public.bands(id),
 confirmed_by uuid NOT NULL,
 confirmed_at timestamptz NOT NULL DEFAULT now(),
 CHECK(alias_key=wacken_private.band_alias_key(alias_name))
);
CREATE INDEX band_aliases_band_id_idx ON public.band_aliases(band_id);
ALTER TABLE public.band_aliases ENABLE ROW LEVEL SECURITY;
CREATE POLICY band_aliases_read_members ON public.band_aliases FOR SELECT TO authenticated
 USING (public.is_group_member('00000000-0000-0000-0000-000000000001'));
REVOKE ALL ON public.band_aliases FROM PUBLIC,anon,authenticated;
GRANT SELECT ON public.band_aliases TO authenticated;
ALTER TABLE public.festival_creation_requests ADD COLUMN reviewed_payload jsonb;

CREATE FUNCTION wacken_private.create_festival_from_reviewed_bands(
 p_request_id uuid,p_group_id uuid,p_festival_id text,p_festival_name text,p_rows jsonb
) RETURNS jsonb LANGUAGE plpgsql SECURITY DEFINER SET search_path='' AS $$
DECLARE actor uuid:=auth.uid(); command jsonb; previous public.festival_creation_requests%ROWTYPE;
 row_data jsonb; target public.bands%ROWTYPE; selected_names text[]:='{}';
 names text[]; response jsonb; alias_id text; is_replay boolean; uploaded text;
BEGIN
 IF actor IS NULL OR p_group_id IS DISTINCT FROM '00000000-0000-0000-0000-000000000001'::uuid
    OR NOT public.is_group_member(p_group_id) THEN
  RAISE EXCEPTION 'Sign in as a group member before adding a festival.' USING ERRCODE='42501';
 END IF;
 IF jsonb_typeof(p_rows) IS DISTINCT FROM 'array' OR jsonb_array_length(p_rows) NOT BETWEEN 1 AND 5000 THEN
  RAISE EXCEPTION 'Review a valid nonempty bands CSV.' USING ERRCODE='22023';
 END IF;
 IF EXISTS(SELECT 1 FROM jsonb_array_elements(p_rows) r WHERE jsonb_typeof(r) IS DISTINCT FROM 'object'
       OR jsonb_typeof(r->'uploaded_name') IS DISTINCT FROM 'string'
       OR nullif(btrim(r->>'uploaded_name'),'') IS NULL
       OR jsonb_typeof(r->'remember_alias') IS DISTINCT FROM 'boolean'
       OR (r->'band_id' IS NOT NULL AND r->'band_id'<>'null'::jsonb AND jsonb_typeof(r->'band_id')<>'string')
       OR ((r->>'remember_alias')::boolean AND nullif(r->>'band_id','') IS NULL))
    OR (SELECT count(*) FROM jsonb_array_elements(p_rows))<>
       (SELECT count(DISTINCT btrim(r->>'uploaded_name') COLLATE "C") FROM jsonb_array_elements(p_rows) r) THEN
  RAISE EXCEPTION 'Review each valid imported name once.' USING ERRCODE='22023';
 END IF;
 SELECT jsonb_build_object('festival_id',btrim(p_festival_id),'festival_name',btrim(p_festival_name),
   'rows',jsonb_agg(jsonb_build_object('uploaded_name',btrim(r->>'uploaded_name'),'band_id',r->>'band_id',
       'band_name',r->>'band_name','remember_alias',(r->>'remember_alias')::boolean)
       ORDER BY btrim(r->>'uploaded_name') COLLATE "C"))
 INTO command FROM jsonb_array_elements(p_rows) r;
 PERFORM pg_advisory_xact_lock(hashtextextended('wacken:festival:create:'||p_group_id::text,0));
 SELECT * INTO previous FROM public.festival_creation_requests WHERE request_id=p_request_id;
 is_replay:=FOUND;
 IF is_replay THEN
  IF previous.user_id<>actor OR previous.group_id<>p_group_id OR previous.reviewed_payload IS DISTINCT FROM command THEN
   RAISE EXCEPTION 'This request belongs to different reviewed festival data.' USING ERRCODE='22023';
  END IF;
  SELECT array_agg(n) INTO names FROM jsonb_array_elements_text(previous.payload->'bands') n;
 ELSE
  -- Also exclude legacy direct catalog inserts/renames between reviewed identity validation
  -- and the compatible exact-name operation. The group lifecycle lock is acquired first.
  LOCK TABLE public.bands IN SHARE ROW EXCLUSIVE MODE;
  FOR row_data IN SELECT value FROM jsonb_array_elements(command->'rows') LOOP
   uploaded:=row_data->>'uploaded_name';
   IF row_data->>'band_id' IS NOT NULL THEN
    SELECT * INTO target FROM public.bands WHERE id=row_data->>'band_id' FOR SHARE;
    IF NOT FOUND OR target.name IS DISTINCT FROM row_data->>'band_name' OR NOT target.active THEN
     RAISE EXCEPTION 'A selected catalog band changed. Review the CSV again.' USING ERRCODE='22023';
    END IF;
   ELSE
    -- An exact published identity can be reused; aliases/fuzzy matches never choose a band.
    IF (SELECT count(*) FROM public.bands WHERE name COLLATE "C"=uploaded COLLATE "C")>1 THEN
     RAISE EXCEPTION 'Duplicate exact catalog names require review.' USING ERRCODE='22023';
    END IF;
    SELECT * INTO target FROM public.bands WHERE name COLLATE "C"=uploaded COLLATE "C";
    IF NOT FOUND THEN target.id:=NULL;target.name:=uploaded; END IF;
   END IF;
   selected_names:=array_append(selected_names,target.name);
   IF (SELECT count(*) FROM public.bands WHERE name COLLATE "C"=target.name COLLATE "C")>1 THEN
    RAISE EXCEPTION 'Duplicate catalog names require review.' USING ERRCODE='22023';
   END IF;
   IF (row_data->>'remember_alias')::boolean AND uploaded<>target.name THEN
    SELECT band_id INTO alias_id FROM public.band_aliases WHERE alias_key=wacken_private.band_alias_key(uploaded);
    IF (FOUND AND alias_id<>target.id) OR EXISTS(SELECT 1 FROM public.bands
       WHERE name COLLATE "C"=uploaded COLLATE "C" AND id<>target.id) THEN
     RAISE EXCEPTION 'This alias already identifies a different band. Review the CSV again.' USING ERRCODE='22023';
    END IF;
    INSERT INTO public.band_aliases(alias_key,alias_name,band_id,confirmed_by)
    VALUES(wacken_private.band_alias_key(uploaded),uploaded,target.id,actor)
    ON CONFLICT(alias_key) DO NOTHING;
   END IF;
  END LOOP;
  names:=selected_names;
 END IF;
 -- Reuse the compatible creation implementation: group lock, ledger, canonical metadata,
 -- each member's once-only established planning snapshot and current committed response.
 response:=wacken_private.create_festival_from_bands(p_request_id,p_group_id,p_festival_id,p_festival_name,names);
 IF NOT is_replay THEN
  UPDATE public.festival_creation_requests SET reviewed_payload=command WHERE request_id=p_request_id;
  -- More than one reviewed spelling may select one identity; preserve one deterministic display
  -- name on the lineup and every spelling/choice in the immutable reviewed request payload.
  UPDATE public.festival_lineup_entries l SET uploaded_display_name=choices.display
  FROM (SELECT name,min(display COLLATE "C") AS display FROM
    unnest(selected_names,ARRAY(SELECT r->>'uploaded_name' FROM jsonb_array_elements(command->'rows') r))
      AS selected(name,display) GROUP BY name) choices
  JOIN public.bands b ON b.name COLLATE "C"=choices.name COLLATE "C"
  WHERE l.festival_id=btrim(p_festival_id) AND l.band_id=b.id;
 END IF;
 RETURN response || jsonb_build_object(
  'lineup',(SELECT coalesce(jsonb_agg(jsonb_build_object('festival_id',l.festival_id,'band_name',b.name,
    'uploaded_display_name',l.uploaded_display_name) ORDER BY b.name),'[]'::jsonb)
    FROM public.festival_lineup_entries l JOIN public.bands b ON b.id=l.band_id WHERE l.festival_id=btrim(p_festival_id)),
  'aliases',(SELECT coalesce(jsonb_agg(jsonb_build_object('alias_name',a.alias_name,'alias_key',a.alias_key,
    'band_id',b.id,'band_name',b.name,'confirmed_by',a.confirmed_by,'confirmed_at',a.confirmed_at) ORDER BY a.alias_key),'[]'::jsonb)
    FROM public.band_aliases a JOIN public.bands b ON b.id=a.band_id));
END
$$;
REVOKE ALL ON FUNCTION wacken_private.create_festival_from_reviewed_bands(uuid,uuid,text,text,jsonb) FROM PUBLIC,anon;
GRANT EXECUTE ON FUNCTION wacken_private.create_festival_from_reviewed_bands(uuid,uuid,text,text,jsonb) TO authenticated;
CREATE FUNCTION public.create_festival_from_reviewed_bands(
 p_request_id uuid,p_group_id uuid,p_festival_id text,p_festival_name text,p_rows jsonb
) RETURNS jsonb LANGUAGE sql SECURITY INVOKER SET search_path='' AS $$
 SELECT wacken_private.create_festival_from_reviewed_bands(p_request_id,p_group_id,p_festival_id,p_festival_name,p_rows)
$$;
REVOKE ALL ON FUNCTION public.create_festival_from_reviewed_bands(uuid,uuid,text,text,jsonb) FROM PUBLIC,anon;
GRANT EXECUTE ON FUNCTION public.create_festival_from_reviewed_bands(uuid,uuid,text,text,jsonb) TO authenticated;
NOTIFY pgrst,'reload schema';

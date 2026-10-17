-- TASK-218 / accepted ADR-0016: archive and promotion are one authorized transaction.
CREATE TABLE public.festival_archive_operations (
    festival_id text PRIMARY KEY REFERENCES public.festivals(id) ON DELETE CASCADE,
    operation_id uuid NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    actor_id uuid NOT NULL REFERENCES public.profiles(id),
    archived_at timestamptz NOT NULL
);
CREATE INDEX festival_archive_operations_actor ON public.festival_archive_operations(actor_id);
ALTER TABLE public.festival_archive_operations ENABLE ROW LEVEL SECURITY;
REVOKE ALL ON public.festival_archive_operations FROM PUBLIC,anon,authenticated;

-- Invoker trigger: authenticated status writes cannot bypass the private transaction.
CREATE FUNCTION wacken_private.guard_festival_status() RETURNS trigger
LANGUAGE plpgsql SECURITY INVOKER SET search_path='' AS $function$
BEGIN
    IF OLD.status IS DISTINCT FROM NEW.status AND current_user<>(
        SELECT pg_catalog.pg_get_userbyid(c.relowner) FROM pg_catalog.pg_class c WHERE c.oid=TG_RELID) THEN
        RAISE EXCEPTION 'Update the app and use Archive to save festival ratings together.' USING ERRCODE='42501';
    END IF;
    RETURN NEW;
END $function$;
REVOKE ALL ON FUNCTION wacken_private.guard_festival_status() FROM PUBLIC,anon,authenticated;
CREATE TRIGGER guard_festival_status BEFORE UPDATE OF status ON public.festivals
FOR EACH ROW EXECUTE FUNCTION wacken_private.guard_festival_status();

CREATE FUNCTION wacken_private.archive_festival(p_festival_id text) RETURNS jsonb
LANGUAGE plpgsql SECURITY DEFINER SET search_path='' AS $function$
DECLARE actor uuid:=auth.uid();
        selected public.festivals%ROWTYPE;
        prior public.festival_archive_operations%ROWTYPE;
        archive_time timestamptz;
BEGIN
    IF actor IS NULL OR NOT public.is_group_member('00000000-0000-0000-0000-000000000001') THEN
        RAISE EXCEPTION 'Sign in as a group member before archiving.' USING ERRCODE='42501';
    END IF;
    IF nullif(btrim(p_festival_id),'') IS NULL THEN
        RAISE EXCEPTION 'Select an active festival to archive.' USING ERRCODE='22023';
    END IF;
    -- Same lifecycle lock as creation; row lock also serializes after-rating writes.
    PERFORM pg_advisory_xact_lock(hashtextextended('wacken:festival:create:00000000-0000-0000-0000-000000000001',0));
    SELECT * INTO selected FROM public.festivals WHERE id=p_festival_id FOR UPDATE;
    IF NOT FOUND THEN RAISE EXCEPTION 'Festival is no longer available. Refresh the festival list.' USING ERRCODE='22023'; END IF;
    SELECT * INTO prior FROM public.festival_archive_operations WHERE festival_id=p_festival_id;
    IF FOUND THEN
        IF selected.status<>'ARCHIVED' THEN RAISE EXCEPTION 'Archive state requires administrator review.' USING ERRCODE='22023'; END IF;
        RETURN jsonb_build_object('confirmed',true,'festival_id',selected.id,'operation_id',prior.operation_id,'archived_at',prior.archived_at,
            'festival',jsonb_build_object('id',selected.id,'name',selected.name,'status','ARCHIVED'));
    END IF;
    IF selected.status<>'ACTIVE' THEN
        RAISE EXCEPTION 'This festival is already archived. Refresh the festival list.' USING ERRCODE='22023';
    END IF;
    IF EXISTS(SELECT 1 FROM public.festival_after_ratings a JOIN public.group_members m ON m.user_id=a.user_id
              JOIN public.festival_lineup_entries l ON l.festival_id=a.festival_id AND l.band_id=a.band_id
              WHERE a.festival_id=selected.id AND a.requires_review AND m.group_id='00000000-0000-0000-0000-000000000001') THEN
        RAISE EXCEPTION 'Members must review recovered after-ratings before archiving. Open the festival band details and select each final value or reset.' USING ERRCODE='22023';
    END IF;
    archive_time:=clock_timestamp();
    INSERT INTO public.personal_band_ratings(user_id,band_id,rating,source_festival_id,source_festival_name,source_event_id,established_at)
    SELECT a.user_id,a.band_id,a.rating,selected.id,selected.name,a.operation_id,archive_time
    FROM public.festival_after_ratings a
    JOIN public.group_members m ON m.user_id=a.user_id AND m.group_id='00000000-0000-0000-0000-000000000001'
    JOIN public.festival_lineup_entries l ON l.festival_id=a.festival_id AND l.band_id=a.band_id
    WHERE a.festival_id=selected.id AND a.rating>0 AND NOT a.requires_review
    ON CONFLICT(user_id,band_id) DO UPDATE SET rating=excluded.rating,source_festival_id=excluded.source_festival_id,
        source_festival_name=excluded.source_festival_name,source_event_id=excluded.source_event_id,established_at=excluded.established_at;
    UPDATE public.festivals SET status='ARCHIVED',archived_at=archive_time WHERE id=selected.id;
    INSERT INTO public.festival_archive_operations(festival_id,actor_id,archived_at)
    VALUES(selected.id,actor,archive_time) RETURNING * INTO prior;
    RETURN jsonb_build_object('confirmed',true,'festival_id',selected.id,'operation_id',prior.operation_id,'archived_at',prior.archived_at,
        'festival',jsonb_build_object('id',selected.id,'name',selected.name,'status','ARCHIVED'));
END $function$;
REVOKE ALL ON FUNCTION wacken_private.archive_festival(text) FROM PUBLIC,anon;
GRANT EXECUTE ON FUNCTION wacken_private.archive_festival(text) TO authenticated;
CREATE FUNCTION public.archive_festival(p_festival_id text) RETURNS jsonb
LANGUAGE sql SECURITY INVOKER SET search_path='' AS $function$
    SELECT wacken_private.archive_festival(p_festival_id)
$function$;
REVOKE ALL ON FUNCTION public.archive_festival(text) FROM PUBLIC,anon;
GRANT EXECUTE ON FUNCTION public.archive_festival(text) TO authenticated;
NOTIFY pgrst,'reload schema';

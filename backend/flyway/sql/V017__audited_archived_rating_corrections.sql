-- TASK-172 / ADR-0016: explicit own archived correction, revision and immutable audit.
ALTER TABLE public.festival_after_ratings ADD COLUMN revision bigint NOT NULL DEFAULT 0;
ALTER TABLE public.festival_planning_ratings ADD COLUMN revision bigint NOT NULL DEFAULT 0;
CREATE FUNCTION wacken_private.bump_rating_revision() RETURNS trigger LANGUAGE plpgsql
SECURITY INVOKER SET search_path='' AS $function$
BEGIN
    IF TG_OP='INSERT' THEN NEW.revision:=0; ELSE NEW.revision:=OLD.revision+1; END IF;
    RETURN NEW;
END $function$;
REVOKE ALL ON FUNCTION wacken_private.bump_rating_revision() FROM PUBLIC,anon,authenticated;
CREATE TRIGGER rating_revision BEFORE INSERT OR UPDATE ON public.festival_after_ratings
FOR EACH ROW EXECUTE FUNCTION wacken_private.bump_rating_revision();
CREATE TRIGGER rating_revision BEFORE INSERT OR UPDATE ON public.festival_planning_ratings
FOR EACH ROW EXECUTE FUNCTION wacken_private.bump_rating_revision();

CREATE TABLE public.rating_corrections (
    operation_id uuid PRIMARY KEY,
    user_id uuid NOT NULL,
    festival_id text NOT NULL,
    festival_name_snapshot text NOT NULL,
    band_id text NOT NULL,
    band_name_snapshot text NOT NULL,
    kind text NOT NULL CHECK(kind IN ('PLANNING','AFTER')),
    previous_value integer NOT NULL CHECK(previous_value BETWEEN 0 AND 5),
    new_value integer NOT NULL CHECK(new_value BETWEEN 0 AND 5 AND (kind='PLANNING' OR new_value>0)),
    expected_revision bigint NOT NULL CHECK(expected_revision>= -1),
    original_recorded_at timestamptz,
    corrected_at timestamptz NOT NULL,
    payload jsonb NOT NULL
);
-- Snapshots intentionally survive festival/catalog/user deletion; no cascading FK.
CREATE INDEX rating_corrections_owner_festival ON public.rating_corrections(user_id,festival_id,corrected_at);
ALTER TABLE public.rating_corrections ENABLE ROW LEVEL SECURITY;
REVOKE ALL ON public.rating_corrections FROM PUBLIC,anon,authenticated;
GRANT SELECT ON public.rating_corrections TO authenticated;
REVOKE DELETE ON public.personal_band_rating_events FROM authenticated;
CREATE POLICY rating_corrections_own_read ON public.rating_corrections FOR SELECT TO authenticated
USING(user_id=(SELECT auth.uid()) AND public.is_group_member('00000000-0000-0000-0000-000000000001'));

-- Old clients cannot bypass the audit by editing an archived target directly.
CREATE FUNCTION wacken_private.guard_archived_rating_write() RETURNS trigger LANGUAGE plpgsql
SECURITY INVOKER SET search_path='' AS $function$
DECLARE old_festival text; new_festival text;
BEGIN
    IF current_user=(SELECT pg_catalog.pg_get_userbyid(c.relowner) FROM pg_catalog.pg_class c WHERE c.oid=TG_RELID) THEN
        IF TG_OP='DELETE' THEN RETURN OLD; ELSE RETURN NEW; END IF;
    END IF;
    IF TG_OP<>'INSERT' THEN old_festival:=OLD.festival_id; END IF;
    IF TG_OP<>'DELETE' THEN new_festival:=NEW.festival_id; END IF;
    -- A matching history replay does not change the opinion or original date.
    -- FK detach on actual festival deletion retains history and its name snapshot.
    IF TG_TABLE_NAME='personal_band_rating_events' AND TG_OP='UPDATE' THEN
        IF NEW.id=OLD.id AND NEW.user_id=OLD.user_id AND NEW.band_id=OLD.band_id AND NEW.rating=OLD.rating
           AND NEW.created_at=OLD.created_at AND (
             NEW.festival_id IS NOT DISTINCT FROM OLD.festival_id OR
             (NEW.festival_id IS NULL AND NOT EXISTS(SELECT 1 FROM public.festivals WHERE id=OLD.festival_id))) THEN RETURN NEW; END IF;
    END IF;
    IF EXISTS(SELECT 1 FROM public.festivals WHERE id IN(old_festival,new_festival) AND status='ARCHIVED') THEN
        RAISE EXCEPTION 'Use explicit archived rating correction so the change is audited.' USING ERRCODE='42501';
    END IF;
    IF TG_OP='DELETE' THEN RETURN OLD; ELSE RETURN NEW; END IF;
END $function$;
REVOKE ALL ON FUNCTION wacken_private.guard_archived_rating_write() FROM PUBLIC,anon,authenticated;
CREATE TRIGGER guard_archived_rating_write BEFORE INSERT OR UPDATE OR DELETE ON public.festival_planning_ratings
FOR EACH ROW EXECUTE FUNCTION wacken_private.guard_archived_rating_write();
CREATE TRIGGER guard_archived_rating_write BEFORE INSERT OR UPDATE OR DELETE ON public.personal_band_rating_events
FOR EACH ROW EXECUTE FUNCTION wacken_private.guard_archived_rating_write();

CREATE FUNCTION wacken_private.review_archived_rating(p_festival_id text,p_band_id text,p_kind text)
RETURNS jsonb LANGUAGE plpgsql SECURITY DEFINER SET search_path='' AS $function$
DECLARE actor uuid:=auth.uid(); value integer:=0; target_revision bigint:=-1; recorded timestamptz;
BEGIN
    IF actor IS NULL OR NOT public.is_group_member('00000000-0000-0000-0000-000000000001') THEN
        RAISE EXCEPTION 'Sign in as a group member to review an archived rating.' USING ERRCODE='42501';
    END IF;
    IF p_kind IS NULL OR p_kind NOT IN('PLANNING','AFTER') OR NOT EXISTS(
        SELECT 1 FROM public.festivals f JOIN public.festival_lineup_entries l ON l.festival_id=f.id
        WHERE f.id=p_festival_id AND f.status='ARCHIVED' AND l.band_id=p_band_id) THEN
        RAISE EXCEPTION 'Choose a band in the selected archived festival.' USING ERRCODE='22023';
    END IF;
    IF p_kind='PLANNING' THEN
        SELECT rating,revision INTO value,target_revision FROM public.festival_planning_ratings
        WHERE user_id=actor AND festival_id=p_festival_id AND band_id=p_band_id
        AND group_id='00000000-0000-0000-0000-000000000001';
    ELSE
        SELECT rating,revision,recorded_at INTO value,target_revision,recorded FROM public.festival_after_ratings
        WHERE user_id=actor AND festival_id=p_festival_id AND band_id=p_band_id;
    END IF;
    RETURN jsonb_build_object('user_id',actor,'festival_id',p_festival_id,'band_id',p_band_id,
        'band_name',(SELECT name FROM public.bands WHERE id=p_band_id),'kind',p_kind,
        'rating',coalesce(value,0),'revision',coalesce(target_revision,-1),'recorded_at',recorded);
END $function$;
REVOKE ALL ON FUNCTION wacken_private.review_archived_rating(text,text,text) FROM PUBLIC,anon;
GRANT EXECUTE ON FUNCTION wacken_private.review_archived_rating(text,text,text) TO authenticated;
CREATE FUNCTION public.review_archived_rating(p_festival_id text,p_band_id text,p_kind text)
RETURNS jsonb LANGUAGE sql SECURITY INVOKER SET search_path='' AS $function$
 SELECT wacken_private.review_archived_rating(p_festival_id,p_band_id,p_kind)
$function$;
REVOKE ALL ON FUNCTION public.review_archived_rating(text,text,text) FROM PUBLIC,anon;
GRANT EXECUTE ON FUNCTION public.review_archived_rating(text,text,text) TO authenticated;

CREATE FUNCTION wacken_private.correct_archived_rating(p_operation_id uuid,p_festival_id text,p_band_id text,
    p_kind text,p_expected_rating integer,p_expected_revision bigint,p_new_rating integer)
RETURNS jsonb LANGUAGE plpgsql SECURITY DEFINER SET search_path='' AS $function$
DECLARE actor uuid:=auth.uid(); command jsonb; prior public.rating_corrections%ROWTYPE;
        reviewed jsonb; target_event uuid; recorded timestamptz; correction_time timestamptz;
BEGIN
    IF actor IS NULL OR NOT public.is_group_member('00000000-0000-0000-0000-000000000001') THEN
        RAISE EXCEPTION 'Sign in as a group member before correcting an archived rating.' USING ERRCODE='42501';
    END IF;
    IF p_operation_id IS NULL OR p_kind IS NULL OR p_kind NOT IN('PLANNING','AFTER')
       OR p_expected_rating IS NULL OR p_expected_rating NOT BETWEEN 0 AND 5
       OR p_expected_revision IS NULL OR p_expected_revision< -1 OR p_new_rating IS NULL
       OR p_new_rating NOT BETWEEN 0 AND 5 OR (p_kind='AFTER' AND p_new_rating=0) THEN
        RAISE EXCEPTION 'Choose a valid archived correction and reviewed value.' USING ERRCODE='22023';
    END IF;
    command:=jsonb_build_object('festival_id',p_festival_id,'band_id',p_band_id,'kind',p_kind,
        'expected_rating',p_expected_rating,'expected_revision',p_expected_revision,'new_rating',p_new_rating);
    -- Use the same lifecycle order as create/archive; correction cannot displace a later source.
    PERFORM pg_advisory_xact_lock(hashtextextended('wacken:festival:create:00000000-0000-0000-0000-000000000001',0));
    SELECT * INTO prior FROM public.rating_corrections WHERE operation_id=p_operation_id;
    IF FOUND THEN
        IF prior.user_id<>actor OR prior.payload<>command THEN
            RAISE EXCEPTION 'This correction operation belongs to different data.' USING ERRCODE='22023';
        END IF;
        RETURN jsonb_build_object('confirmed',true,'correction',to_jsonb(prior)-'payload');
    END IF;
    PERFORM 1 FROM public.festivals WHERE id=p_festival_id FOR SHARE;
    reviewed:=wacken_private.review_archived_rating(p_festival_id,p_band_id,p_kind);
    IF (reviewed->>'rating')::int<>p_expected_rating OR (reviewed->>'revision')::bigint<>p_expected_revision THEN
        RAISE EXCEPTION 'Reload the rating and review the latest value before correcting it.' USING ERRCODE='22023';
    END IF;
    correction_time:=clock_timestamp();
    recorded:=(reviewed->>'recorded_at')::timestamptz;
    IF p_kind='PLANNING' THEN
        INSERT INTO public.festival_planning_ratings(group_id,user_id,festival_id,band_id,rating)
        VALUES('00000000-0000-0000-0000-000000000001',actor,p_festival_id,p_band_id,p_new_rating)
        ON CONFLICT(group_id,user_id,festival_id,band_id) DO UPDATE SET rating=excluded.rating;
    ELSE
        SELECT operation_id INTO target_event FROM public.festival_after_ratings
        WHERE user_id=actor AND festival_id=p_festival_id AND band_id=p_band_id;
        target_event:=coalesce(target_event,p_operation_id);
        INSERT INTO public.festival_after_ratings(user_id,festival_id,band_id,operation_id,rating,recorded_at,requires_review)
        VALUES(actor,p_festival_id,p_band_id,target_event,p_new_rating,coalesce(recorded,correction_time),false)
        ON CONFLICT(user_id,festival_id,band_id) DO UPDATE SET rating=excluded.rating,requires_review=false;
        INSERT INTO public.personal_band_rating_events(id,user_id,band_id,festival_id,rating,created_at)
        VALUES(target_event,actor,p_band_id,p_festival_id,p_new_rating,coalesce(recorded,correction_time))
        ON CONFLICT(id) DO UPDATE SET rating=excluded.rating
        WHERE public.personal_band_rating_events.user_id=actor
          AND public.personal_band_rating_events.band_id=p_band_id
          AND public.personal_band_rating_events.festival_id=p_festival_id;
        IF NOT FOUND THEN RAISE EXCEPTION 'The original event requires administrator review.' USING ERRCODE='22023'; END IF;
        UPDATE public.personal_band_ratings SET rating=p_new_rating,source_event_id=target_event
        WHERE user_id=actor AND band_id=p_band_id AND source_festival_id=p_festival_id;
    END IF;
    INSERT INTO public.rating_corrections(operation_id,user_id,festival_id,festival_name_snapshot,band_id,band_name_snapshot,
        kind,previous_value,new_value,expected_revision,original_recorded_at,corrected_at,payload)
    VALUES(p_operation_id,actor,p_festival_id,(SELECT name FROM public.festivals WHERE id=p_festival_id),
        p_band_id,reviewed->>'band_name',p_kind,p_expected_rating,p_new_rating,p_expected_revision,recorded,correction_time,command)
    RETURNING * INTO prior;
    RETURN jsonb_build_object('confirmed',true,'correction',to_jsonb(prior)-'payload');
END $function$;
REVOKE ALL ON FUNCTION wacken_private.correct_archived_rating(uuid,text,text,text,integer,bigint,integer) FROM PUBLIC,anon;
GRANT EXECUTE ON FUNCTION wacken_private.correct_archived_rating(uuid,text,text,text,integer,bigint,integer) TO authenticated;
CREATE FUNCTION public.correct_archived_rating(p_operation_id uuid,p_festival_id text,p_band_id text,
    p_kind text,p_expected_rating integer,p_expected_revision bigint,p_new_rating integer)
RETURNS jsonb LANGUAGE sql SECURITY INVOKER SET search_path='' AS $function$
 SELECT wacken_private.correct_archived_rating(p_operation_id,p_festival_id,p_band_id,p_kind,p_expected_rating,p_expected_revision,p_new_rating)
$function$;
REVOKE ALL ON FUNCTION public.correct_archived_rating(uuid,text,text,text,integer,bigint,integer) FROM PUBLIC,anon;
GRANT EXECUTE ON FUNCTION public.correct_archived_rating(uuid,text,text,text,integer,bigint,integer) TO authenticated;
NOTIFY pgrst,'reload schema';

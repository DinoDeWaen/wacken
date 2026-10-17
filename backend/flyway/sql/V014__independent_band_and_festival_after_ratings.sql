-- TASK-216 / accepted ADR-0016. Existing events remain history, not the current preference source.
CREATE TABLE public.personal_band_ratings (
    user_id uuid NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    band_id text NOT NULL REFERENCES public.bands(id) ON DELETE RESTRICT,
    rating integer NOT NULL CHECK (rating BETWEEN 1 AND 5),
    source_festival_id text NOT NULL,
    source_festival_name text NOT NULL,
    source_event_id uuid NOT NULL,
    established_at timestamptz NOT NULL,
    PRIMARY KEY(user_id,band_id)
);
CREATE INDEX personal_band_ratings_band ON public.personal_band_ratings(band_id);
CREATE INDEX personal_band_ratings_source ON public.personal_band_ratings(source_festival_id);
CREATE TABLE public.festival_after_ratings (
    user_id uuid NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    festival_id text NOT NULL REFERENCES public.festivals(id) ON DELETE CASCADE,
    band_id text NOT NULL REFERENCES public.bands(id) ON DELETE RESTRICT,
    operation_id uuid NOT NULL,
    rating integer NOT NULL CHECK (rating BETWEEN 0 AND 5),
    recorded_at timestamptz NOT NULL,
    requires_review boolean NOT NULL,
    PRIMARY KEY(user_id,festival_id,band_id)
);
CREATE INDEX festival_after_ratings_festival ON public.festival_after_ratings(festival_id);
CREATE INDEX festival_after_ratings_band ON public.festival_after_ratings(band_id);
CREATE TABLE public.festival_after_rating_operations (
    operation_id uuid PRIMARY KEY,
    user_id uuid NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    festival_id text NOT NULL REFERENCES public.festivals(id) ON DELETE CASCADE,
    band_id text NOT NULL REFERENCES public.bands(id) ON DELETE RESTRICT,
    rating integer NOT NULL CHECK (rating BETWEEN 0 AND 5),
    recorded_at timestamptz NOT NULL,
    accepted_at timestamptz NOT NULL DEFAULT clock_timestamp()
);
CREATE INDEX festival_after_operations_user ON public.festival_after_rating_operations(user_id);
CREATE INDEX festival_after_operations_festival ON public.festival_after_rating_operations(festival_id);
CREATE INDEX festival_after_operations_band ON public.festival_after_rating_operations(band_id);

ALTER TABLE public.personal_band_ratings ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.festival_after_ratings ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.festival_after_rating_operations ENABLE ROW LEVEL SECURITY;
REVOKE ALL ON public.personal_band_ratings,public.festival_after_ratings,public.festival_after_rating_operations FROM PUBLIC,anon,authenticated;
GRANT SELECT ON public.personal_band_ratings,public.festival_after_ratings TO authenticated;
CREATE POLICY personal_band_ratings_own_read ON public.personal_band_ratings FOR SELECT TO authenticated
USING(user_id=(SELECT auth.uid()) AND public.is_group_member('00000000-0000-0000-0000-000000000001'));
CREATE POLICY festival_after_ratings_own_read ON public.festival_after_ratings FOR SELECT TO authenticated
USING(user_id=(SELECT auth.uid()) AND public.is_group_member('00000000-0000-0000-0000-000000000001'));

-- All legacy source rows remain intact. A recovered positive event cannot prove that an
-- old app did not later reset locally; require explicit review before future archive promotion.
INSERT INTO public.festival_after_ratings
SELECT DISTINCT ON(e.user_id,e.festival_id,e.band_id)
    e.user_id,e.festival_id,e.band_id,e.id,e.rating,e.created_at,true
FROM public.personal_band_rating_events e JOIN public.festivals f ON f.id=e.festival_id
WHERE e.rating BETWEEN 1 AND 5 AND e.created_at>'1970-01-01T00:00:00Z'
ORDER BY e.user_id,e.festival_id,e.band_id,e.created_at DESC,e.id DESC;
INSERT INTO public.personal_band_ratings
SELECT DISTINCT ON(e.user_id,e.band_id)
    e.user_id,e.band_id,e.rating,e.festival_id,coalesce(e.festival_name_snapshot,f.name),e.id,e.created_at
FROM public.personal_band_rating_events e JOIN public.festivals f ON f.id=e.festival_id
WHERE f.status='ARCHIVED' AND e.rating BETWEEN 1 AND 5 AND e.created_at>'1970-01-01T00:00:00Z'
ORDER BY e.user_id,e.band_id,e.created_at DESC,e.id DESC;

CREATE SCHEMA IF NOT EXISTS wacken_private;
REVOKE ALL ON SCHEMA wacken_private FROM PUBLIC,anon,authenticated;
GRANT USAGE ON SCHEMA wacken_private TO authenticated;

-- Only this validated business operation writes the new state. The narrowly scoped
-- privileged implementation lives outside exposed schemas; the public wrapper is INVOKER.
CREATE FUNCTION wacken_private.save_festival_after_rating(
    p_operation_id uuid,p_festival_id text,p_band_id text,p_rating integer,p_recorded_at timestamptz
) RETURNS jsonb LANGUAGE plpgsql SECURITY DEFINER SET search_path='' AS $function$
DECLARE actor uuid:=auth.uid();
        prior public.festival_after_rating_operations%ROWTYPE;
        festival_status text;
        response jsonb;
BEGIN
    IF actor IS NULL OR NOT public.is_group_member('00000000-0000-0000-0000-000000000001') THEN
        RAISE EXCEPTION 'Sign in as a group member before saving a festival after-rating.' USING ERRCODE='42501';
    END IF;
    IF p_operation_id IS NULL OR p_rating IS NULL OR p_rating<0 OR p_rating>5 OR p_recorded_at IS NULL
       OR nullif(btrim(p_festival_id),'') IS NULL OR nullif(btrim(p_band_id),'') IS NULL THEN
        RAISE EXCEPTION 'A valid festival after-rating and operation are required.' USING ERRCODE='22023';
    END IF;
    PERFORM pg_advisory_xact_lock(hashtextextended('wacken:after:operation:'||p_operation_id::text,0));
    SELECT * INTO prior FROM public.festival_after_rating_operations WHERE operation_id=p_operation_id;
    IF FOUND THEN
        IF prior.user_id<>actor OR prior.festival_id<>p_festival_id OR prior.band_id<>p_band_id
           OR prior.rating<>p_rating OR prior.recorded_at<>p_recorded_at THEN
            RAISE EXCEPTION 'This rating operation belongs to different data.' USING ERRCODE='22023';
        END IF;
        -- Confirm the old commit without reapplying it over a newer edit/reset.
        RETURN jsonb_build_object('confirmed',true,'operation_id',p_operation_id);
    END IF;
    SELECT status INTO festival_status FROM public.festivals WHERE id=p_festival_id FOR SHARE;
    IF festival_status IS DISTINCT FROM 'ACTIVE' OR NOT EXISTS(
        SELECT 1 FROM public.festival_lineup_entries WHERE festival_id=p_festival_id AND band_id=p_band_id) THEN
        RAISE EXCEPTION 'Choose a band in an active festival before recording an after-rating.' USING ERRCODE='42501';
    END IF;
    PERFORM pg_advisory_xact_lock(hashtextextended('wacken:after:key:'||actor::text||':'||p_festival_id||':'||p_band_id,0));
    INSERT INTO public.festival_after_rating_operations(operation_id,user_id,festival_id,band_id,rating,recorded_at)
    VALUES(p_operation_id,actor,p_festival_id,p_band_id,p_rating,p_recorded_at);
    IF p_rating>0 THEN
        IF EXISTS(SELECT 1 FROM public.personal_band_rating_events WHERE id=p_operation_id AND
            (user_id<>actor OR band_id<>p_band_id OR festival_id IS DISTINCT FROM p_festival_id
             OR rating<>p_rating OR created_at<>p_recorded_at)) THEN
            RAISE EXCEPTION 'The rating history identity belongs to different data.' USING ERRCODE='22023';
        END IF;
        INSERT INTO public.personal_band_rating_events(id,user_id,band_id,festival_id,festival_name_snapshot,rating,created_at)
        SELECT p_operation_id,actor,p_band_id,p_festival_id,name,p_rating,p_recorded_at
        FROM public.festivals WHERE id=p_festival_id ON CONFLICT(id) DO NOTHING;
    END IF;
    INSERT INTO public.festival_after_ratings(user_id,festival_id,band_id,operation_id,rating,recorded_at,requires_review)
    VALUES(actor,p_festival_id,p_band_id,p_operation_id,p_rating,p_recorded_at,false)
    ON CONFLICT(user_id,festival_id,band_id) DO UPDATE SET operation_id=excluded.operation_id,
        rating=excluded.rating,recorded_at=excluded.recorded_at,requires_review=false;
    -- Established personal_band_ratings and planning ratings are deliberately untouched.
    RETURN jsonb_build_object('confirmed',true,'operation_id',p_operation_id);
END $function$;
REVOKE ALL ON FUNCTION wacken_private.save_festival_after_rating(uuid,text,text,integer,timestamptz) FROM PUBLIC,anon;
GRANT EXECUTE ON FUNCTION wacken_private.save_festival_after_rating(uuid,text,text,integer,timestamptz) TO authenticated;
CREATE FUNCTION public.save_festival_after_rating(
    p_operation_id uuid,p_festival_id text,p_band_id text,p_rating integer,p_recorded_at timestamptz
) RETURNS jsonb LANGUAGE sql SECURITY INVOKER SET search_path='' AS $function$
    SELECT wacken_private.save_festival_after_rating(p_operation_id,p_festival_id,p_band_id,p_rating,p_recorded_at)
$function$;
REVOKE ALL ON FUNCTION public.save_festival_after_rating(uuid,text,text,integer,timestamptz) FROM PUBLIC,anon;
GRANT EXECUTE ON FUNCTION public.save_festival_after_rating(uuid,text,text,integer,timestamptz) TO authenticated;

-- Older clients may still append positive history. Keep it as recovered festival
-- context, never as an established preference, and never claim it is a confirmed final value.
CREATE FUNCTION wacken_private.recover_legacy_after_rating() RETURNS trigger
LANGUAGE plpgsql SECURITY DEFINER SET search_path='' AS $function$
BEGIN
    IF NEW.user_id IS DISTINCT FROM auth.uid() OR NEW.created_at<='1970-01-01T00:00:00Z'
       OR NEW.festival_id IS NULL OR NEW.rating<1 OR NOT public.is_group_member('00000000-0000-0000-0000-000000000001')
       OR EXISTS(SELECT 1 FROM public.festival_after_rating_operations WHERE operation_id=NEW.id) THEN RETURN NEW; END IF;
    PERFORM 1 FROM public.festivals WHERE id=NEW.festival_id AND status='ACTIVE' FOR SHARE;
    IF NOT FOUND THEN RETURN NEW; END IF;
    IF NOT EXISTS(SELECT 1 FROM public.festival_lineup_entries WHERE festival_id=NEW.festival_id AND band_id=NEW.band_id) THEN RETURN NEW; END IF;
    INSERT INTO public.festival_after_ratings(user_id,festival_id,band_id,operation_id,rating,recorded_at,requires_review)
    VALUES(NEW.user_id,NEW.festival_id,NEW.band_id,NEW.id,NEW.rating,NEW.created_at,true)
    ON CONFLICT(user_id,festival_id,band_id) DO UPDATE SET operation_id=excluded.operation_id,
        rating=excluded.rating,recorded_at=excluded.recorded_at,requires_review=true
    WHERE public.festival_after_ratings.recorded_at<excluded.recorded_at;
    RETURN NEW;
END $function$;
REVOKE ALL ON FUNCTION wacken_private.recover_legacy_after_rating() FROM PUBLIC,anon,authenticated;
CREATE TRIGGER recover_legacy_after_rating AFTER INSERT ON public.personal_band_rating_events
FOR EACH ROW EXECUTE FUNCTION wacken_private.recover_legacy_after_rating();
NOTIFY pgrst,'reload schema';


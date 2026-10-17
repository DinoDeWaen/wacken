-- TASK-216. Executed only against the disposable fixture database.
BEGIN;
DO $$ BEGIN
  IF coalesce((SELECT rating FROM public.personal_band_ratings WHERE user_id='11111111-1111-1111-1111-111111111111' AND band_id='rating-band'),-1)<>4
    THEN RAISE EXCEPTION 'Migration must establish only acknowledged archived history'; END IF;
  IF EXISTS(SELECT 1 FROM public.personal_band_ratings WHERE band_id='unknown-date-band')
    THEN RAISE EXCEPTION 'Unknown-date history must not establish a current preference'; END IF;
END $$;
SET LOCAL ROLE authenticated;
SELECT set_config('request.jwt.claim.sub','11111111-1111-1111-1111-111111111111',true);
DO $$
DECLARE operation1 uuid='44444444-4444-4444-8444-444444444444';
        operation2 uuid='55555555-5555-4555-8555-555555555555';
BEGIN
  PERFORM public.save_festival_after_rating(operation1,'rating-active','rating-band',2,'2026-10-17T14:00:00Z');
  IF coalesce((SELECT rating FROM public.festival_after_ratings WHERE festival_id='rating-active' AND band_id='rating-band'),-1)<>2
    THEN RAISE EXCEPTION 'Active after-rating not saved'; END IF;
  IF (SELECT requires_review FROM public.festival_after_ratings WHERE festival_id='rating-active' AND band_id='rating-band') IS DISTINCT FROM false
    THEN RAISE EXCEPTION 'Explicit rating must confirm recovered data'; END IF;
  IF coalesce((SELECT rating FROM public.personal_band_ratings WHERE band_id='rating-band'),-1)<>4
    THEN RAISE EXCEPTION 'Active edit must not promote personal preference'; END IF;
  PERFORM public.save_festival_after_rating(operation2,'rating-active','rating-band',0,'2026-10-17T14:01:00Z');
  PERFORM public.save_festival_after_rating(operation1,'rating-active','rating-band',2,'2026-10-17T14:00:00Z');
  IF coalesce((SELECT rating FROM public.festival_after_ratings WHERE festival_id='rating-active' AND band_id='rating-band'),-1)<>0
    THEN RAISE EXCEPTION 'Retry of an older operation resurrected a reset'; END IF;
  IF (SELECT count(*) FROM public.personal_band_rating_events WHERE id=operation1)<>1
    THEN RAISE EXCEPTION 'Replay duplicated positive history'; END IF;
  IF EXISTS(SELECT 1 FROM public.personal_band_rating_events WHERE id=operation2)
    THEN RAISE EXCEPTION 'Reset cannot invent a positive history event'; END IF;
  BEGIN
    PERFORM public.save_festival_after_rating(operation1,'rating-active','rating-band',5,'2026-10-17T14:00:00Z');
    RAISE EXCEPTION 'Mismatched request reuse was accepted';
  EXCEPTION WHEN invalid_parameter_value THEN NULL; END;
  BEGIN
    PERFORM public.save_festival_after_rating(gen_random_uuid(),'rating-archived','rating-band',5,now());
    RAISE EXCEPTION 'Archived direct edit was accepted';
  EXCEPTION WHEN insufficient_privilege THEN NULL; END;
  BEGIN
    PERFORM public.save_festival_after_rating(gen_random_uuid(),'rating-active','outside-band',5,now());
    RAISE EXCEPTION 'Outside-lineup edit was accepted';
  EXCEPTION WHEN insufficient_privilege THEN NULL; END;
  BEGIN
    UPDATE public.personal_band_ratings SET rating=1;
    RAISE EXCEPTION 'Direct current preference update was accepted';
  EXCEPTION WHEN insufficient_privilege THEN NULL; END;
END $$;
SELECT set_config('request.jwt.claim.sub','22222222-2222-2222-2222-222222222222',true);
DO $$ BEGIN
  IF EXISTS(SELECT 1 FROM public.festival_after_ratings) OR EXISTS(SELECT 1 FROM public.personal_band_ratings)
    THEN RAISE EXCEPTION 'Member could read another member rating state'; END IF;
END $$;
SELECT set_config('request.jwt.claim.sub','33333333-3333-3333-3333-333333333333',true);
DO $$ BEGIN
  BEGIN
    PERFORM public.save_festival_after_rating(gen_random_uuid(),'rating-active','rating-band',5,now());
    RAISE EXCEPTION 'Non-member edit was accepted';
  EXCEPTION WHEN insufficient_privilege THEN NULL; END;
END $$;
RESET ROLE;
CREATE FUNCTION public.fail_rating_history() RETURNS trigger LANGUAGE plpgsql AS $$
BEGIN RAISE EXCEPTION 'injected history failure'; END $$;
CREATE TRIGGER inject_rating_failure BEFORE INSERT ON public.personal_band_rating_events
FOR EACH ROW WHEN (NEW.id='66666666-6666-4666-8666-666666666666') EXECUTE FUNCTION public.fail_rating_history();
SET LOCAL ROLE authenticated;
SELECT set_config('request.jwt.claim.sub','11111111-1111-1111-1111-111111111111',true);
DO $$ BEGIN
  BEGIN
    PERFORM public.save_festival_after_rating('66666666-6666-4666-8666-666666666666','rating-active','rating-band',5,now());
    RAISE EXCEPTION 'Expected injected failure';
  EXCEPTION WHEN raise_exception THEN
    IF SQLERRM<>'injected history failure' THEN RAISE; END IF;
  END;
  IF coalesce((SELECT rating FROM public.festival_after_ratings WHERE festival_id='rating-active' AND band_id='rating-band'),-1)<>0
    THEN RAISE EXCEPTION 'History failure left a partially changed after-rating'; END IF;
END $$;
RESET ROLE;
DO $$ BEGIN
  IF EXISTS(SELECT 1 FROM public.festival_after_rating_operations WHERE operation_id='66666666-6666-4666-8666-666666666666')
    THEN RAISE EXCEPTION 'History failure left a committed operation'; END IF;
END $$;
ROLLBACK;
SELECT 'Rating state isolation, reset/replay, migration, RLS and rollback assertions passed.' AS result;

-- Disposable fixture; no production data. Every write is rolled back.
BEGIN;
SET LOCAL ROLE authenticated;
SELECT set_config('request.jwt.claim.sub','11111111-1111-1111-1111-111111111111',true);
SELECT public.save_festival_after_rating('bbbbbbbb-bbbb-4bbb-8bbb-bbbbbbbbbbb1','rating-active','rating-band',3,'2026-10-17T12:00:00Z');
SELECT public.archive_festival('rating-active');
DO $test$
DECLARE reviewed jsonb; corrected jsonb; replay jsonb; revision bigint;
BEGIN
 reviewed:=public.review_archived_rating('rating-active','rating-band','AFTER');
 ASSERT (reviewed->>'rating')::int IS NOT DISTINCT FROM 3;
 revision:=(reviewed->>'revision')::bigint;
 corrected:=public.correct_archived_rating('cccccccc-cccc-4ccc-8ccc-ccccccccccc1','rating-active','rating-band','AFTER',3,revision,2);
 replay:=public.correct_archived_rating('cccccccc-cccc-4ccc-8ccc-ccccccccccc1','rating-active','rating-band','AFTER',3,revision,2);
 ASSERT corrected=replay;
 ASSERT (SELECT rating FROM public.personal_band_ratings WHERE band_id='rating-band') IS NOT DISTINCT FROM 2;
 ASSERT (SELECT rating FROM public.festival_after_ratings WHERE festival_id='rating-active') IS NOT DISTINCT FROM 2;
 ASSERT (SELECT created_at FROM public.personal_band_rating_events WHERE id='bbbbbbbb-bbbb-4bbb-8bbb-bbbbbbbbbbb1') IS NOT DISTINCT FROM '2026-10-17T12:00:00Z'::timestamptz;
 ASSERT (SELECT rating FROM public.personal_band_rating_events WHERE id='bbbbbbbb-bbbb-4bbb-8bbb-bbbbbbbbbbb1') IS NOT DISTINCT FROM 2;
 ASSERT (SELECT count(*) FROM public.rating_corrections)=1;
 ASSERT (SELECT previous_value FROM public.rating_corrections) IS NOT DISTINCT FROM 3;
 ASSERT (SELECT new_value FROM public.rating_corrections) IS NOT DISTINCT FROM 2;
 BEGIN
  PERFORM public.correct_archived_rating(gen_random_uuid(),'rating-active','rating-band','AFTER',3,revision,1);
  RAISE EXCEPTION 'Expected stale review rejection' USING ERRCODE='XX000';
 EXCEPTION WHEN SQLSTATE '22023' THEN ASSERT SQLERRM LIKE '%Reload%'; END;
 BEGIN
  UPDATE public.personal_band_rating_events SET rating=5 WHERE id='bbbbbbbb-bbbb-4bbb-8bbb-bbbbbbbbbbb1';
  RAISE EXCEPTION 'Expected audit bypass denial' USING ERRCODE='XX000';
 EXCEPTION WHEN insufficient_privilege THEN NULL; END;
 BEGIN
  INSERT INTO public.festival_planning_ratings(group_id,user_id,festival_id,band_id,rating)
  VALUES('00000000-0000-0000-0000-000000000001',auth.uid(),'rating-active','rating-band',5);
  RAISE EXCEPTION 'Expected planning bypass denial' USING ERRCODE='XX000';
 EXCEPTION WHEN insufficient_privilege THEN NULL; END;
 reviewed:=public.review_archived_rating('rating-active','rating-band','PLANNING');
 PERFORM public.correct_archived_rating('cccccccc-cccc-4ccc-8ccc-ccccccccccc2','rating-active','rating-band','PLANNING',0,(reviewed->>'revision')::bigint,5);
 ASSERT (SELECT rating FROM public.personal_band_ratings WHERE band_id='rating-band') IS NOT DISTINCT FROM 2;
END $test$;
RESET ROLE;
CREATE FUNCTION public.reject_correction_audit() RETURNS trigger LANGUAGE plpgsql AS $$ BEGIN RAISE EXCEPTION 'Injected audit failure'; END $$;
CREATE TRIGGER reject_correction_audit BEFORE INSERT ON public.rating_corrections FOR EACH ROW EXECUTE FUNCTION public.reject_correction_audit();
SET LOCAL ROLE authenticated;
DO $$ DECLARE reviewed jsonb; BEGIN
 reviewed:=public.review_archived_rating('rating-active','rating-band','AFTER');
 BEGIN
  PERFORM public.correct_archived_rating(gen_random_uuid(),'rating-active','rating-band','AFTER',2,(reviewed->>'revision')::bigint,5);
  RAISE EXCEPTION 'Expected audit rollback' USING ERRCODE='XX000';
 EXCEPTION WHEN SQLSTATE 'P0001' THEN ASSERT SQLERRM='Injected audit failure'; END;
 ASSERT (SELECT rating FROM public.personal_band_ratings WHERE band_id='rating-band') IS NOT DISTINCT FROM 2;
 ASSERT (SELECT rating FROM public.festival_after_ratings WHERE festival_id='rating-active') IS NOT DISTINCT FROM 2;
END $$;
RESET ROLE;
DROP TRIGGER reject_correction_audit ON public.rating_corrections;
-- Simulate an already-established newer source and a later festival planning snapshot.
INSERT INTO public.festivals(id,name,status) VALUES('correction-next','Next','ACTIVE');
INSERT INTO public.festival_planning_ratings(group_id,user_id,festival_id,band_id,rating)
VALUES('00000000-0000-0000-0000-000000000001','11111111-1111-1111-1111-111111111111','correction-next','rating-band',5);
UPDATE public.personal_band_ratings SET rating=5,source_festival_id='newer-source' WHERE band_id='rating-band';
SET LOCAL ROLE authenticated;
DO $$ DECLARE reviewed jsonb; BEGIN
 reviewed:=public.review_archived_rating('rating-active','rating-band','AFTER');
 PERFORM public.correct_archived_rating('cccccccc-cccc-4ccc-8ccc-ccccccccccc3','rating-active','rating-band','AFTER',2,(reviewed->>'revision')::bigint,1);
 ASSERT (SELECT rating FROM public.personal_band_ratings WHERE band_id='rating-band') IS NOT DISTINCT FROM 5;
 ASSERT (SELECT rating FROM public.festival_planning_ratings WHERE festival_id='correction-next') IS NOT DISTINCT FROM 5;
 BEGIN
  PERFORM public.review_archived_rating('correction-next','rating-band','AFTER');
  RAISE EXCEPTION 'Expected active correction denial' USING ERRCODE='XX000';
 EXCEPTION WHEN SQLSTATE '22023' THEN NULL; END;
END $$;
SELECT set_config('request.jwt.claim.sub','22222222-2222-2222-2222-222222222222',true);
DO $$ BEGIN ASSERT (SELECT count(*) FROM public.rating_corrections)=0; END $$;
SELECT set_config('request.jwt.claim.sub','33333333-3333-3333-3333-333333333333',true);
DO $$ BEGIN
 BEGIN PERFORM public.review_archived_rating('rating-active','rating-band','AFTER');
  RAISE EXCEPTION 'Expected outsider denial' USING ERRCODE='XX000';
 EXCEPTION WHEN insufficient_privilege THEN NULL; END;
END $$;
RESET ROLE;
DELETE FROM public.festivals WHERE id='rating-active';
DO $$ BEGIN
 ASSERT (SELECT count(*) FROM public.rating_corrections WHERE festival_id='rating-active')=3;
 ASSERT (SELECT count(*) FROM public.rating_corrections WHERE festival_name_snapshot='Active')=3;
END $$;
ROLLBACK;
SELECT 'Archived correction: audit, original time, replay, stale review, rollback, source protection and ownership passed' AS result;

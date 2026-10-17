-- Same disposable fixture as rating_state.sql; all writes roll back.
BEGIN;
SET LOCAL ROLE authenticated;
SELECT set_config('request.jwt.claim.sub','11111111-1111-1111-1111-111111111111',true);
DO $$ BEGIN
 BEGIN
  PERFORM public.archive_festival('rating-active');
  RAISE EXCEPTION 'Expected legacy review conflict' USING ERRCODE='XX000';
 EXCEPTION WHEN SQLSTATE '22023' THEN ASSERT SQLERRM LIKE '%review%'; END;
 ASSERT (SELECT status FROM public.festivals WHERE id='rating-active')='ACTIVE';
 BEGIN
  UPDATE public.festivals SET status='ARCHIVED' WHERE id='rating-active';
  RAISE EXCEPTION 'Expected status-only archive denial' USING ERRCODE='XX000';
 EXCEPTION WHEN insufficient_privilege THEN NULL; END;
 PERFORM public.save_festival_after_rating('aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaa1','rating-active','rating-band',3,'2026-10-17T12:00:00Z');
END $$;
RESET ROLE;
INSERT INTO public.festival_lineup_entries VALUES('rating-active','outside-band','Outside',now(),now());
INSERT INTO public.personal_band_ratings VALUES('11111111-1111-1111-1111-111111111111','outside-band',5,'rating-archived','Archived',gen_random_uuid(),now());
INSERT INTO public.personal_band_ratings VALUES('11111111-1111-1111-1111-111111111111','unknown-date-band',2,'rating-archived','Archived',gen_random_uuid(),now());
INSERT INTO public.festival_after_ratings VALUES('11111111-1111-1111-1111-111111111111','rating-active','outside-band',gen_random_uuid(),0,now(),false);
INSERT INTO public.festival_planning_ratings(group_id,user_id,festival_id,band_id,rating)
VALUES('00000000-0000-0000-0000-000000000001','11111111-1111-1111-1111-111111111111','rating-active','rating-band',5);
CREATE FUNCTION public.test_reject_archive() RETURNS trigger LANGUAGE plpgsql AS $$ BEGIN RAISE EXCEPTION 'Injected archive failure'; END $$;
CREATE TRIGGER reject_archive BEFORE INSERT ON public.festival_archive_operations FOR EACH ROW EXECUTE FUNCTION public.test_reject_archive();
SET LOCAL ROLE authenticated;
DO $$ BEGIN
 BEGIN
  PERFORM public.archive_festival('rating-active');
  RAISE EXCEPTION 'Expected rollback' USING ERRCODE='XX000';
 EXCEPTION WHEN SQLSTATE 'P0001' THEN ASSERT SQLERRM='Injected archive failure'; END;
 ASSERT (SELECT status FROM public.festivals WHERE id='rating-active')='ACTIVE';
 ASSERT (SELECT rating FROM public.personal_band_ratings WHERE band_id='rating-band') IS NOT DISTINCT FROM 4;
END $$;
RESET ROLE;
DROP TRIGGER reject_archive ON public.festival_archive_operations;
SET LOCAL ROLE authenticated;
SELECT set_config('request.jwt.claim.sub','22222222-2222-2222-2222-222222222222',true);
SELECT public.save_festival_after_rating('aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaa2','rating-active','rating-band',2,'2026-10-17T12:01:00Z');
SELECT set_config('request.jwt.claim.sub','33333333-3333-3333-3333-333333333333',true);
DO $$ BEGIN
 BEGIN PERFORM public.archive_festival('rating-active');
  RAISE EXCEPTION 'Expected outsider denial' USING ERRCODE='XX000';
 EXCEPTION WHEN insufficient_privilege THEN NULL; END;
END $$;
SELECT set_config('request.jwt.claim.sub','11111111-1111-1111-1111-111111111111',true);
DO $$ DECLARE first jsonb; second jsonb; BEGIN
 first:=public.archive_festival('rating-active');
 second:=public.archive_festival('rating-active');
 ASSERT first=second;
 ASSERT (first->>'confirmed')::boolean IS TRUE;
 ASSERT (SELECT status FROM public.festivals WHERE id='rating-active')='ARCHIVED';
 ASSERT (SELECT rating FROM public.personal_band_ratings WHERE band_id='rating-band') IS NOT DISTINCT FROM 3;
 ASSERT (SELECT source_festival_id FROM public.personal_band_ratings WHERE band_id='rating-band') IS NOT DISTINCT FROM 'rating-active';
 ASSERT (SELECT source_event_id FROM public.personal_band_ratings WHERE band_id='rating-band') IS NOT DISTINCT FROM 'aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaa1'::uuid;
 ASSERT (SELECT rating FROM public.personal_band_ratings WHERE band_id='outside-band') IS NOT DISTINCT FROM 5;
 ASSERT (SELECT rating FROM public.personal_band_ratings WHERE band_id='unknown-date-band') IS NOT DISTINCT FROM 2;
 ASSERT (SELECT rating FROM public.festival_planning_ratings WHERE band_id='rating-band')=5;
 ASSERT (SELECT rating FROM public.festival_after_ratings WHERE festival_id='rating-active' AND band_id='rating-band')=3;
END $$;
RESET ROLE;
DO $$ BEGIN
 ASSERT (SELECT count(*) FROM public.festival_archive_operations WHERE festival_id='rating-active')=1;
 ASSERT (SELECT rating FROM public.personal_band_ratings WHERE user_id='22222222-2222-2222-2222-222222222222' AND band_id='rating-band') IS NOT DISTINCT FROM 2;
END $$;
ROLLBACK;
SELECT 'Atomic archive, member promotion, reset retention, review, rollback and legacy guard passed' AS result;

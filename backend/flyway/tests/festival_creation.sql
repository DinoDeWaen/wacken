-- Isolated test data only. Run with ON_ERROR_STOP; every write rolls back.
BEGIN;
UPDATE public.festivals SET status = 'ARCHIVED' WHERE status = 'ACTIVE';
INSERT INTO auth.users(id,email) VALUES
('11111111-1111-1111-1111-111111111111','creator@example.invalid'),
('22222222-2222-2222-2222-222222222222','other@example.invalid'),
('33333333-3333-3333-3333-333333333333','outsider@example.invalid');
DELETE FROM public.group_members WHERE user_id='33333333-3333-3333-3333-333333333333';
INSERT INTO public.bands(id,name,biography_html) VALUES
('any-given-day','Any given Day','Original biography'),
('airbourne','Airbourne','Airbourne biography');
INSERT INTO public.personal_band_rating_events(user_id,band_id,rating,created_at) VALUES
('11111111-1111-1111-1111-111111111111','airbourne',2,'2026-01-01'),
('11111111-1111-1111-1111-111111111111','airbourne',4,'2026-02-01'),
('22222222-2222-2222-2222-222222222222','airbourne',5,'2026-03-01');
INSERT INTO public.personal_band_ratings(user_id,band_id,rating,source_festival_id,source_festival_name,source_event_id,established_at) VALUES
('11111111-1111-1111-1111-111111111111','airbourne',4,'wacken-2026','Wacken history',gen_random_uuid(),'2026-02-01'),
('22222222-2222-2222-2222-222222222222','airbourne',5,'wacken-2026','Wacken history',gen_random_uuid(),'2026-03-01'),
('33333333-3333-3333-3333-333333333333','airbourne',1,'wacken-2026','Wacken history',gen_random_uuid(),'2026-03-01');

CREATE FUNCTION public.test_creation_failure() RETURNS trigger LANGUAGE plpgsql AS $$
BEGIN
 IF to_jsonb(NEW)->>'name' = 'Rejected Act'
 OR to_jsonb(NEW)->>'uploaded_display_name' = 'Rejected Lineup'
 OR (TG_TABLE_NAME='festival_planning_ratings' AND to_jsonb(NEW)->>'festival_id'='fail-rating')
 OR (TG_TABLE_NAME='festival_creation_requests' AND to_jsonb(NEW)->>'festival_id'='fail-request') THEN
  RAISE EXCEPTION 'Injected creation failure';
 END IF;
 RETURN NEW;
END $$;
CREATE TRIGGER test_band_failure BEFORE INSERT ON public.bands FOR EACH ROW EXECUTE FUNCTION public.test_creation_failure();
CREATE TRIGGER test_lineup_failure BEFORE INSERT ON public.festival_lineup_entries FOR EACH ROW EXECUTE FUNCTION public.test_creation_failure();
CREATE TRIGGER test_rating_failure BEFORE INSERT ON public.festival_planning_ratings FOR EACH ROW EXECUTE FUNCTION public.test_creation_failure();
-- The last write must also roll back the whole operation.
CREATE TRIGGER test_request_failure BEFORE INSERT ON public.festival_creation_requests FOR EACH ROW EXECUTE FUNCTION public.test_creation_failure();

SET LOCAL ROLE authenticated;
SELECT set_config('request.jwt.claim.sub','11111111-1111-1111-1111-111111111111',true);
DO $test$
DECLARE
 response jsonb;
 before_bands bigint;
 attempt record;
BEGIN
 response := public.create_festival_from_bands('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
 '00000000-0000-0000-0000-000000000001','test-creation','Test creation',
 ARRAY['Any Given Day','Any given Day','Airbourne','A+B','A B','東京','!!!','Airbourne']);
 ASSERT (response->>'created_bands')::int = 5;
 ASSERT (response->>'reused_bands')::int = 2;
 ASSERT (response->>'prefilled_ratings')::int = 1;
 ASSERT jsonb_array_length(response->'lineup') = 7;
 ASSERT (SELECT count(*) FROM public.bands WHERE name IN ('Any Given Day','Any given Day'))=2;
 ASSERT (SELECT biography_html FROM public.bands WHERE id='any-given-day')='Original biography';
 ASSERT (SELECT rating FROM public.festival_planning_ratings WHERE festival_id='test-creation' AND user_id=auth.uid())=4;
 ASSERT (SELECT count(*) FROM public.festival_planning_ratings WHERE festival_id='test-creation')=2;
 ASSERT (SELECT rating FROM public.festival_planning_ratings WHERE festival_id='test-creation' AND user_id='22222222-2222-2222-2222-222222222222')=5;
 ASSERT jsonb_array_length(response->'planning_ratings')=1;
 ASSERT NOT EXISTS(SELECT 1 FROM public.festival_after_ratings WHERE festival_id='test-creation');
 ASSERT (SELECT count(*) FROM public.personal_band_rating_events)=2; -- RLS exposes only this user's history
 UPDATE public.festival_planning_ratings SET rating=2 WHERE festival_id='test-creation' AND user_id=auth.uid();
 response := public.create_festival_from_bands('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
 '00000000-0000-0000-0000-000000000001','test-creation','Test creation',
 ARRAY['Any Given Day','Any given Day','Airbourne','A+B','A B','東京','!!!','Airbourne']);
 ASSERT (SELECT rating FROM public.festival_planning_ratings WHERE festival_id='test-creation' AND user_id=auth.uid())=2;
 ASSERT (SELECT count(*) FROM public.festival_lineup_entries WHERE festival_id='test-creation')=7;
 BEGIN
  PERFORM public.create_festival_from_bands('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
   '00000000-0000-0000-0000-000000000001','other-active','Other active',ARRAY['Other']);
  RAISE EXCEPTION 'Expected active festival denial' USING ERRCODE='XX000';
 EXCEPTION WHEN SQLSTATE 'P0001' THEN
  ASSERT SQLERRM LIKE '%active festival%';
 END;
 UPDATE public.festivals SET status='ARCHIVED' WHERE id='test-creation';
 BEGIN
  PERFORM public.create_festival_from_bands('cccccccc-cccc-cccc-cccc-cccccccccccc',
   '00000000-0000-0000-0000-000000000001','test-creation','Overwrite history',ARRAY['Other']);
  RAISE EXCEPTION 'Expected archived identity denial' USING ERRCODE='XX000';
 EXCEPTION WHEN SQLSTATE 'P0001' THEN
  ASSERT SQLERRM LIKE '%already exists%';
 END;
 ASSERT (SELECT name FROM public.festivals WHERE id='test-creation')='Test creation';
 before_bands := (SELECT count(*) FROM public.bands);
 FOR attempt IN SELECT * FROM (VALUES
 ('fail-band',ARRAY['First New','Rejected Act']),
 ('fail-lineup',ARRAY['First New','Rejected Lineup']),
 ('fail-rating',ARRAY['First New','Airbourne']),
 ('fail-request',ARRAY['First New'])) AS cases(id,names)
 LOOP
  BEGIN
   PERFORM public.create_festival_from_bands(gen_random_uuid(),
    '00000000-0000-0000-0000-000000000001',attempt.id,attempt.id,attempt.names);
   RAISE EXCEPTION 'Expected injected failure' USING ERRCODE='XX000';
  EXCEPTION WHEN SQLSTATE 'P0001' THEN
   ASSERT SQLERRM='Injected creation failure';
  END;
  ASSERT NOT EXISTS (SELECT 1 FROM public.festivals WHERE id=attempt.id);
  ASSERT (SELECT count(*) FROM public.bands)=before_bands;
  ASSERT NOT EXISTS (SELECT 1 FROM public.festival_lineup_entries WHERE festival_id=attempt.id);
  ASSERT NOT EXISTS (SELECT 1 FROM public.festival_planning_ratings WHERE festival_id=attempt.id);
  ASSERT NOT EXISTS (SELECT 1 FROM public.festival_creation_requests WHERE festival_id=attempt.id);
 END LOOP;
 PERFORM set_config('request.jwt.claim.sub','33333333-3333-3333-3333-333333333333',true);
 BEGIN
  PERFORM public.create_festival_from_bands(gen_random_uuid(),
   '00000000-0000-0000-0000-000000000001','denied','Denied',ARRAY['Denied']);
  RAISE EXCEPTION 'Expected membership denial' USING ERRCODE='XX000';
 EXCEPTION WHEN insufficient_privilege THEN NULL;
 END;
 ASSERT NOT has_function_privilege('anon',
 'public.create_festival_from_bands(uuid,uuid,text,text,text[])','EXECUTE');
 ASSERT NOT has_table_privilege('authenticated','public.festival_creation_requests','INSERT');
 ASSERT NOT has_table_privilege('authenticated','public.personal_band_ratings','INSERT');
END
$test$;
RESET ROLE;
DO $$ BEGIN ASSERT (SELECT count(*) FROM public.personal_band_rating_events)=3; END $$;
ROLLBACK;
SELECT 'festival creation transaction tests passed' AS result;

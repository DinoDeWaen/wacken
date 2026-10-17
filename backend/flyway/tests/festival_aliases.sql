BEGIN;
UPDATE public.festivals SET status='ARCHIVED';
INSERT INTO public.bands(id,name,active,youtube_url) VALUES('alias-known','Abbie Falls',true,'https://www.youtube.com/@abbiefalls');
INSERT INTO public.personal_band_ratings VALUES('11111111-1111-1111-1111-111111111111','alias-known',4,'rating-archived','Archived',gen_random_uuid(),now());
SET LOCAL ROLE authenticated;
SELECT set_config('request.jwt.claim.sub','11111111-1111-1111-1111-111111111111',true);
DO $$ DECLARE first jsonb; replay jsonb; rows jsonb:='[
 {"uploaded_name":"Abbie Fall","band_id":"alias-known","band_name":"Abbie Falls","remember_alias":true},
 {"uploaded_name":"AF","band_id":"alias-known","band_name":"Abbie Falls","remember_alias":false},
 {"uploaded_name":"Separate","band_id":null,"remember_alias":false}]'; BEGIN
 first:=public.create_festival_from_reviewed_bands('eeeeeeee-eeee-4eee-8eee-eeeeeeeeeee1','00000000-0000-0000-0000-000000000001','alias-next','Alias Next',rows);
 ASSERT jsonb_array_length(first->'lineup')=2;
 ASSERT (first->>'reused_bands')::int=1;
 ASSERT (first->>'created_bands')::int=1;
 ASSERT (first->'planning_ratings'->0->>'rating')::int=4;
 ASSERT (SELECT count(*) FROM public.band_aliases WHERE alias_key='abbie fall')=1;
 ASSERT (SELECT youtube_url FROM public.bands WHERE id='alias-known')='https://www.youtube.com/@abbiefalls';
 UPDATE public.festival_planning_ratings SET rating=5 WHERE festival_id='alias-next' AND band_id='alias-known';
 replay:=public.create_festival_from_reviewed_bands('eeeeeeee-eeee-4eee-8eee-eeeeeeeeeee1','00000000-0000-0000-0000-000000000001','alias-next','Alias Next',rows);
 ASSERT (replay->'planning_ratings'->0->>'rating')::int=5;
 BEGIN
  PERFORM public.create_festival_from_reviewed_bands('eeeeeeee-eeee-4eee-8eee-eeeeeeeeeee1','00000000-0000-0000-0000-000000000001','alias-next','Alias Next','[{"uploaded_name":"Changed","band_id":null,"remember_alias":false}]');
  RAISE EXCEPTION 'Expected payload mismatch' USING ERRCODE='XX000';
 EXCEPTION WHEN SQLSTATE '22023' THEN NULL; END;
 BEGIN
  INSERT INTO public.band_aliases(alias_key,alias_name,band_id,confirmed_by) VALUES('forged','Forged','alias-known','11111111-1111-1111-1111-111111111111');
  RAISE EXCEPTION 'Expected alias write denial' USING ERRCODE='XX000';
 EXCEPTION WHEN insufficient_privilege THEN NULL; END;
END $$;
RESET ROLE;
UPDATE public.festivals SET status='ARCHIVED';
CREATE FUNCTION public.test_alias_rollback() RETURNS trigger LANGUAGE plpgsql AS $$ BEGIN
 IF NEW.name='Rejected Alias Act' THEN RAISE EXCEPTION 'Injected alias rollback'; END IF; RETURN NEW; END $$;
CREATE TRIGGER reject_alias_creation BEFORE INSERT ON public.bands FOR EACH ROW EXECUTE FUNCTION public.test_alias_rollback();
SET LOCAL ROLE authenticated;
DO $$ BEGIN
 BEGIN
  PERFORM public.create_festival_from_reviewed_bands(gen_random_uuid(),'00000000-0000-0000-0000-000000000001','alias-fault','Fault','[{"uploaded_name":"AF remembered","band_id":"alias-known","band_name":"Abbie Falls","remember_alias":true},{"uploaded_name":"Rejected Alias Act","band_id":null,"remember_alias":false}]');
  RAISE EXCEPTION 'Expected injected rollback' USING ERRCODE='XX000';
 EXCEPTION WHEN SQLSTATE 'P0001' THEN ASSERT SQLERRM='Injected alias rollback'; END;
 ASSERT NOT EXISTS(SELECT 1 FROM public.band_aliases WHERE alias_key='af remembered');
 ASSERT NOT EXISTS(SELECT 1 FROM public.festivals WHERE id='alias-fault');
 BEGIN
  PERFORM public.create_festival_from_reviewed_bands(gen_random_uuid(),'00000000-0000-0000-0000-000000000001','stale-alias','Stale','[{"uploaded_name":"AF","band_id":"alias-known","band_name":"Old name","remember_alias":false}]');
  RAISE EXCEPTION 'Expected stale catalog rejection' USING ERRCODE='XX000';
 EXCEPTION WHEN SQLSTATE '22023' THEN ASSERT SQLERRM LIKE '%changed%'; END;
 ASSERT NOT EXISTS(SELECT 1 FROM public.festivals WHERE id='stale-alias');
 BEGIN
  PERFORM public.create_festival_from_reviewed_bands(gen_random_uuid(),'00000000-0000-0000-0000-000000000001','alias-conflict','Conflict','[{"uploaded_name":"Abbie Fall","band_id":"rating-band","band_name":"Rating Band","remember_alias":true}]');
  RAISE EXCEPTION 'Expected conflict' USING ERRCODE='XX000';
 EXCEPTION WHEN SQLSTATE '22023' THEN ASSERT SQLERRM LIKE '%alias%'; END;
 ASSERT NOT EXISTS(SELECT 1 FROM public.festivals WHERE id='alias-conflict');
 ASSERT (SELECT rating FROM public.personal_band_ratings WHERE band_id='alias-known')=4;
 ASSERT NOT EXISTS(SELECT 1 FROM public.festival_after_ratings WHERE band_id='alias-known');
END $$;
SELECT set_config('request.jwt.claim.sub','33333333-3333-3333-3333-333333333333',true);
DO $$ BEGIN
 ASSERT (SELECT count(*) FROM public.band_aliases)=0;
 BEGIN
  PERFORM public.create_festival_from_reviewed_bands(gen_random_uuid(),'00000000-0000-0000-0000-000000000001','outsider','Outsider','[{"uploaded_name":"No","band_id":null,"remember_alias":false}]');
  RAISE EXCEPTION 'Expected outsider denial' USING ERRCODE='XX000';
 EXCEPTION WHEN insufficient_privilege THEN NULL; END;
END $$;
ROLLBACK;
SELECT 'Reviewed creation: identity, alias, duplicate canonical, replay, conflict rollback, metadata/rating preservation and membership passed' AS result;

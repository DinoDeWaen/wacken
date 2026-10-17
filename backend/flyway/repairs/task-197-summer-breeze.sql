-- TASK-197: one-time guarded recovery of the observed legacy partial import.
-- Names are the checked-in 63-band CSV. No catalog/history rows are deleted.
BEGIN;
DO $repair$
DECLARE
 names text[] := ARRAY['Summer Breeze Legends & Orchestra','Electric Callboy','Edguy','Saltatio Mortis','Children Of Bodom','HammerFall','Halestorm','Wind Rose','Lord Of The Lost','Eluveitie','H-Blockx','Swiss + Die Andern','Clawfinger','Kataklysm','Mono Inc.','Warkings','Dark Funeral','Gloryhammer','Finntroll','Atreyu','Shadow Of Intent','Emil Bulls','Equilibrium','John Bush Celebrating The Songs Of Anthrax 1993-2003','Carnifex','The Browning','Gaerea','Long Distance Calling','Make Them Suffer','Mittel Alta','Any Given Day','Gutalax','Blue Medusa','Insomnium','Combichrist','Peeling Flesh','Anaal Nathrakh','Fleshgod Apocalypse','Primal Fear','Nasty','Burning Witches','Kupfergold','End Of Green','Wolfheart','Legion Of The Damned','Ellende','Angelus Apatrida','Abbie Falls','Samurai Pizza Cats','Necrotted','Left To Suffer','Evil Invaders','Hiraes','Lavina','Asenblut','Accvsed','Spitting Glass','Ahab','Sunborn','The Night Eternal','Defiance HC','Regarde Les Hommes Tomber','Capacopter'];
 expected public.festivals%ROWTYPE;
 actor uuid;
 actors integer;
 uploaded_name text;
 chosen_id text;
 latest integer;
 original_band_ids text[];
 original_rating_ids uuid[];
 bands_before text;
 ratings_before text;
 history_before text;
 festivals_before text;
 other_lineup_before text;
BEGIN
 PERFORM pg_advisory_xact_lock(hashtextextended('wacken:festival:create:00000000-0000-0000-0000-000000000001',0));
 SELECT * INTO expected FROM public.festivals WHERE id='summer-breeze' FOR UPDATE;
 IF NOT FOUND OR expected.status<>'ACTIVE' OR expected.name<>'Summer breeze'
 OR expected.created_at<>'2026-10-16 18:36:11.622704+00'::timestamptz THEN
  RAISE EXCEPTION 'Recovery guard failed: this is not the observed incomplete festival.';
 END IF;
 IF EXISTS(SELECT 1 FROM public.festival_lineup_entries WHERE festival_id=expected.id) THEN
  RAISE EXCEPTION 'Recovery guard failed: lineup already exists; review rather than overwrite.';
 END IF;
 SELECT count(DISTINCT user_id),min(user_id::text)::uuid INTO actors,actor
 FROM public.festival_planning_ratings WHERE festival_id=expected.id;
 IF actors<>1 OR NOT EXISTS(SELECT 1 FROM public.group_members WHERE user_id=actor
    AND group_id='00000000-0000-0000-0000-000000000001') THEN
  RAISE EXCEPTION 'Recovery guard failed: the creating member cannot be identified safely.';
 END IF;
 SELECT array_agg(id),md5(string_agg(to_jsonb(b)::text,'' ORDER BY id)) INTO original_band_ids,bands_before FROM public.bands b;
 SELECT array_agg(id),md5(string_agg(to_jsonb(r)::text,'' ORDER BY id)) INTO original_rating_ids,ratings_before FROM public.festival_planning_ratings r;
 SELECT md5(coalesce(string_agg(to_jsonb(h)::text,'' ORDER BY id),'')) INTO history_before FROM public.personal_band_rating_events h;
 SELECT md5(coalesce(string_agg(to_jsonb(f)::text,'' ORDER BY id),'')) INTO festivals_before FROM public.festivals f;
 SELECT md5(coalesce(string_agg(to_jsonb(l)::text,'' ORDER BY festival_id,band_id),'')) INTO other_lineup_before FROM public.festival_lineup_entries l;

 FOREACH uploaded_name IN ARRAY names LOOP
  SELECT b.id INTO chosen_id FROM public.bands b WHERE b.name COLLATE "C"=uploaded_name COLLATE "C" ORDER BY b.created_at,b.id LIMIT 1;
  IF NOT FOUND THEN
   chosen_id:='band-'||gen_random_uuid()::text;
   INSERT INTO public.bands(id,name,active) VALUES(chosen_id,uploaded_name,true);
  END IF;
  INSERT INTO public.festival_lineup_entries(festival_id,band_id,uploaded_display_name) VALUES(expected.id,chosen_id,uploaded_name);
  SELECT e.rating INTO latest FROM public.personal_band_rating_events e
   WHERE e.user_id=actor AND e.band_id=chosen_id ORDER BY e.created_at DESC,e.id DESC LIMIT 1;
  IF FOUND THEN
   INSERT INTO public.festival_planning_ratings(group_id,user_id,festival_id,band_id,rating)
   VALUES('00000000-0000-0000-0000-000000000001',actor,expected.id,chosen_id,latest)
   ON CONFLICT(group_id,user_id,festival_id,band_id) DO NOTHING;
  END IF;
 END LOOP;
 ASSERT (SELECT count(*) FROM public.festival_lineup_entries WHERE festival_id=expected.id)=63;
 ASSERT NOT EXISTS(SELECT unnest(names) EXCEPT SELECT uploaded_display_name FROM public.festival_lineup_entries WHERE festival_id=expected.id);
 ASSERT bands_before=(SELECT md5(string_agg(to_jsonb(b)::text,'' ORDER BY id)) FROM public.bands b WHERE id=ANY(original_band_ids));
 ASSERT ratings_before=(SELECT md5(string_agg(to_jsonb(r)::text,'' ORDER BY id)) FROM public.festival_planning_ratings r WHERE id=ANY(original_rating_ids));
 ASSERT history_before=(SELECT md5(coalesce(string_agg(to_jsonb(h)::text,'' ORDER BY id),'')) FROM public.personal_band_rating_events h);
 ASSERT festivals_before=(SELECT md5(coalesce(string_agg(to_jsonb(f)::text,'' ORDER BY id),'')) FROM public.festivals f);
 ASSERT other_lineup_before=(SELECT md5(coalesce(string_agg(to_jsonb(l)::text,'' ORDER BY festival_id,band_id),'')) FROM public.festival_lineup_entries l WHERE festival_id<>expected.id);
END $repair$;
COMMIT;
SELECT 'Recovered Summer breeze: complete 63-band lineup; existing catalog metadata, ratings, festivals, and history preserved' AS result;

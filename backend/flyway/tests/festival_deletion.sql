\set ON_ERROR_STOP on
BEGIN;

CREATE TEMP TABLE deletion_test_context ON COMMIT DROP AS
SELECT member.group_id, member.user_id,
       (SELECT id FROM public.bands LIMIT 1) AS band_id,
       (SELECT id FROM public.stages LIMIT 1) AS stage_id,
       (SELECT count(*) FROM public.bands) AS band_count
FROM public.group_members AS member
JOIN public.profiles AS profile ON profile.id = member.user_id
WHERE profile.is_admin = false
LIMIT 1;

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM deletion_test_context WHERE band_id IS NOT NULL AND stage_id IS NOT NULL) THEN
        RAISE EXCEPTION 'Verification needs an existing non-platform-admin group member, band, and stage';
    END IF;
END $$;

INSERT INTO public.festivals (id, name, status) VALUES
    ('__deletion_test_target__', 'Deletion test target', 'ARCHIVED'),
    ('__deletion_test_other__', 'Deletion test other', 'ARCHIVED');
INSERT INTO public.festival_lineup_entries (festival_id, band_id, uploaded_display_name)
SELECT festival.id, context.band_id, 'Shared test band'
FROM public.festivals AS festival CROSS JOIN deletion_test_context AS context
WHERE festival.id IN ('__deletion_test_target__', '__deletion_test_other__');
INSERT INTO public.performances (id, festival_id, band_id, stage_id, start_at, end_at)
SELECT festival.id, festival.id, context.band_id, context.stage_id, '2030-01-01 12:00', '2030-01-01 13:00'
FROM public.festivals AS festival CROSS JOIN deletion_test_context AS context
WHERE festival.id IN ('__deletion_test_target__', '__deletion_test_other__');
INSERT INTO public.group_schedule_locks (group_id, festival_id, conflict_key, selected_candidate_key, updated_by)
SELECT context.group_id, festival.id, 'same-test-conflict', 'test-winner', context.user_id
FROM public.festivals AS festival CROSS JOIN deletion_test_context AS context
WHERE festival.id IN ('__deletion_test_target__', '__deletion_test_other__');
INSERT INTO public.festival_planning_ratings (group_id, user_id, festival_id, band_id, rating)
SELECT context.group_id, context.user_id, festival.id, context.band_id, 4
FROM public.festivals AS festival CROSS JOIN deletion_test_context AS context
WHERE festival.id IN ('__deletion_test_target__', '__deletion_test_other__');
CREATE TEMP TABLE deletion_test_event ON COMMIT DROP AS SELECT gen_random_uuid() AS id;
INSERT INTO public.personal_band_rating_events (id, user_id, band_id, festival_id, rating)
SELECT event.id, context.user_id, context.band_id, '__deletion_test_target__', 5
FROM deletion_test_context AS context CROSS JOIN deletion_test_event AS event;

DO $$ BEGIN
    PERFORM set_config('request.jwt.claim.sub', (SELECT user_id::text FROM deletion_test_context), true);
END $$;
SET LOCAL ROLE authenticated;
DELETE FROM public.festivals WHERE id = '__deletion_test_target__';
RESET ROLE;

DO $$ BEGIN
    IF EXISTS (SELECT 1 FROM public.festivals WHERE id = '__deletion_test_target__')
       OR EXISTS (SELECT 1 FROM public.performances WHERE festival_id = '__deletion_test_target__')
       OR EXISTS (SELECT 1 FROM public.group_schedule_locks WHERE festival_id = '__deletion_test_target__')
       OR EXISTS (SELECT 1 FROM public.festival_lineup_entries WHERE festival_id = '__deletion_test_target__')
       OR EXISTS (SELECT 1 FROM public.festival_planning_ratings WHERE festival_id = '__deletion_test_target__') THEN
        RAISE EXCEPTION 'Group-member deletion did not remove the selected festival and schedule';
    END IF;
    IF NOT EXISTS (SELECT 1 FROM public.performances WHERE festival_id = '__deletion_test_other__')
       OR NOT EXISTS (SELECT 1 FROM public.group_schedule_locks WHERE festival_id = '__deletion_test_other__')
       OR NOT EXISTS (SELECT 1 FROM public.festival_lineup_entries WHERE festival_id = '__deletion_test_other__')
       OR NOT EXISTS (SELECT 1 FROM public.festival_planning_ratings WHERE festival_id = '__deletion_test_other__') THEN
        RAISE EXCEPTION 'Deletion affected another festival';
    END IF;
    IF (SELECT count(*) FROM public.bands) <> (SELECT band_count FROM deletion_test_context) THEN
        RAISE EXCEPTION 'Deletion changed the central band catalog';
    END IF;
    IF NOT EXISTS (
        SELECT 1 FROM public.personal_band_rating_events
        WHERE id = (SELECT id FROM deletion_test_event) AND festival_id IS NULL
          AND festival_name_snapshot = 'Deletion test target' AND rating = 5
    ) THEN
        RAISE EXCEPTION 'Personal rating history or festival context was lost';
    END IF;
    PERFORM set_config('request.jwt.claim.sub', 'ffffffff-ffff-ffff-ffff-ffffffffffff', true);
END $$;
SET LOCAL ROLE authenticated;
DELETE FROM public.festivals WHERE id = '__deletion_test_other__';
RESET ROLE;
DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM public.festivals WHERE id = '__deletion_test_other__') THEN
        RAISE EXCEPTION 'A non-member was allowed to delete a festival';
    END IF;
END $$;

ROLLBACK;
SELECT 'Festival deletion, preservation, and authorization checks passed; all test changes rolled back.' AS result;

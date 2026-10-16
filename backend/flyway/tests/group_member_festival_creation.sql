-- Run only when no festival is active. Every write rolls back.
BEGIN;

SELECT set_config(
    'request.test_member_sub',
    (SELECT user_id::text
     FROM public.group_members
     WHERE group_id = '00000000-0000-0000-0000-000000000001'::uuid
     LIMIT 1),
    true
);

SET LOCAL ROLE authenticated;

DO $$
BEGIN
    PERFORM set_config('request.jwt.claim.sub', '00000000-0000-0000-0000-000000000099', true);
    BEGIN
        INSERT INTO public.festivals (id, name, status)
        VALUES ('festival-creation-policy-probe', 'Policy probe', 'ACTIVE');
        RAISE EXCEPTION 'A non-member created a festival';
    EXCEPTION WHEN insufficient_privilege THEN
        NULL;
    END;

    BEGIN
        INSERT INTO public.bands (id, name)
        VALUES ('festival-policy-probe-denied', 'Denied probe');
        RAISE EXCEPTION 'A non-member created a band';
    EXCEPTION WHEN insufficient_privilege THEN
        NULL;
    END;
END;
$$;

SELECT set_config('request.jwt.claim.sub', current_setting('request.test_member_sub'), true);

INSERT INTO public.festivals (id, name, status)
VALUES ('festival-creation-policy-probe', 'Policy probe', 'ACTIVE');

-- The Android adapter clears the new festival's empty lineup before inserting it.
DELETE FROM public.festival_lineup_entries
WHERE festival_id = 'festival-creation-policy-probe';

INSERT INTO public.bands (id, name)
VALUES ('festival-policy-probe-allowed', 'Allowed probe'),
       ('festival-policy-probe-lineup-denied', 'Lineup denied probe');

DO $$
BEGIN
    PERFORM set_config('request.jwt.claim.sub', '00000000-0000-0000-0000-000000000099', true);
    BEGIN
        INSERT INTO public.festival_lineup_entries (festival_id, band_id, uploaded_display_name)
        VALUES ('festival-creation-policy-probe', 'festival-policy-probe-lineup-denied', 'Denied probe');
        RAISE EXCEPTION 'A non-member created a lineup entry';
    EXCEPTION WHEN insufficient_privilege THEN
        NULL;
    END;
END;
$$;

SELECT set_config('request.jwt.claim.sub', current_setting('request.test_member_sub'), true);

INSERT INTO public.festival_lineup_entries (festival_id, band_id, uploaded_display_name)
VALUES ('festival-creation-policy-probe', 'festival-policy-probe-allowed', 'Allowed probe');

DO $$
BEGIN
    IF (SELECT count(*) FROM public.festival_lineup_entries
        WHERE festival_id = 'festival-creation-policy-probe') <> 1 THEN
        RAISE EXCEPTION 'Member lineup insert did not persist in the test transaction';
    END IF;
END;
$$;

ROLLBACK;
SELECT 'group member festival creation policy passed' AS result;

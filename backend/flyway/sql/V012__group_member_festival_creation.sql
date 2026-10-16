-- The first post-Wacken version has one shared group and one active festival.
-- Members may create the next active festival and its band-only lineup.
-- Existing admin policies remain in place for operational support.

DROP POLICY IF EXISTS festivals_insert_group_members ON public.festivals;
CREATE POLICY festivals_insert_group_members
ON public.festivals
FOR INSERT
TO authenticated
WITH CHECK (
    status = 'ACTIVE'
    AND public.is_group_member('00000000-0000-0000-0000-000000000001'::uuid)
);

DROP POLICY IF EXISTS bands_insert_group_members ON public.bands;
CREATE POLICY bands_insert_group_members
ON public.bands
FOR INSERT
TO authenticated
WITH CHECK (
    active = true
    AND public.is_group_member('00000000-0000-0000-0000-000000000001'::uuid)
);

DROP POLICY IF EXISTS festival_lineup_entries_insert_group_members ON public.festival_lineup_entries;
CREATE POLICY festival_lineup_entries_insert_group_members
ON public.festival_lineup_entries
FOR INSERT
TO authenticated
WITH CHECK (
    public.is_group_member('00000000-0000-0000-0000-000000000001'::uuid)
    AND EXISTS (
        SELECT 1
        FROM public.festivals
        WHERE id = festival_id
          AND status = 'ACTIVE'
    )
);

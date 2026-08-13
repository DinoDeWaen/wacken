CREATE TABLE public.festival_lineup_entries (
    festival_id text NOT NULL REFERENCES public.festivals(id) ON DELETE CASCADE,
    band_id text NOT NULL REFERENCES public.bands(id) ON DELETE CASCADE,
    uploaded_display_name text NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),
    PRIMARY KEY (festival_id, band_id)
);

CREATE TABLE public.festival_planning_ratings (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    group_id uuid NOT NULL REFERENCES public.groups(id) ON DELETE CASCADE,
    user_id uuid NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    festival_id text NOT NULL REFERENCES public.festivals(id) ON DELETE CASCADE,
    band_id text NOT NULL REFERENCES public.bands(id) ON DELETE CASCADE,
    rating integer NOT NULL CHECK (rating BETWEEN 0 AND 5),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),
    UNIQUE (group_id, user_id, festival_id, band_id)
);

CREATE TABLE public.personal_band_rating_events (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id uuid NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    band_id text NOT NULL REFERENCES public.bands(id) ON DELETE CASCADE,
    festival_id text REFERENCES public.festivals(id) ON DELETE SET NULL,
    rating integer NOT NULL CHECK (rating BETWEEN 1 AND 5),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE INDEX idx_festival_lineup_entries_band ON public.festival_lineup_entries(band_id);
CREATE INDEX idx_festival_planning_ratings_festival_band ON public.festival_planning_ratings(festival_id, band_id);
CREATE INDEX idx_festival_planning_ratings_user ON public.festival_planning_ratings(user_id);
CREATE INDEX idx_personal_band_rating_events_user_band_created ON public.personal_band_rating_events(user_id, band_id, created_at DESC);
CREATE INDEX idx_personal_band_rating_events_festival ON public.personal_band_rating_events(festival_id);

CREATE TRIGGER festival_lineup_entries_touch_updated_at
BEFORE UPDATE ON public.festival_lineup_entries
FOR EACH ROW EXECUTE FUNCTION public.touch_updated_at();

CREATE TRIGGER festival_planning_ratings_touch_updated_at
BEFORE UPDATE ON public.festival_planning_ratings
FOR EACH ROW EXECUTE FUNCTION public.touch_updated_at();

CREATE TRIGGER personal_band_rating_events_touch_updated_at
BEFORE UPDATE ON public.personal_band_rating_events
FOR EACH ROW EXECUTE FUNCTION public.touch_updated_at();

INSERT INTO public.festival_lineup_entries (festival_id, band_id, uploaded_display_name)
SELECT 'wacken-2026', id, name
FROM public.bands
WHERE active = true
ON CONFLICT (festival_id, band_id) DO NOTHING;

INSERT INTO public.festival_planning_ratings (group_id, user_id, festival_id, band_id, rating)
SELECT group_id, user_id, 'wacken-2026', band_id, rating
FROM public.ratings
ON CONFLICT (group_id, user_id, festival_id, band_id) DO UPDATE SET
    rating = excluded.rating;

ALTER TABLE public.festival_lineup_entries ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.festival_planning_ratings ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.personal_band_rating_events ENABLE ROW LEVEL SECURITY;

CREATE POLICY festival_lineup_entries_select_authenticated
ON public.festival_lineup_entries
FOR SELECT
TO authenticated
USING (true);

CREATE POLICY festival_lineup_entries_admin_write
ON public.festival_lineup_entries
FOR ALL
TO authenticated
USING (public.is_platform_admin())
WITH CHECK (public.is_platform_admin());

CREATE POLICY festival_planning_ratings_select_group_members
ON public.festival_planning_ratings
FOR SELECT
TO authenticated
USING (public.is_group_member(group_id) OR public.is_platform_admin());

CREATE POLICY festival_planning_ratings_insert_own_member_rating
ON public.festival_planning_ratings
FOR INSERT
TO authenticated
WITH CHECK (
    user_id = auth.uid()
    AND public.is_group_member(group_id)
);

CREATE POLICY festival_planning_ratings_update_own_member_rating
ON public.festival_planning_ratings
FOR UPDATE
TO authenticated
USING (
    (user_id = auth.uid() AND public.is_group_member(group_id))
    OR public.is_group_admin(group_id)
)
WITH CHECK (
    (user_id = auth.uid() AND public.is_group_member(group_id))
    OR public.is_group_admin(group_id)
);

CREATE POLICY festival_planning_ratings_delete_own_or_group_admin
ON public.festival_planning_ratings
FOR DELETE
TO authenticated
USING (
    (user_id = auth.uid() AND public.is_group_member(group_id))
    OR public.is_group_admin(group_id)
);

CREATE POLICY personal_band_rating_events_select_own_or_admin
ON public.personal_band_rating_events
FOR SELECT
TO authenticated
USING (user_id = auth.uid() OR public.is_platform_admin());

CREATE POLICY personal_band_rating_events_insert_own
ON public.personal_band_rating_events
FOR INSERT
TO authenticated
WITH CHECK (user_id = auth.uid());

CREATE POLICY personal_band_rating_events_update_own_or_admin
ON public.personal_band_rating_events
FOR UPDATE
TO authenticated
USING (user_id = auth.uid() OR public.is_platform_admin())
WITH CHECK (user_id = auth.uid() OR public.is_platform_admin());

CREATE POLICY personal_band_rating_events_delete_own_or_admin
ON public.personal_band_rating_events
FOR DELETE
TO authenticated
USING (user_id = auth.uid() OR public.is_platform_admin());

CREATE OR REPLACE FUNCTION public.is_platform_admin()
RETURNS boolean
LANGUAGE sql
SECURITY DEFINER
SET search_path = public
AS $$
    SELECT EXISTS (
        SELECT 1
        FROM public.profiles
        WHERE id = auth.uid()
          AND is_admin = true
    );
$$;

CREATE OR REPLACE FUNCTION public.is_group_member(target_group_id uuid)
RETURNS boolean
LANGUAGE sql
SECURITY DEFINER
SET search_path = public
AS $$
    SELECT EXISTS (
        SELECT 1
        FROM public.group_members
        WHERE group_id = target_group_id
          AND user_id = auth.uid()
    );
$$;

CREATE OR REPLACE FUNCTION public.is_group_admin(target_group_id uuid)
RETURNS boolean
LANGUAGE sql
SECURITY DEFINER
SET search_path = public
AS $$
    SELECT public.is_platform_admin()
        OR EXISTS (
            SELECT 1
            FROM public.group_members
            WHERE group_id = target_group_id
              AND user_id = auth.uid()
              AND role IN ('owner', 'admin')
        );
$$;

ALTER TABLE public.profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.groups ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.group_members ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.bands ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.stages ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.performances ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.stage_distances ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.food_options ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.ratings ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.import_batches ENABLE ROW LEVEL SECURITY;

CREATE POLICY profiles_select_self_or_admin
ON public.profiles
FOR SELECT
TO authenticated
USING (id = auth.uid() OR public.is_platform_admin());

CREATE POLICY profiles_update_self_or_admin
ON public.profiles
FOR UPDATE
TO authenticated
USING (id = auth.uid() OR public.is_platform_admin())
WITH CHECK (id = auth.uid() OR public.is_platform_admin());

CREATE POLICY groups_select_members_or_admin
ON public.groups
FOR SELECT
TO authenticated
USING (public.is_group_member(id) OR public.is_platform_admin());

CREATE POLICY groups_admin_write
ON public.groups
FOR ALL
TO authenticated
USING (public.is_platform_admin())
WITH CHECK (public.is_platform_admin());

CREATE POLICY group_members_select_same_group_or_admin
ON public.group_members
FOR SELECT
TO authenticated
USING (public.is_group_member(group_id) OR public.is_platform_admin());

CREATE POLICY group_members_admin_write
ON public.group_members
FOR ALL
TO authenticated
USING (public.is_group_admin(group_id))
WITH CHECK (public.is_group_admin(group_id));

CREATE POLICY bands_authenticated_read
ON public.bands
FOR SELECT
TO authenticated
USING (active = true OR public.is_platform_admin());

CREATE POLICY bands_admin_write
ON public.bands
FOR ALL
TO authenticated
USING (public.is_platform_admin())
WITH CHECK (public.is_platform_admin());

CREATE POLICY stages_authenticated_read
ON public.stages
FOR SELECT
TO authenticated
USING (true);

CREATE POLICY stages_admin_write
ON public.stages
FOR ALL
TO authenticated
USING (public.is_platform_admin())
WITH CHECK (public.is_platform_admin());

CREATE POLICY performances_authenticated_read
ON public.performances
FOR SELECT
TO authenticated
USING (true);

CREATE POLICY performances_admin_write
ON public.performances
FOR ALL
TO authenticated
USING (public.is_platform_admin())
WITH CHECK (public.is_platform_admin());

CREATE POLICY stage_distances_authenticated_read
ON public.stage_distances
FOR SELECT
TO authenticated
USING (true);

CREATE POLICY stage_distances_admin_write
ON public.stage_distances
FOR ALL
TO authenticated
USING (public.is_platform_admin())
WITH CHECK (public.is_platform_admin());

CREATE POLICY food_options_authenticated_read
ON public.food_options
FOR SELECT
TO authenticated
USING (true);

CREATE POLICY food_options_admin_write
ON public.food_options
FOR ALL
TO authenticated
USING (public.is_platform_admin())
WITH CHECK (public.is_platform_admin());

CREATE POLICY ratings_select_group_members
ON public.ratings
FOR SELECT
TO authenticated
USING (public.is_group_member(group_id) OR public.is_platform_admin());

CREATE POLICY ratings_insert_own_member_rating
ON public.ratings
FOR INSERT
TO authenticated
WITH CHECK (
    user_id = auth.uid()
    AND public.is_group_member(group_id)
);

CREATE POLICY ratings_update_own_member_rating
ON public.ratings
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

CREATE POLICY ratings_delete_own_or_group_admin
ON public.ratings
FOR DELETE
TO authenticated
USING (
    (user_id = auth.uid() AND public.is_group_member(group_id))
    OR public.is_group_admin(group_id)
);

CREATE POLICY import_batches_admin_access
ON public.import_batches
FOR ALL
TO authenticated
USING (public.is_platform_admin())
WITH CHECK (public.is_platform_admin());

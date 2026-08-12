CREATE TABLE public.festivals (
    id text PRIMARY KEY,
    name text NOT NULL,
    status text NOT NULL CHECK (status IN ('ACTIVE', 'ARCHIVED')),
    archived_at timestamptz,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX idx_festivals_one_active
ON public.festivals(status)
WHERE status = 'ACTIVE';

CREATE TRIGGER festivals_touch_updated_at
BEFORE UPDATE ON public.festivals
FOR EACH ROW EXECUTE FUNCTION public.touch_updated_at();

INSERT INTO public.festivals (id, name, status)
VALUES ('wacken-2026', 'Wacken Open Air 2026', 'ACTIVE')
ON CONFLICT (id) DO NOTHING;

ALTER TABLE public.festivals ENABLE ROW LEVEL SECURITY;

CREATE POLICY festivals_select_authenticated
ON public.festivals
FOR SELECT
TO authenticated
USING (true);

CREATE POLICY festivals_update_group_members
ON public.festivals
FOR UPDATE
TO authenticated
USING (
    public.is_platform_admin()
    OR EXISTS (
        SELECT 1
        FROM public.group_members
        WHERE user_id = auth.uid()
    )
)
WITH CHECK (
    public.is_platform_admin()
    OR EXISTS (
        SELECT 1
        FROM public.group_members
        WHERE user_id = auth.uid()
    )
);

CREATE POLICY festivals_insert_admin
ON public.festivals
FOR INSERT
TO authenticated
WITH CHECK (public.is_platform_admin());

CREATE POLICY festivals_delete_admin
ON public.festivals
FOR DELETE
TO authenticated
USING (public.is_platform_admin());

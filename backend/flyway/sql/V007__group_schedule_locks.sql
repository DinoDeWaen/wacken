CREATE TABLE public.group_schedule_locks (
    group_id uuid NOT NULL REFERENCES public.groups(id) ON DELETE CASCADE,
    conflict_key text NOT NULL,
    selected_candidate_key text NOT NULL,
    updated_by uuid NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),
    PRIMARY KEY (group_id, conflict_key)
);

CREATE INDEX idx_group_schedule_locks_updated_by ON public.group_schedule_locks(updated_by);

CREATE TRIGGER group_schedule_locks_touch_updated_at
BEFORE UPDATE ON public.group_schedule_locks
FOR EACH ROW EXECUTE FUNCTION public.touch_updated_at();

ALTER TABLE public.group_schedule_locks ENABLE ROW LEVEL SECURITY;

CREATE POLICY group_schedule_locks_select_group_members
ON public.group_schedule_locks
FOR SELECT
TO authenticated
USING (public.is_group_member(group_id) OR public.is_platform_admin());

CREATE POLICY group_schedule_locks_insert_group_members
ON public.group_schedule_locks
FOR INSERT
TO authenticated
WITH CHECK (
    updated_by = auth.uid()
    AND public.is_group_member(group_id)
);

CREATE POLICY group_schedule_locks_update_group_members
ON public.group_schedule_locks
FOR UPDATE
TO authenticated
USING (public.is_group_member(group_id) OR public.is_platform_admin())
WITH CHECK (
    (updated_by = auth.uid() AND public.is_group_member(group_id))
    OR public.is_platform_admin()
);

CREATE POLICY group_schedule_locks_delete_group_members
ON public.group_schedule_locks
FOR DELETE
TO authenticated
USING (public.is_group_member(group_id) OR public.is_platform_admin());

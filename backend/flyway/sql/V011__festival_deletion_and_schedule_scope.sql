-- Existing performances and group schedule locks belong to the original Wacken festival.
ALTER TABLE public.performances
    ADD COLUMN festival_id text NOT NULL DEFAULT 'wacken-2026';
ALTER TABLE public.performances
    ADD CONSTRAINT performances_festival_fk
    FOREIGN KEY (festival_id) REFERENCES public.festivals(id) ON DELETE CASCADE;
CREATE INDEX idx_performances_festival_start ON public.performances(festival_id, start_at);

ALTER TABLE public.group_schedule_locks
    ADD COLUMN festival_id text NOT NULL DEFAULT 'wacken-2026';
ALTER TABLE public.group_schedule_locks
    DROP CONSTRAINT group_schedule_locks_pkey;
ALTER TABLE public.group_schedule_locks
    ADD CONSTRAINT group_schedule_locks_pkey PRIMARY KEY (group_id, festival_id, conflict_key);
ALTER TABLE public.group_schedule_locks
    ADD CONSTRAINT group_schedule_locks_festival_fk
    FOREIGN KEY (festival_id) REFERENCES public.festivals(id) ON DELETE CASCADE;

-- History values survive festival deletion; keep the historical display name too.
ALTER TABLE public.personal_band_rating_events
    ADD COLUMN festival_name_snapshot text;
UPDATE public.personal_band_rating_events AS event
SET festival_name_snapshot = festival.name
FROM public.festivals AS festival
WHERE event.festival_id = festival.id;

CREATE FUNCTION public.capture_personal_rating_festival_name()
RETURNS trigger
LANGUAGE plpgsql
SECURITY INVOKER
SET search_path = ''
AS $$
BEGIN
    IF NEW.festival_id IS NOT NULL THEN
        SELECT name INTO NEW.festival_name_snapshot
        FROM public.festivals
        WHERE id = NEW.festival_id;
    END IF;
    RETURN NEW;
END;
$$;

CREATE TRIGGER personal_rating_festival_name_snapshot
BEFORE INSERT OR UPDATE OF festival_id ON public.personal_band_rating_events
FOR EACH ROW EXECUTE FUNCTION public.capture_personal_rating_festival_name();

DROP POLICY festivals_delete_admin ON public.festivals;
CREATE POLICY festivals_delete_group_members
ON public.festivals
FOR DELETE
TO authenticated
USING (
    public.is_platform_admin()
    OR EXISTS (
        SELECT 1 FROM public.group_members WHERE user_id = (SELECT auth.uid())
    )
);

GRANT DELETE ON public.festivals TO authenticated;
NOTIFY pgrst, 'reload schema';

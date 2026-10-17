INSERT INTO public.groups (id, slug, name)
VALUES ('00000000-0000-0000-0000-000000000001', 'wacken-2026', 'Wacken 2026')
ON CONFLICT (slug) DO UPDATE SET
    name = excluded.name,
    updated_at = now();

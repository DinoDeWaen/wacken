INSERT INTO public.groups (id, slug, name)
VALUES ('00000000-0000-0000-0000-000000000001', 'wacken-2026', 'Sofie and Dino')
ON CONFLICT (slug) DO UPDATE SET
    name = excluded.name,
    updated_at = now();

INSERT INTO public.profiles (id, email, display_name)
SELECT
    users.id,
    users.email,
    COALESCE(users.raw_user_meta_data ->> 'display_name', split_part(users.email, '@', 1))
FROM auth.users users
ON CONFLICT (id) DO UPDATE SET
    email = excluded.email,
    display_name = COALESCE(public.profiles.display_name, excluded.display_name),
    updated_at = now();

INSERT INTO public.group_members (group_id, user_id, role)
SELECT
    '00000000-0000-0000-0000-000000000001',
    users.id,
    'member'
FROM auth.users users
ON CONFLICT (group_id, user_id) DO NOTHING;

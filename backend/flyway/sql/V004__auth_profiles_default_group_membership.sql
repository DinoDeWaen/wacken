CREATE OR REPLACE FUNCTION public.handle_new_auth_user()
RETURNS trigger
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public
AS $$
BEGIN
    INSERT INTO public.profiles (id, email, display_name)
    VALUES (
        NEW.id,
        NEW.email,
        COALESCE(NEW.raw_user_meta_data ->> 'display_name', split_part(NEW.email, '@', 1))
    )
    ON CONFLICT (id) DO UPDATE SET
        email = excluded.email,
        display_name = COALESCE(public.profiles.display_name, excluded.display_name),
        updated_at = now();

    INSERT INTO public.group_members (group_id, user_id, role)
    VALUES ('00000000-0000-0000-0000-000000000001', NEW.id, 'member')
    ON CONFLICT (group_id, user_id) DO NOTHING;

    RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS auth_users_create_profile ON auth.users;
CREATE TRIGGER auth_users_create_profile
AFTER INSERT ON auth.users
FOR EACH ROW EXECUTE FUNCTION public.handle_new_auth_user();

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

CREATE POLICY profiles_insert_self
ON public.profiles
FOR INSERT
TO authenticated
WITH CHECK (id = auth.uid() OR public.is_platform_admin());

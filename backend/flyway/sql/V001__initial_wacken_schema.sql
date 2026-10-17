CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE OR REPLACE FUNCTION public.touch_updated_at()
RETURNS trigger
LANGUAGE plpgsql
AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$;

CREATE TABLE public.profiles (
    id uuid PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    display_name text,
    email text,
    is_admin boolean NOT NULL DEFAULT false,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE public.groups (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    slug text NOT NULL UNIQUE,
    name text NOT NULL,
    created_by uuid REFERENCES public.profiles(id) ON DELETE SET NULL,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE public.group_members (
    group_id uuid NOT NULL REFERENCES public.groups(id) ON DELETE CASCADE,
    user_id uuid NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    role text NOT NULL DEFAULT 'member' CHECK (role IN ('owner', 'admin', 'member')),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),
    PRIMARY KEY (group_id, user_id)
);

CREATE TABLE public.bands (
    id text PRIMARY KEY,
    name text NOT NULL,
    slug text,
    source text,
    source_id text,
    country text,
    subtitle text,
    biography_html text,
    image_url text,
    thumbnail_url text,
    youtube_url text,
    spotify_artist_id text,
    spotify_album_id text,
    homepage_url text,
    facebook_url text,
    instagram_url text,
    first_time boolean NOT NULL DEFAULT false,
    active boolean NOT NULL DEFAULT true,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE public.stages (
    id text PRIMARY KEY,
    name text NOT NULL UNIQUE,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE public.performances (
    id text PRIMARY KEY,
    band_id text NOT NULL REFERENCES public.bands(id) ON DELETE CASCADE,
    stage_id text NOT NULL REFERENCES public.stages(id) ON DELETE RESTRICT,
    start_at timestamp without time zone NOT NULL,
    end_at timestamp without time zone NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT performances_end_after_start CHECK (end_at > start_at)
);

CREATE TABLE public.stage_distances (
    from_stage_id text NOT NULL REFERENCES public.stages(id) ON DELETE CASCADE,
    to_stage_id text NOT NULL REFERENCES public.stages(id) ON DELETE CASCADE,
    walking_minutes integer NOT NULL CHECK (walking_minutes >= 0),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),
    PRIMARY KEY (from_stage_id, to_stage_id)
);

CREATE TABLE public.food_options (
    id text PRIMARY KEY,
    name text NOT NULL,
    near_stage_id text REFERENCES public.stages(id) ON DELETE SET NULL,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE public.ratings (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    group_id uuid NOT NULL REFERENCES public.groups(id) ON DELETE CASCADE,
    user_id uuid NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    band_id text NOT NULL REFERENCES public.bands(id) ON DELETE CASCADE,
    rating integer NOT NULL CHECK (rating BETWEEN 0 AND 4),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),
    UNIQUE (group_id, user_id, band_id)
);

CREATE TABLE public.import_batches (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    import_type text NOT NULL,
    file_name text,
    status text NOT NULL CHECK (status IN ('started', 'succeeded', 'failed')),
    row_count integer NOT NULL DEFAULT 0 CHECK (row_count >= 0),
    error_message text,
    started_at timestamptz NOT NULL DEFAULT now(),
    finished_at timestamptz,
    created_by uuid REFERENCES public.profiles(id) ON DELETE SET NULL
);

CREATE INDEX idx_group_members_user_id ON public.group_members(user_id);
CREATE INDEX idx_bands_name ON public.bands(name);
CREATE INDEX idx_performances_band_id ON public.performances(band_id);
CREATE INDEX idx_performances_stage_start ON public.performances(stage_id, start_at);
CREATE INDEX idx_performances_start_at ON public.performances(start_at);
CREATE INDEX idx_food_options_near_stage_id ON public.food_options(near_stage_id);
CREATE INDEX idx_ratings_group_band ON public.ratings(group_id, band_id);
CREATE INDEX idx_ratings_user ON public.ratings(user_id);
CREATE INDEX idx_import_batches_type_started ON public.import_batches(import_type, started_at DESC);

CREATE TRIGGER profiles_touch_updated_at
BEFORE UPDATE ON public.profiles
FOR EACH ROW EXECUTE FUNCTION public.touch_updated_at();

CREATE TRIGGER groups_touch_updated_at
BEFORE UPDATE ON public.groups
FOR EACH ROW EXECUTE FUNCTION public.touch_updated_at();

CREATE TRIGGER group_members_touch_updated_at
BEFORE UPDATE ON public.group_members
FOR EACH ROW EXECUTE FUNCTION public.touch_updated_at();

CREATE TRIGGER bands_touch_updated_at
BEFORE UPDATE ON public.bands
FOR EACH ROW EXECUTE FUNCTION public.touch_updated_at();

CREATE TRIGGER stages_touch_updated_at
BEFORE UPDATE ON public.stages
FOR EACH ROW EXECUTE FUNCTION public.touch_updated_at();

CREATE TRIGGER performances_touch_updated_at
BEFORE UPDATE ON public.performances
FOR EACH ROW EXECUTE FUNCTION public.touch_updated_at();

CREATE TRIGGER stage_distances_touch_updated_at
BEFORE UPDATE ON public.stage_distances
FOR EACH ROW EXECUTE FUNCTION public.touch_updated_at();

CREATE TRIGGER food_options_touch_updated_at
BEFORE UPDATE ON public.food_options
FOR EACH ROW EXECUTE FUNCTION public.touch_updated_at();

CREATE TRIGGER ratings_touch_updated_at
BEFORE UPDATE ON public.ratings
FOR EACH ROW EXECUTE FUNCTION public.touch_updated_at();

CREATE TEMP TABLE import_bands_csv (
    band_id text,
    name text,
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
    first_time text
);

UPDATE public.bands
SET active = false,
    updated_at = now()
WHERE source = 'wacken-json'
  AND id NOT IN (
      SELECT band_id
      FROM import_bands_csv
      WHERE band_id IS NOT NULL
        AND band_id <> ''
  );

WITH upserted AS (
    INSERT INTO public.bands (
        id,
        name,
        slug,
        source,
        source_id,
        country,
        subtitle,
        biography_html,
        image_url,
        thumbnail_url,
        youtube_url,
        spotify_artist_id,
        spotify_album_id,
        homepage_url,
        facebook_url,
        instagram_url,
        first_time,
        active,
        updated_at
    )
    SELECT
        band_id,
        name,
        nullif(slug, ''),
        nullif(source, ''),
        nullif(source_id, ''),
        nullif(country, ''),
        nullif(subtitle, ''),
        nullif(biography_html, ''),
        nullif(image_url, ''),
        nullif(thumbnail_url, ''),
        nullif(youtube_url, ''),
        nullif(spotify_artist_id, ''),
        nullif(spotify_album_id, ''),
        nullif(homepage_url, ''),
        nullif(facebook_url, ''),
        nullif(instagram_url, ''),
        coalesce(nullif(first_time, '')::boolean, false),
        true,
        now()
    FROM import_bands_csv
    WHERE band_id IS NOT NULL
      AND band_id <> ''
      AND name IS NOT NULL
      AND name <> ''
    ON CONFLICT (id) DO UPDATE SET
        name = excluded.name,
        slug = excluded.slug,
        source = excluded.source,
        source_id = excluded.source_id,
        country = excluded.country,
        subtitle = excluded.subtitle,
        biography_html = excluded.biography_html,
        image_url = excluded.image_url,
        thumbnail_url = excluded.thumbnail_url,
        youtube_url = excluded.youtube_url,
        spotify_artist_id = excluded.spotify_artist_id,
        spotify_album_id = excluded.spotify_album_id,
        homepage_url = excluded.homepage_url,
        facebook_url = excluded.facebook_url,
        instagram_url = excluded.instagram_url,
        first_time = excluded.first_time,
        active = true,
        updated_at = now()
    RETURNING id
)
SELECT count(*) AS imported_or_updated_bands FROM upserted;

INSERT INTO public.import_batches (import_type, file_name, status, row_count, finished_at)
SELECT 'bands_csv', :'bands_csv', 'succeeded', count(*), now()
FROM import_bands_csv;

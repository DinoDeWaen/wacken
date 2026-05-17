BEGIN;

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

CREATE TEMP TABLE import_stages_csv (
    stage_id text,
    name text,
    subtitle text,
    latitude text,
    longitude text,
    sort_order text
);

CREATE TEMP TABLE import_performances_csv (
    performance_id text,
    band_id text,
    stage_id text,
    festival_day_id text,
    start_at text,
    end_at text,
    performance_type text,
    title text,
    source_id text
);

CREATE TEMP TABLE import_distances_csv (
    from_stage_id text,
    to_stage_id text,
    walking_minutes text,
    distance_meters text,
    bidirectional text,
    source text
);

CREATE TEMP TABLE import_food_csv (
    food_id text,
    name text,
    near_stage_id text,
    walking_minutes_from_stage text,
    category text,
    notes text,
    latitude text,
    longitude text
);

-- COPY_CSV_DATA_HERE

CREATE TEMP TABLE import_validation_errors (message text);

INSERT INTO import_validation_errors
SELECT 'bands.csv duplicates band_id ' || band_id
FROM import_bands_csv
WHERE band_id IS NOT NULL AND btrim(band_id) <> ''
GROUP BY band_id
HAVING count(*) > 1;

INSERT INTO import_validation_errors
SELECT 'stages.csv duplicates stage_id ' || stage_id
FROM import_stages_csv
WHERE stage_id IS NOT NULL AND btrim(stage_id) <> ''
GROUP BY stage_id
HAVING count(*) > 1;

INSERT INTO import_validation_errors
SELECT 'performances.csv duplicates performance_id ' || performance_id
FROM import_performances_csv
WHERE performance_id IS NOT NULL AND btrim(performance_id) <> ''
GROUP BY performance_id
HAVING count(*) > 1;

INSERT INTO import_validation_errors
SELECT 'food.csv duplicates food_id ' || food_id
FROM import_food_csv
WHERE food_id IS NOT NULL AND btrim(food_id) <> ''
GROUP BY food_id
HAVING count(*) > 1;

INSERT INTO import_validation_errors
SELECT 'performances.csv performance_id ' || performance_id || ' references unknown band_id ' || band_id
FROM import_performances_csv performance
WHERE NOT EXISTS (
    SELECT 1 FROM import_bands_csv band WHERE band.band_id = performance.band_id
);

INSERT INTO import_validation_errors
SELECT 'performances.csv performance_id ' || performance_id || ' references unknown stage_id ' || stage_id
FROM import_performances_csv performance
WHERE NOT EXISTS (
    SELECT 1 FROM import_stages_csv stage WHERE stage.stage_id = performance.stage_id
);

INSERT INTO import_validation_errors
SELECT 'performances.csv performance_id ' || performance_id || ' end_at must be after start_at'
FROM import_performances_csv
WHERE NOT (end_at::timestamp > start_at::timestamp);

INSERT INTO import_validation_errors
SELECT 'distances.csv references unknown stage_id ' || from_stage_id
FROM import_distances_csv distance
WHERE NOT EXISTS (
    SELECT 1 FROM import_stages_csv stage WHERE stage.stage_id = distance.from_stage_id
);

INSERT INTO import_validation_errors
SELECT 'distances.csv references unknown stage_id ' || to_stage_id
FROM import_distances_csv distance
WHERE NOT EXISTS (
    SELECT 1 FROM import_stages_csv stage WHERE stage.stage_id = distance.to_stage_id
);

INSERT INTO import_validation_errors
SELECT 'distances.csv walking_minutes must be 0 or greater for ' || from_stage_id || ' to ' || to_stage_id
FROM import_distances_csv
WHERE walking_minutes::integer < 0;

INSERT INTO import_validation_errors
SELECT 'food.csv food_id ' || food_id || ' references unknown stage_id ' || near_stage_id
FROM import_food_csv food
WHERE btrim(coalesce(near_stage_id, '')) <> ''
  AND NOT EXISTS (
      SELECT 1 FROM import_stages_csv stage WHERE stage.stage_id = food.near_stage_id
  );

INSERT INTO import_validation_errors
SELECT 'performances.csv performance_id ' || left_performance.performance_id
    || ' overlaps ' || right_performance.performance_id
    || ' on stage_id ' || left_performance.stage_id
FROM import_performances_csv left_performance
JOIN import_performances_csv right_performance
  ON left_performance.stage_id = right_performance.stage_id
 AND left_performance.performance_id < right_performance.performance_id
 AND left_performance.start_at::timestamp < right_performance.end_at::timestamp
 AND right_performance.start_at::timestamp < left_performance.end_at::timestamp;

DO $$
DECLARE
    errors text;
BEGIN
    SELECT string_agg(message, E'\n' ORDER BY message)
    INTO errors
    FROM import_validation_errors;

    IF errors IS NOT NULL THEN
        RAISE EXCEPTION 'CSV validation failed:%', E'\n' || errors;
    END IF;
END $$;

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
    active
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
    true
FROM import_bands_csv
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
    active = true;

UPDATE public.bands
SET active = false
WHERE id NOT IN (SELECT band_id FROM import_bands_csv);

INSERT INTO public.stages (id, name)
SELECT stage_id, name
FROM import_stages_csv
ON CONFLICT (id) DO UPDATE SET
    name = excluded.name;

INSERT INTO public.performances (id, band_id, stage_id, start_at, end_at)
SELECT
    performance_id,
    band_id,
    stage_id,
    start_at::timestamp,
    end_at::timestamp
FROM import_performances_csv
ON CONFLICT (id) DO UPDATE SET
    band_id = excluded.band_id,
    stage_id = excluded.stage_id,
    start_at = excluded.start_at,
    end_at = excluded.end_at;

INSERT INTO public.stage_distances (from_stage_id, to_stage_id, walking_minutes)
SELECT
    from_stage_id,
    to_stage_id,
    walking_minutes::integer
FROM import_distances_csv
ON CONFLICT (from_stage_id, to_stage_id) DO UPDATE SET
    walking_minutes = excluded.walking_minutes;

INSERT INTO public.food_options (id, name, near_stage_id)
SELECT
    food_id,
    name,
    nullif(near_stage_id, '')
FROM import_food_csv
ON CONFLICT (id) DO UPDATE SET
    name = excluded.name,
    near_stage_id = excluded.near_stage_id;

UPDATE public.import_batches
SET
    status = 'succeeded',
    row_count = (
        (SELECT count(*) FROM import_bands_csv)
        + (SELECT count(*) FROM import_stages_csv)
        + (SELECT count(*) FROM import_performances_csv)
        + (SELECT count(*) FROM import_distances_csv)
        + (SELECT count(*) FROM import_food_csv)
    ),
    finished_at = now()
WHERE id = :'import_batch_id'::uuid;

COMMIT;

#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
ENV_FILE="${SUPABASE_ENV_FILE:-$ROOT_DIR/.env.supabase.local}"
CSV_FILE="${1:-$ROOT_DIR/data/wacken-2026/bands.csv}"

if [[ ! -f "$ENV_FILE" ]]; then
  echo "Missing $ENV_FILE. Copy backend/flyway/env.template to .env.supabase.local and fill in local credentials." >&2
  exit 1
fi

if [[ ! -f "$CSV_FILE" ]]; then
  echo "Missing CSV file: $CSV_FILE" >&2
  exit 1
fi

set -a
# shellcheck disable=SC1090
source "$ENV_FILE"
set +a

: "${SUPABASE_DB_HOST:?SUPABASE_DB_HOST is required}"
: "${SUPABASE_DB_PORT:?SUPABASE_DB_PORT is required}"
: "${SUPABASE_DB_NAME:?SUPABASE_DB_NAME is required}"
: "${SUPABASE_DB_USER:?SUPABASE_DB_USER is required}"
: "${SUPABASE_DB_PASSWORD:?SUPABASE_DB_PASSWORD is required}"

PSQL_BIN="${PSQL_BIN:-$(command -v psql || find /opt/homebrew -path '*/bin/psql' -type f 2>/dev/null | sort | tail -n 1 || true)}"

if [[ -n "$PSQL_BIN" ]]; then
  PGPASSWORD="$SUPABASE_DB_PASSWORD" PGSSLMODE=require "$PSQL_BIN" \
      -h "$SUPABASE_DB_HOST" \
      -p "$SUPABASE_DB_PORT" \
      -U "$SUPABASE_DB_USER" \
      -d "$SUPABASE_DB_NAME" \
      -v "ON_ERROR_STOP=1" <<SQL
CREATE TEMP TABLE verify_bands_csv (
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
\\copy verify_bands_csv FROM '$CSV_FILE' WITH (FORMAT csv, HEADER true)
CREATE TEMP TABLE band_import_verification AS
    SELECT
        (SELECT count(*) FROM verify_bands_csv) AS csv_rows,
        (SELECT count(*) FROM public.bands WHERE active = true) AS active_database_rows,
        (
            SELECT count(*)
            FROM verify_bands_csv csv
            LEFT JOIN public.bands band ON band.id = csv.band_id AND band.active = true
            WHERE band.id IS NULL
        ) AS missing_active_rows,
        (
            SELECT count(*)
            FROM public.bands band
            LEFT JOIN verify_bands_csv csv ON csv.band_id = band.id
            WHERE band.active = true
              AND csv.band_id IS NULL
        ) AS extra_active_rows,
        (
            SELECT count(*)
            FROM verify_bands_csv csv
            JOIN public.bands band ON band.id = csv.band_id
            WHERE band.name IS DISTINCT FROM csv.name
        ) AS mismatched_names
;
SELECT *
FROM band_import_verification;
DO \$\$
DECLARE
    result record;
BEGIN
    SELECT * INTO result FROM band_import_verification;
    IF result.missing_active_rows <> 0
        OR result.extra_active_rows <> 0
        OR result.mismatched_names <> 0
        OR result.csv_rows <> result.active_database_rows THEN
        RAISE EXCEPTION
            'Band import verification failed: csv_rows=%, active_database_rows=%, missing_active_rows=%, extra_active_rows=%, mismatched_names=%',
            result.csv_rows,
            result.active_database_rows,
            result.missing_active_rows,
            result.extra_active_rows,
            result.mismatched_names;
    END IF;
END \$\$;
SQL
  echo "Band import verified against CSV: active database rows match CSV ids and names."
else
  echo "psql is required for verification. Install libpq or set PSQL_BIN to a psql executable." >&2
  exit 1
fi

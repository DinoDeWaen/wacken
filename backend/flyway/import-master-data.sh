#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
ENV_FILE="${SUPABASE_ENV_FILE:-$ROOT_DIR/.env.supabase.local}"
BANDS_CSV="${BANDS_CSV:-$ROOT_DIR/data/wacken-2026/bands.csv}"
STAGES_CSV="${STAGES_CSV:-$ROOT_DIR/data/wacken-2026/stages.csv}"
PERFORMANCES_CSV="${PERFORMANCES_CSV:-$ROOT_DIR/data/wacken-2026/performances.csv}"
DISTANCES_CSV="${DISTANCES_CSV:-$ROOT_DIR/data/wacken-2026/distances.csv}"
FOOD_CSV="${FOOD_CSV:-$ROOT_DIR/data/wacken-2026/food.csv}"

for file in "$ENV_FILE" "$BANDS_CSV" "$STAGES_CSV" "$PERFORMANCES_CSV" "$DISTANCES_CSV" "$FOOD_CSV"; do
  if [[ ! -f "$file" ]]; then
    echo "Missing required file: $file" >&2
    exit 1
  fi
done

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
if [[ -z "$PSQL_BIN" ]]; then
  echo "psql is required for master-data import. Install libpq or set PSQL_BIN to a psql executable." >&2
  exit 1
fi

if command -v uuidgen >/dev/null 2>&1; then
  IMPORT_BATCH_ID="$(uuidgen | tr '[:upper:]' '[:lower:]')"
else
  IMPORT_BATCH_ID="$("$PSQL_BIN" -Atqc "select gen_random_uuid()")"
fi

run_psql() {
  PGPASSWORD="$SUPABASE_DB_PASSWORD" PGSSLMODE=require "$PSQL_BIN" \
      -h "$SUPABASE_DB_HOST" \
      -p "$SUPABASE_DB_PORT" \
      -U "$SUPABASE_DB_USER" \
      -d "$SUPABASE_DB_NAME" \
      -v "ON_ERROR_STOP=1" \
      "$@"
}

run_psql -c "INSERT INTO public.import_batches (id, import_type, file_name, status) VALUES ('$IMPORT_BATCH_ID', 'master-data-csv', 'data/wacken-2026/*.csv', 'started');"

ERROR_FILE="$(mktemp)"
IMPORT_SQL_FILE="$(mktemp)"
awk -v bands="$BANDS_CSV" \
    -v stages="$STAGES_CSV" \
    -v performances="$PERFORMANCES_CSV" \
    -v distances="$DISTANCES_CSV" \
    -v food="$FOOD_CSV" '
  /^-- COPY_CSV_DATA_HERE$/ {
    printf "\\copy import_bands_csv FROM '\''%s'\'' WITH (FORMAT csv, HEADER true)\n", bands
    printf "\\copy import_stages_csv FROM '\''%s'\'' WITH (FORMAT csv, HEADER true)\n", stages
    printf "\\copy import_performances_csv FROM '\''%s'\'' WITH (FORMAT csv, HEADER true)\n", performances
    printf "\\copy import_distances_csv FROM '\''%s'\'' WITH (FORMAT csv, HEADER true)\n", distances
    printf "\\copy import_food_csv FROM '\''%s'\'' WITH (FORMAT csv, HEADER true)\n", food
    next
  }
  { print }
' "$ROOT_DIR/backend/flyway/import_master_data.sql" > "$IMPORT_SQL_FILE"

if run_psql \
    -v "import_batch_id=$IMPORT_BATCH_ID" \
    -f "$IMPORT_SQL_FILE" 2>"$ERROR_FILE"; then
  rm -f "$ERROR_FILE"
  rm -f "$IMPORT_SQL_FILE"
  echo "Master-data import succeeded. import_batch_id=$IMPORT_BATCH_ID"
else
  ERROR_MESSAGE="$(tr '\n' ' ' < "$ERROR_FILE" | sed "s/'/''/g" | cut -c 1-1000)"
  cat "$ERROR_FILE" >&2
  rm -f "$ERROR_FILE"
  rm -f "$IMPORT_SQL_FILE"
  run_psql -c "UPDATE public.import_batches SET status = 'failed', error_message = '$ERROR_MESSAGE', finished_at = now() WHERE id = '$IMPORT_BATCH_ID';"
  echo "Master-data import failed. import_batch_id=$IMPORT_BATCH_ID" >&2
  exit 1
fi

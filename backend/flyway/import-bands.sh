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
  {
    sed 's/^CREATE TEMP TABLE /CREATE TEMP TABLE IF NOT EXISTS /' "$ROOT_DIR/backend/flyway/import_bands.sql" | sed '/^WITH upserted AS (/,$d'
    printf "\\copy import_bands_csv FROM '%s' WITH (FORMAT csv, HEADER true)\n" "$CSV_FILE"
    printf "\\set bands_csv '%s'\n" "$CSV_FILE"
    sed -n '/^WITH upserted AS (/,$p' "$ROOT_DIR/backend/flyway/import_bands.sql"
  } | PGPASSWORD="$SUPABASE_DB_PASSWORD" PGSSLMODE=require "$PSQL_BIN" \
      -h "$SUPABASE_DB_HOST" \
      -p "$SUPABASE_DB_PORT" \
      -U "$SUPABASE_DB_USER" \
      -d "$SUPABASE_DB_NAME" \
      -v "ON_ERROR_STOP=1"
else
  case "$CSV_FILE" in
    "$ROOT_DIR"/*)
      CONTAINER_CSV="/workspace/${CSV_FILE#"$ROOT_DIR/"}"
      ;;
    *)
      echo "CSV file must be inside the repository so Docker can mount it: $CSV_FILE" >&2
      exit 1
      ;;
  esac

  docker run --rm \
    -v "$ROOT_DIR:/workspace:ro" \
    -w /workspace \
    -e "PGPASSWORD=$SUPABASE_DB_PASSWORD" \
    -e "PGSSLMODE=require" \
    postgres:16-alpine \
    psql \
      -h "$SUPABASE_DB_HOST" \
      -p "$SUPABASE_DB_PORT" \
      -U "$SUPABASE_DB_USER" \
      -d "$SUPABASE_DB_NAME" \
      -v "ON_ERROR_STOP=1" \
      -c "$(sed 's/^CREATE TEMP TABLE /CREATE TEMP TABLE IF NOT EXISTS /' backend/flyway/import_bands.sql | sed '/^WITH upserted AS (/,$d')\\copy import_bands_csv FROM '$CONTAINER_CSV' WITH (FORMAT csv, HEADER true)
$(sed -n '/^WITH upserted AS (/,$p' backend/flyway/import_bands.sql)"
fi

#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
ENV_FILE="${SUPABASE_ENV_FILE:-$ROOT_DIR/.env.supabase.local}"

if [[ ! -f "$ENV_FILE" ]]; then
  echo "Missing $ENV_FILE. Copy backend/flyway/env.template to .env.supabase.local and fill in local credentials." >&2
  exit 1
fi

set -a
# shellcheck disable=SC1090
source "$ENV_FILE"
set +a

SUPABASE_URL="${SUPABASE_URL:-}"
SUPABASE_ANON_KEY="${SUPABASE_ANON_KEY:-}"

if [[ -z "$SUPABASE_URL" ]]; then
  SUPABASE_URL="$(awk -F'\\\\"' '/SUPABASE_URL/ {print $2; exit}' "$ROOT_DIR/app/build.gradle")"
fi

if [[ -z "$SUPABASE_ANON_KEY" ]]; then
  SUPABASE_ANON_KEY="$(awk -F'\\\\"' '/SUPABASE_ANON_KEY/ {print $2; exit}' "$ROOT_DIR/app/build.gradle")"
fi

: "${SUPABASE_URL:?SUPABASE_URL is required or must be present in app/build.gradle}"
: "${SUPABASE_ANON_KEY:?SUPABASE_ANON_KEY is required or must be present in app/build.gradle}"

echo "Checking PostgREST visibility for public.group_schedule_locks..."
response_file="$(mktemp)"
status="$(
  curl -sS -o "$response_file" -w "%{http_code}" \
    "$SUPABASE_URL/rest/v1/group_schedule_locks?select=conflict_key,selected_candidate_key&limit=1" \
    -H "apikey: $SUPABASE_ANON_KEY" \
    -H "Authorization: Bearer $SUPABASE_ANON_KEY"
)"

if [[ "$status" == "200" ]]; then
  echo "PostgREST can see public.group_schedule_locks."
else
  echo "PostgREST check failed with HTTP $status:" >&2
  cat "$response_file" >&2
  rm -f "$response_file"
  exit 1
fi
rm -f "$response_file"

if command -v psql >/dev/null 2>&1; then
  : "${SUPABASE_DB_HOST:?SUPABASE_DB_HOST is required}"
  : "${SUPABASE_DB_PORT:?SUPABASE_DB_PORT is required}"
  : "${SUPABASE_DB_NAME:?SUPABASE_DB_NAME is required}"
  : "${SUPABASE_DB_USER:?SUPABASE_DB_USER is required}"
  : "${SUPABASE_DB_PASSWORD:?SUPABASE_DB_PASSWORD is required}"

  echo "Checking Postgres table and policies..."
  PGPASSWORD="$SUPABASE_DB_PASSWORD" PGSSLMODE=require psql \
    -h "$SUPABASE_DB_HOST" \
    -p "$SUPABASE_DB_PORT" \
    -U "$SUPABASE_DB_USER" \
    -d "$SUPABASE_DB_NAME" \
    -v ON_ERROR_STOP=1 \
    -c "select to_regclass('public.group_schedule_locks') as table_name;" \
    -c "select policyname from pg_policies where schemaname = 'public' and tablename = 'group_schedule_locks' order by policyname;"
else
  echo "psql not found; skipped direct Postgres table/policy verification."
fi

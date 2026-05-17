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

: "${SUPABASE_DB_HOST:?SUPABASE_DB_HOST is required}"
: "${SUPABASE_DB_PORT:?SUPABASE_DB_PORT is required}"
: "${SUPABASE_DB_NAME:?SUPABASE_DB_NAME is required}"
: "${SUPABASE_DB_USER:?SUPABASE_DB_USER is required}"
: "${SUPABASE_DB_PASSWORD:?SUPABASE_DB_PASSWORD is required}"

PSQL_BIN="${PSQL_BIN:-$(command -v psql || find /opt/homebrew -path '*/bin/psql' -type f 2>/dev/null | sort | tail -n 1 || true)}"

if [[ -z "$PSQL_BIN" ]]; then
  echo "psql is required for auth verification. Install libpq or set PSQL_BIN to a psql executable." >&2
  exit 1
fi

PGPASSWORD="$SUPABASE_DB_PASSWORD" PGSSLMODE=require "$PSQL_BIN" \
    -h "$SUPABASE_DB_HOST" \
    -p "$SUPABASE_DB_PORT" \
    -U "$SUPABASE_DB_USER" \
    -d "$SUPABASE_DB_NAME" \
    -v "ON_ERROR_STOP=1" <<SQL
CREATE TEMP TABLE auth_setup_verification AS
    SELECT
        (SELECT count(*) FROM pg_trigger WHERE tgname = 'auth_users_create_profile') AS auth_user_trigger_count,
        (
            SELECT count(*)
            FROM pg_policies
            WHERE schemaname = 'public'
              AND tablename = 'profiles'
              AND policyname = 'profiles_insert_self'
        ) AS profiles_insert_policy_count,
        (
            SELECT count(*)
            FROM pg_policies
            WHERE schemaname = 'public'
              AND tablename = 'ratings'
        ) AS ratings_policy_count,
        (
            SELECT count(*)
            FROM public.groups
            WHERE id = '00000000-0000-0000-0000-000000000001'
        ) AS default_group_count,
        (
            SELECT count(*)
            FROM public.group_members
            WHERE group_id = '00000000-0000-0000-0000-000000000001'
              AND role IN ('member', 'admin', 'owner')
        ) AS default_group_member_count;

SELECT *
FROM auth_setup_verification;

DO \$\$
DECLARE
    result record;
BEGIN
    SELECT * INTO result FROM auth_setup_verification;
    IF result.auth_user_trigger_count <> 1
        OR result.profiles_insert_policy_count <> 1
        OR result.ratings_policy_count < 4
        OR result.default_group_count <> 1 THEN
        RAISE EXCEPTION
            'Auth setup verification failed: trigger=%, profile_insert_policy=%, ratings_policies=%, default_group=%',
            result.auth_user_trigger_count,
            result.profiles_insert_policy_count,
            result.ratings_policy_count,
            result.default_group_count;
    END IF;
END \$\$;
SQL

echo "Auth setup verified: trigger, profile insert policy, ratings RLS policies, and default group are present."

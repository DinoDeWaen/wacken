#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
ENV_FILE="${SUPABASE_ENV_FILE:-$ROOT_DIR/.env.supabase.local}"
if [[ ! -f "$ENV_FILE" ]]; then
  echo "Missing $ENV_FILE. Configure the local Supabase database credentials first." >&2
  exit 1
fi
set -a
# shellcheck disable=SC1090
source "$ENV_FILE"
set +a

PSQL_BIN="${PSQL_BIN:-$(command -v psql || true)}"
if [[ -z "$PSQL_BIN" && -x /opt/homebrew/opt/libpq/bin/psql ]]; then
  PSQL_BIN=/opt/homebrew/opt/libpq/bin/psql
fi
if [[ -z "$PSQL_BIN" ]]; then
  echo "psql is required; set PSQL_BIN to its executable." >&2
  exit 1
fi

PGPASSWORD="${SUPABASE_DB_PASSWORD:?}" PGSSLMODE=require "$PSQL_BIN" \
  -h "${SUPABASE_DB_HOST:?}" -p "${SUPABASE_DB_PORT:?}" \
  -U "${SUPABASE_DB_USER:?}" -d "${SUPABASE_DB_NAME:?}" \
  -v ON_ERROR_STOP=1 -f "$ROOT_DIR/backend/flyway/tests/festival_deletion.sql"

#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
ENV_FILE="${SUPABASE_ENV_FILE:-$ROOT_DIR/.env.supabase.local}"
COMMAND="${1:-info}"

if [[ ! -f "$ENV_FILE" ]]; then
  echo "Missing $ENV_FILE. Copy backend/flyway/env.template to .env.supabase.local and fill in local credentials." >&2
  exit 1
fi

set -a
# shellcheck disable=SC1090
source "$ENV_FILE"
set +a

: "${FLYWAY_URL:?FLYWAY_URL is required}"
: "${SUPABASE_DB_USER:?SUPABASE_DB_USER is required}"
: "${SUPABASE_DB_PASSWORD:?SUPABASE_DB_PASSWORD is required}"

if command -v flyway >/dev/null 2>&1; then
  flyway \
    -url="$FLYWAY_URL" \
    -user="$SUPABASE_DB_USER" \
    -password="$SUPABASE_DB_PASSWORD" \
    -locations="filesystem:$ROOT_DIR/backend/flyway/sql" \
    "$COMMAND"
else
  docker run --rm \
    -v "$ROOT_DIR/backend/flyway/sql:/flyway/sql:ro" \
    -e "FLYWAY_URL=$FLYWAY_URL" \
    -e "FLYWAY_USER=$SUPABASE_DB_USER" \
    -e "FLYWAY_PASSWORD=$SUPABASE_DB_PASSWORD" \
    flyway/flyway:10 \
    "$COMMAND"
fi

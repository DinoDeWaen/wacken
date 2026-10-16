#!/usr/bin/env bash
set -euo pipefail
# Only disposable local containers and fixture identities are used.
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
TEST_PREFIX="wacken-festival-$$"
TEST_DB="${TEST_PREFIX}-db"
TEST_API="${TEST_PREFIX}-api"
TEST_SECRET="local-festival-integration-secret-never-use-outside-tests"
cleanup() {
  docker rm -f "$TEST_API" "$TEST_DB" >/dev/null 2>&1 || true
  docker network rm "$TEST_PREFIX" >/dev/null 2>&1 || true
}
trap cleanup EXIT
docker network create "$TEST_PREFIX" >/dev/null
docker run -d --name "$TEST_DB" --network "$TEST_PREFIX" \
  -e POSTGRES_PASSWORD=festival-tests-only \
  public.ecr.aws/supabase/postgres:17.6.1.166 >/dev/null
for attempt in {1..60}; do
  if docker exec "$TEST_DB" pg_isready -h 127.0.0.1 -U postgres >/dev/null 2>&1; then break; fi
  if [[ "$attempt" == 60 ]]; then docker logs "$TEST_DB"; exit 1; fi
  sleep 1
done
docker cp "$ROOT_DIR/backend/flyway/sql" "$TEST_DB:/tmp/wacken-migrations"
docker exec "$TEST_DB" bash -c 'set -e; for migration in /tmp/wacken-migrations/V*.sql; do psql -X -v ON_ERROR_STOP=1 -U postgres -f "$migration" >/dev/null; done'
docker cp "$ROOT_DIR/backend/flyway/tests/local_auth.sql" "$TEST_DB:/tmp/local_auth.sql"
docker exec "$TEST_DB" psql -X -v ON_ERROR_STOP=1 -U supabase_admin -d postgres -f /tmp/local_auth.sql >/dev/null
docker cp "$ROOT_DIR/backend/flyway/tests/festival_creation.sql" "$TEST_DB:/tmp/festival_creation.sql"
docker exec "$TEST_DB" psql -X -v ON_ERROR_STOP=1 -U postgres -f /tmp/festival_creation.sql
docker exec "$TEST_DB" psql -X -v ON_ERROR_STOP=1 -U postgres -c \
  "INSERT INTO auth.users(id,email) VALUES
  ('11111111-1111-1111-1111-111111111111','creator@example.invalid'),
  ('22222222-2222-2222-2222-222222222222','other@example.invalid'),
  ('33333333-3333-3333-3333-333333333333','outsider@example.invalid');
  DELETE FROM public.group_members WHERE user_id='33333333-3333-3333-3333-333333333333';" >/dev/null
docker run -d --name "$TEST_API" --network "$TEST_PREFIX" -p 127.0.0.1::3000 \
  -e "PGRST_DB_URI=postgres://postgres:festival-tests-only@$TEST_DB:5432/postgres" \
  -e PGRST_DB_SCHEMAS=public -e PGRST_DB_ANON_ROLE=anon -e "PGRST_JWT_SECRET=$TEST_SECRET" \
  public.ecr.aws/supabase/postgrest:v14.5 >/dev/null
TEST_PORT="$(docker port "$TEST_API" 3000/tcp | cut -d: -f2)"
export WACKEN_FESTIVAL_API="http://127.0.0.1:$TEST_PORT"
export WACKEN_FESTIVAL_DB="$TEST_DB"
for attempt in {1..30}; do
  if curl -fsS "$WACKEN_FESTIVAL_API/" >/dev/null 2>&1; then break; fi
  if [[ "$attempt" == 30 ]]; then docker logs "$TEST_API"; exit 1; fi
  sleep 1
done
fixture_token() {
  python3 - "$1" "$TEST_SECRET" <<'PY'
import base64, hashlib, hmac, json, sys, time
encode=lambda value:base64.urlsafe_b64encode(json.dumps(value,separators=(',',':')).encode()).rstrip(b'=')
payload=encode({'alg':'HS256','typ':'JWT'})+b'.'+encode({'role':'authenticated','sub':sys.argv[1],'exp':int(time.time())+3600})
print((payload+b'.'+base64.urlsafe_b64encode(hmac.new(sys.argv[2].encode(),payload,hashlib.sha256).digest()).rstrip(b'=')).decode())
PY
}
export WACKEN_FESTIVAL_TOKEN="$(fixture_token 11111111-1111-1111-1111-111111111111)"
export WACKEN_FESTIVAL_OUTSIDER_TOKEN="$(fixture_token 33333333-3333-3333-3333-333333333333)"
cd "$ROOT_DIR"
./gradlew :app:testDebugUnitTest --tests be.wacken.planner.AddFestivalPersistenceRegressionTest --no-daemon
python3 - "$ROOT_DIR/app/build/test-results/testDebugUnitTest/TEST-be.wacken.planner.AddFestivalPersistenceRegressionTest.xml" <<'PY'
import sys,xml.etree.ElementTree as ET
suite=ET.parse(sys.argv[1]).getroot()
assert int(suite.attrib['tests'])>=10,suite.attrib
assert all(int(suite.attrib.get(key,0))==0 for key in ('failures','errors','skipped')),suite.attrib
print('Real PostgreSQL/PostgREST creation gate passed with no skipped tests.')
PY

#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
TEST_PREFIX="wacken-rating-state-$$"
cleanup() {
  docker rm -f "$TEST_PREFIX-api" >/dev/null 2>&1 || true
  docker rm -f "$TEST_PREFIX" >/dev/null 2>&1 || true
  docker network rm "$TEST_PREFIX" >/dev/null 2>&1 || true
}
trap cleanup EXIT
docker network create "$TEST_PREFIX" >/dev/null
docker run -d --name "$TEST_PREFIX" --network "$TEST_PREFIX" -e POSTGRES_PASSWORD=rating-state-tests-only public.ecr.aws/supabase/postgres:17.6.1.166 >/dev/null
for attempt in {1..60}; do
  if docker exec "$TEST_PREFIX" pg_isready -h 127.0.0.1 -U postgres >/dev/null 2>&1; then break; fi
  if [[ "$attempt" == 60 ]]; then docker logs "$TEST_PREFIX"; exit 1; fi
  sleep 1
done
docker cp "$ROOT_DIR/backend/flyway/sql" "$TEST_PREFIX:/tmp/wacken-migrations"
docker exec "$TEST_PREFIX" bash -c 'set -e; for migration in /tmp/wacken-migrations/V*.sql; do version=${migration##*/V}; version=${version%%__*}; if ((10#$version >= 14)); then continue; fi; psql -X -v ON_ERROR_STOP=1 -U postgres -f "$migration" >/dev/null; done'
docker cp "$ROOT_DIR/backend/flyway/tests/local_auth.sql" "$TEST_PREFIX:/tmp/local_auth.sql"
docker exec "$TEST_PREFIX" psql -X -v ON_ERROR_STOP=1 -U supabase_admin -d postgres -f /tmp/local_auth.sql >/dev/null
docker exec -i "$TEST_PREFIX" psql -X -v ON_ERROR_STOP=1 -U postgres <<'SQL'
INSERT INTO auth.users(id,email) VALUES
('11111111-1111-1111-1111-111111111111','rating-one@example.invalid'),
('22222222-2222-2222-2222-222222222222','rating-two@example.invalid'),
('33333333-3333-3333-3333-333333333333','rating-outsider@example.invalid');
DELETE FROM public.group_members WHERE user_id='33333333-3333-3333-3333-333333333333';
UPDATE public.festivals SET status='ARCHIVED';
INSERT INTO public.festivals(id,name,status) VALUES ('rating-archived','Archived','ARCHIVED'),('rating-active','Active','ACTIVE');
INSERT INTO public.bands(id,name,active) VALUES ('rating-band','Rating Band',true),('outside-band','Outside',true),('unknown-date-band','Unknown',true);
INSERT INTO public.festival_lineup_entries(festival_id,band_id,uploaded_display_name)
VALUES ('rating-active','rating-band','Rating Band'),('rating-archived','rating-band','Rating Band');
INSERT INTO public.personal_band_rating_events(id,user_id,band_id,festival_id,rating,created_at) VALUES
('11111111-1111-4111-8111-111111111111','11111111-1111-1111-1111-111111111111','rating-band','rating-archived',4,'2026-08-01T12:00:00Z'),
('22222222-2222-4222-8222-222222222222','11111111-1111-1111-1111-111111111111','rating-band','rating-active',3,'2026-10-01T12:00:00Z'),
('33333333-3333-4333-8333-333333333333','11111111-1111-1111-1111-111111111111','unknown-date-band','rating-archived',5,'1970-01-01T00:00:00Z');
SQL
docker exec "$TEST_PREFIX" bash -c 'set -e; for migration in /tmp/wacken-migrations/V*.sql; do version=${migration##*/V}; version=${version%%__*}; if ((10#$version < 14)); then continue; fi; psql -X -v ON_ERROR_STOP=1 -U postgres -f "$migration" >/dev/null; done'
docker cp "$ROOT_DIR/backend/flyway/tests/rating_state.sql" "$TEST_PREFIX:/tmp/rating_state.sql"
docker exec "$TEST_PREFIX" psql -X -v ON_ERROR_STOP=1 -U postgres -f /tmp/rating_state.sql
docker run -d --name "$TEST_PREFIX-api" --network "$TEST_PREFIX" -p 127.0.0.1::3000 \
  -e "PGRST_DB_URI=postgres://postgres:rating-state-tests-only@$TEST_PREFIX:5432/postgres" \
  -e PGRST_DB_SCHEMAS=public -e PGRST_DB_ANON_ROLE=anon \
  -e PGRST_JWT_SECRET=rating-state-http-fixture-secret-never-use-outside-tests \
  public.ecr.aws/supabase/postgrest:v14.5 >/dev/null
TEST_PORT="$(docker port "$TEST_PREFIX-api" 3000/tcp | cut -d: -f2)"
export WACKEN_RATING_API="http://127.0.0.1:$TEST_PORT"
for attempt in {1..30}; do
  if curl -fsS "$WACKEN_RATING_API/" >/dev/null 2>&1; then break; fi
  if [[ "$attempt" == 30 ]]; then docker logs "$TEST_PREFIX-api"; exit 1; fi
  sleep 1
done
python3 "$ROOT_DIR/backend/flyway/tests/rating_state_api.py"

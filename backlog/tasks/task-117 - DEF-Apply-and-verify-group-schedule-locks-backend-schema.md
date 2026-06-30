---
id: task-117
title: 'DEF: Apply and verify group schedule locks backend schema'
status: In Progress
assignee:
  - '@codex'
created_date: '2026-06-17 20:23'
updated_date: '2026-06-30 18:00'
labels:
  - defect
  - backend
  - schedule
  - supabase
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The Group Schedule screen reports that Supabase cannot find `public.group_schedule_locks` in the schema cache. The repository contains `backend/flyway/sql/V007__group_schedule_locks.sql`, so the app expects the table, but the active Supabase backend has not applied or exposed that migration.

Business value: group-wide locked schedule choices cannot sync until the backend table exists and PostgREST can see it. The generated schedule can remain usable offline, but manual locks must persist for the group when online.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given V007 has already been applied but PostgREST schema cache is stale, when verification runs, then the schema cache is refreshed or the operational step is documented and executed.
- [x] #2 Given the table is unavailable, when the app opens the schedule, then generated schedule remains visible and the warning does not block normal offline use.
- [x] #3 Migration status and lock-table verification commands are recorded in implementation notes.
- [x] #4 README/business requirements impact is recorded using the canonical wording from delivery-governance.md.
- [ ] #5 Given the production Supabase project is migrated, when the app loads group schedule locks, then `public.group_schedule_locks` exists and PostgREST no longer returns schema-cache missing-table errors.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Inspect V007 group_schedule_locks migration, Supabase lock client, and backend scripts.
2. Run Flyway info to determine whether V007 is applied in the active Supabase database.
3. Run Flyway migrate if V007 is pending.
4. Verify the physical table and RLS policies exist in Postgres without exposing credentials.
5. Verify PostgREST schema visibility for group_schedule_locks and refresh schema cache if needed.
6. Add a repeatable verification script or documentation if current commands are manual-only.
7. Run focused Android tests for schedule lock warning behavior and client parsing.
8. Record migration/status commands, README/business impact, and close the task.

Architecture impact: backend schema/operations significant but already covered by ADR 0008 and ADR 0009; no new architecture style is introduced.
Treatment: standard, because this touches production database schema visibility and operational verification.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Progress 2026-06-18

- Added an Android regression test so PostgREST schema-cache failures for `group_schedule_locks` do not leak internal table/schema details to the schedule UI.
- Updated `ScheduleErrorMessage` so the app shows: `Locked schedule choices are not available yet. Generated schedule is shown.`
- Added `backend/flyway/sql/V008__refresh_postgrest_schema_cache.sql` with `NOTIFY pgrst, 'reload schema';` so PostgREST schema cache is refreshed after migrations when Flyway can run.\n- Updated README backend troubleshooting for `group_schedule_locks` schema-cache failures.\n\nValidation run:\n\n- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:testDebugUnitTest --tests be.wacken.planner.ScheduleErrorMessageTest --tests be.wacken.planner.SupabaseScheduleLockClientTest` passed.\n\nMigration verification attempted:\n\n- `backend/flyway/run-flyway.sh info` failed before reaching the database: `NoRouteToHostException` against `db.dwyunoyaqwxkfbwscxhe.supabase.co:5432`.\n- Supabase direct database DNS did not resolve from this environment via `dig` / `nslookup`.\n- Guessed IPv4 pooler hosts resolved, but rejected the guessed `postgres.dwyunoyaqwxkfbwscxhe` tenant user.\n\nCurrent blocker:\n\n- AC1, AC3, and AC5 still require running/verifying Flyway against the active Supabase project from a network/connection string that can reach the database. The app-side warning is fixed, but the backend table still must be migrated/verified for synced group locks to work.

## Progress 2026-06-30

Added repeatable backend/API verification script `backend/flyway/verify-schedule-locks.sh`. The script checks PostgREST table visibility and, when direct Postgres is reachable with `psql`, verifies the physical table and RLS policies. README backend troubleshooting now documents this verifier.

Current Supabase verification results:

- `backend/flyway/run-flyway.sh info` still fails before migration status can be read: `NoRouteToHostException` for `db.dwyunoyaqwxkfbwscxhe.supabase.co:5432`.
- Direct PostgREST check to `/rest/v1/group_schedule_locks?select=conflict_key,selected_candidate_key&limit=1` returns HTTP 404 / `PGRST205`: `Could not find the table public.group_schedule_locks in the schema cache`.
- `backend/flyway/verify-schedule-locks.sh` reproduces the same HTTP 404 / `PGRST205` result.
- Reachable guessed pooler hosts reject the project tenant/user with `ENOTFOUND`, so the current local `.env.supabase.local` still needs a working Supabase DB connection string or the Supabase-side networking/schema issue must be fixed outside this environment.

Validation run:

- `bash -n backend/flyway/verify-schedule-locks.sh` passed.
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:testDebugUnitTest --tests be.wacken.planner.ScheduleErrorMessageTest --tests be.wacken.planner.SupabaseScheduleLockClientTest` passed.
- `git diff --check` passed.

Current blocker remains: AC1 and AC5 require a functioning Supabase database/PostgREST schema state. The app fallback remains valid, but synced group-wide schedule locks cannot be accepted as complete until `public.group_schedule_locks` is migrated and visible through PostgREST.

## Retry 2026-06-30

Retried backend verification for task-117. The blocker is unchanged:

- `backend/flyway/verify-schedule-locks.sh` returns HTTP 404 / `PGRST205`: PostgREST cannot find `public.group_schedule_locks` in the schema cache.
- `backend/flyway/run-flyway.sh info` still cannot connect to the direct Supabase database host and fails with `NoRouteToHostException` for `db.dwyunoyaqwxkfbwscxhe.supabase.co:5432`.

Conclusion: AC1 and AC5 remain blocked by Supabase-side database connectivity/schema state. The next required input is a working Supabase database connection string/pooler configuration or a Supabase-side migration/schema-cache fix.
<!-- SECTION:NOTES:END -->

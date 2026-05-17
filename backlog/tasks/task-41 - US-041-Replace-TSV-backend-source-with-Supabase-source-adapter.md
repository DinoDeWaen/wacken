---
id: task-41
title: 'US-041: Replace TSV backend source with Supabase source adapter'
status: Done
assignee:
  - '@codex'
created_date: '2026-05-17 16:52'
updated_date: '2026-05-17 18:02'
labels:
  - backend
  - supabase
  - sync
  - room
dependencies:
  - task-39
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a festival attendee, I want the app to synchronize festival master data from Supabase into the local Room cache so that centrally managed bands and schedule data appear in the app without manual TSV import.

Business value:
- Replaces the MVP TSV backend-like source with the real central backend while preserving the Room cache and clean domain boundaries.
- Lets the app owner manage bands centrally in Supabase.

In scope:
- Add Supabase source adapters for bands, stages, performances, stage distances, and food options.
- Keep Room as the local cache used by app reads.
- Keep TSV source available only as a fallback/import tool if needed, not as the primary source.
- Pull backend master data into Room using explicit sync behavior.
- Keep domain/application modules independent of Supabase client details.

Out of scope:
- User rating push/pull sync.
- Admin data editing UI.
- Realtime subscriptions.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given central band data exists in Supabase, when the app syncs, then Room contains the latest bands and the overview can list them.
- [x] #2 Given central performance/stage data exists in Supabase, when the app syncs, then schedule metadata is available from Room.
- [x] #3 Given the app lists bands, then it reads from Room and does not query Supabase directly from UI code.
- [x] #4 Given Supabase replaces TSV as the primary source, then domain/application APIs remain unchanged or changes are explicitly justified.
- [x] #5 Given sync fails, then existing Room data remains available and the user sees a clear error or stale-data state where applicable.
- [x] #6 Automated tests cover adapter mapping and sync behavior; existing listing/import/rating tests remain green.
- [x] #7 README documents the Supabase sync setup and remaining TSV fallback/import role.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Inspect existing TSV source, Room cache, sync repository decorators, Supabase schema, and startup/import flows.
2. Add focused tests for Supabase master-data mapping and cache-preserving sync failure behavior.
3. Implement Android Supabase master-data client/adapters for bands, stages, performances, stage distances, and food options using the existing domain repository ports.
4. Wire AppRepositories so Room remains the read cache and Supabase is the primary source; keep TSV import as an explicit fallback/admin import path.
5. Add explicit sync behavior and a visible stale/error state when Supabase sync fails while cached Room data remains usable.
6. Update README with Supabase sync setup and TSV fallback role.
7. Run Gradle checks, validate against Supabase, update task notes/acceptance criteria, and commit task-41.

Architecture impact: standard depth. This implements the Supabase source adapter direction already approved in task-39/ADR-0008 and keeps domain/application APIs unchanged behind existing repository ports. No additional architecture approval expected unless implementation requires a new dependency or boundary change.
Test strategy: adapter mapping unit tests plus existing listing/import/rating regression checks.
Risks/assumptions: Supabase currently has central bands; schedule/stage/food tables may be empty until imported, so sync must tolerate empty central datasets without wiping usable cache on request failure. README impact expected; diagram/ADR impact likely not needed beyond ADR-0008.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Implementation notes:
- Added Supabase master-data client and source repositories for bands, stages, performances, stage distances, and food options.
- Wired AppRepositories so the default source is Supabase while Room remains the app read cache behind existing domain repository ports.
- Kept CSV/TSV as an explicit fallback by routing ImportCsvActivity through AppRepositories.tsvFallback(...).
- Added Sync from Supabase action and first-empty-cache auto-sync behavior on the band overview. Sync failures leave existing Room data intact and show a cached/stale-data message.
- Added mapper tests for Supabase band/timestamp data and a sync failure regression test proving existing cache data survives source failure.

Validation package:
- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:testDebugUnitTest :infrastructure:test passed.
- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew test passed.
- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew qaTest passed.
- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew assembleDebug passed.
- backend/flyway/verify-bands-import.sh passed: 164 CSV rows match 164 active Supabase band rows, with 0 missing/extra/name mismatches.

README impact: updated to document Supabase as the primary master-data sync source, Room as the app read cache, and TSV/CSV as fallback import tooling.
Diagram impact: README C4 diagrams updated to show Supabase and TSV fallback roles.
ADR impact: no new ADR needed; implementation follows ADR-0008 and the approved Supabase source direction without changing domain/application ports.
Approval status: no new architecture-significant decision beyond the previously approved Supabase/Flyway backend direction.
Risks/follow-ups: live app sync still requires signing in with a Supabase Auth user. Supabase currently has verified band rows; stage/performance/distance/food sync paths are implemented but depend on central data being populated. Rating sync is intentionally deferred to task-42.
<!-- SECTION:NOTES:END -->

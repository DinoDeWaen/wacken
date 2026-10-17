---
id: task-183
title: 'DEF: New festivals and imported lineups do not sync group-wide'
status: Done
assignee:
  - '@codex'
created_date: '2026-08-28 06:32'
updated_date: '2026-08-28 06:50'
labels:
  - defect
  - supabase
  - festival
  - sync
  - android
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Business value: when one group member archives Wacken and adds the next festival from a CSV, other signed-in group members must see the same active festival and lineup after Supabase sync.

Observed defect: a newly added festival is saved only in the Android Room cache. Another app/device cannot see it after sync. Imported lineup entries and newly created band records are also not written to Supabase from the Android app.

Scope: wire the existing Supabase festivals and festival_lineup_entries tables into Android sync, make imported/new band records write through to Supabase, and document/print the current group-wide data model. Existing local-first Room behavior, one-active-festival invariant, explicit festival naming, reviewed band linking, metadata no-overwrite behavior, and rating sync must remain intact.

Out of scope: multiple groups, multiple active festivals, editing archived festivals, new Supabase migrations unless tests prove the existing schema is insufficient, and automatic fuzzy linking.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given a user adds a new festival from CSV while signed in, then the festival, imported lineup entries, and newly created bands are written to Supabase so another signed-in app can pull them.
- [x] #2 Given another signed-in app runs Supabase sync, then it reads festivals and festival lineup entries from Supabase into Room and shows the shared active festival.
- [x] #3 Given Wacken is archived or an active festival is renamed, then the shared festival status/name is written to Supabase and visible after another app syncs.
- [x] #4 Given Supabase is unavailable, then local Room state remains usable and the sync failure is surfaced through the existing sync error path.
- [x] #5 Automated tests cover Supabase festival/lineup/band write-through and pull behavior at the adapter level and protect existing festival use-case behavior.
- [x] #6 The data model for group-wide festival sync is documented in README/business requirements and printed in the final response.
- [x] #7 Architecture, README, business requirements, diagram, and ADR impact are recorded using delivery-governance wording.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Inspected Room/Supabase repository wiring, Supabase client helpers, backend schema, RLS assumptions, existing tests, README, business requirements, ADR 0010, and ADR 0011.
2. Added focused adapter tests for group-wide festival cache/source write-through, lineup cache/source write-through, Supabase festival/lineup mapping, deterministic band ids, Spotify id extraction, and metadata no-overwrite patches.
3. Implemented Supabase master-data read/write methods for festivals, festival lineups, and bands using existing backend tables and source-first writes.
4. Added syncing festival and lineup repositories and wired signed-in Android repositories to Supabase plus Room cache.
5. Updated add-festival and rename UI paths so shared-write failures are visible and do not silently become personal-only local data.
6. Updated README and business requirements with group-wide festival sync behavior and the current data model.
7. Ran focused app tests/compile, broader module validation, and assembleDebug.
Architecture impact: not a new architecture decision; ADR 0011 and ADR 0010 already define Supabase-backed festival/lineup sync and offline-first Room behavior. This task completes missing adapters against existing tables without changing schema, module boundaries, or domain invariants.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

- Fixed add-festival mode to use signed-in Supabase-backed repositories instead of the TSV fallback repository path.
- Added source-first syncing repositories for festivals and festival lineups so add, archive, rename, save lineup, and pull sync update Supabase and Room consistently.
- Added Supabase festival, lineup, and band write/read adapter support, including exact-name band id reuse and no-overwrite handling for existing non-empty band metadata.
- Added Room replace-all support for festival and lineup cache refreshes.
- Added visible error handling for add-festival and active-festival rename failures.
- Documented the group-wide festival data model in README and updated business requirements.

## Validation

- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:testDebugUnitTest :app:compileDebugJavaWithJavac` - passed.
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac` - passed.
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew assembleDebug` - passed.

README impact: updated README with group-wide festival sync behavior and the current festival data model.
Business requirements impact: updated BR-057, BR-087, BR-090, and added BR-105a for group-wide lifecycle changes.
Diagram impact: none, because existing README architecture diagrams already show Supabase as the central backend and this task did not change module boundaries.
ADR impact: none, because ADR 0010 and ADR 0011 already define the offline Room cache and Supabase-backed festival/lineup direction.
Residual risk: the existing backend RLS allows festival and lineup insertion only for platform admins, so non-admin add-festival attempts now fail visibly instead of creating local-only data.
<!-- SECTION:NOTES:END -->

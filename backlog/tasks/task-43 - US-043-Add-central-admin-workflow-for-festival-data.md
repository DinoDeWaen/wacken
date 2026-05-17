---
id: task-43
title: 'US-043: Add central admin workflow for festival data'
status: Done
assignee:
  - '@codex'
created_date: '2026-05-17 16:52'
updated_date: '2026-05-17 18:16'
labels:
  - admin
  - backend
  - supabase
  - csv
dependencies:
  - task-39
  - task-41
priority: medium
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As the app owner, I want to manage bands and festival master data centrally so that users receive corrected lineup data without each device importing CSV files manually.

Business value:
- Moves admin work out of each Android device and into one central backend process.
- Allows band metadata, running order, links, and food/stage data to be corrected once for everyone.

In scope:
- Define the admin workflow for creating/updating bands, stages, performances, distances, and food options in Supabase.
- Support CSV import into the backend schema with validation and import history.
- Preserve existing validation rules for missing references, invalid times, and stage overlaps.
- Record who ran an import and when.

Out of scope:
- Full polished admin web application unless explicitly approved later.
- Android-based admin UI.
- Automated scraping from Wacken pages.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given an admin has a valid CSV dataset, when the backend import is run, then Supabase master data is updated centrally.
- [x] #2 Given invalid CSV data is provided, then the import reports validation errors and does not partially corrupt master data.
- [x] #3 Given users sync after an admin update, then their app receives the corrected master data through Room cache sync.
- [x] #4 Given import history is reviewed, then the admin can see when an import ran and whether it succeeded or failed.
- [x] #5 Existing CSV validation behavior is preserved or intentionally changed with explicit acceptance criteria.
- [x] #6 Automated tests or executable validation cover backend import validation and successful import.
- [x] #7 README documents the admin import workflow and any required commands.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Inspect current backend import scripts, CSV schemas, application validation rules, and Supabase import history table.
2. Add a backend admin import script for bands, stages, performances, distances, and food using one validated transaction plus import_batches history.
3. Preserve validation rules for duplicate ids, missing band/stage references, invalid performance times, overlapping stage performances, negative walking minutes, and invalid food stage references.
4. Add verification/dry-run behavior or executable validation that proves successful import and failed validation behavior without corrupting central data.
5. Document the central admin workflow and TSV/CSV fallback relationship in README.
6. Run backend import validation plus Gradle checks, update task notes/acceptance criteria, and commit task-43.

Architecture impact: standard depth. This adds backend admin automation around the existing Supabase schema/import_batches table from ADR-0008 without changing Android/domain boundaries. No new architecture approval expected.
Test strategy: executable script validation against Supabase plus existing Gradle regression checks.
Risks/assumptions: data/wacken-2026 currently has full bands and header-only schedule/food files, so the production import can validate and preserve empty schedule datasets until final running-order data exists.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Implementation notes:
- Added backend/flyway/import-master-data.sh and backend/flyway/import_master_data.sql for central admin imports of bands, stages, performances, stage distances, and food options.
- The script creates an import_batches row, validates the CSV set, applies data changes in one transaction, marks success with row_count, and marks failure with the captured error message.
- Preserved validation coverage for duplicate ids, missing band/stage references, invalid performance time ranges, stage overlaps, negative walking minutes, and invalid food stage references.
- The Android sync path from task-41 receives these central updates through Supabase-to-Room sync.

Validation package:
- backend/flyway/import-master-data.sh succeeded against data/wacken-2026: 164 bands imported and header-only schedule/food files accepted. Successful import batch recorded: 5ab0aa99-51b2-4395-9b2a-81eceb73fc9a.
- Invalid import validation was executed with sample invalid performances. It failed with clear validation errors for unknown band_id and stage_id and recorded a failed import batch: 9b063b1d-6ae3-4c06-b6c7-e7adc6dc2a96.
- backend/flyway/verify-bands-import.sh passed after the failed import, proving the invalid import did not corrupt the central active band dataset.
- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew test passed.

README impact: updated with the central admin import command, environment overrides, validation behavior, import history, and failure guarantees.
Diagram impact: no new diagram needed; task-41 already updated the Supabase/CSV source roles.
ADR impact: no new ADR needed; this is operational tooling for ADR-0008 rather than a new architecture decision.
Approval status: no new architecture-significant decision introduced.
Risks/follow-ups: production running-order files are currently header-only, so stages/performances/distances/food remain empty until real Wacken schedule data is available.
<!-- SECTION:NOTES:END -->

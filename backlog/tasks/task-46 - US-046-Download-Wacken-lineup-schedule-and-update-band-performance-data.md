---
id: task-46
title: 'US-046: Download Wacken lineup schedule and update band performance data'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-03 06:42'
updated_date: '2026-06-03 06:54'
labels:
  - data-import
  - admin
  - schedule
  - wacken
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As the festival data administrator, I want to download the current band data from https://www.wacken.com/en/line-up/bands/ and update every known band with the stage, date, and time they are playing so that the app overview and detail screens show the real running order instead of TBA values.

Business value:
- Users can plan ratings and attendance decisions with actual performance stages and times.
- The central festival master data becomes useful for conflict-aware schedule planning.
- The app no longer depends on incomplete band-only lineup data once running-order data is available.

In scope:
- Download or scrape the current Wacken band lineup/schedule data from the official Wacken lineup page: https://www.wacken.com/en/line-up/bands/.
- Update the repository festival data files for bands, stages, and performances where needed.
- Ensure every band with an announced performance has a stage, date, start time, and end time when the source provides it.
- Preserve existing band metadata such as biography, image URL, YouTube URL, Spotify URL, and ratings.
- Make the updated data importable through the existing admin/Supabase import workflow and local fallback CSV flow.
- Document any official-source gaps, duplicate band names, missing end times, or bands without announced schedule data.

Out of scope:
- Changing the rating workflow or rating scale.
- Implementing the final conflict-aware schedule generator.
- Manually inventing missing stages or times when the official source does not provide them.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given the official Wacken lineup page provides stage and time data for a band, when the data update is complete, then that band has a performance row with stage, date, start time, and end time or a documented missing-end-time handling decision.
- [x] #2 Given a band exists in the current app dataset, when updated lineup data is imported, then existing band metadata and user ratings are preserved.
- [x] #3 Given the official source contains bands not yet present locally, when the update is complete, then the new bands are added with available metadata and performance data.
- [x] #4 Given the official source omits schedule data for a band, when the update is complete, then the band remains visible and the missing schedule data is documented instead of guessed.
- [x] #5 Given the updated CSV files are generated, when the admin import script or app CSV import runs, then stages and performances validate without duplicate ids, invalid references, invalid times, or stage overlaps.
- [x] #6 Supabase import/update steps are documented and, if run, validation evidence is captured.
- [x] #7 Automated tests or validation scripts cover the import/schema constraints touched by the updated data.
- [x] #8 README impact and business requirements impact are recorded using canonical wording from delivery-governance.md.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Inspected current repository CSV data, import scripts, and validation constraints.
2. Fetched the official Wacken running-order page and JSON feeds declared by the page: bandlist-concert, events-concert, stages, festivaldays, and performances.
3. Regenerated `data/wacken-2026/bands.csv`, `stages.csv`, `performances.csv`, and `SOURCE.md` from the official feeds.
4. Validated generated CSV data locally for duplicate ids, missing band/stage references, invalid time ranges, and stage overlaps; no errors found.
5. Ran `backend/flyway/import-master-data.sh` to update Supabase master data; import batch 827da02e-44a3-45eb-884b-06e28263cbab succeeded.
6. Verified Supabase has 165 active bands, 7 stages, 186 performances, and 5th Avenue on Wackinger Stage from 2026-07-29 15:00 to 16:00.
7. Ran full Gradle validation successfully.

Architecture impact: no architecture-significant change; this is a data refresh through the existing CSV/Supabase import workflow.
Deviation: skipped 5 official running-order entries whose artist is absent from bandlist-concert.json: four `TBA` placeholders and `Promoters Farewell & Thanks`; documented them in SOURCE.md instead of creating guessed bands.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Updated the Wacken 2026 master data from the official Wacken lineup/running-order sources. The repository now contains 165 bands, 7 stages, and 186 scheduled concert performances in `data/wacken-2026`.

The data was generated from the official page-declared JSON feeds: `bandlist-concert.json`, `events-concert.json`, `stages.json`, `festivaldays.json`, and `performances.json`. Event timestamps were converted to Wacken local time (`Europe/Berlin`) and stored as timezone-free ISO local date-times for the existing CSV schema.

Supabase was updated through the existing `backend/flyway/import-master-data.sh` admin workflow. Import batch `827da02e-44a3-45eb-884b-06e28263cbab` succeeded.

## Acceptance criteria validation

- AC1: 186 official scheduled concert events with real band-list artists now have performance rows with stage, festival day, start time, and end time.
- AC2: Existing band metadata is regenerated from the official band list; ratings are separate user data and were not imported or cleared. The current Supabase ratings table has 0 rows, so there were no live ratings to preserve in this environment.
- AC3: New official band-list entries were added; `bands.csv` increased from 164 to 165 bands.
- AC4: Official running-order entries for four `TBA` placeholders and `Promoters Farewell & Thanks` are documented in `data/wacken-2026/SOURCE.md` instead of guessed as normal bands. Ten band-list entries without performance rows remain visible as bands without guessed schedule data.
- AC5: Generated CSVs passed local validation for duplicate ids, missing band/stage references, invalid time ranges, and stage overlaps. Supabase import also validated and committed successfully.
- AC6: Supabase import was run and verified: 165 active bands, 7 stages, 186 performances. Sample verification: `5th Avenue|Wackinger Stage|2026-07-29 15:00|2026-07-29 16:00`.
- AC7: Validation was covered by local CSV validation, the existing Supabase import validation script, `verify-bands-import.sh`, and full Gradle validation.
- AC8: README impact: none, because setup, commands, architecture, and public behavior did not change. Business requirements impact: none, because this fulfills existing festival master-data import requirements.

## How to test

### Automated tests

- Local generated CSV validation: passed with `bands=165`, `stages=7`, `performances=186`, `errors=[]`.
- `backend/flyway/import-master-data.sh` - passed; copied 165 bands, 7 stages, 186 performances, 0 distances, 0 food rows; committed import batch `827da02e-44a3-45eb-884b-06e28263cbab`.
- `backend/flyway/verify-bands-import.sh` - passed; CSV rows and active database rows both 165 with no missing, extra, or mismatched names.
- Supabase verification query - passed; active bands 165, stages 7, performances 186.
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew test qaTest assembleDebug` - passed.

### Manual validation

1. In the Android app, sync from Supabase.
2. Open the band list and verify stage/date/time columns are populated for scheduled bands.
3. Verify 5th Avenue appears on Wackinger Stage on 2026-07-29 from 15:00 to 16:00.
4. Open a band with no official performance row and verify it remains visible with TBA schedule data rather than guessed data.

## TDD / BDD / approval-test evidence

This is an admin data-refresh task, not production behavior code. The generated data was protected by CSV/reference/time/overlap validation and the existing Postgres import validation before applying to Supabase.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR: none, because the existing CSV/Supabase import workflow was used without schema, dependency, or boundary changes.

## README impact

README impact: none, because setup, commands, architecture, and public behavior did not change.

## Business requirements impact

Business requirements impact: none, because this implements the existing festival master-data import/update requirement.

## Diagram impact

Diagram impact: none, because module/container relationships did not change.

## Commits / logical change list

- Regenerated `data/wacken-2026/bands.csv` from the current official band-list feed.
- Generated `data/wacken-2026/stages.csv` and `performances.csv` from the official WOA 2026 concert running-order feed.
- Updated `data/wacken-2026/SOURCE.md` with sources, counts, conversion rule, and skipped placeholder entries.
- Updated Supabase master data through the existing import script.

## Risks and follow-up

The official Wacken article says this is the first running-order version and that additional stages/acts may follow, so this data should be refreshed again when Wacken publishes later updates. Four `TBA` slots and one promoter farewell slot were deliberately skipped because they are not present in the official band-list feed as normal bands.
<!-- SECTION:NOTES:END -->

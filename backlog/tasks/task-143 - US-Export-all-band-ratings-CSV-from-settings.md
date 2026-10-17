---
id: task-143
title: 'US: Export all band ratings CSV from settings'
status: Done
assignee:
  - '@codex'
created_date: '2026-07-02 08:28'
updated_date: '2026-07-06 08:15'
labels:
  - mvp3
  - export
  - settings
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Business value: users need an easy way to take all Wacken rating data out of the app for review, sharing, or backup.

As a Wacken Planner user, I want to export all locally available band ratings from the settings screen, so that I can inspect and keep my planning data outside the app.

Scope: settings action, CSV generation from local cache, Android share/save handoff, and export feedback. The export must work offline from cached data.

Out of scope: changing rating rules, importing the exported CSV back into the app, or Play Store file-provider polishing beyond what direct install needs.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given locally available band data, when I export ratings from settings, then a CSV file is produced with one row per locally known band
- [x] #2 Given ratings are locally available, when the CSV is exported, then it includes band identity, band name, planning rating, real post-show rating if available, locally cached group member ratings where available, stage/date/time where known, and schedule status where known
- [x] #3 Given the device is offline, when I export ratings, then the export uses cached data and does not require Supabase
- [x] #4 Given export succeeds or fails, when the user returns to settings, then clear Wacken-styled feedback is shown
- [x] #5 Automated tests cover CSV escaping, empty/unrated values, and locally cached group ratings
- [x] #6 Manual test steps are documented in implementation notes
- [x] #7 Business requirements and README impact use canonical delivery-governance wording
- [x] #8 Architecture impact is assessed before implementation; if storage/export boundaries require an architecture-significant change, explicit approval is requested before coding
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Inspected SettingsActivity, local Room-backed repositories, list/performance/rating models, and Android file sharing options.
2. Added ExportRatingsCsvUseCase and tests for CSV escaping, unrated empty fields, cached group ratings, real ratings, schedule metadata, and performance-only bands.
3. Implemented local-cache-only CSV generation from BandRepository, PerformanceRepository, RatingRepository, and RealRatingRepository; repository reads use Room cache and do not call Supabase.
4. Added AndroidX Core FileProvider configuration and Settings export action that writes `wacken-ratings.csv` to app cache and launches Android share/save targets.
5. Updated README and business requirements to document Settings CSV export and FileProvider usage.
6. Ran application tests, full multi-module validation, debug build, signed release build, APK signature/hash checks, and diff hygiene.

Architecture impact: not architecture-significant. The change adds an application use case and Android framework FileProvider wiring for task-scoped file sharing, with no new backend, persistence schema, sync strategy, domain boundary, or module dependency direction change. No approval or ADR required.
Deviation: physical Android share/save validation was not run in this environment.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

- Added `ExportRatingsCsvUseCase` to generate a local-cache CSV with one row per locally known band.
- CSV columns are `band_id`, `band_name`, `planning_rating`, `real_rating`, `group_ratings`, `stage`, `date`, `time`, and `schedule_status`.
- Added Settings action **Export ratings CSV** that writes `wacken-ratings.csv` under app cache and shares it through Android `FileProvider`.
- Export uses existing local repository reads only; it does not trigger Supabase sync or require network.
- Updated README and business requirements for the new export behavior.

## Acceptance criteria validation

- AC1: `ExportRatingsCsvUseCaseTest` verifies one CSV row per local band and includes performance-only cached bands.
- AC2: Tests verify band identity/name, planning rating, real rating, cached group member ratings, stage, date, time, and schedule status.
- AC3: Export reads `AppRepositories` local `findAll`/rating methods only and does not call sync methods or Supabase clients.
- AC4: Settings shows success feedback after launching the share chooser and failure feedback if export file/share setup fails.
- AC5: Automated tests cover CSV escaping, empty/unrated values, and locally cached group ratings.
- AC6: Manual test steps are documented below.
- AC7: README and business requirements were updated with canonical impact notes below.
- AC8: Architecture impact was assessed as not architecture-significant; no approval or ADR was required.

## How to test

### Automated tests

- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :application:test`
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac assembleDebug`
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) WACKEN_RELEASE_* ./gradlew assembleRelease` with local keychain-backed signing values
- `git diff --check`

### Manual validation

- Open Settings, tap **Export ratings CSV**, choose a share/save target, and confirm `wacken-ratings.csv` contains one row per cached band.
- Repeat with device network disabled after cached data exists; export should still open the share/save chooser.
- Confirm unrated planning/real ratings are empty CSV fields and cached group ratings appear in `user=rating;user=rating` form.
- Signed APK verification: `apksigner verify --verbose app/build/outputs/apk/release/app-release.apk` verifies v1 and v2 signatures with one signer.
- APK metadata: package `be.wacken.planner`, versionCode `23`, versionName `2.20`, minSdkVersion `23`.
- Debug APK SHA-256: `c54641dd20006d2c170fd6e962e62e6d658e3f8ebe12be802a71751619b9830a`.
- Release APK SHA-256: `3c25a7cb9bf6ec29f5d4a43fac230b82f917a7951ab1b3cf55355b5b288e4390`.
- Physical Android share/save validation was not run in this environment.

## TDD / BDD / approval-test evidence

- Added focused application tests for the CSV export behavior before Android wiring.
- BDD acceptance is covered by tests for complete rows, escaping, unrated fields, cached group ratings, and schedule metadata.
- No approval tests were needed because this was new export behavior rather than legacy refactoring.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR: none, because the existing application/Android boundary is preserved and no backend, sync, or persistence schema strategy changed.

## README impact

README impact: updated Basic Functionality and Technologies to document Settings CSV export and AndroidX Core FileProvider usage.

## Business requirements impact

Business requirements impact: updated current MVP3 capability wording to include CSV export from settings.

## Diagram impact

Diagram impact: none, because module/container relationships did not change.

## Commits / logical change list

- Added local ratings CSV export use case and tests.
- Added Settings export action and FileProvider share configuration.
- Added AndroidX Core dependency for FileProvider.
- Updated README and business requirements.

## Risks and follow-up

- Physical Android share/save UX should be smoke-tested before release.
- Export uses band name as the stable band identity because no separate public band id exists in the domain model.
<!-- SECTION:NOTES:END -->

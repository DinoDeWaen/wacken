---
id: TASK-187
title: 'DEF: Keep Android festival data unchanged when import persistence fails'
status: Done
assignee:
  - '@codex'
created_date: '2026-10-14 06:38'
updated_date: '2026-10-15 18:18'
labels:
  - android
  - import
  - persistence
milestone: m-1
dependencies: []
references:
  - >-
    application/src/main/java/be/wacken/planner/application/ImportFestivalCsvUseCase.java
  - >-
    infrastructure/src/main/java/be/wacken/planner/infrastructure/SyncedBandRepository.java
  - filebackedstorage
  - importfestivalcsvusecase
  - apprepositories
  - importcsvactivity
modified_files:
  - >-
    infrastructure/src/test/java/be/wacken/planner/infrastructure/TsvImportTransactionTest.java
  - >-
    infrastructure/src/main/java/be/wacken/planner/infrastructure/TsvImportTransaction.java
  - >-
    application/src/test/java/be/wacken/planner/application/ImportFestivalCsvUseCaseTest.java
  - >-
    application/src/main/java/be/wacken/planner/application/ImportFestivalCsvUseCase.java
  - app/src/main/java/be/wacken/planner/AppRepositories.java
  - app/src/main/java/be/wacken/planner/ImportCsvActivity.java
  - backlog/decisions/0007-room-local-cache-with-tsv-backend-source.md
  - README.md
  - backlog/docs/business-requirements.md
priority: medium
type: bug
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Business value: A failed device-side festival import must not leave users with an incomplete or inconsistent lineup.

Current evidence: ImportFestivalCsvUseCase validates in memory, then replaces bands, stages, performances, distances, and food sequentially. Synced repositories write source and Room cache separately. The backend admin script in completed TASK-43 has its own transaction and is out of scope.

Scope: First reproduce or rule out partial writes with failure-injection tests in the Android/application import path. If confirmed, make the import commit failure-safe across affected master-data repositories and source/cache updates while preserving ratings.

Out of scope: Backend admin import script, unrelated sync redesign, and a general offline conflict engine.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given a persistence failure after import begins, when the Android festival import fails, then previously usable festival master data remains complete and consistent across affected repositories and cache/source state, or is restored before the failure is reported.
- [x] #2 Given valid input with no persistence failure, when import succeeds, then all five master-data categories are updated and existing ratings are preserved.
- [x] #3 Automated failure-injection tests demonstrate the initial risk or rule it out, and cover the resulting failure and success behavior.
- [x] #4 Architecture and transaction-boundary impact are assessed before implementation; if architecture-significant, explicit approval is obtained before coding and an ADR is created or updated.
- [x] #5 README and business requirements impact are recorded using the canonical wording from delivery-governance.md, and validation evidence is added before completion.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Treatment: standard persistence-boundary change for the Android five-file TSV fallback import.
Completed: confirmed that uncoordinated synced repositories leave earlier TSV writes after a later cache failure. Added TsvImportTransaction to snapshot the five TSV files, execute replacements under one Room transaction, restore source files after a reported write failure, and retain backup files if restoration fails. The application use case validates first, delegates writes to the transaction, and returns persistence errors. Android composition root wires the transaction for TSV fallback only; Supabase and backend admin import are unchanged.
Tests: red compile tests for missing transaction and missing use-case hook, then green failure/success tests; full Gradle test, lintVitalRelease, assembleDebug, and fresh signed assembleRelease; APK signature/metadata/SHA and git diff --check.
Architecture: user explicitly approved this transaction-boundary design on 2026-10-15; ADR 0007, README, and BR-042a/workflow updated. No diagram change because the existing module/container boundaries remain.
Deviation: used a focused infrastructure transaction wrapper plus an application transaction callback rather than a new repository port; this keeps the Room and TSV mechanics outside the application module. Import-time failures are covered; process/power-loss recovery is a follow-up risk.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

- The Android five-file TSV fallback import now runs source and cache replacement as one coordinated operation. Existing TSV files are copied before writes; all Room changes share one database transaction; source copies are restored when an import-time write fails. Ratings are not replaced.
- The use case validates before entering that boundary and reports persistence failures to the import screen. A failed TSV restoration reports the retained backup location.

## Acceptance criteria validation

- Failure injection reproduces the old partial-write risk with uncoordinated synced repositories, then verifies source restoration and simulated cache rollback with the coordinator.
- Successful replacement tests update all five source files; existing use-case tests verify all five categories and rating preservation.
- User explicitly approved this architecture-significant transaction design on 2026-10-15. ADR 0007 records the new boundary.
- README and BR-042a describe the behavior.

## How to test

### Automated tests

- JAVA_HOME=JDK21 ./gradlew test :app:lintVitalRelease :app:assembleDebug --no-daemon — passed.
- JAVA_HOME=JDK21 ./gradlew :infrastructure:test --no-daemon — passed after characterization test.
- Fresh signed local assembleRelease — passed after deleting the prior APK.
- apksigner verify: v1=true, v2=true, one signer. aapt: be.wacken.planner, versionCode 35, versionName 2.32, minSdk 23. SHA-256: 53b2f3b4f6d7251dd5d68d1ec15a2c888c160b300a2d10a393aa9966e0a4d948. git diff --check passed.

### Manual validation

- No device import was run. Android composition wiring compiled in both debug and release variants. Release 2.33 will rebuild and verify the final signed artifact.

## TDD / BDD / approval-test evidence

- The transaction test first failed to compile because the coordinator was absent, then passed after implementation. The use-case failure test likewise failed to compile until the transaction hook existed.
- The characterization test proves sequential source/cache writes can leave new bands and stages after an injected later cache failure.

## Architecture impact

- Architecture-significant change: yes, the five-file import now has a shared persistence boundary.
- Approval received: explicit user response on 2026-10-15 after the design and import-time failure scope were stated.
- ADR impact: updated ADR 0007 with Room rollback, TSV restoration, rating isolation, and crash-recovery limit.

## README impact

README impact: updated the Android five-file import transaction and recovery behavior.

## Business requirements impact

Business requirements impact: updated Workflow 1 and BR-042a with the failed-import guarantee.

## Diagram impact

Diagram impact: none, because module and runtime boundaries did not change.

## Commits / logical change list

- Application import transaction callback and error result; Android TSV fallback wiring; infrastructure TSV snapshot/restore coordinator; failure and success tests; ADR 0007, README, and business requirements.

## Risks and follow-up

- A process or power loss mid-import is not recovered by this import-time transaction design.
- If restoring TSV files also fails, the app reports the retained backup path for manual recovery; Room remains rolled back.
- Device-level Room/TSV fault injection was not available in the JVM suite. Release lintVital passed; full lintDebug has existing unrelated NewApi findings from the prior task baseline.
<!-- SECTION:NOTES:END -->

## Final Summary

<!-- SECTION:FINAL_SUMMARY:BEGIN -->
Android five-file import now restores TSV sources and rolls back Room on reported persistence failure; tests, ADR, README, and business requirements updated. Signed task-state APK verified.
<!-- SECTION:FINAL_SUMMARY:END -->

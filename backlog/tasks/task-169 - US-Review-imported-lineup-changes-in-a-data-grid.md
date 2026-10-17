---
id: task-169
title: 'US: Review imported lineup changes in a data grid'
status: Done
assignee:
  - '@codex'
created_date: '2026-08-19 11:02'
updated_date: '2026-10-17 18:27'
labels:
  - user-story
  - future
  - import
  - data-review
milestone: m-0
dependencies:
  - TASK-215
  - TASK-216
references:
  - importfestivalcsvusecase
  - festivalcsvreader
  - apprepositories
  - wackendatabase
modified_files:
  - >-
    application/src/test/java/be/wacken/planner/application/ReviewFestivalCsvUseCaseTest.java
  - >-
    application/src/main/java/be/wacken/planner/application/FestivalCsvReader.java
  - >-
    application/src/main/java/be/wacken/planner/application/ReviewFestivalCsvUseCase.java
  - app/src/main/java/be/wacken/planner/LocalCsvImportReviewDialog.java
  - app/src/main/java/be/wacken/planner/AppRepositories.java
  - app/src/main/java/be/wacken/planner/ImportCsvActivity.java
  - >-
    app/src/androidTest/java/be/wacken/planner/LocalCsvImportReviewAndroidTest.java
  - backlog/decisions/0019-reviewed-local-festival-csv-import.md
  - README.md
  - backlog/docs/business-requirements.md
  - backlog/decisions/0016-band-catalog-and-archive-promoted-personal-ratings.md
priority: low
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Business value: Festival data changes can be checked safely before they overwrite local planning data.

As a festival data maintainer, I want to review proposed imported changes row by row so that only valid changes are applied.

Scope: proposed CSV or future scraped changes, row-level validation status, accept/reject decisions, and applying only accepted rows.

Out of scope: Wacken scraping itself and fuzzy alias matching.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given an import proposes new, changed, or removed festival data, when the review grid opens, then each row shows what would change and whether it is valid.
- [x] #2 Given a user rejects a proposed row, when the import is applied, then that rejected change is not written.
- [x] #3 Given accepted changes are applied, when existing user ratings already exist, then user rating data is preserved.
- [x] #4 Relevant tests cover validation states, accept/reject behavior, and rating preservation.
- [x] #5 README, business requirements, diagram, and ADR impact are recorded using delivery-governance wording when implemented.
- [x] #6 Given accepted/rejected import rows under ADR-0016, then canonical metadata links, current personal band ratings, festival planning/after-ratings, history, source provenance and pending work remain preserved; imported master data cannot promote or replace user ratings.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Completed framework-free staged rows/diff/accept-reject via existing repos; strict review header/column/date/reference/duplicate validation; final accepted combined dependency/overlap checks and stale baseline guard. Existing TSV/Room transaction now applies active lineup plus local master data, preserving canonical catalog/nonempty metadata, other festivals and every opinion/history/alias/pending record. Native retained grid keeps choices/work across rotation; process recreation reselects files with no saved approvals. Added12 application scenarios and6 native UI/Room/TSV cases; full local65 and required API23/API36 CI65 each pass; full JVM/QA/coverage/lint and clean signed task APK pass. Updated README/BR contradictory non-goals, C4/workflow, accepted ADR0019/0016 link. Deviations: strict row-width and unambiguous distance identity added after failing tests; in-memory retained CSV avoids Binder size limits. Approved standard scope applies; no new service/backend schema/dependency. Existing food-name-only and reported-failure rollback limits documented.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Android local CSV import now stages a review grid before saving. Add/change/remove rows show before/after and validity; all changes start unchecked. Cancel/rejection keep baseline data. Unselected companion files retain data; header-only selected files propose explicit removals. Malformed/duplicate rows cannot be accepted or imply removals.

The framework-free application workflow validates the accepted combined references/overlaps and unchanged master-data baseline before writes. It retains canonical bands/nonempty metadata and music links, other festival data, uploaded display names and every opinion/history/provenance/alias/pending row. Lineup removal never deletes a catalog band. Saving uses the existing five-file TSV backup and Room transaction, including the active lineup.

Parsing/saving run on workers. In-memory retained state preserves the same staged review, decisions and work through rotation without retaining an Activity or serializing whole CSV files into saved-state bundles. Process recreation requires fresh selection/review.

## Acceptance criteria validation

1. Application/native cases cover new/change/remove/invalid rows, dates, numbers, headers, row widths, duplicate IDs/identities and candidate before/after.
2. Mixed-selection and cancel tests keep rejected removals/updates; only checked effective changes are applied.
3. Native SQL fingerprints prove all opinion/history/pending/source data unchanged.
4. Application tests and six new native tests cover decisions, invalid disabled rows, rotation, whole-transaction failure and preservation.
5. README/BR/Mermaid/ADR impacts below.
6. Native tests keep canonical links/metadata, aliases, current/planning/after/history/provenance/pending, other members and other festivals. Application has no rating repository or promotion operation.

## How to test

### Automated tests

- JDK21 ./gradlew test qaTest :app:lintDebug :app:lintVitalRelease: passed, including domain/application coverage gates and existing legacy import/TSV characterization.
- Application review tests cover accepted subsets, missing companions, malformed rows, missing references/overlap, stale baseline, duplicate identities and protected metadata.
- sh scripts/verify-android-smoke.sh emulator-5554 be.wacken.planner.LocalCsvImportReviewAndroidTest:6 passed, zero skipped.
- Full local native65:passed, zero failures/skips. Real Room/TSV tests inject a final lineup-write fault and verify all five files, cache, ratings and pending data roll back unchanged.
- Existing real creation16 and rating/archive/correction/deletion/alias PostgreSQL/PostgREST gates remain mandatory CI checks.
- Full Android lint now passes with no NewApi errors under completed TASK223.
- git diff --check passed. Final CI/signing evidence follows below.

### Manual validation

No human acceptance claimed. Native automation exercises the actual Import screen/dialog and real local repositories; no shared production data is written. Suggested review: choose local CSV files, inspect before/after and invalid rows, accept one addition while rejecting removal, rotate, apply, then check lineup and ratings. Cancel saves nothing. A failed combined selection keeps the dialog for correction; Reload review resets approvals after stale data.

## TDD / BDD / approval-test evidence

Initial application tests failed for the missing review use case. Additional regression tests proved extra CSV cells were silently accepted and stage names containing arrow separators could collapse distinct unchanged distances; both went red and then green after strict review parsing and unambiguous tuple identity. Existing five-file parser/import/rollback tests characterize retained legacy contracts. Native cases verify checkbox decisions, cancellation, disabled invalid rows, same retained review across rotation, unchanged opinion fingerprints and final-write rollback.

## Architecture impact

- Architecture-significant scope: the explicitly approved standard remaining2.34 local review workflow under TASK215/ADR0016.
- Approval received: user's instruction to apply the approved canonical architecture to all stories; no repeated approval required.
- ADR impact: created0019-reviewed-local-festival-csv-import.md and updated0016 implementation link.
- Existing repository and transaction ports remain. No backend API/schema, service, credential, framework or dependency is added.

## README impact

README impact: updated local review/decision/error/retry/rotation semantics, canonical preservation, local-vs-shared behavior and existing storage constraints.

## Business requirements impact

Business requirements impact: updated BR034/042, capability and conflicting historical non-goals to reflect the approved local review scope; scraping/hosted import remain out of scope.

## Diagram impact

Diagram impact: updated C4 application responsibilities and Mermaid preview/accept/validate/transaction workflow.

## Commits / logical change list

28efbcd: framework-free staged review and strict CSV helpers; native retained grid; local repository composition/lineup transaction; application/native regression suite; accepted ADR0019/0016 link and README/BR.

## Risks and follow-up

This import is local TSV/Room; normal shared sync may refresh master data. It does not create shared source records or seed/promote user ratings. Legacy storage uses canonical names and band/start performance keys, so identity changes appear as remove/add. Food near-stage references are validated but the existing food model persists names only. The existing rollback guarantee handles reported failures, not power loss/process interruption. A process restart discards approvals and requires fresh review.

Final release214 still needs contextual detail/export219, integrated lifecycle/upgrade220 and DB-before-APK rollout. This task APK is validation-only2.33.5/code41 and must not be published as completed2.34.

Final evidence: https://github.com/DinoDeWaen/wacken/actions/runs/39057773054 at28efbcd is fully green: full native65 on API23 and API36, JVM/QA/coverage/full lint and actual PostgreSQL/PostgREST/creation/build gates. Local native65 passed. Clean signed build including full lint/lintVital passed. APK SHA256 cdc3838b6450f9dea8b1633a3156869113971696092f4edc611f7124b2ff6a13; stable certificate SHA256 ede2ae97735a305900e87bc0fdc021c03382db4e30435f2349a96a3fdcc7545f. No production migration/data write or2.34 publication.
<!-- SECTION:NOTES:END -->

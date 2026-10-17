---
id: task-169
title: 'US: Review imported lineup changes in a data grid'
status: In Progress
assignee:
  - '@codex'
created_date: '2026-08-19 11:02'
updated_date: '2026-10-17 18:22'
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
- [ ] #1 Given an import proposes new, changed, or removed festival data, when the review grid opens, then each row shows what would change and whether it is valid.
- [ ] #2 Given a user rejects a proposed row, when the import is applied, then that rejected change is not written.
- [ ] #3 Given accepted changes are applied, when existing user ratings already exist, then user rating data is preserved.
- [ ] #4 Relevant tests cover validation states, accept/reject behavior, and rating preservation.
- [ ] #5 README, business requirements, diagram, and ADR impact are recorded using delivery-governance wording when implemented.
- [ ] #6 Given accepted/rejected import rows under ADR-0016, then canonical metadata links, current personal band ratings, festival planning/after-ratings, history, source provenance and pending work remain preserved; imported master data cannot promote or replace user ratings.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Standard accepted remaining2.34 architecture/TASK215/ADR0016. Characterize the existing five-file local CSV parser/import and TSV/Room rollback. TDD framework-free staged rows/diff/accept-reject application workflow: new/change/remove, per-row errors, decisions default unaccepted, malformed files cannot cause inferred removals, missing companion files retain baseline, final combined reference/overlap validation, stale baseline refusal. Preserve canonical catalog and all opinions/history/provenance/pending; lineup removal never deletes a central band. Use existing local TSV/Room transaction and active-festival performance target, update only the active lineup, no remote import API/new persistence/service/dependency. Native review grid with before/after, validity and explicit accept controls, background parse/apply and rotation-retained decisions. Validate mixed selections, invalid/dependent rows, rollback, rating/metadata preservation, native flow/rotation on API23/36, full JVM/QA/coverage/lint and fresh signed task APK. Update README/BR conflicting old non-goals, Mermaid workflow and accepted ADR implementation link. Approval already explicitly covers this standard port/adapter scope; no repeated approval required. Natural persisted identities are canonical band/stage names and band+start performance keys because legacy TSV/Room do not retain CSV IDs.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
2026-10-17: The current CSV import directly replaces validated local snapshots; no review grid exists. Proposed row acceptance/rejection preserves baseline data and ratings using the existing local import transaction. See backlog/docs/release-2.34-remaining-architecture-proposal.md and TASK-215 before implementation.

2026-10-17 user-approved architecture: ADR-0016 governs canonical shared bands, member-specific established band ratings, independent festival planning/after-ratings, one-time creation snapshots and atomic archive promotion. Missing after-ratings retain the prior band rating; archived corrections update it only while their festival remains the source. TASK-216–220 implement and validate the lifecycle. This supersedes earlier latest-event prefill wording; do not treat approved planning as shipped behavior.

Implemented staged application review and native local grid using existing repository/TSV/Room boundaries. New/change/remove/invalid rows start unchecked; unselected companion files retain baseline. Final combined references/overlaps and baseline check gate all writes. Catalog/nonempty metadata, other festivals, all opinions/history/provenance/aliases/pending stay preserved. Native6 import tests passed, including actual UI choices/cancel, invalid disablement, rotation-retained state, final-lineup-fault restoring all five TSV files and Room, and catalog/opinion retention on lineup removal. Application tests were red for missing use case, silently accepted extra CSV cells and distance-key collision, then passed after fixes. Full JVM/QA/coverage/lint and native65 validation are running. README/BR non-goals reconciled with approved169 scope and accepted ADR0019 added. No new backend/schema/dependency. Not Done until final native/CI and fresh signed build evidence.
<!-- SECTION:NOTES:END -->

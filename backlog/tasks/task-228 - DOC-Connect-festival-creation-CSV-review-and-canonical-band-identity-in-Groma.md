---
id: TASK-228
title: >-
  DOC: Connect festival creation, CSV review and canonical band identity in
  Groma
status: Done
assignee:
  - '@codex'
created_date: '2026-10-20 06:25'
updated_date: '2026-10-21 12:22'
labels:
  - groma-audit
  - release-2.35
milestone: m-3
dependencies:
  - TASK-227
references:
  - wacken
  - canonicalband
  - roombandaliasrepository
  - roomfestivalcachemarker
  - addfestivalaction
  - festivalbandcatalog
  - festivalbandreviewdialog
  - festivalcreationgateway
  - reviewfestivalbandsusecase
  - supabasefestivalbandcatalog
  - supabasefestivalcreationgateway
  - roomfestivalcreationcache
  - reviewfestivalcsvusecase
  - localcsvimportreviewdialog
  - tsvimporttransaction
  - festivalcsvreader
  - deletefestivaldialog
  - deletefestivalusecase
  - supabasefestivaldeletiongateway
  - roomfestivaldeletion
  - festivalscopedperformancerepository
  - importcsvactivity
  - apprepositories
  - supabasemasterdataclient
  - addfestivalusecase
  - wackendatabase
  - syncedbandrepository
  - filebackedstorage
  - mainactivity
  - archivedfestivalactivity
  - importfestivalcsvusecase
documentation:
  - backlog/docs/groma-audit-2026-10-20.md
modified_files:
  - >-
    groma/systems/wacken/containers/android-application/components/canonicalband.md
  - >-
    groma/systems/wacken/containers/android-application/components/confirmedbandalias.md
  - >-
    groma/systems/wacken/containers/android-application/components/reviewedfestivalband.md
  - >-
    backlog/tasks/task-227 -
    DOC-Restore-truthful-runtime-and-data-boundaries-in-the-architecture-map.md
  - >-
    groma/systems/wacken/containers/android-application/components/roombandaliasrepository.md
  - >-
    groma/systems/wacken/containers/android-application/components/roombandalias.md
  - >-
    groma/systems/wacken/containers/android-application/components/roombandaliasdao.md
  - >-
    groma/systems/wacken/containers/android-application/components/roomfestivalcachemarker.md
  - >-
    groma/systems/wacken/containers/android-application/components/roomfestivalcachemarkerdao.md
  - >-
    groma/systems/wacken/containers/android-application/components/addfestivalaction.md
  - >-
    groma/systems/wacken/containers/android-application/components/festivalbandcatalog.md
  - >-
    groma/systems/wacken/containers/android-application/components/festivalbandreviewdialog.md
  - >-
    groma/systems/wacken/containers/android-application/components/festivalcreationgateway.md
  - >-
    groma/systems/wacken/containers/android-application/components/reviewfestivalbandsusecase.md
  - >-
    groma/systems/wacken/containers/android-application/components/supabasefestivalbandcatalog.md
  - >-
    groma/systems/wacken/containers/android-application/components/supabasefestivalcreationgateway.md
  - >-
    groma/systems/wacken/containers/android-application/components/roomfestivalcreationcache.md
  - >-
    groma/systems/wacken/containers/android-application/components/reviewfestivalcsvusecase.md
  - >-
    groma/systems/wacken/containers/android-application/components/localcsvimportreviewdialog.md
  - >-
    groma/systems/wacken/containers/android-application/components/tsvimporttransaction.md
  - >-
    groma/systems/wacken/containers/android-application/components/festivalcsvreader.md
  - >-
    groma/systems/wacken/containers/android-application/components/deletefestivaldialog.md
  - >-
    groma/systems/wacken/containers/android-application/components/deletefestivalusecase.md
  - >-
    groma/systems/wacken/containers/android-application/components/supabasefestivaldeletiongateway.md
  - >-
    groma/systems/wacken/containers/android-application/components/roomfestivaldeletion.md
  - >-
    groma/systems/wacken/containers/android-application/components/festivalscopedperformancerepository.md
  - >-
    groma/systems/wacken/containers/android-application/components/importcsvactivity.md
  - >-
    groma/systems/wacken/containers/android-application/components/apprepositories.md
  - >-
    groma/systems/wacken/containers/android-application/components/addfestivalusecase.md
  - >-
    groma/systems/wacken/containers/android-application/components/syncedbandrepository.md
  - >-
    groma/systems/wacken/containers/android-application/components/mainactivity.md
  - >-
    groma/systems/wacken/containers/android-application/components/archivedfestivalactivity.md
  - >-
    groma/systems/wacken/containers/android-application/components/wackendatabase.md
  - backlog/docs/task-228-stale-relationship-approval.md
  - groma/flows/create-reviewed-group-festival.md
  - groma/flows/import-festival-csv.md
  - groma/flows/delete-selected-group-festival.md
  - >-
    groma/systems/wacken/containers/android-application/components/importfestivalcsvusecase.md
  - backlog/docs/groma-festival-administration-evidence-2026-10-21.md
  - README.md
  - >-
    infrastructure/src/test/java/be/wacken/planner/infrastructure/TsvImportTransactionTest.java
  - >-
    application/src/test/java/be/wacken/planner/application/ReviewFestivalCsvUseCaseTest.java
  - >-
    application/src/main/java/be/wacken/planner/application/ImportFestivalCsvUseCase.java
  - >-
    application/src/test/java/be/wacken/planner/application/ImportFestivalCsvUseCaseTest.java
  - app/src/main/java/be/wacken/planner/ImportCsvActivity.java
priority: high
type: docs
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## User story
As a maintainer, I want the festival import and creation paths connected so I can trace how a CSV becomes a shared festival without losing reusable band metadata.

## Scope
Curate the remaining responsibilities, ownership, descriptions, groups and relationships for add/create gateways and caches, canonical bands and aliases, band/CSV review, local TSV/Room import transaction, festival-scoped performance access, deletion and cache markers. Cover these Appendix A records; coordinate shared ownership with the rating-map task. Describe existing behaviour only.

## Audit reference
[Groma architecture audit — 20 October 2026](../docs/groma-audit-2026-10-20.md)
Repository path: backlog/docs/groma-audit-2026-10-20.md
Findings: F3 and F10; Appendix A creation/import/alias/deletion records. Investigation: TASK-225.

## Boundaries and impacts
Preserve ADR-0016: shared canonical metadata, member-specific established band rating, independent festival planning/after-ratings, one-time creation snapshots and source-aware archive promotion/correction. Follow Groma CLI-only curation and Backlog CLI-only task maintenance.
Business requirements impact: none planned unless an approved behaviour change is explicitly required; record the actual impact on completion.
README and diagram impacts are assessed for the affected workflow. ADR impact: none assumed; approval is required for significant changes.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Every disconnected Appendix A record in this scope is explicitly assigned to a truthful component or combined through the CLI with a responsibility-based explanation; no arbitrary arrow is added to hide an island.
- [x] #2 Given reviewed group-wide creation, a flow traces screen, identity review, creation use case/gateway, backend result and cache update, including one-time per-member planning seed and empty after-ratings.
- [x] #3 Given local five-file review or festival deletion, the map distinguishes local review/transaction behaviour from group-wide creation/deletion and documents preservation/failure outcomes.
- [x] #4 Authored collaborations and important port/adapter/callback wiring are reviewed from source. Exception accepted by the user on 21 October 2026: two stale authored arrows involving the deleted ImportFestivalCsvUseCase remain documented because the pinned CLI cannot remove current rows; this does not block task closure or authorize new tooling changes.
- [x] #5 Ownership/source links and rendered scenarios are verified after two scans; README architecture links and diagram impacts are updated.
- [x] #6 Architecture impact is assessed before implementation; significant boundary, dependency, persistence or test-strategy changes receive explicit human approval before coding and an ADR is recorded when required.
- [x] #7 README, business requirements, diagram and ADR impacts use canonical delivery-governance wording; test evidence, manual verification, finalized plan and accepted limitations are recorded before closure.
- [x] #8 Remove the unused legacy full CSV importer and unused screen import, retain relevant coverage on the active reviewed workflow, and record the Groma rescan result.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Reviewed creation, canonical identity/alias, local five-file review, transaction and deletion code against requirements and ADR0016; accounted for all25 scoped AppendixA records under20 responsibilities. 2. Consolidated five implementation-detail records into justified owners through CLI; described/grouped remaining responsibilities and added46 reviewed source/composition collaborations. 3. Added group creation/deletion scenarios and corrected local-review scenario, preserving SQL coverage limits, once-only seeds, empty after-state and failure/preservation semantics. 4. Characterized active reviewed import before removing unused ImportFestivalCsvUseCase, obsolete tests and unused screen import; preserved shared values and retained initial-list API. Detached deleted ownership and updated remaining responsibility through CLI. 5. Verified unique existing source ownership, rendered paths/flows and stable rescans; tests/coverage/lint and fresh stable-key signed APK pass. Following TASK229/230 current map has95 components,294 source files,13 flows and zero disconnected components. 6. User explicitly accepted the known two stale authored arrows and instructed closure on21 October2026. Updated AC4 and closure wording to record that exception, finalized evidence/README and deferred the unapproved CLI extension. Minimal curation/dead-code cleanup changes no runtime boundary or behavior; no additional architecture approval/ADR required.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Festival creation, canonical identity/aliases, local five-file acceptance/rollback and deletion are documented with source-backed composition and scenario flows. All25 scoped audit records have explained ownership under20 responsibility records. Removed the unused full CSV importer and obsolete tests; relevant characterization coverage now exercises the active reviewed workflow. Evidence: backlog/docs/groma-festival-administration-evidence-2026-10-21.md.

## Acceptance criteria validation

All criteria satisfied under the explicit user-accepted exception in AC4. User reviewed the corrected map, then instructed that TASK228 may close and work continue. Two historical authored rows remain because the pinned CLI refuses current-row removal: ImportCsvActivity.java to deleted ImportFestivalCsvUseCase.java; deleted ImportFestivalCsvUseCase.java to Band.java. No current flow uses them. This accepts closure with the limitation; it does not claim those relationships are correct or approve the deferred tooling extension.

## How to test

### Automated tests

Cleanup regression evidence: application163, domain63, infrastructure24 and BDD3 pass. Android264 tests have248 passed and16 pre-existing disposable-API skips; application/domain coverage gates and Android lint pass. These same unchanged sources were rechecked in TASK229/230. Final closure rebuild deletes the previous release APK and executes all53 assembleRelease tasks successfully; apksigner verifies stable-key signing. APK SHA256 7884295495227152bf2c6cf3a6adfcb0f3951c5c695e2281f733965ff128ae4f. Existing version retained; no2.35 publication.

### Manual validation

Reviewed exact calls, supplied callbacks and adapter dispatch from source, including creation response/cache order and deletion preservation. Two cleanup rescans create0/refreshed106/findings88; after TASK229/230 curation current model has95 components,294 existing unique source files,13 resolvable flows and zero disconnected components. CLI lookup of removed source is unknown target. Exported map and exact source links were inspected; user accepted the corrected map. Whitespace diff check passes. SQL/RLS behavior is supported by source review, not Java scanner inference or a new live authorization test.

## TDD / BDD / approval-test evidence

Active reviewed-import quoted metadata and partial-write characterization pass before legacy removal and remain green afterward. Existing application/infrastructure/BDD tests cover unchanged validation, preservation and rollback behavior. No intentional runtime behavior change.

## Architecture impact

Architecture-significant change: none in curation and unused-code removal; existing boundaries, persistence contracts and test strategy remain. Approval received: explicit user authorization for cleanup, corrected map review and closure accepting the two documented stale arrows. ADR impact: none, because no new architectural decision was implemented. The proposed tooling extension is deferred and unapproved.

## README impact

README impact: updated workflow evidence links, cleanup outcome and explicit accepted tooling limitation at closure.

## Business requirements impact

Business requirements impact: none, because active reviewed import, creation, identity and deletion behavior are preserved.

## Diagram impact

Diagram impact: updated Groma ownership, roles/groups, source-backed collaborations and creation/local-review/deletion flows through CLI. Two known stale authored rows remain an explicit accepted exception; no hand-edited architecture.

## Commits / logical change list

3f303e8 curated festival administration map;70464da removed unused importer and verified rescan behavior; closure documentation records the user decision and final evidence.

## Risks and follow-up

Accepted limitation: precisely the two stale deleted-importer arrows above. Scanner warnings/duplicate findings remain analysis limitations. TASK233 must distinguish this specific accepted exception from new unexplained dangling endpoints; no blanket ignore is authorized. TASK241 retains runtime/upgrade gates. No merge or publication.
<!-- SECTION:NOTES:END -->

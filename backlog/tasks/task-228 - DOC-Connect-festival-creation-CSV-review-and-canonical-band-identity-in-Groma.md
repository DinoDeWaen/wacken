---
id: TASK-228
title: >-
  DOC: Connect festival creation, CSV review and canonical band identity in
  Groma
status: In Progress
assignee:
  - '@codex'
created_date: '2026-10-20 06:25'
updated_date: '2026-10-21 11:20'
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
- [ ] #4 Authored links cite actual source interactions; important port/adapter and callback wiring is reviewed from code rather than inferred solely from imports.
- [ ] #5 Ownership/source links and rendered scenarios are verified after two scans; README architecture links and diagram impacts are updated.
- [x] #6 Architecture impact is assessed before implementation; significant boundary, dependency, persistence or test-strategy changes receive explicit human approval before coding and an ADR is recorded when required.
- [ ] #7 README, business requirements, diagram and ADR impacts use canonical delivery-governance wording; test evidence, manual verification, finalized plan and remaining risks are recorded before merge.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Inspect creation/review/local-import/deletion code and relevant requirements; inventory every creation/import/alias/deletion AppendixA record and existing map ownership.
2. Keep independently meaningful responsibilities; group related siblings and consolidate only source-only persistence implementation details where one responsibility warrants it, before prose. No app/runtime/schema/dependency/test-strategy change; documentation-only minimal architecture treatment, no new approval/ADR required.
3. Describe responsibilities, supported exact source collaborations and composition-supplied callbacks; add reviewed creation, local five-file review and deletion flows with failure/preservation semantics and explicit Java-only backend gaps.
4. Record a complete scope/evidence disposition, update README map links, verify ownership/rendered source/flows and two scans, build fresh signed APK, finalize validation package and commit before next task.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Source review reproduced a stale current map edge: ImportCsvActivity only imports legacy ImportFestivalCsvUseCase and instead executes ReviewFestivalCsvUseCase through LocalCsvImportReviewDialog. Existing flow will be corrected via CLI; current authored-edge deletion remains refused by Groma. Concrete narrow tooling-extension proposal recorded; approval required before additional tool coding. Independent flow/evidence work continues.

Curation checkpoint: all25 scoped audit records accounted for under20 explained responsibilities; three CLI combines retain295 unique source files under106 components. Updated TASK227 references for five replaced IDs. Added46 code/composition-supported collaborations with explicit callback vs direct/interface technology. Added reviewed group creation and selected-festival deletion flows; corrected existing import-festival-csv flow to local reviewed five-file workflow. Two scans create0/refreshed106/findings88; five flows resolve. Exported map renders attendee3/admin2 flows; source ownership/path validation and diff check pass. Evidence report and README link added. Known stale ImportCsvActivity-to-legacy ImportFestivalCsvUseCase arrow remains: after correcting its flow, CLI removal still refuses current authored rows. Additional tooling proposal is awaiting explicit approval. AC4/5/7 remain open; no Done, final signed-build claim, merge or publication.
<!-- SECTION:NOTES:END -->

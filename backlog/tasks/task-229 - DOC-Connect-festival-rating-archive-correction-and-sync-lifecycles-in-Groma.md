---
id: TASK-229
title: 'DOC: Connect festival rating, archive, correction and sync lifecycles in Groma'
status: Done
assignee:
  - '@codex'
created_date: '2026-10-20 06:25'
updated_date: '2026-10-21 12:06'
labels:
  - groma-audit
  - release-2.35
milestone: m-3
dependencies:
  - TASK-227
references:
  - wacken
  - festivalafterratingrepository
  - personalbandratingrepository
  - archivedratingcorrectiongateway
  - viewbandratingcontextusecase
  - ratingcorrectionrepository
  - roomfestivalafterratingrepository
  - roompersonalbandratingrepository
  - roomratingcorrectionrepository
  - activefestivalafterratingrepository
  - festivalplanningratingview
  - festivalafterratingview
  - recordfestivalafterratingusecase
  - ratingstatesync
  - ratingstatesyncstore
  - roomratingstatedao
  - supabaseratingstateclient
  - archivefestivalaction
  - festivalarchivegateway
  - supabasefestivalarchivegateway
  - roomfestivalarchivecache
  - archivedratingcorrectiondialog
  - archivedratingcorrectionusecase
  - supabasearchivedratingcorrectiongateway
  - roomarchivedratingcorrectioncache
  - recoverpendingplanningratingusecase
  - missingfestivalplanningratingexception
  - missingfestivalratingdialog
  - exportratinglifecyclecsvusecase
  - exportratingcorrectionscsvusecase
documentation:
  - backlog/docs/groma-audit-2026-10-20.md
modified_files:
  - >-
    groma/systems/wacken/containers/android-application/components/festivalafterratingrepository.md
  - >-
    groma/systems/wacken/containers/android-application/components/festivalafterrating.md
  - >-
    groma/systems/wacken/containers/android-application/components/personalbandratingrepository.md
  - >-
    groma/systems/wacken/containers/android-application/components/personalbandrating.md
  - >-
    groma/systems/wacken/containers/android-application/components/archivedratingcorrectiongateway.md
  - >-
    groma/systems/wacken/containers/android-application/components/archivedratingkind.md
  - >-
    groma/systems/wacken/containers/android-application/components/archivedratingreview.md
  - >-
    groma/systems/wacken/containers/android-application/components/viewbandratingcontextusecase.md
  - >-
    groma/systems/wacken/containers/android-application/components/bandratingcontext.md
  - >-
    groma/systems/wacken/containers/android-application/components/ratingcorrectionrepository.md
  - >-
    groma/systems/wacken/containers/android-application/components/ratingcorrection.md
  - >-
    groma/systems/wacken/containers/android-application/components/roomfestivalafterratingrepository.md
  - >-
    groma/systems/wacken/containers/android-application/components/roomfestivalafterrating.md
  - >-
    groma/systems/wacken/containers/android-application/components/roompersonalbandratingrepository.md
  - >-
    groma/systems/wacken/containers/android-application/components/roompersonalbandrating.md
  - >-
    groma/systems/wacken/containers/android-application/components/roomratingcorrectionrepository.md
  - >-
    groma/systems/wacken/containers/android-application/components/roomratingcorrection.md
  - >-
    groma/systems/wacken/containers/android-application/components/roomratingcorrectiondao.md
  - >-
    groma/systems/wacken/containers/android-application/components/activefestivalafterratingrepository.md
  - >-
    groma/systems/wacken/containers/android-application/components/festivalplanningratingview.md
  - >-
    groma/systems/wacken/containers/android-application/components/festivalafterratingview.md
  - >-
    groma/systems/wacken/containers/android-application/components/recordfestivalafterratingusecase.md
  - >-
    groma/systems/wacken/containers/android-application/components/ratingstatesync.md
  - >-
    groma/systems/wacken/containers/android-application/components/ratingstatesyncstore.md
  - >-
    groma/systems/wacken/containers/android-application/components/roomratingstatedao.md
  - >-
    groma/systems/wacken/containers/android-application/components/supabaseratingstateclient.md
  - >-
    groma/systems/wacken/containers/android-application/components/archivefestivalaction.md
  - >-
    groma/systems/wacken/containers/android-application/components/festivalarchivegateway.md
  - >-
    groma/systems/wacken/containers/android-application/components/supabasefestivalarchivegateway.md
  - >-
    groma/systems/wacken/containers/android-application/components/roomfestivalarchivecache.md
  - >-
    groma/systems/wacken/containers/android-application/components/archivedratingcorrectiondialog.md
  - >-
    groma/systems/wacken/containers/android-application/components/archivedratingcorrectionusecase.md
  - >-
    groma/systems/wacken/containers/android-application/components/supabasearchivedratingcorrectiongateway.md
  - >-
    groma/systems/wacken/containers/android-application/components/roomarchivedratingcorrectioncache.md
  - >-
    groma/systems/wacken/containers/android-application/components/recoverpendingplanningratingusecase.md
  - >-
    groma/systems/wacken/containers/android-application/components/missingfestivalplanningratingexception.md
  - >-
    groma/systems/wacken/containers/android-application/components/missingfestivalratingdialog.md
  - >-
    groma/systems/wacken/containers/android-application/components/exportratinglifecyclecsvusecase.md
  - >-
    groma/systems/wacken/containers/android-application/components/exportratingcorrectionscsvusecase.md
  - >-
    groma/systems/wacken/containers/android-application/components/banddetailactivity.md
  - >-
    groma/systems/wacken/containers/android-application/components/ratebandusecase.md
  - >-
    groma/systems/wacken/containers/android-application/components/apprepositories.md
  - >-
    groma/systems/wacken/containers/android-application/components/archivedfestivalactivity.md
  - >-
    groma/systems/wacken/containers/android-application/components/wackendatabase.md
  - >-
    groma/systems/wacken/containers/android-application/components/mainactivity.md
  - >-
    groma/systems/wacken/containers/android-application/components/addfestivalusecase.md
  - >-
    groma/systems/wacken/containers/android-application/components/settingsactivity.md
  - >-
    groma/systems/wacken/containers/android-application/components/syncingratingrepository.md
  - >-
    groma/systems/wacken/containers/android-application/components/reviewfestivalcsvusecase.md
  - groma/actors/festival-attendee.md
  - groma/flows/edit-selected-festival-planning.md
  - groma/flows/record-and-sync-festival-after-rating.md
  - groma/flows/archive-and-establish-personal-opinions.md
  - groma/flows/correct-own-archived-assessment.md
  - groma/flows/review-orphaned-pending-planning.md
  - groma/flows/export-own-lifecycle-and-correction-audit.md
  - backlog/docs/groma-rating-islands-evidence-2026-10-21.md
  - README.md
priority: high
type: docs
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## User story
As a maintainer, I want rating lifecycles connected so I can verify which values belong to a band, a member and a festival.

## Scope
Curate the remaining rating/context models and ports, Room entities/DAOs/adapters, rating state sync, after-rating recording, archive gateway/cache, correction/review/audit, pending recovery and rating exports. Account for shared composition and cache wiring with the festival-map task. Do not change ADR-0016.

## Audit reference
[Groma architecture audit — 20 October 2026](../docs/groma-audit-2026-10-20.md)
Repository path: backlog/docs/groma-audit-2026-10-20.md
Findings: F3 and F10; Appendix A rating/context/archive/sync/export records. Investigation: TASK-225.

## Boundaries and impacts
Preserve ADR-0016: shared canonical metadata, member-specific established band rating, independent festival planning/after-ratings, one-time creation snapshots and source-aware archive promotion/correction. Follow Groma CLI-only curation and Backlog CLI-only task maintenance.
Business requirements impact: none planned unless an approved behaviour change is explicitly required; record the actual impact on completion.
README and diagram impacts are assessed for the affected workflow. ADR impact: none assumed; approval is required for significant changes.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Every Appendix A rating/archive/correction/sync/export island receives a responsibility, meaningful ownership and supported relationships or is combined with a justified owner; source-free placeholders are handled by the separate cleanup task.
- [x] #2 Flows trace festival-bound planning and after-rating edits, durable reset and offline/pending preservation, with established rating updates only through the existing archive/correction rules.
- [x] #3 Archive and source-aware correction flows distinguish missing versus reset after-ratings, newer-source protection, stale review and member ownership; original event dates and correction times remain distinct.
- [x] #4 Context and legacy/lifecycle/audit exports are described as separate contracts; map prose does not suggest cross-festival fallback or latest-event promotion.
- [x] #5 Two scans preserve IDs, groups, ownership and flows; rendered source links and affected README architecture documentation are verified.
- [x] #6 Architecture impact is assessed before implementation; significant boundary, dependency, persistence or test-strategy changes receive explicit human approval before coding and an ADR is recorded when required.
- [x] #7 README, business requirements, diagram and ADR impacts use canonical delivery-governance wording; test evidence, manual verification, finalized plan and remaining risks are recorded before merge.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Reviewed all screenshot rating/archive/correction/sync/export sources, callers, composition, ADR0016 and V014/V016/V017; all scoped runtime responsibilities are used. 2. Combined10 protocol/value/row/DAO records into8 justified owners through CLI, described29 surviving responsibilities in4 meaningful groups; retained shared query responsibility and all294 sources. 3. Added reviewed source/composition collaborations and6 scenarios covering selected planning, after-state/reset sync, archive promotion, own revision-checked correction, explicit pending recovery and separate exports; also connected shared CSV values and canonical Band. 4. Verified96 components,294 unique existing sources,11 resolvable flows and two scans creating0/refreshed96/findings88; exported Chromium map renders groups/scenarios and exact AppRepositories source pane. Regression/coverage/lint pass; deleted prior APK, rebuilt all53 release tasks and verified stable signing. Updated README/evidence and finalized notes. Minimal documentation-only treatment; no runtime behavior, boundary, schema, dependency or test-strategy change; no additional architecture approval or ADR required. Deviation: responsibility consolidation and missing source interactions also resolved two adjacent apparent islands; six music islands remain for TASK230, while stale deleted-source arrows remain TASK228.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Resolved all scoped rating/archive/correction/sync/export islands. Source review confirms these are used runtime responsibilities. Ten file-shaped records were combined through CLI into eight responsibility owners; 29 surviving roles now explain lifecycle scope and composition. Added six scenarios. Evidence: backlog/docs/groma-rating-islands-evidence-2026-10-21.md.

## Acceptance criteria validation

All seven criteria satisfied. Selected festival planning and after-state/reset, owner-bound sync, archive nonempty promotion, current-source correction and immutable original/correction dates are explicit. Context never falls back across festivals. Legacy, lifecycle v2 and audit CSV remain distinct. Every scoped island is connected or consolidated with rationale; six music records belong to TASK230.

## How to test

### Automated tests

Gradle application163, domain63, infrastructure24, BDD3 and Android264 (248 passed,16 pre-existing disposable-API skips) pass; application/domain coverage gates and Android lint pass. Unchanged production sources reuse passing regression results. Fresh assembleRelease --rerun-tasks executes53 tasks successfully; apksigner verifies stable-key APK. SHA256 194de8bd2436abfc7d9bf904d179ee5b20af48ff358c26994e4957ee99fd3266. No release version bump/publication.

### Manual validation

Source and composition checked for ordinary calls, generated Room queries, explicit Supplier/gateway/request/cache callbacks and composed port dispatch.294 unique owned source files all exist. Two scans create0/refreshed96/findings88; all11 flows load. Static export renders in Chromium with96 components and attendee9/admin2 scenarios; exact AppRepositories source opens, showing composition code. Diff whitespace check passes. SQL descriptions checked against V014/V016/V017; no live database authorization claim.

## TDD / BDD / approval-test evidence

Documentation-only curation preserves runtime behavior; existing BDD and regression tests characterize lifecycle contracts. No new behavior or test strategy was introduced.

## Architecture impact

Architecture-significant change: none; map-only consolidation and existing collaboration descriptions. Approval received: user requested investigation and correction of isolated records. ADR impact: none, because existing ADR0016 boundaries/contracts are preserved.

## README impact

README impact: updated link and explanation of the reviewed rating map.

## Business requirements impact

Business requirements impact: none, because lifecycle behavior and product scope are unchanged.

## Diagram impact

Diagram impact: updated Groma ownership, groups, collaborations and six lifecycle flows through CLI; existing Mermaid boundary diagrams remain accurate.

## Commits / logical change list

One task-sized documentation commit covers reviewed ownership, collaborations, scenarios and evidence.

## Risks and follow-up

Java scanner findings remain evidence limitations; TASK241 retains runtime/upgrade gates. TASK230 owns six remaining music islands. TASK228 still records stale authored arrows to deleted importer; this task does not alter pinned tooling or claim that blocker solved. No merge or publication.
<!-- SECTION:NOTES:END -->

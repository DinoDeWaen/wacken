---
id: TASK-231
title: 'DOC: Resolve empty and obsolete Groma component records'
status: Done
assignee:
  - '@codex'
created_date: '2026-10-20 06:25'
updated_date: '2026-10-20 19:37'
labels:
  - groma-audit
  - release-2.35
milestone: m-3
dependencies:
  - TASK-226
references:
  - wacken
documentation:
  - backlog/docs/groma-audit-2026-10-20.md
modified_files:
  - groma/systems/wacken/components/pendingplanningratingrecovery.md
  - groma/systems/wacken/components/androidjavacompatibility.md
  - backlog/docs/groma-empty-record-dispositions-2026-10-20.md
priority: medium
type: docs
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## User story
As a maintainer, I want source-free placeholders resolved so the architecture does not imply nonexistent implementation.

## Scope
Investigate pendingplanningratingrecovery and androidjavacompatibility, including task references and flows. Retain/rehome meaningful knowledge if supported; otherwise remove obsolete records through Groma. Do not merge the old recovery ID with the current use case by name alone.

## Audit reference
[Groma architecture audit — 20 October 2026](../docs/groma-audit-2026-10-20.md)
Repository path: backlog/docs/groma-audit-2026-10-20.md
Findings: F4. Investigation: TASK-225.

## Boundaries and impacts
Preserve ADR-0016: shared canonical metadata, member-specific established band rating, independent festival planning/after-ratings, one-time creation snapshots and source-aware archive promotion/correction. Follow Groma CLI-only curation and Backlog CLI-only task maintenance.
Business requirements impact: none planned unless an approved behaviour change is explicitly required; record the actual impact on completion.
README and diagram impacts are assessed for the affected workflow. ADR impact: none assumed; approval is required for significant changes.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Each empty record has a recorded evidence-based disposition: retained with a justified supported responsibility, or removed through the CLI when obsolete.
- [x] #2 Removal or replacement preserves useful knowledge and updates all affected Backlog architecture references through the CLI; unresolved historical origin is recorded honestly.
- [x] #3 Rescans do not recreate obsolete components and all task references, relationship endpoints and flow steps resolve after the cleanup.
- [x] #4 README impact: none, because cleanup does not change runtime behaviour, unless the implemented map explanation requires a documented update.
- [x] #5 Architecture impact is assessed before implementation; significant boundary, dependency, persistence or test-strategy changes receive explicit human approval before coding and an ADR is recorded when required.
- [x] #6 README, business requirements, diagram and ADR impacts use canonical delivery-governance wording; test evidence, manual verification, finalized plan and remaining risks are recorded before merge.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Inspected both empty records, available Git source history, map relationships/flows and Backlog references. Neither held useful authored content or a current source responsibility; origin remains unknown.
2. Removed both obsolete records through Groma CLI, recording each deleted path immediately. Kept recoverpendingplanningratingusecase and all application sources unchanged.
3. Ran two scans: no recreated IDs. Verified all 124 surviving map files against original hashes, 289 unique existing owned source paths, 103 relative map links and 106 bare-ID task architecture references.
4. Recorded disposition report and canonical impacts; deleted previous local APK, rebuilt a fresh stable-key signed APK with JDK21, verified v1/v2 signatures and digest, and finalized this task.
Minimal documentation cleanup; no significant architecture/dependency/schema/testing-strategy change, approval or ADR. No implementation deviations; historical manifest intentionally remains scoped to 4630970.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary
Removed the source-free pendingplanningratingrecovery and androidjavacompatibility placeholders through Groma CLI. Preserved all useful architecture knowledge and the separately implemented recoverpendingplanningratingusecase. Dispositions and unknown origin are documented in backlog/docs/groma-empty-record-dispositions-2026-10-20.md.

## Acceptance criteria validation
1. Both unused records had no Code, description, body, group, relationship or flow, and no corresponding source in available history; removed through CLI.
2. No live task architecture references targeted either ID. Historical audit and modified-file paths remain intentional historical evidence. Changed paths recorded immediately through Backlog CLI.
3. Two scans created zero components and did not recreate either ID. All 124 surviving records matched baseline hashes. Independent checks found 289 unique existing owned sources, 103 resolvable relative map links and 106 resolvable bare-ID task references. Path/URL/folded historical references were excluded from the ID check.
4–6. Canonical impacts, architecture assessment, finalized plan, evidence and risks recorded.

## How to test
### Automated tests
Groma 0.6.6 / Java scanner 0.2.2: two groma scan runs passed (created 0, refreshed 109, matched 0, findings 99). Exact-content comparison against 4630970 manifest accepted precisely the two intended deletions; no other map bytes changed. Independent ownership/link/reference checks passed. git diff --check passed. Required JDK21 assembleRelease --rerun-tasks succeeded with 53 executed tasks after deleting old APK. apksigner verified v1/v2 signatures. APK SHA256: 08550fa48018aaf29eb72c1fdf6bf4fa2c4bfbfeff4583a8eaa86a7a344e6380. APK remains 2.34/code42; no publication. Application/native/SQL tests unchanged and not rerun for metadata-only cleanup.
### Manual validation
Reviewed records and searched available Git history, map and task references; no useful knowledge to rehome. Read current recovery owner independently. No UI layout changed.

## TDD / BDD / approval-test evidence
Characterized all 126 map files before removal with baseline manifest. Given two obsolete source-free records, CLI removes only those records; given rescans, they remain absent and all 124 survivors remain unchanged. No application behavior changed.

## Architecture impact
- Architecture-significant change: no; obsolete documentation cleanup only.
- Approval received: not required.
- ADR: ADR impact: none, because no architectural decision changed.

## README impact
README impact: none, because cleanup does not change runtime behaviour and baseline verification is already scoped to its historical commit.

## Business requirements impact
Business requirements impact: none, because product scope and business rules are unchanged.

## Diagram impact
Diagram impact: updated Groma to remove two obsolete empty records; all surviving map content preserved.

## Commits / logical change list
Delete two source-free map records; add disposition/validation report; finalize task through CLI.

## Risks and follow-up
Historical origins remain unknown. Baseline hashes intentionally fail for these deleted files in the cleaned checkout; reproduce historical baseline at 4630970. Container repair remains unresolved in TASK-227. Known 99 lint groups and scanner limitations unchanged; semantic gate remains TASK-233. No merge or publication.
<!-- SECTION:NOTES:END -->

---
id: TASK-204
title: 'DOC: Define the validated 3.0 paired appearance and flow contract'
status: Done
assignee:
  - '@codex'
created_date: '2026-10-17 09:21'
updated_date: '2026-10-21 17:09'
labels:
  - ux
  - release-3.0
milestone: m-2
dependencies: []
modified_files:
  - backlog/docs/release-3.0-design-contract.md
  - backlog/docs/business-requirements.md
  - README.md
  - backlog/docs/visual-design-system.md
priority: medium
type: docs
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As the product owner, I want one explicit approved design contract so the native implementation matches the reviewed flow. Document Ironline dark and Field Notes light palettes, shared hierarchy/spacing/targets, selective serif light headings, labeled navigation, state wording and screen inventory. Record the user validation without inventing completion timings. Mark planned behavior versus current release. Out of scope: Android implementation and changing rating/scheduling rules.

Validated baseline: TASK-201, design/release-2.34/themes.html; user approved 2026-10-17. Keep the existing Android Views stack and current domain/application/infrastructure boundaries. This task is design implementation scope, not permission for an architecture-significant change; assess such changes and obtain explicit approval/ADR before coding if necessary.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given the validated HTML, when the design contract is read, then both palettes and shared component/navigation behavior are specified with links to review evidence.
- [x] #2 Given existing BR-047/BR-061 and the visual design system, when 3.0 scope is documented, then planned theme/navigation changes and retained business rules are explicit without describing them as already shipped.
- [x] #3 Given every current screen and dialog, when the coverage inventory is reviewed, then loading/empty/error/offline/pending and both appearances are assigned to implementation and validation tasks.
- [x] #4 README and business requirements impacts use canonical delivery-governance wording; approved behavior is documented, diagrams updated only where affected, and the full validation package is recorded before merge.
- [x] #5 The flow contract explicitly distinguishes the established personal band rating, selected-festival planning and after-rating under ADR-0016, and specifies one-time carry-over, archive promotion and source-aware correction states.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Reconciled approved paired CSS/review and current native inventory with ADR0016. 2. Wrote exact palettes/shared layout/navigation/state/lifecycle contract and assigned11 Activities/6 named dialogs plus nested/system handoffs to TASK205-212. 3. Updated planned BR047/047a/061 scope, README and older design-system precedence without claiming shipped changes. 4. Passed4 prototype preference cases,80 contrast pairs, all native-inventory/task/link checks and whitespace validation; built fresh stable-key signed APK at35cf9e3 in clean checkout. Documentation-only, no new architecture decision or diagrams; closure evidence commit follows. No product tests repeated for unchanged code.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Created release-3.0-design-contract.md from the approved paired HTML and actual native inventory. Specifies exact palettes, proportional shared typography with light serif headings, spacing/48dp/reflow/contrast, System/Dark/Light semantics, labeled navigation, state wording and no-write appearance changes. Covers all11 Activities and6 named dialog helpers plus nested confirmations, sync and Android-owned handoffs with TASK205-212 owners. Preserves ADR0016 established/planning/after, creation snapshots, archive promotion and source-aware correction invariants.

## Acceptance criteria validation

AC1-3: Contract links actual qualitative approval/CSS source and assigns all native screen/state coverage; planned3.0 is distinct from released2.35.1. Planned BR047/047a/061 overrides and older visual-design-system precedence are explicit. AC5: full role/scope/lifecycle table prohibits cross-member/festival fallback and latest-event-as-established-rating. AC4: README/requirements/source-of-truth updates and validation package below.

## How to test

### Automated tests

Prototype preference suite4/4 passed; existing contrast script80 palette pairs passed (token evidence only). Read-only inventory/link check verifies every11 Activity and6 named dialog helper is present, TASK205-213 owners referenced and every local link resolves. git diff --check passed. Fresh signed APK built in clean checkout at35cf9e3 via assembleRelease --rerun-tasks,53 tasks executed; no prior APK existed. No behavior code changed, so no new application tests or repeated native/database run is justified by this documentation task.

### Manual validation

Compared paired CSS overrides with contract token/layout table, reviewed actual manifest/Activity/dialog sources, current state retention and ADR0016 lifecycle. Qualitative user approval is not a timed/native accessibility pass; TASK212 owns native evidence and unresolved physical/user checks. Stable-key build signature verified separately and artifact digest recorded in follow-up evidence.

## TDD / BDD / approval-test evidence

Documentation-only task: acceptance criteria verified against approved prototype, actual inventory and source links. Existing preference/contrast checks pass; no product behavior or legacy refactoring introduced.

## Architecture impact

Architecture-significant change: none; documents approved Android Views design scope without implementing a new framework, module, lifecycle mechanism or persistence model. Approval received: paired proposal qualitative user validation17October2026 and current milestone start authorization. ADR impact: none, because no architecture decision is made; future significant implementation choices still require explicit approval.

## README impact

README impact: updated link to the native contract and clear planned-versus-shipped/native-validation status.

## Business requirements impact

Business requirements impact: updated approved planned3.0 section and BR047/047a/061 override intent, preserving current implemented rows until native stories complete.

## Diagram impact

Diagram impact: none, because runtime boundaries/collaborations and source ownership are unchanged; documentation tables communicate design/coverage without inventing an architecture diagram.

## Commits / logical change list

35cf9e3: contract, planned requirements, README and older design-system precedence. Final evidence closure is documentation only.

## Risks and follow-up

TASK205-211 must implement state retention and accessibility, not infer them from HTML. TASK212 must report actual device/TalkBack/focus/200%/contrast/target evidence and unverified observations honestly; TASK213 still gates signed3.0 publication. Prototype geometry and outdoor benefit remain unproven. No rating/scheduling/identity/security/provider behavior changes authorized by this contract. Unrelated user changes remain preserved and excluded from build.

Fresh APK SHA256:2bb5a7bd6b8120c8037eaaacb07c169e9432fe29a0173bd05efe3a73c4cd58b1; path /private/tmp/wacken-204-build-checkout/app/build/outputs/apk/release/app-release.apk. apksigner verified v1/v2; stable certificate ede2ae97735a305900e87bc0fdc021c03382db4e30435f2349a96a3fdcc7545f. This validation build does not publish or change released version2.35.1.
<!-- SECTION:NOTES:END -->

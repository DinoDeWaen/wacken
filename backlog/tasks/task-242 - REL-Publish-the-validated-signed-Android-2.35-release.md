---
id: TASK-242
title: 'REL: Publish the validated signed Android 2.35 release'
status: To Do
assignee: []
created_date: '2026-10-20 06:26'
labels:
  - groma-audit
  - release-2.35
milestone: m-3
dependencies:
  - TASK-241
references:
  - wacken
documentation:
  - backlog/docs/groma-audit-2026-10-20.md
priority: high
type: chore
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## User story
As an attendee, I want a verified signed 2.35 update so the reliability improvements reach my existing installation without losing data.

## Scope
Prepare final version metadata, release notes, fresh stable-key signed APK, checksum, supported in-place upgrade and GitHub publication after the 2.35 validation package is complete. Continue the approved signed GitHub APK distribution. No new testing-distribution service or 3.0 design scope.

## Audit reference
[Groma architecture audit — 20 October 2026](../docs/groma-audit-2026-10-20.md)
Repository path: backlog/docs/groma-audit-2026-10-20.md
Findings: Delivery of approved audit remediation. Investigation: TASK-225.

## Boundaries and impacts
Preserve ADR-0016: shared canonical metadata, member-specific established band rating, independent festival planning/after-ratings, one-time creation snapshots and source-aware archive promotion/correction. Follow Groma CLI-only curation and Backlog CLI-only task maintenance.
Business requirements impact: none planned unless an approved behaviour change is explicitly required; record the actual impact on completion.
README and diagram impacts are assessed for the affected workflow. ADR impact: none assumed; approval is required for significant changes.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 The final APK has versionName 2.35 and a versionCode greater than the last published version, is built fresh from the release commit and verifies with the stable release certificate.
- [ ] #2 Installing over the latest supported published stable APK retains login/cache, pending ratings, member-specific established ratings, festival planning/after values, history and corrections; upgrade evidence is tied to the exact candidate.
- [ ] #3 Required unit/QA/Android compatibility/architecture and applicable database gates pass for the final source; no failed or skipped mandatory gate is represented as a pass.
- [ ] #4 GitHub tag/release contain the verified signed APK, checksum, release notes, installation instructions, audit remediation summary and exact validation evidence.
- [ ] #5 Published asset digest/signature/version and tag source are verified after upload; Backlog acceptance checks, notes and Done status are completed after publication.
- [ ] #6 README and release documentation describe implemented changes, retained limits and rollback/upgrade risks without claiming all lint similarities are defects.
- [ ] #7 Architecture impact is assessed before implementation; significant boundary, dependency, persistence or test-strategy changes receive explicit human approval before coding and an ADR is recorded when required.
- [ ] #8 README, business requirements, diagram and ADR impacts use canonical delivery-governance wording; test evidence, manual verification, finalized plan and remaining risks are recorded before merge.
<!-- AC:END -->

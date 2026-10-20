---
id: TASK-234
title: 'BUG: Bound rating and schedule network waits without losing pending changes'
status: To Do
assignee: []
created_date: '2026-10-20 06:26'
labels:
  - groma-audit
  - release-2.35
milestone: m-3
dependencies: []
references:
  - wacken
documentation:
  - backlog/docs/groma-audit-2026-10-20.md
priority: medium
type: bug
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## User story
As an attendee, I want failed or stalled sync to finish with a useful error so cached data remains usable and pending changes can retry.

## Scope
Characterize the duplicated HTTP clients and define bounded connect/read waits for rating, personal history and schedule calls, consistent with a reviewed adapter transport policy. Preserve existing auth refresh, operation identity, request headers/body/upsert and write retention. Reuse mechanics only where justified; no new network framework assumed.

## Audit reference
[Groma architecture audit — 20 October 2026](../docs/groma-audit-2026-10-20.md)
Repository path: backlog/docs/groma-audit-2026-10-20.md
Findings: F6; lint groups 33/34/65/66. Investigation: TASK-225.

## Boundaries and impacts
Preserve ADR-0016: shared canonical metadata, member-specific established band rating, independent festival planning/after-ratings, one-time creation snapshots and source-aware archive promotion/correction. Follow Groma CLI-only curation and Backlog CLI-only task maintenance.
Business requirements impact: none planned unless an approved behaviour change is explicitly required; record the actual impact on completion.
README and diagram impacts are assessed for the affected workflow. ADR impact: none assumed; approval is required for significant changes.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given a connection or response that stalls, each affected operation stops within the documented configured timeout and reports failure while cached data and pending writes remain intact.
- [ ] #2 Auth expiry/refresh/retry, member and group checks, upsert headers, body serialization, success parsing and failure status handling retain their characterized contracts.
- [ ] #3 Controlled local transport tests prove connect/read policy and stalled-response outcomes without depending on live Supabase availability; regressions are written red before implementation.
- [ ] #4 Domain/application remain free of HTTP dependencies; any new shared transport boundary receives explicit approval and ADR handling before coding.
- [ ] #5 README documents timeout/retry/offline troubleshooting and the validation package records supported Android verification.
- [ ] #6 Architecture impact is assessed before implementation; significant boundary, dependency, persistence or test-strategy changes receive explicit human approval before coding and an ADR is recorded when required.
- [ ] #7 README, business requirements, diagram and ADR impacts use canonical delivery-governance wording; test evidence, manual verification, finalized plan and remaining risks are recorded before merge.
<!-- AC:END -->

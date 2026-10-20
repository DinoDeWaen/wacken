---
id: TASK-236
title: 'BUG: Make backend error-message selection consistent and testable'
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
As an attendee, I want clear backend failures so I can understand failed login or sync without ambiguous message selection.

## Scope
Characterize SupabaseAuthClient and SupabaseAuthenticatedRequest error extraction, then define and test intended message/msg precedence, empty/malformed responses and operation-specific fallback. Preserve authentication semantics and raw-response privacy; do not change backend policy or session lifecycle.

## Audit reference
[Groma architecture audit — 20 October 2026](../docs/groma-audit-2026-10-20.md)
Repository path: backlog/docs/groma-audit-2026-10-20.md
Findings: F6-related error drift; lint group 59. Investigation: TASK-225.

## Boundaries and impacts
Preserve ADR-0016: shared canonical metadata, member-specific established band rating, independent festival planning/after-ratings, one-time creation snapshots and source-aware archive promotion/correction. Follow Groma CLI-only curation and Backlog CLI-only task maintenance.
Business requirements impact: none planned unless an approved behaviour change is explicitly required; record the actual impact on completion.
README and diagram impacts are assessed for the affected workflow. ADR impact: none assumed; approval is required for significant changes.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given message, msg, both, neither, blank or malformed response bodies, the selected user-facing error follows an explicit documented rule with an appropriate status/operation fallback.
- [ ] #2 Login versus authenticated retry/session behaviour remains unchanged and no credential or token is exposed in user-visible errors or new logs.
- [ ] #3 Existing behaviour is characterized before refactoring; red/green tests cover precedence and malformed responses, with any intentional display change explicitly documented.
- [ ] #4 Error-parsing reuse stays inside adapters and does not let transport own domain policy; README/business requirements impacts and manual examples are recorded.
- [ ] #5 Architecture impact is assessed before implementation; significant boundary, dependency, persistence or test-strategy changes receive explicit human approval before coding and an ADR is recorded when required.
- [ ] #6 README, business requirements, diagram and ADR impacts use canonical delivery-governance wording; test evidence, manual verification, finalized plan and remaining risks are recorded before merge.
<!-- AC:END -->

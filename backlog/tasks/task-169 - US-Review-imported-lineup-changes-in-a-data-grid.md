---
id: task-169
title: 'US: Review imported lineup changes in a data grid'
status: To Do
assignee: []
created_date: '2026-08-19 11:02'
updated_date: '2026-10-17 13:21'
labels:
  - user-story
  - future
  - import
  - data-review
milestone: m-0
dependencies:
  - TASK-215
  - TASK-216
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

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
2026-10-17: The current CSV import directly replaces validated local snapshots; no review grid exists. Proposed row acceptance/rejection preserves baseline data and ratings using the existing local import transaction. See backlog/docs/release-2.34-remaining-architecture-proposal.md and TASK-215 before implementation.

2026-10-17 user-approved architecture: ADR-0016 governs canonical shared bands, member-specific established band ratings, independent festival planning/after-ratings, one-time creation snapshots and atomic archive promotion. Missing after-ratings retain the prior band rating; archived corrections update it only while their festival remains the source. TASK-216–220 implement and validate the lifecycle. This supersedes earlier latest-event prefill wording; do not treat approved planning as shipped behavior.
<!-- SECTION:NOTES:END -->

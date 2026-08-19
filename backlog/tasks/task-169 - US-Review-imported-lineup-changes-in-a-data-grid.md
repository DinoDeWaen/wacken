---
id: task-169
title: 'US: Review imported lineup changes in a data grid'
status: To Do
assignee: []
created_date: '2026-08-19 11:02'
labels:
  - user-story
  - future
  - import
  - data-review
dependencies: []
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
<!-- AC:END -->

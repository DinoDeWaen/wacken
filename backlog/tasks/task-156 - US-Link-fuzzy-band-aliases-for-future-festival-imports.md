---
id: task-156
title: 'US: Link fuzzy band aliases for future festival imports'
status: To Do
assignee: []
created_date: '2026-08-12 07:53'
updated_date: '2026-08-23 09:35'
labels:
  - user-story
  - future
  - bands
  - aliases
  - import
dependencies:
  - task-151
priority: low
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a group member, I want the app to propose likely existing bands when a future lineup uses a slightly different name so that rating history can be linked without duplicate band records.

Business value: Band history remains accurate when festivals use aliases, punctuation differences, or alternate spellings.

Scope: fuzzy-search candidate proposals during future lineup import, user-confirmed linking, alias storage, and preserving exact existing band identity after confirmation.

Out of scope: first post-Wacken implementation, automatic unreviewed fuzzy merges, multi-group alias ownership.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given an imported band name does not exactly match an existing band, then a future import flow can propose likely matches instead of immediately creating only a duplicate.
- [ ] #2 Given a user confirms a proposed match, then the lineup entry links to the existing band and the imported name can be stored as an alias.
- [ ] #3 Given no candidate is confirmed, then a new band record is created.
- [ ] #4 The story is marked future scope and must not block the first post-Wacken exact-name implementation.
- [ ] #5 README, business requirements, diagram, and ADR impact are recorded using delivery-governance wording when implemented.
<!-- AC:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Update 2026-08-23: User reported a concrete CSV import defect after Summer Breeze upload. Reviewed fuzzy/manual linking and missing-field metadata enrichment are now captured in task-179, which should be treated as the actionable replacement for this older future-scope placeholder. Keep this task only as historical context unless task-179 is split during implementation.
<!-- SECTION:NOTES:END -->

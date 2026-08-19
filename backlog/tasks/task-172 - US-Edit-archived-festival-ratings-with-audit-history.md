---
id: task-172
title: 'US: Edit archived festival ratings with audit history'
status: To Do
assignee: []
created_date: '2026-08-19 11:02'
labels:
  - user-story
  - future
  - archive
  - ratings
  - audit
dependencies: []
priority: low
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Business value: Historical ratings can be corrected after a festival without losing trust in what changed.

As a user, I want to correct ratings on archived festivals with change history so that my past festival record stays accurate.

Scope: explicit archived edit mode, planning or real-rating corrections, and audit visibility for who changed what and when.

Out of scope: editing festival master data, re-opening an archived festival as active, and multiple upcoming festival management.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given an archived festival is opened, then it is read-only by default until the user explicitly enters an allowed edit mode.
- [ ] #2 Given a rating is changed for an archived festival, when the change is saved, then the old value, new value, user, and timestamp are recorded.
- [ ] #3 Given archived ratings are exported or shown in details, then corrected values and historical context remain understandable.
- [ ] #4 Relevant tests cover read-only default behavior, allowed corrections, and audit history.
- [ ] #5 README, business requirements, diagram, and ADR impact are recorded using delivery-governance wording when implemented.
<!-- AC:END -->

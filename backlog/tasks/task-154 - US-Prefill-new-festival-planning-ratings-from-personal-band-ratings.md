---
id: task-154
title: 'US: Prefill new festival planning ratings from personal band ratings'
status: To Do
assignee: []
created_date: '2026-08-12 07:53'
updated_date: '2026-08-12 07:54'
labels:
  - user-story
  - post-mvp3
  - ratings
  - prefill
  - festivals
dependencies:
  - task-151
  - task-152
  - task-153
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a group member, I want known bands in a newly added festival to start with my latest personal band rating so that planning begins with useful defaults.

Business value: Repeated bands become faster to plan while still allowing festival-specific planning choices.

Scope: when a new festival lineup links to existing bands by exact name, prefill the current user's festival planning rating from their latest personal band rating event by created date; leave unknown bands unrated; allow editing the prefilled planning rating without changing personal history.

Out of scope: prefilling from older festival planning ratings, fuzzy alias matching, bulk review grid.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given a known band has no personal rating history for the user, then its planning rating remains unrated.
- [ ] #2 Given a user edits a prefilled planning rating, then the edited value applies only to that festival planning rating.
- [ ] #3 Given older festival planning ratings exist, then they are not used as the prefill source unless a later requirement changes this rule.
- [ ] #4 Domain/application tests cover latest-personal-rating selection, no-history behavior, and independence from planning ratings; BDD covers a new festival import with known and unknown bands.
- [ ] #5 README, business requirements, diagram, and ADR impact are recorded using delivery-governance wording.
- [ ] #6 Given a newly imported festival lineup contains an exact-name match for a band with personal rating history, then the planning rating for that user is prefilled from the latest personal band rating event by created date.
<!-- AC:END -->

---
id: task-13
title: 'US-013: Apply default one-star rating for unrated bands'
status: To Do
assignee: []
created_date: '2026-05-15 06:33'
labels:
  - mvp1
  - rating
  - domain
dependencies:
  - task-4
  - task-5
  - task-8
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## US-013: Apply default one-star rating for unrated bands

**As an** attendee
**I want** unrated bands to count as 1 star by default
**So that** group decisions can treat missing ratings as OK / indifferent without blocking the rating workflow

### Notes
- Source: `backlog/docs/business-requirements.md` BR-020.
- Keep the default behavior in domain/application logic, not Android UI conditionals.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given a group member has not rated a band When group decision logic reads that member rating Then the rating is treated as 1
- [ ] #2 Given a band has no explicit rating from the current user When the band appears in the rating UI Then the user can see or set the 1-star default clearly
- [ ] #3 Given a user changes the default rating When the rating is saved Then the explicit value replaces the default for future reads
<!-- AC:END -->

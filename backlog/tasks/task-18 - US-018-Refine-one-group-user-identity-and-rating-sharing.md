---
id: task-18
title: 'US-018: Refine one-group user identity and rating sharing'
status: To Do
assignee: []
created_date: '2026-05-15 06:34'
labels:
  - refinement
  - rating
  - group
dependencies:
  - task-8
  - task-13
priority: medium
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## US-018: Refine one-group user identity and rating sharing

**As a** group member
**I want** the current one-group identity and rating-sharing model refined
**So that** my group can combine ratings without adding multi-group complexity this year

### Notes
- Source: `backlog/docs/business-requirements.md` BR-037 and open questions about user identity, storage, and invite format.
- Multi-group support is explicitly out of current scope.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given the current product supports one shared group When identity is refined Then the way users are represented in that group is documented
- [ ] #2 Given ratings must be shared inside the one group When storage or sync is refined Then where ratings live and how they are exchanged is documented
- [ ] #3 Given a member has not rated a band When the model is refined Then the default 1-star behavior is included
- [ ] #4 Given friend invites are planned When the invite format is refined Then the most useful Android share format is documented or deferred with rationale
<!-- AC:END -->

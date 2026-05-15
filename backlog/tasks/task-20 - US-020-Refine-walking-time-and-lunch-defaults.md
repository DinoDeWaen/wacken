---
id: task-20
title: 'US-020: Refine walking-time and lunch defaults'
status: To Do
assignee: []
created_date: '2026-05-15 06:34'
labels:
  - refinement
  - scheduling
  - lunch
dependencies:
  - task-4
priority: medium
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## US-020: Refine walking-time and lunch defaults

**As a** group member
**I want** walking-time and lunch defaults refined
**So that** the first schedule generator can make sensible choices before user-configurable settings exist

### Notes
- Source: `backlog/docs/business-requirements.md` BR-023, BR-027, BR-028, and open questions.
- Walking minutes are the default unit; user-configurable walking speed is later scope.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given travel feasibility uses walking minutes by default When defaults are refined Then the source of walking-minute data between stages is documented
- [ ] #2 Given lunch must occur inside 12:00-14:00 When lunch defaults are refined Then the initial lunch block duration and placement rule are documented
- [ ] #3 Given no nearby food exists When lunch behavior is refined Then the no-substitute behavior remains explicit
- [ ] #4 Given these defaults affect scheduling When refinement is complete Then follow-up scheduling implementation tasks are identified
<!-- AC:END -->

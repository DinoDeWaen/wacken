---
id: task-75
title: 'US: Apply MVP stage walking-time defaults'
status: To Do
assignee: []
created_date: '2026-06-10 12:17'
labels:
  - schedule
  - travel
  - mvp3
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The group schedule and future feasibility rules need explicit MVP walking-time defaults for named stage movements. The user clarified that Heavy and Louder are 5 minutes apart, and that moving between Heavy or Louder and another stage is 15 minutes. Walking between two other stages still needs clarification before implementation.

As a festival attendee, I want the app to use agreed walking-time defaults between stages so that schedule travel information and feasibility decisions are understandable.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given the source data does not provide a more specific distance, when travel between Heavy and Louder is needed, then the MVP default walking time is 5 minutes.
- [ ] #2 Given the source data does not provide a more specific distance, when travel between Heavy or Louder and any other stage is needed, then the MVP default walking time is 15 minutes.
- [ ] #3 Before implementation starts, the product decision for walking between two stages that are neither Heavy nor Louder is clarified and recorded in the task notes or acceptance criteria.
- [ ] #4 Architecture impact is assessed before implementation; if an architecture-significant change is needed, explicit approval is requested before coding and an ADR is created or updated if approved.
- [ ] #5 Automated tests cover the default walking-time rules.
- [ ] #6 Business requirements impact: updated for BR-073.
- [ ] #7 README impact is recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->

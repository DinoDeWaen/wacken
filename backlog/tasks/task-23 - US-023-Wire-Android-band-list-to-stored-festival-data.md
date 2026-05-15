---
id: task-23
title: 'US-023: Wire Android band list to stored festival data'
status: To Do
assignee: []
created_date: '2026-05-15 15:22'
labels:
  - mvp1
  - android
  - listing
dependencies:
  - task-21
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## US-023: Wire Android band list to stored festival data

**As an** attendee
**I want** the app band list to show imported stored data
**So that** I can browse the lineup in the APK

### Notes
- Replace the current placeholder empty list wiring.
- Keep business logic in application/domain use cases.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given stored festival data exists When I open the app Then the band list shows imported bands with stage, time, and effective rating
- [ ] #2 Given no festival data exists When I open the app Then the empty state remains clear
- [ ] #3 Given a band has an explicit rating When I open the list Then the rating is shown instead of the default marker
<!-- AC:END -->

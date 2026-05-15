---
id: task-24
title: 'US-024: Wire Android band detail navigation and rating save'
status: To Do
assignee: []
created_date: '2026-05-15 15:22'
labels:
  - mvp1
  - android
  - rating
dependencies:
  - task-21
  - task-23
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## US-024: Wire Android band detail navigation and rating save

**As an** attendee
**I want** to tap a band, rate it, and return to the list
**So that** the APK supports the core rating workflow end to end

### Notes
- Build on the existing `BandDetailActivity` and rating use cases.
- Persist ratings through the MVP local persistence adapter.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given I tap a band in the list When the detail screen opens Then it shows that band and the current effective rating
- [ ] #2 Given I select a 0-4 rating When I return to the list Then the new rating is shown
- [ ] #3 Given a band has YouTube or Spotify metadata When I open details Then available links are shown and missing links are hidden
<!-- AC:END -->

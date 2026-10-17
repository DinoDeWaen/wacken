---
id: task-13
title: 'US-014: Band detail rating screen with music links'
status: To Do
assignee: []
created_date: '2026-05-15 06:33'
labels:
  - mvp1
  - rating
  - ui
dependencies:
  - task-7
  - task-8
  - task-12
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## US-014: Band detail rating screen with music links

**As an** attendee
**I want** a band detail screen inspired by the official Wacken band detail page with rating stars and optional music links
**So that** I can review a band and rate it without leaving the rating flow

### Notes
- Source: `backlog/docs/business-requirements.md` BR-036.
- The screen should be inspired by the Wacken page, not a direct copy.
- YouTube and Spotify links are optional band metadata and should render only when available.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given a band exists When I open its detail screen Then I see the band information needed for rating
- [ ] #2 Given a band has a stored rating When I open its detail screen Then the selected star rating is displayed
- [ ] #3 Given I select a 0-4 star rating on the detail screen When I save or leave the screen Then the rating is stored through the application use case
- [ ] #4 Given a band has YouTube or Spotify links When I open its detail screen Then those links are available from the screen
- [ ] #5 Given a band has no YouTube or Spotify links When I open its detail screen Then the screen does not show broken or empty link controls
<!-- AC:END -->

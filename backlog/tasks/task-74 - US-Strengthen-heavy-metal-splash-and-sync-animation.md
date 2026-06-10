---
id: task-74
title: 'US: Strengthen heavy-metal splash and sync animation'
status: To Do
assignee: []
created_date: '2026-06-10 12:17'
labels:
  - ui
  - sync
  - mvp2
dependencies: []
priority: medium
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The current splash and sync feedback exists, but it should feel more strongly aligned with the Wacken heavy-metal identity.

As a festival attendee, I want startup, reactivation, manual sync, and close-sync feedback to look clearly heavy metal so that waiting for sync feels like part of the app experience rather than a generic loading state.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given the app starts or reactivates and sync is running, when feedback is shown, then the splash or sync animation uses a stronger heavy-metal visual style consistent with the rest of the app.
- [ ] #2 Given manual sync or sync-and-exit is running, when feedback is shown, then the user can tell sync is in progress and conflicting sync or close actions are blocked.
- [ ] #3 Given the heavy-metal splash and sync animation is shown on phone-sized and BlueStacks-sized screens, then text and visuals remain readable and do not overlap.
- [ ] #4 Given sync succeeds or fails, when the operation completes, then the existing success and failure behavior is preserved.
- [ ] #5 Automated or focused compile checks protect existing sync behavior, and manual visual validation is documented.
- [ ] #6 Business requirements impact: updated for BR-072.
- [ ] #7 README impact is recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->

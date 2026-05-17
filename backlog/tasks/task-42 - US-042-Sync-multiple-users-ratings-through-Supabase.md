---
id: task-42
title: 'US-042: Sync multiple users ratings through Supabase'
status: To Do
assignee: []
created_date: '2026-05-17 16:52'
labels:
  - backend
  - ratings
  - sync
  - supabase
  - room
dependencies:
  - task-40
  - task-41
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a group member, I want my band ratings to sync with the central backend and see other members ratings so that the group can collaboratively plan the festival.

Business value:
- Enables multiple people to rate bands from their own devices.
- Provides the central data needed for group decisions and schedule generation.

In scope:
- Store local rating changes immediately in Room with sync status.
- Push the signed-in user rating changes to Supabase.
- Pull group ratings from Supabase into Room.
- Preserve offline-first behavior when the backend is unavailable.
- Enforce that users can only write their own ratings through RLS/backend rules.

Out of scope:
- Final group decision algorithm.
- Realtime UI updates if polling/manual refresh is sufficient for MVP.
- Multiple independent groups beyond the schema support from task-40.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given a signed-in user rates a band offline or online, then the rating is saved locally immediately.
- [ ] #2 Given connectivity is available, when pending local ratings are synced, then Supabase stores the user rating for the correct group and band.
- [ ] #3 Given other group members have ratings in Supabase, when group ratings are synced, then the app stores them locally for later group planning.
- [ ] #4 Given sync fails, then the local rating remains pending and the user does not lose their choice.
- [ ] #5 Given a malicious or incorrect request attempts to update another user rating, then backend policies reject it.
- [ ] #6 Automated tests cover pending/synced/failed rating sync behavior and existing rating behavior remains green.
- [ ] #7 README or implementation notes document the rating sync behavior and manual validation steps.
<!-- AC:END -->

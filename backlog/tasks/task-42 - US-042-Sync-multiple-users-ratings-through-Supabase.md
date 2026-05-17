---
id: task-42
title: 'US-042: Sync multiple users ratings through Supabase'
status: Done
assignee:
  - '@codex'
created_date: '2026-05-17 16:52'
updated_date: '2026-05-17 18:10'
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
- [x] #1 Given a signed-in user rates a band offline or online, then the rating is saved locally immediately.
- [x] #2 Given connectivity is available, when pending local ratings are synced, then Supabase stores the user rating for the correct group and band.
- [x] #3 Given other group members have ratings in Supabase, when group ratings are synced, then the app stores them locally for later group planning.
- [x] #4 Given sync fails, then the local rating remains pending and the user does not lose their choice.
- [x] #5 Given a malicious or incorrect request attempts to update another user rating, then backend policies reject it.
- [x] #6 Automated tests cover pending/synced/failed rating sync behavior and existing rating behavior remains green.
- [x] #7 README or implementation notes document the rating sync behavior and manual validation steps.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Inspect current rating repository flow, Room rating schema, Supabase ratings table/RLS policies, and UI rating save call sites.
2. Add tests for cache-first rating sync behavior: local save before remote push, failed push remains pending, successful push marks synced, and pull merges group ratings.
3. Extend Room rating storage with sync metadata through a Room migration while keeping the domain RatingRepository API unchanged.
4. Add Supabase rating client/source behavior for signed-in user upsert and group rating pull using the authenticated user/group from AuthSession.
5. Wire AppRepositories/MainActivity/BandDetailActivity so rating changes save locally immediately and sync can push pending ratings plus pull group ratings.
6. Validate backend RLS assumptions through existing policies/documented SQL validation and update README/implementation notes.
7. Run checks, update acceptance criteria/task notes, and commit task-42.

Architecture impact: standard depth. This adds rating sync adapters within the approved Supabase/Room architecture and keeps domain/application APIs unchanged. No new architecture approval expected unless a new module/dependency or backend policy strategy is required.
Test strategy: app unit tests for sync coordinator/client mapping plus existing domain/application/infrastructure/app regression checks.
Risks/assumptions: Supabase ratings require authenticated users with default group membership and central band ids. RLS write rejection for other-user writes is covered by backend policy from task-39/task-40 and documented validation unless test credentials are available.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Implementation notes:
- Added Room rating sync metadata (groupId, syncStatus) with a version 1 to 2 Room migration.
- Added SyncingRatingRepository so rating changes save locally as PENDING immediately and are pushed to Supabase only through explicit sync.
- Added SupabaseRatingClient to upsert the signed-in user rating for the authenticated group and pull group ratings back into Room.
- Wired the existing Sync from Supabase action to push pending ratings and pull group ratings after master-data sync.
- Kept the domain/application RatingRepository API unchanged.

Validation package:
- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:testDebugUnitTest passed.
- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew test passed.
- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew qaTest passed.
- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew assembleDebug passed.
- backend/flyway/verify-auth-setup.sh passed and confirms the ratings table has 4 RLS policies plus the default group. The relevant write policies require user_id = auth.uid() and group membership, which rejects another-user rating writes.

README impact: updated with pending rating sync behavior, group rating sync through the app sync action, and RLS write protection details.
Diagram impact: no new diagram update needed after task-41; this task extends the existing Supabase/Room flow already shown.
ADR impact: no new ADR needed; this follows ADR-0008 and keeps existing domain/application boundaries.
Approval status: no new architecture-significant decision beyond the approved Supabase/Room backend direction.
Risks/follow-ups: live end-to-end rating sync still requires at least one Supabase Auth user in the default group; current validation confirms policies/schema but not a real user-device sign-in flow because no test user credentials are stored in the repo.
<!-- SECTION:NOTES:END -->

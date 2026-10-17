---
id: task-150
title: 'US: Archive active festival and show archive start state'
status: Done
assignee:
  - '@codex'
created_date: '2026-08-12 07:52'
updated_date: '2026-08-12 08:45'
labels:
  - user-story
  - post-mvp3
  - festivals
  - archive
dependencies:
  - task-149
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a group member, I want to archive the completed active festival so that Wacken becomes historical and the app can move on to the next festival.

Business value: The group can preserve Wacken for later review while making it clear that no current festival is being planned.

Scope: manual archive action on the active festival start screen, one-active-festival invariant, read-only archived festival state, start screen behavior when no active festival exists.

Out of scope: editing archived festivals, archive confirmation, multiple active or upcoming festivals.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given an active festival exists, when a user opens the start screen, then the active festival band list is shown with an Archive action at the top.
- [x] #2 Given the active festival is archived, when the archive completes, then the festival is marked archived and no longer active.
- [x] #3 Given no active festival exists, when the app starts, then archived festivals and an add-festival path are shown instead of the active band list.
- [x] #4 Given a festival is archived, when a user opens its data, then festival data and ratings are read-only in the first version.
- [x] #5 The one-active-festival invariant is covered by domain/application tests and externally visible behavior is covered by BDD scenarios.
- [x] #6 README, business requirements, diagram, and ADR impact are recorded using delivery-governance wording.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Added domain tests for festival archive/read-only behavior and one-active-festival lifecycle invariants.
2. Added application tests and a QA scenario for active festival start behavior, manual archive behavior, no-active archive start behavior, and archived read-only state.
3. Added domain/application festival lifecycle concepts, a FestivalRepository port, and use cases for showing and archiving the active festival.
4. Added Room festival persistence with database version 6, a default active Wacken festival migration, a repository adapter, and a Supabase Flyway V009 festivals contract.
5. Wired AppRepositories and MainActivity so the start screen shows the active festival with an Archive action, or a no-active archive/add-festival state when the active festival has been archived.
6. Gated active-festival actions in the no-active archive state by hiding the editable band list and disabling schedule access; deeper archived rating history remains task-155.
7. Updated README and business requirements implemented-capability summary.
8. Ran focused tests, QA scenarios, Android compile, debug APK build, and git diff --check.

Design approach: standard DDD/hexagonal slice based on approved ADR 0011, scoped to festival lifecycle only. Test strategy: BDD-style QA scenario, domain tests, application tests, app regression tests, compile/build checks. Architecture impact: implements an approved part of ADR 0011; no new approval required. README impact: updated. Business requirements impact: updated implemented-capability summary. Diagram impact: none. ADR impact: none, because ADR 0011 already records the decision. Deviation: archived festival detail/history viewing is left to task-155; this story gates archived state as read-only by removing active editing controls when no festival is active.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Implemented the first post-MVP3 festival lifecycle slice. The app now has a domain/application festival model, a seeded active `Wacken Open Air 2026` festival in Room, a Supabase `festivals` migration, and an Android start screen that can switch from active festival planning to a no-active archive state after manual archive.

When no festival is active, the editable band list is hidden, schedule access is disabled, archived festivals are listed as read-only, and the add-festival entry point is shown for the next story.

## Acceptance criteria validation

- AC1: Active festival start behavior is implemented in `ShowFestivalStartUseCase`, `FestivalStartScreenContent`, and `MainActivity`; the active festival view shows an Archive action.
- AC2: `ArchiveActiveFestivalUseCase` archives the active festival and leaves no active festival; covered by domain/application/QA tests.
- AC3: No-active start state lists archived festivals and shows an add-festival entry point; covered by application and app tests.
- AC4: Archived state is read-only in this first slice by hiding the editable band list and disabling schedule access when no active festival exists. Full archived rating history viewing remains task-155.
- AC5: One-active-festival invariant is covered by domain tests; externally visible archive behavior is covered by the QA scenario.
- AC6: Impact notes are recorded below using delivery-governance wording.

## How to test

### Automated tests

- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:test :application:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac qaTest
- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew assembleDebug

### Manual validation

- Ran git diff --check with no whitespace errors.
- Reviewed changed README, business requirements, Room migration, Supabase migration, and MainActivity wiring.

## TDD / BDD / approval-test evidence

Started with failing domain/application/QA tests for missing festival lifecycle classes, then implemented the domain model, use cases, Room adapter, and UI wiring until tests passed. Added app regression tests for start-screen content and Room migration text.

## Architecture impact

- Architecture-significant change: yes, this implements the approved festival lifecycle part of ADR 0011.
- Approval received: yes, standard option approved before implementation.
- ADR: ADR impact: none, because ADR 0011 already records the accepted architecture decision.

## README impact

README impact: updated implemented behavior and architecture notes for active festival archive/start-state behavior and the new `V009__festivals.sql` backend contract.

## Business requirements impact

Business requirements impact: updated the implemented-capability summary for the post-MVP3 festival lifecycle foundation.

## Diagram impact

Diagram impact: none, because this slice uses the existing module/container architecture and does not change diagram-level structure.

## Commits / logical change list

- Added domain festival lifecycle model and tests.
- Added application start/archive use cases and QA scenario.
- Added Room `festivals` persistence and migration.
- Added Supabase Flyway `V009__festivals.sql`.
- Updated MainActivity start-state/archive UI wiring.
- Updated README and business requirements.

## Risks and follow-up

- Supabase sync for festival archive state is not wired yet; this slice creates the backend contract but uses Room for the active app behavior.
- Add-festival behavior remains task-151.
- Rich archived festival rating-history viewing remains task-155.
<!-- SECTION:NOTES:END -->

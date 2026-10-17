---
id: task-61
title: 'US-063: Clear a band rating back to unrated'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-07 16:02'
updated_date: '2026-06-07 16:17'
labels:
  - mvp2
  - rating
  - sync
  - android
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a Wacken Planner user, I want to delete/clear my rating for a band, so mistakes can be reset to unrated instead of forcing me to choose 1-5.

In scope:
- Provide a clear rating action in the overview and/or detail rating workflow.
- Store the cleared rating as `0`/unrated locally.
- Sync the cleared rating state to Supabase so other devices and schedule decisions no longer treat the previous rating as active.
- Keep existing 1-5 rating behavior unchanged.

Out of scope:
- Deleting other users ratings, audit history, multiple groups, and schedule manual overrides.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given I have rated a band, when I clear the rating, then the band returns to unrated value 0 locally.
- [x] #2 Given I clear a rating, when sync succeeds, then Supabase no longer contributes my previous explicit rating to group decisions.
- [x] #3 Given another device syncs after I clear a rating, then the cleared band appears unrated for my user and the group decision reflects the cleared state.
- [x] #4 Given I clear a rating from the detail screen, when I return to the overview, then the overview shows no filled stars for that band.
- [x] #5 Given sync fails after clearing, then the local pending clear is preserved and retry sync can complete it later.
- [x] #6 Automated tests cover domain/application rating clear behavior and focused adapter validation covers sync behavior where feasible.
- [x] #7 README and business requirements impact are recorded using the canonical wording from delivery-governance.md.
- [x] #8 Architecture impact is assessed; if backend schema or external API semantics change, explicit approval and ADR handling are required before implementation.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Inspected rating use cases, Room rating persistence, Supabase rating sync, and Android rating controls.
2. Added focused tests for clearing ratings locally, resolving saved zero as unrated, syncing pending clears, preserving pending clears when sync fails, and clearing stale synced group ratings after remote deletion.
3. Implemented clear-as-unrated through the existing RateBandUseCase and RatingRepository path.
4. Implemented Supabase clear semantics by deleting the explicit rating row for local value 0, while preserving the existing backend 1-5 explicit-rating constraint.
5. Replaced pulled synced group ratings in Room so rows missing from the remote pull become local unrated zero values.
6. Added a detail-screen clear rating action and documented public behavior in README/business requirements.
7. Ran focused and broader validation.

Deviation: no backend schema or RLS change was needed; clear sync uses DELETE against the existing ratings row. Architecture impact: not architecture-significant; this changes behavior behind existing app ports/adapters without changing schema, auth, or public backend contracts. ADR impact: none.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Users can now clear their own band rating back to unrated (`0`) from the detail screen. The value is saved locally through the existing rating use case, kept pending when offline, and synced by deleting the explicit Supabase rating row for that user/group/band. Group rating pulls now clear stale synced local rows when a prior remote rating disappeared, so another device stops seeing the old score after sync.

## Acceptance criteria validation

- AC1: `RateBandUseCase` accepts `0` and stores `Rating.of(0)`.
- AC2: `SupabaseRatingClient` translates `0` into a DELETE for the matching rating row, so the previous explicit rating no longer contributes remotely.
- AC3: `SyncingRatingRepository.pullGroupRatings()` replaces synced group rows; missing remote rows become local unrated zero values and stop affecting group decisions.
- AC4: Detail screen includes a clear rating action; returning to the overview reloads/syncs and shows no filled stars for the cleared band.
- AC5: Pending clears use the same pending local state as ratings and remain pending if remote push/delete fails.
- AC6: Application and Android unit tests cover clear behavior and sync handling.
- AC7: README and business requirements impacts are recorded below.
- AC8: Architecture impact assessed below.

## How to test

### Automated tests

- `/bin/zsh -lc 'JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :application:test :app:testDebugUnitTest'`\n- `/bin/zsh -lc 'JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:test :application:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac'`\n- `git diff --check`\n\n### Manual validation\n\n- Not run on a physical device in this task. Behavior is covered by focused application and Android unit tests plus app Java compilation.\n\n## TDD / BDD / approval-test evidence\n\nAdded tests first for application clear behavior, saved-zero effective rating behavior, pending clear sync, failed pending clear retry, and stale remote row clearing on group pull. Implementation followed those tests.\n\n## Architecture impact\n\n- Architecture-significant change: no. The solution uses existing rating repository/use-case flow, existing local pending sync state, and DELETE semantics against the existing Supabase ratings table. No schema, RLS, auth, dependency, or module-boundary change was introduced.\n- Approval received: not required.\n- ADR: none required.\n\n## README impact\n\nREADME impact: updated rating and sync behavior to document clearing ratings back to unrated and deleting explicit Supabase rating contributions after sync.\n\n## Business requirements impact\n\nBusiness requirements impact: updated current capabilities and added BR-001a for clearing a rating back to unrated.\n\n## Diagram impact\n\nDiagram impact: none, because the module structure and data-flow shape remain unchanged.\n\n## Commits / logical change list\n\n- Allow `RateBandUseCase` to store `0` for clear-to-unrated.\n- Treat saved zero as default/unrated in effective rating resolution.\n- Preserve and sync pending clears through existing local sync state.\n- Delete Supabase rating rows for clear operations.\n- Clear stale synced local group ratings when remote pulls no longer include them.\n- Add detail-screen clear rating action and update docs.\n\n## Risks and follow-up\n\n- The clear action is on the detail screen for MVP2. Adding a direct overview clear affordance can be a later UX improvement if needed.
<!-- SECTION:NOTES:END -->

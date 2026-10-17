---
id: task-44
title: 'US-044: Adapt rating scale to 1-5 with unrated zero'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-02 05:45'
updated_date: '2026-06-02 05:59'
labels:
  - ratings
  - business-rules
  - ui
  - database
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a festival attendee, I want band ratings to use the updated 1-5 business scale with 0 meaning unrated so that veto, indifferent, like, want-to-see, and must-see ratings match the current business rules.

Business value:
- Aligns the implemented rating workflow with the updated business source of truth.
- Prevents users from accidentally using 0 as veto now that 0 means no rating has been given.
- Keeps group decision rules, sync, and stored rating data consistent across Android, Room, TSV fallback, and Supabase.

In scope - what needs to change:
- Domain Rating value object must accept 0-5 where 0 is reserved for unrated/no explicit rating and 1-5 are explicit user ratings.
- Application listing/detail behavior must default unrated bands to 0 and treat 0 as no explicit rating/no stars at rest.
- RateBandUseCase and validation must reject values outside 0-5 and enforce that user-selected ratings are 1-5 unless explicitly representing no rating.
- RatingStarsView and overview/detail UI must show five selectable stars that save 1-5, not 0-4; unrated/default state shows no filled stars until hover/focus.
- Overview and detail screens must preserve the hover/focus fill-left-to-right behavior using the new 1-5 scale.
- Supabase ratings constraint and any Flyway migration must allow 1-5 explicit ratings and keep 0 out of persisted explicit backend ratings unless an intentional unrated storage strategy is approved.
- Room/local rating persistence and pending sync behavior must be compatible with the new scale and existing migrated data.
- TSV fallback/file-backed rating persistence must read/write the new scale.
- Tests, QA scenarios, and documentation references must be updated from 0-4/default 1 to 1-5/default 0.

Out of scope:
- Changing unrelated band import, auth, or Supabase admin workflows.
- Implementing the final group decision engine if it is not already part of the current app behavior.
- Redesigning the rating UI beyond scale semantics and required labels/behavior.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given a band has no explicit rating, when it is shown in the overview or detail screen, then the effective rating is 0 and no stars are filled at rest.
- [x] #2 Given the user hovers or focuses an unrated rating control, when the pointer moves across the stars, then the preview fills left-to-right from 1 through 5.
- [x] #3 Given the user selects a star in the overview or detail screen, when the rating is saved, then the stored explicit rating is 1, 2, 3, 4, or 5 matching the selected star count.
- [x] #4 Given code attempts to create or save a rating outside the allowed range, when validation runs, then values below 0 or above 5 are rejected with a clear error.
- [x] #5 Given group decision logic or tests refer to rating meanings, then 1 means veto, 2 means OK/indifferent, 3 means like/fine to miss, 4 means want to see, and 5 means must see.
- [x] #6 Given existing local or backend rating data exists from the previous 0-4 scale, then implementation explicitly handles migration/compatibility or documents why old data must be reset before release.
- [x] #7 Given Supabase rating sync is used, then backend constraints, Android sync code, and tests allow the new 1-5 explicit rating values and do not treat 0 as veto.
- [x] #8 Automated tests are updated for domain validation, application default rating behavior, overview/detail rating behavior where testable, Room/persistence behavior, and Supabase rating sync mapping.
- [x] #9 README and business requirements impact use the canonical wording from delivery-governance.md, and README public behavior is updated to describe the new scale.
- [x] #10 Manual validation steps are documented for rating an unrated band, saving 1 and 5, syncing ratings, and verifying existing ratings after upgrade.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Updated domain and application tests for the 1-5 explicit scale, unrated default 0, invalid high/low validation, and explicit-save rejection of 0.
2. Changed domain Rating to allow 0-5 while RateBandUseCase only saves explicit user ratings from 1-5.
3. Changed EffectiveRatingResolver so missing ratings resolve to effective 0 with default/unrated state.
4. Updated RatingStarsView so five visible star positions save/preview ratings 1 through 5 and unrated controls rest blank.
5. Added Room database version 3 migration to shift old local explicit 0-4 ratings to 1-5, and added Supabase Flyway V005 to shift backend rows and constrain explicit backend ratings to 1-5.
6. Guarded Supabase rating push against syncing 0 as an explicit backend rating.
7. Updated README, business requirements, and one-group rating sharing docs for the new scale and migration/reset guidance.
8. Ran focused and full Gradle validation successfully. Flyway info/migrate could not connect to Supabase from this machine due NoRouteToHost, so live DB application remains a manual release validation item.

Architecture impact: database constraint changed inside the existing approved Room/Supabase/Flyway persistence approach; no new architecture style, adapter, dependency, or ADR was introduced.
Deviation: RateBandUseCase was tightened to reject 0 as an explicit user selection so 0 remains only unrated/no explicit rating.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Implemented the rating-scale change from 0-4/default 1 to 1-5/default 0. Domain Rating now allows 0-5, application defaults unresolved ratings to 0, and RateBandUseCase rejects 0 as an explicit user selection so 0 remains only the unrated/no-explicit-rating state.

The Android star view now supports five selectable positions, preserving blank resting stars for unrated bands. Room database version 3 migrates old local explicit 0-4 values to 1-5 by adding 1. Supabase Flyway migration V005 applies the same conversion and constrains backend explicit ratings to 1-5; Android sync also refuses to push 0 as an explicit rating.

## Acceptance criteria validation

- AC1: Covered by EffectiveRatingResolver/ListBands/ShowBandDetail tests: missing ratings resolve to 0 and default/unrated state.
- AC2: RatingStarsView now has five star positions with MAX_RATING 5 and keeps hover/touch preview left-to-right from the pointer-derived 1-5 value. Manual UI validation still needed on device/emulator.
- AC3: Overview/detail save paths use RateBandUseCase, which now stores only explicit 1-5 values.
- AC4: Domain tests reject below 0 and above 5 with clear validation; RateBandUseCase tests reject explicit 0 and invalid high/low values.
- AC5: Business requirements now define 1 veto, 2 indifferent, 3 like, 4 want to see, 5 must see. No final group decision engine is in scope yet.
- AC6: Existing Room and Supabase old-scale explicit ratings are migrated by +1; TSV fallback old rating files are documented as requiring clear/regenerate because they have no schema version.
- AC7: Supabase migration and Android sync code allow 1-5 explicit ratings and reject 0 as explicit backend data. Live Supabase validation could not run due network NoRouteToHost.
- AC8: Updated domain, application, QA scenario, infrastructure/file-backed, and Android JVM validation coverage where available; there is no existing Android instrumentation/Robolectric setup for direct hover UI tests.
- AC9: README and business requirements were updated.
- AC10: Manual validation steps are listed below.

## How to test

### Automated tests

- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:test :application:test :application:qaTest :infrastructure:test :app:testDebugUnitTest` - passed.
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew test qaTest assembleDebug` - passed.

### Manual validation

1. Install the debug APK, open the band overview, and verify unrated bands show no filled stars at rest.
2. Hover or drag across an unrated rating control and verify stars preview/fill left-to-right from 1 through 5.
3. Save rating 1 for a band and verify it persists after navigating away/back and sync does not treat 0 as veto.
4. Save rating 5 for a band and verify the overview and detail screen show five filled stars.
5. Upgrade from an app build with old local ratings and verify old 0,1,2,3,4 explicit rows appear as 1,2,3,4,5 after Room migration.
6. When Supabase is reachable, run `backend/flyway/run-flyway.sh info`, `backend/flyway/run-flyway.sh migrate`, and `backend/flyway/run-flyway.sh info`; verify `V005__rating_scale_1_to_5.sql` is applied and backend ratings reject 0 but accept 1 and 5.

## TDD / BDD / approval-test evidence

Updated tests first for the changed business behavior in Rating, EffectiveRatingResolver, RateBandUseCase, ListBandsUseCase, ShowBandDetailUseCase, and the QA listing/rating scenario. Existing infrastructure and Android JVM tests were rerun as regression coverage.

## Architecture impact

- Architecture-significant change: yes, database constraints and Room schema version changed.
- Approval received: user approved starting implementation for this story; change stays within the already selected Room/Supabase/Flyway persistence approach and does not add a new architecture decision.
- ADR: none, because this changes a business-rule constraint inside the existing persistence strategy rather than changing technology, boundaries, or schema-management approach.

## README impact

README impact: updated public rating behavior and rating-scale migration guidance.

## Business requirements impact

Business requirements impact: updated rating rules, terminology, and related examples to 1-5/default 0.

## Diagram impact

Diagram impact: none, because module/container relationships did not change.

## Commits / logical change list

- Domain/application rating scale and default behavior.
- Android star view and Supabase explicit-rating guard.
- Room and Flyway migrations for old explicit rating data.
- Tests and docs for the new scale.

## Risks and follow-up

Flyway could not connect to Supabase from this machine/network (`NoRouteToHost`) in both sandboxed and escalated runs, so applying and validating V005 against the live database remains a release validation item. Direct hover behavior still needs device/emulator validation because this repo has no Android UI test framework configured.
<!-- SECTION:NOTES:END -->

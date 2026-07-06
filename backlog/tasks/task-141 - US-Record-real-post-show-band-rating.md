---
id: task-141
title: 'US: Record real post-show band rating'
status: Done
assignee:
  - '@codex'
created_date: '2026-07-02 08:26'
updated_date: '2026-07-06 08:09'
labels:
  - mvp3
  - ratings
  - offline
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Business value: after seeing a band live, users need to capture how good the performance really was without rewriting the planning preference used for the group schedule.

As a Wacken Planner user, I want to add a real post-show rating on the band detail screen, so that I can compare my planning expectation with the actual performance.

Scope: band detail UI, separate real-rating persistence, reset to unrated, offline local save, and display/export availability.

Out of scope: using real ratings in MVP2 schedule decision rules or replacing planning ratings.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given a band detail screen, when I set a real post-show rating from 1 to 5, then it is saved separately from my planning rating
- [x] #2 Given I reset the real post-show rating, when I return to the band detail screen, then the real rating is unrated and my planning rating is unchanged
- [x] #3 Given I am offline, when I set or reset a real post-show rating, then the change is saved locally and remains visible after reopening the app
- [x] #4 Given a real rating exists, when ratings are exported, then the real rating is available to the export story
- [x] #5 Given schedule generation runs, then real post-show ratings do not change planning decisions
- [x] #6 Automated tests cover separation between planning rating and real post-show rating
- [x] #7 Manual test steps are documented in implementation notes
- [x] #8 Business requirements and README impact use canonical delivery-governance wording
- [x] #9 Architecture impact is assessed before implementation; if a new Room/Supabase schema is needed, explicit approval and ADR handling are completed before coding
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Inspected the existing planning-rating domain/application/UI/persistence flow and confirmed schedule generation only reads planning RatingRepository.
2. Received user approval for the standard architecture option: add a separate local-only Room persistence model for real post-show ratings.
3. Added RealRatingRepository and RateRealBandUseCase with tests for save, reset to unrated, export availability, and separation from planning ratings.
4. Extended band detail application data to expose real rating separately from planning rating and added a use-case test for independent display.
5. Added Room real_ratings entity/DAO/repository and migration 4->5, wired AppRepositories, and added a separate Real Rating section on band detail.
6. Updated README and business requirements to document local/offline-first real ratings and the later-sync open question.
7. Ran domain/application/app/multi-module validation, debug build, signed release build, APK signature verification, hash capture, and diff hygiene.

Architecture impact: architecture-significant and approved by the user on 2026-07-06 with the standard option. The change adds a local Room table and repository for real ratings, but does not change Supabase schema/sync, module boundaries, or schedule decision rules. ADR impact: none, because this applies the existing Room/offline-first pattern and records the local-only decision in task notes and docs.
Deviation: physical device offline persistence testing was not run in this environment.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

- Added separate real post-show ratings using a new local-only `RealRatingRepository` and Room `real_ratings` table.
- Added `RateRealBandUseCase` and exposed real rating values on band detail without changing planning ratings.
- Added a separate Real Rating section on the Android band detail screen with 1-5 stars and Reset to unrated.
- Kept real ratings out of Supabase planning-rating sync and out of schedule generation.
- Updated README and business requirements for the new local/offline-first behavior.

## Acceptance criteria validation

- AC1: `RateRealBandUseCaseTest` proves setting a real rating saves it separately from the planning rating.
- AC2: `RateRealBandUseCaseTest` proves reset stores unrated `0`; `ShowBandDetailUseCase` treats stored `0` as unrated when reopening detail.
- AC3: Real ratings are stored in local Room only, with no Supabase dependency; physical airplane-mode testing was not run in this environment.
- AC4: `RealRatingRepository.findAll()` and `RateRealBandUseCaseTest` expose saved real ratings for the upcoming export story.
- AC5: Schedule generation remains wired only to planning `RatingRepository`; no schedule code reads `RealRatingRepository`.
- AC6: Automated tests cover planning/real rating separation, reset, validation, export availability, and band-detail display.
- AC7: Manual test steps are documented below.
- AC8: README and business requirements were updated with canonical impact notes below.
- AC9: Architecture impact was assessed before coding; user approved the standard local Room table/repository option. No Supabase schema was added.

## How to test

### Automated tests

- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:test :application:test`
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:testDebugUnitTest :app:compileDebugJavaWithJavac`
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac assembleDebug`
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) WACKEN_RELEASE_* ./gradlew assembleRelease` with local keychain-backed signing values
- `git diff --check`

### Manual validation

- Open a band detail screen, set planning rating to 5, set Real Rating to 3, leave and reopen detail, and confirm both values remain distinct.
- Tap Reset in the Real Rating section, reopen detail, and confirm real rating is unrated while planning rating remains unchanged.
- Repeat the same real-rating set/reset flow with the device offline after cached data exists.
- Signed APK verification: `apksigner verify --verbose app/build/outputs/apk/release/app-release.apk` verifies v1 and v2 signatures with one signer.
- APK metadata: package `be.wacken.planner`, versionCode `23`, versionName `2.20`, minSdkVersion `23`.
- Debug APK SHA-256: `128a05e7a7987727bc0b7625c3819fa1303c39c0ea77bb4a8bd6fbaeca2c23ed`.
- Release APK SHA-256: `d68821070725324b4bf9c828f6be9cd6035de494e80cf79ec7bef790c01c4daf`.
- Physical device/manual UI validation was not run in this environment.

## TDD / BDD / approval-test evidence

- Added focused tests for the new real-rating behavior before final wiring.
- BDD acceptance is represented by the band-detail and use-case tests for set, reset, separation, and export availability.
- No approval tests were needed because this was new behavior rather than legacy refactoring.

## Architecture impact

- Architecture-significant change: yes, because a new Room table and persistence model were added.
- Approval received: yes, user approved the standard option on 2026-07-06.
- ADR: none, because this uses the existing Room/offline-first pattern and does not change Supabase sync strategy, module boundaries, or public integration contracts.

## README impact

README impact: updated Basic Functionality and repository responsibility text for separate local/offline-first real post-show ratings.

## Business requirements impact

Business requirements impact: updated current MVP3 capability wording and the real-rating sync open question to state current local/personal behavior.

## Diagram impact

Diagram impact: none, because module/container relationships did not change.

## Commits / logical change list

- Added real-rating domain repository and application use case.
- Added local Room `real_ratings` persistence and database migration 4->5.
- Added Real Rating controls to band detail.
- Added tests for real/planning separation, reset, export availability, and detail display.
- Updated README and business requirements.

## Risks and follow-up

- Physical Android offline smoke testing should be run before festival use.
- Real ratings intentionally do not sync to Supabase in this story; a future story can decide and implement shared sync if needed.
<!-- SECTION:NOTES:END -->

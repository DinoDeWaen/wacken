---
id: task-154
title: 'US: Prefill new festival planning ratings from personal band ratings'
status: Done
assignee:
  - '@codex'
created_date: '2026-08-12 07:53'
updated_date: '2026-08-13 05:40'
labels:
  - user-story
  - post-mvp3
  - ratings
  - prefill
  - festivals
dependencies:
  - task-151
  - task-152
  - task-153
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a group member, I want known bands in a newly added festival to start with my latest personal band rating so that planning begins with useful defaults.

Business value: Repeated bands become faster to plan while still allowing festival-specific planning choices.

Scope: when a new festival lineup links to existing bands by exact name, prefill the current user's festival planning rating from their latest personal band rating event by created date; leave unknown bands unrated; allow editing the prefilled planning rating without changing personal history.

Out of scope: prefilling from older festival planning ratings, fuzzy alias matching, bulk review grid.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given a known band has no personal rating history for the user, then its planning rating remains unrated.
- [x] #2 Given a user edits a prefilled planning rating, then the edited value applies only to that festival planning rating.
- [x] #3 Given older festival planning ratings exist, then they are not used as the prefill source unless a later requirement changes this rule.
- [x] #4 Domain/application tests cover latest-personal-rating selection, no-history behavior, and independence from planning ratings; BDD covers a new festival import with known and unknown bands.
- [x] #5 README, business requirements, diagram, and ADR impact are recorded using delivery-governance wording.
- [x] #6 Given a newly imported festival lineup contains an exact-name match for a band with personal rating history, then the planning rating for that user is prefilled from the latest personal band rating event by created date.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Added tests for latest personal rating prefill, no-history behavior, and independence from planning ratings.
2. Wired add-festival import to prefill festival planning ratings for exact-name known bands.
3. Kept prefilled planning ratings editable through the active-festival planning rating repository without mutating personal history.
4. Added QA scenario for a new festival import with known and unknown bands.
5. Updated README and business requirements and ran full validation.

Deviation: implemented with tasks 151-153 because prefill runs inside the add-festival use case and depends on personal rating history plus festival planning ratings. Architecture approval was previously received for ADR 0011 standard option.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Implemented planning-rating prefill from personal band history. When a new festival lineup contains an exact-name known band, the current user gets a festival planning rating from their latest personal band rating event by created date. Unknown bands or known bands without personal history remain unrated.

## Acceptance criteria validation

- AC1: AddFestivalUseCaseTest verifies unknown/no-history bands stay unrated.
- AC2: planning ratings are normal active-festival ratings and remain editable independently of personal history.
- AC3: prefill uses PersonalBandRatingHistoryRepository.latestByUserAndBand, not older planning ratings.
- AC4: AddFestivalUseCaseTest, FestivalRatingIndependenceTest, and FestivalArchiveQaScenarioTest cover the behavior.
- AC5: documentation and impact notes recorded here.
- AC6: exact-name Airbourne prefill from latest personal event is covered by unit and QA tests.

## How to test

### Automated tests

- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:test :application:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac qaTest assembleDebug

### Manual validation

- Give Airbourne a real rating at Wacken, archive Wacken, add a new festival with Airbourne in bands.csv, and confirm the new festival planning rating is prefilled.

## TDD / BDD / approval-test evidence

- Added application tests for latest personal rating prefill and no-history behavior.
- Added QA scenario for importing a new festival with known and unknown bands.

## Architecture impact

- Architecture-significant change: yes, persistence/schema was part of the approved festival rating model.
- Approval received: yes, user approved ADR 0011 standard option.
- ADR: ADR impact: none, because ADR 0011 already records the approved prefill source and rating separation.

## README impact

README impact: updated current behavior and repository/backend notes.

## Business requirements impact

Business requirements impact: updated implemented post-MVP3 capability status.

## Diagram impact

Diagram impact: none, because existing module diagrams still describe the same container/module boundaries.

## Commits / logical change list

- Add latest personal rating prefill in AddFestivalUseCase and tests.
- Keep active-festival planning ratings independent after prefill.

## Risks and follow-up

- Prefill uses exact canonical band names only; aliases and fuzzy suggestions remain future work.
<!-- SECTION:NOTES:END -->

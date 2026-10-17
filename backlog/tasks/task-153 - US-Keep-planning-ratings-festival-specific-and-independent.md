---
id: task-153
title: 'US: Keep planning ratings festival-specific and independent'
status: Done
assignee:
  - '@codex'
created_date: '2026-08-12 07:53'
updated_date: '2026-08-13 05:39'
labels:
  - user-story
  - post-mvp3
  - ratings
  - supabase
  - schedule
dependencies:
  - task-149
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a group member, I want my planning rating for a band to belong to the current festival so that festival-specific reasons to see a band do not change my long-term personal band rating.

Business value: Group schedule decisions can use the rating for the active festival while preserving honest personal ratings separately.

Scope: store and sync per-user, per-festival, per-band planning ratings; keep group schedule rules based on planning ratings for the active festival; ensure changing planning ratings never creates or overwrites personal band rating events.

Out of scope: using personal ratings directly in the schedule decision engine, multiple active festivals, group-average personal rating views.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given a user rates a band for festival planning, then the rating is stored for that user, active festival, and band.
- [x] #2 Given the planning rating syncs, then Supabase stores it separately from personal band rating events.
- [x] #3 Given a band has a personal rating history, when the user changes the current festival planning rating, then no personal band rating event is changed or created.
- [x] #4 Given the group schedule is generated, then the active festival planning ratings remain the ratings used for group decisions.
- [x] #5 Domain/application tests cover planning-versus-personal independence and BDD covers changing a festival planning rating for contextual reasons.
- [x] #6 README, business requirements, diagram, and ADR impact are recorded using delivery-governance wording.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Added festival planning rating domain model/repository and tests.
2. Stored planning ratings by group/user/festival/band separately from personal rating events.
3. Wired active-festival planning ratings into existing overview, detail, and schedule-compatible rating flows.
4. Added Supabase schema/client mapping and sync tests.
5. Updated README and business requirements and ran full validation.

Deviation: implemented together with tasks 151, 152, 154, and 155 because the active-festival rating repository is the shared compatibility point. Architecture approval was previously received for ADR 0011 standard option.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Implemented festival-specific planning ratings. Existing rating flows now resolve through the active festival, while personal band history remains independent and is created only from real post-show rating events. Supabase and Room now store planning ratings separately from personal rating events.

## Acceptance criteria validation

- AC1: ActiveFestivalRatingRepository saves by user, active festival, and band.
- AC2: Supabase migration/client uses festival_planning_ratings separately from personal_band_rating_events.
- AC3: FestivalRatingIndependenceTest verifies planning changes do not create personal events.
- AC4: overview/detail/schedule-compatible rating repository still exposes active festival ratings through the existing RatingRepository contract.
- AC5: FestivalRatingIndependenceTest and QA scenario cover planning/personal independence and contextual planning rating behavior.
- AC6: documentation and impact notes recorded here.

## How to test

### Automated tests

- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:test :application:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac qaTest assembleDebug

### Manual validation

- Rate a band on the active festival, then set a real rating and confirm planning and personal history are shown separately.

## TDD / BDD / approval-test evidence

- Added application tests for planning-versus-personal independence.
- Added app sync/mapping tests for festival planning rating sync.
- QA scenario covers festival-specific planning prefill and editability context.

## Architecture impact

- Architecture-significant change: yes, persistence/schema and rating repository boundary.
- Approval received: yes, user approved ADR 0011 standard option.
- ADR: ADR impact: none, because ADR 0011 already records the approved planning rating model.

## README impact

README impact: updated current behavior, repository responsibilities, and backend sync/database notes.

## Business requirements impact

Business requirements impact: updated implemented post-MVP3 capability status.

## Diagram impact

Diagram impact: none, because existing module diagrams still describe the same container/module boundaries.

## Commits / logical change list

- Add festival planning rating model, Room table, Supabase migration, active-festival compatibility repository, sync client/decorator, and tests.

## Risks and follow-up

- Multiple active/upcoming festivals remain out of first-version scope.
<!-- SECTION:NOTES:END -->

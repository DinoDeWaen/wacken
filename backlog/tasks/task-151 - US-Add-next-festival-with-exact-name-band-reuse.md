---
id: task-151
title: 'US: Add next festival with exact-name band reuse'
status: Done
assignee:
  - '@codex'
created_date: '2026-08-12 07:53'
updated_date: '2026-08-13 05:39'
labels:
  - user-story
  - post-mvp3
  - festivals
  - bands
  - import
dependencies:
  - task-149
  - task-150
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a group member, I want to add the next festival after archiving the current one so that planning can restart without losing known bands.

Business value: The app can move from Wacken to a later festival such as Rock am Ring or Rock im Park while reusing band knowledge over time.

Scope: add a festival only when no active festival exists, upload/import that festival lineup, reuse existing band records by exact name, create new band records for unmatched names, and make the new festival active.

Out of scope: adding a second upcoming festival while one is active, fuzzy matching, alias confirmation, editing archived festival data.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given no active festival exists, when a user adds a new festival with lineup data, then the festival becomes the active festival.
- [x] #2 Given an active festival already exists, when a user tries to add another upcoming festival, then the first post-Wacken version prevents it.
- [x] #3 Given an imported lineup contains a band name that exactly matches an existing band, then the lineup entry links to the existing band record.
- [x] #4 Given an imported lineup contains a band name with no exact existing match, then a new band record is created.
- [x] #5 Fuzzy matching and alias storage are not implemented in this story and remain future scope.
- [x] #6 Domain/application tests cover band reuse and the one-active-festival constraint; BDD covers adding a next festival after archive.
- [x] #7 README, business requirements, diagram, and ADR impact are recorded using delivery-governance wording.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Added festival lineup domain/application model and add-festival tests.
2. Implemented one-active-festival guard, exact-name band reuse, unmatched band creation, and lineup persistence.
3. Wired Android add-festival mode from the no-active start state and added Supabase/Room lineup schema.
4. Added QA scenario coverage for adding a next festival after archive.
5. Updated README and business requirements.
6. Ran full validation.

Deviation: implementation was delivered together with tasks 152-155 because the rating-history model, prefill, and archive view share the same persistence and active-festival boundary. Architecture approval was previously received for ADR 0011 standard option.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Implemented adding the next festival after archive. The app now supports a band-CSV add-festival mode when no active festival exists, reuses exact-name bands, creates unmatched band records, stores festival lineup entries, and makes the new festival active.

## Acceptance criteria validation

- AC1: covered by AddFestivalUseCaseTest and FestivalArchiveQaScenarioTest.
- AC2: covered by AddFestivalUseCaseTest preventsAddingFestivalWhenOneIsAlreadyActive.
- AC3: covered by exact-name Airbourne reuse in unit and QA tests.
- AC4: covered by New Act creation in unit and QA tests.
- AC5: fuzzy matching and aliases remain out of scope; exact-name matching only.
- AC6: domain/application and QA scenario coverage added.
- AC7: documentation and impact notes recorded here.

## How to test

### Automated tests

- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:test :application:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac qaTest assembleDebug

### Manual validation

- Archive the active festival, tap add festival, select a bands.csv, and confirm the new festival starts as active.

## TDD / BDD / approval-test evidence

- Added application unit tests first for add-festival constraints and exact-name reuse.
- Added QA scenario for adding a next festival after archive with known and unknown bands.

## Architecture impact

- Architecture-significant change: yes, persistence/schema and repository ports.
- Approval received: yes, user approved ADR 0011 standard option.
- ADR: ADR impact: none, because ADR 0011 already records the approved standard architecture direction.

## README impact

README impact: updated current behavior, repository responsibilities, and backend sync/database notes.

## Business requirements impact

Business requirements impact: updated implemented post-MVP3 capability status.

## Diagram impact

Diagram impact: none, because existing module diagrams still describe the same container/module boundaries.

## Commits / logical change list

- Add festival lineup model and Room/Supabase schema.
- Add add-festival use case and Android import mode.
- Add QA coverage and documentation updates.

## Risks and follow-up

- Fuzzy matching, aliases, and multiple upcoming festivals remain future tasks by design.
<!-- SECTION:NOTES:END -->

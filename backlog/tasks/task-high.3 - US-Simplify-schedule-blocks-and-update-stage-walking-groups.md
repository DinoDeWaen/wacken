---
id: task-high.3
title: 'US: Simplify schedule blocks and update stage walking groups'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-12 13:04'
updated_date: '2026-06-12 13:15'
labels:
  - user-story
  - ui
  - schedule
dependencies: []
parent_task_id: task-high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a festival attendee viewing the stage-column schedule, I want performance blocks to stay compact and travel times to reflect the real nearby stage groups, so the schedule remains readable and movement guidance is accurate.

In scope:
- Remove the stage name from schedule performance blocks because the stage is already represented by the column.
- Render lost-alternative text smaller than the main act details.
- Update default walking-time groups: Harder, Faster, and Louder are mutually 5 minutes apart; Headbangers Stage and W:E:T Stage are 5 minutes apart; walking between those groups or to other stages defaults to 15 minutes unless the stages are the same.

Out of scope:
- Persisting custom stage distances in Supabase.
- Changing the conflict-resolution algorithm beyond the walking-time policy it already consumes.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given the schedule is shown in stage columns, when a performance block is rendered, then the block does not repeat the stage name.
- [x] #2 Given a selected act has a lost alternative, when the performance block is rendered, then the lost-alternative line is visually smaller than the main band text.
- [x] #3 Given travel is between Harder, Faster, and Louder in any direction, when default walking time is calculated, then it is 5 minutes.
- [x] #4 Given travel is between Headbangers Stage and W:E:T Stage in either direction, when default walking time is calculated, then it is 5 minutes.
- [x] #5 Given travel is between the Harder/Faster/Louder group and the Headbangers/W:E:T group or another stage, when default walking time is calculated, then it is 15 minutes unless both performances are on the same stage.
- [x] #6 Automated tests prove the walking-time groups and schedule block text behavior.
- [x] #7 README, business requirements, diagram, and ADR impact are recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Update domain walking-time policy and tests for the requested nearby stage groups: Harder/Faster/Louder and Headbangers/W:E:T.
2. Update schedule block content/tests so stage names are not repeated inside stage-column blocks.
3. Adjust Android block typography so lost alternatives render smaller than the main band line.
4. Update business requirements for the refined block content and walking-time defaults; README/diagram/ADR expected no impact.
5. Run focused domain/app tests, Android compile, and full relevant validation.
6. Build and verify a fresh local signed APK if validation passes, then close the story with evidence.
Architecture impact: not architecture-significant; this keeps walking-time policy in the domain and visual rendering in Android UI. No ADR expected.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Simplified stage-column schedule blocks by removing the repeated stage line from each performance block and rendering lost alternatives as smaller secondary text. Updated the domain walking-time policy so Harder, Faster, and Louder are a 5-minute walking group; Headbangers Stage and W:E:T/WET Stage are a 5-minute walking group; travel between groups or to other stages defaults to 15 minutes unless the stage is the same.

## Acceptance criteria validation

- AC1: Schedule blocks no longer render the stage name; the stage is represented by the column header.
- AC2: Lost-alternative text is rendered at 11sp, smaller than the 16sp main band line.
- AC3: Domain tests cover Harder, Faster, and Louder as 5 minutes apart in both plain and `Stage`-suffixed names.
- AC4: Domain tests cover Headbangers Stage to W:E:T/WET Stage as 5 minutes in both directions.
- AC5: Domain and application tests cover 15-minute travel between stage groups and same-stage 0-minute travel.
- AC6: Automated tests cover walking-time groups and schedule block text behavior.
- AC7: Impact notes recorded below.

## How to test

### Automated tests

- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:testDebugUnitTest --tests be.wacken.planner.ScheduleBlockContentTest
- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:compileDebugJavaWithJavac
- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac

### Manual validation

- Built signed release APK at app/build/outputs/apk/release/app-release.apk.
- Verified package be.wacken.planner versionName 2.9 versionCode 12 using aapt.
- Verified APK signature with apksigner: v1=true, v2=true.
- SHA-256: 5ef53f1cfcf25e68408db92687d4992a9768b0ce53cdcfeea92b9030bf890f13.

## TDD / BDD / approval-test evidence

Updated StageWalkingTimePolicyTest first for the requested nearby-stage groups; it failed against the old policy before implementation. Updated ScheduleBlockContentTest for the block text behavior and GenerateSharedScheduleUseCaseTest for application-level walking evidence. No approval baseline was needed because this is an intentional behavior change.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR: none.

## README impact

README impact: none, because setup, commands, architecture, and high-level schedule capability descriptions did not change.

## Business requirements impact

Business requirements impact: updated BR-064, BR-073, and BR-074 for simplified stage-column block content and the refined walking-time defaults.

## Diagram impact

Diagram impact: none, because this changes domain policy values and Android presentation details without changing architecture or workflows.

## Commits / logical change list

- Remove repeated stage names from stage-column schedule blocks.
- Render lost alternatives smaller than main band text.
- Update default stage walking groups in the domain policy.
- Update domain, application, and app tests for the new behavior.
- Update business requirements for the new public behavior.

## Risks and follow-up

Low risk. The walking-time policy normalizes `Stage` suffixes and punctuation so both `W:E:T Stage` and `WET Stage` map to the same nearby-stage group.
<!-- SECTION:NOTES:END -->

---
id: task-high.5
title: 'US: Highlight schedule blocks by rating and skipped overlaps'
status: In Progress
assignee:
  - '@codex'
created_date: '2026-06-12 14:57'
updated_date: '2026-06-12 15:05'
labels:
  - user-story
  - ui
  - schedule
dependencies: []
parent_task_id: task-high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a festival attendee using the group schedule, I want selected acts and skipped long-overlap alternatives to be visually obvious in the stage-column calendar, so I can quickly see must-see acts, weak winners, and conflicts where another act was skipped.

In scope:
- Use a gold border for visible schedule blocks where the selected/visible act rating is 5 stars.
- Use a light grey border for visible schedule blocks where the selected/visible act rating is 2 stars.
- Keep the existing red accent border for other visible schedule blocks.
- When the visible schedule block has a lost alternative that overlaps it by more than 15 minutes, draw broad light diagonal scratch bands over the block using a lighter version of the block border color.

Assumption:
- The current schedule overview only renders the visible selected act, not separate blocks for rejected alternatives. Therefore the scratch indication is applied to the visible block that caused a long-overlap alternative to be skipped.

Out of scope:
- Changing conflict resolution or adding separate alternative blocks to the overview.
- Persisting any visual state.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given a visible schedule act has rating 5, when the block is rendered, then the border is gold.
- [x] #2 Given a visible schedule act has rating 2, when the block is rendered, then the border is light grey.
- [x] #3 Given a visible schedule act has any other rating, when the block is rendered, then the border remains the existing red accent.
- [x] #4 Given a visible block has a lost alternative overlapping by more than 15 minutes, when the block is rendered, then broad light diagonal scratch bands are drawn over the block in the border color family.
- [x] #5 Given a visible block has no lost alternative or the overlap is 15 minutes or less, when the block is rendered, then no scratch bands are drawn.
- [x] #6 Automated tests cover the border and scratch styling decisions.
- [x] #7 Business requirements, README, diagram, and ADR impact are recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Add focused app unit tests for schedule block border tone and long-overlap scratch decisions.
2. Add a small ScheduleBlockStyle classifier in the Android app module that derives border tone and scratch state from the visible candidate and slot candidates.
3. Replace the fixed schedule block background with a custom drawable that draws the selected border color and optional broad diagonal scratch bands.
4. Update business requirements for schedule block visual highlights; README, diagrams, and ADR expected no impact.
5. Run focused app tests, Android compile, and full relevant validation.
6. Build a fresh local signed APK for device testing, then close the story with validation evidence and commit.
Architecture impact: not architecture-significant; this is Android presentation behavior with no domain/application rule changes. No ADR expected.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Added schedule block visual highlighting for rating strength and long-overlap skipped alternatives. Visible 5-star acts now classify as gold-border blocks, visible 2-star acts classify as light-grey-border blocks, and all other visible acts keep the red accent border. Blocks with a lost alternative overlapping more than 15 minutes draw broad light diagonal scratch bands in the block border color family.

The schedule overview currently renders only the visible selected/manual act, not separate rejected-alternative blocks. Per the task assumption, the scratch indication is applied to the visible block that caused a long-overlap alternative to be skipped.

## Acceptance criteria validation

- AC1: ScheduleBlockStyleTest covers 5-star visible acts selecting the GOLD border tone.
- AC2: ScheduleBlockStyleTest covers 2-star visible acts selecting the LIGHT_GREY border tone.
- AC3: ScheduleBlockStyleTest covers other visible ratings selecting the RED border tone.
- AC4: ScheduleBlockStyleTest covers a lost alternative with more than 15 minutes overlap setting scratched=true; ScheduleActivity draws broad diagonal scratch bands when scratched=true.
- AC5: ScheduleBlockStyleTest covers no scratch when the lost-alternative overlap is exactly 15 minutes or when no lost alternative exists.
- AC6: Automated tests cover border and scratch styling decisions.
- AC7: Impact notes recorded below.

## How to test

### Automated tests

- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:testDebugUnitTest --tests be.wacken.planner.ScheduleBlockStyleTest --tests be.wacken.planner.ScheduleBlockContentTest
- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:compileDebugJavaWithJavac
- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac

### Manual validation

- Built signed local release APK at app/build/outputs/apk/release/app-release.apk.
- Verified package be.wacken.planner versionName 2.10 versionCode 13 using aapt.
- Verified APK signature with apksigner: v1=true, v2=true.
- SHA-256: 6523de9513b05e7fca6224099fb84e705c26e9676c20e343270e652a8d9e188e.
- git diff --check passed.

## TDD / BDD / approval-test evidence

ScheduleBlockStyleTest was added before the classifier existed and failed at compile time, then passed after implementation. The behavior is presentation-only and covered by focused app unit tests; no domain/application scheduling behavior changed.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR: none.

## README impact

README impact: none, because setup, commands, architecture, and high-level capability descriptions did not change.

## Business requirements impact

Business requirements impact: updated BR-064b for rating-based borders and long-overlap scratch styling.

## Diagram impact

Diagram impact: none, because this changes Android presentation styling without changing architecture or workflows.

## Commits / logical change list

- Add ScheduleBlockStyle classifier for border tone and scratch decisions.
- Add focused tests for 5-star, 2-star, default-border, and overlap scratch behavior.
- Draw schedule block backgrounds with selected border color and optional broad diagonal scratch bands.
- Add BR-064b.

## Risks and follow-up

- The overview still does not draw rejected alternatives as separate blocks; scratches mark the visible selected block that skipped a long-overlap alternative.
- Visual density of scratch bands should be reviewed on-device because line width and spacing are intentionally bold.
<!-- SECTION:NOTES:END -->

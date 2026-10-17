---
id: task-78
title: 'DEF: Keep short schedule blocks readable'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-10 17:04'
updated_date: '2026-06-10 17:12'
labels:
  - defect
  - ui
  - schedule
  - mvp2
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Short schedule overview blocks can clip or hide content because the block tries to show band, stars, stage, time, GO/OPTIONAL status, lost alternative, and walking time inside a small height. Longer events look acceptable, but short events lose important information.

As a festival attendee, I want short schedule blocks to remain readable so that every act can be inspected without clipped text or missing elements.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given the schedule overview is shown, when a performance block is rendered, then GO or OPTIONAL is not shown inside the block.
- [x] #2 Given a performance block has a start and end time, when the calendar is rendered, then the time range is shown on the time scale next to the block rather than inside the block.
- [x] #3 Given a performance block is longer, when it renders with extra vertical space, then the layout remains consistent with short blocks and does not reintroduce unnecessary GO/OPTIONAL text.
- [x] #4 Given a block has a lost alternative, when there is not enough space in the overview block, then the overview keeps the block readable and the full alternative detail remains available by tapping the block.
- [x] #5 Automated tests or focused compile checks protect the layout behavior, and installed-device visual validation is documented.
- [x] #6 Business requirements and README impact are recorded using the canonical wording from delivery-governance.md.
- [x] #7 Given a short performance block is shown in the group schedule, when the available block height cannot fit all schedule details, then the block still shows the band name, rating stars, and stage without clipped text, and walking information is shown as a separate marker between blocks.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Inspected ScheduleActivity and ScheduleCalendarLayout tests around block positioning and content.
2. Added focused layout/helper tests for event end offsets, walking-marker position, and adaptive block content.
3. Moved start/end time labels to the left time scale next to each block.
4. Removed GO/OPTIONAL and in-block time from overview blocks.
5. Moved walking time into a between-block marker and kept lost alternatives only for taller blocks.
6. Ran focused app unit tests and debug compile validation.
Architecture impact: not architecture-significant; Android UI layout behavior only. No ADR needed.
Deviation: no installed-device screenshot was captured in this task; visual UAT remains recommended on phone/BlueStacks.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Short schedule blocks now keep only the essential overview content inside the block: band with stars and stage. Start/end times are rendered on the left time scale using the confirmed bracket layout, GO/OPTIONAL is removed from overview blocks, walking time is rendered as a separate marker between consecutive blocks, and lost alternative text is only shown in taller blocks. Full alternatives remain available through the existing tap-to-detail flow.

## Acceptance criteria validation

All acceptance criteria are met. Short blocks no longer include time, GO/OPTIONAL, lost alternative, or walking text inside the constrained block. Longer blocks can show the lost alternative. Walking information is rendered between blocks.

## How to test

### Automated tests

- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:testDebugUnitTest :app:compileDebugJavaWithJavac
- git diff --check

### Manual validation

- Installed-device visual validation is still recommended on a phone-sized Android device and BlueStacks to confirm final density and alignment.

## TDD / BDD / approval-test evidence

- Added ScheduleBlockContentTest for short/tall adaptive overview content.
- Added ScheduleCalendarLayoutTest coverage for end offsets and walking-marker positioning between blocks.

## Architecture impact

- Architecture-significant change: no
- Approval received: not required
- ADR: none

## README impact

README impact: none, because this refines existing schedule overview presentation without changing setup, architecture, commands, or public capabilities.

## Business requirements impact

Business requirements impact: none, because BR-063, BR-064, and BR-074 already cover calendar blocks and walking-time visibility.

## Diagram impact

Diagram impact: none, because no architecture or workflow diagram changed.

## Commits / logical change list

- Added adaptive ScheduleBlockContent helper and tests.
- Added schedule layout helpers for event end and walking marker offsets.
- Updated ScheduleActivity to render time labels on the left scale and walking markers between blocks.

## Risks and follow-up

- Visual UAT may still tune exact spacing after testing on the installed APK.
<!-- SECTION:NOTES:END -->

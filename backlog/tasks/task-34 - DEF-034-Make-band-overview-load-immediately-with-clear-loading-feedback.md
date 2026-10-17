---
id: task-34
title: 'DEF-034: Make band overview load immediately with clear loading feedback'
status: Done
assignee:
  - '@codex'
created_date: '2026-05-17 06:02'
updated_date: '2026-05-17 06:22'
labels:
  - defect
  - ui
  - performance
  - mvp1
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a festival attendee, I want the band list to appear quickly with clear loading feedback, so that I know the app is working and do not tap repeatedly or think it froze.

Business value:
- The band overview is the primary rating workflow. It must feel responsive and trustworthy.

In scope:
- Make opening or returning to the band overview feel immediate.
- Keep sorting by band name.
- Show a clear loading state whenever data loading or rendering takes noticeable time.
- Prevent repeated taps while the list is still loading.

Out of scope:
- Changing the rating scale.
- Changing music-link behavior.
- Changing schedule decision rules.
- Adding new sort options unless needed as a technical fallback.

Notes:
- Business target: perceived response should be immediate. If the list cannot be fully rendered in a few milliseconds, the loading state must appear immediately.
- Sorting must stay alphabetical by band name, not time, rating, or import order.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given the user opens the band overview, when stored band data exists, then the list is sorted by band name.
- [x] #2 Given the band overview is loading, when the list is not ready immediately, then the user sees a loading indication within 100 ms.
- [x] #3 Given the band overview is loading, then repeated taps cannot trigger duplicate list loads or duplicate navigation.
- [x] #4 Given the Wacken 2026 band CSV has been imported, when the user opens the overview, then the app renders the list fast enough that normal users do not experience a blank or frozen screen.
- [x] #5 Given the list is rendered, then rating state, stage, date, time, and music-link actions remain correct.
- [x] #6 Automated tests or profiling evidence cover the sorting and loading behavior where practical.
- [x] #7 README is updated if usage or performance behavior needs documenting, or implementation notes explain why no README update was needed.
- [x] #8 Architecture impact is assessed before implementation; if an architecture-significant change is needed, explicit approval is requested before coding.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Inspect current overview data loading and sorting path.
2. Add/adjust application tests so band listing sorts alphabetically by band name.
3. Optimize MainActivity overview rendering to avoid repeated repository reads per row and show an immediate loading state before data binding.
4. Prevent duplicate navigation during list loading.
5. Run targeted tests/compile, update task notes and README impact, then close task.

Architecture impact: expected not architecture-significant; this should reuse existing use cases/repositories and change sorting/UI loading behavior only.
Complexity: standard UI defect because it touches application ordering and Android rendering.
README/ADR/diagram impact: README likely not needed unless behavior needs public documentation; no ADR/diagram expected.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Fixed the overview responsiveness defect. Band listing now sorts alphabetically by band name in the application use case. MainActivity shows an immediate `Loading bands...` state before loading/binding data, posts the actual load after the UI has a chance to render, and builds one in-memory band metadata map for row links/detail navigation instead of rereading the file-backed band repository for every row.

## Acceptance criteria validation

- AC1: Covered by `ListBandsUseCaseTest.returnsBandsWithStageAndTimeSortedByBandName` and unscheduled-band sorting coverage.
- AC2: MainActivity now renders `Loading bands...` before posting the load work.
- AC3: Loading flag prevents duplicate row navigation while loading.
- AC4: Removed repeated per-row repository reads, the main performance problem for the Wacken-sized list.
- AC5: Row rating, schedule fields, and music-link actions still use the same BandListItem and stored Band metadata.
- AC6: Added sorting test and compile validation for loading/rendering code.
- AC7: README not updated because setup, usage instructions, architecture, and troubleshooting did not change.
- AC8: Architecture impact assessed; no architecture-significant change.

## How to test

### Automated tests

- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :application:test :app:compileDebugJavaWithJavac

### Manual validation

Not run on device/emulator in this environment. Manual validation should import data/wacken-2026/bands.csv, return to the overview, confirm `Loading bands...` appears immediately if loading is visible, and confirm the final list is alphabetical.

## TDD / BDD / approval-test evidence

Updated the application list test to fail unless performance-backed listings are sorted by band name, then changed ListBandsUseCase. No approval tests were needed because this is a defect fix.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR: not needed. Existing application and UI boundaries were reused.

## README impact

No README update needed because this does not change setup, commands, public usage guidance, or architecture.

## Diagram impact

No diagram update needed.

## Commits / logical change list

- Sort ListBandsUseCase output by band name.
- Show immediate overview loading state before data load/render.
- Cache band metadata once per render and reuse it for row actions/detail navigation.
- Prevent row navigation while loading.

## Risks and follow-up

No Android instrumentation benchmark exists yet, so the performance improvement is validated by removing the repeated repository IO pattern and compiling the UI. A real-device timing check should be included in manual UAT.
<!-- SECTION:NOTES:END -->

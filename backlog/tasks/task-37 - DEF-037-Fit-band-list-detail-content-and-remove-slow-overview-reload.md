---
id: task-37
title: 'DEF-037: Fit band list/detail content and remove slow overview reload'
status: Done
assignee:
  - '@codex'
created_date: '2026-05-17 06:46'
updated_date: '2026-05-17 06:52'
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
As a festival attendee, I want the band list and detail content to fit the screen and load in about 100 ms, so that the app feels usable and does not hide controls or make me wait when opening or returning to the list.

Business value:
- The primary rating workflow must be fast and readable on the device screen. A clipped table or five-second reload blocks normal use.

In scope:
- Fix clipped table/detail content so controls fit inside the visible container without horizontal cut-off.
- Replace eager full-list row rendering with a recycled/native list approach or equivalent so opening and returning to the overview does not rebuild hundreds of row views synchronously.
- Keep alphabetical sorting by band name.
- Preserve rating, stage/date/time, YouTube, Spotify, and detail navigation behavior.
- Avoid reloading/re-rendering the list on every return when data has not changed.

Out of scope:
- Changing the 0-4 rating scale.
- Changing imported CSV schema.
- Adding a new UI framework.

Notes:
- Reported behavior: overview takes about 5 seconds on open and again when returning; target is about 100 ms perceived load.
- Screenshot shows content clipped horizontally; table/detail controls must fit the available container.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given the band overview is shown on the target device width, then the row content fits the visible container without requiring horizontal scrolling or being cut off.
- [x] #2 Given the band detail screen is shown on the target device width, then image, rating, running order, and Home/YouTube/Spotify actions fit without horizontal cut-off.
- [x] #3 Given the Wacken 2026 band list is imported, when the user opens or returns to the overview, then the perceived list load is about 100 ms and no repeated five-second reload occurs.
- [x] #4 Given the overview data has not changed, when the user returns from detail or another app, then the app reuses the current loaded list instead of rebuilding it from storage unnecessarily.
- [x] #5 Given bands are shown in the overview, then sorting remains alphabetical by band name.
- [x] #6 Given a row is displayed, then rating state, stage/date/time, YouTube, Spotify, and detail navigation behavior remain correct.
- [x] #7 Automated tests or compile validation cover sorting and affected UI code where practical; manual validation steps document the 100 ms/perceived-load check.
- [x] #8 README is updated if public behavior or usage guidance changes, or implementation notes explain why no README update was needed.
- [x] #9 Architecture impact is assessed before implementation; if an architecture-significant change is needed, explicit approval is requested before coding.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Inspect current MainActivity eager table rendering and BandDetailActivity layout sizing.
2. Replace overview eager LinearLayout table with a native recycled ListView/BaseAdapter that fits the available width using proportional row cells.
3. Cache loaded overview data inside MainActivity so returning from detail does not reload/rebuild when the data has not changed.
4. Adjust detail layout so image, rating/running-order/actions, and link buttons fit without horizontal clipping.
5. Compile/test, regenerate APK, document manual validation steps, and close task.

Architecture impact: not architecture-significant; Android UI rendering/lifecycle only, using platform widgets and existing use cases.
Complexity: standard performance/UI defect.
README/ADR/diagram impact: no README/ADR/diagram expected unless implementation changes public instructions.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Implementation notes:
- Replaced the band overview eager LinearLayout/ScrollView rendering with a native ListView backed by BaseAdapter row recycling. Only visible rows are created/bound, removing the previous full-list synchronous view creation cost.
- Added overview lifecycle caching: MainActivity now reloads only for first load or after opening import; returning from detail or from another app keeps the current adapter/list instead of rebuilding from storage.
- Kept alphabetical sorting delegated to ListBandsUseCase and preserved row rating, detail navigation, stage/date/time, YouTube, and Spotify behavior.
- Adjusted overview column weights, action button size, and text ellipsizing so table rows fit the available width instead of requiring horizontal scrolling or clipping.
- Adjusted detail layout to match-parent width, reduced image/control sizes, and used a weighted facts panel so image, rating, running order, and Home/YouTube/Spotify controls fit without horizontal cut-off.

Validation:
- Automated: JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home ./gradlew :application:test :app:compileDebugJavaWithJavac passed.
- Automated: JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home ./gradlew test passed.
- Automated: JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home ./gradlew qaTest passed.
- Build: JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home ./gradlew assembleDebug passed and regenerated app/build/outputs/apk/debug/app-debug.apk.
- Manual timing check to perform on target device: import Wacken 2026 CSV, open Bands, open any detail, return to Bands, and verify the list appears immediately without the previous five-second rebuild. adb is not installed in this environment, so device timing was not captured here.

README impact: no README update needed for task-37 because setup, commands, architecture, and public usage instructions did not change.
Diagram impact: no diagram update needed; this is an Android UI rendering/lifecycle defect fix.
ADR impact: no ADR needed; not architecture-significant.
Approval status: no explicit architecture approval required.
Risks: final perceived-load timing still needs confirmation on the target device, but the expensive eager full-list view creation and repeated return reload were removed.
<!-- SECTION:NOTES:END -->

---
id: task-36
title: 'DEF-036: Restore last app screen after leaving and returning'
status: Done
assignee:
  - '@codex'
created_date: '2026-05-17 06:03'
updated_date: '2026-05-17 06:27'
labels:
  - defect
  - android
  - lifecycle
  - mvp1
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a festival attendee, I want the app to return to the screen I was using, so that opening a music link or leaving the app does not lose my place or show an unexpected new-tab screen.

Business value:
- The app must preserve user context during festival use, especially when users switch to YouTube, Spotify, browser, or another app and then return.

In scope:
- Preserve the current internal app screen when Android pauses and resumes the app.
- Returning from YouTube, Spotify, or browser should restore the previous overview/detail state.
- Avoid showing an unexpected browser or new-tab-like screen as the active app state.
- Keep external link behavior working.

Out of scope:
- Implementing deep links from external apps back into a specific band unless needed to restore current state.
- Changing music service support.
- Adding account or cloud session persistence.

Notes:
- This may involve Android activity launch mode, external intent flags, saved instance state, or task/back-stack handling.
- The business rule is simple: returning to the app should restore the user’s last Wacken Planner context.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given the user is on the overview, when they leave the app and return, then the overview is still shown.
- [x] #2 Given the user is on a band detail screen, when they leave the app and return, then the same detail screen is still shown.
- [x] #3 Given the user opens YouTube or Spotify from the app, when they return to Wacken Planner, then they return to the same Wacken Planner screen they came from.
- [x] #4 Given the user returns to the app, then no unexpected new tab or unrelated third page is shown.
- [x] #5 Given rating or scroll state existed before leaving, then the app preserves the most important user context where practical.
- [x] #6 Automated Android lifecycle or focused unit coverage is added where practical; otherwise manual lifecycle validation steps are documented.
- [x] #7 README is updated if lifecycle or known behavior needs documenting, or implementation notes explain why no README update was needed.
- [x] #8 Architecture impact is assessed before implementation; if an architecture-significant change is needed, explicit approval is requested before coding.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Inspect Android manifest and external link intents.
2. Adjust app task/back-stack behavior so launcher returns to the existing Wacken task and external music/browser links open outside the app task.
3. Preserve current overview/detail context across normal pause/resume without resetting to an unrelated screen.
4. Run compile/full relevant checks, document manual lifecycle validation steps, close task.

Architecture impact: expected not architecture-significant; Android manifest/intent lifecycle fix only.
Complexity: standard Android lifecycle defect.
README/ADR/diagram impact: README likely not needed unless user-facing behavior needs notes; no ADR/diagram expected.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Fixed the app-return lifecycle defect at the Android task/intent level. External music/browser links now go through a shared ExternalLinks helper that adds FLAG_ACTIVITY_NEW_TASK, keeping browser/YouTube/Spotify screens outside the Wacken Planner task. The app manifest now retains task state and uses singleTop for MainActivity so relaunching the app returns to the existing task instead of creating surprising duplicate launcher state.

Internal navigation remains unchanged: overview opens detail in the Wacken task, and detail Home still finishes back to overview.

## Acceptance criteria validation

- AC1: Manifest retains the Wacken task and MainActivity uses singleTop for launcher returns.
- AC2: BandDetailActivity remains in the app task, so returning to Wacken keeps detail when it was on top.
- AC3: YouTube/Spotify links open via ExternalLinks with FLAG_ACTIVITY_NEW_TASK, separating external apps from Wacken task state.
- AC4: External browser/new-tab screens should no longer be the Wacken Planner task foreground when returning to the app.
- AC5: Existing overview/detail context is preserved by task retention; scroll-position preservation beyond Android default behavior is not explicitly implemented.
- AC6: Android lifecycle behavior is compile-validated; manual validation steps are documented because no instrumentation harness exists.
- AC7: README not updated because this is defect-level lifecycle behavior, not setup or user instructions.
- AC8: Architecture impact assessed; no architecture-significant change.

## How to test

### Automated tests

- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:compileDebugJavaWithJavac

### Manual validation

1. Install and open the app.
2. Navigate to the band overview, background the app, then return from launcher/recents; overview should still be shown.
3. Open a band detail screen, background the app, then return; the same detail screen should still be shown.
4. From overview or detail, open YouTube or Spotify. Return to Wacken Planner from launcher/recents; the previous Wacken screen should be shown, not a browser new-tab screen.
5. Confirm the Home button on detail still returns to overview.

## TDD / BDD / approval-test evidence

No unit-level behavior was suitable because this is Android task/intent behavior without an instrumentation harness. Compile validation plus documented manual lifecycle validation is used.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR: not needed; Android manifest and intent flags only.

## README impact

No README update needed because setup, commands, architecture, and normal usage instructions did not change.

## Diagram impact

No diagram update needed.

## Commits / logical change list

- Added ExternalLinks helper for external ACTION_VIEW intents.
- Open external links with FLAG_ACTIVITY_NEW_TASK.
- Added alwaysRetainTaskState to the app manifest.
- Set MainActivity launchMode to singleTop.

## Risks and follow-up

This needs real-device/emulator validation because Android task behavior can vary by launcher/browser implementation. If a specific browser still behaves badly, the next step is capturing that package/browser behavior and adjusting intent flags or using a chooser.
<!-- SECTION:NOTES:END -->

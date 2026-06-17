---
id: task-115
title: 'DEF: Fix null error when opening group schedule'
status: To Do
assignee: []
created_date: '2026-06-17 19:41'
updated_date: '2026-06-17 19:42'
labels:
  - defect
  - schedule
  - qa
  - android
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
A user can open Group Schedule and see `Schedule could not be generated: null` instead of a schedule or actionable error.

Business value: the schedule is the main festival planning view. A null error blocks planning and gives no information to the user or QA about the failing dependency/data path.

Observed evidence: screenshot `/Users/dino/Desktop/Screenshot 2026-06-17 at 21.38.42.png` shows the schedule screen rendering `Schedule could not be generated: null`.

Initial analysis: `ScheduleActivity` catches any exception around lock loading and schedule generation, then displays `error.getMessage()`. A plain `NullPointerException` has no message, so the UI renders `null` and masks the actual root cause. The likely failure class is an unguarded null from schedule inputs or repository data during generation/lock loading, but the exact source needs to be reproduced with a regression test and diagnostics.

How this passed QA: current automated checks cover domain/application schedule rules and layout helpers, but not the Android schedule screen error path with real repository wiring, missing/partial synced data, or no-message exceptions. Manual QA also did not include an assertion that schedule failures must have actionable messages and diagnostic logs.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given schedule generation or schedule lock loading throws an exception with no message, when the Group Schedule screen opens, then the UI shows an actionable non-null error message and records diagnostic details.
- [ ] #2 Given local cached schedule data has partial or missing optional records such as distances, locks, ratings, or stage metadata, when the schedule is opened, then the app either generates the schedule with safe defaults or reports the specific missing required data without a raw null error.
- [ ] #3 A failing regression test reproduces the null-message schedule failure before the fix, and passing tests prove the fixed behavior.
- [ ] #4 Implementation notes explain the confirmed root cause, why existing QA missed it, and what validation was added.
- [ ] #5 Business requirements and README impact are recorded using the canonical wording from delivery-governance.md.
- [ ] #6 Android schedule-screen tests or focused presentation coverage include the caught-exception path so `Schedule could not be generated: null` cannot regress.
<!-- AC:END -->

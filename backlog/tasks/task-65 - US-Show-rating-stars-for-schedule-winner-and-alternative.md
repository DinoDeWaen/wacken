---
id: task-65
title: 'US: Show rating stars for schedule winner and alternative'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-10 06:36'
updated_date: '2026-06-10 06:39'
labels:
  - mvp2
  - android
  - schedule
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a Wacken Planner user viewing the generated group schedule, I want to see the rating stars for the selected winner and lost alternative, so I can understand why one band won a conflict and whether the alternative is still worth considering.

In scope:
- Add winner rating stars to each generated schedule slot.
- Add lost-alternative rating stars when a lost alternative exists.
- Use existing group rating data and schedule decision output; do not change conflict rules.
- Keep Android schedule UI compact and readable.

Out of scope:
- Per-person overview ratings, manual schedule overrides, multiple groups, and changing conflict-resolution policy.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given a generated schedule slot has a selected winner, when the schedule is shown, then the winner row displays rating stars derived from the winner group rating used by the schedule.
- [x] #2 Given a generated schedule slot has a lost alternative, when the schedule is shown, then the lost alternative displays its own rating stars.
- [x] #3 Given the selected winner or lost alternative is unrated, when stars are rendered, then no filled stars are shown and the app does not crash.
- [x] #4 Given existing MVP2 conflict rules, when stars are added to the schedule output, then selected winner and lost alternative behavior does not change.
- [x] #5 Automated tests cover schedule slot star values and Android/app compile validation passes.
- [x] #6 README/business requirements/diagram/ADR impact are recorded using delivery-governance wording.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Inspected schedule generation tests, TimelineSlot, conflict resolution output, and ScheduleActivity rendering.
2. Added application tests proving selected winner and lost alternative rating values are carried in generated slots.
3. Extended TimelineSlot with winner rating value and optional lost-alternative rating value derived from the same group ratings used by conflict resolution.
4. Rendered compact read-only stars in ScheduleActivity for winner and lost alternative.
5. Updated README and business requirements for the visible schedule behavior.
6. Ran focused domain/application tests, Android compile, assembleDebug, and diff checks.

Deviation: none. Architecture impact: not architecture-significant; this extends existing application output and Android presentation without changing conflict policy, persistence, backend schema, APIs, dependencies, or module boundaries. ADR impact: none.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Generated schedule slots now carry the winner rating value and optional lost-alternative rating value. ScheduleActivity renders compact read-only stars beside the selected band and beside the lost alternative when one exists. The rating values are derived from the same group ratings used by the schedule generator; conflict winner and lost-alternative selection rules were not changed.

## Acceptance criteria validation

- AC1: `TimelineSlot.rating()` is set from the selected winner highest group rating, and ScheduleActivity renders it beside the winner.
- AC2: `TimelineSlot.lostAlternativeRating()` is set when a lost alternative exists, and ScheduleActivity renders it beside the lost alternative.
- AC3: TimelineSlot accepts rating value `0`, and ScheduleActivity clamps rendered stars to 0-5 so unrated values show no filled stars without crashing.
- AC4: Existing conflict resolver and schedule selection behavior were not changed; tests for generated schedule and decisions still pass.
- AC5: Application tests cover winner and lost-alternative rating values; Android compile and APK assembly passed.
- AC6: README and business requirements impacts are recorded below.

## How to test

### Automated tests

- `/bin/zsh -lc JAVA_HOME=java21 ./gradlew :application:test :domain:test :app:compileDebugJavaWithJavac`
- `/bin/zsh -lc JAVA_HOME=java21 ./gradlew :domain:test :application:test :app:compileDebugJavaWithJavac assembleDebug`
- `git diff --check`

### Manual validation

- Not run on a physical Android device in this task. Install the rebuilt APK and open **View group schedule** to verify winner and lost-alternative stars visually.

## TDD / BDD / approval-test evidence

Added application tests first for winner and lost-alternative rating values in generated schedule slots. Existing schedule/conflict tests protect unchanged selection behavior.

## Architecture impact

- Architecture-significant change: no. The change extends application output and Android presentation only.
- Approval received: not required.
- ADR: none required.

## README impact

README impact: updated basic functionality to mention winner stars and lost-alternative stars in the MVP2 group schedule.

## Business requirements impact

Business requirements impact: updated BR-031 to include winner rating stars and lost-alternative rating stars in timeline slots.

## Diagram impact

Diagram impact: none, because the system structure and data flow did not change.

## Commits / logical change list

- Add winner and lost-alternative rating values to `TimelineSlot`.
- Derive schedule slot star values from group ratings during schedule generation.
- Render winner and lost-alternative stars in Android schedule cards.
- Update README and business requirements.

## Risks and follow-up

- Device visual validation remains to be run on an installed APK.
<!-- SECTION:NOTES:END -->

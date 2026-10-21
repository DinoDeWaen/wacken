---
type: Groma Flow
title: Build the shared calendar
groma:
  id: build-the-shared-calendar
---

An attendee opens the calendar; schedule generation reads performances and applies group conflict rules.

## Steps

| From | To | Action |
| --- | --- | --- |
| [From](../actors/festival-attendee.md) | [To](../systems/wacken/containers/android-application/components/scheduleactivity.md) | Open the group schedule |
| [From](../systems/wacken/containers/android-application/components/scheduleactivity.md) | [To](../systems/wacken/containers/android-application/components/generatesharedscheduleusecase.md) | Generate day slots |
| [From](../systems/wacken/containers/android-application/components/generatesharedscheduleusecase.md) | [To](../systems/wacken/containers/android-application/components/performanceconflictresolver.md) | Resolve overlaps and preferences |

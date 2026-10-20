---
type: C4 Actor
title: Festival attendee
status: stable
groma:
  id: festival-attendee
---

Rates bands, reviews a shared conflict-aware schedule, records personal post-show ratings, and browses archived festivals.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [festival-attendee](festival-attendee.md) | [app/src/main/java/be/wacken/planner/MainActivity.java](../../app/src/main/java/be/wacken/planner/MainActivity.java) | Views active lineup | Android UI |
| [festival-attendee](festival-attendee.md) | [app/src/main/java/be/wacken/planner/ScheduleActivity.java](../../app/src/main/java/be/wacken/planner/ScheduleActivity.java) | Reviews group schedule | Android UI |

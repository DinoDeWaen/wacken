---
type: Groma Flow
title: Edit selected festival planning
groma:
  id: edit-selected-festival-planning
---

The attendee edits planning for the selected active festival. The detail view stays pinned even if the active festival changes; it validates activity and lineup before delegating to the configured pending-capable planning repository. Planning never changes after-state or established opinions.

## Steps

| From | To | Action |
| --- | --- | --- |
| [Attendee](../actors/festival-attendee.md) | [banddetailactivity](../systems/wacken/containers/android-application/components/banddetailactivity.md) | Open the selected festival band |
| [banddetailactivity](../systems/wacken/containers/android-application/components/banddetailactivity.md) | [ratebandusecase](../systems/wacken/containers/android-application/components/ratebandusecase.md) | Choose planning value or reset |
| [ratebandusecase](../systems/wacken/containers/android-application/components/ratebandusecase.md) | [festivalplanningratingview](../systems/wacken/containers/android-application/components/festivalplanningratingview.md) | Use the selected festival adapter |
| [festivalplanningratingview](../systems/wacken/containers/android-application/components/festivalplanningratingview.md) | [bandrepository](../systems/wacken/containers/android-application/components/bandrepository.md) | Validate target and persist pending planning |

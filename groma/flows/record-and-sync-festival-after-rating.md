---
type: Groma Flow
title: Record and sync festival after-rating
groma:
  id: record-and-sync-festival-after-rating
---

The attendee records or resets after-state for one member/festival/band. Zero is durable reset, missing is absence; neither promotes an established opinion. Room saves pending operation and nonzero dated history atomically. A later AppRepositories sync pushes explicit operations, skips uncertain legacy facts, acknowledges exact operations and merges complete own snapshots while preserving pending edits.

## Steps

| From | To | Action |
| --- | --- | --- |
| [Attendee](../actors/festival-attendee.md) | [banddetailactivity](../systems/wacken/containers/android-application/components/banddetailactivity.md) | Open selected active band |
| [banddetailactivity](../systems/wacken/containers/android-application/components/banddetailactivity.md) | [recordfestivalafterratingusecase](../systems/wacken/containers/android-application/components/recordfestivalafterratingusecase.md) | Choose after-rating or durable reset |
| [recordfestivalafterratingusecase](../systems/wacken/containers/android-application/components/recordfestivalafterratingusecase.md) | [festivalafterratingrepository](../systems/wacken/containers/android-application/components/festivalafterratingrepository.md) | Validate active festival and lineup |
| [festivalafterratingrepository](../systems/wacken/containers/android-application/components/festivalafterratingrepository.md) | [roomfestivalafterratingrepository](../systems/wacken/containers/android-application/components/roomfestivalafterratingrepository.md) | Persist pending fact and nonzero history |
| [apprepositories](../systems/wacken/containers/android-application/components/apprepositories.md) | [ratingstatesync](../systems/wacken/containers/android-application/components/ratingstatesync.md) | Later sync explicit own pending work |
| [ratingstatesync](../systems/wacken/containers/android-application/components/ratingstatesync.md) | [supabaseratingstateclient](../systems/wacken/containers/android-application/components/supabaseratingstateclient.md) | Push operation and fetch complete own state |
| [ratingstatesync](../systems/wacken/containers/android-application/components/ratingstatesync.md) | [ratingstatesyncstore](../systems/wacken/containers/android-application/components/ratingstatesyncstore.md) | Acknowledge exact operation and merge |
| [ratingstatesyncstore](../systems/wacken/containers/android-application/components/ratingstatesyncstore.md) | [roomfestivalafterratingrepository](../systems/wacken/containers/android-application/components/roomfestivalafterratingrepository.md) | Keep newer pending edits |

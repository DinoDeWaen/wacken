---
type: Groma Flow
title: Review orphaned pending planning
groma:
  id: review-orphaned-pending-planning
---

A sync error carries the exact orphaned pending planning fact. The user may keep it, explicitly move it to active Wacken2026 only when no local or remote target exists, or separately confirm discard. Owner and unchanged-fact checks prevent stale recovery; all failures preserve pending work.

## Steps

| From | To | Action |
| --- | --- | --- |
| [Attendee](../actors/festival-attendee.md) | [mainactivity](../systems/wacken/containers/android-application/components/mainactivity.md) | Sync retained pending ratings |
| [mainactivity](../systems/wacken/containers/android-application/components/mainactivity.md) | [missingfestivalratingdialog](../systems/wacken/containers/android-application/components/missingfestivalratingdialog.md) | Review typed missing-festival failure |
| [missingfestivalratingdialog](../systems/wacken/containers/android-application/components/missingfestivalratingdialog.md) | [missingfestivalplanningratingexception](../systems/wacken/containers/android-application/components/missingfestivalplanningratingexception.md) | Inspect exact blocked fact |
| [missingfestivalratingdialog](../systems/wacken/containers/android-application/components/missingfestivalratingdialog.md) | [apprepositories](../systems/wacken/containers/android-application/components/apprepositories.md) | Confirm move or discard |
| [apprepositories](../systems/wacken/containers/android-application/components/apprepositories.md) | [recoverpendingplanningratingusecase](../systems/wacken/containers/android-application/components/recoverpendingplanningratingusecase.md) | Validate owner and unchanged pending fact |
| [recoverpendingplanningratingusecase](../systems/wacken/containers/android-application/components/recoverpendingplanningratingusecase.md) | [supabaseratingclient](../systems/wacken/containers/android-application/components/supabaseratingclient.md) | For move check remote target absent |
| [recoverpendingplanningratingusecase](../systems/wacken/containers/android-application/components/recoverpendingplanningratingusecase.md) | [wackendatabase](../systems/wacken/containers/android-application/components/wackendatabase.md) | Apply explicit safe move or discard |

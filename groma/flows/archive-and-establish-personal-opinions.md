---
type: Groma Flow
title: Archive and establish personal opinions
groma:
  id: archive-and-establish-personal-opinions
---

MainActivity runs the supplied archive use case on a worker. AppRepositories gateway callback first syncs pending ratings and locks and refuses unresolved work. V016 atomically archives and promotes each member final nonempty after-rating; missing/reset retain previous established opinions. After validated acknowledgement, own complete snapshots and festival status publish together under a fresh pending guard. Other-device unsent edits remain pending and require explicit archived correction, rather than silent overwrite.

## Steps

| From | To | Action |
| --- | --- | --- |
| [Attendee](../actors/festival-attendee.md) | [mainactivity](../systems/wacken/containers/android-application/components/mainactivity.md) | Archive completed active festival |
| [mainactivity](../systems/wacken/containers/android-application/components/mainactivity.md) | [archivefestivalaction](../systems/wacken/containers/android-application/components/archivefestivalaction.md) | Execute archive worker |
| [archivefestivalaction](../systems/wacken/containers/android-application/components/archivefestivalaction.md) | [addfestivalusecase](../systems/wacken/containers/android-application/components/addfestivalusecase.md) | Invoke supplied command |
| [addfestivalusecase](../systems/wacken/containers/android-application/components/addfestivalusecase.md) | [festivalarchivegateway](../systems/wacken/containers/android-application/components/festivalarchivegateway.md) | Archive selected active festival |
| [festivalarchivegateway](../systems/wacken/containers/android-application/components/festivalarchivegateway.md) | [apprepositories](../systems/wacken/containers/android-application/components/apprepositories.md) | Run supplied pre-sync gateway callback |
| [apprepositories](../systems/wacken/containers/android-application/components/apprepositories.md) | [supabasefestivalarchivegateway](../systems/wacken/containers/android-application/components/supabasefestivalarchivegateway.md) | Refuse pending work then call archive |
| [supabasefestivalarchivegateway](../systems/wacken/containers/android-application/components/supabasefestivalarchivegateway.md) | [supabasemasterdataclient](../systems/wacken/containers/android-application/components/supabasemasterdataclient.md) | Commit authenticated archive RPC |
| [supabasefestivalarchivegateway](../systems/wacken/containers/android-application/components/supabasefestivalarchivegateway.md) | [supabaseratingstateclient](../systems/wacken/containers/android-application/components/supabaseratingstateclient.md) | Fetch acknowledged own rating snapshots |
| [supabasefestivalarchivegateway](../systems/wacken/containers/android-application/components/supabasefestivalarchivegateway.md) | [roomfestivalarchivecache](../systems/wacken/containers/android-application/components/roomfestivalarchivecache.md) | Publish validated snapshot |
| [roomfestivalarchivecache](../systems/wacken/containers/android-application/components/roomfestivalarchivecache.md) | [wackendatabase](../systems/wacken/containers/android-application/components/wackendatabase.md) | Atomically publish unless new pending work exists |

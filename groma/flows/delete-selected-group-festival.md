---
type: Groma Flow
title: Delete selected group festival
groma:
  id: delete-selected-group-festival
---

Active home and archived festival screens share the confirmation dialog. After explicit confirmation, application selection and the authenticated remote adapter delete the chosen festival for the group. Local cleanup runs only after remote acknowledgement and absence check. Remote failure retains local state; local failure after remote success reports sync recovery. SQL V011 and later lifecycle schema preserve canonical bands and established personal history; this flow does not authorize arbitrary deletion or claim production permission testing.

## Steps

| From | To | Action |
| --- | --- | --- |
| [festival-attendee](../actors/festival-attendee.md) | [mainactivity](../systems/wacken/containers/android-application/components/mainactivity.md) | Choose the existing festival |
| [mainactivity](../systems/wacken/containers/android-application/components/mainactivity.md) | [deletefestivaldialog](../systems/wacken/containers/android-application/components/deletefestivaldialog.md) | Confirm removal of selected festival scope |
| [deletefestivaldialog](../systems/wacken/containers/android-application/components/deletefestivaldialog.md) | [apprepositories](../systems/wacken/containers/android-application/components/apprepositories.md) | Dispatch deletion on a worker |
| [apprepositories](../systems/wacken/containers/android-application/components/apprepositories.md) | [deletefestivalusecase](../systems/wacken/containers/android-application/components/deletefestivalusecase.md) | Resolve configured selected-festival deletion workflow |
| [deletefestivalusecase](../systems/wacken/containers/android-application/components/deletefestivalusecase.md) | [supabasefestivaldeletiongateway](../systems/wacken/containers/android-application/components/supabasefestivaldeletiongateway.md) | Invoke configured Gateway |
| [supabasefestivaldeletiongateway](../systems/wacken/containers/android-application/components/supabasefestivaldeletiongateway.md) | [supabasemasterdataclient](../systems/wacken/containers/android-application/components/supabasemasterdataclient.md) | Delete remotely before local cleanup |
| [supabasemasterdataclient](../systems/wacken/containers/android-application/components/supabasemasterdataclient.md) | [supabase](../externals/supabase.md) | DELETE selected festival then confirm absence |
| [supabasefestivaldeletiongateway](../systems/wacken/containers/android-application/components/supabasefestivaldeletiongateway.md) | [roomfestivaldeletion](../systems/wacken/containers/android-application/components/roomfestivaldeletion.md) | Apply local cleanup only after remote acknowledgement |
| [roomfestivaldeletion](../systems/wacken/containers/android-application/components/roomfestivaldeletion.md) | [wackendatabase](../systems/wacken/containers/android-application/components/wackendatabase.md) | Detach retained history and delete selected festival state transactionally |
| [roomfestivaldeletion](../systems/wacken/containers/android-application/components/roomfestivaldeletion.md) | [roomfestivalcachemarker](../systems/wacken/containers/android-application/components/roomfestivalcachemarker.md) | Mark intentionally empty cache initialized |

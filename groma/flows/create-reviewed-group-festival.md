---
type: Groma Flow
title: Create reviewed group festival
groma:
  id: create-reviewed-group-festival
---

An authenticated member reviews uploaded identities and explicitly confirms creation. Cancel leaves no new festival or alias; alias/name conflicts require fresh review. SQL V018 delegates to the existing atomic creation transaction in V015: canonical metadata, lineup and each current member's once-only established-rating planning snapshot commit together; new after-ratings stay empty. The response contains only the creator's planning snapshot. Idempotent retry preserves later edits. A cache failure after shared success is reported separately. Backend behavior is supported by SQL/adapter evidence, not inferred by the Java scanner.

## Steps

| From | To | Action |
| --- | --- | --- |
| [festival-data-admin](../actors/festival-data-admin.md) | [importcsvactivity](../systems/wacken/containers/android-application/components/importcsvactivity.md) | Select lineup CSV and festival name |
| [importcsvactivity](../systems/wacken/containers/android-application/components/importcsvactivity.md) | [reviewfestivalbandsusecase](../systems/wacken/containers/android-application/components/reviewfestivalbandsusecase.md) | Load exact and suggested identity proposals |
| [reviewfestivalbandsusecase](../systems/wacken/containers/android-application/components/reviewfestivalbandsusecase.md) | [festivalbandcatalog](../systems/wacken/containers/android-application/components/festivalbandcatalog.md) | Read fresh canonical identities and confirmed aliases |
| [importcsvactivity](../systems/wacken/containers/android-application/components/importcsvactivity.md) | [festivalbandreviewdialog](../systems/wacken/containers/android-application/components/festivalbandreviewdialog.md) | Review nonexact identities with no default selection |
| [festivalbandreviewdialog](../systems/wacken/containers/android-application/components/festivalbandreviewdialog.md) | [importcsvactivity](../systems/wacken/containers/android-application/components/importcsvactivity.md) | Confirm every row and final summary; optionally remember selected aliases |
| [importcsvactivity](../systems/wacken/containers/android-application/components/importcsvactivity.md) | [addfestivalaction](../systems/wacken/containers/android-application/components/addfestivalaction.md) | Dispatch the confirmed creation command |
| [addfestivalaction](../systems/wacken/containers/android-application/components/addfestivalaction.md) | [addfestivalusecase](../systems/wacken/containers/android-application/components/addfestivalusecase.md) | Run the supplier bound by the import screen |
| [addfestivalusecase](../systems/wacken/containers/android-application/components/addfestivalusecase.md) | [supabasefestivalcreationgateway](../systems/wacken/containers/android-application/components/supabasefestivalcreationgateway.md) | Invoke configured gateway through FestivalCreationGateway |
| [supabasefestivalcreationgateway](../systems/wacken/containers/android-application/components/supabasefestivalcreationgateway.md) | [supabasemasterdataclient](../systems/wacken/containers/android-application/components/supabasemasterdataclient.md) | Submit deterministic reviewed command |
| [supabasemasterdataclient](../systems/wacken/containers/android-application/components/supabasemasterdataclient.md) | [supabase](../externals/supabase.md) | POST create_festival_from_reviewed_bands |
| [supabase](../externals/supabase.md) | [shared-planner-database](../systems/wacken/containers/shared-planner-database/container.md) | Atomically create lineup, confirmed aliases and once-only member planning snapshots |
| [supabasefestivalcreationgateway](../systems/wacken/containers/android-application/components/supabasefestivalcreationgateway.md) | [roomfestivalcreationcache](../systems/wacken/containers/android-application/components/roomfestivalcreationcache.md) | Validate member/festival/lineup acknowledgement and publish creator snapshot |
| [roomfestivalcreationcache](../systems/wacken/containers/android-application/components/roomfestivalcreationcache.md) | [wackendatabase](../systems/wacken/containers/android-application/components/wackendatabase.md) | Commit cache together while preserving pending planning edits |

---
type: Groma Flow
title: Export own lifecycle and correction audit
groma:
  id: export-own-lifecycle-and-correction-audit
---

Settings exposes legacy ratings CSV, lifecycle version2 and correction audit as separate actions/contracts. Lifecycle records retain established provenance, exact festival planning/after-state, history and audit, preserving missing versus zero and unknown dates. Audit export separates original recorded and correction times; neither export chooses a latest event as an established rating.

## Steps

| From | To | Action |
| --- | --- | --- |
| [Attendee](../actors/festival-attendee.md) | [settingsactivity](../systems/wacken/containers/android-application/components/settingsactivity.md) | Choose an own-rating export |
| [settingsactivity](../systems/wacken/containers/android-application/components/settingsactivity.md) | [exportratinglifecyclecsvusecase](../systems/wacken/containers/android-application/components/exportratinglifecyclecsvusecase.md) | Generate lifecycle version2 records |
| [exportratinglifecyclecsvusecase](../systems/wacken/containers/android-application/components/exportratinglifecyclecsvusecase.md) | [personalbandratingrepository](../systems/wacken/containers/android-application/components/personalbandratingrepository.md) | Read established provenance |
| [exportratinglifecyclecsvusecase](../systems/wacken/containers/android-application/components/exportratinglifecyclecsvusecase.md) | [festivalafterratingrepository](../systems/wacken/containers/android-application/components/festivalafterratingrepository.md) | Retain missing and reset distinction |
| [settingsactivity](../systems/wacken/containers/android-application/components/settingsactivity.md) | [exportratingcorrectionscsvusecase](../systems/wacken/containers/android-application/components/exportratingcorrectionscsvusecase.md) | Alternatively choose audit-only contract |
| [exportratingcorrectionscsvusecase](../systems/wacken/containers/android-application/components/exportratingcorrectionscsvusecase.md) | [ratingcorrectionrepository](../systems/wacken/containers/android-application/components/ratingcorrectionrepository.md) | Read immutable own corrections |

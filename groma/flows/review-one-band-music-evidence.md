---
type: Groma Flow
title: Review one band music evidence
groma:
  id: review-one-band-music-evidence
---

Band detail opens the same reviewed workflow scoped to one canonical band name. Search terms refine identity only. Unverified or ambiguous results cannot be saved; public lookup may continue across rotation, while cancellation stops unfinished work. The retained repository uses its expected account for shared writes, so account mismatch or save failure must remain unconfirmed. Approval re-evaluates raw evidence/current links and delegates missing-field writes; search ranking and user acknowledgement alone are not verification.

## Steps

| From | To | Action |
| --- | --- | --- |
| [festival-attendee](../actors/festival-attendee.md) | [banddetailactivity](../systems/wacken/containers/android-application/components/banddetailactivity.md) | Open one selected band |
| [banddetailactivity](../systems/wacken/containers/android-application/components/banddetailactivity.md) | [musiclinksreviewactivity](../systems/wacken/containers/android-application/components/musiclinksreviewactivity.md) | Choose one-band missing-link lookup |
| [musiclinksreviewactivity](../systems/wacken/containers/android-application/components/musiclinksreviewactivity.md) | [findbandmusiclinksusecase](../systems/wacken/containers/android-application/components/findbandmusiclinksusecase.md) | Search and review source evidence |
| [findbandmusiclinksusecase](../systems/wacken/containers/android-application/components/findbandmusiclinksusecase.md) | [musiclinklookupprovider](../systems/wacken/containers/android-application/components/musiclinklookupprovider.md) | Collect identity facts |
| [musiclinklookupprovider](../systems/wacken/containers/android-application/components/musiclinklookupprovider.md) | [verifiedmusiclinksprovider](../systems/wacken/containers/android-application/components/verifiedmusiclinksprovider.md) | Retrieve corroborating evidence |
| [verifiedmusiclinksprovider](../systems/wacken/containers/android-application/components/verifiedmusiclinksprovider.md) | [officialmusicpageclient](../systems/wacken/containers/android-application/components/officialmusicpageclient.md) | Check declared public website |
| [musiclinksreviewactivity](../systems/wacken/containers/android-application/components/musiclinksreviewactivity.md) | [findbandmusiclinksusecase](../systems/wacken/containers/android-application/components/findbandmusiclinksusecase.md) | Confirm selected verified identity |
| [findbandmusiclinksusecase](../systems/wacken/containers/android-application/components/findbandmusiclinksusecase.md) | [searchbandmetadatausecase](../systems/wacken/containers/android-application/components/searchbandmetadatausecase.md) | Revalidate then apply missing fields |
| [searchbandmetadatausecase](../systems/wacken/containers/android-application/components/searchbandmetadatausecase.md) | [bandrepository](../systems/wacken/containers/android-application/components/bandrepository.md) | Keep existing canonical fields |
| [bandrepository](../systems/wacken/containers/android-application/components/bandrepository.md) | [syncedbandrepository](../systems/wacken/containers/android-application/components/syncedbandrepository.md) | Persist through account-bound shared writer |

---
type: Groma Flow
title: Review festival missing music links
groma:
  id: review-festival-missing-music-links
---

Festival admin opens Settings for the active festival. Search selects only lineup bands with missing music fields and never renames them from edited search terms. MusicBrainz/Wikidata corroboration and jointly declared public official-site anchors are facts; the application requires Group identity/name, matching artist ID and a unique canonical platform profile. Optional Spotify/YouTube fallback is explicitly unverified. The user checks source evidence, selects a verified identity and explicitly saves missing links; existing fields and ratings stay unchanged. Cancellation/errors retain saved data and completed reviews for retry. Source-first account-bound save must succeed before cache publication.

## Steps

| From | To | Action |
| --- | --- | --- |
| [festival-data-admin](../actors/festival-data-admin.md) | [settingsactivity](../systems/wacken/containers/android-application/components/settingsactivity.md) | Open active festival administration |
| [settingsactivity](../systems/wacken/containers/android-application/components/settingsactivity.md) | [musiclinksreviewactivity](../systems/wacken/containers/android-application/components/musiclinksreviewactivity.md) | Choose festival missing-link review |
| [musiclinksreviewactivity](../systems/wacken/containers/android-application/components/musiclinksreviewactivity.md) | [findbandmusiclinksusecase](../systems/wacken/containers/android-application/components/findbandmusiclinksusecase.md) | Search lineup bands with missing fields |
| [findbandmusiclinksusecase](../systems/wacken/containers/android-application/components/findbandmusiclinksusecase.md) | [musiclinklookupprovider](../systems/wacken/containers/android-application/components/musiclinklookupprovider.md) | Request raw identity evidence |
| [musiclinklookupprovider](../systems/wacken/containers/android-application/components/musiclinklookupprovider.md) | [verifiedmusiclinksprovider](../systems/wacken/containers/android-application/components/verifiedmusiclinksprovider.md) | Use configured collector |
| [verifiedmusiclinksprovider](../systems/wacken/containers/android-application/components/verifiedmusiclinksprovider.md) | [musicbrainzmetadataprovider](../systems/wacken/containers/android-application/components/musicbrainzmetadataprovider.md) | Retrieve authoritative artist identifier |
| [verifiedmusiclinksprovider](../systems/wacken/containers/android-application/components/verifiedmusiclinksprovider.md) | [wikidatametadataprovider](../systems/wacken/containers/android-application/components/wikidatametadataprovider.md) | Corroborate artist and declared website |
| [verifiedmusiclinksprovider](../systems/wacken/containers/android-application/components/verifiedmusiclinksprovider.md) | [officialmusicpageclient](../systems/wacken/containers/android-application/components/officialmusicpageclient.md) | Load bounded public website anchors |
| [officialmusicpageclient](../systems/wacken/containers/android-application/components/officialmusicpageclient.md) | [Official artist websites](../externals/official-artist-websites.md) | Read public platform cross-links |
| [musiclinksreviewactivity](../systems/wacken/containers/android-application/components/musiclinksreviewactivity.md) | [findbandmusiclinksusecase](../systems/wacken/containers/android-application/components/findbandmusiclinksusecase.md) | Explicitly approve reviewed verified identity |
| [findbandmusiclinksusecase](../systems/wacken/containers/android-application/components/findbandmusiclinksusecase.md) | [searchbandmetadatausecase](../systems/wacken/containers/android-application/components/searchbandmetadatausecase.md) | Revalidate evidence and current missing fields |
| [searchbandmetadatausecase](../systems/wacken/containers/android-application/components/searchbandmetadatausecase.md) | [bandrepository](../systems/wacken/containers/android-application/components/bandrepository.md) | Save only canonical missing links |
| [bandrepository](../systems/wacken/containers/android-application/components/bandrepository.md) | [syncedbandrepository](../systems/wacken/containers/android-application/components/syncedbandrepository.md) | Use source-first composed repository |
| [syncedbandrepository](../systems/wacken/containers/android-application/components/syncedbandrepository.md) | [supabasemasterdataclient](../systems/wacken/containers/android-application/components/supabasemasterdataclient.md) | Save acknowledged shared metadata |
| [syncedbandrepository](../systems/wacken/containers/android-application/components/syncedbandrepository.md) | [wackendatabase](../systems/wacken/containers/android-application/components/wackendatabase.md) | Then cache approved links |

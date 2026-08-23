---
id: task-179.2
title: 'US: Add MusicBrainz band metadata search provider'
status: Done
assignee:
  - '@codex'
created_date: '2026-08-23 14:12'
updated_date: '2026-08-23 14:25'
labels:
  - metadata
  - bands
  - external-source
dependencies:
  - task-179.1
parent_task_id: task-179
priority: medium
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Business value: admins need a canonical music-artist source to identify bands and find official relationships before using less structured sources.

User story: As an admin, I want MusicBrainz results in the reviewed metadata search workflow, so that missing band metadata can be proposed from a stable artist identity source before other providers are considered.

Scope: query MusicBrainz for artist candidates by band name, show source and confidence context in the framework proposal review, and use MusicBrainz artist identity, aliases, and official URL relationships when available.

Out of scope: automatic acceptance, overwriting existing metadata, storing aliases, Spotify credentialed enrichment, YouTube credentialed enrichment, and Supabase schema changes unless separately approved.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given a band has missing metadata, when MusicBrainz search is run, then artist candidates matching the band search term are returned as reviewed proposals with source attribution.
- [x] #2 Given MusicBrainz provides aliases or official URL relationships, when proposals are shown, then only missing app metadata fields are proposed and existing metadata remains untouched.
- [x] #3 Given multiple MusicBrainz artists are possible, then the admin must choose a proposal or reject all proposals; no artist is accepted automatically.
- [x] #4 Given MusicBrainz is unavailable or returns no suitable candidate, then the metadata workflow continues without changing the band.
- [x] #5 Architecture impact is assessed before implementation; any external API/client/configuration impact follows the approved metadata framework ADR or updates it.
- [x] #6 Automated tests cover candidate mapping, ambiguous results, unavailable provider behavior, and no-overwrite behavior.
- [x] #7 README, business requirements, diagram, and ADR impact are recorded using delivery-governance wording.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Confirmed MusicBrainz API constraints from official docs: /ws/2 REST API, JSON via fmt=json or Accept, meaningful User-Agent, no API key for read lookup/search, and one request per second.
2. Added MusicBrainzMetadataProvider in the Android module behind BandMetadataLookupProvider.
3. Added provider tests for artist search, relationship lookup mapping, multiple candidate handling, no-key configured state, and provider failure wrapping.
4. Wired MusicBrainz into BandMetadataReviewActivity and moved metadata searches onto a background thread.
5. Updated README and business requirements for MusicBrainz provider behavior and MUSICBRAINZ_USER_AGENT configuration. ADR 0012 remains the governing architecture decision.
6. Ran focused application and Android unit tests.

Design outcome: Android adapter only; application/domain boundaries remain unchanged after task-179.1.
Architecture approval: covered by the approved reviewed metadata framework and ADR 0012.
Deviation: MusicBrainz can propose Spotify/YouTube URL relationship fields, but not biography or image fields; those remain for later Wikidata/Wikipedia/provider stories.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Added MusicBrainz as the first reviewed external metadata provider. The Android adapter searches MusicBrainz artist candidates, looks up URL relationships, maps Spotify and YouTube relationship URLs into metadata proposals, and reports failures without changing band data. The Settings metadata review screen now uses MusicBrainz through the application provider boundary and performs searches on a background thread.

## Acceptance criteria validation

- AC1: MusicBrainz artist search results are converted into reviewed provider candidates with MusicBrainz source URLs and confidence scores.
- AC2: Artist URL relationships are mapped only into currently missing app metadata fields by the framework; existing metadata remains untouched.
- AC3: Multiple MusicBrainz candidates are returned as separate proposals for the review screen; no candidate is selected or saved automatically.
- AC4: MusicBrainz IO/JSON failures are wrapped as lookup failures, reported by the framework, and do not change bands.
- AC5: Architecture impact follows ADR 0012; no new domain, schema, or Supabase contract change was needed.
- AC6: Automated tests cover provider candidate mapping, multiple candidates, provider configured-without-key behavior, provider failure, and framework no-overwrite behavior.
- AC7: README, business requirements, diagram, and ADR impact are recorded below.

## How to test

### Automated tests

- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :application:test :app:testDebugUnitTest --tests be.wacken.planner.application.BandMetadataSearchFrameworkUseCaseTest --tests be.wacken.planner.MusicBrainzMetadataProviderTest --tests be.wacken.planner.BandMetadataReviewActivityRegressionTest --tests be.wacken.planner.SettingsActivityRegressionTest

### Manual validation

- Build with a meaningful MUSICBRAINZ_USER_AGENT, open Settings, tap Fetch band metadata, and review MusicBrainz Spotify/YouTube proposals before saving accepted fields.

## TDD / BDD / approval-test evidence

- TDD: added provider tests for JSON/path mapping and failure behavior before validating the adapter.
- BDD: task acceptance criteria map to reviewed metadata search behavior.
- Approval tests: not used; no legacy refactoring required.

## Architecture impact

- Architecture-significant change: yes, external MusicBrainz API adapter added behind the approved provider port.
- Approval received: yes, covered by user request to start the framework and first integration after the approval gate.
- ADR: follows ADR 0012; no new ADR needed.

## README impact

README impact: updated external metadata behavior, MusicBrainz provider notes, and MUSICBRAINZ_USER_AGENT configuration.

## Business requirements impact

Business requirements impact: updated external metadata source notes to identify MusicBrainz as the first implemented provider.

## Diagram impact

Diagram impact: none, because MusicBrainz uses the already-documented external metadata boundary and does not add a new app container.

## Commits / logical change list

- Added MusicBrainz provider adapter and HTTP client with User-Agent and rate-limit behavior.
- Wired MusicBrainz into the metadata review screen on a background thread.
- Added focused provider tests and documentation updates.

## Risks and follow-up

- MusicBrainz provides artist identity and URL relationships, not biography/image metadata. Wikidata/Wikipedia remain follow-up stories for those fields.
- The default User-Agent should be overridden for production-like builds with maintainer contact details.
- Live MusicBrainz availability is not exercised in unit tests; adapter tests use deterministic fakes.
<!-- SECTION:NOTES:END -->

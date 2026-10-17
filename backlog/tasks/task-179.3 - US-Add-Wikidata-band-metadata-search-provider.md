---
id: task-179.3
title: 'US: Add Wikidata band metadata search provider'
status: Done
assignee:
  - '@codex'
created_date: '2026-08-23 14:12'
updated_date: '2026-08-24 06:36'
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
Business value: admins need structured, source-linked public facts to enrich band records after the own catalog and canonical artist identity checks.

User story: As an admin, I want Wikidata results in the reviewed metadata search workflow, so that missing band image, official site, social, or identifier metadata can be proposed from structured public data.

Scope: query Wikidata for band/artist entities by search term or upstream provider identity when available, map only relevant missing fields into metadata proposals, and show source attribution for review.

Out of scope: unreviewed saves, overwriting existing metadata, broad knowledge-graph browsing, alias persistence, and Supabase schema changes unless separately approved.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given a band has missing metadata, when Wikidata search is run, then relevant band or artist entity candidates are shown as reviewed proposals with source attribution.
- [x] #2 Given an upstream provider identity is available, when Wikidata lookup runs, then matching by that identity is preferred over broad text search where supported.
- [x] #3 Given Wikidata proposes fields already present on the band, then those fields are not automatically overwritten or selected for save.
- [x] #4 Given multiple Wikidata entities are possible, then the admin must choose one or reject all proposals before any band update occurs.
- [x] #5 Given Wikidata is unavailable or returns no suitable candidate, then the metadata workflow continues without changing the band.
- [x] #6 Architecture impact is assessed before implementation; any external API/client/configuration impact follows the approved metadata framework ADR or updates it.
- [x] #7 Automated tests cover entity mapping, ambiguous results, unavailable provider behavior, identity-assisted lookup where present, and no-overwrite behavior.
- [x] #8 README, business requirements, diagram, and ADR impact are recorded using delivery-governance wording.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Confirmed Wikidata API shape from official Wikibase docs: Action API endpoint, wbsearchentities for label/alias search, wbgetentities for entity labels/descriptions/claims, and JSON format.
2. Added WikidataMetadataProvider in the Android module behind BandMetadataLookupProvider.
3. Mapped relevant claims into reviewed missing-field proposals: P18 image, P1902 Spotify artist ID, and P2397 YouTube channel ID. Biography/text remains for the later Wikipedia story.
4. Added focused tests for entity mapping, multiple candidates, unavailable provider behavior, direct Wikidata Q-id lookup, configured-without-key behavior, and existing framework no-overwrite behavior.
5. Wired Wikidata after MusicBrainz in the Settings metadata review provider list and updated source-regression coverage.
6. Updated README/business requirements impact notes and ran focused validation.

Design outcome: Android adapter behind ADR 0012 provider boundary; no domain, persistence, Supabase schema, or alias storage changes.
Architecture approval: follows ADR 0012; no new ADR required.
Deviation: no persisted upstream identity exists yet, so the live UI path uses search terms; direct Wikidata Q-id lookup is supported for provider identity inputs where available.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Added Wikidata as a reviewed metadata provider. The Android adapter uses Wikibase Action API search/entity reads, returns multiple candidate entities for review, maps Wikidata claims into image, Spotify, and YouTube proposals, and reports provider failures without changing band data. The Settings metadata review workflow now runs MusicBrainz first and Wikidata second.

Sources checked: official Wikibase API docs for `wbsearchentities`, `wbgetentities`, endpoint, and JSON request behavior.

## Acceptance criteria validation

- AC1: Wikidata entity search results are returned as reviewed provider candidates with source attribution to `https://www.wikidata.org/wiki/Q...`.
- AC2: Direct Wikidata Q-id lookup is supported and skips broad search when such an identity is supplied; the current UI path still uses band-name search because no upstream identity is persisted yet.
- AC3: The framework only proposes missing fields and no-overwrite behavior remains covered by tests.
- AC4: Multiple Wikidata entities are returned as separate proposals for admin review; no automatic selection or save occurs.
- AC5: Provider IO/JSON failures are wrapped as lookup failures and do not change bands.
- AC6: Architecture follows ADR 0012; no domain, persistence, Supabase schema, or new ADR change was needed.
- AC7: Automated tests cover entity mapping, ambiguous results, unavailable provider behavior, direct entity-id lookup, configured-without-key behavior, and no-overwrite behavior through the framework tests.
- AC8: README, business requirements, diagram, and ADR impact are recorded below.

## How to test

### Automated tests

- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :application:test :app:testDebugUnitTest --tests be.wacken.planner.application.BandMetadataSearchFrameworkUseCaseTest --tests be.wacken.planner.WikidataMetadataProviderTest --tests be.wacken.planner.BandMetadataReviewActivityRegressionTest --tests be.wacken.planner.SettingsActivityRegressionTest

### Manual validation

- Open Settings, tap Fetch band metadata, and review Wikidata image/Spotify/YouTube proposals after MusicBrainz proposals. Save only selected proposals.

## TDD / BDD / approval-test evidence

- TDD: added focused Wikidata provider tests before validating the adapter behavior.
- BDD: task acceptance criteria are mapped to provider and review workflow behavior.
- Approval tests: not used; no legacy refactoring required.

## Architecture impact

- Architecture-significant change: yes, external Wikidata API adapter added behind the approved provider port.
- Approval received: covered by user request to continue metadata integrations and ADR 0012.
- ADR: follows ADR 0012; no new ADR required.

## README impact

README impact: updated reviewed external metadata provider notes to include Wikidata.

## Business requirements impact

Business requirements impact: updated external metadata source notes to identify Wikidata as implemented for structured image, Spotify, and YouTube proposals.

## Diagram impact

Diagram impact: none, because Wikidata uses the already-documented external metadata boundary and does not add a new app container.

## ADR impact

ADR impact: none, because ADR 0012 already governs reviewed external metadata providers.

## Commits / logical change list

- Added Wikidata provider adapter and tests.
- Wired Wikidata into the metadata review screen after MusicBrainz.
- Updated README and business requirements.

## Risks and follow-up

- Wikidata descriptions are not used as biographies; the Wikipedia story remains responsible for neutral biography/summary text.
- The live UI does not yet pass persisted upstream identities because no such metadata is stored; direct Wikidata Q-id lookup is available when an identity is supplied.
- Live Wikidata availability is not exercised in unit tests; adapter tests use deterministic fakes.
<!-- SECTION:NOTES:END -->

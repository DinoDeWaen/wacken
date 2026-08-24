---
id: task-179.5
title: 'US: Add Spotify band metadata search provider'
status: Done
assignee:
  - '@codex'
created_date: '2026-08-23 14:12'
updated_date: '2026-08-24 11:22'
labels:
  - metadata
  - bands
  - external-source
  - configuration
dependencies:
  - task-179.1
parent_task_id: task-179
priority: medium
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Business value: admins need accurate Spotify artist links and images when available, while keeping credentialed API use explicit and optional.

User story: As an admin, I want Spotify artist proposals in the reviewed metadata search workflow when Spotify is configured, so that missing Spotify links and suitable artist images can be added to the golden-source band record after approval.

Scope: use configured Spotify Web API credentials or an approved app configuration path, search artist candidates by band name or upstream identity where available, and propose missing Spotify URL/image fields for review.

Out of scope: hard-coded secrets, user Spotify account login, playback features, automatic saves, overwriting existing metadata, and failing the metadata workflow when Spotify is not configured.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given Spotify API configuration is present, when a band has missing Spotify or image metadata, then Spotify artist candidates are shown as reviewed proposals with source attribution.
- [x] #2 Given Spotify API configuration is absent, when metadata search runs, then Spotify is reported as not configured and the rest of the metadata workflow continues.
- [x] #3 Given multiple Spotify artists match the search term, then the admin must choose one candidate or reject all proposals before any band update occurs.
- [x] #4 Given a band already has Spotify or image metadata, then Spotify proposals do not overwrite those fields automatically.
- [x] #5 Architecture impact is assessed before implementation; credential/configuration handling follows the approved metadata framework ADR or updates it before coding.
- [x] #6 Automated tests cover configured lookup, missing configuration, ambiguous candidates, provider failure, and no-overwrite behavior.
- [x] #7 README documents required Spotify configuration or states that Spotify enrichment is optional when not configured.
- [x] #8 Business requirements, diagram, and ADR impact are recorded using delivery-governance wording.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Add focused tests for Spotify metadata lookup: configured search, multiple artist candidates, missing configuration, provider failure, and framework-level no-overwrite behavior.
2. Add optional Spotify BuildConfig configuration from Gradle properties or environment variables without committing secrets.
3. Implement a Spotify Web API provider using client credentials only when configured, mapping artist external URL and image proposals into the existing reviewed provider boundary.
4. Wire Spotify into the Settings metadata review provider list after the no-key public metadata sources.
5. Update README and business requirements with optional Spotify configuration and delivery-governance impact notes.
6. Run focused tests, close the task with validation evidence, commit, and push.
Architecture impact: not architecture-significant beyond ADR 0012; this adds a credentialed adapter behind the existing metadata lookup provider boundary and uses optional build-time configuration with no persistence/schema/domain changes.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Implemented optional SpotifyMetadataProvider using Spotify Web API client-credentials token exchange and artist catalog search. The provider is disabled unless SPOTIFY_CLIENT_ID and SPOTIFY_CLIENT_SECRET are supplied as Gradle properties or environment variables; no secrets are committed. Spotify artist candidates propose missing Spotify URL and image metadata only through the reviewed metadata workflow.

Official docs checked: Spotify Client Credentials Flow, Web API Search for Item, and Authorization guidance. Architecture note: Spotify documents that client credentials requires a secret and that mobile/public clients should avoid unsafe secret storage; README documents this and recommends moving token exchange behind a backend before broad distribution with production credentials.

Validation: JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :application:test :app:testDebugUnitTest --tests be.wacken.planner.application.BandMetadataSearchFrameworkUseCaseTest --tests be.wacken.planner.SpotifyMetadataProviderTest --tests be.wacken.planner.BandMetadataReviewActivityRegressionTest --tests be.wacken.planner.SettingsActivityRegressionTest passed.

Delivery impact: README impact: documented optional Spotify configuration and mobile-secret caveat. Business requirements impact: marked Spotify Web API as implemented optional provider and kept YouTube as planned. Diagram impact: none; no new system category beyond existing external metadata providers. ADR impact: no new ADR; implementation follows ADR 0012 reviewed metadata provider boundary. Architecture impact: adapter/configuration-only change, no persistence/schema/domain changes.
<!-- SECTION:NOTES:END -->

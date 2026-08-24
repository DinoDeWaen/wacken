---
id: task-179.6
title: 'US: Add YouTube band channel metadata search provider'
status: Done
assignee:
  - '@codex'
created_date: '2026-08-23 14:12'
updated_date: '2026-08-24 11:31'
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
Business value: admins need official YouTube channel proposals for bands without forcing manual web searches during festival data cleanup.

User story: As an admin, I want YouTube channel proposals in the reviewed metadata search workflow when YouTube is configured, so that missing official YouTube links can be added to the golden-source band record after approval.

Scope: use configured YouTube Data API access or an approved app configuration path, search official channel candidates by band name or upstream identity where available, and propose missing YouTube URL fields for review with enough context to choose safely.

Out of scope: hard-coded secrets, user YouTube login, video playback/search features, automatic saves, overwriting existing metadata, and failing the metadata workflow when YouTube is not configured.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given YouTube API configuration is present, when a band has missing YouTube metadata, then YouTube channel candidates are shown as reviewed proposals with source attribution.
- [x] #2 Given YouTube API configuration is absent, when metadata search runs, then YouTube is reported as not configured and the rest of the metadata workflow continues.
- [x] #3 Given multiple YouTube channels match the search term, then the admin must choose one candidate or reject all proposals before any band update occurs.
- [x] #4 Given a band already has a YouTube link, then YouTube proposals do not overwrite it automatically.
- [x] #5 Architecture impact is assessed before implementation; credential/configuration handling follows the approved metadata framework ADR or updates it before coding.
- [x] #6 Automated tests cover configured lookup, missing configuration, ambiguous candidates, provider failure, and no-overwrite behavior.
- [x] #7 README documents required YouTube configuration or states that YouTube enrichment is optional when not configured.
- [x] #8 Business requirements, diagram, and ADR impact are recorded using delivery-governance wording.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Add focused tests for YouTube metadata lookup: configured channel search, missing API key, multiple channel candidates, provider failure, and framework-level no-overwrite behavior.
2. Add optional YOUTUBE_API_KEY BuildConfig configuration from Gradle properties or environment variables without committing secrets.
3. Implement a YouTube Data API provider using search.list channel queries, mapping channel URLs into the existing reviewed provider boundary.
4. Wire YouTube into the Settings metadata review provider list after the other providers.
5. Update README and business requirements with optional YouTube configuration and delivery-governance impact notes.
6. Run focused tests, close the task with validation evidence, commit, and push.
Architecture impact: not architecture-significant beyond ADR 0012; this adds another optional adapter behind the approved metadata lookup provider boundary, with no persistence/schema/domain changes.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Implemented optional YouTubeMetadataProvider using YouTube Data API search.list channel queries. The provider is disabled unless YOUTUBE_API_KEY is supplied as a Gradle property or environment variable; no API keys are committed. Channel candidates propose missing YouTube URL metadata only through the reviewed metadata workflow.

Official docs checked: YouTube Data API search.list reference and search implementation guide. The provider uses part=snippet, type=channel, q, maxResults, and key query parameters, with no user login, playback, or video search scope.

Validation: JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :application:test :app:testDebugUnitTest --tests be.wacken.planner.application.BandMetadataSearchFrameworkUseCaseTest --tests be.wacken.planner.YouTubeMetadataProviderTest --tests be.wacken.planner.BandMetadataReviewActivityRegressionTest --tests be.wacken.planner.SettingsActivityRegressionTest passed.

Delivery impact: README impact: documented optional YouTube API key configuration. Business requirements impact: marked YouTube Data API as implemented optional provider. Diagram impact: none; no new system category beyond existing external metadata providers. ADR impact: no new ADR; implementation follows ADR 0012 reviewed metadata provider boundary. Architecture impact: adapter/configuration-only change, no persistence/schema/domain changes.
<!-- SECTION:NOTES:END -->

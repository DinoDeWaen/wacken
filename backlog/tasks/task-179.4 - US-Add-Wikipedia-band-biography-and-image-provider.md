---
id: task-179.4
title: 'US: Add Wikipedia band biography and image provider'
status: Done
assignee:
  - '@codex'
created_date: '2026-08-23 14:12'
updated_date: '2026-08-24 06:40'
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
Business value: admins need readable biography text and image candidates for bands whose imported metadata is incomplete.

User story: As an admin, I want Wikipedia summary and image proposals in the reviewed metadata search workflow, so that missing biography/text and picture fields can be filled from a neutral public source when the artist identity is unambiguous.

Scope: search or resolve Wikipedia pages for a band, propose neutral summary text and page/image URLs for missing fields only, and show source attribution plus enough context for review.

Out of scope: scraping arbitrary page HTML, saving unreviewed text, overwriting existing biography/image metadata, translating biographies, and resolving highly ambiguous artists without user confirmation.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given a band is missing biography or image metadata, when Wikipedia lookup finds an unambiguous page, then summary and image proposals are shown with source attribution for review.
- [x] #2 Given a linked Wikidata identity is available, when Wikipedia lookup runs, then the identity-linked page is preferred over broad title search where supported.
- [x] #3 Given multiple or ambiguous Wikipedia pages are possible, then the admin must choose one page or reject all proposals; no page is accepted automatically.
- [x] #4 Given Wikipedia proposes biography or image values for fields already present on the band, then those fields are not overwritten automatically.
- [x] #5 Given Wikipedia is unavailable or no suitable page is found, then the metadata workflow continues without changing the band.
- [x] #6 Architecture impact is assessed before implementation; any external API/client/configuration impact follows the approved metadata framework ADR or updates it.
- [x] #7 Automated tests cover summary/image proposal mapping, ambiguous page handling, unavailable provider behavior, identity-assisted lookup where present, and no-overwrite behavior.
- [x] #8 README, business requirements, diagram, and ADR impact are recorded using delivery-governance wording.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Add focused tests for Wikipedia metadata lookup: summary/image mapping, ambiguous candidates, unavailable provider handling, identity-assisted lookup, and no-overwrite behavior through the existing application framework.
2. Implement a Wikipedia metadata provider using Wikimedia APIs without scraping HTML or persisting unreviewed values.
3. Wire Wikipedia into the existing reviewed metadata workflow after MusicBrainz and Wikidata.
4. Update README and business requirements with Wikipedia provider scope and delivery-governance impact notes.
5. Run focused application and Android unit tests, then close the task with validation evidence.
Architecture impact: not architecture-significant beyond ADR 0012; this adds another adapter implementing the approved metadata lookup provider boundary, with no persistence/schema/domain boundary changes.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Implemented WikipediaMetadataProvider using Wikimedia APIs: title search or direct Wikidata QID sitelink resolution, then English Wikipedia page summaries for neutral biography and image proposals. Wired Wikipedia into the Settings reviewed metadata workflow after MusicBrainz and Wikidata.

Validation: JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :application:test :app:testDebugUnitTest --tests be.wacken.planner.application.BandMetadataSearchFrameworkUseCaseTest --tests be.wacken.planner.WikipediaMetadataProviderTest --tests be.wacken.planner.BandMetadataReviewActivityRegressionTest --tests be.wacken.planner.SettingsActivityRegressionTest passed.

Delivery impact: README impact: updated External Metadata and technology summary to include Wikipedia. Business requirements impact: updated External music metadata source notes to mark Wikipedia implemented and keep Spotify/YouTube as separate planned providers. Diagram impact: none; no system boundary changed beyond the existing external metadata provider category. ADR impact: no new ADR; implementation follows ADR 0012 reviewed metadata provider boundary. Architecture impact: not architecture-significant; adapter-only provider with no persistence/schema/domain changes.
<!-- SECTION:NOTES:END -->

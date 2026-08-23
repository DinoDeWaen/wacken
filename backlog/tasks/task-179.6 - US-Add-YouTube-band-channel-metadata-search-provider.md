---
id: task-179.6
title: 'US: Add YouTube band channel metadata search provider'
status: To Do
assignee: []
created_date: '2026-08-23 14:12'
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
- [ ] #1 Given YouTube API configuration is present, when a band has missing YouTube metadata, then YouTube channel candidates are shown as reviewed proposals with source attribution.
- [ ] #2 Given YouTube API configuration is absent, when metadata search runs, then YouTube is reported as not configured and the rest of the metadata workflow continues.
- [ ] #3 Given multiple YouTube channels match the search term, then the admin must choose one candidate or reject all proposals before any band update occurs.
- [ ] #4 Given a band already has a YouTube link, then YouTube proposals do not overwrite it automatically.
- [ ] #5 Architecture impact is assessed before implementation; credential/configuration handling follows the approved metadata framework ADR or updates it before coding.
- [ ] #6 Automated tests cover configured lookup, missing configuration, ambiguous candidates, provider failure, and no-overwrite behavior.
- [ ] #7 README documents required YouTube configuration or states that YouTube enrichment is optional when not configured.
- [ ] #8 Business requirements, diagram, and ADR impact are recorded using delivery-governance wording.
<!-- AC:END -->

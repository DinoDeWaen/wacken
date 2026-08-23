---
id: task-179.5
title: 'US: Add Spotify band metadata search provider'
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
Business value: admins need accurate Spotify artist links and images when available, while keeping credentialed API use explicit and optional.

User story: As an admin, I want Spotify artist proposals in the reviewed metadata search workflow when Spotify is configured, so that missing Spotify links and suitable artist images can be added to the golden-source band record after approval.

Scope: use configured Spotify Web API credentials or an approved app configuration path, search artist candidates by band name or upstream identity where available, and propose missing Spotify URL/image fields for review.

Out of scope: hard-coded secrets, user Spotify account login, playback features, automatic saves, overwriting existing metadata, and failing the metadata workflow when Spotify is not configured.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given Spotify API configuration is present, when a band has missing Spotify or image metadata, then Spotify artist candidates are shown as reviewed proposals with source attribution.
- [ ] #2 Given Spotify API configuration is absent, when metadata search runs, then Spotify is reported as not configured and the rest of the metadata workflow continues.
- [ ] #3 Given multiple Spotify artists match the search term, then the admin must choose one candidate or reject all proposals before any band update occurs.
- [ ] #4 Given a band already has Spotify or image metadata, then Spotify proposals do not overwrite those fields automatically.
- [ ] #5 Architecture impact is assessed before implementation; credential/configuration handling follows the approved metadata framework ADR or updates it before coding.
- [ ] #6 Automated tests cover configured lookup, missing configuration, ambiguous candidates, provider failure, and no-overwrite behavior.
- [ ] #7 README documents required Spotify configuration or states that Spotify enrichment is optional when not configured.
- [ ] #8 Business requirements, diagram, and ADR impact are recorded using delivery-governance wording.
<!-- AC:END -->

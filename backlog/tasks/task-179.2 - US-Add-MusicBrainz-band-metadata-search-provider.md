---
id: task-179.2
title: 'US: Add MusicBrainz band metadata search provider'
status: To Do
assignee: []
created_date: '2026-08-23 14:12'
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
- [ ] #1 Given a band has missing metadata, when MusicBrainz search is run, then artist candidates matching the band search term are returned as reviewed proposals with source attribution.
- [ ] #2 Given MusicBrainz provides aliases or official URL relationships, when proposals are shown, then only missing app metadata fields are proposed and existing metadata remains untouched.
- [ ] #3 Given multiple MusicBrainz artists are possible, then the admin must choose a proposal or reject all proposals; no artist is accepted automatically.
- [ ] #4 Given MusicBrainz is unavailable or returns no suitable candidate, then the metadata workflow continues without changing the band.
- [ ] #5 Architecture impact is assessed before implementation; any external API/client/configuration impact follows the approved metadata framework ADR or updates it.
- [ ] #6 Automated tests cover candidate mapping, ambiguous results, unavailable provider behavior, and no-overwrite behavior.
- [ ] #7 README, business requirements, diagram, and ADR impact are recorded using delivery-governance wording.
<!-- AC:END -->

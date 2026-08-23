---
id: task-179.3
title: 'US: Add Wikidata band metadata search provider'
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
Business value: admins need structured, source-linked public facts to enrich band records after the own catalog and canonical artist identity checks.

User story: As an admin, I want Wikidata results in the reviewed metadata search workflow, so that missing band image, official site, social, or identifier metadata can be proposed from structured public data.

Scope: query Wikidata for band/artist entities by search term or upstream provider identity when available, map only relevant missing fields into metadata proposals, and show source attribution for review.

Out of scope: unreviewed saves, overwriting existing metadata, broad knowledge-graph browsing, alias persistence, and Supabase schema changes unless separately approved.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given a band has missing metadata, when Wikidata search is run, then relevant band or artist entity candidates are shown as reviewed proposals with source attribution.
- [ ] #2 Given an upstream provider identity is available, when Wikidata lookup runs, then matching by that identity is preferred over broad text search where supported.
- [ ] #3 Given Wikidata proposes fields already present on the band, then those fields are not automatically overwritten or selected for save.
- [ ] #4 Given multiple Wikidata entities are possible, then the admin must choose one or reject all proposals before any band update occurs.
- [ ] #5 Given Wikidata is unavailable or returns no suitable candidate, then the metadata workflow continues without changing the band.
- [ ] #6 Architecture impact is assessed before implementation; any external API/client/configuration impact follows the approved metadata framework ADR or updates it.
- [ ] #7 Automated tests cover entity mapping, ambiguous results, unavailable provider behavior, identity-assisted lookup where present, and no-overwrite behavior.
- [ ] #8 README, business requirements, diagram, and ADR impact are recorded using delivery-governance wording.
<!-- AC:END -->

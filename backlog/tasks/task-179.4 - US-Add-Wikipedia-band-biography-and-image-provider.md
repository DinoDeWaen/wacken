---
id: task-179.4
title: 'US: Add Wikipedia band biography and image provider'
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
Business value: admins need readable biography text and image candidates for bands whose imported metadata is incomplete.

User story: As an admin, I want Wikipedia summary and image proposals in the reviewed metadata search workflow, so that missing biography/text and picture fields can be filled from a neutral public source when the artist identity is unambiguous.

Scope: search or resolve Wikipedia pages for a band, propose neutral summary text and page/image URLs for missing fields only, and show source attribution plus enough context for review.

Out of scope: scraping arbitrary page HTML, saving unreviewed text, overwriting existing biography/image metadata, translating biographies, and resolving highly ambiguous artists without user confirmation.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given a band is missing biography or image metadata, when Wikipedia lookup finds an unambiguous page, then summary and image proposals are shown with source attribution for review.
- [ ] #2 Given a linked Wikidata identity is available, when Wikipedia lookup runs, then the identity-linked page is preferred over broad title search where supported.
- [ ] #3 Given multiple or ambiguous Wikipedia pages are possible, then the admin must choose one page or reject all proposals; no page is accepted automatically.
- [ ] #4 Given Wikipedia proposes biography or image values for fields already present on the band, then those fields are not overwritten automatically.
- [ ] #5 Given Wikipedia is unavailable or no suitable page is found, then the metadata workflow continues without changing the band.
- [ ] #6 Architecture impact is assessed before implementation; any external API/client/configuration impact follows the approved metadata framework ADR or updates it.
- [ ] #7 Automated tests cover summary/image proposal mapping, ambiguous page handling, unavailable provider behavior, identity-assisted lookup where present, and no-overwrite behavior.
- [ ] #8 README, business requirements, diagram, and ADR impact are recorded using delivery-governance wording.
<!-- AC:END -->

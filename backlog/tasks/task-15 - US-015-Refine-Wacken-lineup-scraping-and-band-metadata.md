---
id: task-15
title: 'US-015: Refine Wacken lineup scraping and band metadata'
status: To Do
assignee: []
created_date: '2026-05-15 06:33'
labels:
  - refinement
  - data
  - rating
dependencies:
  - task-12
priority: medium
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## US-015: Refine Wacken lineup scraping and band metadata

**As a** product owner
**I want** the Wacken lineup pages investigated for available band metadata and scraping constraints
**So that** the initial rating import can use real data without guessing the source schema

### Notes
- Source: `backlog/docs/business-requirements.md` open questions about Wacken scraping fields and constraints.
- Inspect the official band list and band detail pages.
- This is a refinement story; output is documented decisions and follow-up implementation stories if needed.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given the official Wacken band list and band detail pages When they are investigated Then the available band fields are documented
- [ ] #2 Given website data is dynamic or unavailable statically When scraping is assessed Then the technical approach and limitations are documented
- [ ] #3 Given YouTube or Spotify links are desired When band metadata is reviewed Then the source and availability of those links are documented
- [ ] #4 Given scraping may have legal or terms constraints When the source is assessed Then any constraints or approval needs are documented before implementation
<!-- AC:END -->

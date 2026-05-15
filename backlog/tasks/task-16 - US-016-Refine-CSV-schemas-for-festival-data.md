---
id: task-16
title: 'US-016: Refine CSV schemas for festival data'
status: To Do
assignee: []
created_date: '2026-05-15 06:33'
labels:
  - refinement
  - data
  - csv
dependencies:
  - task-6
  - task-15
priority: medium
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## US-016: Refine CSV schemas for festival data

**As a** developer
**I want** explicit CSV schemas for bands, stages, performances, distances, and food
**So that** imports can be validated consistently once final lineup data is available

### Notes
- Source: `backlog/docs/business-requirements.md` open question about exact CSV schemas.
- Do not implement parser changes in this refinement story unless a separate implementation task is created.
- Account for early band-only data and later final stage/time data.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given the required festival data concepts When schemas are refined Then bands, stages, performances, distances, and food CSV columns are documented
- [ ] #2 Given final stage and time data may arrive later When schemas are refined Then the band-only early import shape and final running-order shape are both covered
- [ ] #3 Given validation requirements exist When schemas are refined Then missing references, overlaps, and unknown stage checks are mapped to schema fields
- [ ] #4 Given the schema is ready When the refinement is complete Then follow-up implementation tasks are identified if task-6 is not sufficient
<!-- AC:END -->

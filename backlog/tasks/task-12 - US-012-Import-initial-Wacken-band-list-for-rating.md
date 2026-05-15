---
id: task-12
title: 'US-012: Import initial Wacken band list for rating'
status: To Do
assignee: []
created_date: '2026-05-15 06:32'
labels:
  - mvp1
  - rating
  - data
dependencies:
  - task-4
  - task-5
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## US-012: Import initial Wacken band list for rating

**As an** attendee
**I want** the current Wacken band list available before final stage times are known
**So that** my group can start rating bands as the first product priority

### Notes
- Source: `backlog/docs/business-requirements.md` BR-035 and rating-first business goal.
- This story may use a manually supplied file or a simple importer, but must not invent final performance/stage/time schemas.
- If website scraping is used, keep it isolated behind an adapter and preserve domain/application boundaries.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given an initial source that contains band names When the import runs Then the app stores bands that can be listed and rated
- [ ] #2 Given final stage and performance times are not available When bands are imported Then the app still supports band-only records for rating
- [ ] #3 Given the import source contains duplicate band names When the import runs Then duplicate bands are not created
- [ ] #4 Given imported bands are available When I open the band list Then each imported band can be selected for rating
<!-- AC:END -->

---
id: task-12
title: 'US-012: Import initial Wacken band list for rating'
status: In Progress
assignee:
  - '@codex'
created_date: '2026-05-15 06:32'
updated_date: '2026-05-15 09:55'
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

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Add an application-level import use case that accepts an initial band-name source as plain band names and stores valid bands through `BandRepository`.
2. Add application tests first for importing band-only data, allowing missing performance/stage/time data, ignoring duplicate band names, and keeping imported bands available through the repository.
3. Keep source parsing minimal and schema-free so this does not invent final CSV or Wacken scraping details.
4. Add an infrastructure parser only if needed for plain text/newline band names; do not add website scraping in this story.
5. Validate with `./gradlew test` and `./gradlew assembleDebug`.
6. README impact likely not needed unless a new command or setup step is added.

Test strategy: TDD in application/infrastructure around band-only import. No UI test because band list UI is task-7.
Architecture impact: not architecture-significant; this uses existing domain repository ports and module boundaries from ADR 0001 and ADR 0004. No new ADR expected.
Risks/assumptions: AC4 mentions opening the band list, but the band list UI is task-7; this story validates that imported bands are available to be listed/rated through the repository.
<!-- SECTION:PLAN:END -->

---
id: task-15
title: 'US-015: Refine Wacken lineup scraping and band metadata'
status: Done
assignee:
  - '@codex'
created_date: '2026-05-15 06:33'
updated_date: '2026-05-15 11:59'
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
- [x] #1 Given the official Wacken band list and band detail pages When they are investigated Then the available band fields are documented
- [x] #2 Given website data is dynamic or unavailable statically When scraping is assessed Then the technical approach and limitations are documented
- [x] #3 Given YouTube or Spotify links are desired When band metadata is reviewed Then the source and availability of those links are documented
- [x] #4 Given scraping may have legal or terms constraints When the source is assessed Then any constraints or approval needs are documented before implementation
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Inspected official Wacken band list and artist-detail hash routes, plus the page-declared JSON feed.
2. Checked official site signals relevant to scraping approval: `robots.txt` returned 404, and the imprint/data-protection page did not grant explicit scraping permission.
3. Documented available metadata, limitations, YouTube/Spotify availability, recommended technical approach, and approval constraints in `backlog/docs/wacken-lineup-scraping-refinement.md`.
4. Identified follow-up implementation impacts for initial import, detail links, and CSV metadata.
5. Validated by reviewing the documentation; no build required because only docs/task metadata changed.
6. README update not needed because setup, commands, architecture, and run behavior did not change.

Architecture impact: not architecture-significant; documentation/refinement only, no code or module boundary changes.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Documented the Wacken lineup scraping findings in `backlog/docs/wacken-lineup-scraping-refinement.md`. The official band page is client-rendered, but it declares a usable JSON feed at `https://www.wacken.com/fileadmin/Json/bandlist-concert.json`. On 2026-05-15 that feed returned 164 concert entries with names, ids, slugs, biographies, images, YouTube media, Spotify ids, social links, and partial event data.

## Acceptance criteria validation

- AC1: Available fields are documented in the `Available Band Fields` section.
- AC2: Dynamic page limitations and JSON-feed approach are documented in `Dynamic Page And Scraping Approach`.
- AC3: YouTube and Spotify availability are documented in `YouTube And Spotify`.
- AC4: `robots.txt`, imprint/data-protection findings, and approval constraints are documented in `Legal / Approval Constraints`.

## How to test

### Automated tests

No automated tests were run; this was a documentation-only refinement.

### Manual validation

Reviewed the official band list page, artist-detail hash route, discovered JSON feed, imprint/data-protection page, and `robots.txt` response. Reviewed the generated refinement document.

## TDD / BDD / approval-test evidence

Not applicable; refinement documentation only.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR: not needed.

## README impact

README not updated because setup, commands, architecture, troubleshooting, and documented run behavior did not change.

## Diagram impact

No diagram update needed because architecture diagrams did not change.

## Commits / logical change list

- Added Wacken lineup scraping refinement documentation.
- Updated task-15 status, plan, acceptance criteria, and validation evidence.

## Risks and follow-up

The JSON feed includes partial/historical event data, so final running-order import must not treat those events as authoritative without later verification. Product-owner approval is still recommended before unattended scraping implementation.
<!-- SECTION:NOTES:END -->

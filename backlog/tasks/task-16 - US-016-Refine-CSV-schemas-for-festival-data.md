---
id: task-16
title: 'US-016: Refine CSV schemas for festival data'
status: Done
assignee:
  - '@codex'
created_date: '2026-05-15 06:33'
updated_date: '2026-05-15 12:01'
labels:
  - refinement
  - data
  - csv
dependencies:
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
- [x] #1 Given the required festival data concepts When schemas are refined Then bands, stages, performances, distances, and food CSV columns are documented
- [x] #2 Given final stage and time data may arrive later When schemas are refined Then the band-only early import shape and final running-order shape are both covered
- [x] #3 Given validation requirements exist When schemas are refined Then missing references, overlaps, and unknown stage checks are mapped to schema fields
- [x] #4 Given the schema is ready When the refinement is complete Then follow-up implementation tasks are identified if task-6 is not sufficient
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Defined versioned CSV schema assumptions from business requirements and task-15 findings.
2. Documented early band-only import shape separately from final running-order CSVs.
3. Documented bands, stages, performances, distances, and food columns with required/optional status and validation rules in `backlog/docs/festival-data-csv-schemas.md`.
4. Mapped missing references, unknown stages, duplicate ids, invalid times, walking-minute errors, and overlapping performances to concrete fields and error messages.
5. Identified follow-up implementation changes for task-6 and metadata import; updated task-6 to depend on task-16.
6. Validated by documentation review; no build required because only docs/task metadata changed.

Architecture impact: not architecture-significant; documentation/refinement only, no code or module boundary changes.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Documented the MVP CSV import contract in `backlog/docs/festival-data-csv-schemas.md`. The document covers the early band-only import and the final running-order import for `bands.csv`, `stages.csv`, `performances.csv`, `distances.csv`, and `food.csv`. It also maps validation errors to concrete schema fields.

Updated task-6 to depend on task-16 so the implementation uses this schema refinement.

## Acceptance criteria validation

- AC1: CSV columns for bands, stages, performances, distances, and food are documented.
- AC2: Early band-only import and final running-order import are documented as separate import sets.
- AC3: Missing references, overlaps, unknown stages, duplicate ids, invalid times, and walking-minute validation are mapped to fields and error shapes.
- AC4: Follow-up work is documented; task-6 remains sufficient for the first parser and separate follow-up is called out for Wacken JSON metadata, rich band metadata storage, reviewed data-grid updates, and final day/date confirmation.

## How to test

### Automated tests

No automated tests were run; this was a documentation-only refinement.

### Manual validation

Reviewed the schema document against `business-requirements.md`, task-15 findings, and task-6 acceptance criteria.

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

- Added festival data CSV schema refinement documentation.
- Corrected task dependencies so task-16 follows task-15 and task-6 follows task-16.
- Updated task-16 status, plan, acceptance criteria, and validation evidence.

## Risks and follow-up

The exact final Wacken 2026 festival-day/date model still needs confirmation from published running-order data. The MVP schema supports multiple days using `festival_day_id` and local Wacken date-times.
<!-- SECTION:NOTES:END -->

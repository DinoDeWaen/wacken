---
id: task-19
title: 'US-019: Refine festival days and performance time model'
status: Done
assignee:
  - '@codex'
created_date: '2026-05-15 06:34'
updated_date: '2026-05-15 14:13'
labels:
  - refinement
  - scheduling
  - time
dependencies:
  - task-4
  - task-6
priority: medium
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## US-019: Refine festival days and performance time model

**As a** developer
**I want** the festival day and performance time model refined
**So that** scheduling, imports, and timeline output use unambiguous dates and times

### Notes
- Source: `backlog/docs/business-requirements.md` remaining open question about multiple festival days and date/time format.
- This must be resolved before reliable final running-order import and schedule generation.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given Wacken Open Air 2026 has performances across festival days When the model is refined Then the supported day structure is documented
- [x] #2 Given performances have start and end times When the model is refined Then the date/time format and timezone assumptions are documented
- [x] #3 Given performances may cross midnight When the model is refined Then the expected representation is documented
- [x] #4 Given CSV import and schedule generation depend on performance time When refinement is complete Then any required updates to implementation stories are identified
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Documented supported festival-day structure for Wacken Planner 2026.
2. Documented performance date/time format and timezone assumptions.
3. Documented how performances crossing midnight are represented.
4. Identified follow-up implementation updates needed for CSV import, schedule generation, and timeline/PDF output.
5. Validated by documentation review; no build required because only docs/task metadata changed.

Architecture impact: not architecture-significant; documentation/refinement only, no code or module boundary changes.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Documented the festival day and performance time model in `backlog/docs/festival-day-and-time-model.md`. The model supports multiple festival days through `festival_day_id`, uses ISO-8601 local Wacken date-times for `start_at` and `end_at`, and represents midnight-crossing performances with real next-calendar-day end times.

## Acceptance criteria validation

- AC1: Multiple-day structure is documented in `Festival Day Structure`.
- AC2: Date/time format and timezone assumptions are documented in `Date/Time Format`.
- AC3: Midnight-crossing representation is documented in `Midnight-Crossing Performances`.
- AC4: Required updates for CSV import, schedule generation, and timeline/PDF output are documented in `Implications For Existing Stories`.

## How to test

### Automated tests

No automated tests were run; this was a documentation-only refinement.

### Manual validation

Reviewed the document against business requirements, task-6 CSV import behavior, and `festival-data-csv-schemas.md`.

## TDD / BDD / approval-test evidence

Not applicable; refinement documentation only.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR: not needed.

## README impact

README not updated because setup, commands, architecture, troubleshooting, and run behavior did not change.

## Diagram impact

No diagram update needed because architecture diagrams did not change.

## Commits / logical change list

- Added festival day and performance time model documentation.
- Updated task-19 status, plan, acceptance criteria, and validation evidence.

## Risks and follow-up

Official Wacken 2026 festival-day names, dates, and boundaries still need confirmation once the final running order is published. A `festival_days.csv` or configuration can be added later if final import needs explicit day metadata.
<!-- SECTION:NOTES:END -->

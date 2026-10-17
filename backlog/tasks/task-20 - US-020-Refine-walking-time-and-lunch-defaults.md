---
id: task-20
title: 'US-020: Refine walking-time and lunch defaults'
status: Done
assignee:
  - '@codex'
created_date: '2026-05-15 06:34'
updated_date: '2026-05-15 14:14'
labels:
  - refinement
  - scheduling
  - lunch
dependencies:
  - task-4
priority: medium
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## US-020: Refine walking-time and lunch defaults

**As a** group member
**I want** walking-time and lunch defaults refined
**So that** the first schedule generator can make sensible choices before user-configurable settings exist

### Notes
- Source: `backlog/docs/business-requirements.md` BR-023, BR-027, BR-028, and open questions.
- Walking minutes are the default unit; user-configurable walking speed is later scope.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given travel feasibility uses walking minutes by default When defaults are refined Then the source of walking-minute data between stages is documented
- [x] #2 Given lunch must occur inside 12:00-14:00 When lunch defaults are refined Then the initial lunch block duration and placement rule are documented
- [x] #3 Given no nearby food exists When lunch behavior is refined Then the no-substitute behavior remains explicit
- [x] #4 Given these defaults affect scheduling When refinement is complete Then follow-up scheduling implementation tasks are identified
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Documented walking-minute data source defaults and validation expectations.
2. Documented initial lunch block duration and placement rule inside 12:00-14:00.
3. Documented no-substitute behavior when no nearby food exists.
4. Identified follow-up scheduling implementation tasks for travel feasibility, lunch insertion, food suggestions, re-evaluation, and later settings.
5. Validated by documentation review; no build required because only docs/task metadata changed.

Architecture impact: not architecture-significant; documentation/refinement only, no code or module boundary changes.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Documented walking-time and lunch defaults in `backlog/docs/walking-time-and-lunch-defaults.md`. Walking minutes come from `distances.csv`; lunch defaults to a 30-minute block placed at the earliest feasible gap inside 12:00-14:00; and missing nearby food results in no substitute recommendation.

## Acceptance criteria validation

- AC1: Walking-minute source and validation are documented in `Walking-Time Defaults`.
- AC2: Lunch duration and placement are documented in `Lunch Defaults`.
- AC3: No-substitute food behavior is documented in `Food Suggestions`.
- AC4: Scheduling follow-up tasks are documented in `Follow-Up Scheduling Tasks`.

## How to test

### Automated tests

No automated tests were run; this was a documentation-only refinement.

### Manual validation

Reviewed the document against BR-023, BR-027, BR-028, BR-029, BR-030, task-6 import behavior, and `festival-data-csv-schemas.md`.

## TDD / BDD / approval-test evidence

Not applicable; refinement documentation only.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR: not needed for this refinement. Future durable settings storage may require approval/ADR.

## README impact

README not updated because setup, commands, architecture, troubleshooting, and run behavior did not change.

## Diagram impact

No diagram update needed because architecture diagrams did not change.

## Commits / logical change list

- Added walking-time and lunch default documentation.
- Updated task-20 status, plan, acceptance criteria, and validation evidence.

## Risks and follow-up

The 30-minute lunch block is a deterministic starting point and should be revisited with user feedback. Missing stage distances should block or require review rather than guessing travel time.
<!-- SECTION:NOTES:END -->

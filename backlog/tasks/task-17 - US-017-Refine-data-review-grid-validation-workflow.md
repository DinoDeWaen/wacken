---
id: task-17
title: 'US-017: Refine data review grid validation workflow'
status: Done
assignee:
  - '@codex'
created_date: '2026-05-15 06:34'
updated_date: '2026-05-15 14:10'
labels:
  - refinement
  - data
  - admin
dependencies:
  - task-15
  - task-16
priority: medium
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## US-017: Refine data review grid validation workflow

**As an** admin
**I want** the proposed data grid workflow refined
**So that** scraped or imported changes can be reviewed line by line before changing festival data

### Notes
- Source: `backlog/docs/business-requirements.md` BR-034 and data-grid open question.
- This refinement should decide row states, validation messages, accept/reject flow, and how conflicts are shown.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given proposed imported or scraped changes exist When the grid workflow is refined Then row-level accept, reject, and validation states are documented
- [x] #2 Given a row has a missing reference, overlap, or unknown stage When validation feedback is refined Then the user-facing message and resolution options are documented
- [x] #3 Given the admin accepts selected rows When the workflow is refined Then the expected update behavior is documented
- [x] #4 Given the workflow impacts architecture or storage When refinement is complete Then follow-up implementation or ADR tasks are identified
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Documented the proposed data-review grid workflow for scraped and CSV-imported changes.
2. Defined row states, validation states, review decisions, and accept/reject behavior.
3. Mapped missing references, overlaps, unknown stages, duplicate ids, invalid time ranges, and invalid URLs to user-facing messages and resolution options.
4. Documented expected update behavior for accepted, rejected, blocked, and cross-row updates.
5. Identified architecture/storage follow-up work and ADR needs before durable review-batch implementation.
6. Validated by documentation review; no build required because only docs/task metadata changed.

Architecture impact: not architecture-significant; documentation/refinement only, no code or module boundary changes.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Documented the data review grid workflow in `backlog/docs/data-review-grid-workflow.md`. The document defines review batches, row fields, validation states, accept/reject flow, resolution options, expected update behavior, and follow-up architecture/storage work.

## Acceptance criteria validation

- AC1: Row-level accept, reject, validation, and review states are documented in `Row Model` and `Row States`.
- AC2: Missing references, overlaps, unknown stages, and related validation messages/resolution options are documented in `Validation Messages And Resolution Options`.
- AC3: Accepted/rejected row behavior is documented in `Accept / Reject Flow` and `Expected Update Behavior`.
- AC4: Architecture/storage follow-up and ADR needs are documented in `Architecture And Storage Follow-Up`.

## How to test

### Automated tests

No automated tests were run; this was a documentation-only refinement.

### Manual validation

Reviewed the workflow document against BR-034, task-15, task-16, and the CSV schema validation rules.

## TDD / BDD / approval-test evidence

Not applicable; refinement documentation only.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR: not needed for this refinement. Future durable review-batch storage should get ADR/approval.

## README impact

README not updated because setup, commands, architecture, troubleshooting, and run behavior did not change.

## Diagram impact

No diagram update needed because architecture diagrams did not change.

## Commits / logical change list

- Added data review grid workflow documentation.
- Updated task-17 status, plan, acceptance criteria, and validation evidence.

## Risks and follow-up

The workflow intentionally leaves durable review-batch storage as a future architecture decision. MVP can use in-memory review batches for one-session review/apply behavior.
<!-- SECTION:NOTES:END -->

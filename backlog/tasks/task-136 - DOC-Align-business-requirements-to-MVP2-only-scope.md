---
id: task-136
title: 'DOC: Align business requirements to MVP2-only scope'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-30 15:45'
updated_date: '2026-06-30 15:47'
labels:
  - documentation
  - requirements
  - mvp2
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As the product owner, I want the business requirements to describe MVP1 and MVP2 only, so that the team is not blocked by removed MVP3/MVP4 scope when finalising MVP2.

Scope: remove active MVP3/MVP4 roadmap, lunch, food-suggestion, full travel-feasibility re-planning, and PDF export requirements from the current business requirements; keep implemented MVP2 walking-time visibility and imported food master data where relevant.

Out of scope: implementing product behavior, changing backend schema, or releasing an APK.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given the business requirements are reviewed, when the delivery roadmap is read, then only MVP1 and MVP2 are active increments
- [x] #2 Given schedule rules are reviewed, when MVP2 scope is read, then walking-time context remains active but lunch, food suggestions, full travel-feasibility re-planning, and PDF export are not active MVP2 requirements
- [x] #3 Given documentation is reviewed, when stale MVP3/MVP4 wording is found, then it is removed or clearly marked outside active MVP2 scope
- [x] #4 README impact is recorded using the canonical wording from delivery-governance.md
- [x] #5 Business requirements impact is recorded using the canonical wording from delivery-governance.md
- [x] #6 Automated validation or documentation diff review is recorded in implementation notes
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Review current business requirements and README for active MVP3/MVP4 references.
2. Remove active MVP3/MVP4 roadmap/capability scope while preserving implemented MVP2 walking-time context.
3. Align README public scope wording.
4. Validate by searching for stale lunch/PDF/MVP3/MVP4 active-scope wording and reviewing the diff.
5. Record implementation notes and close acceptance criteria.

Architecture impact: not architecture-significant; documentation-only scope clarification.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Aligned the active product scope to MVP1 and MVP2 only. Removed MVP3/MVP4 roadmap and capability rows from the current business requirements, reworded the schedule goals around in-app MVP2 planning, and kept implemented walking-time context as an MVP2 requirement. Lunch planning, food suggestions, full travel-feasibility re-planning, and PDF export are no longer active MVP2 requirements.

Updated README context so it no longer claims lunch-window or printable-timeline behavior.

## Acceptance criteria validation

- AC1: The delivery roadmap now lists only MVP1 and MVP2.
- AC2: Walking-time context remains active; lunch, food suggestions, full travel-feasibility re-planning, and PDF export were removed from active MVP2 scope or marked reserved/future.
- AC3: Targeted stale-scope search found only the intentionally reserved lunch placeholder.
- AC4: README impact: updated current scope wording to describe shared ratings, vetoes, overlaps, walking-time context, and manual group choices, and removed printable-timeline groundwork.
- AC5: Business requirements impact: updated active roadmap, goals, workflow wording, business rules, terminology, reporting notes, edge cases, and open questions to remove MVP3/MVP4 as active scope.
- AC6: Documentation validation is recorded below.

## How to test

### Automated tests

Not run, because this is documentation-only scope alignment with no code behavior change.

### Manual validation

Reviewed README and business requirements diff. Ran: `rg -n "MVP 3|MVP 4|lunch|Lunch|PDF|printable|exportable|food suggestions|Food suggestions|travel feasibility|infeasible|re-run|nearby food|12:00-14:00|suitable for printing|timeline/PDF|PDF export" backlog/docs/business-requirements-wacken.md README.md`. The only remaining match is `BR-010` explicitly stating there is no active MVP2 lunch-window requirement.

## TDD / BDD / approval-test evidence

Not applicable; no executable behavior changed.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR: ADR impact: none, because this only removes future MVP scope from active business requirements and README wording.

## README impact

README impact: updated current product-scope wording and removed printable-timeline groundwork.

## Business requirements impact

Business requirements impact: updated active MVP scope to MVP1/MVP2 only and removed active MVP3/MVP4 lunch, food-suggestion, full travel-feasibility re-planning, and PDF export requirements.

## Diagram impact

Diagram impact: none, because no architecture diagram or module relationship changed.

## Commits / logical change list

- Updated `backlog/docs/business-requirements-wacken.md`.
- Updated `README.md`.

## Risks and follow-up

`AGENTS.md` still references the old `backlog/docs/business-requirements.md` path while the current file on disk is `business-requirements-wacken.md`; this appears to be pre-existing documentation-path drift and should be normalized before the next release if this rename is intentional.
<!-- SECTION:NOTES:END -->

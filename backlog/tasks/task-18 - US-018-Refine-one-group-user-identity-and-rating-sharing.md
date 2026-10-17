---
id: task-18
title: 'US-018: Refine one-group user identity and rating sharing'
status: Done
assignee:
  - '@codex'
created_date: '2026-05-15 06:34'
updated_date: '2026-05-15 14:12'
labels:
  - refinement
  - rating
  - group
dependencies:
  - task-8
  - task-13
priority: medium
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## US-018: Refine one-group user identity and rating sharing

**As a** group member
**I want** the current one-group identity and rating-sharing model refined
**So that** my group can combine ratings without adding multi-group complexity this year

### Notes
- Source: `backlog/docs/business-requirements.md` BR-037 and open questions about user identity, storage, and invite format.
- Multi-group support is explicitly out of current scope.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given the current product supports one shared group When identity is refined Then the way users are represented in that group is documented
- [x] #2 Given ratings must be shared inside the one group When storage or sync is refined Then where ratings live and how they are exchanged is documented
- [x] #3 Given a member has not rated a band When the model is refined Then the default 1-star behavior is included
- [x] #4 Given friend invites are planned When the invite format is refined Then the most useful Android share format is documented or deferred with rationale
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Documented the current one-group identity model and member representation.
2. Documented where ratings live for MVP and how they can be exchanged without multi-group support.
3. Included the default 1-star behavior for missing member ratings.
4. Documented the most useful future Android share/invite format and deferred deep-link invites until storage/sync is designed.
5. Identified follow-up implementation and ADR needs for member models, rating import/export, durable storage, sync, and invites.
6. Validated by documentation review; no build required because only docs/task metadata changed.

Architecture impact: not architecture-significant; documentation/refinement only, no code or module boundary changes.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Documented the one-group identity and rating-sharing model in `backlog/docs/one-group-identity-and-rating-sharing.md`. The current group is fixed as `my group`; members are represented by stable local display names for now; missing ratings resolve to the 1-star default; and rating exchange is documented as explicit import/export until durable sync is designed.

## Acceptance criteria validation

- AC1: Member representation for the one shared group is documented in `Member Representation`.
- AC2: Rating ownership and exchange are documented in `Rating Ownership` and `Rating Exchange For MVP`.
- AC3: Default 1-star behavior is documented in `Rating Ownership`.
- AC4: Android Sharesheet/deep-link invite direction is documented in `Friend Invite Format`, with invite implementation deferred until storage/sync exists.

## How to test

### Automated tests

No automated tests were run; this was a documentation-only refinement.

### Manual validation

Reviewed the document against BR-037, BR-038, task-8, task-13, and current `RatingRepository` behavior.

## TDD / BDD / approval-test evidence

Not applicable; refinement documentation only.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR: not needed for this refinement. Durable storage/sync/invites should get approval and likely ADR later.

## README impact

README not updated because setup, commands, architecture, troubleshooting, and run behavior did not change.

## Diagram impact

No diagram update needed because architecture diagrams did not change.

## Commits / logical change list

- Added one-group identity and rating-sharing documentation.
- Updated task-18 status, plan, acceptance criteria, and validation evidence.

## Risks and follow-up

The current repository still keys ratings by `userName`; a future group-decision task should decide whether to introduce a `GroupMember` model and stable member ids.
<!-- SECTION:NOTES:END -->

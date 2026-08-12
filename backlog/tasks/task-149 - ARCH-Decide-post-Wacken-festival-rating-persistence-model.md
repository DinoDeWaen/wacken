---
id: task-149
title: 'ARCH: Decide post-Wacken festival rating persistence model'
status: Done
assignee:
  - '@codex'
created_date: '2026-08-12 07:52'
updated_date: '2026-08-12 08:14'
labels:
  - architecture
  - post-mvp3
  - festivals
  - ratings
  - supabase
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Business value: The post-Wacken features need one coherent data model before implementation changes Room, Supabase, migrations, sync, and domain boundaries.

Scope: define the festival, archived festival, reusable band, festival lineup entry, festival planning rating, and personal band rating event model; document exact-name matching for the first version; document future alias/fuzzy matching boundaries.

Out of scope: implementing the feature behavior in Android or Supabase.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Architecture-significant changes are explicitly approved before implementation starts.
- [x] #2 An ADR defines the Room, Supabase, sync, and domain model changes for festivals, bands, lineup entries, planning ratings, and personal rating events.
- [x] #3 The model enforces one active festival at a time and supports archived read-only festivals.
- [x] #4 The model keeps personal band rating history separate from festival planning ratings.
- [x] #5 Exact-name band reuse is defined for the first version; fuzzy matching and aliases are documented as future scope.
- [x] #6 README, business requirements, diagram, and ADR impact are recorded using delivery-governance wording.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Inspected existing ADRs, domain/application repository ports, Room entities/DAOs, Supabase migrations, import SQL, and sync adapters related to bands, performances, planning ratings, real ratings, groups, and schedule locks.
2. Prepared a standard-depth architecture approval request with minimal, standard, and maximum alternatives for the post-Wacken festival/rating persistence model.
3. Received explicit user approval for the standard option.
4. Created ADR 0011 documenting the accepted model, invariants, boundaries, migration/sync implications, and deferred future scope.
5. Updated README architecture notes and C4 diagrams to reference ADR 0011 and remove stale active Wacken-site import wording.
6. Validated documentation/task state with focused rg checks, ASCII check, and git diff --check.

Design approach: standard DDD/hexagonal treatment because this affects persistence, Supabase contracts, Room cache, sync adapters, and domain model boundaries. Test strategy: documentation-only architecture task; implementation stories must add BDD plus domain/application/adapter tests. Architecture impact: architecture-significant decision approved by the user; no code/schema implementation performed in task-149. README impact: updated architecture ADR list, current repository notes, and diagrams. Business requirements impact: none, because task-148 already updated validated business scope. Diagram impact: updated README C4 diagrams. ADR impact: created ADR 0011. Deviations: none.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Created ADR 0011 for the approved standard post-Wacken festival rating model. The decision introduces explicit festivals, archived festival state, festival lineup entries, festival planning ratings, and synced personal band rating events across domain/application ports, Room, Supabase, and sync adapters.

Updated README architecture notes and diagrams so future implementation starts from the accepted model.

## Acceptance criteria validation

- AC1: Explicit architecture approval was received in chat for the standard option.
- AC2: ADR 0011 defines the Room, Supabase, sync, and domain model changes for festivals, bands, lineup entries, planning ratings, and personal rating events.
- AC3: ADR 0011 documents the one-active-festival invariant and read-only archived festival behavior.
- AC4: ADR 0011 keeps personal band rating history separate from festival planning ratings.
- AC5: ADR 0011 defines exact-name band reuse for the first version and defers fuzzy matching and aliases to future scope.
- AC6: Impact notes are recorded below using delivery-governance wording.

## How to test

### Automated tests

- Not run, because this task changed architecture documentation only and did not change executable code.

### Manual validation

- Ran focused rg checks for ADR 0011, README references, post-MVP3 model wording, and stale Wacken-site import wording.
- Ran LC_ALL=C rg for non-ASCII characters in touched documentation files.
- Ran git diff --check with no whitespace errors.

## TDD / BDD / approval-test evidence

No executable behavior was implemented in task-149. The dependent implementation stories require BDD scenarios plus domain/application/adapter tests for their behavior.

## Architecture impact

- Architecture-significant change: yes, this records an architecture-significant future persistence/domain/sync decision.
- Approval received: yes, the user approved the standard option in chat.
- ADR: ADR impact: created backlog/decisions/0011-post-wacken-festival-rating-model.md.

## README impact

README impact: updated the architecture ADR list, current repository notes, and C4 diagrams for the accepted post-MVP3 festival rating model.

## Business requirements impact

Business requirements impact: none, because task-148 already updated the validated business scope and task-149 only records the accepted architecture decision.

## Diagram impact

Diagram impact: updated README C4 diagrams to remove stale active Wacken-site import references.

## Commits / logical change list

- Created backlog/decisions/0011-post-wacken-festival-rating-model.md.
- Updated README.md architecture notes and diagrams.
- Updated task-149 plan and validation notes through Backlog.md CLI.

## Risks and follow-up

- Follow-up implementation must happen through task-150 through task-155 and must not include future-only fuzzy aliases or multiple upcoming festival support.
- Existing legacy real ratings do not have original creation timestamps; implementation stories must choose an explicit backfill strategy when migrating them.
<!-- SECTION:NOTES:END -->

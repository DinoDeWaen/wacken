---
id: task-4
title: 'US-004: Core domain entities'
status: In Progress
assignee:
  - '@codex'
created_date: '2026-01-06 16:57'
updated_date: '2026-05-15 09:09'
labels: []
dependencies: []
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## US-004: Core domain entities

**As a** developer
**I want** core domain types for bands, stages, performances, and ratings
**So that** business rules have a solid model foundation
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given a rating creation When value is outside 0-4 Then the domain rejects it with a clear error
- [ ] #2 Given a performance with start and end times When end is not after start Then the domain rejects it
- [ ] #3 Given stage distance definitions When a negative distance is provided Then the domain rejects it and same-stage distance resolves to zero
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Add focused failing JUnit 5 tests for the domain invariants: rating bounds, performance time ordering, non-negative stage distances, and same-stage zero distance.
2. Implement minimal domain value/entity types in `domain`: `Band`, `Stage`, `Performance`, `Rating`, `StageDistance`, and a domain exception or validation style with clear messages.
3. Keep all domain types plain Java with no Android, application, or infrastructure dependencies.
4. Remove or adapt placeholder boundary tests only if they become obsolete; keep coverage gates green.
5. Validate with `./gradlew :domain:test :domain:jacocoTestCoverageVerification`, `./gradlew test`, and `./gradlew assembleDebug`.
6. Update README only if module usage or public commands change; no diagram change expected.

Test strategy: TDD in the domain module. Tests first for each invariant, then minimal implementation, then refactor.
Architecture impact: architecture-significant because this introduces core DDD domain types. Approval required before coding. ADR likely not needed if the implementation follows ADR 0001 module boundaries without adding new architecture style.
Risks/assumptions: task acceptance does not define ID formats or date/time timezone policy; use minimal stable Java types and do not invent scheduling policy beyond end-after-start validation.
<!-- SECTION:PLAN:END -->

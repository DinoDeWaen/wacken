---
id: task-5
title: 'US-005: Domain repositories and in-memory adapters'
status: In Progress
assignee:
  - '@codex'
created_date: '2026-01-06 16:57'
updated_date: '2026-05-15 09:34'
labels: []
dependencies: []
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## US-005: Domain repositories and in-memory adapters

**As a** developer
**I want** repository interfaces with in-memory adapters
**So that** application use cases can run without infrastructure coupling
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given the domain layer When defining repositories Then interfaces exist for bands, performances, ratings, and distances without infrastructure details
- [ ] #2 Given application tests When using fakes Then in-memory implementations allow storing and retrieving bands, performances, ratings, and distances
- [ ] #3 Given clean architecture rules When reviewing dependencies Then repository interfaces live in domain and implementations live in infrastructure
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Add domain repository interfaces for bands, performances, ratings, and stage distances, using domain language and no infrastructure details.
2. Add minimal identity support needed for repository keys without inventing user/group sharing behavior beyond current task scope.
3. Write application/infrastructure tests first that exercise in-memory storing and retrieving for each repository.
4. Implement in-memory adapters in `infrastructure` that implement the domain interfaces.
5. Keep dependency direction clean: domain owns interfaces; infrastructure implements them; application can use interfaces without depending on infrastructure.
6. Validate with `./gradlew test`, `./gradlew assembleDebug`, and grep/dependency inspection for boundary rules.
7. Update README only if module responsibilities need clarification; no diagram change expected unless adapter direction needs documentation.

Test strategy: TDD around repository contracts using in-memory adapters. Use fakes/in-memory implementations; no persistence.
Architecture impact: architecture-significant because this introduces repository ports and adapters. Approval required before coding. ADR expected because this records repository interface ownership and in-memory adapter placement.
Risks/assumptions: task does not define persistent IDs or multi-user identity; use minimal stable keys from current domain concepts and defer richer identity to refinement task-18.
<!-- SECTION:PLAN:END -->

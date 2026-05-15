---
id: task-5
title: 'US-005: Domain repositories and in-memory adapters'
status: Done
assignee:
  - '@codex'
created_date: '2026-01-06 16:57'
updated_date: '2026-05-15 09:54'
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
- [x] #1 Given the domain layer When defining repositories Then interfaces exist for bands, performances, ratings, and distances without infrastructure details
- [x] #2 Given application tests When using fakes Then in-memory implementations allow storing and retrieving bands, performances, ratings, and distances
- [x] #3 Given clean architecture rules When reviewing dependencies Then repository interfaces live in domain and implementations live in infrastructure
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Added domain repository interfaces for bands, performances, ratings, and stage distances, using domain language and no infrastructure details.
2. Used minimal keys from current domain concepts: band names, user names, stage pairs, and performance values; richer identity remains deferred.
3. Wrote infrastructure tests first for storing and retrieving each repository concept.
4. Implemented in-memory adapters in `infrastructure` that implement the domain interfaces.
5. Kept dependency direction clean: domain owns interfaces; infrastructure implements them; application/domain do not reference in-memory adapters.
6. Validated with `./gradlew :infrastructure:test`, `./gradlew test`, `./gradlew assembleDebug`, and grep confirming inner modules do not reference infrastructure adapters.
7. README update was not needed; ADR 0004 documents the repository port/adapter decision.

Deviation: added JUnit 5 test configuration to `infrastructure` so adapter tests can run; coverage gates remain scoped to domain/application per task-2.
Architecture impact: architecture-significant and explicitly approved by the user as minimal before implementation. ADR 0004 added.
README impact: not needed because existing module responsibilities already describe infrastructure adapters.
Diagram impact: not needed because dependency direction did not change.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Added domain-owned repository interfaces for bands, performances, ratings, and stage distances. Added in-memory infrastructure adapters for each repository so future application use cases can run without persistence or Android coupling.

Added adapter tests proving storing and retrieving bands, performances, ratings, and stage distances. Same-stage distance lookup resolves to zero even without stored data.

## Acceptance criteria validation

- AC1: `BandRepository`, `PerformanceRepository`, `RatingRepository`, and `StageDistanceRepository` live in `domain` and contain no infrastructure details.
- AC2: `InMemory*RepositoryTest` tests store/retrieve behavior for bands, performances, ratings, and distances.
- AC3: in-memory implementations live in `infrastructure`; grep confirmed `domain` and `application` do not reference `InMemory` or `infrastructure`.

## How to test

### Automated tests

- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :infrastructure:test`
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew test`
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew assembleDebug`

### Manual validation

- Ran `rg -n "InMemory|infrastructure" domain/src/main/java application/src/main/java`; no matches were found.
- Inspected package placement for repository interfaces and in-memory adapters.

## TDD / BDD / approval-test evidence

Used TDD around repository adapter behavior. The first infrastructure test run failed because repository interfaces, in-memory adapters, and infrastructure test dependencies were missing. Then the minimal interfaces/adapters were implemented and tests passed.

## Architecture impact

- Architecture-significant change: yes, this introduces repository ports and in-memory adapters.
- Approval received: yes, user approved the minimal option before implementation.
- ADR: added `backlog/decisions/0004-domain-repository-ports-and-in-memory-adapters.md`.

## README impact

Not needed because existing README module responsibilities already describe domain rules, application use cases, and infrastructure adapters.

## Diagram impact

Not needed because module boundaries and dependency direction did not change.

## Commits / logical change list

- `9458792` Add repository ports and in-memory adapters

## Risks and follow-up

- Repository keys are intentionally minimal and may change when user identity, catalog versioning, and CSV schemas are refined.
- In-memory adapters are not persistent and are intended for early use cases/tests only.
<!-- SECTION:NOTES:END -->

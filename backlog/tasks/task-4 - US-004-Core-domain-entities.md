---
id: task-4
title: 'US-004: Core domain entities'
status: Done
assignee:
  - '@codex'
created_date: '2026-01-06 16:57'
updated_date: '2026-05-15 09:33'
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
- [x] #1 Given a rating creation When value is outside 0-4 Then the domain rejects it with a clear error
- [x] #2 Given a performance with start and end times When end is not after start Then the domain rejects it
- [x] #3 Given stage distance definitions When a negative distance is provided Then the domain rejects it and same-stage distance resolves to zero
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Added focused failing JUnit 5 tests for domain invariants: rating bounds, performance time ordering, non-negative stage distances, and same-stage zero distance.
2. Implemented minimal plain Java domain types in `domain`: `Band`, `Stage`, `Performance`, `Rating`, `StageDistance`, and `DomainValidationException`.
3. Kept all domain types framework-free with no Android, application, or infrastructure dependencies.
4. Kept placeholder boundary test and added real domain tests while maintaining coverage gates.
5. Validated with `./gradlew :domain:test :domain:jacocoTestCoverageVerification`, `./gradlew test`, `./gradlew assembleDebug`, and grep for Android references in `domain`.
6. README update was not needed because commands and module responsibilities did not change.

Deviation: added minimal blank-name validation for `Band` and `Stage` because named core entities need present names to be usable; no ID/date policy was invented.
Architecture impact: architecture-significant and explicitly approved by the user as option 1 before implementation. No ADR added because the implementation follows ADR 0001 without changing architecture style.
README impact: not needed because setup, commands, and architecture docs remain accurate.
Diagram impact: not needed because module boundaries did not change.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Added the core domain foundation in the `domain` module: `Band`, `Stage`, `Performance`, `Rating`, `StageDistance`, and `DomainValidationException`.

The implementation enforces the task invariants: ratings must be 0-4, performance end time must be after start time, stage walking minutes must not be negative, and same-stage distance resolves to zero.

## Acceptance criteria validation

- AC1: `RatingTest` covers valid ratings and rejects values below 0 or above 4 with `Rating must be between 0 and 4.`
- AC2: `PerformanceTest` rejects end times equal to or before start with `Performance end time must be after start time.`
- AC3: `StageDistanceTest` rejects negative walking minutes with `Stage distance walking minutes must not be negative.` and verifies same-stage distance becomes zero.

## How to test

### Automated tests

- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:test :domain:jacocoTestCoverageVerification`
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew test`
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew assembleDebug`

### Manual validation

- Ran `rg -n "android\.|com\.android|androidx" domain`; no Android references were found.

## TDD / BDD / approval-test evidence

Used TDD in the domain module. The initial red test run failed because the new domain types did not exist. Then the minimal domain implementation was added and the tests passed.

## Architecture impact

- Architecture-significant change: yes, this introduces core DDD domain types.
- Approval received: yes, user selected option 1 before implementation.
- ADR: not needed because the implementation follows ADR 0001 module boundaries without changing architecture style.

## README impact

Not needed because setup, commands, and module responsibilities remain accurate.

## Diagram impact

Not needed because module boundaries did not change.

## Commits / logical change list

- `655d869` Add core domain entities

## Risks and follow-up

- ID formats, festival date/time format, timezone policy, and richer scheduling behavior remain intentionally unresolved for later refinement/tasks.
- Blank-name validation for `Band` and `Stage` was added as minimal domain hygiene; future import stories can decide how to present those errors to users.
<!-- SECTION:NOTES:END -->

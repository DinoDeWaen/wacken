---
id: task-2
title: 'US-002: Configure testing and coverage'
status: Done
assignee:
  - '@codex'
created_date: '2026-01-06 16:56'
updated_date: '2026-05-15 08:58'
labels: []
dependencies: []
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## US-002: Configure testing and coverage

**As a** developer
**I want** JUnit5/Mockito tests with coverage gates
**So that** quality remains enforced from the start
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given the project When I run ./gradlew test Then JUnit5 tests execute for domain and application modules
- [x] #2 Given test code When mocking dependencies Then Mockito or fakes are available and configured
- [x] #3 Given CI configuration When coverage drops below 80% for domain or 70% for application Then the build fails
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Configured JVM test conventions for `domain` and `application`: JUnit 5, Mockito, and JaCoCo.
2. Added proof tests in `domain` and `application` so `./gradlew test` executes JUnit 5 tests for both modules.
3. Added JaCoCo coverage verification gates: 80% instruction coverage for `domain`, 70% instruction coverage for `application`.
4. Wired module `test` tasks to run coverage verification and `check` to depend on coverage verification, giving CI a failing Gradle path when coverage drops.
5. Updated README test and coverage commands.
6. Added ADR 0002 for the approved JVM test and coverage gate strategy.
7. Validated with `./gradlew test`, explicit coverage verification tasks, and `./gradlew assembleDebug`.

Deviation: moved test configuration from root `configure(...)` into the two Java module build files after Gradle evaluation showed the root block ran before `testImplementation` configurations existed.
Architecture impact: architecture-significant and explicitly approved by the user as option 1 before implementation. ADR 0002 added.
README impact: updated.
Diagram impact: not needed because test tooling does not change architecture diagrams.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Configured the minimal JVM testing and coverage foundation for the clean architecture inner modules. `domain` and `application` now use JUnit 5, have Mockito available in test scope, and enforce JaCoCo coverage gates.

Added small proof tests for the existing boundary classes. The application test includes a Mockito usage check so future use-case tests can mock dependencies when fakes are not enough.

## Acceptance criteria validation

- AC1: `./gradlew test` passed and executed JUnit 5 tests for `domain` and `application`.
- AC2: Mockito is configured in test scope and proven by `ApplicationBoundaryTest`.
- AC3: JaCoCo verification gates are configured for `domain` at 80% and `application` at 70%; `test` finalizes with coverage verification and `check` depends on coverage verification.

## How to test

### Automated tests

- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew test`
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:jacocoTestCoverageVerification :application:jacocoTestCoverageVerification`
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew assembleDebug`

### Manual validation

- Inspected `domain/build.gradle` and `application/build.gradle` for JUnit 5, Mockito, and JaCoCo configuration.
- Confirmed README lists the test and coverage commands.

## TDD / BDD / approval-test evidence

No product behavior was added. This task creates the test harness for future TDD. The proof tests are intentionally small and should be replaced or expanded by real domain/application tests as behavior is introduced.

## Architecture impact

- Architecture-significant change: yes, this establishes project-wide testing and coverage gates for inner modules.
- Approval received: yes, user selected option 1 before implementation.
- ADR: added `backlog/decisions/0002-jvm-test-and-coverage-gates.md`.

## README impact

Updated README with JUnit 5, Mockito/fakes, JaCoCo coverage gates, and useful test/coverage commands.

## Diagram impact

Not needed because test tooling does not change runtime architecture or module dependency direction.

## Commits / logical change list

- `f178740` Configure JVM testing and coverage gates

## Risks and follow-up

- Coverage gates currently protect placeholder boundary classes; future behavior stories should add meaningful domain/application tests and may replace the placeholder tests.
- CI wiring itself is handled by `task-3`; this task provides the Gradle commands/gates CI should call.
- Gradle cache access may require permissions outside the workspace sandbox in this environment.
<!-- SECTION:NOTES:END -->

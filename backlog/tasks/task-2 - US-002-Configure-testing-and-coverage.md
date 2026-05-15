---
id: task-2
title: 'US-002: Configure testing and coverage'
status: In Progress
assignee:
  - '@codex'
created_date: '2026-01-06 16:56'
updated_date: '2026-05-15 08:31'
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
- [ ] #1 Given the project When I run ./gradlew test Then JUnit5 tests execute for domain and application modules
- [ ] #2 Given test code When mocking dependencies Then Mockito or fakes are available and configured
- [ ] #3 Given CI configuration When coverage drops below 80% for domain or 70% for application Then the build fails
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Configure shared JVM test conventions for `domain` and `application`: JUnit 5, Mockito, and JaCoCo.
2. Add small compile-proof tests in `domain` and `application` so `./gradlew test` executes JUnit 5 tests for both modules.
3. Add coverage verification gates matching the task: minimum 80% domain instruction coverage and 70% application instruction coverage.
4. Wire root-level verification so `./gradlew test` runs module tests and coverage verification in the same validation path.
5. Update README test commands and note coverage behavior.
6. Validate with `./gradlew test`, targeted coverage verification tasks, and review generated reports/outputs.

Test strategy: introduce infrastructure for future TDD, with minimal tests proving JUnit 5 and Mockito/fakes are configured. No product business behavior is added in this task.
Architecture impact: architecture-significant because this establishes project-wide testing and coverage strategy. Approval required before coding. ADR expected or ADR 0001 update if the decision is scoped to build/test architecture.
Risks/assumptions: dependency resolution may require network/cache access; generated coverage gates on placeholder code must be meaningful enough to fail below configured thresholds without overfitting future code.
<!-- SECTION:PLAN:END -->

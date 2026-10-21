---
id: TASK-243
title: 'TEST: Enforce JVM hexagonal boundaries with ArchUnit'
status: In Progress
assignee:
  - '@codex'
created_date: '2026-10-21 14:49'
updated_date: '2026-10-21 14:54'
labels:
  - architecture
  - testing
dependencies: []
references:
  - >-
    infrastructure/src/main/java/be/wacken/planner/infrastructure/InfrastructureBoundary.java
modified_files:
  - infrastructure/build.gradle
  - >-
    infrastructure/src/test/java/be/wacken/planner/architecture/HexagonalArchitectureTest.java
  - backlog/decisions/0023-archunit-jvm-boundary-tests.md
  - README.md
  - backlog/docs/testing-strategy.md
  - backlog/docs/architecture-tool-assessment-2026-10-21.md
priority: medium
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a maintainer, I want compiled Java dependency rules to fail when inner layers depend on adapters or frameworks, so architecture regressions are caught alongside ordinary tests. Add the previously assessed ArchUnit complement to the existing Groma gate. Scope: JVM domain/application/infrastructure main bytecode, nonempty complete imports, negative fixtures and CI integration. Android bytecode, unused-code detection and product changes are outside this increment. User explicitly requested adoption after the standard assessment. Business requirements impact: none planned because product rules and runtime boundaries remain unchanged.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Compiled main classes of each JVM module are imported completely; missing or empty input fails rather than silently passing, and test fixtures are excluded from production scope.
- [ ] #2 Domain and application dependency rules enforce existing inward-only and framework-free boundaries; infrastructure cannot depend on Android UI; valid inward dependencies pass.
- [ ] #3 Deliberately invalid fixture dependencies and a layer cycle are rejected by the same rules used for production classes, with useful diagnostics.
- [ ] #4 The architecture tests run through ordinary Gradle test/check and required CI without adding ArchUnit to production runtime dependencies.
- [ ] #5 README documents scope, commands and limitations; the approved test-tool decision is recorded in an ADR.
- [ ] #6 Relevant tests and fresh stable-key release build pass; finalized validation notes record README, business requirements, diagram and ADR impacts before completion.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Add ArchUnit core1.5.1 only to infrastructure test scope, using existing JUnit5 and all three JVM main outputs; enforce complete nonempty imports and keep fixture/test classes outside production scope. 2. Write failing negative-control tests, then shared domain/application allowlists, infrastructure UI exclusion and inter-layer cycle rule; no production refactoring. 3. Document user approval from the explicit request to add the previously recommended ArchUnit complement in ADR0023; update README/test strategy and assessment. 4. Run focused red/green tests, JVM suites/QA and runtime dependency check, fresh stable-key release build, pinned Groma fixtures/gate and CI. Finalize validation and commit task promptly. Standard test-boundary treatment; explicit current user request approves adoption, no repeat approval needed. Product requirements and diagrams have no runtime change. Android bytecode and within-layer cycles are outside this initial scope.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Implemented pinned ArchUnit core1.5.1 in infrastructure test scope with existing JUnit5 runner and explicit main output inputs/task dependencies. Red: negative control failed before rules; green: real JVM classes, inward fixtures, forbidden dependency/cycle fixtures and missing/empty imports pass all5 tests. JVM/QA/check passed; runtimeClasspath contains only application/domain projects, no ArchUnit. Current user request explicitly approves assessed test-tool adoption; ADR0023 records scope and limits. No production source or behavior change.
<!-- SECTION:NOTES:END -->

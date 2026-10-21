---
id: TASK-243
title: 'TEST: Enforce JVM hexagonal boundaries with ArchUnit'
status: Done
assignee:
  - '@codex'
created_date: '2026-10-21 14:49'
updated_date: '2026-10-21 15:03'
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
- [x] #1 Compiled main classes of each JVM module are imported completely; missing or empty input fails rather than silently passing, and test fixtures are excluded from production scope.
- [x] #2 Domain and application dependency rules enforce existing inward-only and framework-free boundaries; infrastructure cannot depend on Android UI; valid inward dependencies pass.
- [x] #3 Deliberately invalid fixture dependencies and a layer cycle are rejected by the same rules used for production classes, with useful diagnostics.
- [x] #4 The architecture tests run through ordinary Gradle test/check and required CI without adding ArchUnit to production runtime dependencies.
- [x] #5 README documents scope, commands and limitations; the approved test-tool decision is recorded in an ADR.
- [x] #6 Relevant tests and fresh stable-key release build pass; finalized validation notes record README, business requirements, diagram and ADR impacts before completion.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Added test-only ArchUnit core1.5.1 to existing infrastructure/JUnit5 and explicit JVM main output wiring. 2. Wrote failing negative controls, implemented four shared rules and complete import guards; all5 positive/negative/import tests pass. 3. Recorded explicit user adoption approval in ADR0023 and updated README/test strategy/assessment. 4. Passed focused and clean-checkout full tests/QA/coverage, runtime dependency check, Groma fixtures/two scans, fresh stable-key release build/signature and all4 CI jobs. No production changes; no new module or CI job needed. Final evidence commit only. Android bytecode/within-layer cycles remain outside approved initial scope.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Added pinned ArchUnit core1.5.1 only to infrastructure test scope under the existing JUnit5 runner. Explicit Gradle main outputs and task/input dependencies cover all domain/application/infrastructure compiled classes, including nested classes; missing, empty, duplicate or wrong-package inputs fail. Test fixtures are excluded from production imports. Shared rules enforce domain/application inward dependencies and JDK value contracts, infrastructure independence from Android/UI, and cycles between layers. No production source changed.

## Acceptance criteria validation

All6 criteria satisfied. Five architecture tests cover actual complete main imports, missing/empty inputs, invalid outward/framework dependencies, a layer cycle and valid inward dependencies. Same rules run against actual bytecode and compiled fixtures. Ordinary Gradle test/check and existing required CI execute the suite. RuntimeClasspath report contains only application/domain projects; no ArchUnit runtime dependency.

## How to test

### Automated tests

Focused: JAVA_HOME JDK21 ./gradlew :infrastructure:test --tests *HexagonalArchitectureTest. Local JVM/QA/check and runtime dependency report passed. Clean checkout f5afeb9 ran ./gradlew test qaTest assembleRelease --rerun-tasks: domain63, application163 plus QA3, infrastructure29 including ArchUnit5; zero failures/errors. Android debug/release unit variants each264 with16 existing environment-gated integration skips, not claimed as passing. Pinned Groma27 fixtures/40 assertions and semantic two-scan gate passed95 components/294 sources/13 flows/88 reviewed groups. CI https://github.com/DinoDeWaen/wacken/actions/runs/39621395928 passed all4 jobs, including native API23/API36, lint and disposable PostgreSQL/PostgREST gates.

### Manual validation

Reviewed import completeness/exclusion, rule diagnostics and positive/negative fixture behavior. Clean-checkout fresh signed APK /private/tmp/wacken-243-build-checkout/app/build/outputs/apk/release/app-release.apk SHA256 1d9653f5c99334dbb1f4264fc6f3270b7b2689069e65922e2af541f4579aa974 verifies v1/v2 with stable certificate ede2ae97735a305900e87bc0fdc021c03382db4e30435f2349a96a3fdcc7545f. Fresh checkout had no prior APK. This test-tool task does not republish or replace v2.35 assets.

## TDD / BDD / approval-test evidence

Negative-control suite failed before implementing dependency rules, then all5 tests passed. Given valid inward bytecode it passes; given forbidden dependencies or a layer cycle the same rules reject them with fixture names; given missing/empty outputs it fails. No product behavior change or legacy refactoring, so new product BDD/approval snapshots were unnecessary.

## Architecture impact

Architecture-significant change: complementary test-tool adoption only. Approval received: user explicitly requested please add archunit after the assessed standard JVM recommendation. Existing runtime/module/product boundaries unchanged. ADR impact: created ADR0023 recording approval, pinned core/test-only placement, rules, alternatives and limitations.

## README impact

README impact: updated ArchUnit scope, focused command, ordinary test/check/CI integration and limitations.

## Business requirements impact

Business requirements impact: none, because development-time bytecode checks change no product capabilities, behavior, persistence or runtime boundaries; README change is testing setup only.

## Diagram impact

Diagram impact: none, because no runtime collaboration or source ownership changed; Groma remains the map integrity gate.

## Commits / logical change list

f5afeb9: ArchUnit dependency, Gradle wiring, tests, ADR0023, README/test strategy and assessment adoption note. Final task evidence is documentation only. PR https://github.com/DinoDeWaen/wacken/pull/6.

## Risks and follow-up

Android adapter bytecode and within-layer class cycles are outside this increment. Static dependency tests cannot prove unused-code safety, runtime wiring or business correctness. Groma and behavioral/native/database checks remain required. New legitimate inner-layer JDK packages require explicit rule review; current URI and IOException contracts remain allowed. Unrelated user working-tree files were preserved and excluded from commits/build checkout.
<!-- SECTION:NOTES:END -->

# ADR 0023: ArchUnit checks for JVM dependency boundaries

## Status

Accepted, 21 October 2026. After reviewing the recommendation to complement
Groma with ArchUnit, the user explicitly requested: “please add archunit”.
This authorizes the assessed JVM boundary-testing scope.

## Context

Gradle module dependencies constrain compilation, but name-only boundary tests
do not protect dependency direction if build dependencies later expand. Groma
protects reviewed map meaning and ownership; its source evidence is not a
replacement for compiled dependency checks.

## Decision

Use pinned ArchUnit core 1.5.1 with ordinary JUnit 5 tests in the existing
infrastructure test source set. No new module, test engine or runtime dependency.
Gradle supplies the explicit domain, application and infrastructure main output
directories and builds them before testing. Imports must be nonempty and match
every class file, including nested classes; packages must match their module.
Test classes and generated negative fixtures are outside these main outputs.

Enforce domain dependencies on domain and JDK language/value packages only.
Application can also depend on application and domain; retain the existing URI
value and IOException port-failure contracts, without permitting network/file
mechanisms. JVM infrastructure can depend inward and on external JVM libraries,
but cannot depend on Android/AndroidX or project classes outside the JVM layers.
Reject cycles between the three layers. This does not prohibit every cycle
between classes within a layer.

The same four rules run on compiled positive and deliberately invalid fixtures.
Missing/empty outputs must fail, and negative-control diagnostics must identify
the fixture classes. Existing `test`, `check` and required CI test execution run
the suite; no separate opt-in architecture task is needed.

## Consequences

- Violations fail the existing test/build job with dependency diagnostics.
- Dependency resolution adds a pinned test library and its transitives.
- New legitimate inner-layer JDK packages require an explicit rule review.
- Android adapter bytecode is not imported in this increment. JVM references
  to Android/UI are prohibited, but Android-side dependencies are not checked.
- Static dependencies cannot establish unused-code safety, runtime wiring,
  business correctness, source ownership or a truthful C4 map. Keep the Groma
  semantic gate and behavioral/native/database tests.

## Alternatives considered

- Gradle constraints and existing marker tests alone: insufficient executable
  protection when compile dependencies expand.
- ArchUnit JUnit engine or a new architecture module: unnecessary for this
  small suite using the existing JUnit runner and JVM outputs.
- jdeps custom enforcement or jQAssistant: more custom code or infrastructure
  than these dependency rules need; see the assessment.

## Links

- TASK-243
- [Tool assessment](../docs/architecture-tool-assessment-2026-10-21.md)
- [Groma gate decision](0022-reviewed-groma-architecture-validation-gate.md)
- [ArchUnit user guide](https://www.archunit.org/userguide/html/000_Index.html)

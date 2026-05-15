# ADR 0001: Initial Android Clean Architecture Scaffold

## Status

Accepted

## Context

Wacken Planner 2026 needs an Android project foundation that supports Clean Architecture and DDD boundaries from the first implementation task. The first product priority is band rating, but later stories depend on an app that already separates domain, application, infrastructure, and Android UI concerns.

Task `task-1` requires:

- A debug APK from `./gradlew assembleDebug`.
- Distinct domain, application, infrastructure, and Android app modules.
- Domain and application modules without Android framework references.

## Decision

Create a minimal multi-module Gradle project:

- `domain`: plain Java module for business concepts and rules.
- `application`: plain Java module for use cases and ports; depends on `domain`.
- `infrastructure`: plain Java module for adapters; depends inward on `application` and `domain`.
- `app`: Android application module; depends on the inner modules and produces the debug APK.

Use Java and Gradle, with the Android Gradle Plugin for the `app` module only.

## Consequences

Positive:

- Business logic can be implemented and tested without Android framework dependencies.
- Future features have an explicit place for domain, use-case, adapter, and UI code.
- The Android app can remain a thin presentation/bootstrap layer.
- The dependency direction is visible in Gradle module dependencies.

Negative / trade-offs:

- The scaffold has more modules than a single-module prototype.
- Early placeholder boundary classes exist only to prove compilation until real business code replaces them.
- Build validation requires Android SDK and Gradle wrapper dependency resolution.

## Alternatives considered

- Single Android module: rejected because it would not satisfy task `task-1` or the repository's Clean Architecture requirement.
- Full standard hexagonal architecture with ports, adapters, dependency injection, and sample use cases: rejected because the first task only needs the scaffold; detailed ports should be introduced when real behavior requires them.
- Maximum production architecture: rejected as premature for the current MVP foundation.

## Links

- Related task: task-1
- Related docs: `backlog/docs/architecture-guidelines.md`, `backlog/docs/business-requirements.md`, `README.md`

# ADR 0003: GitHub Actions CI And APK Artifact

## Status

Accepted

## Context

Wacken Planner 2026 needs automated validation from MVP 1. Task `task-3` requires CI to run tests, include the QA suite path, fail on test failure, build a debug APK, and publish a versioned APK artifact.

The project currently has:

- Gradle wrapper-based Android build.
- JUnit 5 tests and JaCoCo coverage gates through `./gradlew test`.
- A planned dedicated QA scenario suite in `task-9`.

## Decision

Use GitHub Actions as the CI platform.

The CI workflow runs on push and pull request, then:

1. Checks out the repository.
2. Sets up JDK 21.
3. Sets up the Android SDK.
4. Runs `./gradlew test`.
5. Runs the current QA placeholder step until `task-9` adds the dedicated suite.
6. Builds `./gradlew assembleDebug`.
7. Uploads `app/build/outputs/apk/debug/app-debug.apk` as a versioned artifact named with the GitHub run number.

## Consequences

Positive:

- Pushes and pull requests get repeatable build and test feedback.
- Existing JUnit 5 and coverage gates run in CI through the standard Gradle test task.
- Successful CI runs provide a downloadable debug APK.
- The workflow has an explicit extension point for the future QA suite.

Negative / trade-offs:

- The dedicated QA suite is not implemented yet; the CI step documents the gap until `task-9`.
- The artifact version uses the CI run number rather than a release versioning scheme.
- The workflow is GitHub Actions-specific.

## Alternatives considered

- Defer CI until more features exist: rejected because MVP 1 requires CI from the start.
- Add a multi-job/matrix workflow now: rejected as unnecessary before the project has multiple variants or long-running suites.
- Add Play Store/internal distribution now: rejected because the MVP delivery target is CI artifact storage.

## Links

- Related task: task-3
- Related docs: `project.md`, `backlog/docs/delivery-governance.md`, `README.md`

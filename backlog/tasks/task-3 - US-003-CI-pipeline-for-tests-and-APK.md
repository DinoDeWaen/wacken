---
id: task-3
title: 'US-003: CI pipeline for tests and APK'
status: Done
assignee:
  - '@codex'
created_date: '2026-01-06 16:56'
updated_date: '2026-05-15 09:08'
labels: []
dependencies: []
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## US-003: CI pipeline for tests and APK

**As a** developer
**I want** CI to run tests and publish an APK artifact
**So that** we detect regressions and share builds automatically
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given a push or pull request When CI runs Then it executes ./gradlew test and the QA suite
- [x] #2 Given a successful CI run When artifacts are published Then a versioned debug APK is available for download
- [x] #3 Given any failing test When CI runs Then the pipeline fails and reports the failure
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Added GitHub Actions workflow `.github/workflows/ci.yml` for push and pull request events.
2. Configured CI to set up JDK 21 and Android SDK, run `./gradlew test`, include a QA-suite placeholder step until task-9, and build `./gradlew assembleDebug`.
3. Added artifact upload for `app/build/outputs/apk/debug/app-debug.apk` with versioned artifact name `wacken-planner-0.1.<run-number>-debug-apk`.
4. Kept the workflow aligned with task-2 coverage gates because `./gradlew test` runs JUnit 5 and JaCoCo verification.
5. Updated README with CI behavior and artifact naming.
6. Added ADR 0003 for the approved GitHub Actions CI and artifact publishing decision.
7. Validated locally with `./gradlew test` and `./gradlew assembleDebug`; inspected workflow syntax manually.

Deviation: dedicated QA suite is not implemented yet, so CI includes an explicit QA-suite placeholder step and documents that `./gradlew test` is the current executable QA path until task-9.
Architecture impact: architecture-significant and explicitly approved by the user as option 1 before implementation. ADR 0003 added.
README impact: updated.
Diagram impact: not needed because CI does not change runtime architecture or module dependency direction.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Added the minimal GitHub Actions CI workflow for MVP 1. The workflow runs on push and pull request, sets up JDK 21 and Android SDK, runs `./gradlew test`, builds the debug APK, and uploads a versioned APK artifact.

Added ADR 0003 and updated README with CI behavior and artifact naming.

## Acceptance criteria validation

- AC1: Workflow runs `./gradlew test`; the dedicated QA suite is represented by an explicit placeholder step until task-9 adds the executable suite.
- AC2: Workflow uploads `app/build/outputs/apk/debug/app-debug.apk` as `wacken-planner-0.1.<run-number>-debug-apk`.
- AC3: GitHub Actions will fail the pipeline when `./gradlew test` or `./gradlew assembleDebug` fails because each command is a normal failing shell step.

## How to test

### Automated tests

- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew test`
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew assembleDebug`

### Manual validation

- Inspected `.github/workflows/ci.yml` for push/pull_request triggers, test/build commands, QA placeholder, and artifact upload path/name.

## TDD / BDD / approval-test evidence

No product behavior was added. This task adds CI/CD configuration. Local validation uses the same Gradle commands the workflow runs.

## Architecture impact

- Architecture-significant change: yes, this establishes CI/CD and artifact publishing.
- Approval received: yes, user selected option 1 before implementation.
- ADR: added `backlog/decisions/0003-github-actions-ci-and-apk-artifact.md`.

## README impact

Updated README with CI behavior and artifact naming.

## Diagram impact

Not needed because CI does not change runtime architecture or module dependency direction.

## Commits / logical change list

- `515fc38` Add GitHub Actions CI workflow

## Risks and follow-up

- The workflow has not run on GitHub because the branch cannot be pushed from this environment until SSH credentials are fixed.
- The dedicated QA suite is still pending task-9; CI contains a visible placeholder until then.
- Artifact versioning uses the GitHub run number for now; release versioning can be refined later.
<!-- SECTION:NOTES:END -->

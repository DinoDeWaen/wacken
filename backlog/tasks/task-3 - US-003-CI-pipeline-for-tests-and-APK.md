---
id: task-3
title: 'US-003: CI pipeline for tests and APK'
status: In Progress
assignee:
  - '@codex'
created_date: '2026-01-06 16:56'
updated_date: '2026-05-15 09:03'
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
- [ ] #1 Given a push or pull request When CI runs Then it executes ./gradlew test and the QA suite
- [ ] #2 Given a successful CI run When artifacts are published Then a versioned debug APK is available for download
- [ ] #3 Given any failing test When CI runs Then the pipeline fails and reports the failure
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Add a GitHub Actions workflow under `.github/workflows/ci.yml` for push and pull request events.
2. Configure CI to install/use JDK 21 and Android SDK support, run `./gradlew test`, run the QA suite command if available, and build `./gradlew assembleDebug`.
3. Add artifact upload for a versioned debug APK name using the workflow run number.
4. Keep the workflow aligned with existing Gradle wrapper commands and task-2 coverage gates.
5. Update README with CI behavior and artifact naming.
6. Add an ADR for the CI/CD decision if approved.
7. Validate locally with `./gradlew test` and `./gradlew assembleDebug`; inspect workflow syntax manually because GitHub Actions cannot run locally here.

Test strategy: CI configuration only; local validation uses the same Gradle commands that CI will run. No product behavior tests added.
Architecture impact: architecture-significant because this introduces CI/CD and artifact publishing. Approval required before workflow implementation. ADR expected.
Risks/assumptions: QA suite is planned but no dedicated QA task exists yet; CI should run `./gradlew test` now and include a placeholder/comment or command path that can be extended by task-9.
<!-- SECTION:PLAN:END -->

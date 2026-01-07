---
id: task-3
title: 'US-003: CI pipeline for tests and APK'
status: To Do
assignee: []
created_date: '2026-01-06 16:56'
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

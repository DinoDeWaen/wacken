---
id: TASK-223
title: 'BUG: Preserve supported Android compatibility in sync and music-link lookup'
status: To Do
assignee: []
created_date: '2026-10-17 16:45'
updated_date: '2026-10-17 16:47'
labels:
  - bug
  - android
  - compatibility
milestone: m-0
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a member using a supported Android version, I need app startup, rating sync and music-link lookup to use APIs available on that version so these flows do not fail on older devices. Full lintDebug currently reports14 existing NewApi errors: File.toPath requires26 and URLEncoder.encode(String, Charset) requires33 while minSdk is23. Resolve the API usage without hiding errors, weakening lint or raising the supported minimum. Out of scope: dependency upgrades and redesign. This is a required release2.34 quality gate discovered while validatingTASK156.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given the declared minimum Android API23, startup/local storage and URL encoding use compatible APIs while preserving UTF-8 query behavior.
- [ ] #2 Full Android lintDebug and release lintVital pass without suppressing or baselining the NewApi findings.
- [ ] #3 Regression tests cover reserved characters, spaces, Unicode and the changed startup/storage path; existing sync/provider/native suites remain green.
- [ ] #4 README, business requirements, diagram and ADR impacts are recorded with delivery-governance wording.
<!-- AC:END -->

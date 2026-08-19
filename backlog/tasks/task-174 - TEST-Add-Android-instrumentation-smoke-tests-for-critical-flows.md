---
id: task-174
title: 'TEST: Add Android instrumentation smoke tests for critical flows'
status: To Do
assignee: []
created_date: '2026-08-19 11:02'
labels:
  - future
  - testing
  - android
  - qa
dependencies: []
priority: low
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Business value: The highest-risk Android journeys can be verified on device or emulator before release instead of relying only on JVM tests.

As a maintainer, I want a small instrumentation smoke suite so that releases catch broken Android wiring, navigation, and offline startup behavior.

Scope: critical smoke tests only: startup with cache, band detail rating screen, schedule open, settings export entry point, and network-free launch behavior.

Out of scope: full visual regression coverage, exhaustive UI tests, and flaky tests that depend on live Supabase.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given the instrumentation suite runs on an emulator or device, then it verifies the app can start with cached data and no network.
- [ ] #2 Given the band detail screen is opened, then the smoke suite verifies rating controls render and can be interacted with without crashing.
- [ ] #3 Given the group schedule is opened, then the smoke suite verifies the screen renders without the previous null schedule failure.
- [ ] #4 The test command is documented and either added to CI or explicitly documented as manual release validation.
- [ ] #5 README, business requirements, diagram, and ADR impact are recorded using delivery-governance wording when implemented.
<!-- AC:END -->

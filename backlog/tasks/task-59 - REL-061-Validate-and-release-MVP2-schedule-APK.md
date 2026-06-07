---
id: task-59
title: 'REL-061: Validate and release MVP2 schedule APK'
status: To Do
assignee: []
created_date: '2026-06-07 15:37'
labels:
  - mvp2
  - release
  - uat
dependencies:
  - task-57
  - task-58
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Release Wacken Planner 2026 MVP2 after the one-group decision engine, conflict resolution, timeline generation, Android schedule view, and invite story are complete enough for local installation.

In scope:
- Create an MVP2 UAT checklist with representative group ratings, overlaps, optional decisions, vetoes, and lost alternatives.
- Run automated validation across domain/application/infrastructure/app as relevant.
- Run Android debug APK build and focused manual schedule UAT.
- Add release notes and bump version metadata for the MVP2 release version.
- Publish the GitHub release with the APK attached.

Out of scope:
- Play Store distribution, MVP3 travel/lunch/food behavior, and MVP4 PDF export.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given MVP2 stories are complete, when release validation runs, then automated tests and debug APK assembly pass.
- [ ] #2 Given representative group ratings and overlapping performances, when MVP2 UAT is run, then the generated schedule demonstrates GO, OPTIONAL, veto-blocked, conflict-winner, and lost-alternative cases.
- [ ] #3 Given the Android schedule screen is tested, when the APK is installed locally, then users can sync, rate, generate, and view the shared schedule.
- [ ] #4 Release notes document MVP2 scope, validation, known non-goals, and accepted risks.
- [ ] #5 The GitHub release is published with the debug APK attached and the tag/version metadata recorded.
- [ ] #6 README, business requirements, diagram, ADR, and architecture impact are recorded using canonical delivery-governance wording.
<!-- AC:END -->

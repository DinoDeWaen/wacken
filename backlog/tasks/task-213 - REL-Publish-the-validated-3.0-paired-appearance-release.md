---
id: TASK-213
title: 'REL: Publish the validated 3.0 paired appearance release'
status: To Do
assignee: []
created_date: '2026-10-17 09:22'
labels:
  - ux
  - release-3.0
milestone: m-2
dependencies:
  - TASK-212
priority: medium
type: task
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As an attendee, I want an installable update containing the validated dark/light experience so I can use it with existing festival data. Follow the current release process, stable signing and package identity. Publish release notes and artifacts only after all 3.0 feature and validation gates pass; do not claim future or untested functionality.

Validated baseline: TASK-201, design/release-2.34/themes.html; user approved 2026-10-17. Keep the existing Android Views stack and current domain/application/infrastructure boundaries. Assess architecture impact before implementation; significant changes require explicit approval and ADR handling.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given the full 3.0 milestone implementation, then required unit/BDD/integration/native checks and user review evidence pass or have explicitly accepted limitations before release.
- [ ] #2 Given an existing supported installation, when the signed 3.0 update is installed, then ratings, history, festival data, login state and pending operations are preserved and default appearance follows the documented migration behavior.
- [ ] #3 Given the release artifact, then version code/name, stable signature, APK digest, source tag and GitHub release refer to the same validated commit.
- [ ] #4 README, business requirements, design system and release notes accurately describe shipped System/Dark/Light behavior and changed navigation; superseded research guidance is clearly identified.
- [ ] #5 Release validation evidence includes build/install/update steps, changelog, checksums and outstanding risks; no milestone item is falsely marked complete.
- [ ] #6 README and business requirements impacts use canonical delivery-governance wording; approved behavior is documented, diagrams updated only where affected, and the full validation package is recorded before merge.
<!-- AC:END -->

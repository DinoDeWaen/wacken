---
id: task-134
title: 'UI: Apply metal design system to support flows'
status: To Do
assignee: []
created_date: '2026-06-19 06:28'
labels:
  - ui
  - design
  - settings
  - sync
  - import
dependencies: []
priority: medium
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Polish settings, login, import/admin, and sync/offline feedback so support flows feel as professional as the daily schedule and rating screens. These flows should use consistent sections, inputs, buttons, status panels, and short actionable messages.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given settings renders, when group, rating allocation, sync, and admin actions appear, then they are grouped into clear sections using shared panels and buttons.
- [ ] #2 Given login renders or shows an error/progress state, then inputs, primary action, and status messaging use the shared visual system.
- [ ] #3 Given import/admin renders file selection and validation results, then selected files, success, warnings, and errors use shared panels and readable status formatting.
- [ ] #4 Given sync/offline/pending states are shown, then they use the global status language and remain useful under weak connectivity.
- [ ] #5 Automated or focused compile validation proves the touched UI code builds; manual validation steps are documented.
- [ ] #6 README and business requirements impact are recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->

---
id: task-131
title: 'UI: Centralize Android visual tokens and component helpers'
status: To Do
assignee: []
created_date: '2026-06-19 06:26'
labels:
  - ui
  - design
  - technical-debt
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Create a shared Android UI styling foundation so colors, typography, spacing, buttons, panels, icon buttons, and status messages follow the visual design system instead of being redefined per Activity. This reduces visual drift and makes later screen polish consistent.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given the app UI code, when common colors and control styles are needed, then Activities use shared visual tokens/helpers instead of duplicating screen-local constants where practical.
- [ ] #2 Given an icon or action button, when it is rendered, then dimensions, text color, background, and content description follow the design system.
- [ ] #3 Existing observable behavior is preserved with focused tests or compile validation.
- [ ] #4 README and business requirements impact are recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->

---
id: task-103
title: 'UI: Apply rating color scheme to schedule'
status: To Do
assignee: []
created_date: '2026-06-14 19:54'
labels:
  - ui
  - schedule
dependencies: []
priority: medium
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Apply the agreed schedule rating color scheme to performance blocks and alternatives. This is a UI-only follow-up and should be implemented separately from tied-alternative behavior.

Recommended scheme:
- 5 star / Must see: border #FFD24A gold, subtle dark gold fill/accent #2F2A18. Strong highlight, premium/winner feel.
- 4 star / Strong choice: border #FF3B6B metal red, current dark panel #263033. Main normal selected act color.
- 2-3 star / Optional or weak choice: border #AAB3B7 steel grey, darker muted panel #20282A. Low priority but still visible.
- 1 star / Veto: no border/fill because vetoed acts must not be selected or shown as lost alternatives.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 5-star schedule blocks use #FFD24A border and #2F2A18 subtle dark gold fill/accent.
- [ ] #2 4-star schedule blocks use #FF3B6B border and the current dark panel #263033.
- [ ] #3 2-3-star schedule blocks use #AAB3B7 steel grey border and #20282A darker muted fill.
- [ ] #4 1-star veto acts are not shown as selected acts or lost alternatives.
- [ ] #5 Scratched/skipped blocks use diagonal bands in the same color family as their rating border.
- [ ] #6 Automated or screenshot-level validation covers representative 5-star, 4-star, 2-3-star, and scratched blocks.
<!-- AC:END -->

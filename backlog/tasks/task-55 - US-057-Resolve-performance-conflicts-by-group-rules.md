---
id: task-55
title: 'US-057: Resolve performance conflicts by group rules'
status: To Do
assignee: []
created_date: '2026-06-07 15:37'
labels:
  - mvp2
  - scheduling
  - domain
dependencies:
  - task-53
  - task-54
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a Wacken Planner group member, I want overlapping performances to be resolved according to the group decision rules, so the generated plan selects the best candidate and shows what was lost.

In scope:
- Resolve each conflict set using group ratings, must-see priority, want-to-see counts, veto counts, and optional outcomes.
- Use existing stage-distance data only as a tie-breaker where BR-014 and BR-017 require shortest/closest travel context.
- Return the selected performance, decision strength, rejected alternatives, and the lost alternative/runner-up.
- Select no performance when all overlapping options are vetoed.

Out of scope:
- Re-running conflicts for infeasible travel paths, lunch insertion, food suggestions, PDF output, and Android UI.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given overlapping performances include a band with any rating of 5, when the conflict is resolved, then a must-see option is preferred over lower-rated alternatives.
- [ ] #2 Given overlapping performances both have a rating of 5, when distance context is available, then the option with the better travel position is selected and the other is recorded as the lost alternative.
- [ ] #3 Given overlapping options only have ratings of 4, when the conflict is resolved, then the option with the most 4 ratings wins, then fewer vetoes wins, then shorter distance wins.
- [ ] #4 Given overlapping options only have ratings of 3, when the conflict is resolved, then the result is OPTIONAL and the option with the most 3 ratings is chosen.
- [ ] #5 Given overlapping options only have ratings of 2, when the conflict is resolved, then the result remains OPTIONAL.
- [ ] #6 Given all overlapping options are vetoed, when the conflict is resolved, then no performance is selected.
- [ ] #7 Automated tests cover BR-013 to BR-021 and runner-up/lost-alternative output.
- [ ] #8 README and business requirements impact are recorded using the canonical wording from delivery-governance.md.
- [ ] #9 Architecture impact is assessed; ADR impact is recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->

---
id: task-101
title: 'DEF: Investigate missing rated acts in generated schedule'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-14 19:35'
updated_date: '2026-06-14 19:42'
labels:
  - defect
  - schedule
  - data
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The group schedule is missing specific acts that are expected to appear. Friday missing acts reported: tuXedoo, Grand Magus, Danko Jones, Insanity Alert, and Hatebreed. Saturday missing acts reported: The Other and Lagwagon. Investigate whether each act has imported performance data, group ratings, conflict alternatives, vetoes, or scheduling-rule exclusions, and identify the root cause before implementing a fix.

As a festival attendee, I want rated acts that should be selected or shown as relevant alternatives to appear in the schedule, so the generated group schedule can be trusted.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 For each reported Friday act, the investigation records whether band data, performance data, and group rating data exist.
- [x] #2 For each reported Saturday act, the investigation records whether band data, performance data, and group rating data exist.
- [x] #3 The generated schedule decision path explains why each reported act is selected, rejected as an alternative, vetoed, or absent.
- [x] #4 If implementation is required, a failing automated regression test is added before changing scheduling logic.
- [x] #5 The root cause, recommended fix, and any business-rule ambiguity are documented before release work.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Inspected local master CSV data and live Supabase data for the reported bands.
2. Confirmed every reported band has a performance row and at least one live group rating.
3. Traced direct middle-30-minute overlaps and replicated the current schedule selection rules against the live ratings.
4. No code change was made because the observed absence follows the current implemented tie-break rules rather than missing data.
5. Documented the root cause and recommended follow-up business decision: clarify whether same-tier tied acts should be shown as scratched alternatives, selected differently, or remain hidden unless opened from the winner detail.
Architecture impact: no architecture-significant change; investigation only. No ADR required.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Investigation summary

Created this defect to investigate reported missing acts in the generated schedule. The reported acts are not missing from master data: each has a performance row in both the local CSV data and live Supabase data, and each has at least one live group rating.

The current scheduler is rejecting them through conflict resolution. It selects one act per direct/connected middle-30-minute conflict slice, then only selected acts become schedule blocks. Rejected acts can appear in the selected block detail only when they directly overlap the selected act; they do not get their own schedule block.

## Reported act findings

Friday:

- tuXedoo: performance exists Friday 12:00-12:45, Wackinger Stage; live rating sofie=3. It loses to Ten56. rated dino=3, sofie=2 by current tie/input-order rules.
- Grand Magus: performance exists Friday 14:00-14:45, Headbangers Stage; live ratings dino=4, sofie=3. It loses to Future Palace rated dino=4, sofie=3 by current tie/input-order rules.
- Danko Jones: performance exists Friday 15:45-16:45, Harder; live rating sofie=4. It loses to Any given Day rated dino=4, sofie=3 because both have a 4 and Any given Day has an additional 3.
- Insanity Alert: performance exists Friday 18:30-19:15, Wasteland Stage; live rating sofie=3. It loses to Saxon rated dino=3, sofie=3 because Saxon has more 3-star ratings.
- Hatebreed: performance exists Friday 19:00-20:00, Louder; live rating sofie=3. It loses to Animals as Leaders rated dino=3, sofie=2 by current tie/input-order rules among same-tier 3-star choices.

Saturday:

- The Other: performance exists Saturday 18:30-19:15, Wasteland Stage; live rating sofie=3. It loses to Ad Infinitum rated dino=4, sofie=2 because Ad Infinitum has a 4-star rating.
- Lagwagon: performance exists Saturday 19:00-19:45, W:E:T Stage; live rating sofie=4. It loses to Ad Infinitum rated dino=4, sofie=2 by current tie/input-order rules.

## Root cause

The immediate root cause is not missing imported performance data. It is the current conflict resolver behavior for tied or same-tier choices. When ratings tie at the same tier, the resolver uses the existing comparator: tier, number of 4-star ratings, veto count, number of 3-star ratings, distance score, then input order. In several reported cases, input order is deciding between otherwise equivalent acts.

There is a business-rule ambiguity: if two acts have equal highest priority, should the app prefer earliest input order/current comparator, prefer the user's own rating, prefer walking feasibility, prefer later/shorter conflict impact, or show both with one scratched?

## Recommended follow-up

Before changing code, decide the desired business rule for same-tier conflicts. Likely options:

- Keep current selection rules but show all directly rejected alternatives as scratched blocks in the schedule grid.
- Change tie-breaking to prefer the current user's own rating, then group rating counts.
- Change tie-breaking to prefer lower walking impact or route feasibility.
- For same-tier tied acts, show an explicit manual-choice state instead of hiding one act behind the winner.

## Acceptance criteria validation

- AC1: Friday band/performance/rating state documented above.
- AC2: Saturday band/performance/rating state documented above.
- AC3: Decision path for each reported act documented above.
- AC4: No implementation was performed because no missing-data defect was confirmed; a regression test should be added if a tie-break rule change is selected.
- AC5: Root cause, recommended fix options, and business-rule ambiguity documented.

## How to test

### Automated tests

No automated tests were run for this investigation-only task. Existing scheduler tests remain the relevant safety net if a rule change is requested.

### Manual validation

- Parsed data/wacken-2026/bands.csv, stages.csv, and performances.csv for the reported acts.
- Ran read-only Supabase SQL queries for the reported acts, live performance rows, and live ratings.
- Replayed the current conflict-resolution comparator against live Friday/Saturday data to identify the selected act each reported act loses to.

## TDD / BDD / approval-test evidence

No TDD implementation loop was started because this task was a diagnosis. If you choose a new tie-break or display rule, the next task should start with a failing schedule-generation or schedule-display regression test.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR: none.

## README impact

README impact: none, because this investigation does not change setup, commands, architecture, or released behavior.

## Business requirements impact

Business requirements impact: none yet, because no business rule was changed. A follow-up rule change should update conflict-resolution requirements.

## Diagram impact

Diagram impact: none, because this investigation does not change architecture or workflows.

## Commits / logical change list

- Created investigation defect task.
- Documented live data and current conflict-resolution outcomes for each reported act.

## Risks and follow-up

The schedule may still feel wrong to users because equal-tier choices can be hidden behind another act. That is a product-rule decision rather than a data import failure.
<!-- SECTION:NOTES:END -->

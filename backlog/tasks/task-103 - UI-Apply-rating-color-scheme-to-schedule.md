---
id: task-103
title: 'UI: Apply rating color scheme to schedule'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-14 19:54'
updated_date: '2026-06-15 05:03'
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
- [x] #1 5-star schedule blocks use #FFD24A border and #2F2A18 subtle dark gold fill/accent.
- [x] #2 4-star schedule blocks use #FF3B6B border and the current dark panel #263033.
- [x] #3 2-3-star schedule blocks use #AAB3B7 steel grey border and #20282A darker muted fill.
- [x] #4 1-star veto acts are not shown as selected acts or lost alternatives.
- [x] #5 Scratched/skipped blocks use diagonal bands in the same color family as their rating border.
- [x] #6 Automated or screenshot-level validation covers representative 5-star, 4-star, 2-3-star, and scratched blocks.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Added app unit tests for exact schedule block colors: 5-star gold/dark-gold, 4-star metal-red/dark-panel, and 2-3-star steel/muted.
2. Added an application unit test proving 1-star vetoed rejected acts are not exposed as slot lost alternatives or decision candidates.
3. Updated ScheduleBlockStyle to own exact border/fill colors and keep scratch rendering derived from the rating border color.
4. Updated ScheduleActivity to render schedule blocks using style-owned fill and border colors.
5. Updated GenerateSharedScheduleUseCase to filter vetoed alternatives from visible slot output.
6. Ran focused and broader automated validation.

Deviation: none. Architecture impact: not architecture-significant; no approval or ADR required.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Applied the agreed schedule rating color scheme to schedule blocks and kept scratched/skipped block bands tied to the rating border color family. Also filtered 1-star vetoed rejected acts out of visible schedule lost alternatives and detail candidates.

## Acceptance criteria validation

- AC1: 5-star blocks now use #FFD24A border and #2F2A18 fill.
- AC2: 4-star blocks now use #FF3B6B border and #263033 fill.
- AC3: 2-3-star blocks now use #AAB3B7 border and #20282A fill.
- AC4: 1-star vetoed rejected acts are filtered from slot lost alternatives and decision candidates.
- AC5: scratched blocks still derive diagonal band color from the active rating border color.
- AC6: app and application unit tests cover representative color styling, scratching, and veto filtering.

## How to test

### Automated tests

- /bin/zsh -lc "JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :application:test :app:testDebugUnitTest"
- /bin/zsh -lc "JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac"

### Manual validation

- Not run; automated unit coverage validates the business-visible output and block style mapping.

## TDD / BDD / approval-test evidence

- Added failing tests first for exact style colors and vetoed schedule alternatives, then implemented the smallest production changes to pass.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR impact: none, because this only changes Android presentation styling and existing application output filtering.

## README impact

README impact: none, because setup, commands, architecture, and public usage instructions did not change.

## Business requirements impact

Business requirements impact: none, because this implements an already captured UI/color-scheme story without changing the scheduling rules beyond hiding vetoed visible alternatives as specified.

## Diagram impact

Diagram impact: none, because no architecture or flow diagrams changed.

## Commits / logical change list

- ScheduleBlockStyle owns exact rating border/fill colors.
- ScheduleActivity uses style-owned colors for schedule blocks.
- GenerateSharedScheduleUseCase hides 1-star vetoed alternatives from visible slot output.
- Added application and app unit tests for the behavior.

## Risks and follow-up

- No known follow-up for this user story. task-100 remains a separate open defect for APK install diagnostics.
<!-- SECTION:NOTES:END -->

---
id: task-7
title: 'US-007: Band listing use case and UI skeleton'
status: In Progress
assignee:
  - '@codex'
created_date: '2026-01-06 16:57'
updated_date: '2026-05-15 10:04'
labels: []
dependencies: []
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## US-007: Band listing use case and UI skeleton

**As an** attendee
**I want** to browse the band lineup with stage and time
**So that** I can plan who to see
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given imported data When I run the band listing use case Then it returns bands with stage and time sorted by start time
- [ ] #2 Given the Android app When I open the band list screen Then I see band name, stage, and time without infrastructure details leaking into the UI layer
- [ ] #3 Given no imported data When I open the band list Then I see an empty state message
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Add an application `ListBandsUseCase` that reads performances and returns listing items sorted by start time.
2. Add application tests first for sorting by start time and formatting band/stage/time data without infrastructure dependencies.
3. Update Android `MainActivity` to render a simple band list screen from application-facing listing items, with an empty state when no performances are available.
4. Keep infrastructure details out of UI; UI wiring may construct in-memory repositories only at the composition edge.
5. Validate with `./gradlew test` and `./gradlew assembleDebug`.
6. README update not expected unless commands or setup change.

Test strategy: TDD for application listing behavior; Android skeleton validated by build because no UI test harness exists yet.
Architecture impact: not architecture-significant; uses existing application/domain/infrastructure boundaries and keeps UI at the edge. No ADR expected.
Risks/assumptions: final date/time display format is unresolved, so use a simple stable local date-time string for now and leave richer formatting to later refinement.
<!-- SECTION:PLAN:END -->

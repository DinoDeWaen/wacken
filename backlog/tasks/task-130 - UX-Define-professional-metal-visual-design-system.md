---
id: task-130
title: 'UX: Define professional metal visual design system'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-19 06:13'
updated_date: '2026-06-19 06:30'
labels:
  - ux
  - design
  - ui
  - metal
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Review and standardize the app visual language as a frontend/design task, not only a usability task. The app should feel professional, uniform, dark, metal, and festival-ready across overview, band detail, schedule, schedule detail, settings, sync, and import flows. This includes color palette, typography scale, button styles, spacing, section hierarchy, cards/panels, icon usage, empty/error states, and visual treatment of ratings/schedule status.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 A visual design audit covers all main screens: overview, band detail, schedule, schedule decision detail, settings, sync feedback, login, and import/admin flows.
- [x] #2 The audit defines a consistent metal design language for colors, typography, spacing, borders, buttons, panels, icons, and status states.
- [x] #3 Concrete implementation tickets are created for the highest-impact visual inconsistencies.
- [x] #4 The proposed design keeps the app readable in festival conditions: phone screen, dark environment, sunlight, fast scanning, and weak connectivity.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Inspected overview, band detail, schedule, schedule decision detail, settings, sync feedback, login, and import/admin UI code for visual patterns and hard-coded styles.
2. Reviewed task-125 UX findings to consolidate rather than duplicate prior UX work.
3. Added backlog/docs/visual-design-system.md with screen audit, design tokens, color palette, typography, spacing, buttons, panels, icons, status states, screen standards, and festival-readability rules.
4. Linked the design system from README and added BR-047a to make the cross-screen metal design language part of the business source of truth.
5. Created follow-up tickets task-131, task-132, task-133, and task-134 for the highest-impact implementation work.
6. Validated documentation formatting and rebuilt a fresh signed release APK per release process.

Design approach: documentation-first visual design system with implementation split into focused tickets.
Architecture impact: not architecture-significant; no code boundaries, persistence, dependencies, or APIs changed.
Deviation: no production UI code changed in this task; implementation is intentionally deferred to the created tickets.
Treatment: standard design-governance task.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Defined the professional metal visual design system for Wacken Planner 2026 and made it the source of truth for future UI work. The audit covers overview, band detail, schedule, schedule decision detail, settings, sync feedback, login, and import/admin flows.

Created concrete implementation tickets:
- task-131: UI: Centralize Android visual tokens and component helpers
- task-132: UI: Apply metal design system to band overview and detail
- task-133: UI: Apply metal design system to schedule and decision details
- task-134: UI: Apply metal design system to support flows

## Acceptance criteria validation

- AC1: Covered all main screens in backlog/docs/visual-design-system.md.
- AC2: Defined colors, typography, spacing, borders, buttons, panels, icons, rating states, schedule states, and status states.
- AC3: Created task-131 through task-134 as implementation tickets.
- AC4: Added festival readability rules for phone use, glare, dark conditions, fast scanning, weak connectivity, touch targets, and status clarity.

## How to test

### Automated tests

- git diff --check
- JAVA_HOME=$(/usr/libexec/java_home -v 21) WACKEN_RELEASE_* ./gradlew :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac assembleRelease

### Manual validation

- Reviewed the design document for all requested screen areas and confirmed follow-up tickets exist in Backlog.md.
- Verified signed APK metadata: versionCode 21, versionName 2.18, package be.wacken.planner.
- Verified APK signature with apksigner v1/v2 schemes.
- APK SHA-256 after fresh rebuild: 101546f8612ad6ef1c6cd0fb6c7f88bbe483aa4add8501fd10c8e8aaa6862ccb

## TDD / BDD / approval-test evidence

This is a documentation and backlog design-governance task. No production UI behavior changed; implementation tickets contain the behavior-level validation requirements.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR: none required.

## README impact

README impact: updated to link the visual design system from the Architecture/context section.

## Business requirements impact

Business requirements impact: updated BR-047a to state that the app should follow one professional dark metal visual design system across daily-use and admin screens.

## Diagram impact

Diagram impact: none, because no architecture relationships or workflows changed.

## Commits / logical change list

- Added backlog/docs/visual-design-system.md.
- Linked the design system from README.
- Added BR-047a.
- Created task-131, task-132, task-133, and task-134.

## Risks and follow-up

The design system is now defined but not yet implemented in production UI. The created follow-up tickets carry that implementation work.
<!-- SECTION:NOTES:END -->

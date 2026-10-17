---
id: task-127
title: 'UX: Refine band detail rating header'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-19 06:04'
updated_date: '2026-06-19 18:44'
labels:
  - ux
  - ui
  - ratings
dependencies: []
priority: medium
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The band detail page mixes own rating, reset, group ratings, running order, links, and biography in a long vertical flow. Improve the top detail area so own rating, reset action, group ratings, and schedule facts are visually grouped and easier to scan.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 The user rating stars, Reset action, and group ratings are grouped together in one clear rating section.
- [x] #2 Running order and band links are visually separated from rating controls.
- [x] #3 The layout remains readable on narrow phone screens without oversized buttons or clipped text.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Added focused BDD-style layout policy scenarios through TDD for narrow and wider device widths.
2. Split the band-detail header into dedicated themed rating, running-order, and band-link panels. The rating panel contains stars, Reset, and group ratings.
3. Replaced the clipped icon-sized Reset control with a compact text action; narrow screens stack the image and panels.
4. Ran focused and full Android validation, deleted the prior release APK, then built and verified a fresh signed APK.
5. Recorded validation evidence and closed the task.
Architecture impact: not architecture-significant; this is Android presentation layout over existing application data and does not change rating rules, persistence, APIs, modules, or dependencies. No ADR required.
Documentation impact: README, business requirements, and diagrams unchanged because no setup, workflow, architecture, or business rule changed.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

- Refined the band-detail top area into separate metal-styled panels for `Your Rating`, `Running Order`, and `Band Links`.
- `Your Rating` now groups the editable stars, compact Reset action, and read-only group ratings in one scan-friendly section.
- On narrow screens, the image and detail panels stack; Reset has a real text-button width so it remains readable.

## Acceptance criteria validation

- AC1: Rating stars, Reset, and group ratings are now contained in the `Your Rating` panel.
- AC2: Running Order and Band Links each render in their own visually distinct panel.
- AC3: `BandDetailLayoutPolicyTest` locks in vertical stacking below 520dp; controls use fixed compact dimensions and no text is placed in an icon-sized Reset button.

## How to test

### Automated tests

- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:testDebugUnitTest --tests be.wacken.planner.BandDetailLayoutPolicyTest :app:compileDebugJavaWithJavac`
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac assembleRelease`
- APK verification: `apksigner verify --verbose app/build/outputs/apk/release/app-release.apk` verified v1 and v2 signatures.

### Manual validation

- Open a band with group ratings on a normal and a narrow phone. Confirm the stars, Reset action, and group ratings are together; Running Order and links are separate; no Reset text is clipped.

## TDD / BDD / approval-test evidence

- Added `BandDetailLayoutPolicyTest` first for the acceptance-level narrow and larger screen scenarios, then used the policy in the Android layout. No legacy behavior was refactored beyond the explicit presentation change.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR impact: none, because no architectural boundary, persistence schema, external API, or dependency changed.

## README impact

- README impact: none, because setup, public workflow, architecture, commands, and troubleshooting instructions did not change.

## Business requirements impact

- Business requirements impact: none, because this refines the existing band-detail presentation without changing business rules.

## Diagram impact

- Diagram impact: none, because the existing system structure is unchanged.

## Commits / logical change list

- Added a responsive detail-layout policy and test.
- Grouped rating controls and group ratings; separated running-order and link panels.
- Rebuilt signed release APK: `app/build/outputs/apk/release/app-release.apk`, SHA-256 `63e26e77c31e8bf034e242e09aa1d1d229a4b5269d28b33b70d9c3a5997a6560`.

## Risks and follow-up

- Image loading remains best-effort and hides unavailable images, preserving the prior behavior.
<!-- SECTION:NOTES:END -->

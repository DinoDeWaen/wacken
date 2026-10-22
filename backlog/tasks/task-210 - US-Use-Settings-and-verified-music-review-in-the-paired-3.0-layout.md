---
id: TASK-210
title: 'US: Use Settings and verified music review in the paired 3.0 layout'
status: In Progress
assignee:
  - '@codex'
created_date: '2026-10-17 09:21'
updated_date: '2026-10-22 15:26'
labels:
  - ux
  - release-3.0
milestone: m-2
dependencies:
  - TASK-205
  - TASK-206
  - TASK-202
references:
  - settingsactivity
  - musiclinksreviewactivity
  - bandlinkreviewactivity
  - bandmetadatareviewactivity
modified_files:
  - app/src/androidTest/java/be/wacken/planner/MusicLinksReviewAndroidTest.java
  - app/src/androidTest/java/be/wacken/planner/CriticalFlowsAndroidTest.java
  - app/src/main/java/be/wacken/planner/WackenTheme.java
  - app/src/main/java/be/wacken/planner/MusicLinksReviewActivity.java
  - app/src/main/java/be/wacken/planner/SettingsActivity.java
  - app/src/main/java/be/wacken/planner/BandLinkReviewActivity.java
  - app/src/main/java/be/wacken/planner/BandMetadataReviewActivity.java
  - >-
    groma/systems/wacken/containers/android-application/components/settingsactivity.md
  - >-
    groma/systems/wacken/containers/android-application/components/bandlinkreviewactivity.md
  - >-
    groma/systems/wacken/containers/android-application/components/bandmetadatareviewactivity.md
  - >-
    groma/systems/wacken/containers/android-application/components/musiclinksreviewactivity.md
  - README.md
  - backlog/docs/business-requirements.md
  - >-
    app/src/test/java/be/wacken/planner/BandLinkReviewActivityRegressionTest.java
priority: medium
type: enhancement
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a festival administrator, I want grouped Settings and trustworthy review screens in both appearances so I can manage links without losing work. Adapt Settings, Link bands and TASK-202 verified music review using production identity evidence. Include provider setup/errors, progress, cancellation, retry, approval/rejection and no-match. No new provider integration or changes to approved verification rules in this presentation story.

Validated baseline: TASK-201, design/release-2.34/themes.html; user approved 2026-10-17. Keep the existing Android Views stack and current domain/application/infrastructure boundaries. Assess architecture impact before implementation; significant changes require explicit approval and ADR handling.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given Settings, when managing a festival, then Link bands and festival music discovery are adjacent and band-only discovery remains available on the appropriate detail page.
- [ ] #2 Given verification results, then candidate identity, canonical URL, clickable sources and verified/unverified rationale are readable in both appearances; unverified candidates cannot be saved.
- [ ] #3 Given a theme change during search/review, then search text, scope, progress and reviewed selections are retained; explicit approval alone writes missing fields.
- [ ] #4 Given rejection/no match/offline/missing configuration/rate limit/cancel/error, then accurate recovery/status is visible and existing metadata, ratings and history remain unchanged.
- [ ] #5 Native tests cover both entry points, evidence and approval gates, ongoing work across theme/recreation, no overwrite and provider failure states.
- [ ] #6 README and business requirements impacts use canonical delivery-governance wording; approved behavior is documented, diagrams updated only where affected, and the full validation package is recorded before merge.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Standard presentation slice within existing Views/ADR0024 and approved retained-review pattern. 1. Characterize explicit verification/missing-field/identity approval gates and provider failures using existing application/native suites; add red native checks for theme/recreation approval retention,200% wrapping and editable link-review state. 2. Group Settings festival administration together with adjacent link/music actions; use shared headings, readable14/16sp wrapping text, live status and retained scroll. 3. Restyle music evidence/search/check controls with paired tint/focus, full canonical source labels, retained scope/search/progress/choices; preserve the existing retained worker and explicit approval-only writes. 4. Keep own-database linking search/match selection/scroll across recreation with vertical48dp controls and contextual native adapters. 5. Retain metadata search/result/checked proposals through recreation without repeating active search; guard destroyed callbacks and show accurate error/retry while retaining existing missing-field use cases/providers. 6. Verify both entry points, proof/no-overwrite/failure states, native large-font/theme/recreation and full JVM/QA/ArchUnit/lint/API/database/Groma gates; update README/BR/map only for actual presentation meaning, fresh stable-key signed checkpoint before closure. No new production dependencies,ports,DB/API,domain policy or architecture-significant boundary; Standard/ADR0024 already approved. Risks: async search callbacks, stale proposal acknowledgement after edited terms, controls rebuilt during keyboard input and native hierarchy restoration. Integrated human/device/TalkBack/contrast review remains212.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Cross-cutting architecture approved2026-10-17: follow ADR-0016 and the implemented2.34 lifecycle from TASK-216–220. The3.0 appearance/navigation redesign must preserve canonical band metadata, member-specific current band ratings, separate festival planning/after-ratings, one-time creation snapshots, archive-only promotion and source-aware corrections. No theme/layout change may reintroduce latest-event-as-band-rating or cross-festival fallback.
<!-- SECTION:NOTES:END -->

---
id: TASK-213
title: 'REL: Publish the validated 3.0 paired appearance release'
status: In Progress
assignee:
  - '@codex'
created_date: '2026-10-17 09:22'
updated_date: '2026-10-23 07:46'
labels:
  - ux
  - release-3.0
milestone: m-2
dependencies:
  - TASK-212
references:
  - app/build.gradle
  - app/src/androidTest/java/be/wacken/planner/SignedUpgradeInstrumentation.java
modified_files:
  - app/src/androidTest/java/be/wacken/planner/SignedUpgradeInstrumentation.java
  - app/build.gradle
  - releases/v3.0.md
  - README.md
  - backlog/docs/business-requirements.md
  - backlog/docs/release-3.0-design-contract.md
  - backlog/docs/visual-design-system.md
  - design/release-2.34/README.md
  - design/release-2.34/validation-review.md
  - design/release-2.34/research.md
  - backlog/docs/release-process.md
priority: medium
type: task
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As an attendee, I want an installable update containing the validated dark/light experience so I can use it with existing festival data. Follow the current release process, stable signing and package identity. Publish release notes and artifacts only after all 3.0 feature and validation gates pass; do not claim future or untested functionality.

Validated baseline: TASK-201, design/release-2.34/themes.html; user approved 2026-10-17. Keep the existing Android Views stack and current domain/application/infrastructure boundaries. Assess architecture impact before implementation; significant changes require explicit approval and ADR handling.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given the full 3.0 milestone implementation, then required unit/BDD/integration/native checks and user review evidence pass or have explicitly accepted limitations before release.
- [ ] #2 Given an existing supported installation, when the signed 3.0 update is installed, then ratings, history, festival data, login state and pending operations are preserved and default appearance follows the documented migration behavior.
- [ ] #3 Given the release artifact, then version code/name, stable signature, APK digest, source tag and GitHub release refer to the same validated commit.
- [ ] #4 README, business requirements, design system and release notes accurately describe shipped System/Dark/Light behavior and changed navigation; superseded research guidance is clearly identified.
- [ ] #5 Release validation evidence includes build/install/update steps, changelog, checksums and outstanding risks; no milestone item is falsely marked complete.
- [ ] #6 README and business requirements impacts use canonical delivery-governance wording; approved behavior is documented, diagrams updated only where affected, and the full validation package is recorded before merge.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Standard release packaging within the existing signed GitHub APK channel. 1. Confirm TASK204–212 Done and current stable2.35.1/code44; prepare3.0/code45 metadata, truthful release notes/README/BR/design status and preserve unrelated user work. 2. Extend the opt-in signed upgrade probe to assert missing legacy appearance preferences resolve to System and follow device mode, retaining all existing data/session/pending/provenance assertions; use the old-version negative control and an explicit wrong-appearance control if feasible. 3. Build a fresh clean stable-key candidate and separately signed test probe from one exact commit; run full JVM/BDD/QA/coverage/ArchUnit/lint/Groma/database/native API23/API36 gates on final source. 4. Download the exact published2.35.1 baseline and verify stable signer/digest; use a separate fresh owned emulator data directory for baseline seed, rejected old-version control, install-r3.0 and retained-state/default-System/offline launch verification. 5. Bind exact commit/APK/CI/upgrade/native evidence and checksums, tagv3.0 and publish the existing official asset names only after gates; independently download and verify assets/signature/version/tag. 6. Finalize canonical validation/impacts, AC and Done before PR merge. Existing Standard/ADR0024, ADR0016 and signed distribution decision retained; no schema/API/security/framework/dependency/domain-policy or architecture-significant change. Risks: stable signing continuity, accidental dirty-source packaging, source/tag/digest binding, emulator state, default appearance and honest physical/outdoor/human-feedback limits. README/BR/design/release notes updated; diagrams/ADR no new boundary impact expected.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Cross-cutting architecture approved2026-10-17: follow ADR-0016 and the implemented2.34 lifecycle from TASK-216–220. The3.0 appearance/navigation redesign must preserve canonical band metadata, member-specific current band ratings, separate festival planning/after-ratings, one-time creation snapshots, archive-only promotion and source-aware corrections. No theme/layout change may reintroduce latest-event-as-band-rating or cross-festival fallback.
<!-- SECTION:NOTES:END -->

---
id: TASK-220
title: 'TEST: Validate the complete band-to-festival rating lifecycle and upgrade'
status: In Progress
assignee:
  - '@codex'
created_date: '2026-10-17 13:18'
updated_date: '2026-10-17 19:10'
labels:
  - ratings
  - festival
  - architecture
  - release-2.34
milestone: m-0
dependencies:
  - TASK-156
  - TASK-169
  - TASK-172
  - TASK-174
  - TASK-217
  - TASK-218
  - TASK-219
modified_files:
  - backend/flyway/tests/rating_state_api.py
  - app/src/androidTest/java/be/wacken/planner/SignedUpgradeInstrumentation.java
  - app/src/androidTest/AndroidManifest.xml
  - scripts/verify-signed-upgrade.sh
  - scripts/tests/test-signed-upgrade-runner.py
  - .github/workflows/ci.yml
  - app/build.gradle
  - backlog/docs/release-process.md
  - README.md
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As the release owner, I need end-to-end evidence that the approved rating lifecycle survives imports, sync, archive, corrections and upgrades. Validate the integrated implementation, not only isolated units. Retain actual artifact/source identifiers and no-skip database/native gates in the delivery package. Final signing/version/publication remains TASK-214; this story owns lifecycle integration and legacy/pending-data preservation evidence.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given band rating4, when a festival is created, planning changes to5, after-rating becomes3 and it is archived, then history retains5/3, band rating becomes3 and the next festival starts at3.
- [ ] #2 Given no/reset after-rating, duplicate submissions, interrupted sync/archive or concurrent edits, then previous established ratings, user ownership and history are preserved with no partial or false-success transitions.
- [ ] #3 Given an archived correction, then correcting the current source updates that personal band rating atomically with audit; correcting an older festival preserves a newer established source and existing later-festival planning snapshots.
- [ ] #4 Given a previous supported signed installation containing archived and active history, metadata, auth and pending work, when upgraded, then migration/normal sync preserve data and do not promote active or ambiguous legacy values accidentally.
- [ ] #5 Unit/BDD, real PostgreSQL/PostgREST/RLS, Room migration and native tests pass on the final source, including mandatory Summer Breeze creation regressions; no skipped required gate is treated as a pass.
- [ ] #6 README/test commands and canonical business/diagram/ADR impacts are recorded, with reproducible manual checks, exact-source reports and upgrade evidence linked for TASK-214.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Standard approved ADR0016 lifecycle/upgrade validation. Extend existing real PostgreSQL/PostgREST runner with one continuous authenticated band4 → create planning4 → plan5/after3 → archive band3 → create nextplanning3 → source/older correction scenario, preserving other-owner/pending/replay/fault coverage. Add an explicit opt-in test-APK Instrumentation probe (no production component/new framework) and safe emulator-only shell runner: install actual published stable-signed2.33.5, seed real old Room9 archived/active/ambiguous history plus canonical metadata, auth/preferences and pending operations, install current same-key candidate with adb -r without uninstall, initialize migration/normal cached startup and verify all data/ownership/pending/provenance. Runner validates serial/isolated datadir, APK package/cert/digests and fails on any missing check; signs test probe with existing key without exposing secrets. Intermediate code41 migration proof here, final higher-code2.34 rerun in214. Add fixtures/probe assertions first and exercise red where feasible, then integration green. Full JVM/QA/coverage/lint, no-skip SQL/API/SummerBreeze16/native API23/36 on final source, clean signed task build. README/release-process/BR/ADR0016 validation impacts and exact source/artifact/report evidence. No production schema/API/policy/dependency changes; existing user approval for lifecycle plus220 validation covers testing adapters. Production deploy/version/publication remains214.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
2026-10-17 user-approved architecture: ADR-0016 governs canonical shared bands, member-specific established band ratings, independent festival planning/after-ratings, one-time creation snapshots and atomic archive promotion. Missing after-ratings retain the prior band rating; archived corrections update it only while their festival remains the source. TASK-216–220 implement and validate the lifecycle. This supersedes earlier latest-event prefill wording; do not treat approved planning as shipped behavior.

Continuous authenticated PostgreSQL/PostgREST lifecycle passed locally, including4→4→5/3→3→3, current/older-source corrections, reset/replay and separate members. Signed published2.33.5 baseline (SHA256 a94c393f45b9f617e99cec2fbf6f0a6d33c2d91dde2122381872465761549f3a) seeded actual Room9 with auth/metadata/history/pending fixtures. Negative control rejected old schema; stable-signed intermediate candidate219 installed with adb-r, probe verified Room12, retained provenance/ownership/pending/auth/private file and normal offline cached startup. Probe registration required an opt-in Gradle instrumentation runner property; normal suite remains unchanged. Fresh emulator cold boot/SystemUI was slow; probe launch now uses a bounded monitor. Final exact-source CI/build/fresh runner proof remains before closure.
<!-- SECTION:NOTES:END -->

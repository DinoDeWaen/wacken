---
id: TASK-244
title: 'REL: Publish signed Android 2.35.1 with ArchUnit checks'
status: In Progress
assignee:
  - '@codex'
created_date: '2026-10-21 15:14'
updated_date: '2026-10-21 15:15'
labels:
  - release
dependencies: []
modified_files:
  - app/build.gradle
  - releases/v2.35.1.md
  - README.md
priority: medium
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Publish the requested patch release following completed TASK-243. Retain signed GitHub APK distribution and runtime behavior; include ArchUnit development checks in the release source. Latest stable is2.35/code43, so use2.35.1/code44 and the same stable certificate. Build from a clean checkout preserving unrelated user changes. Business requirements impact: none planned because this patch packages a development-time architecture gate without product behavior changes.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Fresh signed APK uses versionName2.35.1/code44, package be.wacken.planner and the same stable certificate as published2.35.
- [ ] #2 Required JVM/ArchUnit/QA/coverage/lint, Groma, real database/API and native API23/API36 release gates pass on final source.
- [ ] #3 Exact published2.35 to final2.35.1 signed install-r retains fixture login/cache, pending operations, own/other ratings, planning/after values, history and corrections; negative control rejects the old version.
- [ ] #4 Tag and GitHub release publish APK, checksum, notes, installation guidance and exact source/validation evidence; downloaded assets and remote tag are verified.
- [ ] #5 README release links and notes describe actual scope and limits; final task evidence uses canonical README/business requirements/diagram/ADR impact wording and is complete before merge.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Confirm latest2.35/code43, stable signing key and completed TASK243. Prepare2.35.1/code44 release metadata, notes and README link; routine packaging introduces no new architecture decision or product behavior. 2. Commit exact source and build/test in a clean checkout excluding unrelated user changes; verify stable signer and metadata. Run required CI JVM/ArchUnit/QA/coverage/lint/Groma/database/API and API23/API36 gates. 3. On a separate fresh emulator, seed actual published2.35 using existing current-baseline signed probe, require old-version rejection, install-r44 and verify all retained state/offline launch against exact candidate. 4. Tag exact tested source, publish APK/checksums/notes/provenance/upgrade evidence, download and verify every asset/signature/version/tag. Complete validation notes and task through CLI, commit closure and merge PR only after validation package is complete. Minimal release packaging; no new ADR, runtime diagram or business requirement change. Existing approved GitHub distribution and ADR0023 retained.
<!-- SECTION:PLAN:END -->

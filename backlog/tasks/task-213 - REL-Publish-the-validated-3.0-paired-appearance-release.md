---
id: TASK-213
title: 'REL: Publish the validated 3.0 paired appearance release'
status: Done
assignee:
  - '@codex'
created_date: '2026-10-17 09:22'
updated_date: '2026-10-23 08:18'
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
- [x] #1 Given the full 3.0 milestone implementation, then required unit/BDD/integration/native checks and user review evidence pass or have explicitly accepted limitations before release.
- [x] #2 Given an existing supported installation, when the signed 3.0 update is installed, then ratings, history, festival data, login state and pending operations are preserved and default appearance follows the documented migration behavior.
- [x] #3 Given the release artifact, then version code/name, stable signature, APK digest, source tag and GitHub release refer to the same validated commit.
- [x] #4 README, business requirements, design system and release notes accurately describe shipped System/Dark/Light behavior and changed navigation; superseded research guidance is clearly identified.
- [x] #5 Release validation evidence includes build/install/update steps, changelog, checksums and outstanding risks; no milestone item is falsely marked complete.
- [x] #6 README and business requirements impacts use canonical delivery-governance wording; approved behavior is documented, diagrams updated only where affected, and the full validation package is recorded before merge.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Standard completed: confirmed TASK204–212 Done and baseline2.35.1/code44; prepared3.0/code45 and truthful current README/BR/design/release notes with historical research banners. Extended opt-in signed upgrade assertions for missing preference→System, actual device palette and no stored override. Built exact f575548 clean with verified existing key, complete JVM/QA/ArchUnit/BDD/coverage/lint, separate signed probe and all4 final-source CI gates including API23/API36 each99 native cases and real DB/API regressions. Fresh explicit-image release213 AVD verifies downloaded code44 seed, expected old-version rejection, install-r45, retained state/offline launch and actual System Dark/Light passes with exact settings restoration. Final Groma two-scan gate and runner fixtures pass. Tagged exact source, published5 assets, independently downloaded and verified checksums/signature/v1/v2/metadata/source/tag/upgrade evidence/latest status. Final validation/canonical impacts/AC completed before PR merge. No architecture-significant decision; approved Standard/ADR0024/ADR0016 and existing signed distribution channel retained. Deviations: storage audit replaced ignored-datadir/reused-AVD trial with a genuinely new AVD and explicit fresh image; existing preference regressions plus actual signed Dark/Light checks cover default-mode controls rather than adding an artificial mutation phase. Evidence-only closure follows publication, leaving the tag at exact built/tested source.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Published https://github.com/DinoDeWaen/wacken/releases/tag/v3.0 with official signed app-release.apk, SHA256SUMS, release-validation.json, upgrade-evidence.zip and Groma report. Version3.0/code45, package be.wacken.planner, min23/target36. Exact tagged/build/validated source f57554828a3ae60c288e967a97d7a06b64113aa1; APK SHA256 814de8e72a4e9552f569dd0f19ff9f3e485166976c7b033e7015b265d2cd12fb. Stable signer ede2ae97735a305900e87bc0fdc021c03382db4e30435f2349a96a3fdcc7545f. All TASK204–212 feature/native work is included. Current README/requirements/design authority and release notes describe actual System/Dark/Light, labeled/adaptive navigation and retained flows; conflicting original research is explicitly historical.

## Acceptance criteria validation

AC1: Final-source CI https://github.com/DinoDeWaen/wacken/actions/runs/39920396804 all4 jobs green, head f575548, tested merge e6503f2b78e2f0f2b31f11d0f46e6fa35cafd433 over3a98d4426d8da2a211cae6671a99591f481779ec. Native API23/API36 each99 cases pass. TASK212 supplies full99 Dark/full99 Light plus actual paired200% normal/narrow TalkBack/keyboard/target/contrast/images, with17 October qualitative user design approval. No fabricated physical/outdoor/human spoken-feedback claim.
AC2: Exact downloaded published2.35.1/code44 digest26ae82deb30bd0f91aa4c0a7956db699e1755f26e5e3c27234cb986c6df561d7 seeded on new owned release213 API36 AVD. Its explicitly new data image is /private/tmp/wacken-213-fresh-avds/release213.avd/userdata-qemu.img; hardware-qemu evidence confirms that path and no userdata/application was copied. Seed PASS, before-update code44 negative control expected FAIL, install-r45 PASS, verify-current PASS. Login/private file, canonical metadata/links/festival identity/lineup, own/other established/planning/after values, dated history/correction audit, pending planning/history/schedule operations and normal offline launch retained. Missing appearance stays unset, resolves to System and follows actual device Dark and Light on repeated signed-app verification; no forced explicit preference. Original night/airplane/Wi-Fi settings restored exactly.
AC3: Clean checkout and existing key verified without replacement; release APK and separate opt-in signed test probe checked. v1/v2 signatures, version/name/package/min/target/digest and remote tag bind to exact f575548. Tag never moves to the evidence-only closure commit.
AC4: README release link/current behavior, BR047/047a/061 and current capability scope, native design contract/system and historical research handover now agree. No new provider, backend appearance preference, database migration or rating/scheduling/auth policy.
AC5/6: All five assets independently downloaded after publication; every checksum entry passes; downloaded APK signer/v1/v2/version/digest, manifest source and fresh upgrade ZIP verified. GitHub latest isv3.0. Full package/risks/impacts finalized before PR merge; no incomplete milestone task is counted complete.

## How to test

### Automated tests

From clean exact-source checkout with existing signing key: ./gradlew test qaTest :app:lintDebug assembleRelease --rerun-tasks passed; separately assembleDebugAndroidTest -PwackenUpgradeProbe and sign only the test APK using the same stable key. Domain63/application163/infrastructure29/QA3 pass; Android debug/release JVM variants each257 pass with16 existing fixture-environment integration skips. Final-source CI mandatory real PostgreSQL/PostgREST creation executes those16 without skips and continuous lifecycle/current-older correction/reset/replay/date/member-isolation gates pass. ArchUnit/BDD/coverage/lint pass.
Final CI API23/API36 each99 native tests. Reviewed Groma gate passes two clean scans,96 components/299 sources/13 flows/83 exact reviewed duplicate groups;27 gate fixtures,8 signed-upgrade runner fixtures,3 smoke-runner and6 immutable-image fixtures run in required CI. Local8 signed-upgrade safety fixtures pass.
Exact current-baseline signed upgrade command and reproduction are in backlog/docs/release-process.md and releases/v3.0.md. Published release-validation.json records source/CI/build/counters/baseline/native/upgrade/signer/digests; upgrade-evidence.zip includes old-version control and signed System Dark/Light outputs with device/storage provenance.

### Manual validation

Agent inspected Task212 actual Android images/source/rendered architecture and release source isolation, fresh data-image ownership, signed upgrade phases/default mode, five downloaded public assets, checksums/signature/metadata and remote tag. Human qualitative prototype approval is17 October2026; automated service focus/image review is not a human spoken-feedback study. No user data/device was wiped. Unrelated shared-worktree changes were excluded and preserved.

## TDD / BDD / approval-test evidence

Release packaging changes version/docs only in production. Existing TASK205–212 BDD/characterization/native bug regressions remain green. Opt-in signed probe adds explicit missing-preference/System/device-palette/no-override assertions alongside existing retained-state checks. Old published code44 is rejected by the exact same version gate before installing45; subsequent current/dark/light phases pass. Existing native preference tests cover explicit/invalid modes, System transitions and recreation; no additional production policy or synthetic approval is introduced.

## Architecture impact

Architecture-significant change: none. Existing signed GitHub distribution authorization and user request to deliver all3.0 tasks apply. Existing Standard/ADR0024 presentation and ADR0016 lifecycle remain; no new framework/dependency/schema/API/security/deployment channel or domain-policy boundary.
ADR impact: none, because version metadata, release docs and opt-in upgrade assertions introduce no new architecture decision.

## README impact

README impact: updated actual3.0 behavior, migration default, current release/notes link and historical design/evidence authority.

## Business requirements impact

Business requirements impact: updated current paired presentation/navigation/retention capabilities and BR047/047a/061 to the approved implemented behavior. ADR0016 and backend/rating/scheduling/auth rules remain unchanged. Replaced stale development-status prose with current scope and preserved physical/field limitations.

## Diagram impact

Diagram impact: none, because packaging and test-probe assertions change no runtime boundary, source ownership, collaboration or flow; final reviewed Groma gate remains equal.

## Commits / logical change list

f575548:3.0/code45, extended signed System upgrade probe, release notes/README/BR/current design authority and clearly historical research. Final closure commit contains task evidence only. PR https://github.com/DinoDeWaen/wacken/pull/16. Remote v3.0 remains f575548, the exact clean APK source, before documentation-only closure/merge.

## Risks and follow-up

Manual GitHub APK installation, Android downgrade limits and the historical lost-key reinstall exception remain documented; the stable key was not replaced. Physical Switch Access, spoken-feedback human study, real-phone sun/glove/wet-screen/one-handed observations are unperformed, not passes. Native emulator evidence is representative, not exhaustive certification. Known source-scanner limitations and two previously accepted stale importer arrows remain explicit; no new exception.
Storage provenance review discarded an initially reused-AVD attempt despite its app-upgrade pass. The final accepted run uses a newly initialized release213 AVD and verified explicit data-image path, with all upgrade/System phases repeated successfully. Missing local avdmanager/legacy init-image assumptions did not affect source/artifact; new owned config copied hardware configuration only and initialized fresh data through the installed image. Publication precedes evidence-only task closure so asset/tag acceptance can actually be verified before Done/merge.
<!-- SECTION:NOTES:END -->

---
id: TASK-220
title: 'TEST: Validate the complete band-to-festival rating lifecycle and upgrade'
status: Done
assignee:
  - '@codex'
created_date: '2026-10-17 13:18'
updated_date: '2026-10-17 19:42'
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
  - backlog/docs/release-2.34-lifecycle-validation.md
  - backlog/docs/business-requirements.md
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As the release owner, I need end-to-end evidence that the approved rating lifecycle survives imports, sync, archive, corrections and upgrades. Validate the integrated implementation, not only isolated units. Retain actual artifact/source identifiers and no-skip database/native gates in the delivery package. Final signing/version/publication remains TASK-214; this story owns lifecycle integration and legacy/pending-data preservation evidence.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given band rating4, when a festival is created, planning changes to5, after-rating becomes3 and it is archived, then history retains5/3, band rating becomes3 and the next festival starts at3.
- [x] #2 Given no/reset after-rating, duplicate submissions, interrupted sync/archive or concurrent edits, then previous established ratings, user ownership and history are preserved with no partial or false-success transitions.
- [x] #3 Given an archived correction, then correcting the current source updates that personal band rating atomically with audit; correcting an older festival preserves a newer established source and existing later-festival planning snapshots.
- [x] #4 Given a previous supported signed installation containing archived and active history, metadata, auth and pending work, when upgraded, then migration/normal sync preserve data and do not promote active or ambiguous legacy values accidentally.
- [x] #5 Unit/BDD, real PostgreSQL/PostgREST/RLS, Room migration and native tests pass on the final source, including mandatory Summer Breeze creation regressions; no skipped required gate is treated as a pass.
- [x] #6 README/test commands and canonical business/diagram/ADR impacts are recorded, with reproducible manual checks, exact-source reports and upgrade evidence linked for TASK-214.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Completed continuous authenticated real API lifecycle and source-aware correction/reset/replay/member assertions; fixture-only Instrumentation selected by -PwackenUpgradeProbe (normal JUnit unchanged); safe fresh-emulator signed baseline seed/negative control/adb-r candidate verification and6 safety regressions in CI; bounded startup monitor; portable POSIX runner repaired from CI red. Full green exact-source CI10914a0: JVM/QA/coverage/lint/SQL/API/SummerBreeze16/native68 on each API23/API36; fresh clean stable-signed task build and full fresh signed Room9→12 runner passed without uninstall/wipe. README/BR/release-process and durable lifecycle validation report updated. Existing ADR0016 approval covers test scope; no production architecture/schema/API/dependency change. Deviation: initial AGP registration override required property-based runner; fresh emulator cold boot delayed first attempt. Intermediate41→41 proof explicitly differs from final higher-code release gate in214. No skipped required gate accepted.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Added one continuous real authenticated lifecycle scenario covering band4 → new planning4 → planning5/after3 → archive band3 → next planning3, then current/older-source corrections, reset retention, replay and member isolation. Existing fault/concurrency/RLS/rollback gates remain in the same runner.

Added an explicit test-only signed upgrade Instrumentation and safe emulator-only shell runner, plus6 safety regressions required in CI. The probe seeds actual published Room9, requires an old-version negative control, installs the same-key candidate with adb-r and verifies migration/normal cached startup with metadata/auth/history/pending/provenance preservation. Default AndroidJUnitRunner remains unchanged; no ignored/no-op test is counted as upgrade success.

## Acceptance criteria validation

1. Continuous HTTP scenario asserts4→4→5/3→3→3, retained planning/after/event/source and creator retry preservation.
2. Reset/no-after, own/other owners, duplicate/uncertain replay, rollback and edit/archive race suites pass; signed fixtures retain pending work and ambiguous history without promotion.
3. Current-source correction updates established/audit without refreshing later planning; older correction preserves newer established source and later planning.
4. Actual published stable-signed2.33.5/Room9 installation was updated without uninstall/wipe to Room12. Auth/access/refresh/expiry/group, canonical metadata/links, festival identities/lineups, dated/unknown/pending events, other member, legacy pending and schedule/private-file data all preserved. Normal offline startup rendered cached lineup; pending work remained.
5. CI39061674547 on10914a0c52222274a516e0af6a9fae0c53b804b2 passes all jobs: full JVM/QA/coverage/lint,6 upgrade safety,6 mirror safety, real creation16/no skips, SQL/PostgREST/RLS/lifecycle and68 native on each API23/API36 with0 skipped/failed.
6. Reproducible report in backlog/docs/release-2.34-lifecycle-validation.md and commands in release-process/README link exact code, APK digests, signer and limitations for214.

## How to test

### Automated tests

python3 scripts/tests/test-signed-upgrade-runner.py:6 pass. bash scripts/verify-rating-state.sh: full SQL/API lifecycle pass. Full final-source CI link https://github.com/DinoDeWaen/wacken/actions/runs/39061674547.
Clean JDK21 stable-signed build: ./gradlew :app:clean test qaTest :app:lintDebug :app:lintVitalRelease :app:assembleRelease :app:assembleDebugAndroidTest -PwackenUpgradeProbe passed. Package be.wacken.planner, minSdk23, v1/v2 stable cert ede2ae97735a305900e87bc0fdc021c03382db4e30435f2349a96a3fdcc7545f.
Baseline SHA256 a94c393f45b9f617e99cec2fbf6f0a6d33c2d91dde2122381872465761549f3a.
Fresh candidate SHA256 bc0525c2279b0d73f2e332587c7f4487fe01a47155f8896a772eed9e46382dcf.
Full fresh exact-source runner passed with --allow-same-version (intermediate41→41, not final2.34 publication); report /private/tmp/wacken-upgrade-220-exact-evidence. Old seedPASS; old verifyFAIL with expected Room12 assertion; updateSuccess; candidate verifyPASS; runner exit0. git diff --check passes.

### Manual validation

The signed probe executes real migration and normal offline MainActivity startup on isolated API36 AVD with fresh exact-userdata.img under /private/tmp/wacken-upgrade-220. No user/original emulator data or production account was used. Follow release-process to reproduce on another empty emulator;214 must repeat with final higher-code candidate. No production network write is part of the fixture probe.

## TDD / BDD / approval-test evidence

The continuous4/5/3 lifecycle scenario is an executable business acceptance test through real authenticated HTTP. Safety tests reject physical/booting/existing targets and mismatched/downgrade/same-code candidates before install. Actual old-version negative control failed on Room9; the exact same verification passed after signed update. Existing native68/Room/SQL characterization remains intact. Initial AGP manifest registration was diagnosed and replaced by opt-in test runner selection; CI red for unavailable rg led to portable POSIX grep, then green Linux CI. Early cold boot/SystemUI delays were diagnosed; no failure was claimed as a pass.

## Architecture impact

Architecture-significant change: no production schema/API/policy/dependency change; explicit test-only instrumentation over the existing Android mechanism validates approved ADR0016/220 scope. Normal production APK and native runner boundary remain unchanged.
Approval received: user-approved ADR0016 lifecycle and its task220 validation scope.
ADR impact: none, because no new production architectural decision supersedes ADR0016; validation evidence is linked in README/report.

## README impact

README impact: updated validation status and signed probe/evidence links.

## Business requirements impact

Business requirements impact: updated implemented validation status; product rules unchanged.

## Diagram impact

Diagram impact: none, because test-only validation does not change production containers, ports or domain ownership.

## Commits / logical change list

e80348a: continuous API lifecycle, opt-in probe, safety runner/CI, documentation.
10914a0: portable upgrade script after Linux CI caught missing ripgrep.
This closure records exact-source validation evidence.

## Risks and follow-up

Intermediate candidate is2.33.5/code41, solely migration proof. Final2.34/higher-code upgrade and production V014–V018 deployment/publication remain214. Fixture session tests offline cached startup; real authenticated sync ownership/faults are exercised separately by disposable PostgREST/RLS gates. Published baseline's original lost-key installation line is outside this stable-key compatible upgrade. Probe is only a test APK and is never a release asset. Local reports are retained in/tmp; durable summary and public CI identifiers are in the tracked validation report.
<!-- SECTION:NOTES:END -->

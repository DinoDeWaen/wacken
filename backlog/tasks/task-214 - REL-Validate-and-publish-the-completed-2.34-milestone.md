---
id: TASK-214
title: 'REL: Validate and publish the completed 2.34 milestone'
status: Done
assignee:
  - '@codex'
created_date: '2026-10-17 09:59'
updated_date: '2026-10-17 20:36'
labels:
  - release
  - android
  - release-2.34
milestone: m-0
dependencies:
  - TASK-156
  - TASK-169
  - TASK-172
  - TASK-174
  - TASK-175
  - TASK-201
  - TASK-202
  - TASK-215
  - TASK-216
  - TASK-217
  - TASK-218
  - TASK-219
  - TASK-220
  - TASK-221
  - TASK-222
  - TASK-223
modified_files:
  - app/build.gradle
  - releases/v2.34.md
  - README.md
  - backlog/docs/business-requirements.md
  - backlog/docs/release-2.34-deployment.md
  - backlog/decisions/0016-band-catalog-and-archive-promoted-personal-ratings.md
  - backlog/docs/release-2.34-publication-validation.md
priority: high
type: task
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As the release owner, I want a signed, regression-validated 2.34 package so users can safely install the completed milestone. Scope: finish or explicitly resolve every m-0 task, retain the published 2.33.5 festival/unknown-link fixes, execute required unit/BDD/API/native gates, version the Android app, verify stable signing and upgrade/data preservation, publish release notes/tag and the signed artifact through the selected distribution path. The validated appearance redesign is planned for 3.0 and must not be described as shipped in 2.34. No scope item may be silently dropped to claim milestone completion.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given the release 2.34 milestone, when packaging starts, then each feature/test/distribution task is Done or has an explicit user-approved scope resolution with evidence.
- [x] #2 Given the final source, then unit/QA/database integration and native UI checks run against that state and pass; skipped gates are resolved or explicitly accepted before publication.
- [x] #3 Given an existing supported installation, then the signed update preserves ratings/history, festival identity, metadata, auth and pending work; package identity and certificate are verified.
- [x] #4 Given the signed artifact, then versionName 2.34, an incremented versionCode, Git tag, checksum, release notes and published APK all refer to the same validated commit.
- [x] #5 README, business requirements, distribution documentation and the complete delivery-governance validation package accurately describe shipped features, remaining limitations and the separate 3.0 design roadmap.
- [x] #6 Given ADR-0016 is the approved lifecycle, then TASK-216–220 are complete and the release demonstrates band4 → planning4 → planning5/after3 → archive band3 → next planning3, including source-aware corrections and legacy/pending-data preservation.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Completed all original2.34 dependencies; set version2.34/code42 and reconciled README/BR/ADR status/release notes. Backed up public DB privately, deployed previously approvedV014–V018 through existing Flyway, validated18/schema018 and unchanged7-table hashes plus RLS/invoker grants/zero invalid sources; documented pre-existing advisors separately224. Exact-source full CI5b27bd2 green twice, including68 native per API23/API36 and real creation16/lifecycle SQL/API. Reused clean existing task-owned worktree at detached5b27, configured SDK through environment and produced fresh stable-signed finalAPK/probe. Strict actual2.33.5→2.34/code42 upgrade on empty isolated image passed with old-version negative control/no wipe. Tagged/pushedv2.34 at5b27, published official APK+SHA256SUMS, downloaded and verified digest/cert/metadata and release target. Final publication/rollout/lifecycle reports and complete validation package retained; close milestone and merge via PR3. Deviations: isolated clean worktree avoids unrelated root edits; AGP lacks embedded worktreeGit metadata, so explicit clean git revision/tag/artifact hashes bind provenance. No additional product scope/keys/services; native styling remains3.0.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Published V2.34/code42 as an official stable-signed GitHub APK, completing the2.34 milestone. Source/tag/release target5b27bd2853d24e75e0d3c006974e49214bc4c474, tagv2.34. Includes all approved catalog/personal-vs-festival rating architecture, creation/archive/correction, aliases, reviewed local CSV, verified music links, contextual exports and API23 compatibility. Native paired appearance remains3.0.

Release: https://github.com/DinoDeWaen/wacken/releases/tag/v2.34
APK: https://github.com/DinoDeWaen/wacken/releases/download/v2.34/app-release.apk
Checksum: https://github.com/DinoDeWaen/wacken/releases/download/v2.34/SHA256SUMS

## Acceptance criteria validation

1. Every2.34 feature/test/distribution dependency is Done; no scope item moved out to claim completion. Existing security advisories are a separate baseline follow-up224.
2. Exact release-source CI39063985464/39063981652 pass all jobs: full JVM/QA/coverage/full lint, safety, real creation16/no skips, SQL/PostgREST/RLS/lifecycle and68 native each API23/API36. No required database/native gate waived.
3. Actual published stable-key2.33.5/code41/Room9→final2.34/code42/Room12 upgrade passed without uninstall/wipe on an empty emulator image. Auth, canonical metadata/links, active/archive identity, dated/unknown/pending history, own/other planning, legacy pending, schedule/private file and cached offline startup retained; no active/ambiguous promotion.
4. Version2.34/code42/min23/target36, stable v1/v2 cert, remote tag/release target and downloaded APK all verified. Published APK digest equals validated clean-worktree artifact and SHA256SUMS.
5. README/BR/ADR0016 status, release notes, production rollout and publication validation reports accurately describe shipped scope, limits and separate3.0 roadmap; signed GitHub distribution unchanged.
6. TASK216–220 Done and continuous4→4→5/3→3→3 plus source-aware corrections/replay/reset/owner/pending/legacy validations pass.

## How to test

### Automated tests

Final-source CI: https://github.com/DinoDeWaen/wacken/actions/runs/39063985464 (duplicate39063981652 green).
Clean JDK21+Android SDK signed build in existing clean task-owned worktree at detached5b27bd2: ./gradlew :app:clean test qaTest :app:lintDebug :app:lintVitalRelease :app:assembleRelease :app:assembleDebugAndroidTest -PwackenUpgradeProbe passed. No unrelated root edits were included.
Final strict signed upgrade runner exit0, versionCode41→42; old schema negative control failed as expected, candidate passed. Reports /private/tmp/wacken-214-upgrade-evidence.
Final APK SHA256 e03c6e956af13fd6155537d84e74e6368ae902bc2da90263f28652448b112c4a.
Certificate SHA256 ede2ae97735a305900e87bc0fdc021c03382db4e30435f2349a96a3fdcc7545f; packagebe.wacken.planner.
Downloaded GitHub assets to/private/tmp/wacken-2.34-published; shasum -a256 -c SHA256SUMS returnsOK; downloaded apksigner/aapt metadata matches. Remote v2.34 resolves5b27bd2; release target matches.
Production existing Flyway:5 approved migrations applied013→018,18 valid. Before/after7 table hashes/counts identical, zero invalid established sources. New tables RLS/no direct authenticated writes; lifecycle public wrappers invoker/authenticated-only. Public schema backup/private/tmp/wacken-214-before.dump mode600 is not published. Existing advisor warnings/error unchanged; two new internal-ledger deny-by-default INFOs are intentional. No production fixtures/reset.
git diff --check passes.

### Manual validation

Actual signed fixture migration and normal offline MainActivity launch tested on isolated API36; original/user emulator data untouched. Reproduce using release-process.md signed upgrade commands with final candidate and no --allow-same-version. APK update should retain local data; never uninstall/clear storage to force a signature mismatch. Physical external YouTube/Spotify app behavior is not observed by emulator gates.

## TDD / BDD / approval-test evidence

Feature tasks retain their red/green regressions. This release reruns their complete required gates, including actual SummerBreeze63-band creation, rollback/auth/retry, metadata nulls, review/rotation, lifecycle4/5/3, correction source protection and native migration. Signed probe old-version FAIL→updated candidatePASS validates upgrade assertions. No production behavior was added during packaging.

## Architecture impact

Architecture-significant change: deploy previously approved/tested ADR0016 migrations14–18 via existing Flyway; existing package, SDK support, signing, transport and distribution boundaries retained.
Approval received: user's explicit architecture adoption/all-stories instruction and repeated release/continue authorization; signed GitHub channel selected explicitly.
ADR impact: updated ADR0016 implemented/validated status; no new architectural policy.
Production security baseline follow-up224 requires separate approval before hardening; it was not silently implemented in this release.

## README impact

README impact: updated current2.34 architecture, release link, implemented status, migration rollout and validation links.

## Business requirements impact

Business requirements impact: updated implemented status and remaining legacy scenarios from latest-event prefill to established archive-sourced once-only copies; no new product scope.

## Diagram impact

Diagram impact: none for packaging, because existing canonical C4/application/cache boundaries already updated by feature tasks remain accurate.

## Commits / logical change list

ca36393: version2.34/code42, release notes, database rollout/evidence and baseline follow-up224.
5b27bd2: reconcile remaining BR scenarios; exact validated and tagged release source.
Closure adds publication evidence and task completion only. Main integration proceeds through existing PR3 after the validation package.

## Risks and follow-up

Cached exports may not include unsent work from another device. Ambiguous legacy outcomes require review; no offline archived-correction queue. Old status-only archive clients must update to2.34; guarded atomic API protects promotion. Existing lost older-key installations cannot update across that key boundary; current stable-key releases upgrade normally. Pre-existing Supabase advisory findings tracked224, no new warning/error introduced. AGP managed-worktree metadata does not embed Git; explicit clean-tree/revision plus tag/digest verification establish provenance externally. Probe/backup/debug APK are not release assets.3.0 appearance work remains204–213.
<!-- SECTION:NOTES:END -->

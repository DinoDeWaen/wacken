---
id: TASK-188
title: 'REL: Publish V2.33 signed Android release'
status: Done
assignee:
  - '@codex'
created_date: '2026-10-15 18:03'
updated_date: '2026-10-15 18:41'
labels:
  - release
  - android
milestone: m-1
dependencies:
  - TASK-185
  - TASK-186
  - TASK-187
references:
  - wacken
modified_files:
  - app/build.gradle
  - releases/v2.33.md
  - README.md
priority: high
type: task
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Business value: Ship the completed Release 2.33 fixes as a verifiable, installable Android APK for the group.

Scope: After TASK-185, TASK-186, and TASK-187 are Done or explicitly accepted, bump Android metadata to 2.33, write release notes, run the documented validation build with the existing signing key, verify the APK, publish the v2.33 Git tag and GitHub release asset, and record evidence.

Out of scope: Play Store distribution, key rotation, unrelated features, and publishing an unsigned APK.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given the completed Release 2.33 defect tasks, when release packaging starts, then Android versionCode is 36 and versionName is 2.33.
- [x] #2 Release notes describe the three fixes, known risks, install guidance, and validation, and README links to them.
- [x] #3 Full documented release validation and a signed assembleRelease build pass; the APK has verified v1/v2 signatures, package/version/minSdk metadata, and a recorded SHA-256.
- [x] #4 Tag v2.33 is pushed and the GitHub release is published with the verified app-release.apk asset; the release URL and asset are checked.
- [x] #5 Unrelated pre-existing worktree changes are excluded from release commits and artifact provenance.
- [x] #6 README, business requirements, diagram, and ADR impact are recorded using canonical delivery-governance wording before task completion.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Treatment: standard release packaging; no architecture-significant release-only change. TASK-185, TASK-186, and approved TASK-187 were Done before packaging.
Completed: committed the three defect tasks and Release 2.33 milestone; used an isolated clean managed worktree; set versionCode 36/versionName 2.33; wrote releases/v2.33.md and README link; verified the existing stable release keystore; ran clean full Gradle tests, debug compilation, release lintVital, and signed assembleRelease with ANDROID_HOME/JDK21/Keychain values; verified APK v1/v2 signature, package/version/minSdk, SHA-256, and git diff --check; excluded Backlog CLI's generated config rewrite; pushed publication branch and v2.33 tag; published and verified GitHub release and exact asset digest.
Files: app/build.gradle, releases/v2.33.md, README.md, TASK-188. Milestone m-1 was committed before the clean worktree.
Deviation: clean checkout required ANDROID_HOME because local.properties is gitignored. Tag points to the verified release commit; final task-closure evidence is committed after publication. No new architecture approval, business requirement, diagram, or ADR update for release packaging. Risks: missing old key requires reinstall on older installations; full lintDebug has pre-existing NewApi findings; no device walkthrough.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

- Published Wacken Planner 2026 V2.33 as a signed direct-install Android release containing completed TASK-185, TASK-186, and TASK-187.
- Android metadata is versionCode 36 and versionName 2.33. releases/v2.33.md covers scope, installation, validation, risks, and digest; README links it.
- The release was built from a clean isolated worktree and the published APK matches the locally verified digest.

## Acceptance criteria validation

- AC1: aapt confirms package be.wacken.planner, versionCode 36, versionName 2.33, minSdkVersion 23.
- AC2: release notes cover the three fixes, signing/install guidance, validation, known risks, and non-goals; README links them.
- AC3: clean full validation and signed assembleRelease passed; apksigner verifies v1/v2 with one signer; SHA-256 is 3f445e9c7f2f68452b96371d8ea6db17743fbbf9944496624d76c3a17f4a9b43.
- AC4: v2.33 tag was pushed at 1d633820c11297c8f23d76c90e55bbbad92b775f. Published release: https://github.com/DinoDeWaen/wacken/releases/tag/v2.33 . Verified asset: https://github.com/DinoDeWaen/wacken/releases/download/v2.33/app-release.apk . GitHub reports the same sha256 digest, uploaded state, and non-draft/non-prerelease status.
- AC5: the release worktree was clean before packaging; release commits contained only the three defect tasks, milestone, metadata, notes, README, and task evidence. Original checkout's unrelated edits were excluded. Backlog CLI's automatic config rewrite was restored before tagging.
- AC6: impact notes are below.

## How to test

### Automated tests

- scripts/ensure-release-keystore.sh verified the existing stable keystore.
- ANDROID_HOME=/Users/dino/Library/Android/sdk with JDK 21 and local Keychain signing values: ./gradlew clean :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac :app:lintVitalRelease assembleRelease --no-daemon — passed.
- apksigner verify --verbose: v1=true, v2=true, one signer. aapt dump badging: be.wacken.planner, 36, 2.33, minSdk 23. shasum -a 256: 3f445e9c7f2f68452b96371d8ea6db17743fbbf9944496624d76c3a17f4a9b43. git diff --check passed.
- gh release view v2.33 verified release URL, app-release.apk name, uploaded state, and server digest matching the local APK. git rev-parse HEAD and v2.33 both returned 1d633820c11297c8f23d76c90e55bbbad92b775f at publication.

### Manual validation

- No physical-device install or import walkthrough was run. The signed APK, version metadata, and GitHub download asset were verified.

## TDD / BDD / approval-test evidence

- This task changes packaging metadata and documentation only. The three defect tasks contain their red/green and characterization evidence. TASK-187 received explicit architecture approval before implementation.

## Architecture impact

- Architecture-significant change: no release-only architecture change.
- Approval received: no new approval required; TASK-187 approval is recorded there.
- ADR impact: none, because TASK-187 already updated ADR 0007 for the included persistence boundary.

## README impact

README impact: updated the release-note link for V2.33.

## Business requirements impact

Business requirements impact: none, because release packaging adds no new behavior; TASK-187 already updated Workflow 1 and BR-042a.

## Diagram impact

Diagram impact: none, because release packaging changes no runtime or module boundary.

## Commits / logical change list

- b84b5b1 fixed missing band detail; e91739a hardened CSV parser/import transaction; f871a2b added the milestone and release task; c09630b prepared version metadata/notes; 1d63382 recorded release provenance and is tagged v2.33.
- Publication branch codex/release-2-33-publish was pushed. Final task-closure evidence is committed after publication.

## Risks and follow-up

- Installations signed by the earlier lost key need one uninstall/reinstall; local-only data can be lost, so export ratings first when needed.
- Full lintDebug retains 14 pre-existing NewApi findings in untouched files; lintVitalRelease passes.
- The TSV import handles reported failures, not process or power-loss recovery. No device walkthrough was run.
- The first clean-worktree build stopped before compilation because Android SDK location was unset; setting ANDROID_HOME resolved it and the clean validation passed.
<!-- SECTION:NOTES:END -->

## Final Summary

<!-- SECTION:FINAL_SUMMARY:BEGIN -->
Published signed Wacken Planner V2.33 at https://github.com/DinoDeWaen/wacken/releases/tag/v2.33 with verified app-release.apk (SHA-256 3f445e9c7f2f68452b96371d8ea6db17743fbbf9944496624d76c3a17f4a9b43).
<!-- SECTION:FINAL_SUMMARY:END -->

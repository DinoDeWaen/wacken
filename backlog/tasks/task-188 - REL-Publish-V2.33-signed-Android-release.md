---
id: TASK-188
title: 'REL: Publish V2.33 signed Android release'
status: In Progress
assignee:
  - '@codex'
created_date: '2026-10-15 18:03'
updated_date: '2026-10-15 18:32'
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
- [ ] #4 Tag v2.33 is pushed and the GitHub release is published with the verified app-release.apk asset; the release URL and asset are checked.
- [ ] #5 Unrelated pre-existing worktree changes are excluded from release commits and artifact provenance.
- [x] #6 README, business requirements, diagram, and ADR impact are recorded using canonical delivery-governance wording before task completion.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Treatment: standard release packaging; no architecture-significant change. TASK-185, TASK-186, and approved TASK-187 are Done.
Completed so far: committed the three defect tasks and Release 2.33 milestone; created an isolated clean worktree from those commits; set Android versionCode 36/versionName 2.33, wrote releases/v2.33.md, and linked it in README. Verified the existing stable signing key. A first Gradle attempt lacked ANDROID_HOME because this checkout has no local.properties; reran with the installed SDK path and passed a clean full test, release lintVital, and signed assembleRelease build. Verified v1/v2 signatures, package/version/minSdk, SHA-256, and git diff --check. The release notes contain the final measured digest.
Remaining: commit release metadata/notes and current task evidence, push the publication branch, create/push v2.33, publish and verify the GitHub release asset, then close TASK-188 and push final task evidence.
Files: app/build.gradle, releases/v2.33.md, README.md, TASK-188. The milestone was committed before the clean worktree. Release-only business requirements, diagrams, and ADR changes are unnecessary; TASK-187 already updated the affected architecture/business sources. No new approval required. Risks: old missing-key installations need uninstall/reinstall; full lintDebug has pre-existing NewApi findings; no device walkthrough.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

- Prepared Wacken Planner 2026 V2.33 with Android versionCode 36 and versionName 2.33 after TASK-185, TASK-186, and TASK-187 were completed.
- Added releases/v2.33.md with scope, installation guidance, validation, non-goals, risks, and the verified APK digest. README links the notes.
- Built the signed APK in an isolated release worktree so pre-existing dirty files in the original checkout cannot enter the artifact.

## Acceptance criteria validation

- AC1: aapt confirms package be.wacken.planner, versionCode 36, versionName 2.33, minSdkVersion 23.
- AC2: release notes cover all three fixes, signing/install guidance, validation, and risks; README links them.
- AC3: clean full validation and signed assembleRelease passed; apksigner verifies v1/v2, one signer; SHA-256 is 3f445e9c7f2f68452b96371d8ea6db17743fbbf9944496624d76c3a17f4a9b43.
- AC4: publication pending in this pre-tag validation snapshot.
- AC5: release checkout began clean from the three committed defect fixes and milestone. Backlog CLI rewrites backlog/config.yml locally; it is excluded from release commits and restored before tag/publish.
- AC6: impact notes are below.

## How to test

### Automated tests

- Verified scripts/ensure-release-keystore.sh against the existing stable keystore in the original checkout.
- ANDROID_HOME=/Users/dino/Library/Android/sdk with JDK 21 and Keychain signing values: ./gradlew clean :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac :app:lintVitalRelease assembleRelease --no-daemon — passed.
- apksigner verify --verbose: v1=true, v2=true, one signer. aapt dump badging: be.wacken.planner, 36, 2.33, minSdk 23. shasum -a 256: 3f445e9c7f2f68452b96371d8ea6db17743fbbf9944496624d76c3a17f4a9b43. git diff --check passed.

### Manual validation

- No physical-device install or import walkthrough was run. The signed APK is built and metadata-verified; publication verification remains.

## TDD / BDD / approval-test evidence

- This task changes packaging metadata and documentation only. The three underlying defect tasks include their own red/green and characterization evidence. TASK-187 had explicit architecture approval.

## Architecture impact

- Architecture-significant change: no release-only architecture change.
- Approval received: no new approval required; TASK-187 approval is recorded in that task.
- ADR impact: none, because TASK-187 already updated ADR 0007 for the included persistence change.

## README impact

README impact: updated the release-note link for V2.33.

## Business requirements impact

Business requirements impact: none, because release packaging adds no new behavior; TASK-187 already updated Workflow 1 and BR-042a.

## Diagram impact

Diagram impact: none, because release packaging changes no runtime or module boundary.

## Commits / logical change list

- Defect commits b84b5b1 and e91739a plus milestone setup f871a2b precede the release metadata commit.
- Release metadata commit, tag, and GitHub release to be recorded after publication.

## Risks and follow-up

- The original lost signing key cannot update older installations; those devices need one uninstall/reinstall and may lose local-only data.
- Full lintDebug still has 14 pre-existing NewApi findings in untouched files; lintVitalRelease passes.
- The TSV import design covers reported failures, not process or power-loss recovery. No device walkthrough was run.
- The first clean-worktree build stopped before compilation because Android SDK location was unset; setting ANDROID_HOME resolved it and the clean validation passed.
<!-- SECTION:NOTES:END -->

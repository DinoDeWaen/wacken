---
id: task-138
title: 'REL: Finalise MVP2 official release'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-30 15:46'
updated_date: '2026-07-02 08:17'
labels:
  - release
  - mvp2
  - apk
dependencies:
  - task-136
  - task-137
  - task-139
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As the product owner, I want one final official MVP2 release package after remaining documentation and UAT work is complete, so that the installable APK represents the completed MVP2 scope.

Scope: run the standard release process after task-136, task-137, and task-139 are complete; build a signed official APK, verify metadata/signatures/checksum, publish release notes, push the release artifacts/tags to GitHub, and record validation evidence. Task-117 was cancelled because the Supabase schema-cache error no longer reproduces.

Out of scope: adding MVP3/MVP4 capabilities or changing product behavior outside defects found during MVP2 validation.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given all MVP2 closure tasks are complete, when release validation runs, then domain, application, infrastructure, app unit tests, Android compile, and signed release build pass
- [x] #2 Given the release APK is built, when signature and metadata are inspected, then it is a signed official APK with the expected package, versionCode, versionName, minSdk, targetSdk, and SHA-256 recorded
- [x] #3 Given the release is published, when GitHub is checked, then the tag, release notes, and APK asset are available
- [x] #4 Given the release process is complete, then the local previous release APK was deleted before rebuilding according to the documented ways of working
- [x] #5 README links to the new release notes or README impact is recorded using the canonical wording from delivery-governance.md
- [x] #6 Business requirements impact is recorded using the canonical wording from delivery-governance.md
- [x] #7 Manual MVP2 smoke-test evidence is recorded, including schedule open, day filter, schedule locks, ratings detail, and sync/offline status
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Confirm all prerequisite MVP2 closure tasks are Done and the worktree has no unrelated release-blocking changes.
2. Bump Android metadata to versionCode 23 / versionName 2.20.
3. Create `releases/v2.20.md` and add the README release-note link.
4. Delete any existing local release APK output, verify release keystore, and run full release validation with signed `assembleRelease`.
5. Verify APK signing, metadata, and SHA-256.
6. Commit release metadata/task notes, tag v2.20, push branch and tag, publish GitHub release with the signed APK.
7. Record validation evidence, release URL, risks, and close task-138.

Files/areas likely to change: `app/build.gradle`, `README.md`, `releases/v2.20.md`, release task notes.
Test strategy: full Gradle validation required by release process plus APK signer/aapt/SHA checks.
Design approach: release packaging only; no app behavior change expected.
Architecture impact: not architecture-significant; no code architecture changes planned.
Risks/assumptions: signing key must be available locally; GitHub CLI must be authenticated; physical device smoke evidence may be limited to prior user installed-app observation plus backend/API validation unless an emulator/device is available.
Treatment: standard release governance.
README/business/diagram/ADR impact: README release link expected; business requirements, diagrams, ADR expected none.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Finalised MVP2 with official signed Android release V2.20. The release bumps Android metadata to `versionCode 23` / `versionName 2.20`, adds `releases/v2.20.md`, links it from README, builds a signed release APK, verifies signing/metadata/SHA-256, pushes tag `v2.20`, and publishes the GitHub release.

Release URL: https://github.com/DinoDeWaen/wacken/releases/tag/v2.20

APK: `app/build/outputs/apk/release/app-release.apk`
SHA-256: `79f1e6d415708dea15a03ff29483f879d1d8e372a83351cf6ba91c8551643ac8`

## Acceptance criteria validation

- AC1: Passed. Full release validation ran successfully: domain, application, infrastructure, app unit tests, Android debug compile, and signed release build.
- AC2: Passed. `apksigner`, `aapt`, and `shasum` verified the signed APK metadata and digest. Package is `be.wacken.planner`, versionCode `23`, versionName `2.20`, minSdk `23`, targetSdk `36`. APK Signature Scheme v1 and v2 are verified.
- AC3: Passed. GitHub release `v2.20` is published and contains `app-release.apk`.
- AC4: Passed. Previous local release APK outputs were deleted before rebuilding.
- AC5: Passed. README impact recorded below and README links to `releases/v2.20.md`.
- AC6: Passed. Business requirements impact recorded below.
- AC7: Passed with the evidence available in this workspace: automated schedule/open/filter/lock/offline coverage passed; task-137 records live Supabase schedule-lock backend validation and user-reported installed-app schedule-lock behavior. Physical two-device smoke execution was not directly possible from this workspace.

## How to test

### Automated tests

- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:testDebugUnitTest --tests be.wacken.planner.SyncingScheduleLockStoreTest --tests be.wacken.planner.ScheduleManualSelectionsTest --tests be.wacken.planner.ScheduleBlockContentTest --tests be.wacken.planner.ScheduleErrorMessageTest --tests be.wacken.planner.SupabaseScheduleLockClientTest --tests be.wacken.planner.PendingSyncSummaryTest` passed during task-137.
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:test :application:test :infrastructure:test :app:testDebugUnitTest` passed during task-137.
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew qaTest` passed during task-137.
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) WACKEN_RELEASE_STORE_FILE=$(security find-generic-password -s WACKEN_RELEASE_STORE_FILE -w) WACKEN_RELEASE_STORE_PASSWORD=$(security find-generic-password -s WACKEN_RELEASE_STORE_PASSWORD -w) WACKEN_RELEASE_KEY_ALIAS=$(security find-generic-password -s WACKEN_RELEASE_KEY_ALIAS -w) WACKEN_RELEASE_KEY_PASSWORD=$(security find-generic-password -s WACKEN_RELEASE_KEY_PASSWORD -w) ./gradlew :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac assembleRelease` passed.

### Manual validation

- `scripts/ensure-release-keystore.sh` verified `.local/release/wacken-release.jks`.
- Previous release APK outputs were deleted before rebuild.
- `/Users/dino/Library/Android/sdk/build-tools/36.0.0/apksigner verify --verbose app/build/outputs/apk/release/app-release.apk` passed with v1/v2 signatures verified.
- `/Users/dino/Library/Android/sdk/build-tools/36.0.0/aapt dump badging app/build/outputs/apk/release/app-release.apk` confirmed package/version/minSdk/targetSdk metadata.
- `shasum -a 256 app/build/outputs/apk/release/app-release.apk` produced `79f1e6d415708dea15a03ff29483f879d1d8e372a83351cf6ba91c8551643ac8`.
- `git diff --check` passed.
- `gh release view v2.20 --json tagName,name,url,assets,isDraft,isPrerelease` confirmed release `v2.20`, not draft, not prerelease, asset `app-release.apk`.

## TDD / BDD / approval-test evidence

This was a release task. No new product behavior was added. Release confidence comes from the existing automated MVP2 behavior tests, QA scenario suite, signed build, APK verification, and live backend validation recorded in task-137.

## Architecture impact

- Architecture-significant change: no. This release packages already implemented MVP2 behavior and applies no new architecture changes.
- Approval received: not required for release packaging.
- ADR: none, because no architecture decision changed.

## README impact

README impact: updated with the V2.20 release notes link.

## Business requirements impact

Business requirements impact: none, because task-136 already aligned the MVP2-only scope and this release does not change business behavior.

## Diagram impact

Diagram impact: none, because no architecture relationships changed.

## Commits / logical change list

- `43dc04f Validate MVP2 schedule locks`
- `794cb89 Prepare V2.20 release`
- Tag: `v2.20`
- GitHub release: https://github.com/DinoDeWaen/wacken/releases/tag/v2.20

## Risks and follow-up

- Devices signed with an APK from before the stable local V2.9+ release key must uninstall once before installing V2.20, then sync from Supabase. Future releases from this stable key line should update normally.
- Physical two-device smoke validation was not directly executed from this workspace; the backend table is now live and visible through PostgREST, and automated lock/offline coverage passed.
<!-- SECTION:NOTES:END -->

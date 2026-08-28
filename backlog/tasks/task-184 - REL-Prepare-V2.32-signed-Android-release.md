---
id: task-184
title: 'REL: Prepare V2.32 signed Android release'
status: In Progress
assignee:
  - '@codex'
created_date: '2026-08-28 08:08'
updated_date: '2026-08-28 08:11'
labels:
  - release
  - android
dependencies:
  - task-182
  - task-183
priority: high
---

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Android version metadata is bumped to versionCode 35 and versionName 2.32.
- [x] #2 V2.32 release notes are created and linked from README.
- [x] #3 Full release validation and signed assembleRelease build pass with release signing configured.
- [x] #4 Signed APK metadata, signature, and SHA-256 are verified and recorded.
- [ ] #5 Git tag v2.32 is pushed and GitHub release is published with app-release.apk.
- [x] #6 README, business requirements, diagram, and ADR impact are recorded in task notes.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Confirm release inputs, current version, latest tag, and unreleased commits.
2. Bump Android version metadata to 2.32/35.
3. Create V2.32 release notes and add the README release link.
4. Verify release signing setup and run full validation plus assembleRelease.
5. Verify APK signature, badging metadata, SHA-256, and git diff hygiene.
6. Commit release metadata, push branch, tag v2.32, push tag, and publish GitHub release.
7. Record validation evidence, release URL, and impact notes, then close the release task.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Release preparation

- Bumped Android metadata to `versionCode 35` and `versionName 2.32`.
- Created `releases/v2.32.md` and linked it from README.
- Verified stable release keystore with `scripts/ensure-release-keystore.sh`.
- Built signed release APK at `app/build/outputs/apk/release/app-release.apk`.

## Validation so far

- `JAVA_HOME=$(/usr/libexec/java_home -v 21) WACKEN_RELEASE_STORE_FILE=$(security find-generic-password -s WACKEN_RELEASE_STORE_FILE -w) WACKEN_RELEASE_STORE_PASSWORD=$(security find-generic-password -s WACKEN_RELEASE_STORE_PASSWORD -w) WACKEN_RELEASE_KEY_ALIAS=$(security find-generic-password -s WACKEN_RELEASE_KEY_ALIAS -w) WACKEN_RELEASE_KEY_PASSWORD=$(security find-generic-password -s WACKEN_RELEASE_KEY_PASSWORD -w) MUSICBRAINZ_USER_AGENT="WackenPlanner/2.32 ( local-maintainer )" ./gradlew clean :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac assembleDebug assembleRelease` - passed.
- `/Users/dino/Library/Android/sdk/build-tools/36.0.0/apksigner verify --verbose app/build/outputs/apk/release/app-release.apk` - passed; v1 and v2 signatures verified with one signer.
- `/Users/dino/Library/Android/sdk/build-tools/36.0.0/aapt dump badging app/build/outputs/apk/release/app-release.apk` - package `be.wacken.planner`, versionCode `35`, versionName `2.32`, minSdk `23`, targetSdk `36`.
- `shasum -a 256 app/build/outputs/apk/release/app-release.apk` - `ad6c692ab6fc23fe319734ef9e6f27487788d73fcb7605a9d5231e5d38004d9c`.
- `git diff --check` - passed.

README impact: updated README with the V2.32 release notes link and current MusicBrainz User-Agent example.
Business requirements impact: none for release packaging, because task-182 and task-183 already recorded the product behavior requirement changes being released.
Diagram impact: none, because release packaging does not change architecture or runtime relationships.
ADR impact: none, because release packaging does not change architecture decisions.
<!-- SECTION:NOTES:END -->

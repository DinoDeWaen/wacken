---
id: task-181
title: 'REL: Create V2.31 official release APK'
status: Done
assignee:
  - '@codex'
created_date: '2026-08-24 16:12'
updated_date: '2026-08-25 18:30'
labels:
  - release
  - android
  - uat
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Business value: users need the completed reviewed external metadata provider set delivered as a signed installable Android APK, with a clear user acceptance test overview for manual release acceptance.

Scope: bump Android metadata to V2.31, add a V2.31 user acceptance test overview, create release notes, run full release validation, build and verify the signed release APK, publish git tag and GitHub release, and record validation evidence.

Out of scope: Play Store distribution, Supabase schema changes, automatic unreviewed metadata saves, storing production metadata-provider secrets in the repository, and new feature work beyond the completed metadata-provider stories.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given the release build runs, then domain, application, infrastructure, Android unit tests, Android compile, debug build, and signed release assembly pass.
- [x] #2 Given the release APK is built, then signing, package metadata, versionCode 34, versionName 2.31, minSdk, targetSdk, and SHA-256 are verified and recorded.
- [x] #3 Given release notes are created, then README links V2.31 above older releases and the notes document scope, APK path, build command, metadata, signing, install guidance, validation, user acceptance test overview, non-goals, and risks.
- [x] #4 Given the user acceptance overview is added, then it covers Settings metadata enrichment, own-catalog-first behavior, reviewed external provider proposals, optional provider configuration behavior, no-overwrite behavior, and imported-band linking regression checks.
- [x] #5 Given the release is published, then git tag v2.31 and the GitHub release contain the signed app-release.apk asset.
- [x] #6 Business requirements impact, README impact, diagram impact, and ADR impact are recorded using delivery-governance wording.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Add V2.31 UAT overview documentation for reviewed band linking and metadata enrichment acceptance checks.
2. Bump Android metadata to versionCode 34 / versionName 2.31 and update default metadata-provider User-Agent version strings.
3. Create releases/v2.31.md and add README links for release notes and the UAT overview.
4. Verify release keystore, remove old release outputs, run full signed release validation/build, and verify APK signing/metadata/SHA.
5. Commit release metadata and docs, push branch, create/push v2.31 tag, publish GitHub release with the signed app-release.apk, and verify the release asset.
6. Close the release task with validation evidence and delivery-governance impact notes.
Architecture impact: release packaging and acceptance documentation only; no architecture-significant change.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Published Wacken Planner 2026 V2.31 as an official signed Android release. The release packages the completed reviewed metadata provider set: Wikidata, Wikipedia, optional Spotify, and optional YouTube, on top of the existing reviewed metadata framework and MusicBrainz provider. Added the V2.31 user acceptance test overview.

GitHub release: https://github.com/DinoDeWaen/wacken/releases/tag/v2.31
APK: app/build/outputs/apk/release/app-release.apk
Published asset: https://github.com/DinoDeWaen/wacken/releases/download/v2.31/app-release.apk
SHA-256: dbdcdf44076e14d4fb0b1fd5c012be349525060d2ebdb29dacdbad0af3a814fb

Android metadata: package be.wacken.planner, versionCode 34, versionName 2.31, minSdkVersion 23, targetSdkVersion 36.

## Acceptance criteria validation

- AC1: Full release validation passed, including domain/application/infrastructure tests, Android unit tests, debug Java compile, debug APK assembly, and signed release APK assembly.
- AC2: APK signing and metadata were verified. SHA-256 is recorded above and in releases/v2.31.md.
- AC3: releases/v2.31.md was created and README links V2.31 above older releases; release notes include scope, APK path, build command, metadata, signing, install guidance, validation, UAT overview, non-goals, and risks.
- AC4: backlog/docs/v2.31-user-acceptance-test-overview.md covers reviewed imported-band linking, own-catalog-first metadata enrichment, reviewed external proposals, optional-provider configuration, and no-overwrite checks.
- AC5: Tag v2.31 was pushed and GitHub release v2.31 contains the signed app-release.apk asset.
- AC6: README, business requirements, diagram, and ADR impact are recorded below.

## How to test

### Automated tests and release build

- scripts/ensure-release-keystore.sh
- JAVA_HOME=$(/usr/libexec/java_home -v 21) WACKEN_RELEASE_STORE_FILE=$(security find-generic-password -s WACKEN_RELEASE_STORE_FILE -w) WACKEN_RELEASE_STORE_PASSWORD=$(security find-generic-password -s WACKEN_RELEASE_STORE_PASSWORD -w) WACKEN_RELEASE_KEY_ALIAS=$(security find-generic-password -s WACKEN_RELEASE_KEY_ALIAS -w) WACKEN_RELEASE_KEY_PASSWORD=$(security find-generic-password -s WACKEN_RELEASE_KEY_PASSWORD -w) MUSICBRAINZ_USER_AGENT="WackenPlanner/2.31 ( local-maintainer )" ./gradlew clean :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac assembleDebug assembleRelease

### APK verification

- /Users/dino/Library/Android/sdk/build-tools/36.0.0/apksigner verify --verbose app/build/outputs/apk/release/app-release.apk
- /Users/dino/Library/Android/sdk/build-tools/36.0.0/aapt dump badging app/build/outputs/apk/release/app-release.apk
- shasum -a 256 app/build/outputs/apk/release/app-release.apk
- git diff --check
- gh release view v2.31 --json url,tagName,name,assets

### Manual validation

- Use backlog/docs/v2.31-user-acceptance-test-overview.md for release UAT.
- Download the GitHub release asset and install it over a same-key V2.9+ release, or uninstall first on devices signed with an older key line.

## TDD / BDD / approval-test evidence

- This release packages completed tasks with their own task-level tests and validation.
- Release validation reran the full relevant automated suite and APK verification.
- Approval tests: not used for release packaging.

## Architecture impact

- Architecture-significant change: no, this is release packaging/versioning and acceptance documentation.
- Approval received: not required beyond the user request to build the release.
- ADR: none for the release task; ADR 0012 already covers the reviewed metadata provider boundary.

## README impact

README impact: updated with the V2.31 release-notes link and V2.31 UAT overview link.

## Business requirements impact

Business requirements impact: none in this release task, because the included feature tasks already updated business requirements.

## Diagram impact

Diagram impact: none, because this release task does not change architecture diagrams.

## ADR impact

ADR impact: none in this release task, because architecture decisions were handled by included feature tasks.

## Commits / logical change list

- 5c26212 Prepare V2.31 release
- v2.31 tag pushed
- GitHub release published with app-release.apk

## Risks and follow-up

- Devices still using an APK signed before the stable V2.9+ key was introduced must uninstall once before installing V2.31.
- Optional Spotify and YouTube providers require configured credentials; without them, the workflow reports the provider as not configured and continues.
- The unrelated .idea/workspace.xml change remains unstaged and was not included in this release.
<!-- SECTION:NOTES:END -->

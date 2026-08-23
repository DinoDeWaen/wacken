---
id: task-180
title: 'REL: Create V2.30 official release APK'
status: Done
assignee:
  - '@codex'
created_date: '2026-08-23 14:31'
updated_date: '2026-08-23 15:22'
labels:
  - release
  - android
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Business value: users need the latest festival administration and reviewed metadata improvements delivered as a signed installable Android APK.

Scope: bump Android metadata to V2.30, create release notes, run full release validation, build and verify the signed release APK, publish git tag and GitHub release, and record validation evidence.

Out of scope: adding more metadata providers beyond the completed MusicBrainz provider, Play Store distribution, Supabase schema changes, and unfinished task-179 provider follow-ups.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given the release build runs, then domain, application, infrastructure, Android unit tests, Android compile, debug build, and signed release assembly pass.
- [x] #2 Given the release APK is built, then signing, package metadata, versionCode 33, versionName 2.30, minSdk, targetSdk, and SHA-256 are verified and recorded.
- [x] #3 Given release notes are created, then README links V2.30 above older releases and the notes document scope, APK path, build command, metadata, signing, install guidance, validation, non-goals, and risks.
- [x] #4 Given the release is published, then git tag v2.30 and the GitHub release contain the signed app-release.apk asset.
- [x] #5 Business requirements impact, README impact, diagram impact, and ADR impact are recorded using delivery-governance wording.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Verified release inputs: latest tag was v2.29, GitHub CLI was authenticated, stable release keystore was present, and the only unrelated local change was .idea/workspace.xml.
2. Bumped Android metadata to versionCode 33 / versionName 2.30 and updated the default MusicBrainz User-Agent version.
3. Created releases/v2.30.md and added the README release-note link.
4. Verified the release keystore, removed prior generated release APKs, and ran full signed release validation/build.
5. Verified APK signatures, package metadata, SHA-256, and diff hygiene.
6. Committed release metadata, pushed the branch, created/pushed tag v2.30, published the GitHub release with app-release.apk, and verified the uploaded asset.
7. Recorded validation evidence and closed the release task.

Architecture impact: release packaging only; no architecture change.
README impact: updated V2.30 release-note link.
Business requirements impact: none in this release task; included feature tasks already updated requirements.
Risk: .idea/workspace.xml remains as an unrelated unstaged local change and was not included.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Published Wacken Planner 2026 V2.30 as an official signed Android release. The release packages the completed festival-name, active-festival rename, reviewed imported-band linking, reviewed metadata framework, and MusicBrainz provider work.

GitHub release: https://github.com/DinoDeWaen/wacken/releases/tag/v2.30

APK: app/build/outputs/apk/release/app-release.apk
Published asset: https://github.com/DinoDeWaen/wacken/releases/download/v2.30/app-release.apk
SHA-256: 9bdd502200fc630b9db1b55ce6fe603f6c53920d3c284a64771d938ef694251c

Android metadata: package be.wacken.planner, versionCode 33, versionName 2.30, minSdkVersion 23, targetSdkVersion 36.

## Acceptance criteria validation

- AC1: Full release validation passed, including domain/application/infrastructure tests, Android unit tests, debug Java compile, debug APK assembly, and signed release APK assembly.
- AC2: APK signing and metadata were verified. SHA-256 is recorded above and in releases/v2.30.md.
- AC3: releases/v2.30.md was created and README links V2.30 above older releases.
- AC4: Tag v2.30 was pushed and GitHub release v2.30 contains the signed app-release.apk asset.
- AC5: README, business requirements, diagram, and ADR impact are recorded below.

## How to test

### Automated tests and release build

- scripts/ensure-release-keystore.sh
- rm -f app/build/outputs/apk/release/app-release.apk app/build/outputs/apk/release/app-release-unsigned.apk
- JAVA_HOME=$(/usr/libexec/java_home -v 21) WACKEN_RELEASE_STORE_FILE=$(security find-generic-password -s WACKEN_RELEASE_STORE_FILE -w) WACKEN_RELEASE_STORE_PASSWORD=$(security find-generic-password -s WACKEN_RELEASE_STORE_PASSWORD -w) WACKEN_RELEASE_KEY_ALIAS=$(security find-generic-password -s WACKEN_RELEASE_KEY_ALIAS -w) WACKEN_RELEASE_KEY_PASSWORD=$(security find-generic-password -s WACKEN_RELEASE_KEY_PASSWORD -w) MUSICBRAINZ_USER_AGENT="WackenPlanner/2.30 ( local-maintainer )" ./gradlew clean :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac assembleDebug assembleRelease

### APK verification

- /Users/dino/Library/Android/sdk/build-tools/36.0.0/apksigner verify --verbose app/build/outputs/apk/release/app-release.apk
- /Users/dino/Library/Android/sdk/build-tools/36.0.0/aapt dump badging app/build/outputs/apk/release/app-release.apk
- shasum -a 256 app/build/outputs/apk/release/app-release.apk
- git diff --check
- gh release view v2.30 --json url,tagName,name,assets

### Manual validation

- Download the GitHub release asset and install it over a same-key V2.9+ release, or uninstall first on devices signed with an older key line.

## TDD / BDD / approval-test evidence

- This release packages completed tasks with their own task-level tests and validation.
- Release validation reran the full relevant automated suite and APK verification.
- Approval tests: not used for release packaging.

## Architecture impact

- Architecture-significant change: no, this is release packaging/versioning.
- Approval received: not required beyond the user request to build the next release.
- ADR: none for the release task; ADR 0012 was already created by the metadata framework story.

## README impact

README impact: updated with the V2.30 release-notes link.

## Business requirements impact

Business requirements impact: none in this release task, because the included feature tasks already updated business requirements.

## Diagram impact

Diagram impact: none, because this release task does not change architecture diagrams.

## ADR impact

ADR impact: none in this release task, because architecture decisions were handled by included feature tasks.

## Commits / logical change list

- b84b84f Prepare V2.30 release
- v2.30 tag pushed
- GitHub release published with app-release.apk

## Risks and follow-up

- Devices still using an APK signed before the stable V2.9+ key was introduced must uninstall once before installing V2.30.
- MusicBrainz covers artist URL relationships; Wikidata, Wikipedia, Spotify, and YouTube provider stories remain open for later metadata enrichment.
- The unrelated .idea/workspace.xml change remains unstaged and was not included in this release.
<!-- SECTION:NOTES:END -->

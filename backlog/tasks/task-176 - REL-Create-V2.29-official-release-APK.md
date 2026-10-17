---
id: task-176
title: 'REL: Create V2.29 official release APK'
status: Done
assignee:
  - '@codex'
created_date: '2026-08-23 09:09'
updated_date: '2026-08-23 09:17'
labels:
  - release
  - apk
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Business value: publish the latest workspace state as an official signed Android APK release so the current app version and import data are available from GitHub.\n\nScope: bump Android metadata from V2.28 to V2.29, include the Summer Breeze 2027 band import CSV in the release commit, create release notes, update README, run release validation, verify the signed APK, push the commit and tag, publish the GitHub release, and record validation evidence.\n\nOut of scope: Play Store distribution, new app behavior, Supabase schema changes, and schedule/performance data for Summer Breeze.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given the latest releasable workspace state is packaged, when release validation runs, then domain, application, infrastructure, app unit tests, Android compile, debug build, and signed release build pass.
- [x] #2 Given the V2.29 APK is built, when signing and metadata are inspected, then it is an official signed APK with package, versionCode, versionName, minSdk, targetSdk, and SHA-256 recorded.
- [x] #3 Given the release is published, when GitHub is checked, then tag v2.29, release notes, and the signed APK asset are available.
- [x] #4 Given release packaging is complete, then README links to V2.29 release notes and release notes document scope, APK path, validation, signing, installation guidance, known non-goals, and accepted risks.
- [x] #5 Business requirements, diagram, and ADR impact are recorded using delivery-governance wording.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Confirmed V2.28 was the latest published release and selected V2.29/versionCode 32.
2. Updated app/build.gradle, README, and releases/v2.29.md.
3. Included data/summer-breeze-2027/bands.csv and SOURCE.md in the release commit.
4. Verified Summer Breeze CSV row count and unique band_id/name/source_id values.
5. Verified the stable release keystore, then ran a clean full release validation and signed release build. The first Gradle attempt was blocked by sandbox access to ~/.gradle; the same command passed with approved escalation.
6. Verified APK signing, APK metadata, SHA-256, and git diff hygiene.
7. Committed release metadata/data as 0977398, pushed the branch, created and pushed tag v2.29, published the GitHub release with the signed APK, and verified the release asset digest.
8. Closed task-176 with checked acceptance criteria and validation notes.

Architecture impact: not architecture-significant; no app behavior, schema, dependency, persistence, API, module-boundary, or signing-strategy change. Approval and ADR: not required.
Deviation: existing .idea/workspace.xml remained an unrelated unstaged local change.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Published the official V2.29 Android release. The tagged release commit includes Android version metadata for 2.29, release notes, a README release-note link, and Summer Breeze 2027 band-only import data. No app behavior changed from V2.28.

Release URL: https://github.com/DinoDeWaen/wacken/releases/tag/v2.29

GitHub release verification: tag `v2.29`, title `Wacken Planner 2026 V2.29`, not draft, not prerelease, asset `app-release.apk` uploaded with GitHub digest `sha256:e733ca01c69adb733b0ec99af6e51330f3f86f3f8c381a5ff5ec40a3f22a81be`.

## Acceptance criteria validation

- AC1: Full release validation passed: domain, application, infrastructure, app unit tests, Android compile, debug build, and signed release build.
- AC2: APK signing and metadata were inspected. Official APK path: `app/build/outputs/apk/release/app-release.apk`; package `be.wacken.planner`; versionCode `32`; versionName `2.29`; minSdk `23`; targetSdk `36`; release SHA-256 `e733ca01c69adb733b0ec99af6e51330f3f86f3f8c381a5ff5ec40a3f22a81be`.
- AC3: GitHub release is published at https://github.com/DinoDeWaen/wacken/releases/tag/v2.29 with tag `v2.29`, release notes, and uploaded signed APK asset.
- AC4: README links to `releases/v2.29.md`; release notes document scope, APK path, validation, signing, installation guidance, known non-goals, and accepted risks.
- AC5: Business requirements, diagram, and ADR impacts are recorded below using delivery-governance wording.

## How to test

### Automated tests

- `scripts/ensure-release-keystore.sh`
- Summer Breeze CSV parser/uniqueness validation: 63 rows, no duplicate `band_id`, `name`, or `source_id`.
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) WACKEN_RELEASE_STORE_FILE=$(security find-generic-password -s WACKEN_RELEASE_STORE_FILE -w) WACKEN_RELEASE_STORE_PASSWORD=$(security find-generic-password -s WACKEN_RELEASE_STORE_PASSWORD -w) WACKEN_RELEASE_KEY_ALIAS=$(security find-generic-password -s WACKEN_RELEASE_KEY_ALIAS -w) WACKEN_RELEASE_KEY_PASSWORD=$(security find-generic-password -s WACKEN_RELEASE_KEY_PASSWORD -w) ./gradlew clean :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac assembleDebug assembleRelease`
- `/Users/dino/Library/Android/sdk/build-tools/36.0.0/apksigner verify --verbose app/build/outputs/apk/release/app-release.apk`
- `/Users/dino/Library/Android/sdk/build-tools/36.0.0/aapt dump badging app/build/outputs/apk/release/app-release.apk`
- `shasum -a 256 app/build/outputs/apk/debug/app-debug.apk app/build/outputs/apk/release/app-release.apk`
- `git diff --check`
- `gh release view v2.29 --json tagName,name,url,assets,publishedAt,isDraft,isPrerelease,targetCommitish`

Debug APK SHA-256: `78fcd6023f239d9fc59f39513bc90866e9791d96358ab7ec5f6db5fd9abfab78`.
Release APK SHA-256: `e733ca01c69adb733b0ec99af6e51330f3f86f3f8c381a5ff5ec40a3f22a81be`.

APK signature verification result: verifies with v1 and v2 schemes; apksigner reported Android packaging META-INF warnings only.

APK badging metadata: package `be.wacken.planner`; versionCode `32`; versionName `2.29`; minSdkVersion `23`; targetSdkVersion `36`.

### Manual validation

No physical-device smoke test was performed for this release because V2.29 changes only release metadata, release notes, Backlog planning records, and repository CSV import data.

## TDD / BDD / approval-test evidence

No new product behavior was implemented in this release task. Existing automated tests were run through the full release validation command. No approval or characterization test was needed because no legacy refactor was performed.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR: none required; release packaging and band-only CSV data did not change architecture, module boundaries, dependencies, persistence, sync/storage boundaries, auth design, or signing strategy.

## README impact

README impact: updated README with the V2.29 release-notes link.

## Business requirements impact

Business requirements impact: none, because V2.29 only packages the existing app and adds band-only CSV data that fits the already documented CSV import path.

## Diagram impact

Diagram impact: none, because release packaging and CSV data did not change architecture, module structure, dependencies, or runtime data flow.

## ADR impact

ADR impact: none, because release packaging and CSV data did not introduce or change an architecture decision.

## Commits / logical change list

- `7c1f46c` Add future enhancement backlog stories.
- `0977398` Prepare v2.29 release.
- Tag pushed: `v2.29`.
- GitHub release published: https://github.com/DinoDeWaen/wacken/releases/tag/v2.29

## Risks and follow-up

- Summer Breeze 2027 lineup data may change after 2026-08-23; regenerate before final use.
- Summer Breeze import data is band-only, so bands remain unscheduled until performances/stages are available.
- Devices still using an APK signed before the stable V2.9+ release key must uninstall once, install V2.29, and sync from Supabase.
- Existing `.idea/workspace.xml` is an unrelated local change and was intentionally left untouched.
<!-- SECTION:NOTES:END -->

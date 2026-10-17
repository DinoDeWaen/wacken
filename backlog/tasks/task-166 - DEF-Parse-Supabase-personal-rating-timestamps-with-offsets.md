---
id: task-166
title: 'DEF: Parse Supabase personal rating timestamps with offsets'
status: Done
assignee:
  - '@codex'
created_date: '2026-08-16 16:14'
updated_date: '2026-08-16 17:45'
labels: []
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a signed-in Wacken Planner user syncing cached festival data, I want Supabase personal rating history timestamps with timezone offsets to parse correctly, so sync does not fail and cached data can become up to date.

Scope: fix Supabase personal rating event timestamp parsing for values such as 1970-01-01T00:00:00+00:00 while preserving existing Z-form timestamp support.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given Supabase returns personal_band_rating_events.created_at as 1970-01-01T00:00:00+00:00, when personal rating sync pulls events, then parsing succeeds.
- [x] #2 Given Supabase returns created_at in existing Z instant format, when personal rating sync pulls events, then parsing still succeeds.
- [x] #3 Automated regression tests cover both timestamp formats.
- [x] #4 A signed hotfix APK is built and published after validation.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Inspect the Supabase personal rating sync parser and tests for created_at handling.
2. Add regression coverage for Supabase offset timestamps like 1970-01-01T00:00:00+00:00 and existing Z timestamps.
3. Implement an offset-aware timestamp parser that preserves existing Instant.parse behavior.
4. Run targeted tests and full signed release validation.
5. Publish a signed hotfix APK after validation.
Architecture impact: not architecture-significant; this is an adapter parsing fix with no schema, API, or domain model change.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Published Wacken Planner 2026 V2.28 as a signed Android hotfix for Supabase personal rating sync failing on offset timestamps. The app error showed Supabase returned created_at as 1970-01-01T00:00:00+00:00, which the previous Instant.parse-only path rejected.

The fix adds SupabasePersonalBandRatingClient.parseCreatedAt: it preserves Instant.parse for existing Z-form timestamps and falls back to OffsetDateTime.parse(...).toInstant() for offset timestamps. Parsed values are normalized to Instant before local cache save.

Release URL: https://github.com/DinoDeWaen/wacken/releases/tag/v2.28
APK path: app/build/outputs/apk/release/app-release.apk
SHA-256: bd839c3e2816e6a2d8568e4fb8fe1f60dfd404b2c20f5946a184947cb3d6ba59

## Acceptance criteria validation

- AC1: Passed. Supabase offset timestamp 1970-01-01T00:00:00+00:00 now parses to Instant.EPOCH in personal rating event rows.
- AC2: Passed. Existing Z-form timestamps still parse through Instant.parse.
- AC3: Passed. SyncingPersonalBandRatingHistoryRepositoryTest covers offset row parsing and Z-form parsing.
- AC4: Passed. V2.28 signed APK was built, verified, tagged, pushed, and published to GitHub.

## How to test

### Automated tests

- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:testDebugUnitTest --tests be.wacken.planner.SyncingPersonalBandRatingHistoryRepositoryTest
- JAVA_HOME=$(/usr/libexec/java_home -v 21) WACKEN_RELEASE_STORE_FILE=$(security find-generic-password -s WACKEN_RELEASE_STORE_FILE -w) WACKEN_RELEASE_STORE_PASSWORD=$(security find-generic-password -s WACKEN_RELEASE_STORE_PASSWORD -w) WACKEN_RELEASE_KEY_ALIAS=$(security find-generic-password -s WACKEN_RELEASE_KEY_ALIAS -w) WACKEN_RELEASE_KEY_PASSWORD=$(security find-generic-password -s WACKEN_RELEASE_KEY_PASSWORD -w) ./gradlew clean :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac assembleDebug assembleRelease

### Manual validation

- Verified APK signatures with /Users/dino/Library/Android/sdk/build-tools/36.0.0/apksigner verify --verbose app/build/outputs/apk/release/app-release.apk. V1 and V2 schemes are true.
- Verified APK badging with /Users/dino/Library/Android/sdk/build-tools/36.0.0/aapt dump badging app/build/outputs/apk/release/app-release.apk: package be.wacken.planner, versionCode 31, versionName 2.28, minSdkVersion 23, targetSdkVersion 36.
- Verified checksum with shasum -a 256 app/build/outputs/apk/release/app-release.apk.
- Verified formatting with git diff --check.
- Verified published GitHub release v2.28 contains asset app-release.apk.

## TDD / BDD / approval-test evidence

- Added focused regression tests for the exact Supabase +00:00 timestamp format and the existing Z-form timestamp before the full release validation.

## Architecture impact

- Architecture-significant change: no; this is an adapter parsing fix with no schema, API, domain model, or persistence model change.
- Approval received: not required.
- ADR: not required.

## README impact

README impact: updated with the V2.28 release-notes link.

## Business requirements impact

Business requirements impact: none, because this fixes already required Supabase sync behavior and does not change product scope.

## Diagram impact

Diagram impact: none, because no architecture or flow diagram changed.

## Commits / logical change list

- 0f27d22 Prepare v2.28 Supabase timestamp hotfix
- Tag: v2.28

## Risks and follow-up

- This fix addresses the timestamp parse failure shown in the app message. If sync still reports another error after V2.28, capture the new exact error text so the next failure can be fixed directly.
<!-- SECTION:NOTES:END -->

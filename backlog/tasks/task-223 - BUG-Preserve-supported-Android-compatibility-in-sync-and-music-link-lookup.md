---
id: TASK-223
title: 'BUG: Preserve supported Android compatibility in sync and music-link lookup'
status: In Progress
assignee:
  - '@codex'
created_date: '2026-10-17 16:45'
updated_date: '2026-10-17 16:57'
labels:
  - bug
  - android
  - compatibility
milestone: m-0
dependencies: []
references:
  - musicbrainzmetadataprovider
  - spotifymetadataprovider
  - supabaseratingclient
  - supabasemasterdataclient
  - wikidatametadataprovider
  - wikipediametadataprovider
  - youtubemetadataprovider
  - apprepositories
modified_files:
  - app/src/test/java/be/wacken/planner/AndroidJavaCompatibilityTest.java
  - app/src/main/java/be/wacken/planner/AndroidJavaCompatibility.java
  - app/src/main/java/be/wacken/planner/MusicBrainzMetadataProvider.java
  - app/src/main/java/be/wacken/planner/SpotifyMetadataProvider.java
  - >-
    app/src/main/java/be/wacken/planner/SupabaseFestivalPlanningRatingClient.java
  - app/src/main/java/be/wacken/planner/SupabaseMasterDataClient.java
  - app/src/main/java/be/wacken/planner/SupabasePersonalBandRatingClient.java
  - app/src/main/java/be/wacken/planner/SupabaseRatingClient.java
  - app/src/main/java/be/wacken/planner/SupabaseScheduleLockClient.java
  - app/src/main/java/be/wacken/planner/WikidataMetadataProvider.java
  - app/src/main/java/be/wacken/planner/WikipediaMetadataProvider.java
  - app/src/main/java/be/wacken/planner/YouTubeMetadataProvider.java
  - app/src/main/java/be/wacken/planner/AppRepositories.java
  - app/build.gradle
  - app/src/test/java/be/wacken/planner/YouTubeMetadataProviderTest.java
  - app/src/androidTest/java/be/wacken/planner/SupportedJavaApisAndroidTest.java
  - .github/workflows/ci.yml
  - README.md
  - scripts/tests/test_android_smoke_runner.py
  - scripts/verify-android-smoke.sh
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a member using a supported Android version, I need app startup, rating sync and music-link lookup to use APIs available on that version so these flows do not fail on older devices. Full lintDebug currently reports14 existing NewApi errors: File.toPath requires26 and URLEncoder.encode(String, Charset) requires33 while minSdk is23. Resolve the API usage without hiding errors, weakening lint or raising the supported minimum. Out of scope: dependency upgrades and redesign. This is a required release2.34 quality gate discovered while validatingTASK156.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given the declared minimum Android API23, startup/local storage and URL encoding use compatible APIs while preserving UTF-8 query behavior.
- [ ] #2 Full Android lintDebug and release lintVital pass without suppressing or baselining the NewApi findings.
- [ ] #3 Regression tests cover reserved characters, spaces, Unicode and the changed startup/storage path; existing sync/provider/native suites remain green.
- [ ] #4 README, business requirements, diagram and ADR impacts are recorded with delivery-governance wording.
- [ ] #5 Given CI runs on release changes, full lint and native tests run on minimum API23 and current API36, and an incompatible runtime configuration blocks publication.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Actual root fix: use existing pinned desugar runtime's NIO variant2.1.5 so Java file and newer URL encoding APIs are backported for declared minSdk23; no source wrappers/version upgrade/framework or architecture boundary change. Preserve original storage/provider behavior. Add actual Unicode/reserved query regression, native real TSV/metadata/history round-trip and provider requests; extend existing full-lint/native CI to23/36. TDD runner coverage adds supported API23 airplane control and failure restoration. Full JVM/QA/coverage/full lint and release lintVital pass. Await local59 native, required API23/36 CI and clean signed task APK before closure. README documents runtime and gate; BR/diagram/ADR have no product/boundary impact. Provisional no-config helper approach was removed when lint proved the missing NIO runtime was the root cause.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Root cause confirmed against official Android/desugar documentation: the existing standard desugar_jdk_libs:2.1.5 variant omits java.nio.file support below26, so merely changing File.toPath to Paths.get leaves the same compatibility failure. Revise the bounded fix to select the same-version desugar_jdk_libs_nio:2.1.5 variant of the existing runtime. This is not a version upgrade, new framework/service/module/schema/API or architecture boundary; it supplies already-used NIO APIs for the already-declared API23 support. No additional architecture-significant decision is introduced. Keep default support23, no lint suppression. Source: https://developer.android.com/studio/write/java11-nio-support-table and https://github.com/google/desugar_jdk_libs/blob/master/CHANGELOG.md .

The same-version NIO variant fixes all14 lint errors while retaining the original storage/provider code. The provisional source wrappers were removed because the runtime configuration is the root cause. Final changes are the pinned runtime variant, regression tests against actual YouTube query/storage behavior, and existing lint/native CI gates extended to API23/API36. Local API36 image is installed; API23 runtime validation is delegated to the required GitHub matrix and must pass before this task is Done.

Validated minimal root fix: retain existing File.toPath/Charset-overload code and select same-version NIO runtime. Full JVM/QA/coverage, full lintDebug (zero errors) and release lintVital passed. Added actual provider UTF-8 query regression and native real TSV/metadata/history round-trip plus provider request. Added three fake-device runner tests: API23 legacy settings/broadcast, API36 connectivity command, and restoration on test failure; first API23 assertion failed, all three now pass. CI now requires full lint and full native matrix23/36. Local full native59 is running; API23 CI and fresh signed APK remain before Done. Previously touched wrapper/client paths were restored and provisional helper/tests removed, so they are absent from the final code diff.
<!-- SECTION:NOTES:END -->

---
id: TASK-223
title: 'BUG: Preserve supported Android compatibility in sync and music-link lookup'
status: Done
assignee:
  - '@codex'
created_date: '2026-10-17 16:45'
updated_date: '2026-10-17 18:05'
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
  - festivalcsvreader
  - musiclinksreviewactivity
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
  - app/src/androidTest/java/be/wacken/planner/CriticalFlowsAndroidTest.java
  - app/src/main/java/be/wacken/planner/FestivalBandReviewDialog.java
  - >-
    application/src/main/java/be/wacken/planner/application/FestivalCsvReader.java
  - app/src/main/java/be/wacken/planner/MusicLinksReviewActivity.java
  - app/src/androidTest/java/be/wacken/planner/MusicLinksReviewAndroidTest.java
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a member using a supported Android version, I need app startup, rating sync and music-link lookup to use APIs available on that version so these flows do not fail on older devices. Full lintDebug currently reports14 existing NewApi errors: File.toPath requires26 and URLEncoder.encode(String, Charset) requires33 while minSdk is23. Resolve the API usage without hiding errors, weakening lint or raising the supported minimum. Out of scope: dependency upgrades and redesign. This is a required release2.34 quality gate discovered while validatingTASK156.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given the declared minimum Android API23, startup/local storage and URL encoding use compatible APIs while preserving UTF-8 query behavior.
- [x] #2 Full Android lintDebug and release lintVital pass without suppressing or baselining the NewApi findings.
- [x] #3 Regression tests cover reserved characters, spaces, Unicode and the changed startup/storage path; existing sync/provider/native suites remain green.
- [x] #4 README, business requirements, diagram and ADR impacts are recorded with delivery-governance wording.
- [x] #5 Given CI runs on release changes, full lint and native tests run on minimum API23 and current API36, and an incompatible runtime configuration blocks publication.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Completed same-version NIO runtime configuration with original storage/provider code preserved; actual UTF-8 query and native TSV/metadata/history tests; compatible CSV builder checks and dialog callback; retained text notifications and deterministic recreated/resumed-session test; compatible emulator network runner with three tests and diagnostics; mandatory full lint/API23/API36 CI. Full source84f72db CI is green, native59 per API with no failures/skips, JVM/QA/coverage/real API/SQL/lint and signed clean APK pass. README updated; BR/diagram/ADR no architecture/product impact. Deviation: source wrappers were removed after identifying missing runtime backports; minimum-device validation exposed additional parser, dialog, fixture and lifecycle issues, all resolved. No new framework/major dependency version/schema/API/domain boundary or minSdk change.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Restored the existing declared Android API23 support by selecting the NIO variant of the already-pinned Java compatibility runtime2.1.5. Existing File/Path storage and UTF-8 URL encoding are now backported. The original provider/storage code remains; no wrapper layer, version upgrade, minSdk increase or lint suppression was added.

The minimum-device gate also caught and fixed unsupported StringBuilder.isEmpty calls in CSV parsing, a pre-show dialog callback and an unsupported test-fixture stream read. Retained music-link text handling ignores unchanged/restoring notifications. Rotation validation observes the actual new RESUMED activity and checks retained session/worker/review state.

## Acceptance criteria validation

1. Same supported minimum23 and existing UTF-8/storage semantics; real TSV/provider native regression and full API23/API36 matrix are required.
2. Full lintDebug now passes with zero errors; release lintVital passes.
3. Unicode/reserved query test plus actual native TSV/metadata/history round-trip, existing CSV/creation/ratings/provider suites and rotation coverage.
4. Canonical documentation impacts below.
5. CI runs full lint and native API23/API36 and retains failure diagnostics; runner's API23/API36 setup/restore and failure cleanup have three passing tests.

## How to test

### Automated tests

- JDK21 ./gradlew test qaTest :app:lintDebug :app:lintVitalRelease: passed with domain/application coverage gates.
- python3 scripts/tests/test_android_smoke_runner.py:3 passed; sh -n scripts/verify-android-smoke.sh and git diff --check passed.
- Existing full local API36 native59 tests passed; final affected15 passed, and revised rotation suite7 checked. The current-source GitHub matrix supplies minimum/current-device full-suite proof.
- JDK21 clean signed task build including full lint and release lintVital: see final artifact evidence below.
- Real PostgreSQL/PostgREST creation16 and rating/archive/correction/deletion/alias gates are required CI steps and remain passing.
- No skipped native/API gates may be represented as passed.

### Manual validation

No human acceptance claimed. Local native runs use the isolated API36 emulator, never a physical device or existing release data. API23 is exercised in Linux CI because only the API36 image is installed locally.

## TDD / BDD / approval-test evidence

Full lint initially failed with14 actual NewApi findings; swapping source calls left a remaining NIO failure. The same-version NIO runtime fixed the configuration root cause, so provisional wrappers were removed. API23 CI then demonstrated failures hidden by API36: unsupported test-fixture stream read, early dialog callback and StringBuilder emptiness checks. Existing failing CSV and rotation scenarios were retained, with stronger lifecycle/session assertions. Runner's API23 branch was first red and then all three tests passed. No assertions or compatibility checks were suppressed.

## Architecture impact

- Architecture-significant change: no new framework, major dependency version, module, persistence/API/domain boundary or supported-device policy. This selects the compatible variant of the same existing pinned runtime.
- Approval received: existing architecture and supported-device direction retained; no new architecture decision required.
- ADR impact: none, because the existing architecture and supported range remain; the change repairs runtime configuration and implementation compatibility.
- Existing TSV/Room/Supabase boundaries and production data stay unchanged.

## README impact

README impact: updated the pinned NIO runtime requirement, supported-API troubleshooting, full lint command and native23/36 CI gates.

## Business requirements impact

Business requirements impact: none, because this restores existing supported-device and rotation/CSV behavior without changing product scope or rating rules.

## Diagram impact

Diagram impact: none, because there are no boundary, module or data-flow changes.

## Commits / logical change list

- 47f0d95: pinned NIO runtime variant, actual encoding/storage regression and lint/native CI matrix.
- c216b74: historical cause and validation gap.
- 392c102: compatible fixture read, early dialog callback and failure artifacts.
- af62646: compatible CSV builder checks and retained text behavior.
- 84f72db: actual recreated RESUMED activity/session/worker assertions.
- Provisional helper/client files listed in touched-file metadata were restored/removed and are not final changes.

## Risks and follow-up

Historical cause:1e36f6131621e73391ebdbdf81a227df651a6790 (2026-06-10) lowered minSdk26→23 with the standard desugar variant, which omitted NIO support. Later Charset encoding also required a backport below33. CI previously ran JVM/current API36 without full lint/minimum-device validation. The required lint and real23/36 matrix close that gap.

This task validation APK is not published2.34; release214 still requires169/219/220, final exact-source validation, data-preserving upgrade and DB-before-APK rollout. 

Official reference: https://developer.android.com/studio/write/java11-nio-support-table .

Final validation: CI run https://github.com/DinoDeWaen/wacken/actions/runs/39056235776 at84f72db is fully green: native59 on API23, native59 on API36, and full JVM/QA/coverage/lint/real SQL/API/build job. No native failures/skips. Final affected15 and revised rotation suite7 also passed locally. Clean signed build (test qaTest lintDebug lintVitalRelease assembleRelease) passed; APK SHA256 b8967f92b35dc0ac169a1e4800ccb351da11ea3e0f35a522e3023f502dc57175; certificate SHA256 ede2ae97735a305900e87bc0fdc021c03382db4e30435f2349a96a3fdcc7545f. Intermediate version2.33.5/code41, not a published2.34 release.
<!-- SECTION:NOTES:END -->

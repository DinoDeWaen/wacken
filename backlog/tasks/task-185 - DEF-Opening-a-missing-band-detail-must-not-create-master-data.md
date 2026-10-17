---
id: TASK-185
title: 'DEF: Opening a missing band detail must not create master data'
status: Done
assignee:
  - '@codex'
created_date: '2026-10-14 06:38'
updated_date: '2026-10-15 17:52'
labels:
  - android
  - architecture
  - band-detail
milestone: m-1
dependencies: []
references:
  - app/src/main/java/be/wacken/planner/BandDetailActivity.java
  - >-
    infrastructure/src/main/java/be/wacken/planner/infrastructure/SyncedBandRepository.java
  - listactivefestivalbandsusecase
  - banddetailactivity
modified_files:
  - >-
    application/src/test/java/be/wacken/planner/application/ShowBandDetailUseCaseTest.java
  - app/src/test/java/be/wacken/planner/BandDetailActivityRegressionTest.java
  - >-
    application/src/main/java/be/wacken/planner/application/ShowBandDetailUseCase.java
  - app/src/main/java/be/wacken/planner/BandDetailActivity.java
priority: high
type: bug
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Business value: Viewing a band must never silently add a placeholder to the shared catalog.

Current evidence: BandDetailActivity constructs a Band from an intent name and saves it if absent; SyncedBandRepository writes to both the source and Room cache. Supabase is the configured source in normal operation.

Scope: Handle absent or invalid band detail requests explicitly and keep any deliberate band creation in the established application workflow. Preserve the existing detail and rating behavior for known bands.

Out of scope: Catalog redesign, metadata enrichment, and unrelated detail UI refactoring.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given an unknown or invalid band name, when band detail is opened, then the app handles the missing band without creating a source or cache record.
- [x] #2 Given a known band, when its detail is opened, then its current details and rating actions remain available.
- [x] #3 Automated tests cover the missing-band path and confirm that viewing detail performs no master-data write; existing detail behavior is protected.
- [x] #4 Architecture impact is assessed before implementation; if an architecture-significant change is needed, explicit approval is obtained before coding and an ADR is created or updated.
- [x] #5 README and business requirements impact are recorded using the canonical wording from delivery-governance.md, and validation evidence is added before completion.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Treatment: minimal focused bug fix, completed within the existing Android-to-application read flow.
1. Added a failing application regression for invalid-name lookup and an Android Activity source regression for save-on-view; existing known-band tests characterize the preserved detail and rating behavior.
2. ShowBandDetailUseCase now returns empty for null/blank names and trims valid names. BandDetailActivity renders a missing-band message with Back action instead of saving a placeholder, and uses the returned detail for known bands.
3. Ran focused application and Android unit suites, full ./gradlew test, signed assembleRelease, APK signature/metadata checks, and git diff --check.
Changed files: application/src/test/java/be/wacken/planner/application/ShowBandDetailUseCaseTest.java; app/src/test/java/be/wacken/planner/BandDetailActivityRegressionTest.java; application/src/main/java/be/wacken/planner/application/ShowBandDetailUseCase.java; app/src/main/java/be/wacken/planner/BandDetailActivity.java.
Architecture: no module, port, adapter, persistence, schema, or domain ownership change; approval and ADR not required. README, business requirements, and diagrams did not need updates for this narrow missing-detail error path. Deviation: the first local release build was unsigned; rebuilt with existing local signing credentials and verified the signed APK. Risk: the Activity is covered by a source-level regression because this repository's JVM tests cannot launch Android Activities; no device test was run.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Opening a missing or invalid band now shows an unavailable-detail message and Back action. The detail screen no longer saves placeholder bands to Supabase or Room. Known-band detail and rating controls retain their existing path.

## Acceptance criteria validation

- AC1: Missing/blank/null names return no detail; Activity shows the missing state without save. Application no-write and Activity regression tests pass.
- AC2: Existing known-band detail and rating tests pass in the complete test suite.
- AC3: New failing regressions were observed before the fix and pass after it. The Activity regression guards against reintroducing save-on-view.
- AC4: No architecture-significant change: the existing application read use case and repository boundary are retained. No approval or ADR required.
- AC5: Documentation impact and validation are recorded below.

## How to test

### Automated tests

- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :application:test :app:testDebugUnitTest` passed.
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew test` passed, including coverage verification.
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew assembleRelease` passed with existing local signing values.
- `apksigner verify --verbose` confirmed v1 and v2 signatures. `aapt dump badging` confirmed package be.wacken.planner, versionCode 35, versionName 2.32, minSdk 23. `git diff --check` passed.
- Local signed APK SHA-256: a53f7677d9f767605c924ffcb99143aef7f75fe2a627fa7538e73902af055769.

### Manual validation

No device or emulator walkthrough was run. The local signed APK is a task-validation build at version 2.32, not a published Release 2.33 artifact.

## TDD / BDD / approval-test evidence

The invalid-name application regression and Activity save-on-view regression failed before the fix and passed afterward. Existing known-band tests provided behavior protection. No legacy refactor was performed.

## Architecture impact

- Architecture-significant change: no; the Android Activity calls the existing ShowBandDetailUseCase and no port, adapter, schema, or layer ownership changes.
- Approval received: not required.
- ADR impact: none, because the established read flow and boundaries remain intact.

## README impact

README impact: none, because the existing band-detail capability and setup are unchanged; this corrects an erroneous missing-band path.

## Business requirements impact

Business requirements impact: none, because no business rule, scope, or capability changes.

## Diagram impact

Diagram impact: none, because components and relationships remain the same.

## Commits / logical change list

- Guard invalid band names in the application detail lookup.
- Replace Activity save-on-view with an explicit missing state.
- Add focused application and Activity regressions.

## Risks and follow-up

Activity wiring has a source-level regression rather than a launched-Activity test because the current plain JVM setup has no Android Activity runner. No device test was performed. Release 2.33 versioning and publication remain separate work.
<!-- SECTION:NOTES:END -->

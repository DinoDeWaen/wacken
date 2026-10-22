---
id: TASK-210
title: 'US: Use Settings and verified music review in the paired 3.0 layout'
status: Done
assignee:
  - '@codex'
created_date: '2026-10-17 09:21'
updated_date: '2026-10-22 15:40'
labels:
  - ux
  - release-3.0
milestone: m-2
dependencies:
  - TASK-205
  - TASK-206
  - TASK-202
references:
  - settingsactivity
  - musiclinksreviewactivity
  - bandlinkreviewactivity
  - bandmetadatareviewactivity
modified_files:
  - app/src/androidTest/java/be/wacken/planner/MusicLinksReviewAndroidTest.java
  - app/src/androidTest/java/be/wacken/planner/CriticalFlowsAndroidTest.java
  - app/src/main/java/be/wacken/planner/WackenTheme.java
  - app/src/main/java/be/wacken/planner/MusicLinksReviewActivity.java
  - app/src/main/java/be/wacken/planner/SettingsActivity.java
  - app/src/main/java/be/wacken/planner/BandLinkReviewActivity.java
  - app/src/main/java/be/wacken/planner/BandMetadataReviewActivity.java
  - >-
    groma/systems/wacken/containers/android-application/components/settingsactivity.md
  - >-
    groma/systems/wacken/containers/android-application/components/bandlinkreviewactivity.md
  - >-
    groma/systems/wacken/containers/android-application/components/bandmetadatareviewactivity.md
  - >-
    groma/systems/wacken/containers/android-application/components/musiclinksreviewactivity.md
  - README.md
  - backlog/docs/business-requirements.md
  - >-
    app/src/test/java/be/wacken/planner/BandLinkReviewActivityRegressionTest.java
  - backlog/docs/groma-review-layouts-2026-10-22.md
  - tools/groma/architecture-baseline.json
priority: medium
type: enhancement
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a festival administrator, I want grouped Settings and trustworthy review screens in both appearances so I can manage links without losing work. Adapt Settings, Link bands and TASK-202 verified music review using production identity evidence. Include provider setup/errors, progress, cancellation, retry, approval/rejection and no-match. No new provider integration or changes to approved verification rules in this presentation story.

Validated baseline: TASK-201, design/release-2.34/themes.html; user approved 2026-10-17. Keep the existing Android Views stack and current domain/application/infrastructure boundaries. Assess architecture impact before implementation; significant changes require explicit approval and ADR handling.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given Settings, when managing a festival, then Link bands and festival music discovery are adjacent and band-only discovery remains available on the appropriate detail page.
- [x] #2 Given verification results, then candidate identity, canonical URL, clickable sources and verified/unverified rationale are readable in both appearances; unverified candidates cannot be saved.
- [x] #3 Given a theme change during search/review, then search text, scope, progress and reviewed selections are retained; explicit approval alone writes missing fields.
- [x] #4 Given rejection/no match/offline/missing configuration/rate limit/cancel/error, then accurate recovery/status is visible and existing metadata, ratings and history remain unchanged.
- [x] #5 Native tests cover both entry points, evidence and approval gates, ongoing work across theme/recreation, no overwrite and provider failure states.
- [x] #6 README and business requirements impacts use canonical delivery-governance wording; approved behavior is documented, diagrams updated only where affected, and the full validation package is recorded before merge.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Completed standard existing-Views/ADR0024 slice: characterized7 native verification flows and existing application metadata rules; new source-URL/approval/linking state scenarios red→green. Grouped Settings with readable headings/live status/retained scroll. Added paired48dp input/choice helpers, full source URLs, immediate stale-evidence removal on term edits. Linking retains edited terms/explicit No match/selected candidate/scroll; metadata retains active read-only search/results/checked proposals and safely discards cancelled results. Existing domain/application identity/missing-field/no-overwrite authority unchanged. Local92 native plus final native width check, clean7dd1d60 test/QA/lint/fresh stable-key APK, reviewed rendered Groma+exact82 groups/diagnostics/two scans/27 fixtures and required CI39806092021 (92 each API23/API36,complete build/database gates) all pass. README/BR/map audit updated. No architecture-significant change or new ADR; Standard/ADR0024 retained. Deviations: removed obsolete source-string horizontal-row assertion in favor of actual native usable width; nonlinear Android font-size test uses TypedValue; metadata retained native regression added alongside implementation. Integrated accessibility/device matrix remains212,publication213.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Settings groups festival administration with adjacent Link imported bands/Find music links. Review screens use readable wrapping text, full source URLs,48dp native inputs/choices and paired focus/tint styling. Music term edits immediately remove stale evidence/approval controls without rebuilding the input. Own-catalog linking retains search/match/No match and viewport. Metadata retains read-only search, results and checked proposals across recreation; cancellation discards late results and retry is explicit. Existing use cases alone decide identity verification and missing-field/no-overwrite behavior.

## Acceptance criteria validation

1. Adjacent Settings actions still pass the active festival ID; band detail still passes one canonical band, including the existing no-festival entry.
2. Full canonical URLs/source buttons, identity facts/rationale and verified/unverified controls wrap; unverified Save remains disabled.
3. Native200% music review retains selected identity/acknowledgement across both palettes/recreation with one lookup; editing clears the stale controls immediately. Metadata retains one active lookup and checked proposal through recreation; only explicit Save changes the fake repository.
4. Existing native rejection/rate-limit/error/retry/no-match/cancel/unverified scenarios pass. Existing application/provider tests cover missing configuration/offline/no-overwrite; messages remain visible and no production provider/policy changed.
5. Full92 native tests pass locally, including both real entry points, proof gates and new music/metadata/linking retention tests. Required CI39806092021 passes all four jobs, including92 native tests on each API23/API36.
6. README/requirements and reviewed Groma map/baseline updated; complete CI evidence is recorded below.

## How to test

### Automated tests

- Existing7 MusicLinksReviewAndroidTest cases passed before production changes: /private/tmp/wacken-210-characterization.log.
- New native red: hidden canonical source URL and missing own-band search label, /private/tmp/wacken-210-red-native.log and wacken-210-link-red.log. A test API typo was corrected before observing the expected link-label failure.
- Focused9 music/metadata native cases pass, /private/tmp/wacken-210-music-green.log.200% text uses platform TypedValue conversion rather than scaledDensity division, because modern Android nonlinear font scaling makes division inaccurate.
- sh scripts/verify-android-smoke.sh emulator-5554 all:92/92 pass on owned API36 appearance205; /private/tmp/wacken-210-native-full.log. Final additional native usable-width assertion passes /private/tmp/wacken-210-link-final.log; production source unchanged.
- First clean build exposed an obsolete source-string horizontal-row requirement. Removed only that assertion; existing approval/use-case/No match checks retained, actual native width/state checks cover the intended new layout.
- Clean clone7dd1d60: ./gradlew test qaTest :app:lintDebug assembleRelease --rerun-tasks passes, /private/tmp/wacken-210-signed-final.log. Includes JVM/domain/application/BDD/coverage/QA/ArchUnit.
- Pinned two-scan Groma gate passes96components/299sources/13flows/82reviewedgroups, no new exceptions;27 positive/negative fixtures pass. Logs /private/tmp/wacken-210-groma-final.log and wacken-210-groma-fixtures.log.
- git diff --check passes. Required implementation CI39806092021 onfcc5d441681e92d7a2ce6c09e67d617abd90597c passes all four jobs:92 native cases each API23/API36; JVM/BDD/coverage/QA/ArchUnit/lint/supported APIs; real PostgreSQL/PostgREST creation/rating/archive/correction/deletion and complete ADR0016 lifecycle; pinned Groma and positive/negative fixtures.

### Manual validation

Source/composition and Chromium rendered map/source/entry/application/theme collaborations reviewed in /private/tmp/wacken-210-map; screenshot /private/tmp/wacken-210-map-detail.png. Native tests use actual Activities, controls, use cases and fixture evidence/repository seams; they do not exercise public providers live. Human TalkBack/physical-device/outdoor review is not claimed and remains212.

Fresh stable-key APK from clean7dd1d60: /private/tmp/wacken-210-validated-build-checkout/app/build/outputs/apk/release/app-release.apk; SHA256f00e6ead033d747c84b2d16dd3cbcbaf058e25f188171e7ac406871733c79a83. apksigner certificate ede2ae97735a305900e87bc0fdc021c03382db4e30435f2349a96a3fdcc7545f verifies. Checkout started without previous APK. Subsequent changes are docs/map-baseline/evidence only. Unpublished checkpoint;2.35.1 remains published.

## TDD / BDD / approval-test evidence

Existing7 native verification scenarios characterize explicit acknowledgement, no writes on reject/search, failures and retained lookup. New Given200% proof/When theme+recreate+edit and Given own-catalog candidate/When edit+No match+recreate cases moved red→green. Existing metadata/application cases characterized missing-field/no-overwrite behavior before retained UI refactor; the new retained metadata native scenario was added alongside that implementation and passes. Native test repository seams preserve application policy, with no production test hook or new strategy.

## Architecture impact

Architecture-significant change: no. Existing Activity/session pattern, shared Views helpers and application/provider ports retained; no schema/API/dependency/security/business-rule change.
Approval received: user Standard/ADR0024 foundation retained; ADR0016 roles unchanged.
ADR impact: none, because screen-owned retained read-only state and common style helpers fit approved existing boundaries.

## README impact

README impact: updated development grouped Settings/review/state behavior and exact architecture audit link.

## Business requirements impact

Business requirements impact: updated planned3.0 development increment; existing identity verification and missing-field/no-overwrite guarantees remain unchanged.

## Diagram impact

Diagram impact: updated four Groma responsibility overviews and four actual style collaborations through CLI. Existing containment/ownership/flows unchanged; exact duplicate/scanner evidence reviewed in backlog/docs/groma-review-layouts-2026-10-22.md. Runtime Mermaid boundaries unchanged.

## Commits / logical change list

f28c72d readable paired Settings/reviews and retained state/native scenarios;7dd1d60 replace obsolete orientation source assertion with native usable width;fcc5d44 reviewed Groma duplicate/diagnostic evidence. PR https://github.com/DinoDeWaen/wacken/pull/13.

## Risks and follow-up

Provider availability/live identity facts are external and were not fabricated; fixture proof exercises actual application verification. Cancellation cannot force an external service to stop instantly; cancelled late results are discarded and saved data remains intact. Process-death durable review storage was not introduced; explicit approval remains required.211 owns remaining lifecycle/support forms;212 integrated accessibility/device matrix;213 publication. Unrelated user edits excluded.
<!-- SECTION:NOTES:END -->

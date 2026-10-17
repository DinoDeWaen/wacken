---
id: TASK-219
title: >-
  US: Show and export band ratings and festival ratings with their correct
  context
status: Done
assignee:
  - '@codex'
created_date: '2026-10-17 13:18'
updated_date: '2026-10-17 18:58'
labels:
  - ratings
  - festival
  - architecture
  - release-2.34
milestone: m-0
dependencies:
  - TASK-216
  - TASK-218
references:
  - listactivefestivalbandsusecase
  - banddetailactivity
  - wackendatabase
  - bandrepository
  - generatesharedscheduleusecase
  - exportratingscsvusecase
  - settingsactivity
  - apprepositories
  - syncingratingrepository
  - archivedfestivalactivity
modified_files:
  - >-
    application/src/test/java/be/wacken/planner/application/BandRatingContextTest.java
  - >-
    application/src/main/java/be/wacken/planner/application/BandRatingContext.java
  - >-
    application/src/main/java/be/wacken/planner/application/ViewBandRatingContextUseCase.java
  - >-
    application/src/main/java/be/wacken/planner/application/FestivalPlanningRatingView.java
  - >-
    application/src/main/java/be/wacken/planner/application/FestivalAfterRatingView.java
  - >-
    application/src/main/java/be/wacken/planner/application/ListBandsUseCase.java
  - >-
    application/src/main/java/be/wacken/planner/application/GenerateSharedScheduleUseCase.java
  - >-
    application/src/main/java/be/wacken/planner/application/ListArchivedFestivalBandsUseCase.java
  - >-
    application/src/main/java/be/wacken/planner/application/ExportRatingsCsvUseCase.java
  - >-
    application/src/main/java/be/wacken/planner/application/ShowBandDetailUseCase.java
  - >-
    application/src/main/java/be/wacken/planner/application/ListActiveFestivalBandsUseCase.java
  - app/src/main/java/be/wacken/planner/AppRepositories.java
  - app/src/main/java/be/wacken/planner/BandDetailActivity.java
  - app/src/main/java/be/wacken/planner/ArchivedBandDetailActivity.java
  - >-
    application/src/test/java/be/wacken/planner/application/FestivalDetailRatingScopeTest.java
  - >-
    application/src/test/java/be/wacken/planner/application/ExportRatingLifecycleCsvUseCaseTest.java
  - >-
    domain/src/main/java/be/wacken/planner/domain/PersonalBandRatingHistoryRepository.java
  - >-
    application/src/main/java/be/wacken/planner/application/ExportRatingLifecycleCsvUseCase.java
  - >-
    app/src/main/java/be/wacken/planner/persistence/RoomPersonalBandRatingEventDao.java
  - >-
    app/src/main/java/be/wacken/planner/persistence/RoomPersonalBandRatingHistoryRepository.java
  - >-
    app/src/main/java/be/wacken/planner/SyncingPersonalBandRatingHistoryRepository.java
  - app/src/main/java/be/wacken/planner/SettingsActivity.java
  - app/src/androidTest/java/be/wacken/planner/CriticalFlowsAndroidTest.java
  - >-
    app/src/test/java/be/wacken/planner/ArchivedFestivalLayoutRegressionTest.java
  - README.md
  - backlog/docs/business-requirements.md
  - backlog/decisions/0016-band-catalog-and-archive-promoted-personal-ratings.md
  - >-
    application/src/main/java/be/wacken/planner/application/ViewArchivedFestivalHistoryUseCase.java
  - app/src/main/java/be/wacken/planner/ArchivedFestivalActivity.java
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a member, I want detail/history/export to distinguish my current band rating, festival planning rating and festival after-rating so I understand what carries forward. Apply ADR-0016 to active and archived detail, history, Settings export, missing-value states and any current-rating consumers. Metadata and music links resolve through the canonical band. Do not show another festival's after-rating as the selected festival's value. Keep group scheduling based only on that festival's planning ratings. Archived correction audit presentation remains TASK-172;3.0 styling remains separate.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given a band has current rating4, selected-festival planning5 and after-rating3, then details/history/export identify these as three separate values with user/festival/source context.
- [x] #2 Given the selected festival has no after-rating but another festival does, then its after-rating remains empty and the other result appears only in labeled history/current-band context.
- [x] #3 Given planning or after-rating edits, then group scheduling uses only the selected festival's planning ratings and metadata/music links retain canonical identity.
- [x] #4 Given an existing exported CSV contract, then any extension is documented and compatibility is preserved or versioned; original event dates and correction/archive timestamps are distinguishable.
- [x] #5 Application/native/export regression tests cover different users, repeated bands, renamed/deleted festivals, unrated states and preserved navigation.
- [x] #6 README and business requirements describe the implemented presentation/export; canonical diagram and ADR impacts reference ADR-0016.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Completed: read-only context DTO/use case; explicit festival-bound planning/after views and write rejection; exact canonical-name metadata-independent joins and archived identities; existing cache owner history enumeration; separate22-column lifecycle CSV v2 and Settings action; canonical-only native links; refreshed values/context in place across return and retained festival across rotation. Application regressions red→green; native4/5/3, detached history, export, return/scroll/rotation and archive rejection; CI68 native on each API23/API36 plus full JVM/QA/coverage/lint/realSQL/API; fresh clean stable-signed task APK verified. README/BR/ADR0016/C4 updated. Deviation: inspection exposed whole-record metadata joins, case-collapsed archive identities and active-view legacy archive fallback, so these contextual defects were repaired in scope. Existing approved ADR0016 covers changes; no schema/remote contract/dependency change. Final lifecycle/upgrade TASK220 and publication TASK214 remain.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Added read-only own established-band / explicit selected-festival planning / explicit selected-festival after context. Detail shows member, festival identity, source snapshot and known timestamps; missing/reset stays Unrated. Active detail retains its festival across return/rotation and rejects writes after archive/deletion or lineup removal. Native links resolve only canonical metadata, with no stale intent fallback.

Added a separate 22-column rating lifecycle CSV v2 in Settings. Existing nine-column ratings and correction-audit CSV contracts remain intact. Own established/planning/after/history/correction rows retain recorded, established/archive and correction times separately, including detached/deleted festival history. Unknown dates remain blank, explicit reset0 differs from missing, and canonical links remain empty when unknown. All private row streams filter the requested owner.

Fixed exact canonical-name joins in group votes, export, lists and schedule: whole Band-record equality incorrectly included biography/links. Schedule still consumes only selected festival planning votes. Archived lists/history no longer collapse case-distinct identities; native archive fallback views are festival-bound rather than active-festival-bound.

## Acceptance criteria validation

1. Application/native/export4/5/3 scenarios identify each value and its owner/festival/source.
2. Missing selected after remains empty while detached/other history is explicitly labeled.
3. Planning-only schedule regression and metadata joins; detail writes target opened festival; canonical metadata only.
4. Separate version2 schema/filename preserves old contracts; event, established/archive, original and correction dates have distinct columns.
5. Own/other users, missing/reset/repeated bands, renamed/deleted source, metadata, detached history, return/scroll/rotation and archive rejection are covered.
6. README, BR-077a and ADR0016/C4 describe the implementation.

## How to test

### Automated tests

- CI39059567866 on d4766d232891c19ec0aaa588b4236f1872e5d4dc: all jobs green; full native68 on each API23 andAPI36,0 skipped/failed; full JVM/QA/coverage, full lint, real creation16 and rating-state/SQL/PostgREST/RLS gates pass.
- Local first full native67 passed; final affected CriticalFlows/ArchivedCorrection13 passed, including selected-festival return/scroll/rotation and archive write rejection. Initial ddmlib property timeout recovered without a test failure.
- Clean signed validation: JDK21 ./gradlew :app:clean test qaTest :app:lintDebug :app:lintVitalRelease :app:assembleRelease succeeded. Signature v1/v2 verified; package be.wacken.planner, minSdk23, intermediate2.33.5/code41. Certificate SHA256 ede2ae97735a305900e87bc0fdc021c03382db4e30435f2349a96a3fdcc7545f matches published baseline.
- Fresh task APK SHA256 dffaae589e4cf4a2dc7f29d88d2c453895ad66224f25b7b81167f9e801612b93; this is validation only, not published2.34.
- git diff --check passes. A concurrent duplicate local Gradle attempt caused an unreadable transient test output; sequential full rerun and fresh CI pass. No test weakening or source workaround.

### Manual validation

The native emulator exercises real Activities/Room and offline Settings export entry. Reproduce: open a canonical band with established4, planning5 and after3; verify the three labels and source. Export lifecycle v2 offline and inspect record types/dates. Return from another screen without losing scroll; rotate after the opened festival is archived and confirm reset fails without writing another festival. In an archive with case-distinct bands, select each exact identity. The full rating lifecycle and signed legacy upgrade are TASK220, final production deployment/publication TASK214.

## TDD / BDD / approval-test evidence

Missing context/export use cases first failed compilation. Metadata group join and schedule regressions failed with hidden votes/no schedule, then passed with exact-name joins. Case-distinct archived names failed with1 instead of2, then passed. Existing export/list/schedule/Room characterization suites remain green. Given4/5/3, missing selected after and stale selected festival native/application scenarios assert user-visible behavior. No new framework added.

## Architecture impact

Architecture-significant change: approved ADR0016 presentation/export scope, existing repository composition plus read-only own event enumeration; no schema/remote contract/dependency/transaction change.
Approval received: user's explicit adoption of canonical catalog and archive-promoted member ratings for all stories.
ADR impact: updated ADR0016 implementation consequences; no additional policy supplied by UI.

## README impact

README impact: updated rating context, exact identity, versioned CSV semantics/limits and C4 application responsibility.

## Business requirements impact

Business requirements impact: updated implementation status and BR-077a presentation/export contract.

## Diagram impact

Diagram impact: updated README C4 application description to include rating context/export; existing cache/port/container boundaries remain valid.

## Commits / logical change list

d4766d2: context composition, bound detail views, canonical identity joins, lifecycle export/read wiring, regression tests and documentation.

## Risks and follow-up

Export is a cached snapshot, not proof all devices synced. Planning has no recorded timestamp; absent/unknown dates stay blank. Established_at is promotion time retained across correction; a separate archive timestamp is not fabricated for every festival. Older nine-column exports retain legacy header names. Archived history legacy constructor remains compatible, while native callers supply the explicit target view. Room schema remains12; V014–V018 production deployment and final2.34 version/signing/publication remain TASK214 after TASK220.3.0 styling remains separate.
<!-- SECTION:NOTES:END -->

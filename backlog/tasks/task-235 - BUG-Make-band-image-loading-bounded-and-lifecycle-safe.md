---
id: TASK-235
title: 'BUG: Make band image loading bounded and lifecycle safe'
status: Done
assignee:
  - '@codex'
created_date: '2026-10-20 06:26'
updated_date: '2026-10-21 07:03'
labels:
  - groma-audit
  - release-2.35
milestone: m-3
dependencies: []
references:
  - wacken
  - banddetailactivity
  - bandimageloader
documentation:
  - backlog/docs/groma-audit-2026-10-20.md
modified_files:
  - app/src/test/java/be/wacken/planner/BandImageLoaderTest.java
  - app/src/main/java/be/wacken/planner/BandImageLoader.java
  - app/src/main/java/be/wacken/planner/BandDetailActivity.java
  - app/src/main/java/be/wacken/planner/ArchivedBandDetailActivity.java
  - groma/systems/wacken/components/bandimageloader.md
  - groma/systems/wacken/components/banddetailactivity.md
  - README.md
  - backlog/docs/business-requirements.md
  - app/src/androidTest/java/be/wacken/planner/CriticalFlowsAndroidTest.java
priority: medium
type: bug
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## User story
As an attendee, I want band images to load safely when I navigate or rotate so stale requests do not affect a different screen or retain resources.

## Scope
Characterize active/archive image success and failure behaviour; give duplicated loading a justified Android-side owner with stream closure, bounded waits and lifecycle/stale-target protection. Preserve existing visual design and missing-image behaviour. No new image framework without approval.

## Audit reference
[Groma architecture audit — 20 October 2026](../docs/groma-audit-2026-10-20.md)
Repository path: backlog/docs/groma-audit-2026-10-20.md
Findings: F7; lint groups 7/8. Investigation: TASK-225.

## Boundaries and impacts
Preserve ADR-0016: shared canonical metadata, member-specific established band rating, independent festival planning/after-ratings, one-time creation snapshots and source-aware archive promotion/correction. Follow Groma CLI-only curation and Backlog CLI-only task maintenance.
Business requirements impact: none planned unless an approved behaviour change is explicitly required; record the actual impact on completion.
README and diagram impacts are assessed for the affected workflow. ADR impact: none assumed; approval is required for significant changes.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given image success, decode failure or network failure, streams and owned request resources are released and the existing visible fallback remains correct.
- [x] #2 Given stalled image retrieval, navigation, recreation or a rebound target, requests are bounded/cancelled or ignored safely and stale results cannot update the wrong/destroyed view.
- [x] #3 Characterization tests precede refactoring; controlled stream/network and lifecycle tests cover success, error, timeout and stale completion without public image hosts.
- [x] #4 Active and archived details retain their band/festival context and edit restrictions; no rating state or metadata is changed by image failures.
- [x] #5 README troubleshooting and architecture impacts are documented, with approval/ADR handling for any significant new boundary or dependency.
- [x] #6 Architecture impact is assessed before implementation; significant boundary, dependency, persistence or test-strategy changes receive explicit human approval before coding and an ADR is recorded when required.
- [x] #7 README, business requirements, diagram and ADR impacts use canonical delivery-governance wording; test evidence, manual verification, finalized plan and remaining risks are recorded before merge.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Recorded identical active/archive source callback baseline and existing passing detail JVM baseline. Wrote loader contract tests red before helper existed; five passed before replacing Activity image code.
2. Implemented package-local generic image mechanics with 5s connect/read limits, stream closure, HTTP disconnect, replacement identity and cancellation/shutdown on close. Sixth test covers destroy while decode is running.
3. Integrated both Activities with unchanged image layout, screen-owned bitmap/view updates, finishing/destroyed checks and close in onDestroy. Null decode intentionally uses missing-image hide fallback.
4. Android JVM265 (249 passed, 16 existing API skips), compatibility lint and full native API36 suite70 passed with zero native skips/failures. Added local PNG/recreation/archive and invalid-image/pending-retention native cases. No API23 runtime test claimed.
5. Updated README/BR-052 and Groma source ownership/two collaborations; rescan created zero components. Fresh signed build/signature/digest recorded and completed task committed.
Standard presentation mechanics only: no domain/port/module/dependency/schema/policy ownership change; no architecture approval/ADR needed. Initial read-only emulator reused data and was stopped; actual native tests used a new disposable AVD under /private/tmp with no existing app. Native image baselines added after integration; pre-extraction callback characterization used controlled loader contracts and existing flow baseline, transparently recorded.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary
Active and archived details share one image request owner per screen. Requests configure 5-second connection/inactive-read limits, close streams and disconnect HTTP; replacement/destruction invalidates queued results and cancels owned work. Both Activities guard finishing/destroyed views and close the loader in onDestroy. Failed or null decoding hides the image; rating, metadata and lifecycle flows are unchanged.

## Acceptance criteria validation
1. Controlled success/null/IO decode/timeout cases verify stream and connection release with correct success/missing callbacks. Actual native PNG and invalid-image tests confirm display/hide behavior in both screens.
2. Tests cover queued stale completion after replacement, queued success/failure after destruction, cancellation before work and destruction during active decode. Real Activity recreation and archive detail image loading pass on API36.
3. New loader tests were red before implementation; five callback/resource/lifecycle cases passed before Activity replacement. Sixth running-cancellation case and two actual native image cases added afterward. Existing pre-change flow suite was green; source baseline recorded honestly.
4. Full native suite70 passed, including active edits, archive corrections/read-only behavior, canonical links, selected festival/rotation, pending values and new image cases. Image code references no rating/metadata writers.
5–7. README/BR-052, Groma, finalized plan, architecture assessment and validation impacts recorded.

## How to test
### Automated tests
JDK21 ./gradlew :app:testDebugUnitTest :app:lintDebug passed: 265 tests total, 249 passed, 0 failures/errors, 16 existing disposable-API AddFestivalPersistenceRegressionTest skips. Six BandImageLoaderTest cases use controlled stream/timeout/decoding and deterministic worker/UI queues; no public hosts.
sh scripts/verify-android-smoke.sh emulator-5560 all passed: 70/70 on fresh wacken235native Android16/API36 arm64 emulator, 0 failures/skips. New CriticalFlows cases exercise valid local PNG across Activity recreation/archive, undecodable images in both screens, and unchanged pending planning values. Offline runner restored network/session/data fixtures. First read-only emulator reused prior install and was stopped without tests; actual run used unique /private/tmp AVD/data and no preinstalled app. API23 native not run.
Groma6.6 / Java0.2.2 rescan created0 after described helper and two real source collaborations; scanner diagnostics are not compiler results. git diff --check passed.
Required fresh stable-key signed APK built after deleting prior artifact: JDK21 assembleRelease --rerun-tasks, 53 executed; v1/v2 verified. SHA256 abdddc07f3d69f29f1c8fe3e9abcdbf26299c5f8e1a6079025cad9462b4f88a4 at app/build/outputs/apk/release/app-release.apk, version2.34/code42, not published.
### Manual validation
Reviewed both Activity diffs: same image size/style/content description; view updates still on UI thread; existing archive correction dismissal retained; only image request ownership/onDestroy changes. Native images use local cache files and restore fixture catalog/session/state. No external image service required.

## TDD / BDD / approval-test evidence
Recorded old identical success=set bitmap / exception=hide callback baseline. Loader spec red before implementation, green before moving Activity image methods; no claim of pre-change native image execution. New null-decode hide behavior explicitly documented. Native Given cached canonical image/invalid file, When active detail recreates or archive opens, Then image decodes/hides and pending planning stays unchanged. Existing real flow/native contexts remain green.

## Architecture impact
- Architecture-significant change: no; presentation-side request/resource mechanics within existing Android adapter, no new framework/port/module/runtime/schema/domain ownership.
- Approval received: not required.
- ADR: ADR impact: none, because existing architectural boundaries and policy ownership are unchanged.

## README impact
README impact: updated image timeout/resource/lifecycle troubleshooting and precise per-I/O limits.

## Business requirements impact
Business requirements impact: updated BR-052 for failed/undecodable hiding and stale-result suppression in active/archive detail.

## Diagram impact
Diagram impact: updated Groma with described/grouped image owner and two source-supported Activity collaborations; no new island.

## Commits / logical change list
Controlled loader tests/helper; two Activity delegations/destruction cleanup; two native image cases; README/BR-052; Groma ownership/collaborations; finalized evidence.

## Risks and follow-up
Timeouts bound connection/inactive reads, not total download or CPU decoding. Stale delivery is suppressed even if cancellation completes later. API36 native passed; API23 and real database/final signed-upgrade release gates remain TASK-241. Styling/navigation/3.0 redesign unchanged. Disposable emulator retained for subsequent native validation. No merge/tag/publication.

Final lint rerun including the two new native image scenarios passed. Full native API36 XML/report: app/build/outputs/androidTest-results/connected/debug/TEST-wacken235native(AVD) - 16-_app-.xml, 70 tests with zero failures/skips. No unresolved task-local validation failures.
<!-- SECTION:NOTES:END -->

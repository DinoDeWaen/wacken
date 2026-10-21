---
id: TASK-239
title: 'REF: Share active and archived band-detail presentation without changing flows'
status: Done
assignee:
  - '@codex'
created_date: '2026-10-20 06:26'
updated_date: '2026-10-21 07:39'
labels:
  - groma-audit
  - release-2.35
milestone: m-3
dependencies:
  - TASK-235
references:
  - wacken
  - banddetailactivity
documentation:
  - backlog/docs/groma-audit-2026-10-20.md
modified_files:
  - app/src/androidTest/java/be/wacken/planner/CriticalFlowsAndroidTest.java
  - app/src/main/java/be/wacken/planner/BandDetailPresentation.java
  - app/src/main/java/be/wacken/planner/BandDetailActivity.java
  - app/src/main/java/be/wacken/planner/ArchivedBandDetailActivity.java
  - >-
    app/src/test/java/be/wacken/planner/ArchivedFestivalLayoutRegressionTest.java
  - groma/systems/wacken/components/banddetailpresentation.md
  - groma/systems/wacken/components/banddetailactivity.md
priority: medium
type: task
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## User story
As an attendee, I want consistent band details while active editing and archived correction remain clearly distinct.

## Scope
Characterize current detail screens and share justified read-only presentation for context, history, links, biography and sections. Reuse the lifecycle-safe image owner from the image task. Preserve current styling/navigation; the Ironline/Field Notes redesign remains in TASK-208 and related 3.0 tasks.

## Audit reference
[Groma architecture audit — 20 October 2026](../docs/groma-audit-2026-10-20.md)
Repository path: backlog/docs/groma-audit-2026-10-20.md
Findings: F8; lint groups 2-12. Investigation: TASK-225.

## Boundaries and impacts
Preserve ADR-0016: shared canonical metadata, member-specific established band rating, independent festival planning/after-ratings, one-time creation snapshots and source-aware archive promotion/correction. Follow Groma CLI-only curation and Backlog CLI-only task maintenance.
Business requirements impact: none planned unless an approved behaviour change is explicitly required; record the actual impact on completion.
README and diagram impacts are assessed for the affected workflow. ADR impact: none assumed; approval is required for significant changes.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Approved characterization baselines capture active/archive rendered content and action availability before extraction, including missing metadata and unknown music links.
- [x] #2 Active planning/after edits stay bound to the opened festival; archived controls remain read-only until explicit own-rating correction and never reopen normal editing.
- [x] #3 Canonical link source, per-member ratings, selected-festival history/context, rotation/back navigation and pending state retain behaviour through shared presentation.
- [x] #4 Native regression tests cover both contexts and unsupported actions; no 3.0 theme, navigation or style contract is implemented in this task.
- [x] #5 New presentation ownership stays within the Android adapter; README and diagram impacts are recorded and significant changes require approval.
- [x] #6 Architecture impact is assessed before implementation; significant boundary, dependency, persistence or test-strategy changes receive explicit human approval before coding and an ADR is recorded when required.
- [x] #7 README, business requirements, diagram and ADR impacts use canonical delivery-governance wording; test evidence, manual verification, finalized plan and remaining risks are recorded before merge.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Preserved the prior 70-test API36 baseline and added pre-extraction actual content/action assertions for active/archive missing metadata/unknown links. Corrected two test label expectations to existing labels; 11/11 native critical cases passed before production extraction.
2. Extracted package-local BandDetailPresentation for frame, context styling, history, biography, running order, canonical links, sections/group stars and image panel. Activities retain selection/context queries, rating edits, archive correction and navigation callbacks; BandImageLoader lifecycle retained.
3. Passed unit264 (248 passed, 16 existing API skips), application/coverage/BDD/lint checks and all 71 post-extraction API36 native tests with zero failures/skips. Native asserts context newlines and missing/unsupported actions; replaced one redundant archived detail source-string test with real rendered assertions.
4. Combined helper source ownership through Groma CLI into existing Band detail screens component; rescan created0. Fresh signed APK rebuilt after native checks and signature/digest verified. Canonical no-behavior-change impacts recorded and task committed.
Standard internal Android presentation extraction; no architecture-significant module/runtime/port/domain-policy/auth/schema/dependency or 3.0 contract change; no architecture approval/ADR required. Baselines reviewed against current released behavior, not a new design approval. Deviation: brittle source matcher replaced by stronger native behavior test; literal newline escaping was checked/corrected during extraction before final passing tests.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary
Active/archive detail screens share read-only frame/widgets through BandDetailPresentation. Existing WackenTheme colors/sizes, canonical metadata, personal-history text, context presentation and lifecycle-safe image owner remain unchanged. Active edits and archived own-rating correction/read-only controls remain separately owned by Activities/use cases.

## Acceptance criteria validation
1. Reviewed existing released behavior and passing native70 baseline. Added actual missing-metadata/unknown-link/action assertions; after correcting expected label capitalization, native critical11 passed before any production extraction. Existing canonical/history/rotation tests retained.
2. Active planning/after writes and archive authorization/correction code unchanged. Full native71 passes edit, selected-festival, read-only/reset restrictions and explicit archived corrections.
3. Shared helper reads BandDetailItem, never Intent music extras or repositories. Screen-owned context queries, per-member history, rotation/back and pending-state native assertions pass; explicit newline checks prevent literal backslash-n rendering.
4. Full post-extraction native suite71/API36 passes with zero skips/failures. Existing Metal styling/navigation retained; no 3.0 implementation.
5–7. Ownership stays Android-side; final plan, impacts, tests and risks recorded.

## How to test
### Automated tests
Pre-extraction sh scripts/verify-android-smoke.sh emulator-5560 be.wacken.planner.CriticalFlowsAndroidTest passed 11/11. Post-extraction sh scripts/verify-android-smoke.sh emulator-5560 all passed 71/71 on isolated wacken235native Android16/API36 arm64; zero native skips/failures. This includes unknown links, canonical context/history, group stars, actual PNG/invalid images, recreation/back, active rating/reset and archived corrections.
JDK21 ./gradlew :app:testDebugUnitTest :app:lintDebug :application:test qaTest passed; Android JVM264, 248 passed/16 existing disposable API cases skipped. Application168 and BDD3/coverage gates remain green. One archived-detail source matcher was replaced by actual native rendered/action/image assertions; archived list source checks unchanged. git diff --check passed.
Groma6.6/Java0.2.2 rescan after helper combine: created0/refreshed111/findings92; analysis diagnostics are not compiler errors. Helper is owned by existing banddetailactivity, image owner separate.
Fresh signed build after native run: deleted prior APK; JDK21 assembleRelease --rerun-tasks passed (53 executed), v1/v2 signature verified. SHA256 d36cf8786f68dab47a96d4ace6ae0298bd85fd881dabf7e4d17267a9ed22915e at app/build/outputs/apk/release/app-release.apk. Existing stable key/version2.34/code42; no publication.
### Manual validation
Reviewed extraction against old widgets: same colors, dimensions/padding, text sizes/gravity, narrow layout predicate, labels, home/back descriptions and canonical link callbacks. Context data and editable/correction sections remain in Activities; presentation receives already constructed views, not domain policies. Initial expected-label test error and literal newline escaping were corrected without changing the intended baseline. Native examples verify current content/action availability rather than source method names.

## TDD / BDD / approval-test evidence
Reviewed/accepted current released content/actions as characterization baseline; no new product/design approval inferred. Native11 baseline passed before extraction. Following extraction, identical assertions plus full native71, existing domain/application/BDD and unit tests pass. Intentional implementation-only change removes duplication; no behavior change planned or observed.

## Architecture impact
- Architecture-significant change: no; private view construction within existing Android adapter/component, no new framework/module/port/runtime/schema/domain/auth ownership.
- Approval received: not required.
- ADR: ADR impact: none, because architecture decisions and public contracts are unchanged.

## README impact
README impact: none, because public behavior, setup, commands, configuration and module/runtime boundaries are unchanged.

## Business requirements impact
Business requirements impact: none, because rating roles, active/archive restrictions, styling and navigation are preserved.

## Diagram impact
Diagram impact: updated Groma source ownership to include shared presentation in existing Band detail screens responsibility; existing lifecycle-safe image collaboration retained and no new island created.

## Commits / logical change list
Pre-extraction native content/action baseline; shared read-only widgets; two Activity delegations; replacement of source matcher; Groma ownership; finalized validation record.

## Risks and follow-up
API36 native passed; API23 native, disposable real database and final signed-upgrade gates remain TASK-241. Two unused private layout predicates remain for small-helper review in TASK-240; no forced global utilities. Groma container repair remains TASK-227. No merge/tag/publication.
<!-- SECTION:NOTES:END -->

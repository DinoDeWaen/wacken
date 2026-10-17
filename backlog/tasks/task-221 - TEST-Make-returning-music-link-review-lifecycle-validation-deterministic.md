---
id: TASK-221
title: 'TEST: Make returning music-link review lifecycle validation deterministic'
status: Done
assignee:
  - '@codex'
created_date: '2026-10-17 14:41'
updated_date: '2026-10-17 14:49'
labels:
  - testing
  - music-links
  - release-2.34
milestone: m-0
dependencies:
  - TASK-202
references:
  - banddetailactivity
modified_files:
  - app/src/androidTest/java/be/wacken/planner/MusicLinksReviewAndroidTest.java
  - app/src/main/java/be/wacken/planner/BandDetailActivity.java
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a member, I need approved music links to be available when I return to band detail, with reliable automated coverage. PR CI run39041198765 intermittently failed returningFromReviewRefreshesNewlySavedProfileOnBandDetail while the same-source other CI run passed. Inspect actual resume/refresh ordering, reproduce with a lifecycle-aware native regression, and correct production behavior if needed or synchronize the fixture with the real resumed state. Preserve explicit approval and canonical metadata. No unrelated redesign or provider changes.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given a link saved during review, when the user returns and band detail is resumed, then the saved channel action is available without reopening the band.
- [x] #2 Given asynchronous Android finish/resume ordering, then the regression waits for actual lifecycle completion and still fails when refresh behavior is removed; no arbitrary fixed delay or retry-to-pass.
- [x] #3 Native and relevant JVM tests pass; final-source GitHub Android validation is required before release.
- [x] #4 README/business requirements/ADR impacts and complete validation package are recorded with canonical wording.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Confirmed finish()/idle versus resume ordering race; added actual RESUMED+visible-link bounded condition and initial absence. Focused run passed; deliberate removal of refresh failed. Restored production source and full39 native tests passed. Full JVM/QA/coverage, lint-vital and fresh signed task APK passed; signature verified. No production/architecture changes. Canonical no-impact notes recorded. Final GitHub source validation remains214, registry blocker222.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Made return-from-music-review validation observe actual parent RESUMED lifecycle and the newly saved channel action. The old fixture assumed finish()+waitForIdleSync completed resume; CI disproved that ordering assumption. Production onResume refresh was already synchronous and is unchanged. Initial absence is asserted.

## Acceptance criteria validation

1. Focused native return flow passes and the saved link appears without reopening.
2. Temporarily suppressing onResume refresh made the same test fail at its bounded wait; restored source has no production diff. No fixed delay/retry-to-pass added.
3. Full native suite:39 passed,0 skipped/failures. Full JVM/QA/coverage and fresh signed task APK build pass. Final-source GitHub validation remains mandatory214; image-registry failures tracked222.
4. Canonical impacts and evidence below.

## How to test

### Automated tests

Run sh scripts/verify-android-smoke.sh emulator-5554 all. Focused #returningFromReviewRefreshesNewlySavedProfileOnBandDetail also passed. Mutation run intentionally failed with refresh disabled; source restored before full39-test pass.

Fresh :app:clean test qaTest :app:lintVitalRelease :app:assembleRelease passed. APK SHA256 acd6bee36f647a696d0a684f8d79ab6fde347a72e807f784b82dd13f7a3f59ff; certificate SHA256 ede2ae97735a305900e87bc0fdc021c03382db4e30435f2349a96a3fdcc7545f verified. Task-validation artifact retains2.33.5/code41, not published2.34. git diff --check passed.

### Manual validation

Actual Android lifecycle/UI exercised by instrumentation; no separate human acceptance claimed.

## TDD / BDD / approval-test evidence

CI failure39041198765 is the original regression evidence. Bounded lifecycle acceptance passes; removing actual refresh makes it fail. Production behavior restored and full suite green.

## Architecture impact

Architecture-significant change: no. No ports/dependencies/persistence/public behavior changed. ADR impact: none, because this corrects asynchronous test synchronization only.

## README impact

README impact: none, because behavior, setup and existing test commands are unchanged.

## Business requirements impact

Business requirements impact: none, because saved-link return behavior was already required and remains unchanged.

## Diagram impact

Diagram impact: none, because architecture is unchanged.

## Commits / logical change list

MusicLinksReviewAndroidTest lifecycle synchronization; task validation record. BandDetailActivity listed as temporarily mutated during verification, restored with no final diff.

## Risks and follow-up

Run final-source CI before214 publication. Repeated ECR download limits are tracked222; all gates remain enforced. No merge/tag/publication.
<!-- SECTION:NOTES:END -->

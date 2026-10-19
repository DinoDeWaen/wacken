---
id: TASK-194
title: 'REL: Publish V2.33.2 festival cleanup hotfix'
status: Done
assignee:
  - '@codex'
created_date: '2026-10-16 05:57'
updated_date: '2026-10-19 08:16'
labels: []
milestone: m-1
dependencies:
  - TASK-192
  - TASK-193
modified_files:
  - app/build.gradle
  - README.md
  - releases/v2.33.2.md
priority: high
type: chore
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Deliver the approved pending-rating discard and festival deletion work as a signed Android update over V2.33.1. Scope: versionCode 38, versionName 2.33.2, release notes, full validation, signed APK, tag and GitHub release. Do not publish until TASK-192 and TASK-193 are complete or explicitly accepted for release.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given a V2.33.1 installation, when V2.33.2 is installed with the stable signing key, then Android accepts the update without clearing app data.
- [x] #2 The official release APK is signed, has versionCode 38 and versionName 2.33.2, and passes the required validation.
- [x] #3 Release notes and README document both changes, deletion consequences, installation, and known risks.
- [x] #4 The V2.33.2 tag and GitHub release contain the verified signed APK and validation evidence.
- [x] #5 Business requirements, ADR, and diagram impacts are recorded.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Completed versionCode 38/versionName 2.33.2, README and release notes.
2. Completed JDK 21 unit/QA/database/SQL validation, stable signing, APK metadata/hash verification and release lint-vital.
3. Completed stable-key in-place V2.33.1 emulator upgrade and launch.
4. Published tag v2.33.2 and signed APK on GitHub on 2026-10-16.
5. On 2026-10-19 verified the live published asset digest and source tag, then reconciled stale task status and acceptance checkboxes.
Deviation: tracking closure was missed at publication; no new implementation, tests or deployment for reconciliation. Underlying architecture approval remains ADR 0013.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary
Published V2.33.2 on 2026-10-16 at 08:34 UTC. The task status and acceptance checkboxes were left stale after publication; reconciled on 2026-10-19 against the live GitHub release and existing validation records.

Release: https://github.com/DinoDeWaen/wacken/releases/tag/v2.33.2

## Acceptance criteria validation
1. Recorded emulator verification: adb install -r over the published V2.33.1 APK succeeded, retained firstInstallTime and launched the upgraded app without clearing storage.
2. Recorded JDK 21 build, release lint-vital and apksigner v1/v2 checks passed. Package be.wacken.planner, versionCode 38, versionName 2.33.2, minSdk 23, targetSdk 36. Stable certificate SHA-256: ede2ae97735a305900e87bc0fdc021c03382db4e30435f2349a96a3fdcc7545f.
3. README and releases/v2.33.2.md document discard, festival deletion, data preservation, installation and risks.
4. Live GitHub release is published (not draft), with uploaded app-release.apk. Its GitHub asset digest exactly matches the originally verified SHA-256: 2d846f269d5d209c1fb37515424e8804dbbae72bbf36dc79545555dcd8bb8175. Tag v2.33.2 resolves to release commit 095583a.
5. Underlying behavior and architecture were documented by completed TASK-192 and TASK-193, BR-105b and approved ADR 0013.

## How to test
### Automated tests
Historical release validation recorded in releases/v2.33.2.md: full unit and QA suites, four Android database tests, and transactional V011 SQL authorization/cascade/history checks passed. Release lint-vital passed. These historical tests were not rerun for this status-only correction.

### Manual validation
Original stable-key APK upgrade and launch passed on the emulator; the affected physical phone was not attached. Reconciliation used gh release view v2.33.2 to confirm publication, asset and digest, git log v2.33.2 to confirm the source commit, and read the release notes and task evidence.

## TDD / BDD / approval-test evidence
Feature regression and acceptance evidence belongs to completed TASK-192 and TASK-193. This reconciliation changes tracking only and introduces no behavior requiring additional tests.

## Architecture impact
- Architecture-significant change: none in release packaging or this reconciliation.
- Approval received: TASK-193 standard design approved on 2026-10-16 and recorded in ADR 0013.
- ADR impact: none, because the release packages the already approved design in ADR 0013.

## README impact
README impact: updated release link and documented the delivered discard/deletion behavior in the original release; no further change for status reconciliation.

## Business requirements impact
Business requirements impact: none, because the release packages the already documented TASK-192/TASK-193 behavior, including BR-105b, without adding business rules.

## Diagram impact
Diagram impact: none, because packaging and task-status reconciliation do not change the architecture documented by TASK-193.

## Commits / logical change list
- e56a706: exact pending-rating discard recovery.
- b3f25b2: festival-owned schedule deletion with shared-band/history preservation.
- 095583a: V2.33.2 metadata, README and release notes.
- Reconciliation: close stale TASK-194 with checked criteria and verified publication evidence.

## Risks and follow-up
Historical release limitations remain recorded in releases/v2.33.2.md: debug lint reported 14 existing NewApi errors, confirmed deletion/discard have no undo, and historical installations signed with a missing older key cannot upgrade in place. This task does not claim those historical limitations were absent. No new APK or deployment was necessary.
<!-- SECTION:NOTES:END -->

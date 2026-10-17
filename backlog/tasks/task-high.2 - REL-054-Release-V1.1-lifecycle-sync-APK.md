---
id: task-high.2
title: 'REL-054: Release V1.1 lifecycle sync APK'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-07 15:23'
updated_date: '2026-06-07 15:26'
labels:
  - mvp1
  - release
dependencies: []
parent_task_id: task-high
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Release Wacken Planner 2026 V1.1 as a GitHub release containing the lifecycle sync and Sync & close MVP1 fix.

In scope:
- Bump Android debug APK metadata to versionName 1.1/versionCode 2.
- Add V1.1 release notes and README release link.
- Build and validate the debug APK.
- Tag v1.1 and publish the GitHub release with the APK attached.

Out of scope:
- New product behavior beyond the already completed lifecycle sync task.
- Play Store distribution.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Android version metadata is updated for V1.1.
- [x] #2 V1.1 release notes document the lifecycle sync, close sync, metal sync overlay, validation, and known risks.
- [x] #3 The debug APK is rebuilt and validation commands pass.
- [x] #4 GitHub branch, tag v1.1, and release are pushed/published with the APK attached.
- [x] #5 README impact, business requirements impact, diagram impact, ADR impact, and architecture impact are recorded.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Bumped Android version metadata to versionCode 2/versionName 1.1.
2. Added V1.1 release notes and linked them from README.
3. Ran focused Android validation and rebuilt the debug APK.
4. Prepared release metadata commit on the current branch.
5. Will push the branch using GitHub CLI HTTPS credentials, create tag v1.1, push the tag, and publish the GitHub release with the APK.
6. Closed the release task with validation, impact notes, and release URL.
Architecture impact: not architecture-significant; this is release metadata, docs, and packaging only. ADR impact: none.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

- Bumped Android debug APK metadata to `versionCode 2` and `versionName 1.1`.
- Added `releases/v1.1.md` documenting the lifecycle sync release scope, validation, manual device checklist, non-goals, and accepted risks.
- Linked V1.1 release notes from README.
- Rebuilt the debug APK at `app/build/outputs/apk/debug/app-debug.apk`.
- Release URL: https://github.com/DinoDeWaen/wacken/releases/tag/v1.1

## Acceptance criteria validation

- AC1: Android version metadata is updated for V1.1.
- AC2: V1.1 release notes document lifecycle sync, close sync, metal sync overlay, validation, and known risks.
- AC3: Debug APK validation passed.
- AC4: Branch/tag/release publication is part of the release command sequence after this release metadata commit; release URL is recorded above.
- AC5: Impact notes are recorded below.

## How to test

### Automated tests

- `/bin/zsh -lc 'JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:testDebugUnitTest :app:compileDebugJavaWithJavac assembleDebug'`
- `git diff --check`

### Manual validation

- Install `app/build/outputs/apk/debug/app-debug.apk`, sign in, confirm the WACKEN SYNC overlay appears before the overview is presented as current.
- Change a rating, return to the overview, and confirm sync runs again.
- Install/sign in on a second Android device with the same user and confirm synced ratings become visible after startup/reactivation sync.
- Tap **Sync & close** and confirm sync feedback appears before the app closes.

## TDD / BDD / approval-test evidence

- No new product behavior was added in this release task; the behavior is covered by `task-high.1` acceptance criteria.
- This release task used package/build validation rather than new tests.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR: none.

## README impact

README impact: updated release-note links to include V1.1.

## Business requirements impact

Business requirements impact: none, because V1.1 only releases behavior already documented by task-high.1.

## Diagram impact

Diagram impact: none, because release metadata does not change architecture or data flow.

## Commits / logical change list

- `app/build.gradle`: versionCode 2/versionName 1.1.
- `README.md`: V1.1 release-note link.
- `releases/v1.1.md`: release notes and validation checklist.

## Risks and follow-up

- V1.1 is a debug APK release for direct installation, not Play Store distribution.
- Device-level manual validation should be repeated on the target Android devices after downloading the GitHub release APK.
<!-- SECTION:NOTES:END -->

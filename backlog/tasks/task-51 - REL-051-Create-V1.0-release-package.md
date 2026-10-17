---
id: task-51
title: 'REL-051: Create V1.0 release package'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-04 18:51'
updated_date: '2026-06-04 20:33'
labels:
  - mvp1
  - release
  - v1.0
dependencies:
  - task-49
  - task-50
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## Business value

After MVP1 is validated, the project needs a clear V1.0 release package so the APK, release notes, validation evidence, and known limitations can be handed over consistently.

## User story

As the app owner, I want a V1.0 release package, so that MVP1 can be installed, reviewed, and shared as a concrete release rather than an informal build artifact.

## Scope

In scope:
- Confirm the Android version label/version name for V1.0 is correct or document why no code change is needed.
- Produce or identify the V1.0 debug APK artifact.
- Prepare release notes summarizing MVP1 scope, validation evidence, known non-goals, and any accepted risks.
- Create the V1.0 Git/release artifact if that is part of the repository workflow.

Out of scope:
- Adding new product features.
- Fixing release-blocking defects discovered by UAT or validation; those require separate defect tasks before this task can close.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given MVP1 UAT and final validation are complete, when the V1.0 release package is prepared, then the debug APK artifact path and build provenance are recorded.
- [x] #2 Given V1.0 is being released, when app version metadata is inspected, then it is set to V1.0 or the reason no metadata change is required is recorded.
- [x] #3 Given release notes are prepared, when they are reviewed, then they summarize MVP1 scope, validation evidence, backend readiness, known non-goals, and accepted risks.
- [x] #4 Given the repository release workflow requires Git tagging or a GitHub release, when the package is created, then the V1.0 tag/release is created or the reason it was not created is recorded.
- [x] #5 README impact, business requirements impact, diagram impact, and ADR impact are recorded using the canonical wording from delivery-governance.md.
- [x] #6 The task is set to Done only after release-blocking defects from UAT or validation are resolved or explicitly accepted.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Updated Android release metadata to versionName 1.0.
2. Added releases/v1.0.md with MVP1 scope, validation evidence, backend readiness, known non-goals, accepted risks, APK path, and version metadata.
3. Linked the V1.0 release notes from README.
4. Rebuilt app/build/outputs/apk/debug/app-debug.apk and verified packaged versionName 1.0 with aapt.
5. Committed the release package, pushed the branch and v1.0 tag, and created the GitHub release with app-debug.apk attached.

Architecture impact: not architecture-significant. No approval or ADR required.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Created the Wacken Planner 2026 V1.0 release package. The Android app now reports versionName 1.0, release notes live in releases/v1.0.md, README links to the release notes, and the GitHub release is published with the debug APK attached.

Release URL: https://github.com/DinoDeWaen/wacken/releases/tag/v1.0

## Acceptance criteria validation

- AC1: Passed. Debug APK path and provenance are recorded: app/build/outputs/apk/debug/app-debug.apk, commit 4b7cf5275055a9ef51ebb839b3e3b142278dbb5e before final task-note amend, SHA-256 53e9f3c6cb78b0c4e0587b3fa1dc493e33f79cee735ac75278349156a5e31d92.
- AC2: Passed. Android metadata is versionCode 1 and versionName 1.0.
- AC3: Passed. releases/v1.0.md summarizes MVP1 scope, validation evidence, backend readiness, known non-goals, and accepted risks.
- AC4: Passed. Git tag v1.0 was created and pushed; GitHub release v1.0 was created with app-debug.apk attached.
- AC5: Impact notes recorded below.
- AC6: Passed. UAT task-49, validation task-50, and defect task-52 are Done; no release-blocking defects remain known.

## How to test

### Automated tests

- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew assembleDebug
- /Users/dino/Library/Android/sdk/build-tools/36.1.0-rc1/aapt dump badging app/build/outputs/apk/debug/app-debug.apk

### Manual / release validation

- Verified APK metadata reports versionName 1.0.
- Verified local APK SHA-256 is 53e9f3c6cb78b0c4e0587b3fa1dc493e33f79cee735ac75278349156a5e31d92.
- Verified GitHub release v1.0 exists and includes app-debug.apk with the same SHA-256 digest.

## TDD / BDD / approval-test evidence

This was release packaging, not new behavior. Full MVP1 UAT and final validation are recorded in task-49 and task-50.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR: none.

## README impact

README impact: updated with a link to releases/v1.0.md so the release notes are discoverable from the project entry point.

## Business requirements impact

Business requirements impact: none, because the release package does not change product scope or business rules.

## Diagram impact

Diagram impact: none, because no architecture or workflow diagram changed.

## Commits / logical change list

- Updated app/build.gradle versionName to 1.0.
- Added releases/v1.0.md.
- Linked release notes from README.
- Included task-49, task-50, task-51, and task-52 Backlog.md records.
- Created tag/release v1.0 and attached app-debug.apk.

## Risks and follow-up

- V1.0 is a debug APK release for local installation, not a Play Store release.
- A disposable Supabase UAT user was created during UAT.
- The UAT checklist still has an older empty-state expectation; current release behavior requires Supabase Auth and syncs central master data after sign-in.
<!-- SECTION:NOTES:END -->

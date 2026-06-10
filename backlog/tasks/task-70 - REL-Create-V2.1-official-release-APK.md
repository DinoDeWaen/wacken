---
id: task-70
title: 'REL: Create V2.1 official release APK'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-10 08:20'
updated_date: '2026-06-10 08:31'
labels:
  - release
  - mvp2
  - android
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Release Wacken Planner 2026 V2.1 as a non-debug Android release package after completing the MVP2 UI schedule stories.

In scope:
- Bump Android version metadata to V2.1.
- Build and validate a release APK rather than a debug APK.
- Add V2.1 release notes covering settings navigation, calendar schedule, decision details, and local manual schedule selection.
- Publish a GitHub V2.1 release with the release APK attached.
- Record signing/package limitations clearly.

Out of scope:
- Play Store distribution, shared synced manual schedule choices, and production secret/key management beyond local release packaging.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given all V2.1 stories are complete, when release validation runs, then automated tests and release APK assembly pass.
- [x] #2 Given V2.1 is released, then Android version metadata is bumped to versionCode 4 and versionName 2.1.
- [x] #3 Given this is not a debug release, then the attached APK is built from the release variant and release notes document signing status.
- [x] #4 Given GitHub release v2.1 is published, then the release APK and notes are attached/recorded.
- [x] #5 README, business requirements, diagram, ADR, and architecture impact are recorded using canonical delivery-governance wording.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Confirmed V2.1 implementation stories task-66, task-67, task-68, and task-69 are Done.
2. Received user approval to finalise all stories and do the release; proceeded with local environment/property-driven release signing support.
3. Added release signing config backed by local Gradle properties/environment variables without committing secrets or private keys.
4. Bumped Android metadata to versionCode 4 / versionName 2.1.
5. Added V2.1 release notes and README release link.
6. Generated a local self-signed release keystore outside git, ran full automated tests, assembled the release APK, verified APK signature, published GitHub release v2.1, then closed the task.

Deviation: V2.1 uses a local self-signed release key for direct installation/testing, not Play App Signing. Architecture impact: deployment/signing configuration only; approved by user request to proceed with the release. ADR impact: none.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Finalised Wacken Planner 2026 V2.1 as a non-debug Android release APK. GitHub release `v2.1` is published at https://github.com/DinoDeWaen/wacken/releases/tag/v2.1 with `app-release.apk` attached.

Android metadata is now `versionCode 4` / `versionName 2.1`. The release artifact is `app/build/outputs/apk/release/app-release.apk` and was built from the Android `release` variant with local environment/property-driven signing.

## Acceptance criteria validation

- AC1: V2.1 implementation stories task-66, task-67, task-68, and task-69 are Done. Full automated validation and `assembleRelease` passed.
- AC2: Android metadata was bumped to `versionCode 4` and `versionName 2.1`.
- AC3: The attached APK is `app-release.apk` from the release variant. V2.1 notes document local self-signed release-key status and no Play App Signing custody.
- AC4: GitHub release `v2.1` is published with `app-release.apk` attached. Asset digest reported by GitHub: `sha256:8743c753146005d3451928777f571a437ebe326813c142c5248c86a6961123d8`.
- AC5: README, business requirements, diagram, ADR, and architecture impact are recorded below.

## How to test

### Automated tests

- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac assembleRelease`
- `/Users/dino/Library/Android/sdk/build-tools/36.1.0-rc1/apksigner verify --verbose app/build/outputs/apk/release/app-release.apk`
- `git diff --check`

### Manual validation

- Installed-device UAT was not run from this environment. Use `backlog/docs/mvp2-android-uat-checklist.md` after installing the V2.1 release APK.

## TDD / BDD / approval-test evidence

This release task packages already completed behavior. The V2.1 implementation stories added focused tests for calendar layout, schedule decision detail data, and local manual-selection state. Existing domain/application/app tests cover schedule generation, conflict behavior, sync, invite text, and Android wiring.

## Architecture impact

- Architecture-significant change: yes, limited to Android deployment/signing configuration.
- Approval received: yes; user requested finalising all stories and doing the release after the signing approval request.
- ADR impact: none, because this is standard local Gradle release signing configuration using properties/environment variables, without new infrastructure, schema, API, persistence, dependency, or domain-boundary decisions.

## README impact

README impact: updated release links with V2.1 release notes.

## Business requirements impact

Business requirements impact: none in this release task, because task-69 already updated the manual-selection business rules before this release.

## Diagram impact

Diagram impact: none, because the system structure and data flow did not change.

## Commits / logical change list

- Add property/environment-driven release signing configuration.
- Bump Android version metadata to V2.1.
- Add V2.1 release notes and README link.
- Build signed release APK from `assembleRelease`.
- Publish GitHub release `v2.1` with `app-release.apk`.

## Risks and follow-up

- V2.1 is signed with a locally generated self-signed release key for direct installation/testing. It is not a Play Store release and does not establish Play App Signing custody.
- Installed-device UAT remains to be run on a connected Android device or emulator.
<!-- SECTION:NOTES:END -->

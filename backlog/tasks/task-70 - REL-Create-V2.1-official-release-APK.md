---
id: task-70
title: 'REL: Create V2.1 official release APK'
status: In Progress
assignee:
  - '@codex'
created_date: '2026-06-10 08:20'
updated_date: '2026-06-10 08:29'
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
- [ ] #1 Given all V2.1 stories are complete, when release validation runs, then automated tests and release APK assembly pass.
- [ ] #2 Given V2.1 is released, then Android version metadata is bumped to versionCode 4 and versionName 2.1.
- [ ] #3 Given this is not a debug release, then the attached APK is built from the release variant and release notes document signing status.
- [ ] #4 Given GitHub release v2.1 is published, then the release APK and notes are attached/recorded.
- [ ] #5 README, business requirements, diagram, ADR, and architecture impact are recorded using canonical delivery-governance wording.
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
## Architecture approval pending

Creating an installable non-debug APK requires release signing support. This affects deployment/security configuration and therefore needs explicit approval before implementation continues.

## Progress update

Release signing support, version metadata, README link, and V2.1 release notes are prepared. Full automated validation and `assembleRelease` passed. `apksigner verify --verbose app/build/outputs/apk/release/app-release.apk` verifies the APK using APK Signature Scheme v2 with one signer.
<!-- SECTION:NOTES:END -->

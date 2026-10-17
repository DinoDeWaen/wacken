---
id: task-122
title: 'REL: Publish V2.16 official GitHub release'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-18 19:29'
updated_date: '2026-06-18 19:34'
labels:
  - release
  - android
  - github
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Publish the already validated V2.16 signed release APK as an official GitHub release. The release includes the reset-button label improvement and all V2.15 changes already committed on the current branch.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given V2.16 release metadata is committed, when release packaging runs, then the signed release APK is verified with versionCode 19 and versionName 2.16.
- [x] #2 Given the current branch is ready, when publishing runs, then the branch and tag v2.16 are pushed to GitHub.
- [x] #3 Given the GitHub release is created, when it is viewed, then it contains the signed app-release.apk asset and uses releases/v2.16.md as release notes.
- [x] #4 Release task notes record the release URL, APK SHA-256, validation commands, README/business/diagram/ADR impact, and signing risk.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Verified V2.16 signed APK metadata and checksum.
2. Committed the release tracking task.
3. Pushed branch `codex-task-2-testing-coverage` to GitHub over HTTPS.
4. Created and pushed tag `v2.16`.
5. Published GitHub release `v2.16` with `app-release.apk` and `releases/v2.16.md`.
6. Verified the GitHub release asset, URL, non-draft/non-prerelease status, and SHA-256 digest.
7. Closed the release task with validation evidence.

Deviation: `origin` uses SSH but this environment had no SSH key available, so branch/tag push used the authenticated HTTPS GitHub URL. Architecture impact: not architecture-significant.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Published Wacken Planner 2026 V2.16 as an official GitHub release. The release uses the signed local release APK built from the V2.16 reset-button label change and includes the prior V2.15 UUID label fix.

Release URL: https://github.com/DinoDeWaen/wacken/releases/tag/v2.16
Asset URL: https://github.com/DinoDeWaen/wacken/releases/download/v2.16/app-release.apk

## Acceptance criteria validation

- AC1: Met. Local APK verification confirmed package `be.wacken.planner`, `versionCode 19`, `versionName 2.16`, and APK signing verification passed for v1/v2 schemes.
- AC2: Met. Branch `codex-task-2-testing-coverage` was pushed to GitHub and tag `v2.16` was created and pushed.
- AC3: Met. GitHub release `v2.16` is published, not draft, not prerelease, and contains `app-release.apk` with digest `sha256:ee655593fece35e94bbbb8d3f38411f20bb3f19a01a1c083e10e55ad2ef53394`.
- AC4: Met. Release URL, APK SHA-256, validation commands, and impact notes are recorded here.

## How to test

### Automated tests

Previously run for V2.16 before publication:

- `JAVA_HOME=$(/usr/libexec/java_home -v 21) WACKEN_RELEASE_STORE_FILE=$(security find-generic-password -s WACKEN_RELEASE_STORE_FILE -w) WACKEN_RELEASE_STORE_PASSWORD=$(security find-generic-password -s WACKEN_RELEASE_STORE_PASSWORD -w) WACKEN_RELEASE_KEY_ALIAS=$(security find-generic-password -s WACKEN_RELEASE_KEY_ALIAS -w) WACKEN_RELEASE_KEY_PASSWORD=$(security find-generic-password -s WACKEN_RELEASE_KEY_PASSWORD -w) ./gradlew :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac assembleRelease`
- `/Users/dino/Library/Android/sdk/build-tools/36.0.0/apksigner verify --verbose app/build/outputs/apk/release/app-release.apk`
- `/Users/dino/Library/Android/sdk/build-tools/36.0.0/aapt dump badging app/build/outputs/apk/release/app-release.apk`
- `shasum -a 256 app/build/outputs/apk/release/app-release.apk`
- `git diff --check`

Publication verification run:

- `git push https://github.com/DinoDeWaen/wacken.git codex-task-2-testing-coverage`
- `git tag v2.16`
- `git push https://github.com/DinoDeWaen/wacken.git v2.16`
- `gh release create v2.16 app/build/outputs/apk/release/app-release.apk --title "Wacken Planner 2026 V2.16" --notes-file releases/v2.16.md`
- `gh release view v2.16 --json tagName,name,url,assets,isDraft,isPrerelease`

### Manual validation

Installed-device validation was not run in this environment. Download the GitHub release asset and install it on a device already on the stable V2.9+ signing line, or uninstall once first if the device has an older missing-key build.

## TDD / BDD / approval-test evidence

No new behavior was implemented in this release publication task. It packages and publishes the already validated V2.16 build.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR: none, because this task only publishes an already built signed APK.

## README impact

README impact: none in this release publication task, because V2.16 release-note links were already committed in task-121.

## Business requirements impact

Business requirements impact: none, because release publication does not change product behavior or business rules.

## Diagram impact

Diagram impact: none, because release publication does not change system structure or flows.

## Commits / logical change list

- `586629c Hide UUIDs in group rating labels`
- `b9b583d Rename rating reset button`
- `09432e6 Track V2.16 official release`

## Risks and follow-up

V2.16 uses the stable local V2.9+ release-key line. Devices installed with older APKs signed by the missing temporary key must uninstall once, install V2.16, sign in, and sync from Supabase. Future releases from this stable key should install as normal updates.

APK path: `app/build/outputs/apk/release/app-release.apk`
GitHub release: https://github.com/DinoDeWaen/wacken/releases/tag/v2.16
SHA-256: `ee655593fece35e94bbbb8d3f38411f20bb3f19a01a1c083e10e55ad2ef53394`
<!-- SECTION:NOTES:END -->

---
id: task-105
title: 'DOC: Rebuild release APK after each completed ticket'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-15 07:12'
updated_date: '2026-06-15 07:20'
labels:
  - docs
  - release
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Update the development ways of working so every completed ticket removes the previous local release APK and builds a fresh release APK after validation. Apply the process immediately by deleting the current release APK and rebuilding it from the latest code.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Development ways of working documents that each completed ticket deletes the previous local release APK and rebuilds a fresh release APK.
- [x] #2 The existing local release APK is deleted before rebuilding.
- [x] #3 A fresh release APK is built from the latest committed code.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Updated backlog/docs/development-ways-of-working.md completion checklist with the release APK rebuild rule.
2. Deleted the previous app/build/outputs/apk/release/app-release.apk.
3. Ran assembleRelease once without signing variables and confirmed it only produced app-release-unsigned.apk, then removed that unsigned artifact.
4. Rebuilt with documented keychain-backed release signing variables.
5. Verified the signed app-release.apk exists, records versionCode 14 / versionName 2.11, and passes apksigner v1/v2 verification.

Deviation: the first rebuild intentionally exposed missing signing variables and produced only an unsigned APK; per release-process guidance it was removed and rebuilt signed. Architecture impact: not architecture-significant; no approval or ADR required.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Updated the development ways of working so every completed ticket requires deleting the previous local release APK and building a fresh release APK from the completed ticket state. Applied the new rule immediately.

The old signed release APK was deleted. A first unsigned Gradle output was removed, then a fresh signed release APK was built with the documented keychain-backed release signing values.

Fresh APK: app/build/outputs/apk/release/app-release.apk
SHA-256: 3ca844821dc982197fba0a2dc9378539b8837783813e5e1d999e0903ed1eea3e
Version metadata: versionCode 14, versionName 2.11

## Acceptance criteria validation

- AC1: development-ways-of-working.md now documents deleting the previous local release APK and rebuilding a fresh release APK before task completion.
- AC2: app/build/outputs/apk/release/app-release.apk was deleted before rebuilding.
- AC3: a fresh signed release APK was built from the latest workspace state and verified.

## How to test

### Automated tests

- Not run in this documentation/process task. The current code validation was already green before this process update.

### Build and verification

- rm app/build/outputs/apk/release/app-release.apk
- /bin/zsh -lc "JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew assembleRelease" produced only app-release-unsigned.apk, confirming signing was missing.
- rm app/build/outputs/apk/release/app-release-unsigned.apk
- /bin/zsh -lc "JAVA_HOME=$(/usr/libexec/java_home -v 21) WACKEN_RELEASE_STORE_FILE=$(security find-generic-password -s WACKEN_RELEASE_STORE_FILE -w) WACKEN_RELEASE_STORE_PASSWORD=*** WACKEN_RELEASE_KEY_ALIAS=$(security find-generic-password -s WACKEN_RELEASE_KEY_ALIAS -w) WACKEN_RELEASE_KEY_PASSWORD=*** ./gradlew assembleRelease"
- /Users/dino/Library/Android/sdk/build-tools/36.0.0/apksigner verify --verbose app/build/outputs/apk/release/app-release.apk
- shasum -a 256 app/build/outputs/apk/release/app-release.apk

### Manual validation

- Not installed on device in this task.

## TDD / BDD / approval-test evidence

- Not applicable; documentation/process update plus release artifact rebuild only.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR impact: none, because this does not change architecture, dependencies, persistence, APIs, or module boundaries.

## README impact

README impact: none, because the documented process change belongs in the ways-of-working document, not setup or public app usage.

## Business requirements impact

Business requirements impact: none, because no product behavior or business rule changed.

## Diagram impact

Diagram impact: none, because no architecture or flow changed.

## Commits / logical change list

- Added the local release APK delete-and-rebuild rule to the completion checklist.
- Rebuilt a fresh signed release APK from the latest code.

## Risks and follow-up

- The APK remains versionName 2.11/versionCode 14 because this was a local rebuild, not a new versioned public release.
<!-- SECTION:NOTES:END -->

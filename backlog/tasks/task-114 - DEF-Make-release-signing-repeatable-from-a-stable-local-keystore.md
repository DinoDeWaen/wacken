---
id: task-114
title: 'DEF: Make release signing repeatable from a stable local keystore'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-17 05:01'
updated_date: '2026-06-17 05:41'
labels:
  - release
  - signing
  - android
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The release build currently depends on `WACKEN_RELEASE_STORE_FILE=/private/tmp/wacken-v2.9-release.jks`. That temporary keystore is missing, so signed release APK rebuilds are blocked.

As a maintainer, I need release signing to use a stable local keystore path and a repeatable bootstrap/check process, so the app can be built, released, and updated consistently in future releases.

In scope:
- Add a repeatable local release-signing bootstrap/check script that uses macOS keychain secrets and a stable gitignored keystore path.
- Update release process documentation to use the stable path and explain signing-key rotation impact.
- Generate or verify the local release keystore and update the keychain path so future builds work.
- Rebuild and verify the signed release APK.

Out of scope:
- Recovering the missing original `/private/tmp` keystore if no copy exists.
- Claiming APKs signed with a newly generated key can update installations signed by the missing old key.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given the release keystore is not present in /private/tmp, when the signing bootstrap is run, then it creates or verifies a stable gitignored local keystore path.
- [x] #2 Given future release builds run, when keychain signing secrets are available, then Gradle can build a signed release APK without relying on /private/tmp.
- [x] #3 Given a new signing key is generated because the old key is missing, then documentation clearly states existing installs signed with the old key cannot be updated in place.
- [x] #4 Given the signed APK is rebuilt, then apksigner verification and APK metadata checks pass.
- [x] #5 Automated checks or shell validation prove the script behavior enough for local release use.
- [x] #6 README, release-process, business requirements, ADR, and diagram impact are recorded using canonical delivery-governance wording.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Inspected current release signing configuration, keychain values, release process, and failure mode.
2. Added `.local/` to `.gitignore` and created `scripts/ensure-release-keystore.sh`.
3. Ran the script to generate a stable local release keystore at `.local/release/wacken-release.jks` because the old `/private/tmp` key was missing.
4. Fixed duplicate keychain path entries so `WACKEN_RELEASE_STORE_FILE` resolves to the stable path.
5. Updated README and release-process documentation, including the update limitation for installations signed with the missing old key.
6. Rebuilt and verified the signed release APK.

Architecture impact: not architecture-significant for application architecture; release/signing process only. Signing-key rotation was required because the previous key file was lost.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Release signing no longer depends on `/private/tmp/wacken-v2.9-release.jks`. Added `scripts/ensure-release-keystore.sh`, which reads the existing macOS keychain signing secrets, creates or verifies a stable gitignored keystore at `.local/release/wacken-release.jks`, and updates `WACKEN_RELEASE_STORE_FILE` in keychain.

A new stable local release key was generated because the old temporary keystore file was missing and no copy was found. Future APKs signed with this stable key can update each other. Devices with installs signed by the missing old key must uninstall once, install the new APK, then sync from Supabase.

## Acceptance criteria validation

- AC1: `scripts/ensure-release-keystore.sh` created and verified `.local/release/wacken-release.jks`; `.local/` is gitignored.
- AC2: Keychain `WACKEN_RELEASE_STORE_FILE` now resolves to `/Users/dino/Documents/backlog/wacken/.local/release/wacken-release.jks`, and `assembleRelease` succeeds with keychain-backed values.
- AC3: `backlog/docs/release-process.md` documents the signing-key rotation impact and uninstall/reinstall requirement for old-key installs.
- AC4: Signed APK was rebuilt and verified with `apksigner` and `aapt`.
- AC5: `bash -n scripts/ensure-release-keystore.sh`, script verify run, Gradle release build, APK verification, and `git diff --check` passed.
- AC6: README and release-process impacts are recorded below; business requirements, ADR, and diagram impacts are none.

## How to test

### Automated tests

- `bash -n scripts/ensure-release-keystore.sh`
- `scripts/ensure-release-keystore.sh`
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) WACKEN_RELEASE_STORE_FILE=$(security find-generic-password -s WACKEN_RELEASE_STORE_FILE -w) WACKEN_RELEASE_STORE_PASSWORD=$(security find-generic-password -s WACKEN_RELEASE_STORE_PASSWORD -w) WACKEN_RELEASE_KEY_ALIAS=$(security find-generic-password -s WACKEN_RELEASE_KEY_ALIAS -w) WACKEN_RELEASE_KEY_PASSWORD=$(security find-generic-password -s WACKEN_RELEASE_KEY_PASSWORD -w) ./gradlew assembleRelease`
- `/Users/dino/Library/Android/sdk/build-tools/36.0.0/apksigner verify --verbose app/build/outputs/apk/release/app-release.apk`
- `/Users/dino/Library/Android/sdk/build-tools/36.0.0/aapt dump badging app/build/outputs/apk/release/app-release.apk`
- `shasum -a 256 app/build/outputs/apk/release/app-release.apk`
- `git diff --check`

APK metadata: package `be.wacken.planner`, `versionCode=15`, `versionName=2.12`, minSdk `23`, targetSdk `36`.

APK SHA-256: `237836320da995a70d132070ab99d3e2ef8e4504e1139b98e7a6f570308f608f`.

### Manual validation

No physical-device install was run. Install/update validation should use devices already migrated to the new stable signing key. Devices on the old missing key need one uninstall/reinstall.

## TDD / BDD / approval-test evidence

This is release infrastructure. Validation is shell/script syntax, keychain path verification, signed Gradle release build, and APK signature/metadata verification.

## Architecture impact

- Architecture-significant change: no for app/domain/backend architecture.
- Approval received: user requested making builds and releases possible each time; signing-key rotation was necessary because the old key was missing.
- ADR: none, because this changes local release operations, not app architecture.

## README impact

README impact: updated setup/run guidance with the release keystore bootstrap command.

## Business requirements impact

Business requirements impact: none, because this changes local release operations and signing recovery, not product behavior.

## Diagram impact

Diagram impact: none, because architecture diagrams do not change.

## Commits / logical change list

- Add stable local release keystore bootstrap script.
- Ignore `.local/` local secret-bearing release files.
- Update release-process signing path and recovery instructions.
- Update README with release keystore bootstrap command.

## Risks and follow-up

- APKs signed with the new stable key cannot update installs signed with the missing old key. Those devices must uninstall once and reinstall.
- The keystore file remains local and gitignored; it should be backed up outside the repository to prevent another signing break.
<!-- SECTION:NOTES:END -->

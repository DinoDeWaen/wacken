# Release Process

## Purpose

Use this checklist for every Wacken Planner Android release. The goal is to
produce a repeatable, official non-debug APK release with clear validation,
signing evidence, release notes, Git tag, and GitHub release asset.

## Release Type

Current official Android releases are direct-install `release` variant APKs.
They are not Play Store releases and do not establish Play App Signing custody.

For 2.34 the release owner explicitly chose to retain **signed GitHub APK
distribution** (2026-10-17, TASK-175). This keeps the existing package, signing key
and group install process without requiring a managed testing account. Play
internal testing and Firebase App Distribution are deferred. GitHub does not
automatically install updates; each member downloads and installs the next APK.

### Member install and update

1. Open this repository's official GitHub release page and choose the intended
   version. Download its `app-release.apk`; avoid debug/unsigned artifacts.
2. Compare the downloaded file's SHA-256 with the digest recorded in that release.
   The maintainer also checks its signer against the preceding stable-key APK.
3. On Android, allow installation from the browser/file manager used for this
   download, open the APK and choose Install or Update. Device policy may prohibit
   direct installation; GitHub cannot override it.
4. An update requires package `be.wacken.planner`, the same signing certificate,
   and a higher `versionCode`. Do not uninstall a working stable-key installation
   to update it: Android retains its data during a compatible update.
5. Open the app and confirm cached festivals/ratings remain visible. Sign in/sync
   if needed. Keep local pending ratings protected until sync is confirmed.

The lost older-key exception described below requires a one-time reinstall;
uninstalling clears local data, including unsynced changes. Confirm synced data
and export available local ratings before considering that recovery. A signature
mismatch must be investigated rather than treated as a normal update step.

### Privacy and distribution information

This is a private friend-group planning app. Authentication, shared festival/band
data and member ratings use the configured Supabase project; cached data and
pending operations live on the device. Export/sharing is explicitly triggered by
the user and sends a CSV to the chosen Android destination. Metadata lookup sends
the edited band search term to configured/public music sources and retrieves
official website evidence. These requests expose normal network information to
those services. See the README for provider configuration and credential limits.

GitHub hosts the APK/release notes and processes download requests under its own
policies. There is no Play/Firebase tester enrollment, managed update delivery,
store privacy review or submitted store Data safety form in this channel. Do not
claim store approval. Any future managed track must separately establish account
ownership, tester access, privacy declarations and signing/update continuity.

### Promotion checks

TASK-214 owns publication of 2.34. Publish only after its feature, real API, native,
signed-build and upgrade-preservation gates pass. Compare the previous published
APK and candidate using `apksigner verify --print-certs`; the signer certificate
SHA-256 must match. On an isolated release-test emulator, install the previous
signed APK, seed cached/pending data, install the higher-code candidate with
`adb install -r`, and verify retained data and normal launch. Record the exact
versions and result. Never replace the key or wipe user data to force this gate.
After upload, download the published asset and compare its digest with the local
verified APK before announcing availability. A draft/tag alone is not delivery.

Debug APKs are only acceptable when a task explicitly asks for a debug release.
For normal release requests, create an official signed release APK.

## Required Inputs

Before starting release packaging, confirm:

- The feature or defect task being released is Done or explicitly accepted for
  release.
- The worktree contains no unrelated uncommitted changes.
- The next semantic app version is known.
- Android `versionCode` is one higher than the previous official release.
- Android `versionName` matches the release tag without the `v` prefix.
- The release keystore file exists locally. Run `scripts/ensure-release-keystore.sh`
  before release validation to create or verify the stable local keystore path.
- The release signing passwords are available through local environment
  variables, local Gradle properties, or an approved secret store.
- GitHub CLI authentication can push tags and publish releases.

For the current local release-key line, signing uses the stable gitignored
keystore path managed by `scripts/ensure-release-keystore.sh`:

```text
WACKEN_RELEASE_STORE_FILE=/Users/dino/Documents/backlog/wacken/.local/release/wacken-release.jks
WACKEN_RELEASE_STORE_PASSWORD=<local secret>
WACKEN_RELEASE_KEY_ALIAS=wacken-v2-9
WACKEN_RELEASE_KEY_PASSWORD=<local secret>
```

The current local signing values are stored in the macOS keychain under these
service names:

```text
WACKEN_RELEASE_STORE_FILE
WACKEN_RELEASE_STORE_PASSWORD
WACKEN_RELEASE_KEY_ALIAS
WACKEN_RELEASE_KEY_PASSWORD
```

Never commit keystores, passwords, token files, or generated secret-bearing
configuration.

Important: the original V2.9+ keystore path was
`/private/tmp/wacken-v2.9-release.jks`. That file was temporary and is no longer
available locally. The stable `.local/release/wacken-release.jks` key enables
repeatable future releases from this machine, but APKs signed with this newly
generated key cannot update installations signed by the missing old key. Those
devices must uninstall the old app once, install the new signed APK, and sync
from Supabase. Future APKs signed with this stable key can update each other.

## Backlog Task

Create or update a release task with acceptance criteria for:

- Full release validation.
- Signed release APK verification.
- GitHub release publication.
- README release-note link.
- Business requirements impact.
- ADR and diagram impact.

Set the task to `In Progress`, assign it to `@codex`, and add an
implementation plan through the Backlog.md CLI before editing files.

## Version And Notes

Update:

- `app/build.gradle`: bump `versionCode` and `versionName`.
- `releases/vX.Y.md`: create release notes for the new version.
- `README.md`: add the new release notes link above older release links.

Release notes must include:

- Scope.
- APK path.
- Build command.
- Android version metadata.
- Signing configuration.
- Install/upgrade guidance.
- Validation commands.
- Known non-goals.
- Accepted risks.

## Required festival creation regression gate

Before tagging or publishing, both commands must pass on the exact release source:

```bash
scripts/verify-festival-creation.sh
./gradlew :app:connectedDebugAndroidTest
```

Use JDK 21, Android SDK, Docker, Python 3, curl, and a connected emulator.
The integration runner tests the complete checked-in 63-band Summer Breeze CSV
against disposable real PostgreSQL/PostgREST with repository migrations and RLS.
It verifies rollback, archived identity protection, exact-name collisions,
concurrency, and lost-response retries, and rejects skipped tests.
The Android suite exercises UI progress/failure and transactional Room updates.
A successful build, launch, or isolated authorization query does not replace
these gates. Record their results and any explicit human waiver in the release task.

## Validation Build

Run the full validation and release build with signing variables configured:

```bash
JAVA_HOME=$(/usr/libexec/java_home -v 21) \
WACKEN_RELEASE_STORE_FILE=$(security find-generic-password -s WACKEN_RELEASE_STORE_FILE -w) \
WACKEN_RELEASE_STORE_PASSWORD=<local secret> \
WACKEN_RELEASE_KEY_ALIAS=wacken-v2-9 \
WACKEN_RELEASE_KEY_PASSWORD=<local secret> \
./gradlew :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac assembleRelease
```

The official artifact must be:

```text
app/build/outputs/apk/release/app-release.apk
```

If Gradle produces only:

```text
app/build/outputs/apk/release/app-release-unsigned.apk
```

then release signing was not configured. Stop. Do not tag, publish, rename, or
upload the unsigned APK as an official release.

## APK Verification

Verify signing, metadata, and digest:

```bash
/Users/dino/Library/Android/sdk/build-tools/36.0.0/apksigner verify --verbose app/build/outputs/apk/release/app-release.apk
/Users/dino/Library/Android/sdk/build-tools/36.0.0/aapt dump badging app/build/outputs/apk/release/app-release.apk
shasum -a 256 app/build/outputs/apk/release/app-release.apk
git diff --check
```

Required checks:

- `apksigner` reports verified APK signatures.
- APK Signature Scheme v1 and v2 are enabled.
- Package is `be.wacken.planner`.
- `versionCode` matches the release task.
- `versionName` matches the release task.
- `minSdkVersion` remains `23` unless a task explicitly changes it.
- SHA-256 is recorded in the release task notes.

## Commit, Tag, And Push

Commit the release metadata and notes:

```bash
git add app/build.gradle README.md releases/vX.Y.md backlog/tasks/<release-task>.md
git commit -m "Prepare VX.Y release"
git push
```

Create and push the tag:

```bash
git tag vX.Y
git push origin vX.Y
```

If the branch has not been pushed yet, push the current branch first.

## GitHub Release

Publish the GitHub release with the signed APK:

```bash
gh release create vX.Y app/build/outputs/apk/release/app-release.apk \
  --title "Wacken Planner 2026 VX.Y" \
  --notes-file releases/vX.Y.md
```

Verify the published release:

```bash
gh release view vX.Y
```

The release must contain `app-release.apk`, not an unsigned APK.

## Close The Task

Before setting the release task to Done:

- Check every acceptance criterion through the Backlog.md CLI.
- Record the release URL.
- Record the APK path and SHA-256.
- Record all validation commands.
- Record APK signing and metadata verification.
- Record README, business requirements, diagram, and ADR impact using the
  canonical wording from `delivery-governance.md`.
- Record risks, especially signing-certificate upgrade constraints.

## Signing Failure Recovery

If signing credentials are unavailable:

1. Leave the release task `In Progress`.
2. Record the blocker in task notes.
3. Keep any release metadata edits untagged and unpublished.
4. Run `scripts/ensure-release-keystore.sh` if the keychain secrets exist but
   the keystore path is missing or still points to `/private/tmp`.
5. Ask for the existing release-key passwords or explicit approval to generate
   a new release key when keychain secrets are missing or key rotation has not
   been approved.

Generating a new release key is allowed only with explicit approval because
Android will not install the new APK over previous releases signed with a
different certificate. Users must uninstall the existing app first, then install
the new APK and sync from Supabase.

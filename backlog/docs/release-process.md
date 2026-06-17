# Release Process

## Purpose

Use this checklist for every Wacken Planner Android release. The goal is to
produce a repeatable, official non-debug APK release with clear validation,
signing evidence, release notes, Git tag, and GitHub release asset.

## Release Type

Current official Android releases are direct-install `release` variant APKs.
They are not Play Store releases and do not establish Play App Signing custody.

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

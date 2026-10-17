---
id: TASK-200
title: 'REL: Publish V2.33.5 missing music links hotfix'
status: Done
assignee:
  - '@codex'
created_date: '2026-10-17 07:10'
updated_date: '2026-10-17 07:40'
labels:
  - release
  - android
dependencies:
  - TASK-199
modified_files:
  - app/build.gradle
  - README.md
  - releases/v2.33.5.md
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Deliver completed TASK-199 to phones as an official signed Android update. Unknown YouTube/Spotify links must remain empty after sync, and known links must be preserved. Package and validate only the completed fix; no unrelated behavior, schema or signing-key change.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 The signed APK is 2.33.5 with versionCode 41 and the stable certificate; it updates published V2.33.4 in place with app data retained.
- [x] #2 Clean unit/QA/coverage, all 15 Android tests, the real festival creation API gate, release lint-vital and a fresh signed build pass on the release source.
- [x] #3 APK package, SDK metadata, v1/v2 signature and SHA-256 are verified and recorded.
- [x] #4 Git tag and GitHub release publish the verified signed app-release.apk with release notes, and the downloaded asset matches.
- [x] #5 README links the new notes, canonical business requirements/ADR/diagram impact and complete validation evidence are recorded in the visible Backlog task.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Completed minimal release packaging from TASK-199 in codex/release-2-33-5.
1. Bumped app/build.gradle to 2.33.5/41 and added release notes/README link (1928e82).
2. Verified the stable key. Exact-source real API gate passed all ten scenarios with zero skips and SQL assertions. Clean unit/QA/coverage, all 15 Android tests, release lint-vital and fresh signed build passed.
3. Verified signature, metadata and digest; installed over published V2.33.4, retaining install timestamp/data directory/external data marker, and launched successfully.
4. Tagged validated source 1928e82, published the signed APK and verified the downloaded file byte for byte.
Deviation: release CI's API job was blocked before tests by public ECR Rate exceeded. The same mandatory API gate passed locally; CI Android and unit/QA steps passed. No new architecture, dependency, schema, key, ADR or diagram change; BR-036 already describes the shipped behavior.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Published official signed V2.33.5 (versionCode 41), delivering completed TASK-199. Unknown YouTube/Spotify links stay empty after creation and sync; valid stored links remain. Sync replaces the old cached null values. Published release: https://github.com/DinoDeWaen/wacken/releases/tag/v2.33.5 .

## Acceptance criteria validation

1. The stable-key APK updated the downloaded published V2.33.4 in place. Version 2.33.4/40 became 2.33.5/41; firstInstallTime stayed 2026-10-17 09:31:41 Europe/Brussels, dataDir stayed /data/user/0/be.wacken.planner and the external app data marker task200-preserve-data survived. MainActivity launched successfully to LoginActivity on the test device without a session.
2. Clean unit/QA/coverage, all 15 Android tests with zero failures/skips, release lint-vital and signed build passed (139 tasks, 138 executed). The exact-source real PostgreSQL/PostgREST gate passed ten HTTP scenarios with zero skips plus SQL transaction/permission assertions.
3. Package be.wacken.planner, 2.33.5/41, minSdk23, targetSdk36 verified. APK signatures v1/v2 verified with stable certificate SHA-256 ede2ae97735a305900e87bc0fdc021c03382db4e30435f2349a96a3fdcc7545f.
4. Tag v2.33.5 points to validated release source 1928e82f64fe9724a69e7f33cc72c8fb2b9cb2fa. GitHub release is published, not draft/prerelease, with app-release.apk. Downloaded asset matches the local signed artifact byte for byte.
5. README links releases/v2.33.5.md; notes include scope, install/sync steps, build, signing, validation and limits. All impact statements are below.

## How to test

### Automated tests

- JDK21/SDK36: scripts/verify-festival-creation.sh — passed all ten real API scenarios, zero skips, transactional SQL assertions and permission checks.
- Existing Keychain signing values with JDK21/SDK36: ./gradlew clean test qaTest :app:connectedDebugAndroidTest :app:lintVitalRelease assembleRelease --no-daemon — passed.
- apksigner verify --verbose --print-certs, aapt dump badging, shasum -a 256, git diff --check and cmp of downloaded/local APK all passed.
- Artifact: /Users/dino/.codex/worktrees/fix-festival-creation/wacken/app/build/outputs/apk/release/app-release.apk.
- SHA-256: a94c393f45b9f617e99cec2fbf6f0a6d33c2d91dde2122381872465761549f3a.
- CI Android job passed: https://github.com/DinoDeWaen/wacken/actions/runs/39013048683 . Its API job was blocked before tests by public ECR image-download Rate exceeded (exit125). The same mandatory gate passed locally on the exact release source; no passing overall CI run is claimed. Unit/QA/coverage steps also passed in CI.
- Completed fix CI passed before packaging: https://github.com/DinoDeWaen/wacken/actions/runs/39012327244 .

### Manual validation

The signed in-place update and successful launch were observed on the emulator. Install the attached release APK over V2.33.4, use Sync from Supabase, and reopen Abbie Falls: unknown YouTube/Spotify actions should be absent. Bands with stored links should keep them.
The real BandDetailActivity, native JSON and Room behavior were exercised in the 15-test suite. The original phone/external YouTube app and real signed-in account were not used for the upgrade smoke check. Internal pending-edit preservation is covered by the existing Room tests; the upgrade smoke directly checks install identity and the external app data marker.

## TDD / BDD / approval-test evidence

Release packaging changes version metadata and documentation only. TASK-199's native red baseline d83607e reproduced three failures before correction; its five new cases plus the ten existing Android cases passed on this release source. No implementation-mirroring metadata tests were added.

## Architecture impact

- Architecture-significant change: none in packaging; delivers the already documented adapter correction.
- Approval received: user requested the new release.
- ADR impact: none, because runtime boundaries and design decisions are unchanged.

## README impact

README impact: updated the release-note index with V2.33.5.

## Business requirements impact

Business requirements impact: none, because TASK-199 already documents empty-link behavior in BR-036 and this task only packages it.

## Diagram impact

Diagram impact: none, because packaging changes no runtime flow or boundary.

## Commits / logical change list

- fb5f09f: release task plan.
- 1928e82: 2.33.5/41 metadata, README and release notes.
- Publication evidence committed separately after tagging.

## Risks and follow-up

No new schema, metadata discovery, key rotation or Play Store distribution. A successful sync is required to clear old cached bogus links after updating. Existing stored channel availability is not checked. Existing full lintDebug NewApi errors and Supabase advisor findings are unchanged; release lint-vital passed.
Current stable-key releases update in place. Installations signed with the lost older key require protecting local-only data before a one-time reinstall.

## Modified files

app/build.gradle, README.md, releases/v2.33.5.md
<!-- SECTION:NOTES:END -->

## Final Summary

<!-- SECTION:FINAL_SUMMARY:BEGIN -->
Published V2.33.5/41: https://github.com/DinoDeWaen/wacken/releases/tag/v2.33.5 . Signed/downloaded APK SHA-256 a94c393f45b9f617e99cec2fbf6f0a6d33c2d91dde2122381872465761549f3a matches. Clean build, ten real API and fifteen Android tests passed; in-place upgrade/launch passed. CI Android passed; API CI was registry rate-limited before tests, with exact-source local gate passed. Install over V2.33.4 and sync to clear unknown music links.
<!-- SECTION:FINAL_SUMMARY:END -->

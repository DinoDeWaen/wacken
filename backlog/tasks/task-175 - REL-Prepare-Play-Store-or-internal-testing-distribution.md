---
id: task-175
title: 'REL: Prepare signed GitHub APK distribution for 2.34'
status: Done
assignee:
  - '@codex'
created_date: '2026-08-19 11:03'
updated_date: '2026-10-17 11:53'
labels:
  - future
  - release
  - distribution
  - android
milestone: m-0
dependencies: []
modified_files:
  - backlog/docs/release-process.md
  - README.md
priority: low
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a release owner, I want a documented signed GitHub APK distribution path so that group members can install verified builds and update with the stable package and certificate. User decision 2026-10-17: keep signed GitHub APK distribution for 2.34. In scope: stable signing/versioning, digest and asset checks, install/upgrade steps, promotion checklist, and privacy/distribution notes. Managed Play/Firebase distribution is explicitly deferred; no accounts, projects or store publication are claimed. TASK-214 owns final 2.34 release publication and actual upgrade validation.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given a distribution path is selected, then the project documents why it was chosen and how releases are promoted.
- [x] #2 Given an official release is built, then signing, package identity, and versioning support install and update on Android devices.
- [x] #3 Release process documentation is updated so future releases follow the same steps.
- [x] #4 README, business requirements, diagram, and ADR impact are recorded using delivery-governance wording when implemented.
- [x] #5 Given signed GitHub APK distribution is used, then users can find privacy/data-handling notes and understand that no Play/Firebase managed track or store data-safety declaration is provided.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Completed: honored the explicit GitHub APK decision; documented rationale, privacy/data handling, install/upgrade/promotion checks; downloaded and digest-verified published2.33.5; compared its stable signing certificate with a fresh signed task APK; passed existing unit/QA/coverage/release-lint/build checks. No architecture/runtime change. Final2.34 versioning, actual upgrade preservation, asset upload/download verification and publication remain mandatory TASK-214 gates.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Kept signed GitHub APK distribution for2.34 following the user's explicit2026-10-17 decision. Renamed/reframed this preparation task accordingly; managed Play/Firebase setup is deferred, not reported as delivered. Documented choice, release promotion, digest/certificate checks, manual Android update steps, local-data precautions and privacy/data flows. TASK-214 owns the actual2.34 version increment, upgrade-preservation test and publication.

## Acceptance criteria validation

1,3: Release process explains the selected existing channel, rationale and repeatable promotion checks.
2: Downloaded the official v2.33.5 APK and verified its published SHA-256. Its signing certificate matches the freshly built task APK; package remains be.wacken.planner and baseline code41/version2.33.5. Documented the required higher-code/same-signer update rule. Actual2.34 upgrade proof is a mandatory TASK-214 gate.
4,5: README entry and privacy/distribution notes accurately distinguish direct installation from managed tracks; impacts below.

## How to test

### Automated tests

Existing Keychain/JDK21: ./gradlew :app:clean test qaTest :app:lintVitalRelease :app:assembleRelease passed (fresh signed APK, previous output removed by app:clean). Unit/QA/coverage and release lint-vital passed; ten existing API-fixture app tests skip without Docker, to be exercised by final release gate. No runtime code changed.
apksigner verification of downloaded baseline and rebuilt APK passed; matching certificate SHA-256 ede2ae97735a305900e87bc0fdc021c03382db4e30435f2349a96a3fdcc7545f.
Downloaded v2.33.5 digest matches GitHub: a94c393f45b9f617e99cec2fbf6f0a6d33c2d91dde2122381872465761549f3a.
Fresh task APK digest: 54e5642636a853f8c84b702b2ba34c8b8e085ceea8e2e1ca1475dfa941253a1d.
git diff --check passed.

### Manual validation

Reviewed install/update/privacy instructions against current README, release workflow and official asset metadata. Follow the documented isolated-emulator previous-APK → higher-code candidate upgrade and download-after-publication checks during TASK-214. No physical-device install or2.34 upgrade is claimed here.

## TDD / BDD / approval-test evidence

Documentation-only preparation and artifact compatibility checks; no new runtime behavior or redundant unit tests. Existing signed build/test gates were executed.

## Architecture impact

Architecture-significant change: none; retains existing signed GitHub channel and certificate. User approved this distribution choice explicitly. No account, dependency, signing rotation or deployment integration added.

## README impact

README impact: updated install/update entry and link to distribution/privacy guidance.

## Business requirements impact

Business requirements impact: none, because app capabilities and data workflows are unchanged; distribution retains the existing direct-install channel.

## Diagram impact

Diagram impact: none, because system/container relationships and deployment channel are unchanged.
ADR impact: none, because this retains the existing release architecture.

## Commits / logical change list

Distribution decision and scope update; release-process guidance; README entry; signed artifact comparison.

## Risks and follow-up

Manual updates require members to download/install each APK; device policies can prevent sideloading. Old lost-key installs cannot be updated in place; protect unsynced data before exceptional reinstall. TASK-214 must pass exact-source feature/API/native/upgrade/publication checks before2.34 is announced. No Play data-safety submission or Firebase/Play tester delivery is claimed.
<!-- SECTION:NOTES:END -->

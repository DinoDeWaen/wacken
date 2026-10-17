---
id: task-135
title: 'REL: Publish V2.19 official GitHub release'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-19 19:09'
updated_date: '2026-06-19 19:16'
labels:
  - release
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a Wacken Planner user, I need the latest UX refinements delivered as a signed official APK and GitHub release so I can install a verified version.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 A signed V2.19 release APK is built from the current committed worktree and passes the full validation suite
- [x] #2 APK signing, package metadata, versionCode 22, versionName 2.19, and SHA-256 are verified and recorded
- [x] #3 README links V2.19 release notes and the release notes document scope, installation guidance, validation, and accepted risks
- [x] #4 Git tag v2.19 and a GitHub release containing the signed APK are published
- [x] #5 Business requirements, diagram, and ADR impact are recorded using the canonical wording from delivery-governance.md
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Verified clean release input, Gradle/JDK 21, GitHub authentication, and the stable local keystore.
2. Updated Android version metadata to versionCode 22/versionName 2.19; added V2.19 release notes and the README link.
3. Deleted the prior local release APK and ran the full signed validation/build.
4. Verified v1/v2 APK signing, package metadata, digest, and git diff hygiene.
5. Committed the release metadata, pushed the branch through authenticated HTTPS after the configured SSH remote was unavailable, tagged v2.19, and published the GitHub release with the signed APK.
6. Recorded validation, publication URL, and impact notes; checked acceptance criteria.

Test strategy completed: full domain, application, infrastructure, and Android unit-test validation plus signed APK verification.
Architecture impact: not architecture-significant; no module, persistence, API, dependency, or business-rule changes.
Treatment: standard release packaging. Legacy behavior: no behavior refactor.
Deviation: the configured SSH Git remote could not authenticate on this machine, so the existing authenticated GitHub CLI HTTPS credentials were used for push and release publication.
README impact: added the V2.19 release-notes link. Business requirements, diagram, and ADR impact: none, because this packages existing completed behavior only.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

- Published the official signed Wacken Planner 2026 V2.19 Android release.
- Included clearer offline and pending-sync feedback, structured band detail panels, schedule key and filter wording, and clearer settings and import feedback.
- GitHub release: https://github.com/DinoDeWaen/wacken/releases/tag/v2.19
- APK asset: https://github.com/DinoDeWaen/wacken/releases/download/v2.19/app-release.apk

## Acceptance criteria validation

- AC1: Passed full signed validation and produced the official release APK.
- AC2: Verified v1 and v2 signatures, package be.wacken.planner, versionCode 22, versionName 2.19, minSdk 23, and SHA-256 9ddb443323299b4965f834fcd7051ece1f2891f0a6098ec44e02054aea2be730.
- AC3: Added releases/v2.19.md and linked it from README.
- AC4: Published tag v2.19 and the GitHub release with app-release.apk.
- AC5: Recorded all required impact notes below.

## How to test

### Automated tests

- Full signed validation: domain, application, infrastructure, and Android unit tests plus assembleRelease.
- APK verification: apksigner signature verification, aapt package metadata verification, SHA-256 verification, and git diff check.

### Manual validation

- Open the GitHub V2.19 release and confirm the uploaded asset is app-release.apk.
- Install the signed APK on an Android device already using the stable V2.9+ signing-key line, then open the band detail, schedule, and settings screens.

## TDD / BDD / approval-test evidence

- Release packaging contains no product behavior change. Existing focused unit tests from the completed UX tasks were included in the full validation suite.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR impact: none, because this packages existing completed behavior only.

## README impact

- README impact: updated the V2.19 release-notes link.

## Business requirements impact

- Business requirements impact: none, because this packages existing completed behavior only.

## Diagram impact

- Diagram impact: none, because this packages existing completed behavior only.

## Commits / logical change list

- 2747d13 Prepare V2.19 release.

## Risks and follow-up

- The configured SSH Git remote could not authenticate on this machine. The local remote was switched to the existing GitHub CLI-authenticated HTTPS URL for publication.
- Devices using an APK signed before the stable V2.9+ key must uninstall once before installing V2.19; future APKs signed with the stable key update normally.
- task-117 remains open for backend verification of the optional group-schedule-lock schema.
<!-- SECTION:NOTES:END -->

---
id: task-100
title: 'DEF: Diagnose V2.11 APK install failure'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-12 16:05'
updated_date: '2026-06-15 05:07'
labels:
  - defect
  - release
  - android
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
V2.11 APK install reports App not installed even after deleting the app first. Diagnose the release package and installation constraints before changing code or publishing a replacement release.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 V2.11 package metadata, SDK constraints, and signing certificate are verified.
- [x] #2 V2.11 signing certificate is compared with V2.10.
- [x] #3 The likely root cause and install recovery steps are documented.
- [x] #4 If the APK is defective, a corrected release plan is created before publishing a replacement.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Verify the local V2.11 APK metadata, SDK constraints, and signing certificate.
2. Download the published V2.10 release APK and compare package metadata and signer certificate with V2.11.
3. Check whether the V2.11 GitHub release asset digest matches the local signed APK.
4. Identify likely install failure causes and document concrete recovery steps.
5. If the APK is defective, create a corrected replacement-release plan before changing/publishing anything.
Architecture impact: not architecture-significant; this is release/package diagnosis only. No ADR expected.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Closed based on user validation: the APK install now works. The earlier package diagnosis found no local release-package defect: V2.11 verified successfully, used the expected package metadata, matched the published release asset digest, and used the same signing certificate as V2.10.

## Acceptance criteria validation

- AC1: V2.11 package metadata, SDK constraints, and signing certificate were verified in the earlier diagnosis.
- AC2: V2.11 signing certificate was compared with V2.10 and matched.
- AC3: Root-cause notes and recovery direction were documented during the investigation; user has now confirmed install works.
- AC4: No corrected release plan is needed because the APK is now installing successfully.

## How to test

### Automated tests

- Not run; this was a release-package/install validation task, not a code change.

### Manual validation

- User confirmed on 2026-06-15 that the app install works.

## TDD / BDD / approval-test evidence

- Not applicable; no production code changed.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR impact: none, because this was release/install validation only.

## README impact

README impact: none, because no setup, commands, architecture, or troubleshooting instructions changed.

## Business requirements impact

Business requirements impact: none, because no product behavior or business rule changed.

## Diagram impact

Diagram impact: none, because no architecture or flow changed.

## Commits / logical change list

- Closed the release install defect based on successful user validation.

## Risks and follow-up

- No follow-up needed unless the install failure reappears on a specific device/emulator with a captured INSTALL_* package-manager error.
<!-- SECTION:NOTES:END -->

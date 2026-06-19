---
id: task-135
title: 'REL: Publish V2.19 official GitHub release'
status: In Progress
assignee:
  - '@codex'
created_date: '2026-06-19 19:09'
updated_date: '2026-06-19 19:09'
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
- [ ] #1 A signed V2.19 release APK is built from the current committed worktree and passes the full validation suite
- [ ] #2 APK signing, package metadata, versionCode 22, versionName 2.19, and SHA-256 are verified and recorded
- [ ] #3 README links V2.19 release notes and the release notes document scope, installation guidance, validation, and accepted risks
- [ ] #4 Git tag v2.19 and a GitHub release containing the signed APK are published
- [ ] #5 Business requirements, diagram, and ADR impact are recorded using the canonical wording from delivery-governance.md
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Confirm release tools, clean worktree, existing version, and signing-keystore availability.\n2. Update Android version metadata to versionCode 22/versionName 2.19; add V2.19 release notes and README link.\n3. Delete the previous local release APK and run the full signed validation/build.\n4. Verify APK signatures, package metadata, digest, and diff hygiene.\n5. Commit release metadata, push the branch, publish tag v2.19 and GitHub release with the signed APK.\n6. Record validation evidence, impact notes, and publication URL; check acceptance criteria and close the task.\n\nTest strategy: full domain, application, infrastructure, and Android unit-test validation plus signed APK verification.\nArchitecture impact: not architecture-significant; no module, persistence, API, dependency, or business-rule changes.\nTreatment: standard release packaging. Legacy behavior: no behavior refactor.\nREADME impact: add release-notes link. Business requirements, diagram, and ADR impact: none, because this packages existing completed behavior only.
<!-- SECTION:PLAN:END -->

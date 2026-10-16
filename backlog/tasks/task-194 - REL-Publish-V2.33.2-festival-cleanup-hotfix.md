---
id: TASK-194
title: 'REL: Publish V2.33.2 festival cleanup hotfix'
status: To Do
assignee: []
created_date: '2026-10-16 05:57'
labels: []
milestone: m-1
dependencies:
  - TASK-192
  - TASK-193
priority: high
type: chore
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Deliver the approved pending-rating discard and festival deletion work as a signed Android update over V2.33.1. Scope: versionCode 38, versionName 2.33.2, release notes, full validation, signed APK, tag and GitHub release. Do not publish until TASK-192 and TASK-193 are complete or explicitly accepted for release.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given a V2.33.1 installation, when V2.33.2 is installed with the stable signing key, then Android accepts the update without clearing app data.
- [ ] #2 The official release APK is signed, has versionCode 38 and versionName 2.33.2, and passes the required validation.
- [ ] #3 Release notes and README document both changes, deletion consequences, installation, and known risks.
- [ ] #4 The V2.33.2 tag and GitHub release contain the verified signed APK and validation evidence.
- [ ] #5 Business requirements, ADR, and diagram impacts are recorded.
<!-- AC:END -->

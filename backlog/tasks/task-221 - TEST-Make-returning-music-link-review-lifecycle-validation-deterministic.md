---
id: TASK-221
title: 'TEST: Make returning music-link review lifecycle validation deterministic'
status: To Do
assignee: []
created_date: '2026-10-17 14:41'
updated_date: '2026-10-17 14:41'
labels:
  - testing
  - music-links
  - release-2.34
milestone: m-0
dependencies:
  - TASK-202
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a member, I need approved music links to be available when I return to band detail, with reliable automated coverage. PR CI run39041198765 intermittently failed returningFromReviewRefreshesNewlySavedProfileOnBandDetail while the same-source other CI run passed. Inspect actual resume/refresh ordering, reproduce with a lifecycle-aware native regression, and correct production behavior if needed or synchronize the fixture with the real resumed state. Preserve explicit approval and canonical metadata. No unrelated redesign or provider changes.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given a link saved during review, when the user returns and band detail is resumed, then the saved channel action is available without reopening the band.
- [ ] #2 Given asynchronous Android finish/resume ordering, then the regression waits for actual lifecycle completion and still fails when refresh behavior is removed; no arbitrary fixed delay or retry-to-pass.
- [ ] #3 Native and relevant JVM tests pass; final-source GitHub Android validation is required before release.
- [ ] #4 README/business requirements/ADR impacts and complete validation package are recorded with canonical wording.
<!-- AC:END -->

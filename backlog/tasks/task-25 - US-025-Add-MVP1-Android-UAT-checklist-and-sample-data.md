---
id: task-25
title: 'US-025: Add MVP1 Android UAT checklist and sample data'
status: To Do
assignee: []
created_date: '2026-05-15 15:22'
labels:
  - mvp1
  - uat
  - android
dependencies:
  - task-22
  - task-23
  - task-24
priority: medium
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## US-025: Add MVP1 Android UAT checklist and sample data

**As a** tester
**I want** sample import data and a clear UAT checklist
**So that** I can verify MVP 1 from the built APK

### Notes
- Include a small valid sample CSV set.
- Include at least one invalid sample or documented mutation to test validation feedback.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given the repo is checked out When I need to test the APK Then sample CSV data is available
- [ ] #2 Given the APK is built When I follow the UAT checklist Then I can verify import, listing, detail, rating, persistence, and invalid import feedback
- [ ] #3 Given final validation is run When MVP 1 is complete Then Gradle test, qaTest, and assembleDebug all pass
<!-- AC:END -->

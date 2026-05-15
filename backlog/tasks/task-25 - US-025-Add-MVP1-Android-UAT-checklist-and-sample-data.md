---
id: task-25
title: 'US-025: Add MVP1 Android UAT checklist and sample data'
status: Done
assignee:
  - '@codex'
created_date: '2026-05-15 15:22'
updated_date: '2026-05-15 18:46'
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
- [x] #1 Given the repo is checked out When I need to test the APK Then sample CSV data is available
- [x] #2 Given the APK is built When I follow the UAT checklist Then I can verify import, listing, detail, rating, persistence, and invalid import feedback
- [x] #3 Given final validation is run When MVP 1 is complete Then Gradle test, qaTest, and assembleDebug all pass
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Added a small valid CSV sample set covering bands, stages, performances, distances, food, and music metadata.
2. Added invalid-performances.csv to trigger row-level unknown band/stage validation feedback without mutating existing data.
3. Added an MVP 1 Android UAT checklist covering APK build/install, import, list, detail, rating, persistence, and invalid import behavior.
4. Linked the checklist and sample data from README.
5. Ran final Gradle validation: test, qaTest, assembleDebug.
6. Closed the task with validation notes.

Architecture impact: not architecture-significant; docs and sample data only.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Implementation notes:
- Added valid sample CSV files under samples/mvp1 for bands, stages, performances, distances, and food.
- Added samples/mvp1/invalid-performances.csv for validation feedback testing.
- Added backlog/docs/mvp1-android-uat-checklist.md with manual steps for empty state, valid import, listing, detail links, rating save, persistence, and invalid import feedback.
- Linked the UAT checklist and sample data from README.

Validation package:
- Automated checks run: JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew test; JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew qaTest; JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew assembleDebug.
- Manual validation: checklist authored but not executed on a physical device/emulator in this session.
- README impact: README updated with links to the MVP 1 UAT checklist and sample data.
- Diagram impact: no diagram update needed.
- ADR impact: no ADR needed.
- Approval status: no architecture approval required.
- Risks: UAT still needs to be run manually on an installed APK.
<!-- SECTION:NOTES:END -->

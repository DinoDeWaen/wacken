---
id: task-30
title: File-based CSV import and Wacken band overview
status: Done
assignee:
  - '@codex'
created_date: '2026-05-16 07:15'
updated_date: '2026-05-16 07:23'
labels:
  - android
  - import
  - ux
  - mvp1
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## Feature: File-based CSV import and Wacken band overview

**As a** user
**I want** to upload CSV files and browse a Wacken-themed band overview
**So that** importing and rating the lineup feels usable on Android

### Notes
- Replace paste-based import fields with Android file selection.
- Uploading CSV files should update existing festival master data.
- Existing ratings must be preserved across imports.
- Improve the band overview visual presentation with a Wacken-inspired theme.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given I open the import screen When I select CSV files Then the selected file names are shown and the import uses the file contents
- [x] #2 Given existing imported festival data and ratings When I import newer CSV files Then master data is updated and existing ratings are preserved
- [x] #3 Given I open the band overview Then it uses a Wacken-themed visual style and still supports opening band details
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Added application tests for successful re-import replacing festival master data while preserving ratings.
2. Extended repository ports/adapters with replace-all operations for imported master data only; left RatingRepository unchanged.
3. Updated ImportFestivalCsvUseCase to commit parsed data as a replacement only after validation passes.
4. Replaced paste inputs in ImportCsvActivity with Android file picker buttons and selected file labels.
5. Restyled MainActivity into a Wacken-themed band overview with themed header, import action, and band cards.
6. Ran targeted tests, full tests, qaTest, and assembleDebug with JDK 21.
7. Closed task and committed.

Architecture impact: not architecture-significant; this extends existing repository ports for master-data replacement and keeps Android/framework details outside domain/application.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Implementation notes:
- ImportCsvActivity now uses Android ACTION_OPEN_DOCUMENT file pickers instead of paste text areas.
- Selected CSV file names are shown on the import screen.
- Import requires at least bands.csv and reports file-read/import validation errors on screen.
- ImportFestivalCsvUseCase now replaces imported master data after validation succeeds, while ratings are preserved because RatingRepository is not touched.
- MainActivity now renders a dark Wacken-themed overview with amber header/action styling and tappable band cards.
- README and MVP1 UAT checklist updated for file-based import.

Validation package:
- Automated checks run: JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :application:test :infrastructure:test; JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:compileDebugJavaWithJavac; JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew test; JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew qaTest; JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew assembleDebug.
- Manual validation: not run on device in this shell.
- README impact: README updated from paste-based to file-based import.
- Diagram impact: no diagram update needed.
- ADR impact: no ADR needed.
- Approval status: no architecture approval required.
- Risks: Android file picker availability depends on the device document provider; APK build validates compile/package only.
<!-- SECTION:NOTES:END -->

---
id: task-22
title: 'US-022: Add Android CSV import screen'
status: To Do
assignee: []
created_date: '2026-05-15 15:22'
labels:
  - mvp1
  - android
  - import
dependencies:
  - task-21
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## US-022: Add Android CSV import screen

**As an** admin
**I want** to import CSV files from the Android app
**So that** I can load festival data into the APK without developer test hooks

### Notes
- Use the CSV schemas in `backlog/docs/festival-data-csv-schemas.md`.
- A minimal first UI may accept pasted CSV text or picked files, but the chosen path must be usable on Android.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given I open the import screen When I provide valid CSV data Then the app imports bands, stages, performances, distances, and food
- [ ] #2 Given the CSV contains validation errors When I import Then the app shows explicit row-level errors and does not mutate existing data
- [ ] #3 Given import succeeds When I return to the band list Then imported bands are visible
<!-- AC:END -->

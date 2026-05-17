---
id: task-43
title: 'US-043: Add central admin workflow for festival data'
status: To Do
assignee: []
created_date: '2026-05-17 16:52'
labels:
  - admin
  - backend
  - supabase
  - csv
dependencies:
  - task-39
  - task-41
priority: medium
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As the app owner, I want to manage bands and festival master data centrally so that users receive corrected lineup data without each device importing CSV files manually.

Business value:
- Moves admin work out of each Android device and into one central backend process.
- Allows band metadata, running order, links, and food/stage data to be corrected once for everyone.

In scope:
- Define the admin workflow for creating/updating bands, stages, performances, distances, and food options in Supabase.
- Support CSV import into the backend schema with validation and import history.
- Preserve existing validation rules for missing references, invalid times, and stage overlaps.
- Record who ran an import and when.

Out of scope:
- Full polished admin web application unless explicitly approved later.
- Android-based admin UI.
- Automated scraping from Wacken pages.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given an admin has a valid CSV dataset, when the backend import is run, then Supabase master data is updated centrally.
- [ ] #2 Given invalid CSV data is provided, then the import reports validation errors and does not partially corrupt master data.
- [ ] #3 Given users sync after an admin update, then their app receives the corrected master data through Room cache sync.
- [ ] #4 Given import history is reviewed, then the admin can see when an import ran and whether it succeeded or failed.
- [ ] #5 Existing CSV validation behavior is preserved or intentionally changed with explicit acceptance criteria.
- [ ] #6 Automated tests or executable validation cover backend import validation and successful import.
- [ ] #7 README documents the admin import workflow and any required commands.
<!-- AC:END -->

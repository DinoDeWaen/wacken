---
id: task-141
title: 'US: Export all band ratings CSV from settings'
status: To Do
assignee: []
created_date: '2026-07-02 08:26'
labels:
  - mvp3
  - export
  - settings
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Business value: users need an easy way to take all Wacken rating data out of the app for review, sharing, or backup.

As a Wacken Planner user, I want to export all locally available band ratings from the settings screen, so that I can inspect and keep my planning data outside the app.

Scope: settings action, CSV generation from local cache, Android share/save handoff, and export feedback. The export must work offline from cached data.

Out of scope: changing rating rules, importing the exported CSV back into the app, or Play Store file-provider polishing beyond what direct install needs.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given locally available band data, when I export ratings from settings, then a CSV file is produced with one row per locally known band
- [ ] #2 Given ratings are locally available, when the CSV is exported, then it includes band identity, band name, planning rating, real post-show rating if available, locally cached group member ratings where available, stage/date/time where known, and schedule status where known
- [ ] #3 Given the device is offline, when I export ratings, then the export uses cached data and does not require Supabase
- [ ] #4 Given export succeeds or fails, when the user returns to settings, then clear Wacken-styled feedback is shown
- [ ] #5 Automated tests cover CSV escaping, empty/unrated values, and locally cached group ratings
- [ ] #6 Manual test steps are documented in implementation notes
- [ ] #7 Business requirements and README impact use canonical delivery-governance wording
- [ ] #8 Architecture impact is assessed before implementation; if storage/export boundaries require an architecture-significant change, explicit approval is requested before coding
<!-- AC:END -->

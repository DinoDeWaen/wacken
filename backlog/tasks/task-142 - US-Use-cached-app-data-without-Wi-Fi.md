---
id: task-142
title: 'US: Use cached app data without Wi-Fi'
status: To Do
assignee: []
created_date: '2026-07-02 08:26'
labels:
  - mvp3
  - offline
  - sync
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Business value: Wacken network conditions are unreliable, so the app must remain useful during the festival without Wi-Fi or mobile data.

As a festival attendee, I want the app to work from cached/imported data with no network connection, so that I can still view bands, details, ratings, real ratings, and schedule while at Wacken.

Scope: validate and harden startup, overview, detail, settings, schedule, rating edits, real-rating edits, and manual schedule choices when no network is available after a prior sync/import.

Out of scope: making first install magically contain data when no sync/import has ever happened.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given the app has previously synced or imported festival data, when it starts with no network, then cached overview data is shown without blocking on Supabase
- [ ] #2 Given the device has no network, when I open band details and schedule, then cached details, ratings, real ratings, and generated schedule remain usable
- [ ] #3 Given the device has no network, when I edit planning ratings, real ratings, or manual schedule choices, then changes are saved locally and pending sync state is visible where applicable
- [ ] #4 Given the app has no cached/imported festival data and no network, when it starts, then it explains that an initial sync or import is needed
- [ ] #5 Automated tests or documented manual checks cover offline startup and offline edits
- [ ] #6 Manual no-Wi-Fi smoke-test steps are documented in implementation notes
- [ ] #7 Business requirements and README impact use canonical delivery-governance wording
- [ ] #8 Architecture impact is assessed before implementation; if sync/storage boundaries change, explicit approval and ADR handling are completed before coding
<!-- AC:END -->

---
id: task-21
title: 'US-021: Choose and implement MVP local persistence'
status: To Do
assignee: []
created_date: '2026-05-15 15:22'
labels:
  - mvp1
  - android
  - persistence
dependencies:
  - task-6
  - task-8
  - task-13
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## US-021: Choose and implement MVP local persistence

**As a** user
**I want** imported festival data and ratings to persist locally on the device
**So that** the APK remains useful after closing and reopening the app

### Notes
- This is architecture-significant because it introduces a persistence strategy/adapter.
- Prefer the smallest durable storage that supports MVP 1 data and can be migrated later.
- Must keep domain/application independent from Android storage APIs.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given festival data has been imported When the app restarts Then bands and performances are still available
- [ ] #2 Given I rate a band When the app restarts Then the rating is still available
- [ ] #3 Given persistence is introduced When the task is completed Then an ADR records the storage decision and README documents any setup or behavior impact
<!-- AC:END -->

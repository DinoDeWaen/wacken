---
id: task-6
title: 'US-006: CSV import for festival data'
status: To Do
assignee: []
created_date: '2026-01-06 16:57'
labels: []
dependencies: []
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## US-006: CSV import for festival data

**As an** admin
**I want** to import bands, stages, performances, distances, and food from CSV
**So that** the system has validated master data for scheduling
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given valid CSV files for bands, stages, performances, distances, and food When I run the import use case Then domain repositories are populated with parsed objects
- [ ] #2 Given CSV data with missing references When the import runs Then it fails with explicit errors listing the missing ids
- [ ] #3 Given overlapping performances on the same stage When the import runs Then it flags the overlap and fails the import
<!-- AC:END -->

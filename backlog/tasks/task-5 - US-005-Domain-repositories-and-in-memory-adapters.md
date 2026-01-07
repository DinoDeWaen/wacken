---
id: task-5
title: 'US-005: Domain repositories and in-memory adapters'
status: To Do
assignee: []
created_date: '2026-01-06 16:57'
labels: []
dependencies: []
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## US-005: Domain repositories and in-memory adapters

**As a** developer
**I want** repository interfaces with in-memory adapters
**So that** application use cases can run without infrastructure coupling
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given the domain layer When defining repositories Then interfaces exist for bands, performances, ratings, and distances without infrastructure details
- [ ] #2 Given application tests When using fakes Then in-memory implementations allow storing and retrieving bands, performances, ratings, and distances
- [ ] #3 Given clean architecture rules When reviewing dependencies Then repository interfaces live in domain and implementations live in infrastructure
<!-- AC:END -->

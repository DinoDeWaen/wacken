---
id: task-4
title: 'US-004: Core domain entities'
status: To Do
assignee: []
created_date: '2026-01-06 16:57'
labels: []
dependencies: []
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## US-004: Core domain entities

**As a** developer
**I want** core domain types for bands, stages, performances, and ratings
**So that** business rules have a solid model foundation
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given a rating creation When value is outside 0-4 Then the domain rejects it with a clear error
- [ ] #2 Given a performance with start and end times When end is not after start Then the domain rejects it
- [ ] #3 Given stage distance definitions When a negative distance is provided Then the domain rejects it and same-stage distance resolves to zero
<!-- AC:END -->

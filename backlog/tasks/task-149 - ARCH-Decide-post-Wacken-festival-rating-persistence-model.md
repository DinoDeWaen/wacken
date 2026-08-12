---
id: task-149
title: 'ARCH: Decide post-Wacken festival rating persistence model'
status: To Do
assignee: []
created_date: '2026-08-12 07:52'
labels:
  - architecture
  - post-mvp3
  - festivals
  - ratings
  - supabase
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Business value: The post-Wacken features need one coherent data model before implementation changes Room, Supabase, migrations, sync, and domain boundaries.

Scope: define the festival, archived festival, reusable band, festival lineup entry, festival planning rating, and personal band rating event model; document exact-name matching for the first version; document future alias/fuzzy matching boundaries.

Out of scope: implementing the feature behavior in Android or Supabase.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Architecture-significant changes are explicitly approved before implementation starts.
- [ ] #2 An ADR defines the Room, Supabase, sync, and domain model changes for festivals, bands, lineup entries, planning ratings, and personal rating events.
- [ ] #3 The model enforces one active festival at a time and supports archived read-only festivals.
- [ ] #4 The model keeps personal band rating history separate from festival planning ratings.
- [ ] #5 Exact-name band reuse is defined for the first version; fuzzy matching and aliases are documented as future scope.
- [ ] #6 README, business requirements, diagram, and ADR impact are recorded using delivery-governance wording.
<!-- AC:END -->

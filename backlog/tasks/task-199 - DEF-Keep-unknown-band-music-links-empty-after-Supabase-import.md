---
id: TASK-199
title: 'DEF: Keep unknown band music links empty after Supabase import'
status: In Progress
assignee:
  - '@codex'
created_date: '2026-10-16 21:05'
updated_date: '2026-10-16 21:05'
labels:
  - defect
  - android
  - metadata
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
New Summer breeze bands such as Abbie Falls show a failing YouTube action despite having no known channel. Preserve absent remote metadata as absent through creation, sync, Room and screen rendering; never invent a channel or show a null link. Known music links must remain usable. Diagnose platform-specific JSON behavior and add regression coverage. No unrelated enrichment, database schema change or release publication.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given a newly imported band such as Abbie Falls whose Supabase YouTube value is null, when its lineup or detail is shown, no YouTube action is offered.
- [ ] #2 Null, missing and blank optional band metadata remain absent after creation and normal sync/cache mapping; known YouTube and Spotify values remain unchanged.
- [ ] #3 A regression reproduces the actual Android JSON behavior before the fix and validates persisted/cache and screen behavior after it.
- [ ] #4 README and business requirements describe unknown-link behavior; tests, root cause, introducing change, architecture/ADR/diagram impact and validation package are recorded.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Standard defect treatment in an isolated branch based on V2.33.4. 1. Confirm real Abbie Falls metadata and trace JSON to Room/UI; assess architecture impact (no new boundary, schema, dependency or API contract anticipated). 2. Add an Android-native regression for explicit JSON null through creation and normal sync, Room and current/archive music actions; protect known links and missing/blank fields. Run red before repair. 3. Correct optional metadata mapping at the existing adapter boundary and preserve valid links. Check existing-cache correction on sync and metadata missing-field detection. 4. Run relevant unit and Android tests plus full fresh signed validation build, update README/requirements and record validation package, root cause/history and limitations. No new release publication or metadata enrichment. ADR/diagram impact none unless inspection reveals a new architectural decision.
<!-- SECTION:PLAN:END -->

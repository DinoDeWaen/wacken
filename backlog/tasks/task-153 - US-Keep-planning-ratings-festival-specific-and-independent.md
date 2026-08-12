---
id: task-153
title: 'US: Keep planning ratings festival-specific and independent'
status: To Do
assignee: []
created_date: '2026-08-12 07:53'
labels:
  - user-story
  - post-mvp3
  - ratings
  - supabase
  - schedule
dependencies:
  - task-149
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a group member, I want my planning rating for a band to belong to the current festival so that festival-specific reasons to see a band do not change my long-term personal band rating.

Business value: Group schedule decisions can use the rating for the active festival while preserving honest personal ratings separately.

Scope: store and sync per-user, per-festival, per-band planning ratings; keep group schedule rules based on planning ratings for the active festival; ensure changing planning ratings never creates or overwrites personal band rating events.

Out of scope: using personal ratings directly in the schedule decision engine, multiple active festivals, group-average personal rating views.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given a user rates a band for festival planning, then the rating is stored for that user, active festival, and band.
- [ ] #2 Given the planning rating syncs, then Supabase stores it separately from personal band rating events.
- [ ] #3 Given a band has a personal rating history, when the user changes the current festival planning rating, then no personal band rating event is changed or created.
- [ ] #4 Given the group schedule is generated, then the active festival planning ratings remain the ratings used for group decisions.
- [ ] #5 Domain/application tests cover planning-versus-personal independence and BDD covers changing a festival planning rating for contextual reasons.
- [ ] #6 README, business requirements, diagram, and ADR impact are recorded using delivery-governance wording.
<!-- AC:END -->

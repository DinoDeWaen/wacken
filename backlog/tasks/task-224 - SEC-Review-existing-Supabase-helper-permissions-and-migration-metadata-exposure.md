---
id: TASK-224
title: >-
  SEC: Review existing Supabase helper permissions and migration metadata
  exposure
status: To Do
assignee: []
created_date: '2026-10-17 19:45'
labels:
  - security
  - supabase
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As the release owner, I want the existing Supabase security advisories assessed and narrowly hardened so public API exposure stays intentional. Baseline before2.34 rollout: mutable search_path on public.touch_updated_at; RLS disabled on public.flyway_schema_history; anon/authenticated execution of legacy definer helpers handle_new_auth_user/is_group_admin/is_group_member/is_platform_admin; leaked-password protection disabled. These predate2.34 and were recorded in2.33.5 limitations. Scope an approved compatible fix without disrupting auth triggers, membership/RLS helpers or Flyway. This is a separate security follow-up, not a new rating lifecycle change. Architecture/security-boundary changes require explicit human approval before implementation.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given the baseline advisories, when assessed, then actual grants/ownership/data exposure and compatible minimum changes are documented with source evidence.
- [ ] #2 Given proposed security-boundary changes, then human approval and an ADR precede implementation, and no required auth/Flyway/member workflow is broken.
- [ ] #3 Given approved hardening, when anon/member/nonmember and migration/auth regression tests run, then unauthorized access is denied and intended behavior remains.
- [ ] #4 Security advisors and direct grant/RLS verification show resolved findings or explicit justified residuals; README/business/diagram/ADR impacts are recorded.
<!-- AC:END -->

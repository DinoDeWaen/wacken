---
id: task-40
title: 'US-040: Add Supabase Auth and group membership model'
status: To Do
assignee: []
created_date: '2026-05-17 16:52'
labels:
  - backend
  - auth
  - supabase
  - groups
dependencies:
  - task-39
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a festival attendee, I want to sign in and belong to the shared Wacken planning group so that ratings can be linked to the right person and group.

Business value:
- Enables multiple people to contribute ratings safely.
- Provides the identity and group boundary needed for shared planning and admin access.

In scope:
- Configure the app/backend contract for Supabase Auth.
- Use the schema from task-39 for profiles, groups, and group_members.
- Support one default planning group for the current MVP while keeping the schema ready for more groups later.
- Define member/admin roles for group data access.
- Add app configuration for Supabase URL and anon key through non-secret build config.

Out of scope:
- Full invite UX.
- Multiple independent production groups.
- Admin data management screens.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given a user signs in, then the app can associate that user with a profile and group membership.
- [ ] #2 Given the current MVP uses one shared planning group, then authenticated members can be assigned to that group without blocking future multi-group support.
- [ ] #3 Given a user is not a group member, then RLS prevents access to private group ratings.
- [ ] #4 Given a group admin exists, then admin/member role data is available for later admin workflows.
- [ ] #5 Supabase URL and anon key are configured without committing service-role secrets or database passwords.
- [ ] #6 Automated tests or documented validation prove the auth/group access assumptions.
- [ ] #7 README documents auth setup and required Supabase project settings.
<!-- AC:END -->

---
id: task-60
title: 'US-062: Backfill existing users into Sofie and Dino group'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-07 16:02'
updated_date: '2026-06-07 16:13'
labels:
  - mvp2
  - supabase
  - group
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As the shared Wacken planning group, I want every existing Supabase app user to belong to the first group named Sofie and Dino, so all current ratings participate in the same MVP2 schedule decisions.

In scope:
- Ensure the first shared group is named `Sofie and Dino` or confirm that existing canonical group identity.
- Add every existing app user/profile to that group membership.
- Make the backfill idempotent so it can be rerun safely.
- Verify rating sync and group-rating reads use the shared group after the backfill.

Out of scope:
- Multiple independent groups, user self-service group creation, invite-token lifecycle, and Play Store distribution.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given existing Supabase app users exist, when the backfill runs, then each user is a member of the Sofie and Dino group.
- [x] #2 Given the backfill is run more than once, when it completes, then no duplicate group memberships are created.
- [x] #3 Given a user signs in after the backfill, when ratings sync runs, then their ratings are associated with the Sofie and Dino shared group.
- [x] #4 Given another existing user opens the app, when sync completes, then ratings from the shared group can be pulled for MVP2 schedule decisions.
- [x] #5 Automated or focused backend validation verifies the group and membership data.
- [x] #6 README and business requirements impact are recorded using the canonical wording from delivery-governance.md.
- [x] #7 Architecture impact is assessed; if schema, RLS, or auth contract changes are needed, explicit approval and ADR handling are required before implementation.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Added idempotent Flyway migration V006 to ensure canonical group 00000000-0000-0000-0000-000000000001 is named Sofie and Dino.
2. Added idempotent profile and group membership backfill for every existing auth.users row, using ON CONFLICT to avoid duplicates.
3. Extended backend verification to assert the Sofie and Dino group exists and every Supabase auth user has default group membership.
4. Updated README and business requirements to document the shared MVP2 group and existing-user backfill.
5. Ran Flyway migration validation and backend auth/group verification against Supabase.

Deviation: none. Architecture impact: not architecture-significant; this is an idempotent data migration and verification update within the existing group schema, RLS policy model, and app sync contract. ADR impact: none.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Added an idempotent Supabase/Flyway backfill that keeps the canonical MVP shared group named `Sofie and Dino`, creates missing profiles for existing auth users, and adds every existing Supabase auth user as a member of that group without duplicate memberships. Backend verification now fails if any auth user is missing from the shared group.

## Acceptance criteria validation

- AC1: Verified by V006 membership insert from `auth.users` and backend check showing zero auth users missing from the default group.
- AC2: The migration uses `ON CONFLICT` for group, profile, and membership writes, so reruns are idempotent.
- AC3: The canonical group id remains the existing app default group id used by sign-in and rating sync.
- AC4: Existing users are now in the same shared group, so group rating reads use shared membership after sync.
- AC5: `backend/flyway/verify-auth-setup.sh` verifies the group and membership data.
- AC6: README and business requirements impacts are recorded below.
- AC7: Architecture impact assessed below.

## How to test

### Automated tests

- `backend/flyway/run-flyway.sh migrate`
- `backend/flyway/verify-auth-setup.sh`
- `git diff --check`

### Manual validation

- Flyway reported schema `public` at version 006 and up to date.
- Backend verification reported `default_group_count = 1`, `default_group_member_count = 4`, and `auth_users_without_default_group = 0`.

## TDD / BDD / approval-test evidence

This task is a backend data backfill and verification change. The migration is idempotent by construction through database constraints and `ON CONFLICT`; focused backend verification covers the observable acceptance criteria.

## Architecture impact

- Architecture-significant change: no. The change uses the existing Supabase schema, canonical group id, RLS model, and app sync contract.
- Approval received: not required.
- ADR: none required.

## README impact

README impact: updated Supabase Auth notes with the canonical `Sofie and Dino` shared group and V006 existing-user backfill behavior.

## Business requirements impact

Business requirements impact: updated MVP2 implemented capabilities and added BR-038a for existing users belonging to the Sofie and Dino shared group.

## Diagram impact

Diagram impact: none, because this does not change the documented system structure or data flow.

## Commits / logical change list

- Add V006 Sofie and Dino group/profile/membership backfill.
- Extend backend auth setup verification for shared group membership completeness.
- Document MVP2 shared group behavior in README and business requirements.

## Risks and follow-up

- Existing Supabase users are covered by the current backfill. Future multi-group invite behavior remains out of scope for MVP2.
<!-- SECTION:NOTES:END -->

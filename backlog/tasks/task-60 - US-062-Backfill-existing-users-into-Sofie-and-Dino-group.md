---
id: task-60
title: 'US-062: Backfill existing users into Sofie and Dino group'
status: To Do
assignee: []
created_date: '2026-06-07 16:02'
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
- [ ] #1 Given existing Supabase app users exist, when the backfill runs, then each user is a member of the Sofie and Dino group.
- [ ] #2 Given the backfill is run more than once, when it completes, then no duplicate group memberships are created.
- [ ] #3 Given a user signs in after the backfill, when ratings sync runs, then their ratings are associated with the Sofie and Dino shared group.
- [ ] #4 Given another existing user opens the app, when sync completes, then ratings from the shared group can be pulled for MVP2 schedule decisions.
- [ ] #5 Automated or focused backend validation verifies the group and membership data.
- [ ] #6 README and business requirements impact are recorded using the canonical wording from delivery-governance.md.
- [ ] #7 Architecture impact is assessed; if schema, RLS, or auth contract changes are needed, explicit approval and ADR handling are required before implementation.
<!-- AC:END -->

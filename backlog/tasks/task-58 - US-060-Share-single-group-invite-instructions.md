---
id: task-58
title: 'US-060: Share single-group invite instructions'
status: To Do
assignee: []
created_date: '2026-06-07 15:37'
labels:
  - mvp2
  - invite
  - android
dependencies: []
priority: medium
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a Wacken Planner group member, I want a simple Android share action for joining the shared planning group, so friends know how to install, sign in, and contribute ratings for MVP2 planning.

In scope:
- Provide a reachable Android invite/share action for the current single shared group.
- Share installation/sign-in instructions and the group context needed for MVP2 rating contribution.
- Make clear that the current version supports one shared group only.
- Avoid exposing secrets or backend service credentials.

Out of scope:
- Multiple independent groups, invite token lifecycle, admin approval flows, Play Store distribution, and new Supabase schema unless explicitly approved in a later task.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given a signed-in user opens the invite action, when they choose to share, then Android opens a share sheet with clear friend onboarding text.
- [ ] #2 Given the invite text is shared, then it explains this is the single shared Wacken planning group and does not expose secrets.
- [ ] #3 Given a recipient follows the invite instructions and signs in with a provisioned account, then their ratings can participate in the existing shared group sync model.
- [ ] #4 Given multi-group support is not part of this version, then the UI/invite text does not imply separate group creation.
- [ ] #5 Focused validation covers the invite/share text and Android share action where feasible.
- [ ] #6 README and business requirements impact are recorded using the canonical wording from delivery-governance.md.
- [ ] #7 Architecture impact is assessed; if invite tokens, schema changes, or new external contracts become necessary, explicit approval and ADR handling are required before implementation.
<!-- AC:END -->

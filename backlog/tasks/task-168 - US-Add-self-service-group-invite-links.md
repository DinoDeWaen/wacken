---
id: task-168
title: 'US: Add self-service group invite links'
status: To Do
assignee: []
created_date: '2026-08-19 11:02'
labels:
  - user-story
  - future
  - groups
  - invites
dependencies: []
priority: low
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Business value: Group onboarding becomes usable without manually provisioning every friend.

As a group owner, I want to share an invite link so that a friend can join the correct planning group after signing in.

Scope: invite creation, expiry/revocation, link handling, join confirmation, and clear invalid-invite feedback.

Out of scope: multiple upcoming festivals and automatic cross-group data migration.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given a group member creates an invite, when the invite is shared, then it contains no raw secret data that should be visible in the UI.
- [ ] #2 Given a recipient opens a valid invite and signs in, when they accept it, then they become a member of the intended group.
- [ ] #3 Given an invite is expired, revoked, or already invalid, when it is opened, then the app shows a clear failure message and does not change membership.
- [ ] #4 Relevant tests cover invite success, invalid invite handling, and permission boundaries.
- [ ] #5 README, business requirements, diagram, and ADR impact are recorded using delivery-governance wording when implemented.
<!-- AC:END -->

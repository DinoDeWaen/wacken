---
id: task-58
title: 'US-060: Share single-group invite instructions'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-07 15:37'
updated_date: '2026-06-07 16:23'
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
- [x] #1 Given a signed-in user opens the invite action, when they choose to share, then Android opens a share sheet with clear friend onboarding text.
- [x] #2 Given the invite text is shared, then it explains this is the single shared Wacken planning group and does not expose secrets.
- [x] #3 Given a recipient follows the invite instructions and signs in with a provisioned account, then their ratings can participate in the existing shared group sync model.
- [x] #4 Given multi-group support is not part of this version, then the UI/invite text does not imply separate group creation.
- [x] #5 Focused validation covers the invite/share text and Android share action where feasible.
- [x] #6 README and business requirements impact are recorded using the canonical wording from delivery-governance.md.
- [x] #7 Architecture impact is assessed; if invite tokens, schema changes, or new external contracts become necessary, explicit approval and ADR handling are required before implementation.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Inspected the overview action row, Supabase Auth/group documentation, and one-group invite requirements.
2. Added focused app-module tests for invite share text covering onboarding clarity, single shared group wording, and no secret/token/deep-link exposure.
3. Added a reachable Android overview button that opens ACTION_SEND through Android chooser with the tested invite text.
4. Updated README and business requirements with the current single-group plain-text invite behavior.
5. Ran focused app validation and diff checks.

Deviation: implemented plain onboarding share text only; no invite tokens, schema changes, RLS changes, backend API changes, or deep-link handling were needed. Architecture impact: not architecture-significant. ADR impact: none.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Added a signed-in overview action, **Share group invite**, that opens the Android share sheet with clear onboarding text for the single shared `Sofie and Dino` planning group. The text tells friends to install the APK, sign in with a provisioned Supabase account, sync from Supabase, and contribute ratings to the shared MVP2 schedule. It intentionally avoids passwords, API keys, service-role credentials, invite tokens, and deep links.

## Acceptance criteria validation

- AC1: MainActivity now exposes a reachable share action that launches `Intent.ACTION_SEND` via Android chooser.
- AC2: `InviteShareTextTest` verifies the shared text states the single shared group context and omits secrets/token/deep-link wording.
- AC3: The text directs recipients to use a provisioned Supabase account and sync ratings into the existing shared group model.
- AC4: The UI text and invite message say one shared planning group only and do not imply group creation.
- AC5: Focused app-module tests cover invite/share text; app Java compilation validates the Android share action wiring.
- AC6: README and business requirements impacts are recorded below.
- AC7: Architecture impact assessed below.

## How to test

### Automated tests

- `/bin/zsh -lc JAVA_HOME=java21 ./gradlew :app:testDebugUnitTest :app:compileDebugJavaWithJavac`
- `git diff --check`

### Manual validation

- Not run on a physical device in this task. The invite text is covered by focused tests and MainActivity share-intent wiring is compile-validated.

## TDD / BDD / approval-test evidence

Added invite text tests before wiring the Android share action. The tests encode the acceptance criteria for onboarding clarity, one-group scope, and no secret/token exposure.

## Architecture impact

- Architecture-significant change: no. The change is Android presentation/share intent behavior only. No invite-token model, schema, RLS, backend API, deep-link contract, dependency, or module-boundary change was introduced.
- Approval received: not required.
- ADR: none required.

## README impact

README impact: updated basic functionality and Supabase Auth notes with the `Share group invite` action and safe plain-text onboarding behavior.

## Business requirements impact

Business requirements impact: updated implemented capabilities, added BR-038b for the MVP2 invite action, and refined the future deep-link invite open question.

## Diagram impact

Diagram impact: none, because the system structure and data flow did not change.

## Commits / logical change list

- Add tested `InviteShareText` for single-group onboarding.
- Add overview `Share group invite` button and Android chooser intent.
- Document plain-text MVP2 invite behavior.

## Risks and follow-up

- This is not a self-service invite-token flow. Friends still need provisioned Supabase accounts for MVP2.
<!-- SECTION:NOTES:END -->

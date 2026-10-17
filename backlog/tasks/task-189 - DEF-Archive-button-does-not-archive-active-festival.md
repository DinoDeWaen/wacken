---
id: TASK-189
title: 'DEF: Archive button does not archive active festival'
status: Done
assignee:
  - '@codex'
created_date: '2026-10-15 19:16'
updated_date: '2026-10-15 19:31'
labels:
  - defect
  - android
  - festival
dependencies: []
references:
  - mainactivity
  - supabasemasterdataclient
modified_files:
  - app/src/test/java/be/wacken/planner/ArchiveFestivalActionTest.java
  - app/src/main/java/be/wacken/planner/ArchiveFestivalAction.java
  - app/src/main/java/be/wacken/planner/MainActivity.java
  - app/src/test/java/be/wacken/planner/SupabaseMasterDataClientTest.java
  - app/src/main/java/be/wacken/planner/SupabaseMasterDataClient.java
  - backlog/decisions/0011-post-wacken-festival-rating-model.md
  - README.md
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Business value: a signed-in group member can archive the active festival and proceed to the next one. Observed: tapping Archive produces no visible change; no festival write reaches Supabase. Scope: repair the Android archive action and make success or failure visible while preserving the cached festival on failure. Out of scope: changing festival schema or pending-rating recovery.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given a signed-in user and an active festival, when Archive is tapped, then the remote archive completes and the no-active-festival state is shown.
- [x] #2 Given the remote archive fails, when Archive is tapped, then the active festival remains in the local cache and an actionable error is shown.
- [x] #3 Automated regression tests cover the archive action and failure behavior.
- [x] #4 README impact is recorded using delivery-governance wording.
- [x] #5 ADR impact and architecture approval are recorded for the festival API write-path change.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Added failing ArchiveFestivalAction regression tests, then moved the archive use case to a worker thread and delivered success/failure callbacks on the UI thread.
2. With explicit user architecture approval, added failing SupabaseMasterDataClient tests for archive, rename, and new-festival creation; changed existing-festival writes to PATCH and new-festival writes to POST under current RLS.
3. Updated ADR 0011 and README with the existing permission boundary and archive feedback.
4. Ran focused and broad tests, QA scenarios, compile, lint attempt, clean signed release build, APK signature/metadata checks, and git diff --check.
5. Linked each changed file and affected Groma component through Backlog CLI.

Deviation: Android lint remains blocked by 14 pre-existing NewApi errors outside the changed lines. The signed APK remains version 2.33 for local validation; publishing a new version is separate release work. No schema, domain, application port, or RLS change was made.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

- Archive now performs the existing source-first festival write on a worker thread, reports progress or a clear failure on the UI thread, and leaves the active Room festival intact when the remote write fails.
- The Supabase festival adapter reads by ID, PATCHes an existing festival for archive/rename, and POSTs a new festival. This matches the current RLS: group members may update, while only platform admins may insert.
- Updated README and ADR 0011 to describe the write path and present permission limit.

## Acceptance criteria validation

- AC1: Archive success callback receives the no-active state after the remote repository save; tested with a recording worker/UI executor and fake repository. Physical-device tap remains to be checked after installation.
- AC2: A simulated remote failure leaves the active festival in cache and delivers an error callback; MainActivity displays the error and re-enables Archive.
- AC3: Focused tests cover success/failure, PATCH archive and rename, and POST create.
- AC4-5: README and ADR impact recorded below; user approved the integration change on 2026-10-15 with “please do that.”

## How to test

### Automated tests

- Focused ArchiveFestivalActionTest and SupabaseMasterDataClientTest passed.
- Domain, application, infrastructure, and app unit tests; application qaTest; and debug Java compile passed.
- assembleRelease passed after :app:clean.
- :app:lintDebug ran but failed on 14 existing NewApi errors, including File.toPath and URL encoder calls; none are in newly changed lines.

### Manual validation

- Live Supabase read-only checks confirmed wacken-2026 ACTIVE and current group-member UPDATE versus admin-only INSERT policies. No user device was attached, so the installed app could not be tapped.
- apksigner verified v1 and v2 signatures. APK package be.wacken.planner, versionCode 36, versionName 2.33, minSdk 23. SHA-256: 164364cc8f63f0c26bb3fe52383824665402ea07ebac95490d65ae31b04c85c1. git diff --check passed.

## TDD / BDD / approval-test evidence

- Archive action tests first failed because the asynchronous action was absent, then passed after implementation. Festival adapter tests first failed because no testable request seam existed, then passed after PATCH/POST behavior was implemented. Existing archive use-case tests and QA scenario passed.

## Architecture impact

- Architecture-significant change: yes, the existing Supabase integration write path changed.
- Approval received: yes, the user explicitly replied “please do that” to the standard PATCH-existing/POST-new proposal.
- ADR: updated ADR 0011; no new schema, domain model, application port, or RLS change.

## README impact

README impact: updated archive feedback and current festival write permissions.

## Business requirements impact

Business requirements impact: none, because archive behavior remains the same desired workflow. README describes the current admin-only creation policy; BR-087 continues to state the intended broader signed-in group behavior, which remains follow-up work.

## Diagram impact

Diagram impact: none, because container/component boundaries and relationships did not change.

## Commits / logical change list

- Async archive action and UI feedback.
- Festival PATCH/POST write selection and regression tests.
- README and ADR 0011 permission notes.

## Risks and follow-up

- The APK is freshly signed but retains version 2.33 and is not a newly published GitHub release. Device installation and an actual tap are still unverified.
- Existing lint errors remain; task-190 separately tracks the orphaned pending planning rating that still blocks sync.
- New festival creation by a non-admin group member remains blocked by the current RLS policy.
<!-- SECTION:NOTES:END -->

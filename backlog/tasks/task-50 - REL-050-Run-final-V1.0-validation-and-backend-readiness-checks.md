---
id: task-50
title: 'REL-050: Run final V1.0 validation and backend readiness checks'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-04 18:50'
updated_date: '2026-06-04 20:00'
labels:
  - mvp1
  - release
  - validation
  - backend
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## Business value

V1.0 needs a current validation package that proves the codebase, APK build, QA suite, and backend setup are ready at the point of release.

## User story

As the app owner, I want final automated and backend readiness checks recorded for V1.0, so that the release is based on fresh evidence rather than older task-level validation.

## Scope

In scope:
- Run final Gradle automated checks for tests, QA scenarios, and debug APK assembly.
- Verify Supabase/Flyway backend readiness using the documented release commands where credentials are available.
- Verify master-data import and auth setup checks where credentials are available.
- Record any skipped external checks with the concrete reason.

Out of scope:
- Reopening the already closed sync defect.
- Changing backend schema or app architecture unless a separate approved task is created.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given the release branch/worktree is ready, when final validation runs, then Gradle test, qaTest, and assembleDebug pass or failures are captured as release blockers.
- [x] #2 Given Supabase credentials are available, when backend readiness is checked, then Flyway status/migration, master-data import, band import verification, and auth setup verification are recorded.
- [x] #3 Given external credentials or services are unavailable, when validation is recorded, then the skipped checks include the concrete missing prerequisite and release risk.
- [x] #4 Given validation completes, when implementation notes are written, then they include the delivery-governance validation package with automated tests, manual/backend checks, risks, and logical change list.
- [x] #5 README impact, business requirements impact, diagram impact, and ADR impact are recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Ran final Gradle validation with JDK 21: test, qaTest, and assembleDebug all passed.
2. Ran Flyway backend readiness checks: info, migrate, and info all passed; schema is at version 005 and up to date.
3. Ran Supabase readiness checks: import-master-data, import-bands, verify-bands-import, and verify-auth-setup all passed.
4. No external checks were skipped; credentials, network, Flyway, and psql were available.
5. Recorded delivery-governance validation evidence and closed the task.

Architecture impact: validation-only, not architecture-significant. No approval or ADR required.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Completed final V1.0 validation and backend readiness checks. Gradle validation passed, Flyway reports schema version 005 with no pending migrations, master-data import succeeded, band import verification matched the CSV, and auth setup verification passed.

## Acceptance criteria validation

- AC1: Passed. Gradle test, qaTest, and assembleDebug all passed.
- AC2: Passed. Supabase/Flyway checks ran successfully: info, migrate, info, import-master-data, import-bands, verify-bands-import, and verify-auth-setup.
- AC3: Passed. No external checks were skipped; credentials, network, Flyway, and psql were available.
- AC4: Passed. This note contains the delivery-governance validation package.
- AC5: Impact notes recorded below.

## How to test

### Automated tests

- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew test
- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew qaTest
- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew assembleDebug

### Manual / backend validation

- backend/flyway/run-flyway.sh info: schema version 005, all migrations successful.
- backend/flyway/run-flyway.sh migrate: schema up to date, no migration necessary.
- backend/flyway/run-flyway.sh info: schema version 005 confirmed after migrate.
- backend/flyway/import-master-data.sh: succeeded.
- backend/flyway/import-bands.sh: imported/updated 165 bands.
- backend/flyway/verify-bands-import.sh: 165 CSV rows matched 165 active database rows; no missing, extra, or mismatched active rows.
- backend/flyway/verify-auth-setup.sh: trigger, profile insert policy, ratings RLS policies, and default group verified.

## TDD / BDD / approval-test evidence

This was release validation, not a behavior change. Existing automated tests and QA scenarios passed.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR: none.

## README impact

README impact: none, because this task only executed existing documented validation and backend commands.

## Business requirements impact

Business requirements impact: none, because no product scope or business rule changed.

## Diagram impact

Diagram impact: none, because no architecture or workflow diagram changed.

## Commits / logical change list

- Ran final Gradle validation.
- Ran final Flyway and Supabase backend readiness checks.

## Risks and follow-up

No validation or backend-readiness release blockers remain from this task.
<!-- SECTION:NOTES:END -->

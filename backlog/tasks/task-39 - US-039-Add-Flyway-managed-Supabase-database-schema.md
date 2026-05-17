---
id: task-39
title: 'US-039: Add Flyway-managed Supabase database schema'
status: Done
assignee:
  - '@codex'
created_date: '2026-05-17 16:52'
updated_date: '2026-05-17 17:45'
labels:
  - backend
  - database
  - flyway
  - supabase
  - architecture
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As the app owner, I want the Supabase Postgres schema managed with Flyway migrations so that database changes are repeatable, reviewable, and safe to apply from the repository.

Business value:
- Creates the central database foundation for shared ratings, central band management, and future admin workflows.
- Makes schema changes explicit and reproducible instead of manual Supabase dashboard edits.

In scope:
- Add a backend Flyway migration structure to the repository.
- Add local-only environment setup for the Supabase database connection.
- Add migrations for core tables: profiles, groups, group_members, bands, stages, performances, stage_distances, food_options, ratings, and import batches.
- Add indexes, timestamps, foreign keys, and updated_at handling where needed.
- Add row-level security baseline policies for authenticated users, group members, and admins.
- Document how to run migrations against the configured Supabase project.

Out of scope:
- Android app integration with Supabase.
- Admin UI.
- Real-time rating sync.
- Committing database passwords or service-role secrets.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given a clean Supabase Postgres database, when Flyway migrations are run, then the core Wacken Planner schema is created successfully.
- [x] #2 Given migrations have already run, when Flyway is run again, then it reports the schema is up to date without recreating data.
- [x] #3 Given the schema is created, then foreign keys, indexes, timestamps, and RLS policies protect the central data model.
- [x] #4 Given local database credentials are needed, then they are loaded from ignored local env/config files and are not committed to the repository.
- [x] #5 Architecture impact is assessed before implementation; if additional architecture-significant decisions are needed, explicit approval is requested before coding.
- [x] #6 An ADR is created or updated for the backend database and migration strategy.
- [x] #7 README documents Flyway setup, migration commands, and secret handling.

- [x] #8 Given the Wacken bands CSV is uploaded with the import script, then the database bands table matches the CSV data and rerunning the upload is idempotent.
- [x] #9 Given the app or a validation query reads bands from the database after upload, then the returned band data matches the effect of uploading the same CSV again.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Confirmed task, ADR rules, secret handling, repository setup, local flyway, and local psql availability.
2. Used the user-approved standard option: Flyway SQL migrations for Supabase Postgres.
3. Added ignored Supabase env pattern and backend/flyway/env.template without secrets.
4. Added Flyway runner plus bands import and verification scripts; scripts prefer local flyway/psql and only fall back to Docker where applicable.
5. Added schema migrations for core tables, indexes, updated_at triggers, RLS baseline policies, and default Wacken 2026 group seed.
6. Added ADR-0008 for Supabase Postgres + Flyway migrations.
7. Updated README with migration setup, commands, secret handling, and band upload flow; updated business requirements with Supabase backend integration.
8. Fixed local env user to postgres without exposing the password, ran Flyway against Supabase, verified second migrate is idempotent, uploaded bands CSV twice, and verified active DB bands match CSV ids/names.

Architecture impact: significant, approved by user. ADR-0008 accepted.
Security note: .env.supabase.local is ignored and was not staged; the database password previously shared in chat should still be rotated.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Implemented the approved Flyway/Supabase foundation. The repository now contains versioned Flyway SQL migrations for the central Supabase Postgres schema, local-secret setup through ignored env files, scripts for migrations, and an idempotent bands CSV upload plus verification flow.

The Supabase project was migrated successfully to schema version 003. The Wacken bands CSV was uploaded twice, and verification confirmed active database bands match the CSV by row count, id presence, and band name.

## Acceptance criteria validation

- AC1: Flyway migrate created the core schema successfully in Supabase.
- AC2: A second Flyway migrate reported schema public is up to date and no migration was necessary.
- AC3: V001/V002 include foreign keys, indexes, timestamps, update triggers, RLS helper functions, and RLS policies.
- AC4: Credentials are read from .env.supabase.local, which is ignored by git; no password was committed.
- AC5: Architecture impact was assessed and user approved option 2 before implementation.
- AC6: ADR-0008 documents Supabase Postgres plus Flyway migrations.
- AC7: README documents env setup, migration commands, secret handling, and band upload commands.
- AC8: import-bands.sh uploaded data/wacken-2026/bands.csv twice successfully; the script upserts CSV rows and marks absent Wacken-source bands inactive.
- AC9: verify-bands-import.sh confirmed 164 active database bands match 164 CSV rows with no missing active rows, extra active rows, or name mismatches.

## How to test

### Automated/executable validation

- bash -n backend/flyway/run-flyway.sh backend/flyway/import-bands.sh backend/flyway/verify-bands-import.sh
- backend/flyway/run-flyway.sh info
- backend/flyway/run-flyway.sh migrate
- backend/flyway/run-flyway.sh migrate
- backend/flyway/run-flyway.sh info
- backend/flyway/import-bands.sh
- backend/flyway/import-bands.sh
- backend/flyway/verify-bands-import.sh

### Manual validation

- Open Supabase dashboard and confirm schema version 003 in flyway_schema_history.
- Confirm public.bands contains 164 active rows after import.
- Confirm .env.supabase.local is untracked and ignored.

## TDD / BDD / approval-test evidence

This task is migration/script infrastructure. Validation was done with executable Flyway and psql commands against the actual Supabase Postgres project, including idempotent migration and idempotent band upload checks.

## Architecture impact

- Architecture-significant change: yes, backend database and migration strategy.
- Approval received: yes, user approved option 2.
- ADR: ADR-0008 added.

## README impact

README updated with backend database setup, local env guidance, Flyway commands, and band upload/verification commands.

## Diagram impact

No diagram update needed in this task; the backend database section and ADR describe the migration boundary.

## Commits / logical change list

- Added .env.supabase* ignore rule and env template.
- Added Flyway migrations V001-V003.
- Added migration, bands import, and verification scripts.
- Added ADR-0008.
- Updated README and business requirements for Supabase Postgres.
- Created backend integration follow-up tasks task-40 through task-43.

## Risks and follow-up

- The database password previously shared in chat should be rotated.
- Android Supabase integration is intentionally deferred to task-41 and rating sync to task-42.
- Admin import workflow beyond bands is deferred to task-43.
<!-- SECTION:NOTES:END -->

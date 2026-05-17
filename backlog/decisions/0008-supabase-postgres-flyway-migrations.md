# ADR 0008: Supabase Postgres With Flyway Migrations

## Status

Accepted

## Context

The app needs a central backend so multiple people can add ratings and the app
owner can manage bands, performances, stages, distances, and other admin data
centrally. Supabase Postgres was selected as the backend database direction.

Manual schema edits in the Supabase dashboard would be hard to review and
repeat. The repository needs a clear, versioned way to create and evolve the
backend schema.

## Decision

Use Flyway SQL migrations stored in `backend/flyway/sql` to manage the Supabase
Postgres schema.

The first migration set creates the core backend model for:

- profiles
- groups and group memberships
- bands
- stages
- performances
- stage distances
- food options
- ratings
- admin import batches

The schema includes foreign keys, indexes, timestamps, update triggers, row
level security, and a default Wacken 2026 group. Local credentials are supplied
through ignored environment files such as `.env.supabase.local`; database
passwords and service-role secrets must not be committed.

Band CSV upload is handled by a script that upserts `data/wacken-2026/bands.csv`
into the `bands` table. Re-running the upload is idempotent. Bands missing from
the CSV are marked inactive rather than deleted, avoiding accidental cascades
into performances or ratings while keeping normal reads equivalent to the active
CSV set.

## Consequences

Positive:
- Schema changes are reviewable and reproducible.
- Supabase can be rebuilt or updated from repository migrations.
- The backend database model is ready for multi-user ratings and central admin
  workflows.
- Band CSV upload can be repeated safely.

Negative / trade-offs:
- Developers must keep local Supabase credentials outside git.
- Flyway migrations need careful review because applied migrations should not be
  edited after production use.
- The first implementation has scripts and SQL migrations, but not yet Android
  Supabase integration or admin UI.

## Alternatives Considered

- Manual Supabase SQL Editor setup: rejected because it is not reproducible.
- Liquibase: rejected because the app currently benefits more from simple,
  Postgres-native SQL migrations than from a larger changelog framework.
- Full backend service with CI-owned migrations now: deferred because the MVP can
  start with repository scripts and local migration execution.

## Links

- Related task: task-39
- Related docs: [`business-requirements.md`](../docs/business-requirements.md)

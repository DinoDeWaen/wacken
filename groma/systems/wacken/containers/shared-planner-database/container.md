---
type: C4 Container
title: Shared planner database
status: stable
groma:
  id: shared-planner-database
  parent: wacken
  technology: PostgreSQL on Supabase
---

Owned planner schema, canonical catalog, member-scoped ratings, archive/correction RPCs and RLS, hosted by Supabase. SQL evidence is in backend/flyway/sql and ADR0016. Supabase Auth and hosted PostgREST belong to the external platform. Java-only analysis does not inspect the SQL.

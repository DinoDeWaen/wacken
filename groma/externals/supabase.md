---
type: C4 System
title: Supabase
status: stable
groma:
  id: supabase
---

Provides Auth and PostgREST access to shared festival master data, planning ratings, personal rating history, and group schedule locks.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [supabase](supabase.md) | [shared-planner-database](../systems/wacken/containers/shared-planner-database/container.md) | Serves authorized planner data | PostgREST / PostgreSQL |

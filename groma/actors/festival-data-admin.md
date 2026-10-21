---
type: C4 Actor
title: Festival data admin
status: stable
groma:
  id: festival-data-admin
---

Imports validated festival CSVs and reviews possible band links and metadata before saving them.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [festival-data-admin](festival-data-admin.md) | [app/src/main/java/be/wacken/planner/SettingsActivity.java](../../app/src/main/java/be/wacken/planner/SettingsActivity.java) | Manages festival data | Android UI |
| [festival-data-admin](festival-data-admin.md) | [app/src/main/java/be/wacken/planner/ImportCsvActivity.java](../../app/src/main/java/be/wacken/planner/ImportCsvActivity.java) | Imports festival CSV | Android document picker |

---
type: C4 Component
title: TSV and Room import transaction
status: stable
groma:
  id: tsvimporttransaction
  parent: android-application
  code:
    - scanner: java
      file: infrastructure/src/main/java/be/wacken/planner/infrastructure/TsvImportTransaction.java
  group: Local festival import
description: Restores TSV on cache failure
---

Consumer supplied by AppRepositories to local CSV review. Snapshots five existing TSV master files, runs replacement within the supplied WackenDatabase.runInTransaction callback and restores TSV files if the Room transaction fails. Restoration failure reports and preserves the backup path for recovery. It does not issue shared backend writes or turn a successful commit into failure solely for stale backup cleanup.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [infrastructure/src/main/java/be/wacken/planner/infrastructure/TsvImportTransaction.java](../../../../../../infrastructure/src/main/java/be/wacken/planner/infrastructure/TsvImportTransaction.java) | [app/src/main/java/be/wacken/planner/persistence/WackenDatabase.java](../../../../../../app/src/main/java/be/wacken/planner/persistence/WackenDatabase.java) | Invokes supplied Room transaction | Composed Java callback |

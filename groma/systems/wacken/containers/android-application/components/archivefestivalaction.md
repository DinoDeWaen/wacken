---
type: C4 Component
title: Archive screen worker
status: stable
groma:
  id: archivefestivalaction
  parent: android-application
  code:
    - scanner: java
      file: app/src/main/java/be/wacken/planner/ArchiveFestivalAction.java
      symbol: ArchiveFestivalAction
  group: Festival archive and correction
description: Runs supplied archive command
---

MainActivity supplies AppRepositories.archiveFestivalUseCase through a Supplier. Executes it on the worker and returns success/failure on the UI executor; does not itself choose promotion or persistence rules.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [app/src/main/java/be/wacken/planner/ArchiveFestivalAction.java](../../../../../../app/src/main/java/be/wacken/planner/ArchiveFestivalAction.java) | [application/src/main/java/be/wacken/planner/application/ArchiveActiveFestivalUseCase.java](../../../../../../application/src/main/java/be/wacken/planner/application/ArchiveActiveFestivalUseCase.java) | Runs supplied archive command | Composed Java Supplier |

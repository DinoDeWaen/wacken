---
type: C4 Component
title: Atomic festival archive port
status: stable
groma:
  id: festivalarchivegateway
  parent: android-application
  code:
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/FestivalArchiveGateway.java
      symbol: FestivalArchiveGateway
  group: Festival archive and correction
description: Commits archive and promotion
---

Application output contract invoked by ArchiveActiveFestivalUseCase. AppRepositories supplies a pre-syncing callback which verifies no pending work before delegating to the remote archive adapter. Missing/reset after-ratings retain established values; actual atomic promotion is implemented by SQL V016.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [application/src/main/java/be/wacken/planner/application/FestivalArchiveGateway.java](../../../../../../application/src/main/java/be/wacken/planner/application/FestivalArchiveGateway.java) | [app/src/main/java/be/wacken/planner/AppRepositories.java](../../../../../../app/src/main/java/be/wacken/planner/AppRepositories.java) | Runs configured archive callback | Composed Java gateway lambda |

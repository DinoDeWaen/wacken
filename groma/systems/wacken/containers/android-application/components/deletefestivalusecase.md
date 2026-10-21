---
type: C4 Component
title: Festival deletion use case
status: stable
groma:
  id: deletefestivalusecase
  parent: android-application
  code:
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/DeleteFestivalUseCase.java
  group: Festival deletion
description: Deletes the selected existing festival
---

Resolves the requested festival from the repository and invokes its configured Gateway; missing festivals require refresh. AppRepositories binds SupabaseFestivalDeletionGateway, separating application selection from remote deletion and local cache cleanup.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [application/src/main/java/be/wacken/planner/application/DeleteFestivalUseCase.java](../../../../../../application/src/main/java/be/wacken/planner/application/DeleteFestivalUseCase.java) | [app/src/main/java/be/wacken/planner/SupabaseFestivalDeletionGateway.java](../../../../../../app/src/main/java/be/wacken/planner/SupabaseFestivalDeletionGateway.java) | Invokes configured deletion gateway | Java interface dispatch |

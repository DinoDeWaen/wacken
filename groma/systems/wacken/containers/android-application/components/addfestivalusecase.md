---
type: C4 Component
title: Festival lifecycle use cases
status: stable
groma:
  id: addfestivalusecase
  parent: android-application
  code:
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/AddFestivalUseCase.java
      symbol: AddFestivalUseCase
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/AddFestivalResult.java
      symbol: AddFestivalResult
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/ArchiveActiveFestivalUseCase.java
      symbol: ArchiveActiveFestivalUseCase
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/RenameActiveFestivalUseCase.java
      symbol: RenameActiveFestivalUseCase
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/RenameActiveFestivalResult.java
      symbol: RenameActiveFestivalResult
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/ShowFestivalStartUseCase.java
      symbol: ShowFestivalStartUseCase
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/FestivalStartState.java
      symbol: FestivalStartState
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/ApplicationBoundary.java
      symbol: ApplicationBoundary
  group: Application workflows
description: Add, rename, archive, and enter festivals
---

Orchestrates active-festival transitions, validates new festival input, and prepares the start state and results presented by Android screens.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [application/src/main/java/be/wacken/planner/application/AddFestivalUseCase.java](../../../../../../application/src/main/java/be/wacken/planner/application/AddFestivalUseCase.java) | [domain/src/main/java/be/wacken/planner/domain/Festival.java](../../../../../../domain/src/main/java/be/wacken/planner/domain/Festival.java) | Creates active festival | Java call |

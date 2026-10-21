---
type: C4 Component
title: Shared schedule generation
status: stable
groma:
  id: generatesharedscheduleusecase
  parent: android-application
  code:
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/GenerateSharedScheduleUseCase.java
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/SharedSchedule.java
      symbol: SharedSchedule
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/SharedScheduleStatus.java
      symbol: SharedScheduleStatus
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/ScheduleDay.java
      symbol: ScheduleDay
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/TimelineSlot.java
      symbol: TimelineSlot
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/ScheduleDecisionCandidate.java
      symbol: ScheduleDecisionCandidate
  group: Application workflows
description: Build a conflict-aware day timeline
---

Reads performances, group ratings, and stage distances, applies domain conflict decisions, and returns day and slot projections for the calendar.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [application/src/main/java/be/wacken/planner/application/GenerateSharedScheduleUseCase.java](../../../../../../application/src/main/java/be/wacken/planner/application/GenerateSharedScheduleUseCase.java) | [domain/src/main/java/be/wacken/planner/domain/PerformanceConflictResolver.java](../../../../../../domain/src/main/java/be/wacken/planner/domain/PerformanceConflictResolver.java) | Resolves performance conflicts | Java call |
| [application/src/main/java/be/wacken/planner/application/GenerateSharedScheduleUseCase.java](../../../../../../application/src/main/java/be/wacken/planner/application/GenerateSharedScheduleUseCase.java) | [domain/src/main/java/be/wacken/planner/domain/PerformanceRepository.java](../../../../../../domain/src/main/java/be/wacken/planner/domain/PerformanceRepository.java) | Reads performances | Repository port |
| [application/src/main/java/be/wacken/planner/application/GenerateSharedScheduleUseCase.java](../../../../../../application/src/main/java/be/wacken/planner/application/GenerateSharedScheduleUseCase.java) | [domain/src/main/java/be/wacken/planner/domain/Performance.java](../../../../../../domain/src/main/java/be/wacken/planner/domain/Performance.java) | Builds performance timeline | Java call |
| [application/src/main/java/be/wacken/planner/application/GenerateSharedScheduleUseCase.java](../../../../../../application/src/main/java/be/wacken/planner/application/GenerateSharedScheduleUseCase.java) | [application/src/main/java/be/wacken/planner/application/MemberRatingProjection.java](../../../../../../application/src/main/java/be/wacken/planner/application/MemberRatingProjection.java) | Projects member planning stars | Java call |

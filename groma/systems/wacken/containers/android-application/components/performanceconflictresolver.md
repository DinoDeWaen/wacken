---
type: C4 Component
title: Schedule decision rules
status: stable
groma:
  id: performanceconflictresolver
  parent: android-application
  code:
    - scanner: java
      file: domain/src/main/java/be/wacken/planner/domain/PerformanceConflictResolver.java
    - scanner: java
      file: domain/src/main/java/be/wacken/planner/domain/PerformanceConflictDetector.java
      symbol: PerformanceConflictDetector
    - scanner: java
      file: domain/src/main/java/be/wacken/planner/domain/PerformanceConflictSet.java
      symbol: PerformanceConflictSet
    - scanner: java
      file: domain/src/main/java/be/wacken/planner/domain/PerformanceConflictResolution.java
      symbol: PerformanceConflictResolution
    - scanner: java
      file: domain/src/main/java/be/wacken/planner/domain/PerformanceOverlapPolicy.java
    - scanner: java
      file: domain/src/main/java/be/wacken/planner/domain/StageWalkingTimePolicy.java
      symbol: StageWalkingTimePolicy
    - scanner: java
      file: domain/src/main/java/be/wacken/planner/domain/GroupDecision.java
      symbol: GroupDecision
    - scanner: java
      file: domain/src/main/java/be/wacken/planner/domain/GroupDecisionPolicy.java
      symbol: GroupDecisionPolicy
    - scanner: java
      file: domain/src/main/java/be/wacken/planner/domain/GroupDecisionStatus.java
      symbol: GroupDecisionStatus
  group: Domain model
description: Conflict detection and group choice policy
---

Detects overlapping performances and applies group preferences, vetoes, optional choices, and walking-time context to decide schedule candidates.

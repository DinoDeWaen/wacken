---
type: C4 Component
title: Festival lifecycle model
status: stable
groma:
  id: festival
  parent: android-application
  code:
    - scanner: java
      file: domain/src/main/java/be/wacken/planner/domain/Festival.java
      symbol: Festival
    - scanner: java
      file: domain/src/main/java/be/wacken/planner/domain/FestivalStatus.java
      symbol: FestivalStatus
    - scanner: java
      file: domain/src/main/java/be/wacken/planner/domain/FestivalLifecycle.java
      symbol: FestivalLifecycle
    - scanner: java
      file: domain/src/main/java/be/wacken/planner/domain/FestivalLineupEntry.java
      symbol: FestivalLineupEntry
  group: Domain model
description: Festival identity, status, and lineup
---

Models an active or archived festival and its lineup entries, including lifecycle constraints used when a festival is archived or added.

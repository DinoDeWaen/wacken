---
type: C4 Component
title: Room correction publication
status: stable
groma:
  id: roomarchivedratingcorrectioncache
  parent: android-application
  code:
    - scanner: java
      file: app/src/main/java/be/wacken/planner/RoomArchivedRatingCorrectionCache.java
      symbol: RoomArchivedRatingCorrectionCache
  group: Festival archive and correction
description: Preserves newer pending edits
---

Captures exact pending operation/value and validates acknowledged member/festival/band/kind before transactional publication. Refuses newer pending edits; resolves only the reviewed pending fact, merges snapshots and appends immutable audit together. Original offline history date remains separate from correction time.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [app/src/main/java/be/wacken/planner/RoomArchivedRatingCorrectionCache.java](../../../../../../app/src/main/java/be/wacken/planner/RoomArchivedRatingCorrectionCache.java) | [app/src/main/java/be/wacken/planner/persistence/WackenDatabase.java](../../../../../../app/src/main/java/be/wacken/planner/persistence/WackenDatabase.java) | Guards transactional publication | Java call |
| [app/src/main/java/be/wacken/planner/RoomArchivedRatingCorrectionCache.java](../../../../../../app/src/main/java/be/wacken/planner/RoomArchivedRatingCorrectionCache.java) | [app/src/main/java/be/wacken/planner/persistence/RoomFestivalAfterRatingRepository.java](../../../../../../app/src/main/java/be/wacken/planner/persistence/RoomFestivalAfterRatingRepository.java) | Merges own rating snapshots | Java call |
| [app/src/main/java/be/wacken/planner/RoomArchivedRatingCorrectionCache.java](../../../../../../app/src/main/java/be/wacken/planner/RoomArchivedRatingCorrectionCache.java) | [app/src/main/java/be/wacken/planner/persistence/RoomRatingCorrectionRepository.java](../../../../../../app/src/main/java/be/wacken/planner/persistence/RoomRatingCorrectionRepository.java) | Appends acknowledged audit | Java call |

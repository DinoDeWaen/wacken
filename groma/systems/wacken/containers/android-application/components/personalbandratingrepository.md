---
type: C4 Component
title: Established personal band opinion contract
status: stable
groma:
  id: personalbandratingrepository
  parent: android-application
  code:
    - scanner: java
      file: domain/src/main/java/be/wacken/planner/domain/PersonalBandRatingRepository.java
      symbol: PersonalBandRatingRepository
    - scanner: java
      file: domain/src/main/java/be/wacken/planner/domain/PersonalBandRating.java
      symbol: PersonalBandRating
  group: Festival rating context
description: Reads archive-established opinions
---

Read-only domain access and provenance-bearing PersonalBandRating values. Each member has their own opinion; source festival/name/event survive deletion. Nonempty archive promotion and eligible current-source correction are the write mechanisms, rather than latest-event fallback or normal detail edits.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [domain/src/main/java/be/wacken/planner/domain/PersonalBandRatingRepository.java](../../../../../../domain/src/main/java/be/wacken/planner/domain/PersonalBandRatingRepository.java) | [app/src/main/java/be/wacken/planner/persistence/RoomPersonalBandRatingRepository.java](../../../../../../app/src/main/java/be/wacken/planner/persistence/RoomPersonalBandRatingRepository.java) | Uses composed opinion reader | Composed Java port dispatch |

---
type: C4 Component
title: Member planning-rating projection
status: stable
groma:
  id: memberratingprojection
  parent: wacken
  code:
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/MemberRatingProjection.java
      symbol: MemberRatingProjection
  group: Application workflows
description: Projects scoped member planning stars
---

Projects an already festival-scoped planning snapshot by exact canonical band name, retains positive values including veto stars and preserves stable case-insensitive member ordering. List, detail, schedule and CSV workflows reuse this read-only transformation. Repository selection, archive scope, decision rules and CSV formatting remain in their existing owners; after-ratings and established ratings are not substituted.

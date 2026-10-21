---
type: C4 Component
title: Festival identity review port
status: stable
groma:
  id: festivalbandcatalog
  parent: android-application
  code:
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/FestivalBandCatalog.java
  group: Festival creation and identity
description: Loads canonical bands and aliases
---

Application read port returning immutable canonical band and confirmed-alias snapshots. ReviewFestivalBandsUseCase consumes fresh catalog data; Android composition supplies SupabaseFestivalBandCatalog. A failed read must prevent presenting incomplete choices as authoritative.

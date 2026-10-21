---
type: C4 Component
title: Festival CSV parsing
status: stable
groma:
  id: festivalcsvreader
  parent: android-application
  code:
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/FestivalCsvReader.java
  group: Local festival import
description: Reads validated CSV rows
---

Shared application CSV reader for lineup names and local master-data review. Supplies parsed rows/headers and format errors before writes; callers own group-wide creation versus local five-file acceptance. Parsing does not confirm canonical identity or mutate ratings.

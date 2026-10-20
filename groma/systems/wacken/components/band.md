---
type: C4 Component
title: Band catalog model
status: stable
groma:
  id: band
  parent: wacken
  code:
    - scanner: java
      file: domain/src/main/java/be/wacken/planner/domain/Band.java
      symbol: Band
    - scanner: java
      file: domain/src/main/java/be/wacken/planner/domain/BandVisibilityPolicy.java
      symbol: BandVisibilityPolicy
    - scanner: java
      file: domain/src/main/java/be/wacken/planner/domain/DomainBoundary.java
      symbol: DomainBoundary
    - scanner: java
      file: domain/src/main/java/be/wacken/planner/domain/DomainValidationException.java
      symbol: DomainValidationException
  group: Domain model
description: Band identity and visibility rules
---

Models reusable bands and the rule that hides generic Metal Battle placeholders from rating views while preserving imported records.

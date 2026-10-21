---
type: C4 Component
title: Imported band linking
status: stable
groma:
  id: reviewimportedbandlinksusecase
  parent: android-application
  code:
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/ReviewImportedBandLinksUseCase.java
      symbol: ReviewImportedBandLinksUseCase
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/LinkImportedBandUseCase.java
      symbol: LinkImportedBandUseCase
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/BandLinkCandidate.java
      symbol: BandLinkCandidate
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/BandLinkResult.java
      symbol: BandLinkResult
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/BandNameMatcher.java
      symbol: BandNameMatcher
  group: Application workflows
description: Review possible existing-band matches
---

Identifies likely catalog matches for imported names and applies an explicit user-reviewed band link.

---
type: C4 Component
title: Reviewed canonical band identity
status: stable
groma:
  id: canonicalband
  parent: android-application
  code:
    - scanner: java
      file: domain/src/main/java/be/wacken/planner/domain/CanonicalBand.java
      symbol: CanonicalBand
    - scanner: java
      file: domain/src/main/java/be/wacken/planner/domain/ConfirmedBandAlias.java
      symbol: ConfirmedBandAlias
    - scanner: java
      file: domain/src/main/java/be/wacken/planner/domain/ReviewedFestivalBand.java
      symbol: ReviewedFestivalBand
  group: Festival creation and identity
description: Protects explicit canonical selections
---

Defines the stable catalog ID/metadata value, confirmed alias provenance and reviewed uploaded-name selection. Alias keys fold ASCII case and spacing only, retaining punctuation and non-ASCII spelling. A new-band choice has no selected canonical value; remembering an alias requires an explicit selection. These values support identity review, not automatic fuzzy merging.

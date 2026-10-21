---
type: C4 Component
title: Band image request owner
status: stable
groma:
  id: bandimageloader
  parent: android-application
  code:
    - scanner: java
      file: app/src/main/java/be/wacken/planner/BandImageLoader.java
  group: Android screens
description: Loads current detail images safely
---

Owns one background image request for a detail screen, including 5-second connect/inactive-read limits, stream closure and HTTP disconnect. Replacing a target or destroying its owner cancels the current request and invalidates queued callbacks. Bitmap decoding and view updates stay with the Android screen; failures and null decode use its missing-image fallback. Does not read or write ratings, metadata or festival lifecycle state.

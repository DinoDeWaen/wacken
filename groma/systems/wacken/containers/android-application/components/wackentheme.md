---
type: C4 Component
title: Android visual theme
status: stable
groma:
  id: wackentheme
  parent: android-application
  code:
    - scanner: java
      file: app/src/main/java/be/wacken/planner/WackenTheme.java
    - scanner: java
      file: app/src/main/java/be/wacken/planner/AppearanceController.java
    - scanner: java
      file: app/src/main/java/be/wacken/planner/AppearancePreference.java
  group: Android screens
description: Apply shared native appearance without replaying work
---

Provides the Wacken-inspired shared styling and semantic component helpers for Android screens. Resolves the device-local System, Ironline dark or Field Notes light preference and updates explicitly bound views, panels and system bars in place. Activity lifecycle and preference listeners refresh current appearance and detach on destruction; screen-owned drafts, retained sessions and pending business operations are not reconstructed or executed by restyling. Uses existing private Android SharedPreferences without backend synchronization. Pure palette policy and the controller implement this one presentation responsibility; ADR0024 records the approved design.

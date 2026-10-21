---
type: C4 Component
title: Local auth session
status: stable
groma:
  id: authsessionstore
  parent: android-application
  code:
    - scanner: java
      file: app/src/main/java/be/wacken/planner/AuthSessionStore.java
      symbol: AuthSessionStore
    - scanner: java
      file: app/src/main/java/be/wacken/planner/AuthSession.java
      symbol: AuthSession
    - scanner: java
      file: app/src/main/java/be/wacken/planner/AuthSessionRepository.java
      symbol: AuthSessionRepository
  group: Storage and synchronization
description: Persist and expose the signed-in session
---

Stores the current Supabase user and group context on device so app workflows can address shared and personal records.

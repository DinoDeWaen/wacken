---
type: C4 Component
title: Authenticated HTTP transport
status: stable
groma:
  id: supabaseauthenticatedrequest
  parent: wacken
  code:
    - scanner: java
      file: app/src/main/java/be/wacken/planner/SupabaseAuthenticatedRequest.java
    - scanner: java
      file: app/src/main/java/be/wacken/planner/SupabaseConfig.java
      symbol: SupabaseConfig
    - scanner: java
      file: app/src/main/java/be/wacken/planner/SupabaseDiagnostics.java
      symbol: SupabaseDiagnostics
    - scanner: java
      file: app/src/main/java/be/wacken/planner/SupabaseSyncException.java
      symbol: SupabaseSyncException
    - scanner: java
      file: app/src/main/java/be/wacken/planner/SupabaseErrorMessages.java
      symbol: SupabaseErrorMessages
  group: Storage and synchronization
description: Send configured Supabase requests
---

Adds configuration and session credentials to HTTP requests and reports backend failures used by remote repository adapters.

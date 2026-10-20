---
type: C4 Component
title: Supabase authentication adapter
status: stable
groma:
  id: supabaseauthclient
  parent: wacken
  code:
    - scanner: java
      file: app/src/main/java/be/wacken/planner/SupabaseAuthClient.java
    - scanner: java
      file: app/src/main/java/be/wacken/planner/SupabaseAuthGateway.java
      symbol: SupabaseAuthGateway
    - scanner: java
      file: app/src/main/java/be/wacken/planner/SupabaseSessionManager.java
      symbol: SupabaseSessionManager
    - scanner: java
      file: app/src/main/java/be/wacken/planner/AuthenticationRequiredException.java
      symbol: AuthenticationRequiredException
    - scanner: java
      file: app/src/main/java/be/wacken/planner/InvalidAuthSessionException.java
      symbol: InvalidAuthSessionException
  group: Storage and synchronization
description: Sign in and refresh authenticated sessions
---

Calls Supabase Auth, manages session lifecycle, and reports invalid or missing authentication to app workflows.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [app/src/main/java/be/wacken/planner/SupabaseAuthClient.java](../../../../app/src/main/java/be/wacken/planner/SupabaseAuthClient.java) | [supabase](../../../externals/supabase.md) | Authenticates users | HTTPS/Auth |
| [app/src/main/java/be/wacken/planner/SupabaseAuthClient.java](../../../../app/src/main/java/be/wacken/planner/SupabaseAuthClient.java) | [app/src/main/java/be/wacken/planner/SupabaseErrorMessages.java](../../../../app/src/main/java/be/wacken/planner/SupabaseErrorMessages.java) | Selects safe failure messages | Java call |

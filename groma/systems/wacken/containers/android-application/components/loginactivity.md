---
type: C4 Component
title: Login screen
status: stable
groma:
  id: loginactivity
  parent: android-application
  code:
    - scanner: java
      file: app/src/main/java/be/wacken/planner/LoginActivity.java
      symbol: LoginActivity
  group: Android screens
description: Authenticate a festival planner user
---

Collects credentials and starts a Supabase Auth session before the planning screens are entered.

The form scrolls and inputs grow with text. A screen-owned retained sign-in state keeps one active request, progress/failure and memory-only password draft through recreation; only the existing acknowledged Auth client result writes the existing session store. The worker holds application context and retained data, while replaceable lifecycle observers route to the existing Main destination. Passwords are excluded from saved hierarchy/Bundle state; palette changes do not submit credentials.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [app/src/main/java/be/wacken/planner/LoginActivity.java](../../../../../../app/src/main/java/be/wacken/planner/LoginActivity.java) | [app/src/main/java/be/wacken/planner/AuthSessionStore.java](../../../../../../app/src/main/java/be/wacken/planner/AuthSessionStore.java) | Stores signed-in session | Java call |
| [app/src/main/java/be/wacken/planner/LoginActivity.java](../../../../../../app/src/main/java/be/wacken/planner/LoginActivity.java) | [app/src/main/java/be/wacken/planner/WackenTheme.java](../../../../../../app/src/main/java/be/wacken/planner/WackenTheme.java) | Styles sign-in controls | Android Views |

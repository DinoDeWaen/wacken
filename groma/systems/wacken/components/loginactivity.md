---
type: C4 Component
title: Login screen
status: stable
groma:
  id: loginactivity
  parent: wacken
  code:
    - scanner: java
      file: app/src/main/java/be/wacken/planner/LoginActivity.java
      symbol: LoginActivity
  group: Android screens
description: Authenticate a festival planner user
---

Collects credentials and starts a Supabase Auth session before the planning screens are entered.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [app/src/main/java/be/wacken/planner/LoginActivity.java](../../../../app/src/main/java/be/wacken/planner/LoginActivity.java) | [app/src/main/java/be/wacken/planner/AuthSessionStore.java](../../../../app/src/main/java/be/wacken/planner/AuthSessionStore.java) | Stores signed-in session | Java call |

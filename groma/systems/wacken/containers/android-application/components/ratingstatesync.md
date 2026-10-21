---
type: C4 Component
title: After-rating state synchronization
status: stable
groma:
  id: ratingstatesync
  parent: android-application
  code:
    - scanner: java
      file: app/src/main/java/be/wacken/planner/RatingStateSync.java
  group: Rating synchronization
description: Pushes pending and pulls own snapshots
---

AppRepositories invokes this coordinator. Pushes explicit pending facts before acknowledging the exact operation, skips migrated facts needing review, then fetches both complete own after-rating and established-opinion snapshots before cache merge. Exceptions preserve pending work; established state is read from the server.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [app/src/main/java/be/wacken/planner/RatingStateSync.java](../../../../../../app/src/main/java/be/wacken/planner/RatingStateSync.java) | [app/src/main/java/be/wacken/planner/RatingStateSyncStore.java](../../../../../../app/src/main/java/be/wacken/planner/RatingStateSyncStore.java) | Acknowledges and merges own state | Java call |
| [app/src/main/java/be/wacken/planner/RatingStateSync.java](../../../../../../app/src/main/java/be/wacken/planner/RatingStateSync.java) | [app/src/main/java/be/wacken/planner/SupabaseRatingStateClient.java](../../../../../../app/src/main/java/be/wacken/planner/SupabaseRatingStateClient.java) | Pushes and fetches snapshots | Composed Java remote dispatch |

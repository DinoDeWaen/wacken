---
type: C4 Component
title: Verified music link review screen
status: stable
groma:
  id: musiclinksreviewactivity
  parent: android-application
  code:
    - scanner: java
      file: app/src/main/java/be/wacken/planner/MusicLinksReviewActivity.java
  group: Verified music discovery
description: Reviews missing links before explicit save
---

Settings supplies active festival ID or BandDetailActivity supplies one canonical band name. Retained worker/session preserves completed reviews across rotation, cancellation interrupts unfinished lookup, edited terms invalidate choices and never rename bands. Only verified selected identity plus explicit acknowledgement enables save. Search may be public, while save uses the captured account-bound shared repository; no claim is made that an account switch automatically cancels public lookup.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [app/src/main/java/be/wacken/planner/MusicLinksReviewActivity.java](../../../../../../app/src/main/java/be/wacken/planner/MusicLinksReviewActivity.java) | [application/src/main/java/be/wacken/planner/application/FindBandMusicLinksUseCase.java](../../../../../../application/src/main/java/be/wacken/planner/application/FindBandMusicLinksUseCase.java) | Searches and approves missing links | Java call |
| [app/src/main/java/be/wacken/planner/MusicLinksReviewActivity.java](../../../../../../app/src/main/java/be/wacken/planner/MusicLinksReviewActivity.java) | [app/src/main/java/be/wacken/planner/VerifiedMusicLinksProvider.java](../../../../../../app/src/main/java/be/wacken/planner/VerifiedMusicLinksProvider.java) | Composes evidence collector | Java call |
| [app/src/main/java/be/wacken/planner/MusicLinksReviewActivity.java](../../../../../../app/src/main/java/be/wacken/planner/MusicLinksReviewActivity.java) | [app/src/main/java/be/wacken/planner/AuthSessionStore.java](../../../../../../app/src/main/java/be/wacken/planner/AuthSessionStore.java) | Checks signed-in session | Java call |

---
type: C4 Component
title: Bounded official artist page reader
status: stable
groma:
  id: officialmusicpageclient
  parent: android-application
  code:
    - scanner: java
      file: app/src/main/java/be/wacken/planner/OfficialMusicPageClient.java
  group: Verified music discovery
description: Retrieves public HTTPS evidence
---

PageLoader used by the evidence collector retrieves public HTTPS HTML with five-second connect/read timeouts,512KiB limit and at most three redirects. Each destination is revalidated; private/local addresses, credentials, nonstandard ports and non-HTML responses fail. Uses no cookies, proxy, scripts or automatic redirects and checks interruption. Parsed anchors are evidence only; the application still verifies declared site and unique profile links.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [app/src/main/java/be/wacken/planner/OfficialMusicPageClient.java](../../../../../../app/src/main/java/be/wacken/planner/OfficialMusicPageClient.java) | [official-artist-websites](../../../../../externals/official-artist-websites.md) | Reads public profile anchors | Bounded HTTPS HTML |

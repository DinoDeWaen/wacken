---
type: C4 Component
title: Music identity evidence collector
status: stable
groma:
  id: verifiedmusiclinksprovider
  parent: android-application
  code:
    - scanner: java
      file: app/src/main/java/be/wacken/planner/VerifiedMusicLinksProvider.java
      symbol: VerifiedMusicLinksProvider
  group: Verified music discovery
description: Collects corroborating public source facts
---

Implements evidence lookup through existing MusicBrainz and Wikidata HTTP clients plus supplied official-page loader. Artist ID may refine search without renaming canonical band. Loads official pages only after corroborated identity. Optional configured Spotify/YouTube searches provide unverified fallback candidates when primary identity results are empty; credentials are optional and unavailable providers report status. Rate limits/source/page failures remain retryable; adapter never declares candidates trusted.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [app/src/main/java/be/wacken/planner/VerifiedMusicLinksProvider.java](../../../../../../app/src/main/java/be/wacken/planner/VerifiedMusicLinksProvider.java) | [app/src/main/java/be/wacken/planner/MusicBrainzMetadataProvider.java](../../../../../../app/src/main/java/be/wacken/planner/MusicBrainzMetadataProvider.java) | Queries artist identity facts | Nested Java HTTP client |
| [app/src/main/java/be/wacken/planner/VerifiedMusicLinksProvider.java](../../../../../../app/src/main/java/be/wacken/planner/VerifiedMusicLinksProvider.java) | [app/src/main/java/be/wacken/planner/WikidataMetadataProvider.java](../../../../../../app/src/main/java/be/wacken/planner/WikidataMetadataProvider.java) | Queries corroborating identifiers | Nested Java HTTP client |
| [app/src/main/java/be/wacken/planner/VerifiedMusicLinksProvider.java](../../../../../../app/src/main/java/be/wacken/planner/VerifiedMusicLinksProvider.java) | [app/src/main/java/be/wacken/planner/OfficialMusicPageClient.java](../../../../../../app/src/main/java/be/wacken/planner/OfficialMusicPageClient.java) | Retrieves declared site anchors | Composed Java PageLoader callback |
| [app/src/main/java/be/wacken/planner/VerifiedMusicLinksProvider.java](../../../../../../app/src/main/java/be/wacken/planner/VerifiedMusicLinksProvider.java) | [app/src/main/java/be/wacken/planner/SpotifyMetadataProvider.java](../../../../../../app/src/main/java/be/wacken/planner/SpotifyMetadataProvider.java) | Collects optional unverified fallback | Composed Java provider dispatch |
| [app/src/main/java/be/wacken/planner/VerifiedMusicLinksProvider.java](../../../../../../app/src/main/java/be/wacken/planner/VerifiedMusicLinksProvider.java) | [app/src/main/java/be/wacken/planner/YouTubeMetadataProvider.java](../../../../../../app/src/main/java/be/wacken/planner/YouTubeMetadataProvider.java) | Collects optional unverified fallback | Composed Java provider dispatch |
| [app/src/main/java/be/wacken/planner/VerifiedMusicLinksProvider.java](../../../../../../app/src/main/java/be/wacken/planner/VerifiedMusicLinksProvider.java) | [application/src/main/java/be/wacken/planner/application/MusicLinkEvidence.java](../../../../../../application/src/main/java/be/wacken/planner/application/MusicLinkEvidence.java) | Builds raw evidence values | Java call |

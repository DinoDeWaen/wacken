---
id: task-24
title: 'US-024: Wire Android band detail navigation and rating save'
status: Done
assignee:
  - '@codex'
created_date: '2026-05-15 15:22'
updated_date: '2026-05-15 18:45'
labels:
  - mvp1
  - android
  - rating
dependencies:
  - task-21
  - task-23
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## US-024: Wire Android band detail navigation and rating save

**As an** attendee
**I want** to tap a band, rate it, and return to the list
**So that** the APK supports the core rating workflow end to end

### Notes
- Build on the existing `BandDetailActivity` and rating use cases.
- Persist ratings through the MVP local persistence adapter.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given I tap a band in the list When the detail screen opens Then it shows that band and the current effective rating
- [x] #2 Given I select a 0-4 rating When I return to the list Then the new rating is shown
- [x] #3 Given a band has YouTube or Spotify metadata When I open details Then available links are shown and missing links are hidden
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Added focused tests for persisted band music metadata from CSV import and file-backed band storage.
2. Extended Band metadata minimally so imported YouTube/Spotify links can reach the detail screen without changing use-case boundaries.
3. Made MainActivity render clickable band rows that open BandDetailActivity with current rating and available links.
4. Kept rating saves through RateBandUseCase and file-backed repositories, then refreshed the list on return via MainActivity.onResume.
5. README update was not needed because existing MVP behavior already says users can rate bands.
6. Validated with Gradle tests, QA scenarios, and debug APK build.

Architecture impact: not architecture-significant; this extends an existing domain concept with imported display metadata and keeps persistence behind existing repository ports.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Implementation notes:
- Band now carries optional YouTube and Spotify links while preserving the existing Band(name) constructor.
- CSV import maps youtube_url and spotify_artist_id into band metadata; Spotify artist ids become open.spotify.com artist links.
- File-backed band storage persists optional music links.
- MainActivity renders clickable band rows and passes rating/default/link extras to BandDetailActivity.
- BandDetailActivity preserves stored band metadata and saves ratings through the existing rate use case.

Validation package:
- Automated checks run: JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :application:test :infrastructure:test; JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew test; JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew qaTest; JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew assembleDebug.
- Manual validation: not run on device yet.
- README impact: no update needed; README already documents band rating behavior.
- Diagram impact: no diagram update needed.
- ADR impact: no ADR needed; no new architecture decision.
- Approval status: no architecture approval required.
- Risks: The list UI is intentionally minimal; task-25 covers UAT checklist and sample data for device-level validation.
<!-- SECTION:NOTES:END -->

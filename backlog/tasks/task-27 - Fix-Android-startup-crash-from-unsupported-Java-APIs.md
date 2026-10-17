---
id: task-27
title: Fix Android startup crash from unsupported Java APIs
status: Done
assignee:
  - '@codex'
created_date: '2026-05-15 19:52'
updated_date: '2026-05-15 19:54'
labels:
  - bug
  - android
  - mvp1
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## Bug: Fix Android startup crash from unsupported Java APIs

**As a** tester
**I want** the APK to open on Android without crashing
**So that** MVP 1 can be tested from the built app

### Notes
- adb is not available in this shell, so logcat could not be pulled.
- Startup path uses Java 9+/16 runtime APIs such as List.of and Stream.toList in production code. These can crash on Android without core library desugaring.
- Detail screen also uses String.repeat and application result uses List.copyOf.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Production code avoids unsupported Java 9+ convenience APIs on Android runtime paths
- [x] #2 Debug APK builds successfully
- [x] #3 Gradle test and qaTest pass
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Replaced Java 9+/16 convenience APIs in production runtime code with Java 8-compatible equivalents.
2. Kept repository behavior and application use-case behavior unchanged.
3. Ran test, qaTest, and assembleDebug with JDK 21.
4. Closed the task and committed the fix.

Architecture impact: none; compatibility bug fix only.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Implementation notes:
- Removed production runtime use of List.of, Stream.toList, List.copyOf, and String.repeat.
- Kept test code unchanged; the APK only executes production code.
- adb is not available in this shell, so logcat/device verification could not be run here.

Validation package:
- Automated checks run: JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew test; JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew qaTest; JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew assembleDebug.
- Manual validation: not run on device because adb is not available in this environment.
- README impact: no update needed.
- Diagram impact: no diagram update needed.
- ADR impact: no ADR needed.
- Approval status: no architecture approval required.
- Risks: if the device is below Android 8.0/API 26, the APK is not supported by the current minSdk; otherwise this removes the likely startup crash source.
<!-- SECTION:NOTES:END -->

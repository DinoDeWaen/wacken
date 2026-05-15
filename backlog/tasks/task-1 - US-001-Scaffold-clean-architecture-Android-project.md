---
id: task-1
title: 'US-001: Scaffold clean architecture Android project'
status: Done
assignee:
  - '@codex'
created_date: '2026-01-06 16:56'
updated_date: '2026-05-15 08:19'
labels: []
dependencies: []
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## US-001: Scaffold clean architecture Android project

**As a** developer
**I want** a multi-module Android project aligned with clean architecture
**So that** features can evolve without mixing layers
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given the repository When I run ./gradlew assembleDebug Then a debug APK is produced successfully
- [x] #2 Given the project structure When I inspect modules Then I see domain, application, infrastructure, and android app modules with dependencies only pointing inward
- [x] #3 Given the domain and application modules When I check dependencies Then they compile without any Android framework references
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Created a minimal multi-module Gradle/Android scaffold: `app`, `domain`, `application`, and `infrastructure`.
2. Kept dependency direction inward: `application -> domain`, `infrastructure -> application/domain`, `app -> application/infrastructure/domain`.
3. Added minimal Java source and Android manifest/resource files to prove each module compiles and the app packages.
4. Added Gradle wrapper/build files and validated module discovery and APK packaging.
5. Updated README with setup, module map, and module responsibilities.
6. Added ADR 0001 for the approved initial Android Clean Architecture scaffold.
7. Validated with `./gradlew assembleDebug`, `./gradlew :domain:compileJava :application:compileJava`, APK existence check, and grep confirming no Android references in `domain` or `application`.

Deviation: global `gradle` is installed but fails before build startup due to a native-platform dylib load error, so the checked-in Gradle wrapper is used. Gradle wrapper/cache access required elevated permission because `~/.gradle` is outside the workspace sandbox.
Architecture impact: architecture-significant and explicitly approved by the user before implementation. ADR 0001 added.
README impact: updated.
Diagram impact: updated README module map Mermaid diagram.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Created the initial Wacken Planner Android/Java Clean Architecture scaffold with four modules: `domain`, `application`, `infrastructure`, and `app`. The Android app module packages a debug APK, while `domain` and `application` remain plain Java modules without Android framework references.

Added Gradle wrapper/build files, minimal compile-proof source files, a simple launcher activity, README module/setup documentation, and ADR 0001 for the approved architecture scaffold.

## Acceptance criteria validation

- AC1: `./gradlew assembleDebug` passed and produced `app/build/outputs/apk/debug/app-debug.apk`.
- AC2: `./gradlew projects` showed `:app`, `:application`, `:domain`, and `:infrastructure`; Gradle dependencies point inward.
- AC3: `./gradlew :domain:compileJava :application:compileJava` passed, and `rg -n "android\.|com\.android|androidx" domain application` returned no matches.

## How to test

### Automated tests

- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew assembleDebug`
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:compileJava :application:compileJava`

### Manual validation

- Confirmed the debug APK exists at `app/build/outputs/apk/debug/app-debug.apk`.
- Inspected module declarations and dependency direction in `settings.gradle` and module `build.gradle` files.

## TDD / BDD / approval-test evidence

No business behavior was implemented in this scaffold task, so no domain BDD/TDD tests were added. Validation is build and boundary focused.

## Architecture impact

- Architecture-significant change: yes, this establishes the project module/build architecture.
- Approval received: yes, user approved the minimal scaffold option before implementation.
- ADR: added `backlog/decisions/0001-initial-android-clean-architecture-scaffold.md`.

## README impact

Updated README with module responsibilities, a Mermaid module map, ADR link, setup notes, and useful build commands.

## Diagram impact

Updated README with a Mermaid module dependency map.

## Commits / logical change list

- `dcc3223` Scaffold Gradle modules for Android app
- `3171f1d` Add minimal app and module source boundaries
- `7b32e03` Document scaffold architecture decision

## Risks and follow-up

- The global `gradle` command fails locally before build startup due to a native-platform dylib load error; the checked-in Gradle wrapper works and should be used.
- Gradle wrapper/cache access needed elevated permission because `~/.gradle` is outside the workspace sandbox.
- Real domain entities, tests, CI, and feature behavior remain in later backlog tasks.
<!-- SECTION:NOTES:END -->

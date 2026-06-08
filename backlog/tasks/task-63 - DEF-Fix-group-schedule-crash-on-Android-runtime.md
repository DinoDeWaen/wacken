---
id: task-63
title: 'DEF: Fix group schedule crash on Android runtime'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-08 07:16'
updated_date: '2026-06-08 07:20'
labels:
  - android
  - mvp2
  - defect
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a signed-in Android user, I want the group schedule screen to open without crashing, so MVP2 schedule validation can run on device.

Root-cause candidate: MVP2 schedule generation uses Java Stream.toList() in Android runtime code. On Android devices without that API available, this can throw NoSuchMethodError; ScheduleActivity catches Exception but not Error, so the app crashes.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given a signed-in user taps View group schedule, when schedule generation runs on Android, then the app does not call Java APIs unavailable on the supported Android runtime.
- [x] #2 Given representative schedule data exists, when GenerateSharedScheduleUseCase runs, then domain/application tests still pass.
- [x] #3 Given the APK is rebuilt, when Android Java compile and assembleDebug run, then they pass.
- [x] #4 README/business requirements/diagram/ADR impact are recorded using delivery-governance wording.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Replaced Android-incompatible Java 9+ APIs in the MVP2 schedule runtime path: Stream.toList(), Optional.stream(), List.of(), List.copyOf(), and no-arg Optional.orElseThrow().
2. Replaced collection copies with Java 8-compatible ArrayList plus Collections.unmodifiableList/emptyList.
3. Ran a production-code compatibility scan for the unsupported APIs.
4. Ran domain/application/infrastructure tests, Android Java compile, assembleDebug, and git diff check.

Deviation: the fix expanded from Stream.toList() only to the adjacent Java 9+ APIs in the same shipped runtime surface to avoid a second Android NoSuchMethodError. Architecture impact: not architecture-significant; compatibility-only code change with no behavior/schema/API/dependency/module-boundary change. ADR impact: none.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Fixed the likely group schedule crash by removing Java APIs that compile on the desktop JDK but can be missing at Android runtime. The MVP2 schedule path no longer uses `Stream.toList()`, `Optional.stream()`, `List.of()`, `List.copyOf()`, or no-argument `Optional.orElseThrow()` in production code. The replacement uses Java 8-compatible collectors, explicit optional handling, `Collections.emptyList()`, and defensive `ArrayList` copies wrapped with `Collections.unmodifiableList()`.

Live Supabase verification from the investigation showed all current auth users are in the same `Sofie and Dino` group: 4 default-group members and 0 auth users missing default group membership.

## Acceptance criteria validation

- AC1: Production-code scan found no remaining unsupported schedule/runtime APIs: `rg -nP "(?<!Collectors)\.toList\(\)|List\.of\(|List\.copyOf\(|\.orElseThrow\(\)" app/src/main/java application/src/main/java domain/src/main/java infrastructure/src/main/java` returned no matches.
- AC2: Domain, application, and infrastructure tests passed.
- AC3: Android Java compile and `assembleDebug` passed; rebuilt APK is `app/build/outputs/apk/debug/app-debug.apk` with `versionCode 3`, `versionName 2.0`.
- AC4: Impact notes are recorded below.

## How to test

### Automated tests

- `/bin/zsh -lc JAVA_HOME=java21 ./gradlew :domain:test :application:test :infrastructure:test :app:compileDebugJavaWithJavac assembleDebug`
- `git diff --check`
- Production-code unsupported API scan shown above.

### Manual validation

- `adb devices` returned no connected devices or emulators, so installed-device verification could not be run here. Install the rebuilt APK and tap **View group schedule** as `dinodewaen` to confirm the crash is gone.

## TDD / BDD / approval-test evidence

This is a runtime compatibility defect fix. Existing schedule, conflict, and timeline tests protect the behavior while the implementation swaps unsupported APIs for Java 8-compatible equivalents.

## Architecture impact

- Architecture-significant change: no. This is a compatibility-only implementation change.
- Approval received: not required.
- ADR: none required.

## README impact

README impact: none, because setup, public behavior, architecture, commands, and troubleshooting did not change.

## Business requirements impact

Business requirements impact: none, because this fixes an Android runtime crash without changing business scope or rules.

## Diagram impact

Diagram impact: none, because the system structure and data flow did not change.

## Commits / logical change list

- Replace Java 9+ collection/optional APIs in the MVP2 schedule runtime surface with Java 8-compatible equivalents.
- Rebuild the V2.0 debug APK.

## Risks and follow-up

- Device verification remains needed because no Android device or emulator is connected in this environment.
<!-- SECTION:NOTES:END -->

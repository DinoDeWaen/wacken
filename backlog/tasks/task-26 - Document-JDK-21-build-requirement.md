---
id: task-26
title: Document JDK 21 build requirement
status: Done
assignee:
  - '@codex'
created_date: '2026-05-15 19:19'
updated_date: '2026-05-15 19:19'
labels:
  - docs
  - build
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## Bug: Document JDK 21 build requirement

**As a** developer
**I want** the README to call out the supported local JDK for Gradle
**So that** builds do not fail under unsupported newer JDKs such as Java 25

### Notes
- Java 25 reproduces `Type T not present` while creating `:application:test`.
- JDK 21 is known-good for test, qaTest, and assembleDebug.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 README setup names JDK 21 as the supported local build JDK
- [x] #2 README troubleshooting includes the Java 25 Type T not present failure and the JDK 21 command
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Updated README setup prerequisites to require JDK 21 for local Gradle builds.
2. Added troubleshooting guidance for the Java 25 `Type T not present` failure and the JDK 21 command.
3. Validated the documented JDK 21 test command with Gradle.
4. Closed task with notes.

Architecture impact: none; documentation-only build guidance.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Implementation notes:
- Reproduced the reported `Type T not present` failure with the shell default Java 25.
- Confirmed JDK 21 works with Gradle 8.11.1 and the current Android Gradle Plugin.
- Updated README setup and troubleshooting sections with explicit JDK 21 commands.

Validation package:
- Automated checks run: JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew test.
- Manual validation: reproduced failure with default Java 25 using ./gradlew :application:test.
- README impact: README updated.
- Diagram impact: no diagram update needed.
- ADR impact: no ADR needed.
- Approval status: no architecture approval required.
- Risks: Developers still need JDK 21 installed locally.
<!-- SECTION:NOTES:END -->

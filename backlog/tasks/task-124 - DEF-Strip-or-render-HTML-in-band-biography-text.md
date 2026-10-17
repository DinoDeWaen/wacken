---
id: task-124
title: 'DEF: Strip or render HTML in band biography text'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-19 05:57'
updated_date: '2026-06-19 06:22'
labels:
  - defect
  - ui
  - content
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Imported band biography text can still show raw HTML tags and entities such as br tags and non-breaking-space entities on the band detail screen. The app should render or sanitize this content so users see readable plain text.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given a biography contains HTML tags, when the band detail screen renders it, then raw tags are not visible.
- [x] #2 Given a biography contains HTML entities, when the band detail screen renders it, then entities are decoded or converted to normal readable spacing.
- [x] #3 A regression test or focused UI/formatting validation covers the formatter behavior.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Inspect existing CSV sanitizer and live Supabase biography path.
2. Add focused tests for HTML tag removal, line breaks, and entity decoding/spacing.
3. Implement a reusable biography text formatter in the application layer and use it from band detail presentation.
4. Run focused application tests and Android compile/unit checks.
5. Rebuild a fresh signed local release APK per repository release rule.

Design approach: keep sanitation in application presentation data so all sources benefit without changing stored master data.
Architecture impact: not architecture-significant; no persistence schema, API, dependency, or domain rules change.
Documentation impact: README/business requirement wording may be updated to clarify readable biography text.
Treatment: minimal defect fix with regression tests.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Implemented readable biography formatting in the application presentation path. Raw HTML tags are stripped, common entities and numeric entities are decoded, and paragraph/line-break spacing is preserved for display. Added a regression test covering tags, <br>, paragraph spacing, non-breaking spaces, ampersands, quotes, decimal entities, and hex entities.

Validation package:
- Tests/build: JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :application:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac
- Release validation: JAVA_HOME=$(/usr/libexec/java_home -v 21) WACKEN_RELEASE_* ./gradlew :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac assembleRelease
- APK signature: apksigner verify --verbose app/build/outputs/apk/release/app-release.apk passed with v1/v2 signatures.
- APK metadata: versionCode 21, versionName 2.18, package be.wacken.planner.
- APK SHA-256: 25ef63c75d7e009a61c31676e980d4f4bda2ceef91e6d9bf4ba47551a00d4429
- Static check: git diff --check passed.

README impact: Updated to state that imported biography text is shown as readable text without raw HTML tags and linked V2.18 release notes.
Business requirements impact: Updated BR-051 to require readable biography display without raw HTML tags/entities.
Diagram impact: No diagram impact; no architecture relationships changed.
ADR impact: No ADR needed; this is a presentation formatting defect fix and not architecture-significant.
<!-- SECTION:NOTES:END -->

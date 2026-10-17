---
id: TASK-199
title: 'DEF: Keep unknown band music links empty after Supabase import'
status: Done
assignee:
  - '@codex'
created_date: '2026-10-16 21:05'
updated_date: '2026-10-17 07:07'
labels:
  - defect
  - android
  - metadata
dependencies: []
references:
  - supabasemasterdataclient
modified_files:
  - app/src/androidTest/java/be/wacken/planner/UnknownBandLinksAndroidTest.java
  - README.md
  - backlog/docs/business-requirements.md
  - app/src/main/java/be/wacken/planner/SupabaseMasterDataClient.java
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
New Summer breeze bands such as Abbie Falls show a failing YouTube action despite having no known channel. Preserve absent remote metadata as absent through creation, sync, Room and screen rendering; never invent a channel or show a null link. Known music links must remain usable. Diagnose platform-specific JSON behavior and add regression coverage. No unrelated enrichment, database schema change or release publication.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given a newly imported band such as Abbie Falls whose Supabase YouTube value is null, when its lineup or detail is shown, no YouTube action is offered.
- [x] #2 Null, missing and blank optional band metadata remain absent after creation and normal sync/cache mapping; known YouTube and Spotify values remain unchanged.
- [x] #3 A regression reproduces the actual Android JSON behavior before the fix and validates persisted/cache and screen behavior after it.
- [x] #4 README and business requirements describe unknown-link behavior; tests, root cause, introducing change, architecture/ADR/diagram impact and validation package are recorded.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Completed standard defect repair in codex/fix-empty-band-links based on V2.33.4.
1. Read-only production queries confirmed Abbie Falls optional metadata is SQL NULL and no literal-null music URLs are stored. Traced the defect to Android org.json in mapper commit 663475f.
2. Added five Android-native cases. Three failed on unchanged code (d83607e), reproducing creation cache, sync/detail actions and reviewed-null metadata failures. Known-link and blank/missing cases preserve existing behavior.
3. Corrected JSON null handling at the existing Supabase adapter boundary and reused it for missing-field patch decisions (a2f4841). No new architecture, schema, API contract or dependency; no new approval or ADR required.
4. Clean unit/QA/coverage, all 15 Android tests, release lint-vital and a fresh signed validation APK passed. Verified signature, metadata, digest and diff formatting; updated README and BR-036, created draft PR2.
Deviation: one green attempt was interrupted by an ADB reconnect; the complete clean final run replaces that invalid result. The original phone/external YouTube app was not attached. No production records, published release or APK version changed. A new installed build plus sync is needed for the phone. Remote CI is running separately.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Fixed unknown music links at the Supabase JSON mapping boundary. SQL/JSON null, missing and blank metadata now remain absent for YouTube, Spotify, image and biography. New festival creation and normal sync share that mapper. Normal sync replaces stale cached "null" links, and the detail screen offers no unknown YouTube/Spotify action. Reviewed metadata can fill null fields without overwriting known links.

The real Abbie Falls row has SQL NULL for all four optional fields. A catalog check found zero literal "null" YouTube/Spotify values; no production data change or guessed channel was needed.

## Acceptance criteria validation

- AC1: native Android sync/cache/detail regression proves Abbie Falls has no YouTube/Spotify/image action when metadata is null. Overview rows use the same absent Band metadata and existing optional rendering.
- AC2: Android tests cover creation snapshot publication, normal sync replacing a stale bogus cache, missing and blank values, and exact preservation of known YouTube/Spotify values and their detail actions.
- AC3: the native red baseline d83607e ran five tests with three expected failures: Optional[null] after creation, visible unknown-link button after sync, and inability to fill reviewed null metadata. Final result: all 15 Android tests passed with zero failures/errors/skips in the clean final run.
- AC4: README and BR-036 document the requested empty-link behavior. The full validation package and cause history are below.

## How to test

### Automated tests

- JDK 21/SDK36: ./gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=be.wacken.planner.UnknownBandLinksAndroidTest --no-daemon — red baseline reproduced three behavior failures out of five; known/missing/blank cases passed.
- Existing Keychain signing values, JDK21/SDK36: ./gradlew clean test qaTest :app:connectedDebugAndroidTest :app:lintVitalRelease assembleRelease --no-daemon — passed; 139 tasks, 138 executed, including all 15 Android tests with zero failures/errors/skips.
- git diff --check passed.
- Signed local validation APK: /Users/dino/.codex/worktrees/fix-festival-creation/wacken/app/build/outputs/apk/release/app-release.apk. Digest: 61aa56181685ac85a4cbe5d3414feddb7b9bb4e26fe2ee879f2af4bf07d80b04. Signature/metadata: v1/v2 signatures verified with stable certificate ede2ae97735a305900e87bc0fdc021c03382db4e30435f2349a96a3fdcc7545f; be.wacken.planner, version 2.33.4/40, minSdk23, targetSdk36.
- Remote CI was still running at closure: https://github.com/DinoDeWaen/wacken/actions/runs/39012327244 . No remote success is claimed; the complete required local validation passed.

### Manual validation

After installing a build containing this fix, run Sync from Supabase and open Abbie Falls from Summer breeze: no unknown YouTube or Spotify action should be shown. Open a band with stored links and confirm they remain available. The actual BandDetailActivity was exercised on the emulator; the reported phone and external YouTube app were not attached.
No new release is published in TASK-199; the local validation APK retains 2.33.4/40 metadata and is not a replacement published release.

## TDD / BDD / approval-test evidence

Real Android org.json converts JSONObject.NULL to the nonempty string "null" in optString, unlike the desktop test JSON library's fallback behavior. Existing unit tests covered blank strings and known links, while creation instrumentation fixtures omitted optional keys instead of sending explicit nulls. The new native tests send the actual nullable JSON shape and assert Room/domain values and rendered actions. They failed before production repair; the valid-link case protects existing behavior.
One green attempt was interrupted by an ADB reconnect and returned an incomplete run; that result is not counted. The complete final run above provides validation.

## Architecture impact

- Architecture-significant change: none. This corrects DTO mapping and existing missing-field detection inside the existing adapter; no boundary, schema, dependency, auth, persistence model or API contract changes.
- Approval received: user requested the defect repair; no new architecture approval needed.
- ADR impact: none, because the adapter boundary and all existing design decisions are unchanged.

## README impact

README impact: updated the music-link capability to state that unknown links stay empty and import does not invent them.

## Business requirements impact

Business requirements impact: updated BR-036 to explicitly omit unknown overview/detail music actions, using Abbie Falls as the example.

## Diagram impact

Diagram impact: none, because the existing Supabase-to-domain-to-Room/UI flow is unchanged.

## Commits / logical change list

- 81a8aa3: TASK-199 plan.
- d83607e: Android red regression baseline.
- a2f4841: null-safe band metadata mapping and missing-field patch detection; README/BR-036.
- Closure evidence synchronized to the visible user checkout and isolated fix branch.

## Risks and follow-up

The bug existed in mapper commit 663475f (17 May 2026, Add Supabase master data sync). The festival creation work reused that mapper and exposed it for new names with absent metadata. V2.33.4's successful existing creation tests missed explicit JSON nulls because fixtures omitted keys, and desktop org.json behaved differently.
An updated installed build and successful sync are required to replace old cached bogus links. Known stored URLs are preserved; this task does not verify whether an existing remote channel is still live, discover channels, automatically enrich metadata or rewrite production records. No physical-phone file-picker/browser walkthrough was performed.

## Modified files

app/src/androidTest/java/be/wacken/planner/UnknownBandLinksAndroidTest.java, README.md, backlog/docs/business-requirements.md, app/src/main/java/be/wacken/planner/SupabaseMasterDataClient.java

Review: https://github.com/DinoDeWaen/wacken/pull/2
<!-- SECTION:NOTES:END -->

## Final Summary

<!-- SECTION:FINAL_SUMMARY:BEGIN -->
Fixed absent music metadata on Android: unknown channels stay empty after creation and sync; stale null links are replaced and known links remain. Native red baseline reproduced three defects; clean full unit/QA/coverage, 15 Android tests, release lint and signed validation build passed. PR: https://github.com/DinoDeWaen/wacken/pull/2 . No new release published; updated build and sync required.
<!-- SECTION:FINAL_SUMMARY:END -->

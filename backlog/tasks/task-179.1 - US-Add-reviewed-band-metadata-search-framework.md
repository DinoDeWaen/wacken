---
id: task-179.1
title: 'US: Add reviewed band metadata search framework'
status: Done
assignee:
  - '@codex'
created_date: '2026-08-23 14:11'
updated_date: '2026-08-23 14:20'
labels:
  - metadata
  - architecture
  - bands
  - settings
dependencies: []
parent_task_id: task-179
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Business value: admins need a safe framework for finding missing band metadata without overwriting trusted catalog data or coupling provider details into the UI.

User story: As an admin, I want a reviewed metadata search framework that checks the own band catalog first and then gathers provider proposals, so that missing band pictures, biographies, Spotify links, and YouTube links can be filled only after approval.

Scope: define the metadata proposal workflow, provider boundary, Settings/Admin entry point, proposal review behavior, and no-overwrite save behavior. The framework may include a fake or empty provider for tests but does not need real MusicBrainz, Wikidata, Wikipedia, Spotify, or YouTube API calls; those are separate stories.

Out of scope: provider-specific HTTP clients, credential provisioning, automatic unreviewed metadata saves, overwriting non-empty metadata, Supabase schema changes, and alias storage unless separately approved.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given a band is missing one or more metadata fields, when metadata search runs, then proposals are produced only for missing fields and existing non-empty metadata is not overwritten.
- [x] #2 Given the own band catalog contains trusted metadata for a likely confirmed band match, when metadata search runs, then own-catalog values are preferred before any external provider proposal.
- [x] #3 Given provider proposals are available, when the admin reviews them, then each proposed field can be accepted or rejected before the golden-source band record is changed.
- [x] #4 Given no provider is configured or no proposal is suitable, when the admin rejects or skips results, then the band remains unchanged and the workflow can continue for other bands.
- [x] #5 Architecture impact is assessed before implementation; because this introduces metadata provider boundaries, explicit approval is requested before coding and an ADR is created or updated if approved.
- [x] #6 Automated tests cover proposal generation, own-catalog priority, approval-required saving, no-match/no-proposal handling, and no-overwrite behavior.
- [x] #7 README, business requirements, diagram, and ADR impact are recorded using delivery-governance wording.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Inspected existing band metadata model, Settings actions, repository wiring, and regression-test patterns.
2. Added application-layer metadata proposal records, lookup-provider port, search use case, and approval/save use case.
3. Added TDD coverage for missing-field proposals, own-catalog priority, approval-required saving, no-proposal/no-change handling, unavailable providers, and no-overwrite behavior.
4. Added a Settings metadata review screen and source-regression coverage for selectable proposal approval.
5. Created ADR 0012 for the reviewed metadata search boundary and updated README/business requirements.
6. Ran focused application and Android unit tests.

Design outcome: standard architecture depth with a new application provider boundary and Android-side review UI. Domain and persistence schemas stayed unchanged.
Architecture approval: user approved starting the framework and first integration after the architecture approval gate.
Deviation: the previous one-click Settings metadata action now opens reviewed proposals so metadata is not silently written.
Follow-up: task-179.2 plugs MusicBrainz into the provider boundary.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Added a reviewed band metadata search framework. The application layer now produces missing-field metadata proposals, prefers own-catalog values first, exposes an external provider boundary, and saves only user-accepted proposals without overwriting existing metadata. Settings now opens a metadata review screen instead of applying metadata silently.

## Acceptance criteria validation

- AC1: Search proposals are generated only for missing fields. Existing non-empty metadata is filtered out before proposals are shown.
- AC2: Own-catalog matches are proposed first and suppress external proposals for the same missing field.
- AC3: The Android review screen uses checkboxes and saves only accepted proposals.
- AC4: Empty selections, unconfigured providers, and no proposals leave bands unchanged and allow the workflow to continue.
- AC5: Architecture impact was assessed; user approval was received by the request to start the framework and first integration; ADR 0012 was created.
- AC6: Automated tests cover proposal generation, own-catalog priority, approval-required saving, no-proposal handling, unavailable provider handling, and no-overwrite behavior.
- AC7: README, business requirements, diagram, and ADR impact are recorded below.

## How to test

### Automated tests

- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :application:test :app:testDebugUnitTest --tests be.wacken.planner.application.BandMetadataSearchFrameworkUseCaseTest --tests be.wacken.planner.SettingsActivityRegressionTest --tests be.wacken.planner.BandMetadataReviewActivityRegressionTest

### Manual validation

- Open Settings, tap Fetch band metadata, review proposed missing metadata fields, select desired proposals, and tap Save accepted metadata.

## TDD / BDD / approval-test evidence

- TDD: added focused application tests for the framework rules before validating the implementation.
- BDD: acceptance criteria are expressed as Given/When/Then task criteria and mapped to tests.
- Approval tests: not used; no legacy refactoring required.

## Architecture impact

- Architecture-significant change: yes, a provider boundary for external metadata lookup was added.
- Approval received: yes, user asked to start the framework and first integration after the approval gate was presented.
- ADR: created backlog/decisions/0012-reviewed-band-metadata-search-framework.md.

## README impact

README impact: updated Settings behavior, reviewed metadata search behavior, and ADR links.

## Business requirements impact

Business requirements impact: updated implemented post-MVP3 capabilities and external metadata source notes to mention the reviewed framework.

## Diagram impact

Diagram impact: none, because the existing C4 module boundaries remain accurate and no new runtime container was added.

## Commits / logical change list

- Added metadata proposal/search/apply use cases and provider port.
- Added Android metadata review screen and regression tests.
- Added ADR 0012 and documentation updates.

## Risks and follow-up

- Real provider HTTP behavior is intentionally deferred to provider stories.
- Android review UI is source-regression tested; full instrumentation remains a separate future testing concern.
<!-- SECTION:NOTES:END -->

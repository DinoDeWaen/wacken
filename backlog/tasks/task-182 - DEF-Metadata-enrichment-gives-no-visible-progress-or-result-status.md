---
id: task-182
title: 'DEF: Metadata enrichment gives no visible progress or result status'
status: Done
assignee:
  - '@codex'
created_date: '2026-08-26 20:03'
updated_date: '2026-08-27 14:21'
labels:
  - defect
  - metadata
  - settings
  - android
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Business value: admins need to understand whether metadata enrichment is still running, found no work, found proposals, skipped completed bands, or hit provider/configuration problems.

Observed defect: when trying metadata sync/enrichment, the Settings metadata review screen can show no useful results and no detailed status or log, making it impossible to tell which bands were checked, which providers ran, what is still needed, and what is already done.

Scope: add a visible task/status area to the metadata review workflow that summarizes pending metadata work, completed/no-work bands, proposal counts, unavailable providers, and lookup failures. The workflow must continue to require explicit user approval before saving metadata and must not overwrite existing non-empty metadata.

Out of scope: changing provider matching quality, automatically accepting proposals, background system notifications, persistent audit history, or adding new external providers.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given the metadata review screen starts a search, then the user sees a visible in-progress status instead of an empty or silent screen.
- [x] #2 Given metadata enrichment finishes with proposals, then the user sees how many bands need review and how many proposals were found.
- [x] #3 Given metadata enrichment finishes with no proposals, then the user sees a clear no-proposals status and a summary of bands that were already complete or still need missing metadata.
- [x] #4 Given providers are unavailable, unconfigured, or fail, then the user sees provider status messages in the task/status area while the workflow continues.
- [x] #5 Given a proposal is saved, then the user sees saved/skipped outcome status and the refreshed task/status area remains visible.
- [x] #6 Automated regression tests cover the metadata review source controls/status area and application-level metadata search summary behavior where appropriate.
- [x] #7 README, business requirements, diagram, and ADR impact are recorded using delivery-governance wording.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Inspected the metadata review UI and application search use case; confirmed the UI only had a generic status line and the use case dropped complete/no-proposal context.
2. Added application-level search run summary behavior with counts for checked bands, completed bands, missing metadata, bands needing review, proposal count, bands without proposals, and provider messages.
3. Added Android review-screen task/status panel showing in-progress work, done/needed/review counts, provider status, no-proposal messaging, and last save outcome.
4. Preserved explicit approval and no-overwrite metadata behavior.
5. Updated README and business requirements to document visible metadata enrichment task/status feedback.
6. Ran focused application and Android unit tests plus diff hygiene.
Architecture impact: not architecture-significant; UI/status reporting and application summary data over existing metadata search behavior, with no provider boundary, persistence, schema, or domain model change.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Added visible metadata enrichment task/status feedback so the Settings review workflow no longer appears silent when it is searching, finds no proposals, hits unconfigured providers, or saves selected proposals. The screen now shows checked bands, completed bands, bands missing metadata, proposal counts, still-missing bands with no proposals, provider status messages, and the latest save outcome.

Added `BandMetadataSearchRun` in the application layer so the UI can render a run summary without changing provider behavior or metadata approval rules. Existing non-empty metadata remains protected from automatic overwrite.

## Acceptance criteria validation

- AC1: The screen shows `Metadata enrichment tasks` and an in-progress line while searching.
- AC2: Completed runs show bands needing review and proposal counts.
- AC3: Runs with zero proposals show a clear no-proposals status plus completed/missing/still-needed summary.
- AC4: Provider messages such as unconfigured or failed providers are surfaced in the task/status area while the workflow continues.
- AC5: Save results are retained as `Last save` after the refresh.
- AC6: Added application summary coverage and Android source regression coverage for the visible status area.
- AC7: README and business requirements were updated; diagram and ADR impact are recorded below.

## How to test

### Automated tests

- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :application:test :app:testDebugUnitTest --tests be.wacken.planner.application.BandMetadataSearchFrameworkUseCaseTest --tests be.wacken.planner.BandMetadataReviewActivityRegressionTest --tests be.wacken.planner.SettingsActivityRegressionTest
- git diff --check

### Manual validation

- Open Settings -> Fetch band metadata.
- Confirm the task/status area shows in-progress state first.
- Confirm the finished view shows checked/done/needed/review counts, provider status, no-proposal state when applicable, and last save result after saving selected proposals.

## TDD / BDD / approval-test evidence

- Added a focused application test for metadata search run summary behavior.
- Updated Android source regression coverage for the visible metadata task/status panel.
- BDD/TDD style was used at a minimal defect-fix level.
- Approval tests were not needed; this is additive status reporting around existing behavior.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR: none; this follows the existing reviewed metadata framework boundary from ADR 0012.

## README impact

README impact: updated the metadata enrichment section to document the visible task/status summary.

## Business requirements impact

Business requirements impact: updated Workflow 11c to require visibility into done, still-needed, and provider-blocked metadata enrichment tasks.

## Diagram impact

Diagram impact: none, because this task does not change architecture or system relationships.

## ADR impact

ADR impact: none, because no architecture decision changed.

## Commits / logical change list

- Added metadata search run summary model and use-case aggregation.
- Added metadata review task/status panel and save outcome visibility.
- Added/updated focused tests and documentation.

## Risks and follow-up

- This does not improve provider matching quality; it makes missing/no-result/provider-status causes visible.
- The unrelated .idea/workspace.xml change remains unstaged and was not included.
<!-- SECTION:NOTES:END -->

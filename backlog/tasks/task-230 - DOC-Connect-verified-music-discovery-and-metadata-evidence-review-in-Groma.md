---
id: TASK-230
title: 'DOC: Connect verified music discovery and metadata evidence review in Groma'
status: Done
assignee:
  - '@codex'
created_date: '2026-10-20 06:25'
updated_date: '2026-10-21 12:12'
labels:
  - groma-audit
  - release-2.35
milestone: m-3
dependencies:
  - TASK-227
references:
  - wacken
  - musiclinklookupprovider
  - musiclinksreviewactivity
  - findbandmusiclinksusecase
  - verifiedmusiclinksprovider
  - officialmusicpageclient
  - official-artist-websites
documentation:
  - backlog/docs/groma-audit-2026-10-20.md
modified_files:
  - >-
    groma/systems/wacken/containers/android-application/components/musiclinklookupprovider.md
  - >-
    groma/systems/wacken/containers/android-application/components/musiclinkevidence.md
  - >-
    groma/systems/wacken/containers/android-application/components/musiclinksreviewactivity.md
  - >-
    groma/systems/wacken/containers/android-application/components/findbandmusiclinksusecase.md
  - >-
    groma/systems/wacken/containers/android-application/components/verifiedmusiclinksprovider.md
  - >-
    groma/systems/wacken/containers/android-application/components/officialmusicpageclient.md
  - groma/externals/official-artist-websites.md
  - >-
    groma/systems/wacken/containers/android-application/components/settingsactivity.md
  - >-
    groma/systems/wacken/containers/android-application/components/banddetailactivity.md
  - >-
    groma/systems/wacken/containers/android-application/components/searchbandmetadatausecase.md
  - >-
    groma/systems/wacken/containers/android-application/components/bandrepository.md
  - >-
    groma/systems/wacken/containers/android-application/components/syncedbandrepository.md
  - groma/flows/review-festival-missing-music-links.md
  - groma/flows/review-one-band-music-evidence.md
  - backlog/docs/groma-music-islands-evidence-2026-10-21.md
  - README.md
priority: high
type: docs
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## User story
As a maintainer, I want verified music discovery connected so I can distinguish trusted evidence from ranked metadata suggestions.

## Scope
Curate MusicLinksReviewActivity, lookup use case/port, evidence values, verified provider and official-page client with existing providers and external services. Show Settings festival and band-detail entry points. Keep optional credentials and verification trust boundaries explicit; do not implement a new provider.

## Audit reference
[Groma architecture audit — 20 October 2026](../docs/groma-audit-2026-10-20.md)
Repository path: backlog/docs/groma-audit-2026-10-20.md
Findings: F3 and F10; Appendix A music-link/evidence/provider records. Investigation: TASK-225.

## Boundaries and impacts
Preserve ADR-0016: shared canonical metadata, member-specific established band rating, independent festival planning/after-ratings, one-time creation snapshots and source-aware archive promotion/correction. Follow Groma CLI-only curation and Backlog CLI-only task maintenance.
Business requirements impact: none planned unless an approved behaviour change is explicitly required; record the actual impact on completion.
README and diagram impacts are assessed for the affected workflow. ADR impact: none assumed; approval is required for significant changes.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Every Appendix A music-link island is described and assigned to a justified owner, with source-supported incoming/outgoing interactions where it has an independent responsibility.
- [x] #2 Given festival Settings or one band, a flow traces lookup, identity/source evidence review, explicit confirmation and canonical link save; unknown or unverified links stay empty.
- [x] #3 General metadata ranking and verified link evidence remain distinct; optional provider unavailability, cancellation and account changes are documented without claiming ranking alone establishes identity.
- [x] #4 Existing provider/external relationships remain truthful and no secret or credential value appears in map documentation.
- [x] #5 Two scans and rendered source/flow inspection preserve curated knowledge; README architecture and diagram impacts are recorded.
- [x] #6 Architecture impact is assessed before implementation; significant boundary, dependency, persistence or test-strategy changes receive explicit human approval before coding and an ADR is recorded when required.
- [x] #7 README, business requirements, diagram and ADR impacts use canonical delivery-governance wording; test evidence, manual verification, finalized plan and remaining risks are recorded before merge.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Traced all six remaining music islands from Settings/BandDetail entry points through retained review worker, application verification/approval, provider HTTP clients, official pages and account-bound source-first metadata save. All are used; no production source removal justified. 2. Combined MusicLinkEvidence into its lookup protocol and described five independent roles in Verified music discovery group through CLI;294 source files remain. 3. Added exact source/composition collaborations and declared public Official artist websites external interaction, preserving existing providers. Added festival and one-band evidence-review scenarios with ambiguity, cancellation/retry, optional fallback and account/save limitations. 4. Verified zero disconnected components,95 components,294 unique existing sources and13 resolvable flows. Two scans create0/refreshed95/findings88; normalized annotated model is unchanged after rescan. Chromium export renders festival scenario and exact MusicLinksReviewActivity source without page errors. Regression/coverage/lint pass; prior APK removed and fresh stable-key release rebuild/signature verification pass. README/evidence updated. Minimal documentation-only curation changes no architecture boundary, provider, policy, contract, dependency or test strategy; no additional approval/ADR required. Existing regression/BDD suites characterize unchanged behavior; no live provider or production database validation claimed.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Resolved all six music-discovery islands by tracing actual runtime use. Combined evidence values with their lookup protocol, retained five independent responsibilities and connected Settings/one-band entry points through source evidence, application verification, explicit approval and canonical save. Declared bounded public official-site interaction and two reviewed scenarios. Evidence: backlog/docs/groma-music-islands-evidence-2026-10-21.md.

## Acceptance criteria validation

All seven criteria met. Every music record is used and connected or consolidated with rationale. General ranking and optional unverified fallback remain distinct from corroborated identity and unique official cross-links. Missing/unverified links stay empty; existing fields and rating/history/lineup state are preserved. Cancellation/retry, rotation, optional providers and expected-account save behavior are explicit. No credential values included.

## How to test

### Automated tests

Application163, domain63, infrastructure24 and BDD3 pass; Android264 tests with248 passed and16 existing disposable-API skips. Application/domain coverage gates and Android lint pass. Fresh assembleRelease --rerun-tasks rebuilds53 tasks; apksigner verifies v1/v2 with stable certificate SHA256 ede2ae97735a305900e87bc0fdc021c03382db4e30435f2349a96a3fdcc7545f. APK SHA256 a6d781c5d1af5a2d7a4e11d094005f3f3af9cb08dbd6a4094888c5da99b8b53f. Existing version retained; no publication.

### Manual validation

Checked entry Intents, use-case verification/approval, provider nested HTTP clients and PageLoader callback, source-first shared/cache save and expected-user binding. Current graph has zero disconnected components (47 before TASK229/230),95 components,294 existing unique owned sources and13 resolvable flows. Two scans create0/refreshed95/findings88; normalized annotated model remains identical after rescan. Static export renders festival review steps and exact MusicLinksReviewActivity source in Chromium without page errors; screenshots inspected. Diff check passes. No live source-provider or production authorization claim.

## TDD / BDD / approval-test evidence

Documentation-only curation changes no runtime behavior; existing BDD and regression suites preserve established contracts. No new test strategy or behavioral implementation.

## Architecture impact

Architecture-significant change: none; map records existing interactions and consolidates protocol values only. Approval received: user requested investigation and correction of isolated components. ADR impact: none, because existing boundaries, trust policy and source/save contracts are preserved.

## README impact

README impact: updated music evidence link and current ownership/island disposition.

## Business requirements impact

Business requirements impact: none, because existing evidence-review and missing-field rules are documented without behavior changes.

## Diagram impact

Diagram impact: updated Groma responsibility ownership, group, source-backed relationships, public website external and two scenarios through CLI; existing Mermaid boundary diagrams remain accurate.

## Commits / logical change list

One task-sized documentation commit covers evidence protocol consolidation, reviewed collaborations/scenarios and reports.

## Risks and follow-up

Degree checks complement source review and do not prove runtime correctness. Java scanner still has88 lint finding groups and unresolved framework/generated references. TASK241 retains runtime/upgrade verification. TASK228 still needs safe CLI removal of stale authored arrows to the deleted importer. Public lookup is not claimed to automatically cancel on account change; authenticated writes use expected-user checks. No merge or publication.
<!-- SECTION:NOTES:END -->

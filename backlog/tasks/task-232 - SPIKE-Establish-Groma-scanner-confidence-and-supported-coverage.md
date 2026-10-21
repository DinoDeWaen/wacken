---
id: TASK-232
title: 'SPIKE: Establish Groma scanner confidence and supported coverage'
status: Done
assignee:
  - '@codex'
created_date: '2026-10-20 06:25'
updated_date: '2026-10-21 08:15'
labels:
  - groma-audit
  - release-2.35
milestone: m-3
dependencies:
  - TASK-226
references:
  - wacken
documentation:
  - backlog/docs/groma-audit-2026-10-20.md
modified_files:
  - backlog/docs/groma-scanner-confidence-2026-10-21.md
  - README.md
priority: medium
type: spike
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## User story
As a maintainer, I want analysis limitations visible so an incomplete call graph is not mistaken for proof that code is disconnected or unused.

## Scope
Investigate unresolved cross-module/generated/Android symbols, deferred callbacks and Java-only coverage using the installed scanner and representative fixtures. Evaluate supported configuration/tool options, including whether classpath-aware analysis is available. Recommend a minimal approach; no dependency upgrade, new scanner or runtime architecture change without approval.

## Audit reference
[Groma architecture audit — 20 October 2026](../docs/groma-audit-2026-10-20.md)
Repository path: backlog/docs/groma-audit-2026-10-20.md
Findings: Scanner limitations; F2/F3/F5. Investigation: TASK-225.

## Boundaries and impacts
Preserve ADR-0016: shared canonical metadata, member-specific established band rating, independent festival planning/after-ratings, one-time creation snapshots and source-aware archive promotion/correction. Follow Groma CLI-only curation and Backlog CLI-only task maintenance.
Business requirements impact: none planned unless an approved behaviour change is explicitly required; record the actual impact on completion.
README and diagram impacts are assessed for the affected workflow. ADR impact: none assumed; approval is required for significant changes.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Reproduce representative ordinary-call, callback, Room/generated and cross-module cases; distinguish scanner diagnostics from actual Gradle compiler results.
- [x] #2 Document which supported settings or tools improve analysis and which interactions still require authored evidence; do not promise a complete graph or zero unresolved references without proof.
- [x] #3 Document SQL/RPC/RLS, manifest/resource and verification-script coverage gaps and the evidence required for curated relationships across them.
- [x] #4 Compare minimal, standard and maximum options with repeatable commands, compatibility and maintenance costs; obtain explicit approval before any significant implementation.
- [x] #5 README records the supported scan interpretation/troubleshooting, or uses canonical no-impact wording if this remains a decision report.
- [x] #6 Architecture impact is assessed before implementation; significant boundary, dependency, persistence or test-strategy changes receive explicit human approval before coding and an ADR is recorded when required.
- [x] #7 README, business requirements, diagram and ADR impacts use canonical delivery-governance wording; test evidence, manual verification, finalized plan and remaining risks are recorded before merge.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Inspected pinned public CLI and installed primary scanner adapter/manifest; no supported classpath/annotation-processing setting found.
2. Reproduced static calls, method references, isolated module, actual Android/executor/Room gaps, with real javac Java17 and passing Gradle/native controls.
3. Documented non-Java gaps, per-input diagnostic interpretation, repeatable disposable probes and minimal/standard/maximum costs; recommended minimal without significant implementation.
4. Updated README; executed exact report probe and assertions; scan preserved map, diff check passed; fresh established-key signed build verified.
Documentation-only minimal treatment; no ADR/approval needed. Exploratory Java21 probe repeated at declared Java17; internal worker commands are investigative rather than a supported CI interface.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary
Established reproducible scanner confidence limits for ordinary calls, deferred callbacks, isolated modules and actual Room/generated Android code. Report compares minimal/standard/maximum options and recommends pinned tooling plus authored evidence. README links the troubleshooting report. No tooling/configuration/CI/runtime changes.

## Acceptance criteria validation
1. Combined static-call fixture resolves; isolated module fails symbol resolution; deferred method reference is not an invocation. Real javac --release 17 control passes. Actual Band.name, executor and Room calls reproduced unresolved while Gradle/native controls pass.
2. Inspected installed scanner manifest/adapter: source selection/release controls exist; no supported dependency classpath or annotation-processing setting. Readiness does not establish Android compilation.
3. SQL/RPC/RLS, generated Room, manifest/resources and script/deployment gaps recorded with exact evidence and independent test requirements.
4. Minimal recommended; standard TASK-233 and maximum tooling changes remain proposals requiring explicit approval. Nothing significant implemented.
5–7. README troubleshooting, finalized plan, impacts and evidence included.

## How to test
### Automated tests
Public CLI: groma --version (0.6.6); scanner list/check (@groma/scanner-java@0.2.2 ready); groma scan PASS, created 0/refreshed 111/findings 88. git diff --exit-code -- groma PASS. Lint count 88 exits 1 as documented.
Executed the exact report Python probe from its Markdown fence, including worker observations and real JDK21 javac --release17 control. Assertions verified resolved static call, deferred callback count, isolated missing type/unresolved target, absent generated implementations and representative unresolved project calls. Probe output is temporary; reproducible source is in the report.
Non-Java groma view checks return expected no-scanner errors; generated ignored source returns not-a-repository-file.
Unchanged production-source baseline from TASK-240: application170/domain63/BDD3, coverage/lint pass; Android unit264 (248pass,16 existing disposable-API skips); native71/71 API36 zero skips. No redundant behavior tests added for documentation.
Deleted prior local APK and rebuilt ./gradlew assembleRelease --rerun-tasks PASS. apksigner verifies established-key artifact. APK SHA256 51cca1417ed946067c7ba83d6384056cd5c8739380603adbdaa4ca809e6abe72. Intermediate version remains2.34/code42, not published.
git diff --check PASS.

### Manual validation
Read installed primary scanner source and supported CLI help. Cross-checked source and generated Room implementations, actual RPC SQL/Java endpoint and coverage errors. Reviewed options and report commands. No new manual device testing or production security review claimed.

## TDD / BDD / approval-test evidence
Research spike uses positive and negative fixture controls rather than changing application behavior. Existing full behavior baseline passes. Internal worker protocol is documented as investigative and version-sensitive, not a supported CI interface.

## Architecture impact
Architecture-significant change: no; documentation and disposable analysis only.
Approval received: not required for report; no standard/maximum implementation authorized.
ADR impact: none, because no boundary, dependency, persistence or test-strategy change is implemented.

## README impact
README impact: updated scanner confidence/troubleshooting and grouped diagnostic interpretation.

## Business requirements impact
Business requirements impact: none, because product behavior and scope are unchanged.

## Diagram impact
Diagram impact: none, because no architecture records or relationships change and scan preserves map bytes.

## Commits / logical change list
One TASK-232 documentation commit: reproducible confidence report and README interpretation.

## Risks and follow-up
Scanner warning text can show representative counts for grouped inputs; counts are not completeness guarantees. Direct worker protocol is internal. TASK-227 still blocks container curation; TASK-233 needs approved test-strategy design after map curation. API23/backend/signed-upgrade milestone validation remains TASK-241. No upgrade, merge or release publication.
<!-- SECTION:NOTES:END -->

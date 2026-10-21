---
id: TASK-233
title: 'TEST: Prevent architecture islands and unreviewed Groma drift in CI'
status: Done
assignee:
  - '@codex'
created_date: '2026-10-20 06:25'
updated_date: '2026-10-21 13:07'
labels:
  - groma-audit
  - release-2.35
milestone: m-3
dependencies:
  - TASK-228
  - TASK-229
  - TASK-230
  - TASK-231
references:
  - wacken
  - android-application
documentation:
  - backlog/docs/groma-audit-2026-10-20.md
  - backlog/docs/task-233-architecture-gate-proposal.md
modified_files:
  - backlog/docs/task-233-architecture-gate-proposal.md
  - scripts/tests/groma-gate.test.ts
  - scripts/architecture/groma-gate.mjs
  - scripts/architecture/verify-groma.mjs
  - scripts/setup-groma.sh
  - >-
    backlog/tasks/task-227 -
    DOC-Restore-truthful-runtime-and-data-boundaries-in-the-architecture-map.md
  - tools/groma/architecture-baseline.json
  - backlog/docs/architecture-tool-assessment-2026-10-21.md
  - backlog/decisions/0022-reviewed-groma-architecture-validation-gate.md
  - backlog/docs/groma-gate-review-2026-10-21.md
  - .github/workflows/ci.yml
  - README.md
  - backlog/docs/delivery-governance.md
priority: high
type: task
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## User story
As a maintainer, I want CI to reject unexplained architecture drift so incomplete documentation cannot repeatedly pass unnoticed.

## Scope
Add a reviewed, pinned architecture validation gate after map curation. Verify ownership, containment, meaningful documentation, referenced IDs, authored relationships, flows and repeatability. Preserve explicit justified exceptions for implementation values/ports and scanner limitations. Baseline the audit-reviewed duplicate groups by stable identities; do not demand zero duplication or blanket-ignore lint exit 1.

## Audit reference
[Groma architecture audit — 20 October 2026](../docs/groma-audit-2026-10-20.md)
Repository path: backlog/docs/groma-audit-2026-10-20.md
Findings: F5; missing map regression checks. Investigation: TASK-225.

## Boundaries and impacts
Preserve ADR-0016: shared canonical metadata, member-specific established band rating, independent festival planning/after-ratings, one-time creation snapshots and source-aware archive promotion/correction. Follow Groma CLI-only curation and Backlog CLI-only task maintenance.
Business requirements impact: none planned unless an approved behaviour change is explicitly required; record the actual impact on completion.
README and diagram impacts are assessed for the affected workflow. ADR impact: none assumed; approval is required for significant changes.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given the curated baseline, two scans and clean-checkout validation pass without duplicate ownership, missing sources, dangling IDs/endpoints/flows or unexplained containment changes.
- [x] #2 Given deliberately disconnected responsibility, removed owned source, lost authored relationship, broken task reference or recreated component fixtures, the gate fails with actionable diagnostics.
- [x] #3 Known intentional duplicates and explicitly justified isolated implementation details are reviewable exceptions; new findings or unexplained islands fail without requiring fake arrows or merged domain responsibilities.
- [x] #4 Scanner/tool versions are reproducible and diagnostic degradation is surfaced; scanner output warnings are not represented as application compiler errors.
- [x] #5 Positive and negative tests cover semantic identity, legitimate file moves/combines and fixture restoration; manual/browser review remains required for meaning.
- [x] #6 README and delivery guidance document local/CI commands, exception approval and PR evidence. Architecture/test-strategy approval is obtained before implementing this gate.
- [x] #7 Architecture impact is assessed before implementation; significant boundary, dependency, persistence or test-strategy changes receive explicit human approval before coding and an ADR is recorded when required.
- [x] #8 README, business requirements, diagram and ADR impacts use canonical delivery-governance wording; test evidence, manual verification, finalized plan and remaining risks are recorded before merge.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Inspected CI, exact tool/scanner/work-source pins, curated raw/resolved map, duplicate audit and accepted TASK228 limitation; user explicitly approved standard proposal before code. Created ADR0022 and assessed ArchUnit/jdeps/jQAssistant using primary documentation and current module boundaries. 2. Implemented pure semantic validation plus pinned-reader/fresh-scanner adapter, disposable clean checkout and two-scan comparison including inferred relationships; retained exact two stale-row exceptions and required all294 owned sources to appear in scanner evidence. 3. Added27 positive/negative TDD fixtures covering identity, missing/duplicate ownership, islands, lost collaborations, invalid task references/flows, changed duplicates/pins, diagnostic/evidence degradation, legitimate reviewed moves/combines and temporary-checkout cleanup. Real gate found11 stale TASK227 references; corrected them through CLI. 4. Individually reviewed all88 current duplicate groups against the99-group audit/TASK240, recorded stable full source/member/token identities and rationales; no count/exit-code blanket suppression. 5. Added blocking CI job, external-only baseline proposals, machine diagnostics, README/local usage and delivery review policy. Real clean-checkout gate passes with95 components/294 sources/13 flows and two scans creating0/refreshed95. Existing regression/coverage/lint, JS/shell syntax, CI YAML parsing and fresh53-task stable-key APK build pass. No production code/dependency or Groma-owned map change. No ArchUnit dependency added: assessment recommends it as complementary Java-bytecode boundary enforcement, not replacement for map validation. Final source and human-reviewed map semantics remain unchanged; remote GitHub CI execution is part of integrated TASK241 validation.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Implemented the user-approved standard development-only Groma validation gate and blocking CI job. ADR0022 records approval and scope. Gate uses exact pinned Groma readers/raw rows and fresh Java scanner evidence, validates curated structure/meaning identities and current task links, scans only a disposable clean Git checkout twice and emits build/groma-validation.json. All88 current duplicate groups have individual member/token-based identities and rationale; exactly two user-accepted TASK228 stale rows are explicit exceptions. Corrected11 stale TASK227 references discovered by the real gate.

ArchUnit assessment: recommended for complementary compiled Java dependency/layer/cycle rules. JDK jdeps is useful reporting; jQAssistant adds unnecessary graph machinery here. None replaces Groma ownership/prose/task/flow checks. Assessment: backlog/docs/architecture-tool-assessment-2026-10-21.md. No Java dependency added.

## Acceptance criteria validation

All eight criteria met. Missing sources/coverage, duplicate ownership/IDs, undocumented roles, containment drift, lost authored rows, new islands, invalid references/flows, recreated components and unreviewed duplicate identities fail with actionable messages. Exact exceptions cannot legitimize derived rows, regained deleted ownership or other endpoints. Tool/scanner/work-source pins, operation/token/invocation evidence and diagnostic categories/fact counts expose degradation. Before/after and repeated scans also compare inferred rows. Legitimate moves/combines require explicit reviewed baseline updates; ordering/line shifts preserve identity. Source/browser review remains required for meaning.

## How to test

### Automated tests

bun test scripts/tests/groma-gate.test.ts:27 passed,0 failed,40 assertions. Real bun scripts/architecture/verify-groma.mjs passes in disposable clean checkout:95 components,294 unique owned existing sources,13 resolvable flows,88 reviewed duplicate identities; first and second scans create0/refreshed95 with no conflicts. Exactly two stale rows and eight known diagnostic categories are reported, not hidden. Reader source rebuilt from approved exact commit/patch; source receipt/pin compatibility verified.

Existing Gradle domain63/application163/infrastructure24/BDD3 pass; Android264 tests have248 passed and16 pre-existing disposable-API skips. Application/domain coverage gates and Android lint pass. JS syntax, shell syntax, CI YAML parsing and git diff --check pass. Fresh assembleRelease --rerun-tasks rebuilt53 tasks after deleting previous APK; apksigner verifies v1/v2 with stable certificate ede2ae97735a305900e87bc0fdc021c03382db4e30435f2349a96a3fdcc7545f. APK SHA256 24fef4103c6d64e2eaff5a859c78277b540684520abacd0777e032525c691875. Existing version retained; no release publication.

### Manual validation

Inspected raw and resolved endpoints, model/flow preservation, per-group duplicate review table and JSON diagnostics. The actual negative probe found11 broken task references, then the corrected committed state passed. Fixture cleanup test injects failure in a cloned temporary Git repo, checks original file remains unchanged and temporary checkout is removed. No Groma architecture meaning changed; the previously rendered/user-accepted95-component map and13 scenarios are semantically identical. ArchUnit/tool comparison checked primary documentation and actual module/test configuration. Remote GitHub workflow is configured; no remote Linux/native success is claimed here.

## TDD / BDD / approval-test evidence

Started with failing fixture import; added red/green cases for silently lost scanner coverage, dropped operation evidence/increased unresolved facts and disappearing inferred rows. Final27 tests pass. Existing BDD/regression contracts remain unchanged. No app behavior or legacy production structure changed.

## Architecture impact

Architecture-significant change: development validation/test strategy only. Approval received: user selected standard before implementation. ADR impact: created ADR0022 for the reviewed semantic gate, explicit exceptions and human review boundary. Existing ADR0016 application/domain/persistence contracts remain unchanged.

## README impact

README impact: updated pinned local/CI commands, source receipt, diagnostic meaning, reviewed baseline/exception process and ArchUnit assessment link.

## Business requirements impact

Business requirements impact: none, because this development gate changes no product scope, rating lifecycle, public behavior or data contract.

## Diagram impact

Diagram impact: none, because the gate validates the unchanged CLI-authored architecture and adds no application boundary or workflow; existing rendered map review remains valid.

## Commits / logical change list

4580a7b adds the semantic gate, fixtures, reviewed baseline, task-reference repairs, CI and documentation. d7215cb covers inferred-row repeatability with red/green regression evidence. Closure notes finalize validation and approval evidence.

## Risks and follow-up

Human source/composition/browser review remains essential; prose truth and runtime reachability are not proven by structural checks. Known Java diagnostic categories and two TASK228 stale rows remain explicit limits. Intentional source moves, duplicate-body changes or scanner-evidence decreases require reviewed baseline updates. GitHub/Linux/native/database/upgrade integration is TASK241, not an inferred local success. No merge or publication. ArchUnit remains an assessed complementary option rather than an unrequested added dependency.
<!-- SECTION:NOTES:END -->

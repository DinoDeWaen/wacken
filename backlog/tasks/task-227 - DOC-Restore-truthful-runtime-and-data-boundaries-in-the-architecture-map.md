---
id: TASK-227
title: 'DOC: Restore truthful runtime and data boundaries in the architecture map'
status: In Progress
assignee:
  - '@codex'
created_date: '2026-10-20 06:25'
updated_date: '2026-10-21 05:56'
labels:
  - groma-audit
  - release-2.35
milestone: m-3
dependencies:
  - TASK-226
references:
  - wacken
  - apprepositories
  - wackendatabase
documentation:
  - backlog/docs/groma-audit-2026-10-20.md
modified_files:
  - backlog/docs/groma-container-limitations-2026-10-20.md
  - README.md
priority: high
type: docs
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## User story
As a developer, I want runtime and storage boundaries visible so I can understand how the installed app and shared backend fit together.

## Scope
After the baseline is tracked, inspect actual runtime/storage ownership and curate the missing C4 container level using Groma. Explain Android process, local Room storage and owned data versus the external Supabase platform as supported by evidence and tool capabilities. Gradle modules and visual groups must not be treated as independent runtimes. No deployed architecture change.

## Audit reference
[Groma architecture audit — 20 October 2026](../docs/groma-audit-2026-10-20.md)
Repository path: backlog/docs/groma-audit-2026-10-20.md
Findings: F2. Investigation: TASK-225.

## Boundaries and impacts
Preserve ADR-0016: shared canonical metadata, member-specific established band rating, independent festival planning/after-ratings, one-time creation snapshots and source-aware archive promotion/correction. Follow Groma CLI-only curation and Backlog CLI-only task maintenance.
Business requirements impact: none planned unless an approved behaviour change is explicitly required; record the actual impact on completion.
README and diagram impacts are assessed for the affected workflow. ADR impact: none assumed; approval is required for significant changes.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given the rendered map, a reader can identify the internal software system, Android runtime boundary and local/shared storage ownership without interpreting groups as containers.
- [x] #2 Every existing component has truthful containment or an explicitly documented tool limitation; IDs, file ownership and useful authored meaning are preserved and no fake source file or boundary is invented.
- [x] #3 Groma CLI constraints on described-component moves are investigated safely; blocked operations are documented with a supported remedy or explicit unresolved decision, without stripping meaning or directly editing Groma Markdown.
- [ ] #4 Two rescans and a fresh-checkout comparison preserve the approved structure, source links and existing flows; README and the affected architecture diagrams explain the boundaries.
- [x] #5 Architecture impact is assessed before implementation; significant boundary, dependency, persistence or test-strategy changes receive explicit human approval before coding and an ADR is recorded when required.
- [ ] #6 README, business requirements, diagram and ADR impacts use canonical delivery-governance wording; test evidence, manual verification, finalized plan and remaining risks are recorded before merge.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Confirm actual Android runtime, Room database and shared-data ownership from manifest, Gradle, composition, persistence and existing ADR evidence.
2. Probe Groma 0.6.6 supported container creation/move constraints in a disposable checkout; preserve descriptions, exact source ownership, IDs and flows.
3. Curate only source-supported boundaries through Groma CLI; if unsupported, document the exact limitation and unresolved remedy without inventing sources or clearing meaning.
4. Update README C4 diagram and evidence/impact notes, then verify two scans and a fresh checkout before closing.
Architecture assessment: documentation of existing deployed boundaries only, no runtime/module/schema/dependency/test-strategy changes planned. No architecture approval needed unless a tool/dependency change becomes necessary. Standard documentation treatment; exact-content characterization of map knowledge before and after; no application refactoring.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Confirmed in disposable baseline checkout: groma accept android-application exits 1 (no scan match); moving apprepositories exits 1 because it has authored meaning. Experiment left the release map untouched. See backlog/docs/groma-container-limitations-2026-10-20.md for source-supported runtime/storage boundaries and unresolved supported-tooling remedy. Container acceptance/relocation is not complete; do not waive dependent release criteria.

README now documents existing runtime/storage boundaries with C4 Mermaid and explicitly states Groma containment remains unresolved. README impact: updated current runtime/storage explanation and tool limits. Business requirements impact: none, because only existing architecture is described. Diagram impact: updated README C4 container view; Groma unchanged. ADR impact: none, because deployed architecture and existing decisions are unchanged. AC1/4/6 remain open pending supported Groma containment remedy and final validation.

2026-10-21 recheck: found repository-local Groma 0.7.2, while plain groma still resolves global 0.6.6. Earlier global checks missed the local installation. Explicitly invoked local binary in disposable baseline worktree: accept android-application still exits 1 (no scan match), and edit apprepositories --parent android-application still exits 1 (authored meaning). Local structure guide retains restrictions and now refers to config.json. Evidence report updated; no release-map/package/config migration performed. Same containment blockers remain.
<!-- SECTION:NOTES:END -->

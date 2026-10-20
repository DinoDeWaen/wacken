---
id: TASK-225
title: 'SPIKE: Audit Groma scan, duplicate findings and disconnected architecture'
status: Done
assignee:
  - '@codex'
created_date: '2026-10-20 05:43'
updated_date: '2026-10-20 06:02'
labels: []
dependencies: []
references:
  - wacken
modified_files:
  - backlog/docs/groma-audit-2026-10-20.md
priority: high
type: spike
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Investigate the current Groma scan and lint, review every duplicate candidate, explain disconnected components and scanner coverage limitations, and deliver an evidence-based report with prioritized recommendations. Scope is investigation and reporting; application refactoring and architecture curation are excluded.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Fresh scan and complete lint results are recorded with tool versions and limitations.
- [x] #2 Disconnected components are inventoried and compared with source usage to distinguish missing map relationships from unused code.
- [x] #3 Every lint finding is reviewed and recommendations include source evidence and priorities.
- [x] #4 A full report records scan changes, validation, architecture and documentation impacts, and follow-up actions.
- [x] #5 README impact is recorded using the canonical delivery-governance wording.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Preserved the pre-scan map in /private/tmp and completed two fresh scans and full lint.
2. Independently checked ownership, containment, all stored relationship endpoints and all disconnected records; reviewed representative actual wiring.
3. Classified all 99 duplicate groups with source locations and preserved full raw output.
4. Exported and inspected browser map; delivered report with findings, root-cause confidence, recommendations and scope limits.
5. Verified report counts, source links and byte-for-byte map stability. Documentation-only change; no application implementation or architecture decision. No deviation in investigation scope.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary
Delivered backlog/docs/groma-audit-2026-10-20.md with full investigation, source evidence, all 72 disconnected records, review of all 99 duplicate groups / 199 comparisons, raw tool output and prioritized remediation proposals. No application code or curated map changed.

## Acceptance criteria validation
1. Groma 0.6.6 and Java scanner 0.2.2 recorded; two fresh scans, full lint and limitations included.
2. Independently inventoried 111 components / 289 owned sources / 42 authored relationships. Found 72 islands: 70 own existing source with other-main-file lexical witnesses; 2 empty records. Representative runtime wiring inspected. No duplicate owners, missing owned files or unresolved relationship endpoints.
3. Appendix B gives a disposition for every lint group; Appendix C retains every candidate location. Confirmed timeout policy divergence and duplicate uncontrolled image loading; other similarities classified without claiming 99 defects.
4. Full report includes source/map causes, unchanged-rescan evidence, missing container boundaries, untracked baseline, missing CI gate, risks, next steps and complete scope limitations.
5. Canonical README and other impact wording included in report and below.

## How to test
### Automated tests
Ran two groma scan commands: created 0, refreshed 109, matched 0, findings 99 on both.
Ran groma lint --max-count 200: exit 1, all 99 groups present; 199 comparisons counted.
Independent source/map analysis: no duplicate owners, no missing owned paths, all 42 relationship endpoints resolve.
Report verification passed: exactly 72 inventory rows, exactly 99 disposition rows, all linked repository source files exist.
diff -qr against pre-scan snapshot: no differences.
git diff --check passed.
No application unit, instrumentation, Android lint or APK build was run for this documentation-only investigation.

### Manual validation
Inspected static browser export; system directly contains 111 components, with no container records.
Opened ViewBandRatingContextUseCase; rendered details have no description/relationships, matching CLI source view.
Read direct callers for CSV creation/review, verified links, after-rating, context, correction and composition.
Inspected duplicate source bodies, including explicit master-data timeouts versus four sibling sends without timeout setters.
Existing unrelated workspace changes and untracked Groma content preserved.

## TDD / BDD / approval-test evidence
Investigation and report only; no new or changed application behaviour. Repeated scan and source/relationship checks provide audit evidence. Proposed characterization, negative controls and behavioural tests are future remediation, not claimed as implemented.

## Architecture impact
Architecture-significant change: none implemented. Proposed curation, CI strategy and transport changes require separate assessment; this task does not authorize changing application boundaries.
Approval received: user explicitly requested scan, lint, investigation and full report.
ADR impact: none, because this audit records findings without deciding or changing architecture.

## README impact
README impact: none, because setup, implemented commands and application behaviour were not changed; report documents findings and proposed follow-up.

## Business requirements impact
Business requirements impact: none, because no scope or business rule was changed; current requirements supplied the comparison baseline.

## Diagram impact
Diagram impact: none, because the existing map was inspected/exported but stayed byte-for-byte unchanged.

## Commits / logical change list
New investigation task TASK-225 and backlog/docs/groma-audit-2026-10-20.md only. Groma directory was already untracked; it is not included as audit-authored content.

## Risks and follow-up
Groma is not a complete compiler/call graph: unresolved references, framework dispatch and Java-only coverage limit conclusions. Witnesses are lexical evidence, not exhaustive runtime reachability. No stalled network/resource failure or live backend permission check was performed.
Likely recurrence mechanism: incomplete curation plus missing map gate; current scans did not erase links. Historical causation cannot be reconstructed while groma is untracked.
Report proposes tracked baseline, truthful containers and missing workflow relationships, approved architecture gate, bounded networking/image lifecycle work, then selective reuse with 3.0. Remediation remains outstanding; only this investigation is complete.
<!-- SECTION:NOTES:END -->

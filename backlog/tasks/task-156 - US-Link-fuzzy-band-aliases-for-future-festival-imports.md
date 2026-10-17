---
id: task-156
title: 'US: Link fuzzy band aliases for future festival imports'
status: To Do
assignee: []
created_date: '2026-08-12 07:53'
updated_date: '2026-10-17 13:24'
labels:
  - user-story
  - bands
  - aliases
  - import
milestone: m-0
dependencies:
  - TASK-151
  - TASK-215
  - TASK-217
priority: low
type: enhancement
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a group member, I want likely existing canonical bands and confirmed aliases proposed before a new festival is created so rating history and metadata reuse the correct identity without duplicates. Release2.34 scope: fuzzy-search proposals, per-row explicit match/no-match choice, optional durable alias storage, and atomic canonical lineup creation following ADR-0016/TASK-217. No match creates a catalog band then links it. Planning prefill copies each member's established band rating once; after-ratings remain festival-specific and are promoted only on archive. Preserve existing identities, metadata, ratings/history and old exact-name clients. No automatic fuzzy merge, rating merge or multi-group alias ownership. TASK-179's post-import linking remains available but does not complete this pre-creation/alias scope.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given an imported band name does not exactly match an existing band, then a future import flow can propose likely matches instead of immediately creating only a duplicate.
- [ ] #2 Given a user confirms a proposed match, then the lineup entry links to the existing band and the imported name can be stored as an alias.
- [ ] #3 Given no candidate is confirmed, then a new band record is created.
- [ ] #4 README, business requirements, diagram, and ADR impact are recorded using delivery-governance wording when implemented.
- [ ] #5 Given a reviewed canonical band or alias is selected before creation, then metadata is reused and planning prefill comes from that member's established band rating under ADR-0016; linking cannot merge/overwrite ratings, after-ratings or history.
- [ ] #6 Existing published exact-name creation remains compatible while the reviewed creation path is introduced; the completed first post-Wacken implementation is retained as baseline rather than being blocked retroactively.
<!-- AC:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Update 2026-08-23: User reported a concrete CSV import defect after Summer Breeze upload. Reviewed fuzzy/manual linking and missing-field metadata enrichment are now captured in task-179, which should be treated as the actionable replacement for this older future-scope placeholder. Keep this task only as historical context unless task-179 is split during implementation.

2026-10-17 release2.34 assessment: TASK-179 covers post-import reviewed linking but not pre-creation reviewed aliases or durable reuse. Remaining acceptance criteria stay in scope. Concrete boundary proposal: backlog/docs/release-2.34-remaining-architecture-proposal.md (TASK-215); do not implement its schema/API extension before explicit approval.

2026-10-17 user-approved architecture: ADR-0016 governs canonical shared bands, member-specific established band ratings, independent festival planning/after-ratings, one-time creation snapshots and atomic archive promotion. Missing after-ratings retain the prior band rating; archived corrections update it only while their festival remains the source. TASK-216–220 implement and validate the lifecycle. This supersedes earlier latest-event prefill wording; do not treat approved planning as shipped behavior.
<!-- SECTION:NOTES:END -->

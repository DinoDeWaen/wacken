---
id: task-140
title: 'DOC: Define MVP3 business scope'
status: Done
assignee:
  - '@codex'
created_date: '2026-07-02 08:25'
updated_date: '2026-07-02 08:29'
labels:
  - mvp3
  - docs
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Business value: MVP3 needs a clear source of truth before implementation starts.

Scope: update the business requirements with MVP3 roadmap scope for ratings CSV export, real post-show ratings, and no-Wi-Fi festival use.

Out of scope: implementation, release packaging, or changing completed MVP2 behavior.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given MVP3 is planned, when business requirements are read, then the roadmap includes an MVP3 increment for rating export, real post-show ratings, and no-Wi-Fi operation
- [x] #2 Given MVP3 export is planned, when business rules are read, then CSV export requirements are defined
- [x] #3 Given MVP3 post-show ratings are planned, when business rules are read, then real ratings are separate from planning ratings
- [x] #4 Given MVP3 no-Wi-Fi use is planned, when business rules are read, then the offline cached-use boundary is defined
- [x] #5 README impact is recorded using the canonical wording from delivery-governance.md
- [x] #6 Business requirements impact is recorded using the canonical wording from delivery-governance.md
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Read story-writing guidance and current business requirements.
2. Add MVP3 roadmap scope for rating export, real post-show ratings, and no-Wi-Fi festival use.
3. Add MVP3 workflows, business rules, concepts, reporting notes, edge cases, and open question.
4. Create focused MVP3 implementation and release backlog tasks.
5. Record validation notes and close the scope task.

Architecture impact: requirements planning only; no architecture implementation change.
README impact: none expected because this task changes product scope source of truth, not implemented setup or current app behavior.
Business requirements impact: updated.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Defined MVP3 in `backlog/docs/business-requirements.md` as a planned increment focused on ratings CSV export, real post-show ratings, and no-Wi-Fi festival use. Created implementation stories `task-141`, `task-142`, `task-143`, and release task `task-144`.

## Acceptance criteria validation

- AC1: MVP3 roadmap row added.
- AC2: BR-077 defines rating CSV export.
- AC3: BR-078 and BR-079 define real post-show ratings as separate from planning ratings.
- AC4: BR-080 defines cached no-Wi-Fi operation after initial data is available.
- AC5: README impact recorded below.
- AC6: Business requirements impact recorded below.

## How to test

Manual review: read `backlog/docs/business-requirements.md` sections for Current Product Scope, Current Implemented Capabilities, Business Rules Index, Business goals, Delivery roadmap, Business capabilities, Workflows 7-9, BR-077 to BR-080, Data and terminology, Reporting or audit needs, Edge cases, and Open questions.

Automated tests: not run, because this is requirements/story definition only.

## Architecture impact

- Architecture-significant change: no implementation change. Future implementation stories explicitly require architecture assessment and approval if persistence/sync/export boundaries change.
- Approval received: not required for requirements planning.
- ADR: none, because no architecture decision was made.

## README impact

README impact: none, because MVP3 is planned scope and the implemented app behavior/setup has not changed yet.

## Business requirements impact

Business requirements impact: updated with MVP3 scope, workflows, business rules, concepts, reporting note, edge cases, and open question.

## Diagram impact

Diagram impact: none, because no architecture relationships changed.

## Risks and follow-up

Open question remains: whether real post-show ratings should sync to Supabase for the group or remain local/personal in MVP3.
<!-- SECTION:NOTES:END -->

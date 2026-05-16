---
id: task-31
title: Update business requirements for file import and Wacken overview
status: Done
assignee:
  - '@codex'
created_date: '2026-05-16 07:25'
updated_date: '2026-05-16 07:26'
labels:
  - docs
  - business-rules
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## Docs: Update business requirements for file import and Wacken overview

**As a** product owner
**I want** the business requirements to match current import and overview behavior
**So that** future stories and implementation follow the intended rules

### Notes
- Capture file-based CSV import.
- Capture update existing master data while preserving ratings.
- Capture Wacken-themed band overview expectations.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Business requirements describe file-based CSV import
- [x] #2 Business requirements state re-import updates master data and preserves ratings
- [x] #3 Business requirements describe Wacken-themed band overview expectations
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Updated business goals/capabilities/workflows to describe file-based CSV import.
2. Added business rules for re-importing master data while preserving ratings.
3. Added business rules and terminology for Wacken-themed band overview.
4. Refined stale open questions now that CSV schema and import behavior are documented.
5. Reviewed the updated business requirements and closed the task.

Architecture impact: none; documentation-only business rule update.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Implementation notes:
- Updated backlog/docs/business-requirements.md with file-upload CSV import language.
- Added business rules BR-040 through BR-048 covering file selection, selected file names, master data replacement, rating preservation, band-only imports, unscheduled labels, and Wacken-themed overview cards.
- Added workflow coverage for selected CSV import and Wacken-themed band browsing.
- Added terminology for festival master data, user rating data, and band overview cards.

Validation package:
- Automated checks run: not needed; documentation-only change.
- Manual validation: reviewed updated sections with rg/sed for consistency.
- README impact: no README change needed.
- Diagram impact: no diagram update needed.
- ADR impact: no ADR needed.
- Approval status: no architecture approval required.
- Risks: none beyond future final Wacken running-order schema still being open.
<!-- SECTION:NOTES:END -->

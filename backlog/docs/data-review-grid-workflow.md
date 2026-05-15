# Data Review Grid Workflow

Date: 2026-05-15

Related task: task-17

## Purpose

The data review grid lets an admin inspect proposed changes from CSV import or Wacken JSON scraping before those changes affect festival data. The grid is the user-facing validation step for BR-034.

The workflow applies to:

- Early band-only updates from the Wacken JSON feed.
- Final CSV imports for bands, stages, performances, distances, and food.
- Later mixed updates where scraped metadata and final schedule data arrive separately.

## Principles

- Proposed changes must not mutate current festival data until the admin accepts them.
- Validation runs before acceptance and after each edit.
- A row can be accepted only when its blocking validation errors are resolved.
- Rejected rows remain visible in the import review until the admin closes or clears the review batch.
- The grid should support line-by-line decisions, but cross-row errors such as overlaps must show every affected row.

## Review Batch

A review batch represents one import/scrape attempt.

| Field | Meaning |
| --- | --- |
| `batch_id` | Internal id for the proposed changes. |
| `source` | `csv`, `wacken-json`, or `manual`. |
| `created_at` | Local timestamp when the proposal was created. |
| `status` | `draft`, `validated`, `partially_applied`, `applied`, `discarded`. |
| `summary` | Counts by entity type, action, and validation status. |

## Row Model

Each grid row represents one proposed entity change.

| Field | Meaning |
| --- | --- |
| `row_id` | Internal row id in the review batch. |
| `source_file` | CSV file name or source feed name. |
| `source_row_number` | CSV row number when available. |
| `entity_type` | `band`, `stage`, `performance`, `distance`, or `food`. |
| `entity_id` | Stable id from the schema. |
| `change_type` | `create`, `update`, `delete`, or `unchanged`. |
| `current_value` | Existing stored value, if any. |
| `proposed_value` | New imported/scraped value. |
| `validation_state` | `valid`, `warning`, or `blocked`. |
| `review_decision` | `undecided`, `accepted`, or `rejected`. |
| `messages` | User-facing validation and warning messages. |

## Row States

| State | Meaning | User actions |
| --- | --- | --- |
| `undecided + valid` | Proposal has no blocking validation issues. | Accept, reject, edit where supported. |
| `undecided + warning` | Proposal can be applied, but needs attention. | Accept, reject, edit where supported. |
| `undecided + blocked` | Proposal cannot be applied. | Edit, reject, inspect linked rows. |
| `accepted + valid` | Row is selected for update. | Undo accept before applying batch. |
| `accepted + warning` | Row is selected despite warnings. | Undo accept, inspect warnings. |
| `rejected` | Row will not be applied. | Undo reject before closing batch. |
| `applied` | Row has already updated festival data. | No edit in this batch; future import creates a new proposal. |

Warnings are non-blocking. Examples:

- Optional music link is missing.
- Biography changed significantly.
- Wacken feed contains historical event data that is ignored for final scheduling.

Blocked states prevent apply. Examples:

- Missing band reference.
- Unknown stage reference.
- Invalid time range.
- Same-stage performance overlap.

## Validation Messages And Resolution Options

| Case | User-facing message | Affected rows | Resolution options |
| --- | --- | --- | --- |
| Missing band reference | `Performance references unknown band "{band_id}".` | Performance row. | Import/create the band row, edit the band id, or reject the performance row. |
| Unknown stage in performance | `Performance references unknown stage "{stage_id}".` | Performance row. | Import/create the stage row, edit the stage id, or reject the performance row. |
| Unknown stage in distance | `Distance references unknown stage "{stage_id}".` | Distance row. | Import/create the stage row, edit the stage id, or reject the distance row. |
| Unknown stage in food | `Food option references unknown stage "{stage_id}".` | Food row. | Import/create the stage row, clear the optional near-stage field, edit the stage id, or reject the food row. |
| Invalid time range | `Performance end time must be after start time.` | Performance row. | Edit start/end time or reject the row. |
| Same-stage overlap | `Performance overlaps another accepted or proposed performance on "{stage_name}".` | Every overlapping performance row. | Edit times/stage, accept only one conflicting row, or reject conflicting rows. |
| Duplicate id | `Another proposed row uses the same id "{id}".` | Every duplicate row. | Edit ids, accept one row and reject duplicates, or reject all. |
| Invalid URL | `"{column}" is not a valid link.` | Band/metadata row. | Edit the URL, clear the optional field, or reject the row. |

## Accept / Reject Flow

1. Admin opens an import/scrape proposal.
2. App validates all proposed rows.
3. Admin reviews rows with filters for `blocked`, `warning`, `valid`, `accepted`, and `rejected`.
4. Admin edits supported fields inline where needed.
5. App revalidates edited rows and any linked rows.
6. Admin accepts valid or warning rows line by line, or uses `Accept all valid`.
7. Admin rejects rows that should not be applied.
8. App applies only accepted rows that are still not blocked.
9. App shows a summary of applied, skipped, and rejected rows.

## Expected Update Behavior

Accepted rows:

- `create`: insert a new entity if the id does not exist.
- `update`: replace the existing entity fields included in the proposal.
- `delete`: not part of MVP 1 unless explicitly added later.
- `unchanged`: no write.

Rejected rows:

- Do not update current festival data.
- Remain in the review batch for audit/context until the batch is closed.

Blocked accepted rows:

- Cannot be applied.
- Must be moved back to `undecided + blocked` with a message explaining why apply was refused.

Cross-row behavior:

- If accepting a stage row resolves unknown-stage errors, linked performance/distance/food rows should revalidate automatically.
- If accepting only one row from an overlap group resolves the conflict, the rejected rows should be marked as skipped/rejected and the accepted row can apply.
- If a later accepted row updates an entity already updated in the same batch, the grid must show the order or collapse proposals into one final row before apply.

## Architecture And Storage Follow-Up

Implementing this workflow will be architecture-significant if it introduces durable review batches, pending-change storage, or new application ports.

Recommended follow-up tasks:

- Add application model for `ReviewBatch`, `ReviewRow`, row decisions, and validation messages.
- Add a review-batch repository port only when the app needs to persist review state between sessions.
- Add an Android admin grid screen for filtering, inline edits, accept/reject, and apply summary.
- Extend the CSV/Wacken import flow so it can produce proposals without immediately mutating repositories.
- Add ADR before durable review-batch storage or sync behavior is implemented.

For MVP 1, an in-memory review batch is acceptable if the admin applies or discards the proposal during one session.

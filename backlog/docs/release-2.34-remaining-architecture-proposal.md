# Architecture approval request: remaining release 2.34 stories

Status: Accepted with the user's 2026-10-17 rating-lifecycle clarification, recorded
in ADR-0016. Implementation of these remaining stories is pending.
Date: 2026-10-17. Assessment task: TASK-215.
Feature tasks: TASK-156, TASK-169, TASK-172. Final release gate: TASK-214.

## Approval and governing architecture

The user explicitly instructed: "Indeed please use this architecture for all stories
and add tasks if needed for this." ADR-0016 governs the standard implementation:
canonical shared bands; personal established band ratings; independent festival
planning and after-ratings; creation copies band ratings once; archive atomically
promotes nonempty after-ratings; missing values retain the old band rating; archived
corrections update only the still-current source. This supersedes any conflicting
latest-event prefill or correction wording below. No repeated approval is needed
for this accepted scope; material deviations still require a new decision.

Added TASK-216–220 for model/migration, member-specific creation snapshots, atomic
archive promotion, correct presentation/export and integrated upgrade validation.

## Why explicit approval was needed

The explicit TASK-202 approval allowed evidence verification with **no new schema**.
It does not cover durable aliases or correction audit records. AGENTS.md,
architecture-decision-rules.md and delivery-governance.md require a new explicit
decision before changing persistence, APIs, security or transaction boundaries.

## Inspected implementation and gaps

| Story | Existing behavior | Gap to complete the story |
| --- | --- | --- |
| TASK-156 aliases | TASK-179 reviews/relinks an already-created active lineup, retaining uploaded_display_name on that festival entry. AddFestivalUseCase/create_festival_from_bands still receives exact names and creates unfamiliar bands immediately. | Review candidate identities before creating a new festival; retain reusable confirmed aliases beyond one festival; atomically create the lineup with selected canonical bands. TASK-179 does not complete these criteria. |
| TASK-169 import grid | ImportCsvActivity directly calls ImportFestivalCsvUseCase; it parses/validates the entire CSV set then replaces local TSV/Room master-data snapshots through the existing import transaction. | A preview of additions/changes/removals, validity and per-row decisions; rejected changes retained from the baseline; validate the accepted combined result before writing. No review screen exists. |
| TASK-172 archived corrections | ArchivedBandDetailActivity disables rating controls. Personal events retain value/date, but there is no correction audit model, correction transaction, conflict check or audit export. Current detail can fall back to another festival's real rating when none exists for the selected archive. | Explicit own-rating edit mode; atomic correction plus immutable before/after/actor/time audit; exact-festival detail and history/export context; remote/local consistency and conflict handling. |

Evidence: application LinkImportedBandUseCase, ReviewImportedBandLinksUseCase,
ImportFestivalCsvUseCase, AddFestivalUseCase; Android ImportCsvActivity,
ArchivedBandDetailActivity, AppRepositories, SyncingFestivalLineupRepository;
Room lineup/rating repositories; Flyway V010/V013; ADR-0011/0012/0014.

## Alternatives considered

### 1. Minimal

Keep post-import TASK-179 linking, add an in-memory import grid and local-only
archived edits. No new backend contract. This cannot satisfy durable shared aliases
or a trustworthy synced correction audit; local corrections could be lost during
sync. Not recommended as completion of the existing milestone.

### 2. Standard — recommended

Complete the existing stories within the present Android/application/Room/
Supabase architecture, with the bounded changes below. No new service, framework,
credential, managed distribution account or multi-group product scope.

### 3. Maximum

A hosted import/diff job service, generalized event sourcing and cross-device
offline correction queues with conflict resolution UI. Larger operations, schema
and delivery scope than these stories require. Defer.

## Recommended scope and contracts

### TASK-156: reviewed aliases before atomic festival creation

- Application-owned matching proposes catalog names and previously confirmed
  aliases. Exact canonical matches remain reusable; every nonexact choice requires
  explicit confirmation. A known alias is a proposal, never an automatic merge.
- Add a small confirmed-alias model/repository and additive shared
  band_aliases table, mirrored in Room. Store normalized alias key, original
  spelling, canonical band identity, confirming actor and timestamp. Use existing
  single-group membership; read shared aliases only as authorized members.
- One normalized alias can identify only one canonical band in this single group.
  A conflicting confirmation fails and requires a fresh review; never silently
  reassign an alias or merge bands/ratings.
- Extend the existing creation gateway with reviewed input rows (uploaded name,
  optional selected canonical identity, optional remember-alias choice). Keep the
  existing exact-name RPC available for older APKs; introduce a reviewed RPC or
  compatible overload with a distinct signature and deterministic request payload.
- The server validates selected identities against current catalog state and
  commits festival, deduplicated canonical lineup, confirmed aliases and creating
  members' established-band-rating prefill in one transaction. Original uploaded names
  remain display/lineage data. Repeated rows selecting one canonical band produce
  one lineup member. Existing bands, ratings and history are never merged/deleted.
- No match creates a separate band. Cancelling preview writes nothing.
- Preserve existing request-id retry, lost-response recovery, archived-identity
  protection and shared-group serialization from ADR-0014.
- Include aliases in the existing master-data sync/cache path. Server-first
  confirmation updates Room transactionally; failure keeps the cache usable.

### TASK-169: review the existing local CSV import

- Stage parsed proposed rows and compare them with the current local import
  baseline. Show type, identity, before/after values, validity and decision.
- Default new/changed rows to review; removals require an explicit accepted
  removal. Unsupported/malformed rows cannot be accepted.
- Apply only selected valid changes. A rejected update/removal retains its current
  row; a rejected addition is absent. Revalidate references and overlaps against
  the final accepted combined dataset; dependency conflicts block all writes.
- Use the existing TSV/Room import transaction and repository boundaries.
  Preserve personal/planning ratings, archived data and unrelated central bands;
  removing a performance/lineup proposal is not permission to delete rating history.
- Keep this action's existing **local CSV import** semantics explicit. It does not
  introduce a new group-wide master-data edit API. Subsequent normal sync may
  refresh the local cache from the shared source, as today.
- Run parsing/apply off the UI thread; retain the staged decisions across rotation.
  No future scraper, remote import service or new import persistence schema.
- Reuse the reviewed creation preview where appropriate, without weakening the
  atomic Add Festival gateway or making shared creation local-only.

### TASK-172: own archived-rating corrections with atomic audit

- Archives remain read-only by default. An explicit correction mode permits only
  the signed-in member's planning or real rating for the selected archived
  festival/band. It never changes another member's rating or reactivates a festival.
- Planning values use the current 0–5 scale. Real corrections use the current
  persisted 1–5 event scale; clearing/deleting real history is not part of this
  story. Existing real events keep their original identity and recorded date.
- Add an append-only rating_corrections audit table and Room cache: operation id,
  rating kind/target, festival and band snapshots, actor, previous/new value and
  server timestamp. Preserve historical context if a festival is later deleted.
- Add a correction application port and authenticated server transaction/RPC.
  Derive actor from auth.uid(), validate membership/ownership and ARCHIVED status,
  lock/recheck the target, compare the reviewed expected value/revision and commit
  the corrected value plus audit together. A stale edit fails with reload guidance.
  Repeating the same operation confirms the result without duplicating audit.
- Prefer SECURITY INVOKER with explicit grants and RLS. Audit creation is controlled
  by the correction transaction; direct rating updates must not bypass auditing
  for archived targets. A narrowly scoped trigger can enforce the invariant where
  existing direct-table clients would otherwise bypass it. Ordinary active rating
  operations retain their current behavior.
- Use remote-first corrections, then atomically cache the acknowledged value/audit.
  Offline users can read history and stage an edit, but cannot claim a saved
  correction; retry confirms uncertain results. A new offline correction queue is
  out of scope. Never overwrite pending active-rating work.
- Show corrected current values and the old → new change, actor and correction
  time in archive detail. Identify real events by festival identity, not display
  name or fallback to a different festival.
- A corrected archived after-rating updates the personal established band rating
  only while that festival remains its source. Planning corrections never promote;
  older corrections cannot overwrite newer established sources or later planning snapshots.
- Add an archived-rating/audit export that retains the existing normal ratings CSV
  contract and labels original event dates separately from correction timestamps.

## Authorization, migration and rollout

- Additive versioned Flyway migrations and corresponding non-destructive Room
  migrations; retain existing public contracts for older APKs where possible.
- No service-role secret in Android. Explicit authenticated grants and RLS for
  each new table/RPC, with member/owner checks; no user-editable JWT metadata policy.
- Test all migrations and constraints against disposable PostgreSQL/PostgREST
  before rollout. Apply through the repository's existing migration process.
  Do not drop/recreate production data or rotate signing keys.
- DB-before-APK rollout with the old exact-name creation path still functional.
  If a required migration is absent, show an actionable failure and keep cached
  data. Binary rollback retains additive tables/audit; do not delete history.
- Write an accepted ADR for aliases/reviewed creation and one for archived
  correction/audit after approval. Update README and affected business rules
  (BR-086 read-only exception, BR-092 aliases and import-grid roadmap status).
  Update relevant Mermaid diagrams if the approved port/data map needs them.

## Validation gates before release

- BDD/TDD: pre-create fuzzy/alias proposals, explicit match/no match, duplicate
  canonical choices, alias conflicts, cancellation and exact-name compatibility.
- Import grid: new/change/remove, invalid row, rejected/accepted mixed set,
  dependency/overlap conflict, preserved ratings/history, rollback and rotation.
- Corrections: read-only default, own vs other user, archived vs active, before/after
  audit, stale/concurrent edits, idempotent retry, offline/remote/cache failure,
  exact-festival display, audit export and original event-date preservation.
- Real PostgreSQL/PostgREST/RLS allow/deny tests, transaction fault injection,
  Room migration tests and native entry/approval/failure tests.
- Retain mandatory Summer Breeze 63-band atomic creation API gate, full native
  suite, unit/QA/coverage, signed release lint/build and previous-APK upgrade proof.

## Current provider documentation check

Read Supabase's changelog index and current RLS/functions documentation on
2026-10-17. The announced November TLS-cipher removal is relevant to future
older-device compatibility; verify modern TLS during the final supported-device
gate. No proposed SDK/framework change follows from this assessment.

Sources:
- [Supabase RLS](https://supabase.com/docs/guides/database/postgres/row-level-security)
- [Database functions](https://supabase.com/docs/guides/database/functions)
- [TLS change notice](https://supabase.com/changelog/cbc-tls-ciphers-deprecated)

## Approved delivery direction

The user approved the **standard architecture, revised by ADR-0016**, for TASK-156, TASK-169 and TASK-172
and TASK-216–220, using the above application ports, additive Room/Flyway schema,
authenticated transactional APIs and validation gates. This approval does not
claim those features or release2.34 are finished. The selected signed GitHub APK
channel remains, and the validated Ironline/Field Notes implementation stays in3.0.

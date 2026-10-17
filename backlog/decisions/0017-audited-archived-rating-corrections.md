# ADR 0017: Audited own-rating corrections for archived festivals

## Status

Accepted under the user's 2026-10-17 instruction to use the clarified architecture
for all stories, TASK-215 and ADR-0016, including the standard TASK-172 proposal.
Implementation: TASK-172. No new service, credential, framework or correction queue.

## Context

Archived records must remain readable while allowing a member to explicitly
correct an opinion. Direct updates would lose the previous value, overwrite
concurrent edits, or incorrectly replace a newer established band-rating source.

## Decision

Keep archives read-only by default. The explicit correction dialog loads the
member's current value/revision, stages a proposed value and requires confirmation.
Planning accepts0–5; after-rating corrections accept1–5. Clearing real history
or reopening a festival is outside this slice.

The application validates member/target/kind/value. Authenticated review and
correction RPCs derive the actor from auth.uid(), validate membership and the
archived canonical lineup. Transport is bound to the member who opened/reviewed
the operation and rechecks that identity on token retry, preventing an account
switch from writing or publishing another member's state. Server writes
compare the reviewed value and server-owned
revision. Existing public wrappers delegate to narrow private-schema functions
with empty search paths. Client roles cannot write audit or after-state tables.

FlywayV017 adds server-owned revisions and append-only rating_corrections:
operation, actor, festival/band snapshots, kind, old/new values, reviewed revision,
original recorded date and correction time. The server serializes lifecycle
changes and commits target, eligible established-band update and audit together.
The original event identity/created date remains; correction time is separate.
Missing original dates stay unknown. Explicitly adding a missing after-rating
creates its first recorded fact at correction time without inventing a show date.

Only an after-rating whose festival is still the established source updates that
band opinion. Preserve the original establishment ordering/time. Older corrections
and planning corrections cannot change a newer source or later planning snapshots.
Operation replay confirms the prior audit without reapplying its value. A stale
review fails with reload guidance. Direct archived planning/history mutations are
guarded; matching history replay and actual festival deletion/detach remain safe.
Client history deletion is revoked.

Room10→11 adds an audit cache without changing existing ratings/history/auth.
Remote confirmation plus rating snapshots and audit publish in one Room
transaction. A pending target captured for explicit recovery may be resolved;
its original offline fact/date is retained. A newer pending operation causes
cache publication to fail and keeps the data for review. Other pending work
remains independent. Audit sync appends immutable owner-scoped facts.

A separate correction-audit CSV includes corrected values, before/after, actor
and distinct original/correction dates. The existing normal ratings CSV remains
unchanged; broader contextual exports remain TASK-219.

## Consequences

- Corrections require connectivity to load/recheck and save; offline staging is
  not a new durable correction queue and cannot falsely claim a saved result.
- An uncertain save can retry its stable operation. A cache failure reports that
  remote correction may already be saved; replay safely confirms recovery.
- Audit snapshots survive festival deletion. Original personal history remains.
- Apply additive database migrations before the APK. Older status/history writes
  may receive an actionable update/correction-required error.

## Alternatives considered

- Local-only edits: rejected because sync could overwrite them and audit would
  not be trustworthy across devices.
- Direct row updates: rejected because they bypass audit/source/revision rules.
- New offline correction queue/event-sourcing service: outside the approved slice.

## Validation

Real PostgreSQL/RLS/PostgREST tests cover owner isolation, stale/concurrent edits,
idempotent replay, injected audit rollback, original dates, direct-write guards,
source eligibility and later planning preservation. Room/native tests cover
additive migration, audit immutability, transactional cache/pending recovery,
read-only default and explicit confirmation/failure. JVM tests validate application
rules, wire acknowledgements and audit CSV ownership/escaping.

## Links

- [ADR-0016](0016-band-catalog-and-archive-promoted-personal-ratings.md).
- [Accepted proposal](../docs/release-2.34-remaining-architecture-proposal.md).
- TASK-172; TASK-219 contextual views/exports; TASK-220/214 final validation/release.

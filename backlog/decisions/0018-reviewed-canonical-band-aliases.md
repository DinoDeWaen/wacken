# ADR 0018: Reviewed canonical band aliases before festival creation

## Status

Accepted, 2026-10-17. The user's instruction to apply the canonical band architecture
to all stories approved the standard remaining 2.34 proposal under TASK215/ADR0016.

## Context

Exact-name creation remains a published compatibility contract. Nonexact spellings
can create duplicates, while automatically merging fuzzy matches risks assigning
metadata and personal history to the wrong artist. Post-import linking alone does
not provide pre-creation review or reusable confirmed aliases.

## Decision

- Keep matching in a framework-free application review use case. Exact canonical
  names are reusable; fuzzy and remembered-alias candidates require explicit
  choice. Cancellation writes no festival or alias.
- Carry stable catalog IDs and reviewed names across the boundary. Reject stale,
  missing, inactive or ambiguous selected catalog identities.
- Keep the old create_festival_from_bands RPC/signature. Add a distinct reviewed
  RPC, reusing its authorized creation transaction and shared lifecycle lock.
- Add V018 band_aliases: normalized spelling key, original spelling, band ID,
  confirming member and server time. Normalization folds ASCII case/spacing and
  retains punctuation/non-ASCII spelling. Clients have member-scoped SELECT only.
  Confirmed aliases cannot be reassigned through client writes.
- The private authenticated operation atomically commits aliases, deduplicated
  canonical lineup, any new bands and each member's established planning snapshot.
  A conflicting remembered alias rejects the whole operation for fresh review.
  No-match ignores alias suggestions and creates/reuses only the exact name.
- Store the normalized reviewed request in the existing creation ledger. Retry
  payload/actor checks prevent different choices from masquerading as a retry.
  Replays return committed state without resetting later ratings.
- Multiple imported spellings selecting one canonical identity yield one lineup
  member. Its display spelling is deterministic; every choice remains in the
  reviewed request payload.
- Add Room12 alias cache. Existing master-data refresh includes aliases; creation
  publishes its alias snapshot with festival/lineup/planning in one cache transaction.
  Fresh authenticated review is required; no offline alias-write queue.
- Bind both review and creation transport to the captured member. Recheck the
  authenticated account on actual sends, including token refresh retry.

## Consequences

Known identities retain metadata and personal history. Planning prefill follows
ADR0016, once per member; alias linking never merges opinions, events or bands.
An explicit create-new choice can coexist with a remembered alias; exact canonical
names retain precedence on later imports. Stale/conflicting review requires retry.
The UI stages each nonexact choice and then asks for final creation confirmation.

Apply V018 before the new APK. Older exact-name clients continue to work.
Binary rollback leaves additive alias/ledger data intact.

## Alternatives considered

- Post-import linking only: does not satisfy reusable alias/pre-creation review.
- Automatic fuzzy merge: unsafe identity assumption, rejected.
- Separate import service/general merge system: unnecessary scope and operations.

## Validation

Domain/application tests cover suggestions, explicit identity/no-match, invariant
validation and cancellation without persistence. Real PostgreSQL/PostgREST tests
cover replay/concurrency, membership, alias conflicts and transaction failure,
canonical metadata, every-member planning snapshots and unchanged after-ratings.
Native tests cover staged choice/final confirmation/cancel and additive Room cache.
The legacy Summer Breeze63 creation contract remains a mandatory gate.

## Links

- TASK156; TASK215; TASK217; release gate TASK214.
- [Accepted proposal](../docs/release-2.34-remaining-architecture-proposal.md)
- [Rating architecture](0016-band-catalog-and-archive-promoted-personal-ratings.md)

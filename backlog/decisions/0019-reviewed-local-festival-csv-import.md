# ADR 0019: Reviewed local festival CSV changes

## Status

Accepted, 2026-10-17, under the user's approved standard remaining2.34 proposal
and governing canonical-band/personal-rating architecture in ADR0016 (TASK215).

## Context

The legacy Android import validated a complete CSV set and immediately replaced
local master-data snapshots. Members could not accept individual rows. Replacing
the global catalog also risked hiding unrelated canonical bands and an imported
lineup did not consistently update the active festival's lineup entries.

## Decision

- Review in a framework-free application use case using existing repository ports.
  Stage additions, changes, removals, original/effective values and validity.
  The application creates the immutable review; the UI supplies accepted row IDs.
- Default every proposed change to unchecked. An unselected companion file keeps
  its baseline. A selected header-only file proposes explicit removals.
  Structural/malformed/duplicate rows cannot be accepted and cannot imply removals.
- Resolve CSV reference IDs through supplied companion files or exact persisted
  names. Legacy TSV/Room retain names and band/start keys, not CSV IDs; changing
  such an identity is an explicit remove/add pair. Never infer fuzzy identity.
- Revalidate references and performance overlaps against the accepted combined
  dataset. Reject stale master-data reviews before writes, inside the transaction.
  Cancellation writes nothing.
- Retain the complete canonical catalog. A removed band row removes only the
  active lineup link. New rows may add a band or fill missing metadata; existing
  nonempty canonical metadata and music links stay authoritative.
- Preserve uploaded display names for existing lineup links, other festivals'
  performances/lineups, all personal/planning/after/history/audit/source data and
  pending work. Import has no rating repository and cannot promote an opinion.
- Apply through the existing TSV backup/restore and Room transaction, including
  the active lineup. No schema, service, credential, remote import API or dependency.
- Keep local semantics explicit: later normal shared-source sync may refresh cache.
  Group-wide festival creation remains the separately reviewed atomic RPC.
- Parse and save on workers. Retain staged review, decisions and in-flight work
  across rotation without retaining an Activity or putting full CSV contents into
  Android saved-state Binder transactions. After process recreation without the
  retained state, reselect files and review again; no old approvals are replayed.

## Existing constraints

The legacy food model stores names only. Optional food near-stage references are
validated during review but do not introduce new food-location persistence.
This workflow does not make TSV/Room atomic across process termination/power loss;
it retains the existing coordinated rollback guarantee for reported write failures.

## Consequences

Members can apply a valid subset while keeping rejected changes and opinions.
Dependent choices can fail combined validation, requiring another selection.
Local imports do not establish new planning snapshots or alter rating provenance.
Natural persisted identities make rename/start-time changes explicit remove/add
decisions. No broad merge engine or new import backend is introduced.

## Alternatives considered

- Immediate snapshot replacement: cannot satisfy explicit row review/preservation.
- New hosted import/diff service or persistent staging schema: unnecessary scope.
- Automatically accepting removals/matching similar names: unsafe assumptions.

## Validation

Application tests exercise new/change/remove, invalid columns/dates/numbers/IDs,
duplicate identities, rejected dependencies, overlap, metadata protection and stale
review. Native tests use actual screen/dialog, real Room and TSV transaction,
including final-write rollback, all opinion/pending rows and rotation retention.
Full JVM/QA/lint, native API23/API36, creation/API baseline and signed build remain
required gates.

## Links

- TASK169; final release214; integrated lifecycle220.
- [Accepted remaining proposal](../docs/release-2.34-remaining-architecture-proposal.md)
- [Canonical band architecture](0016-band-catalog-and-archive-promoted-personal-ratings.md)

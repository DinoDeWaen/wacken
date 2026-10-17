# ADR 0016: Canonical band catalog and archive-promoted personal ratings

## Status

Accepted on 2026-10-17 by explicit user instruction:
"Indeed please use this architecture for all stories and add tasks if needed for this."

This accepts the lifecycle explained immediately before that instruction, including
personal ratings per member, retaining the previous band rating when no after-rating
exists, and preventing older archived corrections from overwriting a newer source.
It governs all remaining 2.34 stories and subsequent stories, including 3.0.
Implementation is pending TASK-216–220 and the aligned existing stories.

TASK-216 implementation: separate current-band/festival-after records and ports,
Room9→10 and additive FlywayV014, owner-scoped sync and durable reset/replay.
Normal rating edits cannot write the established preference. The public invoker
RPC delegates to a private-schema privileged function with explicit actor/member,
active-festival and lineup checks; authenticated roles have no direct table DML.
This permits atomic state/history/replay without granting client-side arbitrary
preference writes. Recovered old-client values require review because legacy
resets were not synced. Creation and archive promotion remain TASK-217/218.

Supersedes ADR-0011's latest-event-as-current-rating rule and immediate promotion
semantics; revises ADR-0014's history-derived/creator-only prefill for the new
creation path. Existing deployed contracts remain until compatible migration.

## Context

A band is reusable across festivals. Shared metadata belongs to that band;
personal preferences belong to each member. Festival planning and performance
assessments must retain their own context. Current code updates a global latest
real rating immediately, derives prefill from latest history, and archives by
changing status only. Those behaviors do not implement the approved lifecycle.

## Decision

Retain the existing Android → application/domain → adapters architecture,
Supabase central database, Room offline cache and authenticated sync. No separate
database service, framework, credential or signing/distribution change.

### Concepts and ownership

| Concept | Business key and responsibility |
| --- | --- |
| Canonical band | Stable band identity; shared name, information/biography, image and verified YouTube/Spotify links. Exists independently of a festival. |
| Personal current band rating | User + canonical band; last established value and its source festival/event or explicit legacy provenance. Never a single shared group rating column on bands. |
| Festival lineup entry | Festival + canonical band; preserves uploaded display name/approved alias lineage. Reuse an existing band or create it before linking. |
| Festival planning rating | User + festival + band; editable intent to attend, used by that festival's group schedule. |
| Festival after-rating | User + festival + band; current assessment of the performance, independent of planning and the established band rating. |
| Rating history/correction audit | Original event context and append-only corrections; actor, old/new values and relevant event/archive/correction times remain distinguishable. |

The domain/application owns carry-over, promotion, missing-value rules and
correction eligibility. Adapters persist/enforce authorized atomic outcomes;
Activities and callers do not supply interchangeable rating policies.

### Creation

Resolve the canonical band for each uploaded row. Exact matches reuse it;
nonexact/alias proposals require explicit per-row confirmation. No match creates
a new catalog band, then links it. Shared metadata is not duplicated per festival.

Copy each applicable existing member's established band rating into that member's
new festival planning rating once. Missing values remain unrated; the new festival
after-rating starts empty. Later edits to any role do not refresh the snapshot.
Creation remains one authorized, deduplicated, replay-safe transaction, preserving
the published atomic creation and archived-identity protections.

### Active festival

Planning and after-ratings are scoped to this festival and member. Editing/resetting
either cannot change the other role, another festival or the current band rating.
Keep existing offline local-first editing, pending-sync protection and scale:
1–5, with 0/absence meaning unrated. History may record several changes, but the
final current festival after-rating determines promotion, not a stale older event.

### Archiving

Archive status and promotion of each member's final nonempty after-rating to their
matching current band rating commit together. Planning ratings never supply the
promotion value. Missing/reset after-ratings retain the previous established value.

Persist source provenance and promotion ordering so retry cannot duplicate effects
or overwrite a later established source. Cache the acknowledged result atomically.
Denied/offline/interrupted requests cannot falsely report completion; retain pending
data and give recovery for unresolved local/remote conflicts before finalization.
Existing/older status-write paths cannot bypass promotion when the new lifecycle is
enabled. Authorize the group-wide operation server-side without exposing other
members' private rating history or service-role secrets to Android.

### Archived corrections

Read-only remains the default. Explicit own-rating correction saves value and audit
atomically. Changing archived planning never updates the band rating. Correcting
an archived after-rating updates the established band rating only if that festival
is still its source; an older correction cannot displace a newer source.
Already-created festivals retain their planning snapshots.

The bounded TASK-172 slice corrects stored real after-ratings on the existing 1–5
scale; deleting real history is outside that slice. Audit and original event dates
remain available and separately labeled. Lookup by festival identity, never a
different festival's fallback value.

### Persistence, migration and compatibility

Use additive versioned Flyway and non-destructive Room migrations, focused ports
and existing sync adapters. Persist current personal band ratings and festival
after-ratings explicitly; existing history events remain history rather than the
implicit current rating source. Apply authenticated grants/RLS and transactional
ownership/status/conflict checks.

Preserve all legacy ratings, original/unknown dates, canonical references, auth,
metadata and pending work. Backfill from reliable archived outcomes; retain and
surface ambiguous legacy provenance instead of inventing dates or promoting active
festival outcomes. A precise backfill inventory and tests are part of TASK-216.
Retain old creation contracts where possible; guard unsafe legacy archive/correction
writes. Roll out database compatibility before the new APK. No destructive reset,
unreviewed merge or key rotation.

### Remaining features

TASK-156 aliases must resolve the canonical band before creation and retain rating
identity. TASK-169 row review cannot import/replace user ratings or silently delete
shared catalog/history; local import semantics stay explicit. TASK-202 enrichment
updates missing canonical metadata only. TASK-172 follows the conditional source
correction rule above. All 3.0 views keep these rating roles independent.

## Alternatives considered

1. Keep latest edited history event as the current band rating: rejected; active
   festival edits would carry forward before archive.
2. Explicit personal current rating plus independent festival values and atomic
   archive promotion: accepted by the user.
3. New service/generalized event-sourcing platform: unnecessary for this scope.

## Consequences

- Extra persistence and coordinated migration/sync work is required.
- No new service or framework is required.
- Archive becomes a business transaction, not a status-only edit.
- Existing published data needs conservative provenance-aware migration.
- Creation must seed member-specific snapshots; it cannot copy one shared rating.
- Failures, concurrency and old-client behavior need real database/native tests.

## Validation

Core example: current band4 → new planning4 → planning5 + after3 →
archive preserves5/3 and establishes band3 → next festival planning3.
Test multiple users/festivals, unknown/reset values, source-aware corrections,
rollback, concurrency, idempotent retry, RLS, sync/pending work and signed upgrades.

## Links

- TASK-215 decision/planning; TASK-216 model/migration; TASK-217 creation;
  TASK-218 archive; TASK-219 presentation/export; TASK-220 integration/upgrade.
- Existing TASK-156, TASK-169, TASK-172 and final release TASK-214.
- [ADR-0011](0011-post-wacken-festival-rating-model.md).
- [ADR-0014](0014-atomic-festival-creation.md).
- [Revised delivery proposal](../docs/release-2.34-remaining-architecture-proposal.md).

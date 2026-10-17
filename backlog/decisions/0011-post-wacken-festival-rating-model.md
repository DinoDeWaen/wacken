# ADR 0011: Post-Wacken Festival Rating Model

## Status

Accepted

Rating lifecycle superseded on 2026-10-17 by [ADR-0016](0016-band-catalog-and-archive-promoted-personal-ratings.md).
The current band rating is established at festival archive, not by every latest
history event. Festival planning and after-ratings remain independent per user,
festival and canonical band; new planning values copy the established band rating.
The decision below records the earlier implemented model, not the new target.

## Context

The app has grown from Wacken-only planning into a product that must preserve
festival history and reuse personal band ratings for future festivals.

The current model is not festival-aware enough for that direction:

- Supabase `bands` are reusable in practice, but performances and planning
  ratings are not scoped to a festival.
- Room ratings and real ratings are keyed by user name and band name only.
- Real post-show ratings are local-only and do not retain a festival/date
  history.
- The current active festival is implicit instead of represented as a domain
  concept.

The validated first post-MVP3 scope keeps the app for one friend group, supports
only one active festival at a time, makes archived festivals read-only, reuses
bands by exact name, syncs personal band rating history to Supabase, and uses
the latest personal band rating to prefill new festival planning ratings.

Fuzzy band linking, alias storage, multiple upcoming festivals at the same time,
multiple independent groups, invite flows, and archived-festival editing are
future or out of scope.

## Decision

Use the standard architecture option: introduce explicit festival, lineup,
planning-rating, and personal-rating-history concepts across the domain,
application ports, Room cache, Supabase schema, and sync adapters.

### Domain model

Add domain concepts for:

- `Festival`: an event with identity, name, status, and optional date range.
- `FestivalStatus`: at least `ACTIVE` and `ARCHIVED`.
- `FestivalLineupEntry`: links one festival to one reusable band and preserves
  the uploaded festival-specific display name when it differs from the canonical
  band name.
- `FestivalPlanningRating`: a user's intent to see one band at one festival.
- `PersonalBandRatingEvent`: a user's personal rating for a band, with optional
  festival reference and created date.

The domain/application core owns these invariants:

- Only one festival can be active in the first post-Wacken version.
- Archived festivals are read-only for planning and master-data edits.
- Festival planning ratings and personal band rating events are independent.
- Group schedule decisions use festival planning ratings for the active
  festival.
- The current personal band rating is the latest personal band rating event by
  created date.
- New festival planning ratings are prefilled from personal band rating history
  only, never from older festival planning ratings.
- First-version band reuse is exact-name reuse; fuzzy matching and aliases are
  deferred.

### Application ports and use cases

Add explicit ports instead of stretching the current name-only rating
repository contracts:

- `FestivalRepository`
- `FestivalLineupRepository`
- `FestivalPlanningRatingRepository`
- `PersonalBandRatingHistoryRepository`

Existing use cases may keep compatibility wrappers while individual stories
migrate screens and schedule generation, but new post-Wacken behavior must pass
festival context explicitly through application use cases.

Add or evolve use cases for:

- reading the active festival and archive start state
- archiving the active festival
- adding a new festival after archive
- importing a festival lineup and linking exact-name band matches
- saving/syncing festival planning ratings
- saving/syncing personal band rating events
- prefilling planning ratings from latest personal band ratings
- reading archived festival and band rating history

### Supabase model

Evolve the Flyway-managed Supabase schema with additive migrations for:

- `festivals`
  - id, slug/name, status, optional starts/ends date range, archived timestamp,
    timestamps
  - one active festival enforced for the first post-Wacken version
- `festival_lineup_entries`
  - festival id, band id, uploaded display name, timestamps
  - unique festival/band relationship
- `performances`
  - add festival id and keep band/stage/time references
- `festival_planning_ratings`
  - group id, user id, festival id, band id, rating, sync timestamps
  - unique group/user/festival/band row
  - readable to members of the one shared group and writable by the owning user
- `personal_band_rating_events`
  - user id, band id, optional festival id, rating, created at, timestamps
  - append-style history so multiple ratings for the same band across festivals
    remain visible
  - readable/writable by the owning user; platform admin access remains
    available for operational support

The existing `ratings` table is treated as the legacy planning-rating table.
Implementation may either migrate it in place or create
`festival_planning_ratings` and backfill from `ratings`; the implementation
story must choose the least risky migration path after inspecting live-data
constraints.

Existing Wacken master data is backfilled into an initial festival record. Any
legacy local real ratings without a stored creation timestamp must receive a
best-available created date during migration because the original timestamp was
not recorded.

### Room and offline sync

Room remains the offline-first read/write model from ADR 0010.

Add Room tables or migrate existing ones for:

- festivals
- festival lineup entries
- festival-scoped performances
- festival planning ratings with pending/synced metadata
- personal band rating events with pending/synced metadata

Mutable rating writes follow the existing local-first sync pattern:

- user edits are stored locally first
- pending operations are pushed when Supabase is available
- successful remote writes mark local records synced
- remote pulls do not overwrite pending local writes for the same business key
- network failure must not clear a valid local session or block cached work

### Missing festival rating recovery

Before pushing a pending planning rating, the sync adapter checks its festival
against the refreshed festival cache. A missing festival produces a typed
diagnostic containing the stored festival ID, band, and value; the pending row
is retained. The user may confirm moving that one rating to active Wacken 2026.
An application use case checks both Room and the exact remote
user/festival/band key for a destination rating. Room moves the pending key in
one transaction, rechecking the original value and destination key. Any conflict or failed remote check
leaves the original row pending. A successful move remains pending until the
normal Supabase push succeeds. This is a repair of an orphaned local reference,
not a general festival merge mechanism.

### Festival write authorization

Members of the single shared group may insert the next active festival, active
band records needed by its CSV, and lineup entries for an active festival.
`V012__group_member_festival_creation.sql` grants only these insert operations;
non-members remain denied and metadata updates retain their existing admin
policy. The Android festival adapter checks whether the festival ID already
exists, uses `PATCH` for archive and rename, and uses `POST` for a new festival.
Band and lineup creation use plain inserts so they do not require update
permission. A failed remote festival write leaves the Room festival unchanged
and reports the error. The Add Festival action runs this remote work outside
the Android UI thread.

TASK-197 replaces the new-festival sequence with the atomic creation operation
and request replay contract in [ADR 0014](0014-atomic-festival-creation.md).
The complete response is published to Room in one transaction; generic lifecycle
updates remain separate. Exact names receive collision-safe identities and
creation cannot reactivate an archived identity.

### Import and band matching

Festival imports remain validated CSV-based imports.

When importing a new festival lineup:

- exact band-name matches reuse existing band records
- unmatched names create new band records
- uploaded lineup display names remain available on the lineup entry
- fuzzy search, alias proposals, and alias storage are deferred to a future
  story

## Consequences

Positive:

- Festival history becomes a first-class concept instead of an implicit Wacken
  assumption.
- Planning ratings can vary by festival without corrupting personal band
  history.
- Real/seen ratings become durable personal history and can sync across devices.
- Future festival setup can prefill useful planning defaults.
- The design follows the existing Clean Architecture and offline-first sync
  boundary.

Negative / trade-offs:

- Several repository ports, Room entities, Supabase migrations, import scripts,
  and sync clients must change in coordinated stories.
- Existing name-keyed domain objects will need stable identity handling for
  future festival-aware workflows.
- Backfilling legacy real ratings cannot recover original creation timestamps
  because they were not previously stored.
- Deferring fuzzy aliases means similar names can create duplicate band records
  in the first version.
- Database-only enforcement of archived read-only behavior may be limited; the
  domain/application layer must enforce it consistently.

## Alternatives considered

- Minimal option: add `festival_id` columns to existing tables and patch the
  current repositories. Rejected because it keeps planning ratings, real
  ratings, and festival history too tightly coupled to the current Wacken-only
  assumptions.
- Standard option: introduce explicit festival, lineup, planning-rating, and
  personal-rating-history concepts across domain ports, Room, Supabase, and
  sync adapters. Accepted because it matches the actual boundary change without
  adding future-only product scope.
- Maximum option: implement multiple independent groups, multiple upcoming
  festivals, alias management, audit history, admin-only archive permissions,
  and richer conflict resolution now. Rejected because those features are
  explicitly future or out of scope for the first post-Wacken version.

## Links

- Related task: task-149
- Related stories: task-150, task-151, task-152, task-153, task-154, task-155
- Festival write-path correction: task-189
- Missing festival rating recovery: task-190
- Group-member festival creation: task-195
- Future scope: task-156, task-157
- Related ADR: `0008-supabase-postgres-flyway-migrations.md`
- Related ADR: `0010-offline-first-sync-boundary.md`
- Related docs: `backlog/docs/business-requirements.md`

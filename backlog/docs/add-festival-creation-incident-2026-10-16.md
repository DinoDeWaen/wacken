# Add Festival creation incident, 16 October 2026

Task: TASK-197. Investigation baseline: published V2.33.3, tag `da0dc97`.

## Observed failure and impact

The supplied `data/summer-breeze-2027/bands.csv` contains 63 bands. Its
`Any Given Day` entry differs in case from the existing golden catalog record
`Any given Day`, whose database ID is `any-given-day`.

The application matches exact names, so it decides that the uploaded spelling
is new. It then generates `any-given-day` from that spelling too. Inserting the
new name therefore violates `bands_pkey`. This is an identity-generation bug;
the CSV parses successfully and the group-member insert policy allows the write.

Postgres logs show SQLSTATE `23505` on the band POST at 18:36:16 UTC
(20:36:16 Europe/Brussels). A read-only production inspection found:

- Active festival `summer-breeze`, display name `Summer breeze`.
- Zero lineup entries and one festival planning rating.
- 29 band records inserted during the failed attempt.
- Archived Wacken remains present with 165 lineup entries and 272 planning ratings.

No production records have been changed during this investigation. Existing
bands or history must not be deleted to recover the import.

## Related failures found in the creation flow

| Scenario | Finding | Evidence |
| --- | --- | --- |
| Same slug, different exact band names | Fails with `bands_pkey`; restoring the old upsert could instead overwrite the existing record | Full real CSV regression and member-RLS SQL reproduction |
| A later band write fails | Festival and earlier band writes remain committed | New failure-injection regression |
| CSV contains the same name twice | Duplicate festival/band keys fail the final lineup insert | New duplicate-row regression |
| New festival uses an archived festival ID | Generic save updates the historical festival to ACTIVE and changes its name | New archived-identity regression |
| Cache is stale | The active-festival guard and reuse/prefill decisions read cached data; server state can differ | Repository wiring inspection; integration scenario required |
| Response is lost after a successful request | Creation has no request identity or idempotent retry; the active-festival guard can block retry | Request and use-case inspection; lost-response scenario required |
| Lineup persistence fails | Festival, bands, and earlier prefill work have already occurred; success is not one unit | Use-case ordering; failure-injection coverage to extend |
| Cached band has extra metadata | V2.33.3 now reuses its remote ID in the lineup path without attempting the forbidden metadata PATCH | Existing TASK-195 regression |
| Failed import is retried | A partial active festival can block the attempt, even though its lineup was never saved | Current active-festival guard plus production partial state |

Case-only differences will not be silently merged. The existing business rule
uses exact-name matching; reviewed non-exact linking is a separate workflow.
Database IDs must therefore support distinct exact names even when their
display slugs are identical. The same principle covers punctuation and names
that generate an empty ASCII slug.

## Changes that introduced or exposed the problems

1. `99a4804` (13 August 2026) introduced the sequential festival creation use
   case: save festival, save each band and prefill, then save the lineup. It
   contains no aggregate transaction or retry identity.
2. `277046e` (28 August 2026) connected that sequence to independent Supabase
   write-through requests and generated band IDs from lossy display slugs.
   Exact-name lookup and slug identity were not compatible for all inputs.
3. TASK-195 (`1a50201`, integrated as `2631560`) moved requests off the Android
   UI thread and enabled group-member creation. It changed band/lineup upserts
   to plain inserts to avoid requiring administrator UPDATE privileges.
   This exposed the existing identity collision as an explicit database error.
   Returning to upserts would risk changing the name and metadata of the
   existing golden record. Historical data loss has not been established.
4. V2.33.3 (`da0dc97`) packaged those changes. Its validation covered build,
   signing, installation upgrade, dispatch, request methods, and isolated RLS
   inserts; it did not execute this CSV through the complete feature.

## Why the tests missed it

- Application tests used a fake band repository indexed by exact name. It had
  no independent database ID and could not exhibit a primary-key collision.
- Supabase adapter tests returned one fixed GET response and recorded request
  methods. They did not maintain a catalog or enforce unique constraints.
- The ID test asserted that `Any Given Day` becomes `any-given-day` without
  comparing a second exact name that produces the same value.
- Worker/UI tests verified dispatch and fallback errors; they did not run the
  import or inspect persisted state.
- RLS tests inserted one synthetic band with a hand-selected unused ID. They
  proved authorization and did not compose the real application algorithm.
- Release emulator validation proved upgrade and launch. It did not select
  this CSV and verify the saved lineup on another group-member session.
- No release gate required the real fixture, injected failures, or a retry.

Those checks were useful but insufficient evidence that Add Festival worked.
The earlier completion claim exceeded what had actually been exercised.

## Regression evidence prepared before the fix

`AddFestivalPersistenceRegressionTest` composes the production use case and
Supabase adapters with a stateful API contract double that enforces primary
keys. Running it against V2.33.3 produced four expected failures:

```text
completeSummerBreezeCsvDoesNotOverwriteOrCollideWithKnownBand: bands_pkey
failedBandWriteDoesNotLeaveAnActiveFestivalOrPartialCatalog: partial state
duplicateCsvBandRowsProduceOneLineupEntry: festival_lineup_entries_pkey
archivedFestivalIdentityCannotBeReactivatedByCreatingAnotherFestival: reactivated history
4 tests completed, 4 failed
```

Command, with JDK 21 and Android SDK configured:

```bash
./gradlew :app:testDebugUnitTest \
  --tests 'be.wacken.planner.AddFestivalPersistenceRegressionTest' --no-daemon
```

Separately, all repository migrations V001–V012 were applied to an isolated
local Supabase PostgreSQL container. Under the `authenticated` role and an
actual group-member JWT subject, exact lookup for `Any Given Day` found no
row and inserting `any-given-day` reproduced `bands_pkey`. That transaction
was rolled back. This confirms the database boundary agrees with the diagnosis;
the stateful double is not the only evidence.

## Architecture approval request

Task: TASK-197 — reliable festival creation and complete workflow validation.

### Proposed architectural change

Add a creation-specific application port and one Supabase database operation
that saves the festival, new exact-name bands, lineup, and creating user's
planning prefill in one transaction. Use collision-safe band identities,
protect archived/active festival identities, and make retries idempotent.
Publish the corresponding Room state after complete remote success. Preserve
existing catalog metadata and personal rating history.

### Why it is needed

Independent HTTP writes cannot provide rollback of the complete creation.
Compensating deletes would be unsafe if another device already used a newly
created record. The current partial production state demonstrates this risk.

### Alternatives considered

1. Minimal: fix band-ID collisions in the client. This addresses the visible
   error while leaving partial writes and retry ambiguity.
2. Standard, recommended: one atomic creation operation behind a narrow port,
   with `SECURITY INVOKER`, explicit group authorization, and safe retry.
3. Maximum: introduce a general import job/staging system. Its complexity is
   unnecessary for the current one-group, one-active-festival workflow.

### Impact

- Domain/application: explicit creation operation and validation; preserve
  exact-name matching and rating independence.
- Ports/adapters: creation-specific boundary instead of unrelated saves.
- Infrastructure: one Flyway migration and Supabase RPC; keep RLS in effect.
- Tests: real PostgreSQL/PostgREST integration, full CSV, failure injection,
  duplicate/Unicode/collision cases, retry/concurrency, member/non-member,
  stale cache and Room/UI verification.
- README: describe creation behavior and repeatable validation command.
- Business requirements: define complete creation and safe retry behavior.
- Diagrams: assess whether the new operation needs a flow diagram.
- ADR: document the transaction and retry boundary after approval.

### Approval needed

Explicit approval is pending under `architecture-decision-rules.md` and
`delivery-governance.md`, which require it before a transaction/API change.
An approval question has been presented in this chat. Production implementation
and recovery of the incomplete festival have not started.

## Required completion evidence

The fix must pass the full 63-band fixture through the real API/schema and
verify persisted lineup and history, not only return a successful request.
Inject failure after each logical write and verify rollback; simulate a lost
response and retry; exercise simultaneous requests and stale cache; reject
non-members; verify Android Room publication and responsive UI on the emulator.
Make the integration command a required CI/release gate. Recovery of the
existing incomplete import must preserve existing metadata and ratings and
verify the complete expected lineup afterward.

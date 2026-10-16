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

No production records were changed during the initial investigation. Existing
bands or history must not be deleted to recover the import.

## Related failures found in the legacy creation flow

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

The user explicitly approved the recommended atomic creation change with
“Please proceed” on 16 October 2026, before production implementation.
ADR 0014 records the accepted design. Flyway V013 has been deployed and validated.

## Required completion evidence and outcome

The fix must pass the full 63-band fixture through the real API/schema and
verify persisted lineup and history, not only return a successful request.
Inject failure after each logical write and verify rollback; simulate a lost
response and retry; exercise simultaneous requests and stale cache; reject
non-members; verify Android Room publication and responsive UI on the emulator.
Make the integration command a required CI/release gate. Recovery of the
existing incomplete import must preserve existing metadata and ratings and
verify the complete expected lineup afterward.

## Completed repair and verification

The code now uses a creation-specific application port, one authorized database
transaction and a request ledger, and one Room publication transaction. New
band IDs are independent of display slugs. The same exact-name rule is
preserved; different spellings are not silently merged. Missing name headers
and blank band names are rejected before persistence instead of importing IDs
or dropping rows.

The initial four-red-test baseline is committed at `5e4b334`. The stateful
contract double has since been replaced by real PostgreSQL/PostgREST tests in
the same regression class. The final validation is:

- Ten real HTTP/API scenarios pass, including the actual 63-band CSV,
  case/punctuation/Unicode identities, personal prefill, duplicate rows,
  archived identity protection, stale active-state rejection, lost-response
  retry, concurrent identical and competing requests, and non-member denial.
- SQL fault injection at band, lineup, prefill, and final request-ledger writes
  proves rollback of the whole operation. Replay preserves later rating edits.
- Ten Android instrumentation tests pass: six creation UI/Room cases and four
  existing festival deletion/migration cases. The creation cases cover the
  actual screen with the checked-in CSV, worker responsiveness, error feedback,
  local rollback/retry, stale cache reconciliation, pending edits/history,
  session mismatch, and malformed CSV rejection.
- A clean full unit/QA/coverage run, release lint-vital, and signed APK build pass.
  The local untagged validation APK retains 2.33.3/39 metadata; it is not a new
  published release. Its SHA-256 is
  `ae211c03bb8866d1285220b5ba12c39a22108b02939b7c73794275ae660f047d`.
- Flyway validates all 13 migrations. A rollback-only hosted member-role test
  created and replayed the full CSV and rejected a non-member. Every existing
  data fingerprint matched before and after that verification.
- Security advisor finding names and counts did not change.

The guarded support transaction in
`backend/flyway/repairs/task-197-summer-breeze.sql` then completed the legacy
partial festival. It checked the exact observed identity, status, creation
timestamp, empty lineup, and creating-member evidence, and asserted preservation
of all existing band metadata, planning ratings, festivals, other lineups, and
personal history before committing. The active Summer breeze lineup now has
63 bands and its existing planning rating remains. Archived Wacken retains
165 lineup entries and 272 planning ratings.

`scripts/verify-festival-creation.sh` and the Android instrumentation job are
now part of CI. The release process requires both commands on the release
source. Ordinary unit-run integration skips cannot satisfy the API gate.

GitHub Actions run [38960044738](https://github.com/DinoDeWaen/wacken/actions/runs/38960044738)
passed both jobs on fix commit `d2ab031`: unit/QA/coverage, real PostgreSQL and
PostgREST validation, debug APK build, and Android emulator instrumentation.
The first CI attempt exposed an existing SDK setup default requesting the
removed `tools` package. Both jobs now explicitly install platform-tools,
Android 36 and build-tools 36.0.0; the successful run validates that correction.

Physical-phone CSV/file-picker interaction remains unverified because the
reported phone is not attached. The emulator validates the actual activity,
worker and cache with a supplied CSV selection, while the real HTTP suite
validates the database/API path. A new app build must be installed to deliver
the code fix; the repaired existing festival is available to V2.33.3 through
**Sync from Supabase**.

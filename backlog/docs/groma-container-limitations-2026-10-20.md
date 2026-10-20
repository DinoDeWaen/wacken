# Runtime boundaries and Groma containment limits

TASK-227, 20 October 2026. Baseline: `4630970`; evidence closure: `c5f73cb`.
This describes existing architecture, not a proposed deployment change.

## Existing boundaries

- **Wacken Planner** is the owned software system. The installed Android
  application is one runtime. `app/src/main/AndroidManifest.xml` declares its
  Activities without separate `android:process` values; `app/build.gradle`
  packages the domain, application and infrastructure modules into that app.
  Gradle modules and Groma visual groups are not independent runtime containers.
- **Room cache** is owned on-device storage, not a separately deployed process.
  `WackenDatabase.get` opens `wacken-cache.db` through `Room.databaseBuilder`.
  Entities, migrations and DAOs implement its schema; repository adapters remain
  Java code executing inside the Android process. Their source ownership must
  not be moved into a database box just because they access that database.
- **Shared planner database** is owned application data hosted on external
  Supabase infrastructure. The schema, RPCs and RLS are versioned in
  `backend/flyway/sql`; ADR-0008 and ADR-0016 define their ownership and rating
  semantics. The external Supabase platform provides Auth and HTTPS/PostgREST
  access. The owned database and external hosting platform are distinct concepts.
- **TSV fallback files** are app-private storage used by the documented fallback
  import path; they are not another server. See ADR-0007 and current README.

## Safe CLI experiment

The experiment ran only in `/private/tmp/wacken-235-baseline`, a detached
checkout of `4630970`. No experimental draft was copied into the release branch.
Groma CLI 0.6.6 and `@groma/scanner-java@0.2.2` were used.

```text
groma draft container 'Android application' --parent wacken \
  --overview 'Installed Android process running Activities, application use cases, domain logic and adapters. The app uses an on-device Room database and accesses shared data through Supabase HTTPS APIs.' \
  --technology 'Android / Java'
=> exit 0; draft ID android-application

groma accept android-application
=> exit 1; no scan match

groma edit apprepositories --parent android-application
=> exit 1; cannot move "apprepositories" because it has authored meaning
```

`groma view android-application` confirms `type: C4 Container`, `status: draft`.
It does not establish a current runtime boundary. The CLI guide explicitly
requires component moves to have an empty body and forbids clearing authored
meaning to bypass a refusal. It also requires a scan match before accepting
hand-proposed software. Both restrictions apply here.

## Unresolved decision

The current supported commands do not complete the required preservation-safe
container migration. A supported tooling remedy must allow explicit declaration
of verified existing runtime/storage boundaries and relocation of authored
components while retaining IDs, descriptions, source ownership, relationships
and flow links. Any proposed tool upgrade must be pinned and tested on a
disposable copy before the release map is migrated.

No fake Java source, cleared descriptions, direct architecture Markdown edits,
unmatched current container or guessed scanner match is an acceptable substitute.
All existing components retain their baseline containment under `wacken` until
that remedy is verified. This is a documented tool limitation, not truthful C4
container coverage. TASK-227 remains incomplete; dependent map repairs and the
release validation must not claim this containment issue fixed.

The README C4 view documents the actual boundaries in the meantime. It is
separate from the Groma map and does not repair Groma containment.

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

## Local 0.7.2 recheck — 21 October 2026

The user installed Groma 0.7.2 in repository-local `node_modules/.bin/groma`.
Plain `groma` still resolves to global 0.6.6; the earlier rechecks of the global
command did not examine the local update. The local executable was subsequently
invoked explicitly against the same disposable baseline worktree:

```text
/Users/dino/Documents/backlog/wacken/node_modules/.bin/groma --version
=> 0.7.2

/Users/dino/Documents/backlog/wacken/node_modules/.bin/groma accept android-application
=> exit 1; no scan match

/Users/dino/Documents/backlog/wacken/node_modules/.bin/groma edit apprepositories --parent android-application
=> exit 1; cannot move "apprepositories" because it has authored meaning
```

The local structure guide retains the same preservation restrictions. Its
configuration guidance now refers to `config.json` rather than `scanners.json`;
that is a tooling/configuration change to assess separately, not an instruction
to rewrite architecture records. No local package/config upgrade was committed
or applied to the release map. TASK-227 remains incomplete with the same two
reproduced blockers under the actual local update.

## Published release and upstream confirmation — 21 October 2026

On the next attempt to unblock TASK-227, `npm view groma.md version dist-tags
--json` confirmed that 0.7.2 remains the latest published release. Explicit local
0.7.2 calls in the disposable baseline checkout still returned `no scan match`
on acceptance and `cannot move "apprepositories" because it has authored meaning`
on relocation. The release map remains unchanged.

Upstream [issue 170](https://github.com/MrLesk/groma.md/issues/170), inspected
through the GitHub API, is open and describes both failures. Its analysis
identifies acceptance requiring code on the container itself while only
components can declare code. It also records the described-component move
restriction. No fix for this issue appeared among the open pull requests at
the time of this check. These are upstream implementation findings reported by
the issue, consistent with our independently reproduced CLI failures; no patched
binary has been validated here.

### Concrete tooling change proposed for approval

Task: TASK-227. This changes architecture tooling, not the deployed Android app.

1. Minimal option: retain the accurate README diagram and wait for an upstream
   release fixing both restrictions. TASK-227 and its dependent tasks remain
   incomplete.
2. Standard option (recommended if work should continue now): prepare a small
   Groma source fix in a disposable checkout of upstream commit
   `b8b325fd44f9e78107d271ac794a6e40321dcf53` (0.7.2). Add an explicit declaration
   path for an evidence-backed existing container, including data stores that
   the Java scanner cannot see. Add preservation-safe component relocation that
   keeps IDs, descriptions, technologies, source ownership, relationships and
   flow endpoints. Existing validation must still reject wrong kinds, invalid
   parents, duplicate ownership, collisions and dangling endpoints. No automatic
   acceptance based solely on a directory or import. Review and test the patch
   before pinning the exact patched tool artifact and using its CLI on this map.
3. Maximum option: evaluate another architecture tool or broader scanner changes.
   This creates migration and maintenance work beyond this milestone and is not
   recommended for this blocker.

The standard option requires focused failing tests for the two reproduced
restrictions, positive preservation tests, negative validation tests, and the
tool's existing suite. Then test a disposable copy of this map: move described
components through the patched CLI, compare semantic IDs/meaning/ownership and
all relationships/flows, run two scans, and reproduce from a clean checkout.
Do not migrate the release map until those controls pass. Pinning details must
identify the upstream commit, patch and executable digest rather than silently
replacing the installed 0.7.2 package.

Impact: no domain/application/port/persistence/runtime change. The tool fix adds
an explicit human declaration and preservation-safe curation capability;
README tooling instructions, a tooling ADR and reproducibility evidence would
be required before using it for delivery. Authored Groma Markdown remains
CLI-only. TASK-233's CI/test-strategy design still needs its own approval.

Explicit human approval is required before implementing this tooling change,
under AGENTS.md's architecture decision and approval rule. No upstream message,
issue, PR, package publication, tool source modification or release-map
migration was performed during this investigation.

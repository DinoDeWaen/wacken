# Runtime boundaries and Groma containment limits

TASK-227, 20 October 2026. Baseline: `4630970`; evidence closure: `c5f73cb`.
This describes existing architecture, not a proposed deployment change.

**Current status:** resolved with the explicitly approved, repository-pinned
tooling fix described below. Earlier failures are retained as historical evidence.

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

## Approved remedy and verified migration — 21 October 2026

The user approved the standard option: “i approve”.
[ADR0021](../decisions/0021-pinned-groma-container-curation-fix.md) records the
decision and maintenance boundary. The patch and pin live in `tools/groma/`;
use `sh scripts/setup-groma.sh` with Bun1.4.3, then `sh scripts/groma.sh`.
The wrapper leaves the global0.6.6 and npm0.7.2 installations unchanged.

The patch adds `accept <container-id> --declared` for reviewed existing
containers and preservation-safe component moves through existing rename
link rewriting. Component acceptance still requires source evidence; container
moves and combines retain their prior restrictions. No Groma Markdown was
edited directly, no descriptions were cleared and no Java entry point was
invented.

Tooling evidence:

- Base upstream commit: `b8b325fd44f9e78107d271ac794a6e40321dcf53`.
- Patch SHA256: `0ff82c8eec4af88396c169d0f807f7550d06ef230d65602959ebabec1345862d`.
- Fresh-build executable SHA256: `540abf35bc1c2052f24375ead74f9ad892d55a911823e60d4a5dbbace69a2b2d`.
  Build hosts/paths may produce different executable bytes; the source and patch
  are pinned and each local build records/checks its own digest.
- Two new focused regressions failed on unmodified0.7.2 and passed after the
  fix. Tests preserve prose, metadata, groups, IDs, code, concept/file
  relationships and flow endpoints across component moves and rescans; existing
  invalid-kind, parent, collision and unresolved-result checks remain active.
- Full upstream `bun run check`: Node24/24, Bun903 passed,62 optional-runtime
  skips,0 failures; typecheck and lint passed with one existing lint warning.
  Node22 required `NODE_OPTIONS=--experimental-strip-types`. The local SDK lacks
  SwiftParser; tests used the matching prebuilt scanner0.2.2 through the existing
  `GROMA_TEST_SWIFT_PACKAGE` hook. This does not enable Swift in Wacken.
- Full-check command, from the patched upstream checkout, with Bun1.4.3 on PATH:
  `GROMA_TEST_SWIFT_PACKAGE=/private/tmp/wacken-227-swift-test/node_modules/@groma/scanner-swift NODE_OPTIONS=--experimental-strip-types bun run check`.
  The package was installed only in a temporary test directory. Initial SDK and
  sandbox failures were diagnosed rather than represented as passing tests.

The disposable map migration passed before the real one. The release map now
declares `android-application`, `on-device-cache` and `shared-planner-database`
under `wacken`. All111 Java components belong to the Android runtime. The295
unique source references, original component IDs/prose/metadata/groups, all54
prior connections and all3 flows were preserved semantically. A55th connection
from external Supabase to the owned planner database records hosted authorized
data access. Config migration retained the exact Java0.2.2 scanner patterns;
automatic updates are explicitly disabled.

Two scans created0/refreshed111/findings88 and preserved the curated structure.
A clean clone of migration commit `2769bfe` ran two scans and reproduced all129
map/config files byte for byte. Repeat with the pinned wrapper and
`git diff --exit-code -- groma`; use the historical126-file manifest only for
the historical TASK-226 commit.

Browser inspection of the exported working-tree map confirmed all three
containers, the Android111-component hierarchy, owned Room storage explanation,
and the composition root's exact AppRepositories.java link opening its source.
The Groma CLI still requires source endpoints for internal software edges, so
an app-to-source-free storage arrow is not fabricated. README C4 explains that
interaction; SQL/generated coverage gaps remain explicit. Workflow curation
continues in TASK-228–230, rather than claiming all islands are repaired.

The previous APK was deleted and a fresh stable-key signed release built with
`./gradlew assembleRelease --rerun-tasks`. Signature v1/v2 and established signer
SHA256 `ede2ae97735a305900e87bc0fdc021c03382db4e30435f2349a96a3fdcc7545f`
verify. APK SHA256:
`d4eaab3aede97b5dde294b212ffa227c7b04ead1c6c5ad6757d6e079026c56bb`.
This intermediate APK remains2.34/code42; milestone2.35 is not published.

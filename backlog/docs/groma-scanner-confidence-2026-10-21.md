# Groma scanner confidence — 21 October 2026

TASK-232 is an analysis spike against source commit `49d056b`. The supported workflow remains pinned Groma 0.6.6 with Java scanner 0.2.2. The separately installed local Groma 0.7.2 was already checked for TASK-227; this spike neither upgrades tooling nor changes scanner configuration, CI, runtime boundaries or test strategy.

## Observed confidence

| Case | Reproduction and result | What the result establishes |
| --- | --- | --- |
| Ordinary source call | Disposable `Caller.call -> Target.value` with both files in one worker input produces a resolved invocation with a source target. Real JDK 21 `javac --release 17` compiles it. | Ordinary calls can be source evidence. They do not automatically create authored architectural relationships. |
| Callback | `Target::callback` in the same fixture produces `JAVA_METHOD_REFERENCES`: one deferred reference. No invocation targets that callback body. `BandImageLoader` executor `submit` (line 39) and delivery `execute` (line 60) have unresolved targets in the project probe. | Registration/reference is not proof of invocation, ordering, cancellation or lifecycle safety. Read callback/dispatcher code and tests. |
| Cross-module | Isolated app fixture imports the absent `lib.Target`: package/symbol diagnostics and unresolved `value`. Combined fixture compiles/resolves. Actual `SupabaseCanonicalBandLookup.idFor` calls to domain `Band.name()` are unresolved in the separate app input. | Separately analyzed module dependencies are not a usable source implementation target. Real Gradle resolves module dependencies; missing scanner edges cannot prove dead code. |
| Room/generated | Project probe reports missing `androidx.room`, `RoomDatabase` and unresolved `Room.databaseBuilder` at WackenDatabase line 285. No `_Impl` source occurs among scanner files. Real Gradle generates RoomBandDao_Impl and WackenDatabase_Impl under ignored `app/build/generated/ap_generated_sources/debug/out`. | DAO annotations and generated implementations need framework and runtime evidence. This scanner performs no annotation processing or Room runtime dispatch inference. |

The real build control passed immediately before this documentation-only spike: application 170, domain 63, BDD 3; Android JVM 264 (248 passed, 16 existing disposable-API skips), coverage gates and Android lint. Full native API 36 regression passed 71/71 without skips on the disposable AVD. These results validate selected compiled/runtime behavior, not a complete call graph, API 23, production RLS or the final milestone release.

## Public CLI reproduction

Run from the repository using the pinned global executable. Do not accidentally substitute the local 0.7.2 binary.

```sh
groma --version
groma scanner list
groma scanner check
groma scan
groma lint --count
git diff --exit-code -- groma
groma view app/src/main/AndroidManifest.xml
groma view app/src/main/res/values/strings.xml
groma view backend/flyway/sql/V014__independent_band_and_festival_after_ratings.sql
groma view scripts/verify-android-smoke.sh
```

Observed: CLI 0.6.6; java found @groma/scanner-java@0.2.2; readiness passed. Scan exits 0, created 0/refreshed 111/matched 0/findings 88; architecture files remain unchanged. Lint prints 88 and exits 1 because candidates remain, separate from Android lint.

The scan prints:
- `JAVA_MISSING_EXTERNAL_TYPES ×1`: 4,212 unresolved symbol/package references, explicitly saying project dependencies/generated sources are not loaded.
- `JAVA_UNRESOLVED_CALLS ×4` with representative message “2622 calls have no supported source implementation target”.
- `JAVA_METHOD_REFERENCES ×4` with representative message “105 deferred method references”.
- Five invalid-method-reference warnings, 225 override warnings, 36 incompatible-type warnings and one unexpected-lambda warning.

The ×4 lines group multiple diagnostics and display a representative message; **2,622 and 105 are not proven whole-repository totals**. Do not sum, compare or gate them as totals without retaining each observation. Diagnostics change with selected files/compiler inputs and source changes. No zero-unresolved claim is made.

All four non-Java `view` commands report “no enabled scanner reads it” (nonzero expected). Viewing the ignored generated RoomBandDao_Impl path reports “not a repository file”, even after a successful build. Do not create synthetic components or Java marker files to hide these gaps.

## Repeatable disposable probes

The following uses the installed Java worker directly for investigation, not a supported project configuration or proposed CI integration. Its internal protocol can change; use the pinned package. It creates only temporary fixtures/output and never writes the project map. Resolve the package directory from the installed scanner cache; on this machine it was `/Users/dino/.groma/cache/scanners/9de8a657866097e7/node_modules/@groma/scanner-java`.

```sh
python3 - /Users/dino/.groma/cache/scanners/9de8a657866097e7/node_modules/@groma/scanner-java <<'PY'
from pathlib import Path
import json, platform, subprocess, sys, tempfile
scanner = Path(sys.argv[1]).resolve()
host = {"Darwin": "darwin", "Linux": "linux"}[platform.system()]
arch = {"arm64": "arm64", "aarch64": "arm64", "x86_64": "x64"}[platform.machine()]
worker = [str(scanner / f"dist/{host}-{arch}/runtime/bin/java"),
          "-Xmx2048m", "-jar", str(scanner / "dist/worker.jar")]
root = Path(tempfile.mkdtemp(prefix="wacken-232-"))
sources = {
    "lib/Target.java": 'package lib; public class Target { public static String value(){return "ok";} public static void callback(){} }',
    "app/Caller.java": 'package app; import lib.Target; public class Caller { public String call(){return Target.value();} public Runnable callback(){return Target::callback;} public String deferred(){return callback().toString();} }',
    "isolated-app/Caller.java": 'package app; import lib.Target; public class Caller { public String call(){return Target.value();} }'
}
for name, source in sources.items():
    path = root / name
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(source + "\n")
for name, base, files in [
    ("combined", root, ["lib/Target.java", "app/Caller.java"]),
    ("isolated", root / "isolated-app", ["Caller.java"])
]:
    result = subprocess.run(worker + [str(base), "17", "UTF-8"],
        input="\n".join(files) + "\n", text=True, capture_output=True, check=True)
    (root / (name + ".json")).write_text(result.stdout)
    observation = json.loads(result.stdout)
    print(name, observation["invocations"], observation["diagnostics"])
jdk = subprocess.check_output(["/usr/libexec/java_home", "-v", "21"], text=True).strip()
subprocess.run([jdk + "/bin/javac", "--release", "17", "-d", str(root / "classes"),
               str(root / "lib/Target.java"), str(root / "app/Caller.java")], check=True)
# Separate module inputs reproduce missing external/generated source limitations.
repo = Path.cwd()
files = subprocess.check_output(
    ["git", "ls-files", "--cached", "--others", "--exclude-standard"], text=True).splitlines()
args, inputs = [], []
for module in ["domain", "application", "infrastructure", "app"]:
    selected = sorted(path[len(module)+1:] for path in files
        if path.startswith(module + "/src/main/java/") and path.endswith(".java")
        and not Path(path).name.startswith("InMemory"))
    args += [str(repo / module), "17", "UTF-8"]
    inputs.append("\n".join(selected) + "\n")
result = subprocess.run(worker + args, input="\n".join(inputs),
                        text=True, capture_output=True, check=True)
(root / "project.jsonl").write_text(result.stdout)
print("Evidence directory:", root)
PY
```

Actual probes used `/private/tmp/wacken-232-fixtures` and `/private/tmp/wacken-232-project-17.jsonl`. Scanner engine identifies itself as javac-tree 25.0.4.1+1-LTS, distinct from the real project build JDK 21 / Java 17 target. An initial release-21 exploratory probe was repeated at the declared release 17. Project probe file counts were domain 48/application 77/infrastructure 15/app 155. This explicit probe excludes every InMemory-prefixed file and supplies separate Java-17 inputs; it is **not** a claim of byte-identical public CLI settings or diagnostic totals.

## Supported controls and limitations

Installed package primary evidence: scanner package.json and src/index.js, especially readJavaInput, scanJavaInputs, summarizeMissingTypes and exported scanner object.

- `groma/scanners.json` pins package and include/exclude patterns. Git visibility, main-source roots and per-scanner patterns control which files can be listed. Excluding generated `build/` and in-memory repositories is intentional; removing exclusions solely to reduce warnings is not a correctness fix.
- The adapter reads supported Maven/Gradle declarations, conventional main Java roots, recognized source-root declarations, Java version and encoding. A recognized declared source root can improve source selection; it cannot supply Android binaries or run annotation processors.
- Readiness checks the packaged compiler runtime and readable project inputs; it does not establish a successful Android/Gradle build.
- The worker invocation passes project root, release, encoding and selected source filenames. The scanner entry points accept `_settings` but ignore it; the installed public CLI has no classpath option and this adapter exposes no supported dependency-classpath/annotation-processing setting.
- Changing JAVA_HOME to the build JDK does not replace the scanner's bundled compiler. Running Gradle first supplies useful independent build/generated evidence; it does not make ignored generated code or dependency JARs load into the scanner.
- Co-locating source inputs improved the disposable ordinary-call fixture, but flattening project modules or changing source sets is not a justified production fix.
- Import, field type and deferred method reference evidence require interpretation. Existing map relationships are authored knowledge; no automatic completeness promise follows from scan success. A source owner with no arrow may represent a legitimate DTO/value grouping.

## Coverage gaps and evidence required

| Boundary | Evidence required before curating a relationship | Independent validation |
| --- | --- | --- |
| Supabase RPC/transaction | Java endpoint, exact SQL function/public wrapper, auth identity and transaction/carry-over/promotion code. Example: SupabaseFestivalArchiveGateway POST /rest/v1/rpc/archive_festival plus V016 public/private archive functions. | Disposable PostgreSQL/PostgREST lifecycle and unauthorized/rollback/replay tests; no production permissions claim. |
| RLS/auth | SQL policies, grants, SECURITY DEFINER/search_path and auth.uid checks, plus adapter session/member binding. | Real role/session negative controls. Java method similarity or HTTP success alone does not establish authorization. |
| Android Room | DAO/entity/schema, composition databaseBuilder, migrations and generated implementations inspected after build. | Native persistence/migration/rollback tests; generated method presence alone is insufficient. |
| Manifest/resources | Manifest Activity declarations, Intent producers/consumers, resource IDs/layout/localization/theme use. | Native launch/navigation/recreation/missing-resource regressions. Scanner cannot certify manifest routing or appearance. |
| Shell/Python/build/deployment | Script input/exit behavior, Gradle wiring, workflow job/artifact/signature checks and supported environment. | Execute the appropriate disposable verification gates and signed upgrade; Java scan does not inspect these workflows. |

Do not invent source ownership for unread languages or fabricate endpoints to connect islands. Record coverage gaps, exact evidence paths and remaining uncertainty. Container curation is still constrained by TASK-227; this spike does not erase authored meaning to bypass it.

## Options and recommendation

| Option | Compatibility / cost | Result and approval |
| --- | --- | --- |
| Minimal — recommended now | Keep pinned tools; run scan/readiness, retain diagnostics, inspect exact source evidence and curate through CLI. Existing Gradle/native/backend gates remain separate. Low maintenance, works with current limitations. | Documentation-only workflow clarification here. No significant implementation or approval needed. Curated completeness still awaits TASK-227–230. |
| Standard — future approved gate | Pin tool/runtime and add reviewed map/source-ownership/relationship/flow baseline checks with explicit exceptions and a failing negative fixture. Moderate CI/baseline maintenance. | TASK-233 requires explicit architecture/test-strategy approval before coding. It would expose drift, not resolve Android dependencies or infer every runtime edge. Not implemented here. |
| Maximum — future tooling investigation | Evaluate a classpath/generated-aware scanner or approved scanner enhancement plus Android/SQL/resource/script coverage. Reproduce this fixture matrix and build controls before accepting it. High compatibility and upgrade/maintenance cost; no validated candidate claimed. | Tool/dependency/test-strategy changes need separate explicit approval and ADR assessment. No scanner installation, upgrade or framework introduced. |

The minimal option is sufficient to prevent treating a missing edge as proof of disconnected or unused code. It does not repair all architectural knowledge or authorize the standard/maximum implementation.

# Release 2.34 lifecycle and upgrade validation

## Validated source

Code/test commit: `10914a0c52222274a516e0af6a9fae0c53b804b2`.
[Complete CI run39061674547](https://github.com/DinoDeWaen/wacken/actions/runs/39061674547)
passed JVM/QA/coverage, full Android lint, runner safety6, immutable-image fallback,
real Summer Breeze creation16 with no skips, rating SQL/PostgREST/RLS, and the
full68 native tests on each Android API23/API36.

The continuous authenticated API scenario in `rating_state_api.py` proves:

1. Established band4 seeds a new festival's planning4.
2. Planning changes to5 and after-rating3 without promoting the band opinion.
3. Creation replay preserves the edits; archive retains planning5/after3 and
   establishes band3 with the correct source.
4. The next festival starts planning3.
5. A current-source correction updates the established opinion and audit while
   preserving the already seeded later planning.
6. A later archive becomes the source; correcting the older festival cannot
   replace it. Reset/missing values retain the established opinion, and another
   member retains their own value/source.

Existing SQL/API tests additionally exercise rollback, RLS, stale reviewed
revisions, concurrent edit/archive and duplicate/uncertain-response replay.

## Signed upgrade evidence

Baseline: published stable-key `2.33.5`, versionCode41, Room9.

- Baseline APK SHA256:
  `a94c393f45b9f617e99cec2fbf6f0a6d33c2d91dde2122381872465761549f3a`.
- Exact-source intermediate candidate APK SHA256:
  `bc0525c2279b0d73f2e332587c7f4487fe01a47155f8896a772eed9e46382dcf`.
- Both APKs and the probe certificate SHA256:
  `ede2ae97735a305900e87bc0fdc021c03382db4e30435f2349a96a3fdcc7545f`.
- Candidate package `be.wacken.planner`, minSdk23; signed v1/v2.
- Candidate remains intermediate `2.33.5`/code41. TASK214 must repeat the gate
  with final `2.34` and a higher code before publication.

Executed without uninstall/wipe on an isolated API36 emulator:
AVD `Medium_Phone_API_36.0`, data directory
`/private/tmp/wacken-upgrade-220`, fresh data image `exact-userdata.img`.
The original/user emulator data was not used. Reports:
`/private/tmp/wacken-upgrade-220-exact-evidence`.

```text
seed: upgrade_result=PASS; INSTRUMENTATION_CODE: -1
before update: FAIL; AssertionError: Candidate migrated to Room12
signed update: adb install -r; Success
verify: upgrade_result=PASS; INSTRUMENTATION_CODE: -1
PASS: signed upgrade retained auth, metadata, festival identity, history,
pending work and safe established provenance. versionCode41 ->41.
```

The probe seeds actual old schema data and verifies auth/access/refresh fields,
expiry, group, canonical biography/music links, active/archive identities and
lineups, original dated/pending/unknown-date history, per-member planning, legacy
pending values, pending schedule decision and an app-private file. Room9→12
establishes only acknowledged dated archived history; active/unknown events stay
unpromoted and recovered after-values require review. It then launches the normal
signed app offline and verifies cached lineup and retained pending work.

Reproduction and signing instructions:
[release process](release-process.md#reproducible-signed-upgrade-probe-task-220).
The opt-in probe is only in a test APK. Normal native CI keeps AndroidJUnitRunner,
with no ignored/skipped probe counted as a pass.

## Build and limits

Clean JDK21 validation and stable-signed build passed:
`./gradlew :app:clean test qaTest :app:lintDebug :app:lintVitalRelease :app:assembleRelease :app:assembleDebugAndroidTest -PwackenUpgradeProbe`,
using existing protected signing credentials. No production schema/API/policy was
changed by TASK220. FlywayV014–V018 deployment and final publication remain214.

A first cold emulator was not ready; an initial probe manifest entry was
overridden by AGP, so registration now uses an explicit test-only Gradle property.
The old-version negative control and full fresh run passed after that repair.
CI also caught the runner's accidental ripgrep dependency; POSIX grep removed it,
and Linux safety checks pass. Cold emulator/SystemUI delays are not treated as
product success. No required database/native gate was waived.

Offline upgrade proof uses a fixture session, not a real account. Normal
authenticated sync, owner restrictions and fault/replay behavior are validated by
the disposable real PostgreSQL/PostgREST gates. Production deployment is separate
from those tests. Final higher-code upgrade is still mandatory in TASK214.

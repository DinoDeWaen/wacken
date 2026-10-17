# Release 2.34 publication validation

## Published artifact and exact source

- [GitHub release](https://github.com/DinoDeWaen/wacken/releases/tag/v2.34)
- [Signed APK](https://github.com/DinoDeWaen/wacken/releases/download/v2.34/app-release.apk)
- [Checksum](https://github.com/DinoDeWaen/wacken/releases/download/v2.34/SHA256SUMS)
- Tag `v2.34` and release target:
  `5b27bd2853d24e75e0d3c006974e49214bc4c474`.
- Package `be.wacken.planner`, versionName `2.34`, versionCode42,
  minSdk23/targetSdk36; v1/v2 signatures verified.
- APK SHA256:
  `e03c6e956af13fd6155537d84e74e6368ae902bc2da90263f28652448b112c4a`.
- Stable certificate SHA256:
  `ede2ae97735a305900e87bc0fdc021c03382db4e30435f2349a96a3fdcc7545f`.

The APK was built from a clean existing task-owned worktree at detached5b27bd2:
`/Users/dino/.codex/worktrees/fix-festival-creation/wacken`.
Git status was clean before/after the signed build. Unrelated edits in the primary
workspace were preserved. AGP reports no embedded Git metadata for this managed
worktree; explicit git revision/clean-tree evidence binds source externally.

The published APK/checksum were downloaded to
`/private/tmp/wacken-2.34-published`. `shasum -a256 -c SHA256SUMS` passed;
downloaded signature/certificate/package/version matched the local validated
artifact. Remote tag resolution and release target both match5b27bd2.

## Gates

[Final-source CI39063985464](https://github.com/DinoDeWaen/wacken/actions/runs/39063985464)
and its duplicate39063981652 are green: full JVM/QA/coverage, full lint,
runner/mirror safety, real Summer Breeze creation16/no skips, rating-state
SQL/PostgREST/RLS and the continuous4→4→5/3→3→3 lifecycle, plus68 native
regressions on each API23/API36 with no skipped/failed native tests.

Clean JDK21 signed build, with the existing SDK/key configured:
`./gradlew :app:clean test qaTest :app:lintDebug :app:lintVitalRelease :app:assembleRelease :app:assembleDebugAndroidTest -PwackenUpgradeProbe`
passed. The separate probe is a test APK, never a release asset.

## Final higher-code upgrade

An empty `release-2.34-userdata.img` under
`/private/tmp/wacken-upgrade-220` isolated the release test from prior/user data.
The actual published stable-key2.33.5/code41/Room9 APK was installed and seeded.

```text
seed: PASS
old-version negative control: FAIL (expected Room12 assertion)
adb install -r final signed candidate: Success
candidate verify: PASS
PASS: signed upgrade retained auth, metadata, festival identity, history,
pending work and safe established provenance. versionCode41 ->42.
```

Report directory: `/private/tmp/wacken-214-upgrade-evidence`.
No uninstall/wipe or same-version option was used. The probe verifies auth,
canonical metadata/links, active/archive identities/lineups, dated/pending/unknown
history, own/other planning, legacy pending values, pending schedule work, a
private file and normal offline MainActivity startup. Active/ambiguous history
does not establish an opinion; recovered values require review.

## Server and scope

[Production rollout evidence](release-2.34-deployment.md) records successful
V014–V018 before publication,18 valid migrations, unchanged existing data
counts/hashes and expected RLS/RPC grants. No production fixture import/reset
occurred. [Lifecycle evidence](release-2.34-lifecycle-validation.md) retains the
broader integrated scenario and intermediate migration proof.

All2.34 feature/test/distribution dependencies are complete. TASK224 tracks
pre-existing advisor findings separately, requiring approval before changes.
Native Ironline/Field Notes/System appearance implementation remains3.0.

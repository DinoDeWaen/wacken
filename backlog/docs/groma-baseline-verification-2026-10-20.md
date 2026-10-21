# Groma baseline verification — 20 October 2026

TASK-226 preserves the map already reviewed in the [Groma audit](groma-audit-2026-10-20.md).
Baseline source commit: `4630970` on `codex/release-2-35`. No architecture
Markdown or scanner configuration was rewritten. The 126 files include the
project/index/configuration, one system, 111 components, two actors, six external
systems and three flows. Generated exports and unrelated local changes are excluded.

## Reproduction

Prerequisites: Groma CLI **0.6.6**, configured and installed
**@groma/scanner-java@0.2.2**, Java runtime, Git and `shasum`.
Use a disposable checkout of the baseline commit, without copying local map files:

```bash
git worktree add --detach /private/tmp/wacken-groma-baseline-check 4630970
cd /private/tmp/wacken-groma-baseline-check
groma --version
groma scanner list
shasum -a 256 -c backlog/docs/groma-baseline-2026-10-20.sha256
groma scan
shasum -a 256 -c backlog/docs/groma-baseline-2026-10-20.sha256
groma scan
shasum -a 256 -c backlog/docs/groma-baseline-2026-10-20.sha256
git status --porcelain
```

All 126 checksums must pass and final status must be empty. The manifest protects
every stored byte, including IDs, ownership, groups, descriptions, relationship
tables, flows and scanner configuration. Also check that scans created no files:
checksum verification by itself detects missing/changed baseline files but does
not reject additional files. Later curation intentionally supersedes this
historical manifest; compare it at its source commit rather than automatically
regenerating hashes to accept changes.

## Results

- Working checkout: two consecutive scans each reported `created 0, refreshed 109,
  matched 0, findings 99`; all 126 hashes and the exact file set stayed unchanged.
- Fresh detached worktree `/private/tmp/wacken-235-baseline` at `4630970`: verified
  every baseline hash before scanning and after each of two scans, and compared
  the complete file set. Both scans reported the same counts above. Final
  `git status --porcelain` was empty. Scan logs were recorded outside the repo at
  `/private/tmp/wacken-235-clean-scan-1.log` and `wacken-235-clean-scan-2.log`.
- Negative control: copied the map into a temporary directory, verified it
  successfully, removed only the disposable `groma/flows/rate-a-festival-band.md`,
  and reran `shasum -a 256 -c` against the original manifest. It exited **1** and
  identified the missing flow. The real map was untouched.
- `git diff --cached --check` passed before the baseline commit.

These are exact-content characterization checks, not a new CI policy. No runtime,
domain, API, persistence, dependency or testing-strategy change is introduced.
No architecture approval or ADR is needed for preserving existing documentation.

## Limits and follow-up

Preserved defects remain visible: zero containers, 72 disconnected records
(including two empty records), and 99 Groma lint candidate groups. These are
scheduled under TASK-227–233; TASK-226 does not claim to repair them. Stable
checksums demonstrate preservation, not semantic correctness or completeness.

The scanner covers main Java only and excludes `InMemory*Repository.java`.
SQL/RLS/RPC, manifest/resources, Gradle and verification scripts require separate
evidence. The scan still reports 4,260 unresolved symbol/package references,
2,683 unsupported calls and 111 deferred method references, plus framework/type
diagnostics. Those are scanner limits; they are not application compiler results.

Application unit, native and database suites are unchanged and are not rerun for
this documentation-only preservation task. Full release validation remains
TASK-241. The required per-ticket signed APK build is recorded in the task notes;
it does not constitute publication or a version bump to 2.35.

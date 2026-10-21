# Release2.35 integrated validation

TASK241. Source under test: `935ca2f63fec7c24e6f0ca413e84124b280c8885`
on `codex/release-2-35`. Draft [PR5](https://github.com/DinoDeWaen/wacken/pull/5).
[CI run39608937492](https://github.com/DinoDeWaen/wacken/actions/runs/39608937492)
tests that source on Linux; local checks use the same application/map sources.
CI tested merge candidate `70dbece4795ee5d39c94b37f294f668f790252f2` over base
`84cafb6aff1dc19fd001b29884e6717fd21823f9`. Documentation-only validation
commits introduce no application/map changes relative to the tested source.

**Integrated behavior/architecture checks pass.** All four GitHub jobs are green,
including71 native tests on each of API23 and API36 with no failures or skips.
The signed Room9-to-current retained-data regression also passes. Final task
artifact build/signature evidence follows below; publication and latest-stable
2.34-to-final2.35 upgrade remain TASK242 responsibilities.

## Audit finding → repair → evidence

| Audit finding | Repair/disposition | Verification |
| --- | --- | --- |
| F1 untracked map | Fixed by TASK226; current curated map and pin are versioned. | Clean-checkout gate loads tracked roles/ownership/meaning, scans twice and preserves semantics. Historical baseline evidence remains linked from README. |
| F2 missing runtime/data containers | Fixed by TASK227 with approved pinned-tool patch, ADR0021. Android runtime, owned on-device cache and shared planner database are distinct. | Exact pin/patch/reader compatibility plus valid system/container/component hierarchy; prior rendered source/container review and unchanged semantic baseline. SQL coverage is explicitly separate. |
| F3 active-feature islands | Fixed by TASK228/229/230; responsibility-based combines and source/composition collaborators replace unexplained records. |95 components,294 existing unique owned files, zero component islands; negative island/source/reference fixtures. Two exact historical stale-row exceptions are reported rather than presented as live calls. |
| F4 obsolete source-free records | Fixed by TASK231 via CLI; the actual pending-recovery use case remains. | No source-free component allowed by gate; owned source and role checks pass. |
| F5 absent architecture quality gate | Fixed by TASK233, user-approved standard option and ADR0022. |27 positive/negative fixtures; clean-checkout before/after/two-scan comparison includes inferred rows; exact source/task/flow/pin/diagnostic checks. Linux CI gate passes. |
| F6 unbounded rating/schedule waits | Fixed by TASK234, approved adapter transport ADR0020. Error precedence fixed in TASK236. | SupabaseHttpTransportTest, SupabaseClientTransportContractTest, SupabaseErrorMessageTest and existing pending/sync tests; real local-server stalled-header/body timeout case, configured connect/read bounds, auth retry and member/account protection. No overall deadline claim. |
| F7 image resource/lifecycle risk | Fixed by TASK235. | BandImageLoaderTest covers success/fallback, closed stream/connection, replacement/stale result, destroy/cancel and active decode teardown. Existing native detail cases retain UI behavior. |
| F8 active/archive presentation drift | Shared read-only detail presentation in TASK239 and selected helpers in TASK240. Active/archive actions stay separate. Larger table/theme work intentionally stays in approved3.0 TASK207/209/210/211. | Detail/festival scope application tests, BandDetailActivityRegressionTest and native archive/correction/unknown-link cases; all99 historical helper dispositions explain retained visual/domain differences. |
| F9 repeated identity/scoped projections | Fixed selected responsibility reuse in TASK237/238/240. | Canonical transport contract tests cover case/escaping, absent/malformed IDs, unknown joins and account changes. MemberRatingProjectionCharacterizationTest and MemberRatingSummaryCharacterizationTest cover own/festival/group scopes and summaries. |
| F10 shallow lifecycle flows | Fixed by TASK228/229/230 with source-supported collaborators and preservation/failure semantics. |13 resolvable flows: creation, reviewed local import/deletion, selected planning/after-state/reset sync, archive, reviewed correction, pending recovery, separate exports, verified festival/one-band music evidence and retained schedule flow. |

## Duplicate and scanner disposition

All99 original groups/comparisons remain accounted for in the
[helper dispositions](groma-helper-dispositions-2026-10-21.md), with fixed,
partially consolidated, intentionally separate and approved3.0-owned work kept
distinct. The current88 groups each have full source/member/token identities and
an individual rationale in the [gate review](groma-gate-review-2026-10-21.md) and
[accepted baseline](../../tools/groma/architecture-baseline.json). Neither total
finding count nor lint exit1 is blanket-ignored.

The [scanner confidence report](groma-scanner-confidence-2026-10-21.md) retains the
Java-only scope and unresolved framework/generated/cross-module limitations.
Scanner warnings are not application compile errors. Gate evidence contains
294 observed sources,2285 operations,1436 tokenized operations and11617 invocations;
it rejects missing owned observations, decreased operation evidence, changed
pins and new/increased diagnostic categories or unresolved facts.

Exactly two stale authored relationships to/from the deleted full CSV importer
are user-accepted TASK228 exceptions. No flow or owned Code reference uses the
deleted source. The gate reports them explicitly and accepts no other dangling
endpoint. There are no island exceptions. This preserves the user-approved
closure decision rather than claiming the old arrows are correct.

## Commands and results

| Check | Result |
| --- | --- |
| `bun test scripts/tests/groma-gate.test.ts` |27 passed,0 failed; positive/negative semantic fixtures and failure cleanup. |
| `bun scripts/architecture/verify-groma.mjs` | Pass locally and Linux CI;95 components,294 owned sources,13 flows,88 reviewed duplicate identities; two scans create0/refreshed95. |
| Existing JVM/BDD/Android-unit + coverage/lint | Pass at source935ca2f; domain63/application163/infrastructure24/BDD3; Android264 with248 passed and16 existing environment-gated API skips. Dedicated real database gate below exercises those integration cases. |
| `JAVA_HOME=<JDK21> sh scripts/verify-festival-creation.sh` | Pass locally with no skipped creation tests; disposable PostgreSQL/PostgREST. |
| `JAVA_HOME=<JDK21> bash scripts/verify-rating-state.sh` | Pass locally: migration/reset/replay/RLS/rollback, archive/current-source correction/audit, deletion/preservation and reviewed alias creation; HTTP concurrent replay/owner checks; integrated band4 → planning4 → planning5/after3 → archive band3 → next planning3. |
| Linux CI build/coverage/lint/runner safety/SQL/API | Pass in CI run39608937492; four jobs green. |
| API23/API36 native suite | Pass in CI: API23 job119252378743 and API36 job119252378993 each finish71 tests,0 failed,0 skipped. The older persisted local API36 image initially stopped in smoke preflight because phone service was absent; fresh isolated emulator has the service and the required equivalent API36 gate passed in CI. No runner weakening or masked application failure. |
| Signed retained-data upgrade probe | Pass on fresh API36 data: published2.33.5/code41/Room9 → current candidate2.34/code42/Room12, same stable key. Seed passes, old-schema negative control rejects before update, install-r candidate and verification pass; auth/cache/metadata/festival/history/pending/schedule/provenance and normal offline startup retained. Existing signed probe source is unchanged from v2.34. Final latest-stable2.34 → higher-code2.35 distribution upgrade is TASK242. |
| Final task APK | Fresh build/signature evidence is added to task notes after the documentation checkpoint; no publication claimed. |

## Review and coverage limits

No product redesign, schema, authorization or production configuration change is
introduced by this validation task. ADR0016 contracts remain the business
baseline; ADR0020/0021/0022 carry the prior explicit approvals. ArchUnit remains
an assessed complementary option, not a newly added dependency.

Disposable SQL/API tests validate fixture authorization behavior, not a fresh
production permission audit. Separate TASK224 remains out of this milestone
validation claim. Main Java scanner evidence cannot establish SQL/RLS semantics,
resource/manifest wiring or runtime reachability by itself. Human source and
rendered-map review remains required and the user accepted the curated map.

Draft PR remains unmerged. Version bump, final signed higher-code upgrade,
release metadata and publication belong to TASK242 after this package passes.


## Retained upgrade evidence

The isolated runner report records baseline/candidate/probe versions, signatures,
SHA256 values, install logs, seed, rejected old-schema control and passing verify.
The signed test APK is the existing unchanged opt-in probe; normal native tests
keep AndroidJUnitRunner. Probe fixture credentials are synthetic/offline and no
real user installation or production account was touched.

The old persisted emulator preflight issue did not pass as an application test.
Required native coverage comes from successful Linux API23/API36 runs; the fresh
local emulator also completed the signed upgrade regression without changing the
smoke runner. The final release must still prove latest-published stable retained
state, including existing correction audit, against the exact2.35 APK in TASK242.

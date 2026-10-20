# Groma architecture audit — 20 October 2026

Task: TASK-225. Repository: Wacken Planner. Audited working-tree base: `a2b5532cc395012c8d86d5360dc9060b4d00f127` (`codex/release-2-34`). This is an investigation report, not an implementation or release validation.

## Summary

**The disconnected architecture is a confirmed map-maintenance problem. A rescan does not repair it.** There are 111 stored components: 39 described/grouped/connected components and 72 undescribed, ungrouped components with zero incoming or outgoing map relationships. Seventy disconnected records own existing source and have references in other main Java files; two have no Code references. Source inspection confirms important disconnected features are actually wired into the app.

Two consecutive scans created no components and left the entire Groma directory byte-for-byte unchanged from the pre-scan snapshot. This run did not delete relationships or recreate previously combined files. With the map entirely untracked, its earlier evolution cannot be reconstructed from Git; the precise historical event that first introduced each island is unknown.

Groma lint reports **99 candidate groups, containing 199 candidate comparisons**. These are review questions, not 99 confirmed defects. The most useful code finding is inconsistent HTTP timeout handling across otherwise duplicated clients. Other worthwhile work concerns active/archive UI drift, image-loading resource management, repeated identity lookup and scoped rating projections. Constructors, typed persistence mappings and separate planning/after-rating policies account for many harmless similarities.

## Scope and method

- Read Groma inspect, structure, relationships and task-link guides; reviewed repository governance and current requirements.
- Copied the pre-existing untracked map to `/private/tmp/wacken-groma-before-20261020` before scanning.
- Ran `groma scan` twice, `groma lint --max-count 200`, scanner inventory, element/source views and a static export. Reviewed the rendered browser map and the disconnected `ViewBandRatingContextUseCase` details.
- Parsed stored ownership and relationship tables independently; resolved exact file endpoints, checked duplicate ownership, missing source paths, description/group coverage and disconnected records.
- Cross-checked main-source references for all 70 source-owning islands. These are lexical witnesses, not a complete runtime call graph. Concrete calls and composition were inspected for creation/import, rating context, archive/correction, sync and verified music links.
- Reviewed lint candidates by responsibility and source bodies; Appendix B gives a disposition for every group. Appendix C preserves the full scanner output, including every candidate location.
- Application source, database, production configuration and curated map were not changed. No APK was built and no application unit/instrumentation suite or Android lint was run. Here “lint” means **Groma lint**, as requested.

## Measured results

| Check | Result |
| --- | --- |
| Groma CLI | 0.6.6 |
| Configured scanner | `@groma/scanner-java@0.2.2`, found |
| Internal software systems | 1: `wacken` |
| External systems / actors / flows | 6 / 2 / 3 |
| Containers | **0** |
| Stored components | 111; 109 own source, 2 own no source |
| Owned source files | 289; no duplicate owners or missing owned paths |
| Main Java files | 295; six `InMemory*Repository.java` files intentionally excluded |
| Owned files by module | app 150; application 76; domain 48; infrastructure 15 |
| Described and grouped components | 39 |
| Undescribed, ungrouped and disconnected components | **72 (64.9%)** |
| Stored relationships | 42 authored; **0 derived**, 0 draft |
| Unresolved stored relationship endpoints | 0 |
| Duplicate candidates | 99 groups / 199 comparisons; lint exit code 1 |
| Scan repeatability | Both runs: created 0, refreshed 109, matched 0, findings 99 |
| Map before versus after scans | No content differences |
| Version control | No files under `groma/` tracked by Git |

The 42 relationships are authored claims. A scan preserves them; preservation does not establish that they remain semantically correct. Groups are presentation groupings, not containers. “No broken endpoint” is therefore much weaker than “complete architecture”.

## Must fix: architecture evidence and delivery controls

### F1 — P1: the curated map is absent from Git

Evidence: `git status --short` shows `?? groma/`; `git ls-files groma` returns no paths. Existing local descriptions, combinations, actors, flows and scanner configuration consequently do not travel with a normal clean checkout. There is no tracked baseline to prove when an element became disconnected or to review architecture changes in a PR.

**Recommendation:** review and commit the intended Groma baseline, then make map updates part of each relevant task/PR. Do not blindly stage all untracked content: this audit preserved the existing user-owned directory. Validate a fresh worktree/clone reproduces the approved map before calling this fixed.

### F2 — P1: the map has no actual container boundaries

All 111 components have `parent: wacken` directly; no `C4 Container` record exists. The browser shows the same direct system-to-component hierarchy. “Android screens”, “Application workflows”, “Domain model”, “Storage and synchronization” and “Metadata enrichment” are groups, and do not restore the C4 level.

**Recommendation:** model the installed Android application as its runtime boundary; show Room as an owned on-device storage boundary when source ownership and Groma capabilities support a truthful representation. Decide explicitly how the owned Supabase database relates to the external Supabase platform. Java Gradle modules should not be declared separate runtime containers just because their folders differ. This is proposed documentation curation, not a change to the deployed application architecture.

CLI constraint: moving already described components is restricted by current Groma commands. Preserve authored meaning and report any refused operation; do not strip descriptions or hand-edit Groma Markdown to bypass it.

### F3 — P1: important current features are islands

All 72 isolated records are also undescribed and ungrouped. This clean partition is consistent with incomplete curation of scan-created records, rather than widespread removal of authored links during this run. It does not prove when that happened.

Examples with direct source evidence:

| Existing collaboration missing from the map | Source evidence |
| --- | --- |
| Settings and band detail launch verified-link review | `SettingsActivity.java:145`, `BandDetailActivity.java:372` |
| Music-link review constructs lookup use case and verified provider | `MusicLinksReviewActivity.java:56` |
| Import opens identity review and runs creation action | `ImportCsvActivity.java:242,259` |
| Composition supplies creation gateway and Room cache | `AppRepositories.java:222` |
| Composition exposes local CSV review | `AppRepositories.java:296` |
| Composition exposes festival-bound rating context | `AppRepositories.java:317` |
| Band detail constructs after-rating writer | `BandDetailActivity.java:90` |
| Archived detail opens correction dialog | `ArchivedBandDetailActivity.java:192` |

`groma view application/src/main/java/be/wacken/planner/application/ViewBandRatingContextUseCase.java` reports both incoming and outgoing relationships as “none”, despite the constructor call in AppRepositories and usage from band detail. The browser details likewise contain no description or relationships. This is missing architectural knowledge, not evidence of dead code.

**Recommendation:** finish responsibilities, ownership and scenario relationships using exact source endpoints and the Groma CLI. A port, DTO, DAO or exception does not always merit a standalone architecture box. Combine related implementation details where one responsibility justifies it; do not add artificial edges merely to make every box connected. Appendix A identifies all islands.

### F4 — P2: two empty records remain indefinitely

`pendingplanningratingrecovery` and `androidjavacompatibility` have no source, description, group or relationships. Neither scan removed them. No source file of either exact name was found in the available Git history. The evidence does not establish whether they came from an earlier local file, a detach operation or another curation step.

**Recommendation:** review whether they still represent any current responsibility, then remove them through `groma remove` if obsolete and unreferenced. Do not infer that `pendingplanningratingrecovery` is the implemented `RecoverPendingPlanningRatingUseCase`: that is a separate, source-owning record and ID.

### F5 — P1: the quality gate does not check the reported problem

`.github/workflows/ci.yml` contains no Groma scan/lint or map-completeness check. Groma lint currently checks possible duplicated logic; it does not validate container coverage, missing descriptions, isolated responsibilities, source ownership completeness or business-flow coverage. A successful scan can therefore coexist with a very incomplete map.

Task `references` and modified-file links support traceability; they do **not** add architectural relationships. Updating those fields alone cannot connect new components.

**Recommendation:** add a separate architecture validation gate with pinned tool versions, a reviewed baseline and explicit exceptions. Assess differences, not only absolute counts. Reject missing referenced IDs, duplicate owners, missing owned files, unexplained new islands, unintended loss of authored edges and unreviewed source ownership. Verify a deliberately disconnected fixture fails. An approved value-object grouping may legitimately have no independent edge; avoid a blanket rule that forces meaningless arrows.

## Scanner limitations: what these results do not prove

The scan reports 4,260 unresolved symbol/package references and 2,683 unsupported call targets. It says project dependencies/generated sources are not loaded, scans main sources only, and does not infer annotation processing or framework runtime dispatch. It also reports 111 deferred method references. The diagnostics include 3 invalid method references, 224 override warnings, 36 incompatible-type warnings and 1 unexpected lambda warning.

These diagnostics are from Groma analysis, **not a Gradle/Android compiler failure in this audit**. Android classes, Room-generated implementations, cross-module types, lambdas and callback wiring are specifically affected. Their presence reduces confidence in inferred connectivity; absence of an edge cannot prove absence of a collaboration.

The installed guide says derivation selects certain named-callback and certain HTTP-endpoint evidence; ordinary calls and unresolved wiring still require interpretation. This map contains zero derived edges. Merely scanning again is not a complete relationship-recovery strategy.

Coverage is Java-only. SQL/Flyway migrations, RPC transaction rules, RLS, Android manifest navigation, resources, shell/Python verification gates and Gradle deployment behaviour are not established by these Java components. The map must identify these gaps and curate external/runtime interactions from source evidence. This audit did not inspect production permissions or claim a backend security review.

## Should fix: code risks exposed by lint

### F6 — P2: duplicated HTTP send paths have diverged

`SupabaseMasterDataClient.send` at line 352 sets a 15,000 ms connection timeout and 30,000 ms read timeout. The sibling `send` methods in `SupabaseFestivalPlanningRatingClient:144`, `SupabasePersonalBandRatingClient:146`, `SupabaseRatingClient:136` and `SupabaseScheduleLockClient:106` set neither. This is a concrete policy difference, even though no stalled-request failure was reproduced here.

Impact: those operations lack an explicit bounded wait at these call sites. On a stalled connection, background sync can wait far longer than the master-data path. Central authentication/retry is already in `SupabaseAuthenticatedRequest`; that class does not configure these HttpURLConnection objects.

Recommendation: characterize current headers/body/upsert/auth-retry behaviour, define bounded transport behaviour, and reuse transport mechanics inside the adapter boundary. Preserve member binding, pending-write retention and operation-specific response handling. Changing a shared transport boundary may require the repository architecture-approval gate; do not silently implement it as deduplication.

### F7 — P2: duplicated image loading lacks explicit resource/lifecycle controls

`ArchivedBandDetailActivity:299` and `BandDetailActivity:390` use identical raw threads with `BitmapFactory.decodeStream(new URL(url).openStream())`. These methods do not explicitly close the input stream, set timeouts, cancel on screen destruction or guard against a stale view target.

Recommendation: characterize success/error behaviour, then give image loading one lifecycle-aware owner with bounded networking, stream closure and stale-result protection. No new image framework is assumed or approved by this report.

### F8 — P2: active and archived presentation can drift

The two detail screens duplicate substantial rendering, link panels, history and image code; active/archive tables also share row/action construction. The business distinction is deliberate: archive is read-only by default, with explicit correction, while active detail has normal editing. Generic unification must not reopen archive editing or read another festival by fallback.

Recommendation: share visual primitives or immutable detail presentation under the planned 3.0 design tasks, keep workflow-specific actions separate, and retain active/archive/festival-ID tests. Small dp/TextView/constructor duplicates do not justify their own large refactor.

### F9 — P2: identity and scoped-rating projections repeat

Exact-name band-ID lookups occur in three remote clients; ID/name mapping also repeats. Per-person positive-rating sorting/projection occurs in schedule, list, detail and export use cases. These are drift risks around previously sensitive band identity and festival scope, not proof of a current wrong-rating defect.

Recommendation: consider one adapter-owned canonical lookup and a small application-owned scoped projection. Preserve exact name/case semantics, member/festival boundaries, legacy export contracts and the approved separation of established, planning and after-ratings. Do not merge the domain ports or let an adapter choose rating policy to reduce the lint count.

### F10 — P2: current flows are too shallow to explain 2.34

The three stored flows cover planning rating, a two-step CSV import, and schedule generation. They omit reviewed group-wide festival creation, established-rating seed, after-rating sync, archive promotion, source-aware correction and verified music-link evidence review. The import flow remains a limited scenario and cannot be treated as coverage of all current CSV/create behaviour.

Recommendation: add those scenarios after their actual component relationships are curated. Each step must use an existing directed relationship. Include failure/offline and preservation outcomes in prose where useful; do not claim a parent aggregate edge establishes the runtime sequence.

## Nice to have / intentional repetition

- Reuse small presentation helpers during the 3.0 visual work: density conversion, stars, table cells and panels.
- Consider private/provider-adapter helpers for nested optional JSON strings and UTF-8 reading.
- Retain constructor injection and defensive list copies; do not introduce a shared superclass for unrelated DTOs/use cases.
- Retain typed Room/TSV adapters and separate lifecycle ports. PENDING/SYNCED, current/after, own/group and active/archive differences are business-relevant.
- Preserve short domain invariants locally unless a domain-owned helper makes actual maintenance easier. Never add a dependency from domain to app/application merely to silence duplication.

## Missing checks and proposed verification

| Area | Useful regression check |
| --- | --- |
| Map durability | Clean worktree reproduces tracked IDs, ownership, groups, authored descriptions/relationships and flows |
| Rescan stability | Two scans are idempotent; combining/moving files does not recreate old boxes |
| Map completeness | New unexplained island/missing container or missing important scenario fails with actionable diagnostics |
| Scanner limitations | Unresolved-call diagnostics are captured; degraded analysis is visible rather than reported as a complete graph |
| Transport | Stalled connect/read are bounded; errors retain pending writes; auth refresh, member binding, headers and upsert remain correct |
| Image loading | Stream closes on success/failure; timeout/cancellation and destroyed/rebound view do not apply stale results |
| UI sharing | Active edits stay festival-bound; archive remains read-only until explicit correction; unknown links remain disabled/empty |
| Projection reuse | Canonical identity, positive-rating filter, stable member sorting and archive festival scope survive |

These are proposed additions. This audit did not inventory every existing behavioural test or claim the application currently lacks all coverage in these areas. The concrete missing gate verified here is architecture-map validation in CI.

## Recommended work order

1. **P1 — establish and review the tracked Groma baseline.** Preserve the user's current curation and document Java-only coverage.
2. **P1 — repair map structure and meaning.** Settle truthful runtime/data boundaries, curate the 70 source-owning islands, review the two empty records, and author the missing lifecycle/link-review scenarios through the CLI. Obtain approval if resolving this requires any change to actual application architecture.
3. **P1 — prevent recurrence.** Pin versions and add the reviewed architecture gate with negative controls, idempotence and fresh-checkout verification. Test strategy/CI changes require the normal approval assessment.
4. **P2 — address bounded transport and image resource behaviour through separate TDD/characterization tasks.** Review any shared-boundary/dependency change before implementation.
5. **P2/P3 — consolidate justified presentation/query mechanics with 3.0 work.** Keep the approved lifecycle and data architecture intact.

No remediation task is marked implemented by this report. TASK-225 tracks the investigation; recommendations are proposed work packages, not promises of a corrected map.

## Positive observations

- All 289 owned files exist and have exactly one component owner.
- Every stored relationship endpoint resolved.
- Existing groups, multifile owners and authored relationships survived both scans unchanged.
- Domain/application and adapter source remain separate modules; several apparent duplicates correctly preserve those boundaries.
- Existing described actors, external services and purpose are useful; preserve their meaning while completing the map.

## Delivery and impact notes

- README impact: none, because this investigation adds an audit report without changing application setup, commands or implemented behaviour. Recommended map/runbook updates remain future work.
- Business requirements impact: none, because no business rule, scope or workflow was changed; current requirements were used to assess map completeness.
- Diagram impact: none, because the existing curated map was inspected and exported but remained byte-for-byte unchanged.
- ADR impact: none, because this investigation makes no architecture decision or application change; proposed boundary/transport work requires its own assessment.
- TDD/BDD/approval-test impact: no product behaviour was changed. Validation consists of repeat scans, full lint review, source/ownership checks and browser inspection, not new passing product tests.
- Other existing workspace changes and the untracked Groma directory were preserved. Nothing was deployed or published.

## Appendix A — complete disconnected-component inventory

“Witness” is the first other main Java file containing the owned type name, with its first matching line. It may be an import or type declaration; it establishes source usage evidence, not a runtime invocation by itself. Detailed representative call sites are listed in F3. All rows have zero stored incoming/outgoing edges, no description and no group.

| Component ID | Owned source | First source witness | Other main files referencing owned type |
| --- | --- | --- | ---: |
| `activefestivalafterratingrepository` | [ActiveFestivalAfterRatingRepository.java](../../application/src/main/java/be/wacken/planner/application/ActiveFestivalAfterRatingRepository.java) | [AppRepositories.java:13](../../app/src/main/java/be/wacken/planner/AppRepositories.java#L13) | 1 |
| `addfestivalaction` | [AddFestivalAction.java](../../app/src/main/java/be/wacken/planner/AddFestivalAction.java) | [ImportCsvActivity.java:259](../../app/src/main/java/be/wacken/planner/ImportCsvActivity.java#L259) | 1 |
| `androidjavacompatibility` | **No Code references** | None | 0 |
| `archivedratingcorrectiondialog` | [ArchivedRatingCorrectionDialog.java](../../app/src/main/java/be/wacken/planner/ArchivedRatingCorrectionDialog.java) | [ArchivedBandDetailActivity.java:45](../../app/src/main/java/be/wacken/planner/ArchivedBandDetailActivity.java#L45) | 1 |
| `archivedratingcorrectiongateway` | [ArchivedRatingCorrectionGateway.java](../../application/src/main/java/be/wacken/planner/application/ArchivedRatingCorrectionGateway.java) | [SupabaseArchivedRatingCorrectionGateway.java:3](../../app/src/main/java/be/wacken/planner/SupabaseArchivedRatingCorrectionGateway.java#L3) | 2 |
| `archivedratingcorrectionusecase` | [ArchivedRatingCorrectionUseCase.java](../../application/src/main/java/be/wacken/planner/application/ArchivedRatingCorrectionUseCase.java) | [ArchivedRatingCorrectionDialog.java:7](../../app/src/main/java/be/wacken/planner/ArchivedRatingCorrectionDialog.java#L7) | 3 |
| `archivedratingkind` | [ArchivedRatingKind.java](../../domain/src/main/java/be/wacken/planner/domain/ArchivedRatingKind.java) | [ArchivedRatingCorrectionDialog.java:19](../../app/src/main/java/be/wacken/planner/ArchivedRatingCorrectionDialog.java#L19) | 9 |
| `archivedratingreview` | [ArchivedRatingReview.java](../../domain/src/main/java/be/wacken/planner/domain/ArchivedRatingReview.java) | [ArchivedRatingCorrectionDialog.java:21](../../app/src/main/java/be/wacken/planner/ArchivedRatingCorrectionDialog.java#L21) | 5 |
| `archivefestivalaction` | [ArchiveFestivalAction.java](../../app/src/main/java/be/wacken/planner/ArchiveFestivalAction.java) | [MainActivity.java:371](../../app/src/main/java/be/wacken/planner/MainActivity.java#L371) | 1 |
| `bandratingcontext` | [BandRatingContext.java](../../application/src/main/java/be/wacken/planner/application/BandRatingContext.java) | [ViewBandRatingContextUseCase.java:19](../../application/src/main/java/be/wacken/planner/application/ViewBandRatingContextUseCase.java#L19) | 1 |
| `canonicalband` | [CanonicalBand.java](../../domain/src/main/java/be/wacken/planner/domain/CanonicalBand.java) | [SupabaseFestivalCreationGateway.java:55](../../app/src/main/java/be/wacken/planner/SupabaseFestivalCreationGateway.java#L55) | 7 |
| `confirmedbandalias` | [ConfirmedBandAlias.java](../../domain/src/main/java/be/wacken/planner/domain/ConfirmedBandAlias.java) | [SupabaseFestivalCreationGateway.java:108](../../app/src/main/java/be/wacken/planner/SupabaseFestivalCreationGateway.java#L108) | 5 |
| `deletefestivaldialog` | [DeleteFestivalDialog.java](../../app/src/main/java/be/wacken/planner/DeleteFestivalDialog.java) | [MainActivity.java:397](../../app/src/main/java/be/wacken/planner/MainActivity.java#L397) | 2 |
| `deletefestivalusecase` | [DeleteFestivalUseCase.java](../../application/src/main/java/be/wacken/planner/application/DeleteFestivalUseCase.java) | [AppRepositories.java:14](../../app/src/main/java/be/wacken/planner/AppRepositories.java#L14) | 2 |
| `exportratingcorrectionscsvusecase` | [ExportRatingCorrectionsCsvUseCase.java](../../application/src/main/java/be/wacken/planner/application/ExportRatingCorrectionsCsvUseCase.java) | [SettingsActivity.java:285](../../app/src/main/java/be/wacken/planner/SettingsActivity.java#L285) | 1 |
| `exportratinglifecyclecsvusecase` | [ExportRatingLifecycleCsvUseCase.java](../../application/src/main/java/be/wacken/planner/application/ExportRatingLifecycleCsvUseCase.java) | [SettingsActivity.java:268](../../app/src/main/java/be/wacken/planner/SettingsActivity.java#L268) | 1 |
| `festivalafterrating` | [FestivalAfterRating.java](../../domain/src/main/java/be/wacken/planner/domain/FestivalAfterRating.java) | [SupabaseFestivalArchiveGateway.java:39](../../app/src/main/java/be/wacken/planner/SupabaseFestivalArchiveGateway.java#L39) | 14 |
| `festivalafterratingrepository` | [FestivalAfterRatingRepository.java](../../domain/src/main/java/be/wacken/planner/domain/FestivalAfterRatingRepository.java) | [RatingStateSyncStore.java:6](../../app/src/main/java/be/wacken/planner/RatingStateSyncStore.java#L6) | 8 |
| `festivalafterratingview` | [FestivalAfterRatingView.java](../../application/src/main/java/be/wacken/planner/application/FestivalAfterRatingView.java) | [ArchivedFestivalActivity.java:59](../../app/src/main/java/be/wacken/planner/ArchivedFestivalActivity.java#L59) | 3 |
| `festivalarchivegateway` | [FestivalArchiveGateway.java](../../application/src/main/java/be/wacken/planner/application/FestivalArchiveGateway.java) | [SupabaseFestivalArchiveGateway.java:3](../../app/src/main/java/be/wacken/planner/SupabaseFestivalArchiveGateway.java#L3) | 2 |
| `festivalbandcatalog` | [FestivalBandCatalog.java](../../application/src/main/java/be/wacken/planner/application/FestivalBandCatalog.java) | [ImportCsvActivity.java:55](../../app/src/main/java/be/wacken/planner/ImportCsvActivity.java#L55) | 3 |
| `festivalbandreviewdialog` | [FestivalBandReviewDialog.java](../../app/src/main/java/be/wacken/planner/FestivalBandReviewDialog.java) | [ImportCsvActivity.java:56](../../app/src/main/java/be/wacken/planner/ImportCsvActivity.java#L56) | 1 |
| `festivalcreationgateway` | [FestivalCreationGateway.java](../../application/src/main/java/be/wacken/planner/application/FestivalCreationGateway.java) | [SupabaseFestivalCreationGateway.java:13](../../app/src/main/java/be/wacken/planner/SupabaseFestivalCreationGateway.java#L13) | 4 |
| `festivalcsvreader` | [FestivalCsvReader.java](../../application/src/main/java/be/wacken/planner/application/FestivalCsvReader.java) | [ImportCsvActivity.java:29](../../app/src/main/java/be/wacken/planner/ImportCsvActivity.java#L29) | 3 |
| `festivalplanningratingview` | [FestivalPlanningRatingView.java](../../application/src/main/java/be/wacken/planner/application/FestivalPlanningRatingView.java) | [BandDetailActivity.java:83](../../app/src/main/java/be/wacken/planner/BandDetailActivity.java#L83) | 1 |
| `festivalscopedperformancerepository` | [FestivalScopedPerformanceRepository.java](../../app/src/main/java/be/wacken/planner/FestivalScopedPerformanceRepository.java) | [AppRepositories.java:259](../../app/src/main/java/be/wacken/planner/AppRepositories.java#L259) | 1 |
| `findbandmusiclinksusecase` | [FindBandMusicLinksUseCase.java](../../application/src/main/java/be/wacken/planner/application/FindBandMusicLinksUseCase.java) | [MusicLinksReviewActivity.java:28](../../app/src/main/java/be/wacken/planner/MusicLinksReviewActivity.java#L28) | 2 |
| `localcsvimportreviewdialog` | [LocalCsvImportReviewDialog.java](../../app/src/main/java/be/wacken/planner/LocalCsvImportReviewDialog.java) | [ImportCsvActivity.java:57](../../app/src/main/java/be/wacken/planner/ImportCsvActivity.java#L57) | 1 |
| `missingfestivalplanningratingexception` | [MissingFestivalPlanningRatingException.java](../../app/src/main/java/be/wacken/planner/MissingFestivalPlanningRatingException.java) | [MissingFestivalRatingDialog.java:15](../../app/src/main/java/be/wacken/planner/MissingFestivalRatingDialog.java#L15) | 2 |
| `missingfestivalratingdialog` | [MissingFestivalRatingDialog.java](../../app/src/main/java/be/wacken/planner/MissingFestivalRatingDialog.java) | [MainActivity.java:474](../../app/src/main/java/be/wacken/planner/MainActivity.java#L474) | 2 |
| `musiclinkevidence` | [MusicLinkEvidence.java](../../application/src/main/java/be/wacken/planner/application/MusicLinkEvidence.java) | [OfficialMusicPageClient.java:6](../../app/src/main/java/be/wacken/planner/OfficialMusicPageClient.java#L6) | 4 |
| `musiclinklookupprovider` | [MusicLinkLookupProvider.java](../../application/src/main/java/be/wacken/planner/application/MusicLinkLookupProvider.java) | [VerifiedMusicLinksProvider.java:12](../../app/src/main/java/be/wacken/planner/VerifiedMusicLinksProvider.java#L12) | 2 |
| `musiclinksreviewactivity` | [MusicLinksReviewActivity.java](../../app/src/main/java/be/wacken/planner/MusicLinksReviewActivity.java) | [BandDetailActivity.java:372](../../app/src/main/java/be/wacken/planner/BandDetailActivity.java#L372) | 2 |
| `officialmusicpageclient` | [OfficialMusicPageClient.java](../../app/src/main/java/be/wacken/planner/OfficialMusicPageClient.java) | [VerifiedMusicLinksProvider.java:16](../../app/src/main/java/be/wacken/planner/VerifiedMusicLinksProvider.java#L16) | 1 |
| `pendingplanningratingrecovery` | **No Code references** | None | 0 |
| `personalbandrating` | [PersonalBandRating.java](../../domain/src/main/java/be/wacken/planner/domain/PersonalBandRating.java) | [SupabaseFestivalArchiveGateway.java:39](../../app/src/main/java/be/wacken/planner/SupabaseFestivalArchiveGateway.java#L39) | 10 |
| `personalbandratingrepository` | [PersonalBandRatingRepository.java](../../domain/src/main/java/be/wacken/planner/domain/PersonalBandRatingRepository.java) | [AppRepositories.java:21](../../app/src/main/java/be/wacken/planner/AppRepositories.java#L21) | 4 |
| `ratingcorrection` | [RatingCorrection.java](../../domain/src/main/java/be/wacken/planner/domain/RatingCorrection.java) | [SupabaseArchivedRatingCorrectionGateway.java:16](../../app/src/main/java/be/wacken/planner/SupabaseArchivedRatingCorrectionGateway.java#L16) | 7 |
| `ratingcorrectionrepository` | [RatingCorrectionRepository.java](../../domain/src/main/java/be/wacken/planner/domain/RatingCorrectionRepository.java) | [AppRepositories.java:334](../../app/src/main/java/be/wacken/planner/AppRepositories.java#L334) | 4 |
| `ratingstatesync` | [RatingStateSync.java](../../app/src/main/java/be/wacken/planner/RatingStateSync.java) | [SupabaseFestivalArchiveGateway.java:13](../../app/src/main/java/be/wacken/planner/SupabaseFestivalArchiveGateway.java#L13) | 4 |
| `ratingstatesyncstore` | [RatingStateSyncStore.java](../../app/src/main/java/be/wacken/planner/RatingStateSyncStore.java) | [RatingStateSync.java:14](../../app/src/main/java/be/wacken/planner/RatingStateSync.java#L14) | 2 |
| `recordfestivalafterratingusecase` | [RecordFestivalAfterRatingUseCase.java](../../application/src/main/java/be/wacken/planner/application/RecordFestivalAfterRatingUseCase.java) | [BandDetailActivity.java:27](../../app/src/main/java/be/wacken/planner/BandDetailActivity.java#L27) | 2 |
| `recoverpendingplanningratingusecase` | [RecoverPendingPlanningRatingUseCase.java](../../application/src/main/java/be/wacken/planner/application/RecoverPendingPlanningRatingUseCase.java) | [FestivalPlanningRatingSyncLocalStore.java:7](../../app/src/main/java/be/wacken/planner/FestivalPlanningRatingSyncLocalStore.java#L7) | 2 |
| `reviewedfestivalband` | [ReviewedFestivalBand.java](../../domain/src/main/java/be/wacken/planner/domain/ReviewedFestivalBand.java) | [SupabaseFestivalCreationGateway.java:50](../../app/src/main/java/be/wacken/planner/SupabaseFestivalCreationGateway.java#L50) | 5 |
| `reviewfestivalbandsusecase` | [ReviewFestivalBandsUseCase.java](../../application/src/main/java/be/wacken/planner/application/ReviewFestivalBandsUseCase.java) | [ImportCsvActivity.java:239](../../app/src/main/java/be/wacken/planner/ImportCsvActivity.java#L239) | 2 |
| `reviewfestivalcsvusecase` | [ReviewFestivalCsvUseCase.java](../../application/src/main/java/be/wacken/planner/application/ReviewFestivalCsvUseCase.java) | [LocalCsvImportReviewDialog.java:14](../../app/src/main/java/be/wacken/planner/LocalCsvImportReviewDialog.java#L14) | 3 |
| `roomarchivedratingcorrectioncache` | [RoomArchivedRatingCorrectionCache.java](../../app/src/main/java/be/wacken/planner/RoomArchivedRatingCorrectionCache.java) | [AppRepositories.java:348](../../app/src/main/java/be/wacken/planner/AppRepositories.java#L348) | 1 |
| `roombandalias` | [RoomBandAlias.java](../../app/src/main/java/be/wacken/planner/persistence/RoomBandAlias.java) | [WackenDatabase.java:29](../../app/src/main/java/be/wacken/planner/persistence/WackenDatabase.java#L29) | 3 |
| `roombandaliasdao` | [RoomBandAliasDao.java](../../app/src/main/java/be/wacken/planner/persistence/RoomBandAliasDao.java) | [WackenDatabase.java:249](../../app/src/main/java/be/wacken/planner/persistence/WackenDatabase.java#L249) | 1 |
| `roombandaliasrepository` | [RoomBandAliasRepository.java](../../app/src/main/java/be/wacken/planner/persistence/RoomBandAliasRepository.java) | [AppRepositories.java:238](../../app/src/main/java/be/wacken/planner/AppRepositories.java#L238) | 2 |
| `roomfestivalafterrating` | [RoomFestivalAfterRating.java](../../app/src/main/java/be/wacken/planner/persistence/RoomFestivalAfterRating.java) | [WackenDatabase.java:26](../../app/src/main/java/be/wacken/planner/persistence/WackenDatabase.java#L26) | 3 |
| `roomfestivalafterratingrepository` | [RoomFestivalAfterRatingRepository.java](../../app/src/main/java/be/wacken/planner/persistence/RoomFestivalAfterRatingRepository.java) | [RoomFestivalArchiveCache.java:19](../../app/src/main/java/be/wacken/planner/RoomFestivalArchiveCache.java#L19) | 3 |
| `roomfestivalarchivecache` | [RoomFestivalArchiveCache.java](../../app/src/main/java/be/wacken/planner/RoomFestivalArchiveCache.java) | [AppRepositories.java:400](../../app/src/main/java/be/wacken/planner/AppRepositories.java#L400) | 1 |
| `roomfestivalcachemarker` | [RoomFestivalCacheMarker.java](../../app/src/main/java/be/wacken/planner/persistence/RoomFestivalCacheMarker.java) | [WackenDatabase.java:22](../../app/src/main/java/be/wacken/planner/persistence/WackenDatabase.java#L22) | 4 |
| `roomfestivalcachemarkerdao` | [RoomFestivalCacheMarkerDao.java](../../app/src/main/java/be/wacken/planner/persistence/RoomFestivalCacheMarkerDao.java) | [WackenDatabase.java:267](../../app/src/main/java/be/wacken/planner/persistence/WackenDatabase.java#L267) | 2 |
| `roomfestivalcreationcache` | [RoomFestivalCreationCache.java](../../app/src/main/java/be/wacken/planner/RoomFestivalCreationCache.java) | [AppRepositories.java:223](../../app/src/main/java/be/wacken/planner/AppRepositories.java#L223) | 1 |
| `roomfestivaldeletion` | [RoomFestivalDeletion.java](../../app/src/main/java/be/wacken/planner/persistence/RoomFestivalDeletion.java) | [AppRepositories.java:44](../../app/src/main/java/be/wacken/planner/AppRepositories.java#L44) | 2 |
| `roompersonalbandrating` | [RoomPersonalBandRating.java](../../app/src/main/java/be/wacken/planner/persistence/RoomPersonalBandRating.java) | [WackenDatabase.java:27](../../app/src/main/java/be/wacken/planner/persistence/WackenDatabase.java#L27) | 4 |
| `roompersonalbandratingrepository` | [RoomPersonalBandRatingRepository.java](../../app/src/main/java/be/wacken/planner/persistence/RoomPersonalBandRatingRepository.java) | [AppRepositories.java:52](../../app/src/main/java/be/wacken/planner/AppRepositories.java#L52) | 1 |
| `roomratingcorrection` | [RoomRatingCorrection.java](../../app/src/main/java/be/wacken/planner/persistence/RoomRatingCorrection.java) | [WackenDatabase.java:28](../../app/src/main/java/be/wacken/planner/persistence/WackenDatabase.java#L28) | 3 |
| `roomratingcorrectiondao` | [RoomRatingCorrectionDao.java](../../app/src/main/java/be/wacken/planner/persistence/RoomRatingCorrectionDao.java) | [WackenDatabase.java:251](../../app/src/main/java/be/wacken/planner/persistence/WackenDatabase.java#L251) | 2 |
| `roomratingcorrectionrepository` | [RoomRatingCorrectionRepository.java](../../app/src/main/java/be/wacken/planner/persistence/RoomRatingCorrectionRepository.java) | [AppRepositories.java:335](../../app/src/main/java/be/wacken/planner/AppRepositories.java#L335) | 2 |
| `roomratingstatedao` | [RoomRatingStateDao.java](../../app/src/main/java/be/wacken/planner/persistence/RoomRatingStateDao.java) | [WackenDatabase.java:253](../../app/src/main/java/be/wacken/planner/persistence/WackenDatabase.java#L253) | 2 |
| `supabasearchivedratingcorrectiongateway` | [SupabaseArchivedRatingCorrectionGateway.java](../../app/src/main/java/be/wacken/planner/SupabaseArchivedRatingCorrectionGateway.java) | [AppRepositories.java:342](../../app/src/main/java/be/wacken/planner/AppRepositories.java#L342) | 2 |
| `supabasefestivalarchivegateway` | [SupabaseFestivalArchiveGateway.java](../../app/src/main/java/be/wacken/planner/SupabaseFestivalArchiveGateway.java) | [RoomFestivalArchiveCache.java:8](../../app/src/main/java/be/wacken/planner/RoomFestivalArchiveCache.java#L8) | 2 |
| `supabasefestivalbandcatalog` | [SupabaseFestivalBandCatalog.java](../../app/src/main/java/be/wacken/planner/SupabaseFestivalBandCatalog.java) | [SupabaseFestivalCreationGateway.java:103](../../app/src/main/java/be/wacken/planner/SupabaseFestivalCreationGateway.java#L103) | 2 |
| `supabasefestivalcreationgateway` | [SupabaseFestivalCreationGateway.java](../../app/src/main/java/be/wacken/planner/SupabaseFestivalCreationGateway.java) | [AppRepositories.java:223](../../app/src/main/java/be/wacken/planner/AppRepositories.java#L223) | 2 |
| `supabasefestivaldeletiongateway` | [SupabaseFestivalDeletionGateway.java](../../app/src/main/java/be/wacken/planner/SupabaseFestivalDeletionGateway.java) | [AppRepositories.java:189](../../app/src/main/java/be/wacken/planner/AppRepositories.java#L189) | 1 |
| `supabaseratingstateclient` | [SupabaseRatingStateClient.java](../../app/src/main/java/be/wacken/planner/SupabaseRatingStateClient.java) | [AppRepositories.java:200](../../app/src/main/java/be/wacken/planner/AppRepositories.java#L200) | 1 |
| `tsvimporttransaction` | [TsvImportTransaction.java](../../infrastructure/src/main/java/be/wacken/planner/infrastructure/TsvImportTransaction.java) | [AppRepositories.java:41](../../app/src/main/java/be/wacken/planner/AppRepositories.java#L41) | 1 |
| `verifiedmusiclinksprovider` | [VerifiedMusicLinksProvider.java](../../app/src/main/java/be/wacken/planner/VerifiedMusicLinksProvider.java) | [MusicLinksReviewActivity.java:56](../../app/src/main/java/be/wacken/planner/MusicLinksReviewActivity.java#L56) | 1 |
| `viewbandratingcontextusecase` | [ViewBandRatingContextUseCase.java](../../application/src/main/java/be/wacken/planner/application/ViewBandRatingContextUseCase.java) | [AppRepositories.java:317](../../app/src/main/java/be/wacken/planner/AppRepositories.java#L317) | 2 |

## Appendix B — disposition of all 99 lint groups

Numbers follow the complete lint output in Appendix C. A group may contain several candidate comparisons; mixed clusters explicitly retain unrelated candidates. Priorities describe recommended follow-up, not proof of a reproduced application failure.

| Group | Representative source/method | Review | Recommendation / reason |
| ---: | --- | --- | --- |
| 1 | [AddFestivalAction.java:19](../../app/src/main/java/be/wacken/planner/AddFestivalAction.java#L19) `execute` | P3 reuse | Same worker/UI/error shell; keep operation-specific success and fallback messages. |
| 2 | [ArchivedBandDetailActivity.java:115](../../app/src/main/java/be/wacken/planner/ArchivedBandDetailActivity.java#L115) `render` | P2 UI drift | Active and archived rendering overlap; share read-only presentation after characterization, retaining edit restrictions. |
| 3 | [ArchivedBandDetailActivity.java:207](../../app/src/main/java/be/wacken/planner/ArchivedBandDetailActivity.java#L207) `ratingSection` | Keep distinct | Planning section includes group ratings; after-rating section has a different value, colour and reset meaning. |
| 4 | [ArchivedBandDetailActivity.java:249](../../app/src/main/java/be/wacken/planner/ArchivedBandDetailActivity.java#L249) `personalRatingHistorySection` | P2 UI drift | Share personal-history presentation; preserve festival/date context and correction state. |
| 5 | [ArchivedBandDetailActivity.java:264](../../app/src/main/java/be/wacken/planner/ArchivedBandDetailActivity.java#L264) `runningOrderSection` | P3 UI reuse | Same running-order presentation responsibility; extract with the detail presentation work. |
| 6 | [ArchivedBandDetailActivity.java:273](../../app/src/main/java/be/wacken/planner/ArchivedBandDetailActivity.java#L273) `linksSection` | P2 UI drift | Centralize link rendering and disabled/empty behaviour while preserving canonical-source lookup. |
| 7 | [ArchivedBandDetailActivity.java:286](../../app/src/main/java/be/wacken/planner/ArchivedBandDetailActivity.java#L286) `imagePanel` | P3 UI reuse | Share image panel presentation with the image loading change. |
| 8 | [ArchivedBandDetailActivity.java:299](../../app/src/main/java/be/wacken/planner/ArchivedBandDetailActivity.java#L299) `loadImage` | P2 resource risk | Identical raw-thread URL.openStream loader; no explicit stream close, timeout or lifecycle cancellation here. |
| 9 | [ArchivedBandDetailActivity.java:310](../../app/src/main/java/be/wacken/planner/ArchivedBandDetailActivity.java#L310) `detailSection` | P3 UI reuse | Detail-section styling belongs with shared presentation. |
| 10 | [ArchivedBandDetailActivity.java:325](../../app/src/main/java/be/wacken/planner/ArchivedBandDetailActivity.java#L325) `sectionTitle` | Mixed / P3 | Label/TextView construction is structurally similar; groupRatingsView and renderRows have different responsibilities. Do not combine the whole cluster. |
| 11 | [ArchivedBandDetailActivity.java:336](../../app/src/main/java/be/wacken/planner/ArchivedBandDetailActivity.java#L336) `paragraphPanel` | P3 UI reuse | Panel styling overlaps; avoid combining Settings semantics with band-detail behaviour. |
| 12 | [ArchivedBandDetailActivity.java:388](../../app/src/main/java/be/wacken/planner/ArchivedBandDetailActivity.java#L388) `valueExtra` | P3 reuse | Identical missing Intent-extra fallback; small UI helper if touched. |
| 13 | [ArchivedBandDetailActivity.java:396](../../app/src/main/java/be/wacken/planner/ArchivedBandDetailActivity.java#L396) `dp` | P3 reuse | Repeated density conversion across twelve screens is trivial boilerplate; low priority. |
| 14 | [ArchivedBandDetailActivity.java:400](../../app/src/main/java/be/wacken/planner/ArchivedBandDetailActivity.java#L400) `narrowScreen` | P3 reuse | Identical narrow-screen predicate; put breakpoint ownership with the shared visual design. |
| 15 | [ArchivedFestivalActivity.java:127](../../app/src/main/java/be/wacken/planner/ArchivedFestivalActivity.java#L127) `headerRow` | P3 UI reuse | Active/archive table headers overlap; share presentation without sharing lifecycle actions. |
| 16 | [ArchivedFestivalActivity.java:138](../../app/src/main/java/be/wacken/planner/ArchivedFestivalActivity.java#L138) `row` | P3 UI reuse | Table-row styling reuse, not business logic. |
| 17 | [ArchivedFestivalActivity.java:151](../../app/src/main/java/be/wacken/planner/ArchivedFestivalActivity.java#L151) `cell` | P3 UI reuse | Cell creation differs in parameter order; extract only the visual primitive. |
| 18 | [ArchivedFestivalActivity.java:165](../../app/src/main/java/be/wacken/planner/ArchivedFestivalActivity.java#L165) `headerCell` | P3 UI reuse | Header cell styling can use the same primitive. |
| 19 | [ArchivedFestivalActivity.java:172](../../app/src/main/java/be/wacken/planner/ArchivedFestivalActivity.java#L172) `bandNameCell` | P3 UI reuse | Band-name cell presentation is shared. |
| 20 | [ArchivedFestivalActivity.java:191](../../app/src/main/java/be/wacken/planner/ArchivedFestivalActivity.java#L191) `rowActions` | P2 UI drift | Action availability and routing must remain active/archive aware even with shared rendering. |
| 21 | [ArchivedFestivalActivity.java:202](../../app/src/main/java/be/wacken/planner/ArchivedFestivalActivity.java#L202) `iconButton` | P3 UI reuse | Identical icon-button construction; consolidate with design implementation. |
| 22 | [ArchivedFestivalActivity.java:226](../../app/src/main/java/be/wacken/planner/ArchivedFestivalActivity.java#L226) `bandsByName` | P3 reuse | Same exact-name map projection; keep identity semantics unchanged. |
| 23 | [ArchivedRatingCorrectionDialog.java:31](../../app/src/main/java/be/wacken/planner/ArchivedRatingCorrectionDialog.java#L31) `ArchivedRatingCorrectionDialog` | Boilerplate | Unrelated constructors assign fields; no common business responsibility. |
| 24 | [BandDetailActivity.java:494](../../app/src/main/java/be/wacken/planner/BandDetailActivity.java#L494) `musicLinksFromIntent` | P3 cleanup | musicLinksFromIntent returns empty links; use MusicLinks.none and consider a clearer method name. |
| 25 | [BandLinkReviewActivity.java:193](../../app/src/main/java/be/wacken/planner/BandLinkReviewActivity.java#L193) `actionButton` | P3 UI reuse | Button styling overlaps but Settings accepts a style parameter; preserve that distinction. |
| 26 | [BandLinkReviewActivity.java:51](../../app/src/main/java/be/wacken/planner/BandLinkReviewActivity.java#L51) `content` | P3 UI reuse | Similar review-screen shell; candidate flows differ. |
| 27 | [BandMetadataReviewActivity.java:43](../../app/src/main/java/be/wacken/planner/BandMetadataReviewActivity.java#L43) `metadataProviders` | Keep distinct | General metadata provider list and evidence-verifying composition have different trust contracts. |
| 28 | [FestivalStartScreenContent.java:16](../../app/src/main/java/be/wacken/planner/FestivalStartScreenContent.java#L16) `FestivalStartScreenContent` | Boilerplate | Screen state and persistence-row constructors are unrelated field initialization. |
| 29 | [LocalCsvImportReviewDialog.java:23](../../app/src/main/java/be/wacken/planner/LocalCsvImportReviewDialog.java#L23) `notifyUi` | P3 reuse | Identical null-safe notification; lifecycle/session ownership still differs. |
| 30 | [MainActivity.java:173](../../app/src/main/java/be/wacken/planner/MainActivity.java#L173) `loadCurrentSession` | P3 reuse | Identical current-session lookup; consider with login navigation only. |
| 31 | [MainActivity.java:178](../../app/src/main/java/be/wacken/planner/MainActivity.java#L178) `redirectToLogin` | P3 reuse | Same login redirection Intent flags; characterize navigation before sharing. |
| 32 | [MainActivity.java:611](../../app/src/main/java/be/wacken/planner/MainActivity.java#L611) `startSyncAnimation` | P3 UI reuse | Similar animation; preserve each screen lifecycle and busy state. |
| 33 | [MusicBrainzMetadataProvider.java:144](../../app/src/main/java/be/wacken/planner/MusicBrainzMetadataProvider.java#L144) `get` | P2 transport drift | HTTP GET code repeats; MusicBrainz throttle, host, headers and provider-specific failures must survive any extraction. |
| 34 | [MusicBrainzMetadataProvider.java:174](../../app/src/main/java/be/wacken/planner/MusicBrainzMetadataProvider.java#L174) `read` | P3 reuse | UTF-8 stream-reading mechanics repeat in eleven clients; consolidate within adapters, not through the domain. |
| 35 | [MusicBrainzMetadataProvider.java:49](../../app/src/main/java/be/wacken/planner/MusicBrainzMetadataProvider.java#L49) `search` | Keep distinct | Provider searches parse different responses and ranking evidence; surface similarity is not one search policy. |
| 36 | [RoomBandRepository.java:46](../../app/src/main/java/be/wacken/planner/persistence/RoomBandRepository.java#L46) `toRow` | Keep distinct | Room row mapping and CSV review comparison tuple serve separate boundaries. |
| 37 | [RoomFestivalAfterRatingRepository.java:60](../../app/src/main/java/be/wacken/planner/persistence/RoomFestivalAfterRatingRepository.java#L60) `domain` | Keep distinct | After-rating, personal history and established rating have different provenance and dates. |
| 38 | [RoomFestivalLineupRepository.java:20](../../app/src/main/java/be/wacken/planner/persistence/RoomFestivalLineupRepository.java#L20) `saveAllForFestival` | Keep distinct | Festival-scoped lineup replacement and global replaceAll differ in deletion scope and transaction handling. |
| 39 | [RoomFestivalLineupRepository.java:28](../../app/src/main/java/be/wacken/planner/persistence/RoomFestivalLineupRepository.java#L28) `findByFestival` | Keep distinct | Similar stream mapping spans festival filters and different rating models; do not erase scope. |
| 40 | [RoomFestivalPlanningRatingRepository.java:27](../../app/src/main/java/be/wacken/planner/persistence/RoomFestivalPlanningRatingRepository.java#L27) `saveSynced` | Keep distinct | saveSynced inserts state; discardPendingRating is an exact compare/delete with stale protection. |
| 41 | [RoomFestivalPlanningRatingRepository.java:31](../../app/src/main/java/be/wacken/planner/persistence/RoomFestivalPlanningRatingRepository.java#L31) `findPending` | Keep distinct | Festival planning and legacy rating repositories return distinct domain types. |
| 42 | [RoomFestivalPlanningRatingRepository.java:74](../../app/src/main/java/be/wacken/planner/persistence/RoomFestivalPlanningRatingRepository.java#L74) `findAll` | Keep distinct | Same query/map shape, distinct tables and domain identities. |
| 43 | [RoomPersonalBandRatingHistoryRepository.java:28](../../app/src/main/java/be/wacken/planner/persistence/RoomPersonalBandRatingHistoryRepository.java#L28) `save` | Keep distinct | PENDING versus SYNCED is material offline behaviour, not a redundant method. |
| 44 | [RoomRatingRepository.java:65](../../app/src/main/java/be/wacken/planner/persistence/RoomRatingRepository.java#L65) `findByUserAndBand` | Keep distinct | Same optional mapping for planning versus real-rating stores; retain separate ports. |
| 45 | [RoomScheduleLockStore.java:48](../../app/src/main/java/be/wacken/planner/persistence/RoomScheduleLockStore.java#L48) `findActiveLocks` | Keep distinct | Active locks exclude delete operations; pending selections use a different DAO predicate. |
| 46 | [RoomStageDistanceRepository.java:33](../../app/src/main/java/be/wacken/planner/persistence/RoomStageDistanceRepository.java#L33) `findBetween` | Keep distinct | Room and TSV adapters implement the same port; sharing the zero-distance rule is optional and minor. |
| 47 | [ScheduleActivity.java:483](../../app/src/main/java/be/wacken/planner/ScheduleActivity.java#L483) `hourGridLine` | P3 UI reuse | Identical View factories; layout geometry remains separate. |
| 48 | [ScheduleActivity.java:489](../../app/src/main/java/be/wacken/planner/ScheduleActivity.java#L489) `hourGridLineLayout` | Keep distinct | Different line width, height and offset represent different calendar grid elements. |
| 49 | [ScheduleActivity.java:882](../../app/src/main/java/be/wacken/planner/ScheduleActivity.java#L882) `stars` | P3 reuse | Identical clamped star formatting; useful small shared presentation function. |
| 50 | [ScheduleCalendarLayout.java:112](../../app/src/main/java/be/wacken/planner/ScheduleCalendarLayout.java#L112) `topOffsetMinutes` | Keep distinct | Typed overloads adapt two projections to the same offset rule; acceptable. |
| 51 | [ScheduleCalendarLayout.java:116](../../app/src/main/java/be/wacken/planner/ScheduleCalendarLayout.java#L116) `durationMinutes` | Keep distinct | Typed overloads share minimum-duration calculation; acceptable until layout changes. |
| 52 | [SettingsActivity.java:265](../../app/src/main/java/be/wacken/planner/SettingsActivity.java#L265) `exportRatingLifecycleCsv` | Keep distinct | Lifecycle v2 and legacy CSV have different contracts; only Android share mechanics may be reused. |
| 53 | [SpotifyMetadataProvider.java:108](../../app/src/main/java/be/wacken/planner/SpotifyMetadataProvider.java#L108) `imageFrom` | False positive | Spotify image array and Wikidata English-label object are unrelated JSON schemas. |
| 54 | [SpotifyMetadataProvider.java:120](../../app/src/main/java/be/wacken/planner/SpotifyMetadataProvider.java#L120) `nestedString` | P3 reuse | Identical optional nested-string accessor within Android provider adapters. |
| 55 | [SpotifyMetadataProvider.java:135](../../app/src/main/java/be/wacken/planner/SpotifyMetadataProvider.java#L135) `normalize` | Keep distinct | Trivial null/trim in adapter versus application value; do not add an outward dependency to remove it. |
| 56 | [SupabaseArchivedRatingCorrectionGateway.java:110](../../app/src/main/java/be/wacken/planner/SupabaseArchivedRatingCorrectionGateway.java#L110) `text` | Keep distinct | Both require identity strings, but failure context belongs to each response contract. |
| 57 | [SupabaseArchivedRatingCorrectionGateway.java:118](../../app/src/main/java/be/wacken/planner/SupabaseArchivedRatingCorrectionGateway.java#L118) `encode` | P3 reuse | Identical UTF-8 URL encoding within adapters. |
| 58 | [SupabaseAuthClient.java:100](../../app/src/main/java/be/wacken/planner/SupabaseAuthClient.java#L100) `requestJson` | Keep distinct | JSONObject versus JSONArray parsing is deliberately different. |
| 59 | [SupabaseAuthClient.java:153](../../app/src/main/java/be/wacken/planner/SupabaseAuthClient.java#L153) `errorMessage` | P2 error drift | Auth prefers msg; authenticated transport prefers message. Define and test precedence before sharing parsing. |
| 60 | [SupabaseBandRepository.java:33](../../app/src/main/java/be/wacken/planner/SupabaseBandRepository.java#L33) `findByName` | Keep distinct | Same repository query shape for different domain aggregates. |
| 61 | [SupabaseFestivalArchiveGateway.java:40](../../app/src/main/java/be/wacken/planner/SupabaseFestivalArchiveGateway.java#L40) `Snapshot` | Boilerplate | List.copyOf defensive copying for unrelated immutable snapshots; retain. |
| 62 | [SupabaseFestivalBandCatalog.java:15](../../app/src/main/java/be/wacken/planner/SupabaseFestivalBandCatalog.java#L15) `SupabaseFestivalBandCatalog` | Boilerplate | Two-field dependency assignment in different adapters; retain. |
| 63 | [SupabaseFestivalPlanningRatingClient.java:112](../../app/src/main/java/be/wacken/planner/SupabaseFestivalPlanningRatingClient.java#L112) `bandIdFor` | P2 identity drift | Repeated exact-name-to-ID lookup across three clients; one adapter-owned resolver could prevent future identity drift. |
| 64 | [SupabaseFestivalPlanningRatingClient.java:125](../../app/src/main/java/be/wacken/planner/SupabaseFestivalPlanningRatingClient.java#L125) `bandNamesById` | Mixed / P2 | Band ID/name lookup is repeated; parseLocks maps conflict keys, not band identities. Keep that candidate separate. |
| 65 | [SupabaseFestivalPlanningRatingClient.java:140](../../app/src/main/java/be/wacken/planner/SupabaseFestivalPlanningRatingClient.java#L140) `request` | P3 wrapper | Thin delegation to shared authenticated request is expected; couple cleanup to transport consolidation. |
| 66 | [SupabaseFestivalPlanningRatingClient.java:144](../../app/src/main/java/be/wacken/planner/SupabaseFestivalPlanningRatingClient.java#L144) `send` | P2 timeout drift | Master-data send sets 15s connect/30s read; four sibling send methods set neither. Concrete inconsistent timeout policy. |
| 67 | [SupabaseFestivalPlanningRatingClient.java:62](../../app/src/main/java/be/wacken/planner/SupabaseFestivalPlanningRatingClient.java#L62) `pullGroupRatings` | Keep distinct | Group planning pull versus own history pull use different row scope and endpoints. |
| 68 | [SupabaseFestivalPlanningRatingClient.java:93](../../app/src/main/java/be/wacken/planner/SupabaseFestivalPlanningRatingClient.java#L93) `parseRatings` | Keep distinct | Planning snapshot versus history-event deserialization carry different identity/date fields. |
| 69 | [SupabaseMasterDataClient.java:129](../../app/src/main/java/be/wacken/planner/SupabaseMasterDataClient.java#L129) `foodOptions` | Keep distinct | Typed master-data endpoint parsing; genericizing all resources adds complexity. |
| 70 | [RoomBandRepository.java:66](../../app/src/main/java/be/wacken/planner/persistence/RoomBandRepository.java#L66) `optional` | Keep distinct | Trivial Optional conversion at separate hexagonal boundaries; no cross-layer utility solely for deduplication. |
| 71 | [SupabaseRatingStateClient.java:36](../../app/src/main/java/be/wacken/planner/SupabaseRatingStateClient.java#L36) `afterRatings` | Keep distinct | After-rating and established rating responses differ in source provenance and review flags. |
| 72 | [SyncingFestivalLineupRepository.java:13](../../app/src/main/java/be/wacken/planner/SyncingFestivalLineupRepository.java#L13) `SyncingFestivalLineupRepository` | Boilerplate | Cache/source dependency constructors repeat the decorator pattern; no useful generic hierarchy required. |
| 73 | [SyncingFestivalRepository.java:27](../../app/src/main/java/be/wacken/planner/SyncingFestivalRepository.java#L27) `save` | Keep distinct | Source-before-cache write-through pattern is intentional; tests should protect failure ordering. |
| 74 | [SyncingPersonalBandRatingHistoryRepository.java:15](../../app/src/main/java/be/wacken/planner/SyncingPersonalBandRatingHistoryRepository.java#L15) `SyncingPersonalBandRatingHistoryRepository` | Boilerplate | Offline history and planning sync constructors wire similar dependencies with different protocols. |
| 75 | [VerifiedMusicLinksProvider.java:113](../../app/src/main/java/be/wacken/planner/VerifiedMusicLinksProvider.java#L113) `entity` | Keep distinct | Verified lookup checks interruption and restores requested identity; generic metadata lookup lacks those semantics. |
| 76 | [VerifiedMusicLinksProvider.java:131](../../app/src/main/java/be/wacken/planner/VerifiedMusicLinksProvider.java#L131) `relationships` | Keep distinct | Typed external relationship filtering versus validated canonical music-profile extraction have different trust meanings. |
| 77 | [WikidataMetadataProvider.java:94](../../app/src/main/java/be/wacken/planner/WikidataMetadataProvider.java#L94) `scoreFor` | P3 reuse | Identical name-match scoring heuristic; share only if provider ranking policy is intentionally unified. It is not verification evidence. |
| 78 | [ActiveFestivalAfterRatingRepository.java:23](../../application/src/main/java/be/wacken/planner/application/ActiveFestivalAfterRatingRepository.java#L23) `findAll` | Keep distinct | Active planning and active after-rating read different repositories; preserve the approved architecture. |
| 79 | [ApplyBandMetadataProposalsUseCase.java:13](../../application/src/main/java/be/wacken/planner/application/ApplyBandMetadataProposalsUseCase.java#L13) `ApplyBandMetadataProposalsUseCase` | Boilerplate | Single repository constructor injection across independent use cases. |
| 80 | [BandDetailItem.java:41](../../application/src/main/java/be/wacken/planner/application/BandDetailItem.java#L41) `personRatingSummary` | P3 reuse | Identical per-person star summary across three projections; small presentation-level extraction possible. |
| 81 | [BandMetadataProviderCandidate.java:14](../../application/src/main/java/be/wacken/planner/application/BandMetadataProviderCandidate.java#L14) `BandMetadataProviderCandidate` | Boilerplate | Metadata candidate and canonical Band normalize optional fields but are different models. |
| 82 | [EffectiveRatingResolver.java:13](../../application/src/main/java/be/wacken/planner/application/EffectiveRatingResolver.java#L13) `EffectiveRatingResolver` | Boilerplate | Single-dependency constructors; no behavioural duplication. |
| 83 | [EnrichBandMetadataFromCatalogUseCase.java:48](../../application/src/main/java/be/wacken/planner/application/EnrichBandMetadataFromCatalogUseCase.java#L48) `firstPresent` | Keep distinct | Existing-value-first policy repeats locally; do not accidentally allow metadata overwrites through a generic merge. |
| 84 | [ExportRatingLifecycleCsvUseCase.java:19](../../application/src/main/java/be/wacken/planner/application/ExportRatingLifecycleCsvUseCase.java#L19) `ExportRatingLifecycleCsvUseCase` | Boilerplate | Fourteen use-case constructor candidates initialize different repositories; no shared use-case superclass. |
| 85 | [ExportRatingsCsvUseCase.java:89](../../application/src/main/java/be/wacken/planner/application/ExportRatingsCsvUseCase.java#L89) `groupRatings` | P2 query drift | Repeated exact-name/positive-rating/user-sort projection; archive query has explicit festival scope and export returns CSV text. Share only a scoped projection. |
| 86 | [FestivalBandCatalog.java:11](../../application/src/main/java/be/wacken/planner/application/FestivalBandCatalog.java#L11) `Snapshot` | Boilerplate | Defensive list copying in unrelated lookup snapshots. |
| 87 | [ImportFestivalCsvResult.java:8](../../application/src/main/java/be/wacken/planner/application/ImportFestivalCsvResult.java#L8) `ImportFestivalCsvResult` | Boilerplate | Immutable result/candidate copying in unrelated features. |
| 88 | [ListActiveFestivalBandsUseCase.java:62](../../application/src/main/java/be/wacken/planner/application/ListActiveFestivalBandsUseCase.java#L62) `performanceFor` | P3 reuse | Identical exact-name performance lookup over supplied lists; preserve caller-selected festival list. |
| 89 | [MusicLinks.java:15](../../application/src/main/java/be/wacken/planner/application/MusicLinks.java#L15) `normalize` | Keep distinct | Tiny optional normalization across application/domain; never make domain depend on MusicLinks. |
| 90 | [RateBandUseCase.java:17](../../application/src/main/java/be/wacken/planner/application/RateBandUseCase.java#L17) `rateBand` | Keep distinct | Planning and real-rating saves deliberately target different ports despite identical orchestration. |
| 91 | [RenameActiveFestivalUseCase.java:14](../../application/src/main/java/be/wacken/planner/application/RenameActiveFestivalUseCase.java#L14) `RenameActiveFestivalUseCase` | Boilerplate | FestivalRepository constructor injection into different lifecycle use cases. |
| 92 | [SearchBandMetadataUseCase.java:128](../../application/src/main/java/be/wacken/planner/application/SearchBandMetadataUseCase.java#L128) `addCandidateProposals` | P3 reuse | Same four-field proposal expansion for Band and provider DTO; shared private projection may reduce future drift. |
| 93 | [Band.java:26](../../domain/src/main/java/be/wacken/planner/domain/Band.java#L26) `requireName` | Keep distinct | Small domain-owned nonblank-text invariants; optional domain helper, no external validation framework needed. |
| 94 | [FileBackedBandRepository.java:20](../../infrastructure/src/main/java/be/wacken/planner/infrastructure/FileBackedBandRepository.java#L20) `save` | Keep distinct | Typed TSV read/replace/write algorithm; retain schema/key semantics and transaction wrapper. |
| 95 | [FileBackedBandRepository.java:32](../../infrastructure/src/main/java/be/wacken/planner/infrastructure/FileBackedBandRepository.java#L32) `writeBands` | Keep distinct | Distinct band/performance/distance TSV column contracts; not interchangeable serializers. |
| 96 | [FileBackedBandRepository.java:54](../../infrastructure/src/main/java/be/wacken/planner/infrastructure/FileBackedBandRepository.java#L54) `loadBandsByName` | Keep distinct | Typed name-keyed file loading; acceptable adapter repetition. |
| 97 | [FileBackedFoodOptionRepository.java:30](../../infrastructure/src/main/java/be/wacken/planner/infrastructure/FileBackedFoodOptionRepository.java#L30) `writeFoodOptions` | Keep distinct | One-column serializers happen to match for food/stage; do not merge domain types. |
| 98 | [FileBackedRatingRepository.java:45](../../infrastructure/src/main/java/be/wacken/planner/infrastructure/FileBackedRatingRepository.java#L45) `loadRatingsByKey` | Keep distinct | Different compound keys and value schemas: user-band ratings versus stage-pair distance. |
| 99 | [SyncedBandRepository.java:29](../../infrastructure/src/main/java/be/wacken/planner/infrastructure/SyncedBandRepository.java#L29) `replaceAll` | Keep distinct | Same source-before-cache replaceAll protocol across independent ports; protect ordering, avoid generic redesign. |

## Appendix C — complete raw Groma lint output

Exit code: **1**. Captured with `groma lint --max-count 200`; all 99 groups fit on this page.

```text
be.wacken.planner.AddFestivalAction#execute(java.util.function.Supplier<AddFestivalResult>,java.util.function.Consumer<AddFestivalResult>,java.util.function.Consumer<java.lang.String>)  app/src/main/java/be/wacken/planner/AddFestivalAction.java:19
  possible duplicates:
    be.wacken.planner.ArchiveFestivalAction#execute(java.util.function.Consumer<FestivalStartState>,java.util.function.Consumer<java.lang.String>)  app/src/main/java/be/wacken/planner/ArchiveFestivalAction.java:23
  not identical
be.wacken.planner.ArchivedBandDetailActivity#render(BandDetailItem)  app/src/main/java/be/wacken/planner/ArchivedBandDetailActivity.java:115
  possible duplicates:
    be.wacken.planner.BandDetailActivity#render(BandDetailItem)  app/src/main/java/be/wacken/planner/BandDetailActivity.java:159
  not identical
be.wacken.planner.ArchivedBandDetailActivity#ratingSection(BandDetailItem)  app/src/main/java/be/wacken/planner/ArchivedBandDetailActivity.java:207
  possible duplicates:
    be.wacken.planner.ArchivedBandDetailActivity#realRatingSection(BandDetailItem)  app/src/main/java/be/wacken/planner/ArchivedBandDetailActivity.java:216
  not identical
be.wacken.planner.ArchivedBandDetailActivity#personalRatingHistorySection(BandDetailItem)  app/src/main/java/be/wacken/planner/ArchivedBandDetailActivity.java:249
  possible duplicates:
    be.wacken.planner.BandDetailActivity#personalRatingHistorySection(BandDetailItem)  app/src/main/java/be/wacken/planner/BandDetailActivity.java:316
  not identical
be.wacken.planner.ArchivedBandDetailActivity#runningOrderSection()  app/src/main/java/be/wacken/planner/ArchivedBandDetailActivity.java:264
  possible duplicates:
    be.wacken.planner.BandDetailActivity#runningOrderSection()  app/src/main/java/be/wacken/planner/BandDetailActivity.java:351
  not identical
be.wacken.planner.ArchivedBandDetailActivity#linksSection(BandDetailItem)  app/src/main/java/be/wacken/planner/ArchivedBandDetailActivity.java:273
  possible duplicates:
    be.wacken.planner.BandDetailActivity#linksSection(BandDetailItem)  app/src/main/java/be/wacken/planner/BandDetailActivity.java:360
  not identical
be.wacken.planner.ArchivedBandDetailActivity#imagePanel(java.lang.String)  app/src/main/java/be/wacken/planner/ArchivedBandDetailActivity.java:286
  possible duplicates:
    be.wacken.planner.BandDetailActivity#imagePanel(java.lang.String)  app/src/main/java/be/wacken/planner/BandDetailActivity.java:377
  not identical
be.wacken.planner.ArchivedBandDetailActivity#loadImage(java.lang.String,ImageView)  app/src/main/java/be/wacken/planner/ArchivedBandDetailActivity.java:299
  possible duplicates:
    be.wacken.planner.BandDetailActivity#loadImage(java.lang.String,ImageView)  app/src/main/java/be/wacken/planner/BandDetailActivity.java:390
be.wacken.planner.ArchivedBandDetailActivity#detailSection()  app/src/main/java/be/wacken/planner/ArchivedBandDetailActivity.java:310
  possible duplicates:
    be.wacken.planner.BandDetailActivity#detailSection()  app/src/main/java/be/wacken/planner/BandDetailActivity.java:401
  not identical
be.wacken.planner.ArchivedBandDetailActivity#sectionTitle(java.lang.String)  app/src/main/java/be/wacken/planner/ArchivedBandDetailActivity.java:325
  possible duplicates:
    be.wacken.planner.ArchivedBandDetailActivity#infoLine(java.lang.String,java.lang.String)  app/src/main/java/be/wacken/planner/ArchivedBandDetailActivity.java:357
    be.wacken.planner.ArchivedBandDetailActivity#groupRatingsView(BandDetailItem)  app/src/main/java/be/wacken/planner/ArchivedBandDetailActivity.java:367
    be.wacken.planner.BandDetailActivity#sectionTitle(java.lang.String,boolean)  app/src/main/java/be/wacken/planner/BandDetailActivity.java:416
    be.wacken.planner.BandDetailActivity#infoLine(java.lang.String,java.lang.String)  app/src/main/java/be/wacken/planner/BandDetailActivity.java:448
    be.wacken.planner.BandDetailActivity#groupRatingsView(BandDetailItem)  app/src/main/java/be/wacken/planner/BandDetailActivity.java:458
    be.wacken.planner.BandLinkReviewActivity#renderRows()  app/src/main/java/be/wacken/planner/BandLinkReviewActivity.java:82
    be.wacken.planner.BandMetadataReviewActivity#taskPanelTitle(java.lang.String)  app/src/main/java/be/wacken/planner/BandMetadataReviewActivity.java:190
    be.wacken.planner.BandMetadataReviewActivity#taskLine(java.lang.String)  app/src/main/java/be/wacken/planner/BandMetadataReviewActivity.java:200
    be.wacken.planner.BandMetadataReviewActivity#messageRow(java.lang.String)  app/src/main/java/be/wacken/planner/BandMetadataReviewActivity.java:218
    be.wacken.planner.LocalCsvImportReviewDialog#label(java.lang.String,int)  app/src/main/java/be/wacken/planner/LocalCsvImportReviewDialog.java:128
    be.wacken.planner.MusicLinksReviewActivity#label(java.lang.String)  app/src/main/java/be/wacken/planner/MusicLinksReviewActivity.java:217
    be.wacken.planner.ScheduleActivity#sectionTitle(java.lang.String)  app/src/main/java/be/wacken/planner/ScheduleActivity.java:347
    be.wacken.planner.ScheduleActivity#stageHeader(java.lang.String)  app/src/main/java/be/wacken/planner/ScheduleActivity.java:442
    be.wacken.planner.ScheduleActivity#message(java.lang.String,int)  app/src/main/java/be/wacken/planner/ScheduleActivity.java:900
    be.wacken.planner.SettingsActivity#infoText(java.lang.String)  app/src/main/java/be/wacken/planner/SettingsActivity.java:210
  not identical
be.wacken.planner.ArchivedBandDetailActivity#paragraphPanel(java.lang.String)  app/src/main/java/be/wacken/planner/ArchivedBandDetailActivity.java:336
  possible duplicates:
    be.wacken.planner.BandDetailActivity#paragraphPanel(java.lang.String)  app/src/main/java/be/wacken/planner/BandDetailActivity.java:427
    be.wacken.planner.SettingsActivity#section(java.lang.String)  app/src/main/java/be/wacken/planner/SettingsActivity.java:188
  not identical
be.wacken.planner.ArchivedBandDetailActivity#valueExtra(java.lang.String)  app/src/main/java/be/wacken/planner/ArchivedBandDetailActivity.java:388
  possible duplicates:
    be.wacken.planner.BandDetailActivity#valueExtra(java.lang.String)  app/src/main/java/be/wacken/planner/BandDetailActivity.java:486
be.wacken.planner.ArchivedBandDetailActivity#dp(int)  app/src/main/java/be/wacken/planner/ArchivedBandDetailActivity.java:396
  possible duplicates:
    be.wacken.planner.ArchivedFestivalActivity#dp(int)  app/src/main/java/be/wacken/planner/ArchivedFestivalActivity.java:234
    be.wacken.planner.BandDetailActivity#dp(int)  app/src/main/java/be/wacken/planner/BandDetailActivity.java:501
    be.wacken.planner.BandLinkReviewActivity#dp(int)  app/src/main/java/be/wacken/planner/BandLinkReviewActivity.java:204
    be.wacken.planner.BandMetadataReviewActivity#dp(int)  app/src/main/java/be/wacken/planner/BandMetadataReviewActivity.java:270
    be.wacken.planner.ImportCsvActivity#dp(int)  app/src/main/java/be/wacken/planner/ImportCsvActivity.java:413
    be.wacken.planner.LoginActivity#dp(int)  app/src/main/java/be/wacken/planner/LoginActivity.java:133
    be.wacken.planner.MainActivity#dp(int)  app/src/main/java/be/wacken/planner/MainActivity.java:746
    be.wacken.planner.MusicLinksReviewActivity#dp(int)  app/src/main/java/be/wacken/planner/MusicLinksReviewActivity.java:225
    be.wacken.planner.ScheduleActivity#dp(int)  app/src/main/java/be/wacken/planner/ScheduleActivity.java:918
    be.wacken.planner.SettingsActivity#dp(int)  app/src/main/java/be/wacken/planner/SettingsActivity.java:444
be.wacken.planner.ArchivedBandDetailActivity#narrowScreen()  app/src/main/java/be/wacken/planner/ArchivedBandDetailActivity.java:400
  possible duplicates:
    be.wacken.planner.BandDetailActivity#narrowScreen()  app/src/main/java/be/wacken/planner/BandDetailActivity.java:505
be.wacken.planner.ArchivedFestivalActivity#headerRow()  app/src/main/java/be/wacken/planner/ArchivedFestivalActivity.java:127
  possible duplicates:
    be.wacken.planner.MainActivity#tableHeader()  app/src/main/java/be/wacken/planner/MainActivity.java:631
  not identical
be.wacken.planner.ArchivedFestivalActivity#row(int,int)  app/src/main/java/be/wacken/planner/ArchivedFestivalActivity.java:138
  possible duplicates:
    be.wacken.planner.MainActivity#tableRow(int,int)  app/src/main/java/be/wacken/planner/MainActivity.java:654
  not identical
be.wacken.planner.ArchivedFestivalActivity#cell(java.lang.String,float,Typeface)  app/src/main/java/be/wacken/planner/ArchivedFestivalActivity.java:151
  possible duplicates:
    be.wacken.planner.MainActivity#cell(java.lang.String,Typeface,float)  app/src/main/java/be/wacken/planner/MainActivity.java:666
  not identical
be.wacken.planner.ArchivedFestivalActivity#headerCell(java.lang.String,float)  app/src/main/java/be/wacken/planner/ArchivedFestivalActivity.java:165
  possible duplicates:
    be.wacken.planner.MainActivity#headerCell(java.lang.String,float)  app/src/main/java/be/wacken/planner/MainActivity.java:642
  not identical
be.wacken.planner.ArchivedFestivalActivity#bandNameCell(TextView)  app/src/main/java/be/wacken/planner/ArchivedFestivalActivity.java:172
  possible duplicates:
    be.wacken.planner.MainActivity#bandNameCell(TextView)  app/src/main/java/be/wacken/planner/MainActivity.java:680
  not identical
be.wacken.planner.ArchivedFestivalActivity#rowActions(java.util.Optional<Band>)  app/src/main/java/be/wacken/planner/ArchivedFestivalActivity.java:191
  possible duplicates:
    be.wacken.planner.MainActivity#rowActions(java.util.Optional<Band>)  app/src/main/java/be/wacken/planner/MainActivity.java:700
  not identical
be.wacken.planner.ArchivedFestivalActivity#iconButton(java.lang.String,java.lang.String,int,java.lang.String,int)  app/src/main/java/be/wacken/planner/ArchivedFestivalActivity.java:202
  possible duplicates:
    be.wacken.planner.MainActivity#iconButton(java.lang.String,java.lang.String,int,java.lang.String,int)  app/src/main/java/be/wacken/planner/MainActivity.java:711
be.wacken.planner.ArchivedFestivalActivity#bandsByName(be.wacken.planner.AppRepositories)  app/src/main/java/be/wacken/planner/ArchivedFestivalActivity.java:226
  possible duplicates:
    be.wacken.planner.MainActivity#bandsByName(be.wacken.planner.AppRepositories)  app/src/main/java/be/wacken/planner/MainActivity.java:302
be.wacken.planner.ArchivedRatingCorrectionDialog#ArchivedRatingCorrectionDialog(Activity,ArchivedRatingCorrectionUseCase,java.lang.String,java.lang.String,Band,java.util.function.ToIntFunction<ArchivedRatingKind>,java.lang.Runnable)  app/src/main/java/be/wacken/planner/ArchivedRatingCorrectionDialog.java:31
  possible duplicates:
    be.wacken.planner.AuthSession#AuthSession(java.lang.String,java.lang.String,java.lang.String,java.lang.String,long,java.lang.String,java.lang.String)  app/src/main/java/be/wacken/planner/AuthSession.java:12
    be.wacken.planner.persistence.RoomFestivalAfterRating#RoomFestivalAfterRating(java.lang.String,java.lang.String,java.lang.String,java.lang.String,int,java.lang.String,java.lang.String,boolean)  app/src/main/java/be/wacken/planner/persistence/RoomFestivalAfterRating.java:17
    be.wacken.planner.persistence.RoomPersonalBandRating#RoomPersonalBandRating(java.lang.String,java.lang.String,int,java.lang.String,java.lang.String,java.lang.String,java.lang.String)  app/src/main/java/be/wacken/planner/persistence/RoomPersonalBandRating.java:15
    be.wacken.planner.persistence.RoomPersonalBandRatingEvent#RoomPersonalBandRatingEvent(java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String,int,java.lang.String,java.lang.String)  app/src/main/java/be/wacken/planner/persistence/RoomPersonalBandRatingEvent.java:24
  not identical
be.wacken.planner.BandDetailActivity#musicLinksFromIntent()  app/src/main/java/be/wacken/planner/BandDetailActivity.java:494
  possible duplicates:
    be.wacken.planner.application.MusicLinks#none()  application/src/main/java/be/wacken/planner/application/MusicLinks.java:11
be.wacken.planner.BandLinkReviewActivity#actionButton(java.lang.String,android.view.View.OnClickListener)  app/src/main/java/be/wacken/planner/BandLinkReviewActivity.java:193
  possible duplicates:
    be.wacken.planner.BandMetadataReviewActivity#actionButton(java.lang.String,android.view.View.OnClickListener)  app/src/main/java/be/wacken/planner/BandMetadataReviewActivity.java:259
    be.wacken.planner.SettingsActivity#actionButton(java.lang.String,be.wacken.planner.WackenTheme.ButtonStyle,android.view.View.OnClickListener)  app/src/main/java/be/wacken/planner/SettingsActivity.java:177
  not identical
be.wacken.planner.BandLinkReviewActivity#content()  app/src/main/java/be/wacken/planner/BandLinkReviewActivity.java:51
  possible duplicates:
    be.wacken.planner.BandMetadataReviewActivity#content()  app/src/main/java/be/wacken/planner/BandMetadataReviewActivity.java:53
  not identical
be.wacken.planner.BandMetadataReviewActivity#metadataProviders()  app/src/main/java/be/wacken/planner/BandMetadataReviewActivity.java:43
  possible duplicates:
    be.wacken.planner.VerifiedMusicLinksProvider#VerifiedMusicLinksProvider()  app/src/main/java/be/wacken/planner/VerifiedMusicLinksProvider.java:19
  not identical
be.wacken.planner.FestivalStartScreenContent#FestivalStartScreenContent(java.lang.String,java.lang.String,java.lang.String,boolean,boolean,boolean)  app/src/main/java/be/wacken/planner/FestivalStartScreenContent.java:16
  possible duplicates:
    be.wacken.planner.persistence.RoomBandAlias#RoomBandAlias(java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String)  app/src/main/java/be/wacken/planner/persistence/RoomBandAlias.java:14
    be.wacken.planner.persistence.RoomFestivalPlanningRating#RoomFestivalPlanningRating(java.lang.String,java.lang.String,java.lang.String,java.lang.String,int,java.lang.String)  app/src/main/java/be/wacken/planner/persistence/RoomFestivalPlanningRating.java:20
    be.wacken.planner.persistence.RoomScheduleLock#RoomScheduleLock(java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String)  app/src/main/java/be/wacken/planner/persistence/RoomScheduleLock.java:21
  not identical
be.wacken.planner.LocalCsvImportReviewDialog.State#notifyUi()  app/src/main/java/be/wacken/planner/LocalCsvImportReviewDialog.java:23
  possible duplicates:
    be.wacken.planner.MusicLinksReviewActivity.Session#notifyUi()  app/src/main/java/be/wacken/planner/MusicLinksReviewActivity.java:45
be.wacken.planner.MainActivity#loadCurrentSession()  app/src/main/java/be/wacken/planner/MainActivity.java:173
  possible duplicates:
    be.wacken.planner.SettingsActivity#loadCurrentSession()  app/src/main/java/be/wacken/planner/SettingsActivity.java:60
be.wacken.planner.MainActivity#redirectToLogin()  app/src/main/java/be/wacken/planner/MainActivity.java:178
  possible duplicates:
    be.wacken.planner.SettingsActivity#redirectToLogin()  app/src/main/java/be/wacken/planner/SettingsActivity.java:65
be.wacken.planner.MainActivity#startSyncAnimation()  app/src/main/java/be/wacken/planner/MainActivity.java:611
  possible duplicates:
    be.wacken.planner.SettingsActivity#startSyncAnimation()  app/src/main/java/be/wacken/planner/SettingsActivity.java:389
  not identical
be.wacken.planner.MusicBrainzMetadataProvider.RateLimitedMusicBrainzHttpClient#get(java.lang.String)  app/src/main/java/be/wacken/planner/MusicBrainzMetadataProvider.java:144
  possible duplicates:
    be.wacken.planner.WikidataMetadataProvider.UrlConnectionWikidataHttpClient#get(java.lang.String)  app/src/main/java/be/wacken/planner/WikidataMetadataProvider.java:177
    be.wacken.planner.WikipediaMetadataProvider.UrlConnectionWikimediaHttpClient#get(java.lang.String,java.lang.String)  app/src/main/java/be/wacken/planner/WikipediaMetadataProvider.java:169
    be.wacken.planner.YouTubeMetadataProvider.UrlConnectionYouTubeHttpClient#get(java.lang.String)  app/src/main/java/be/wacken/planner/YouTubeMetadataProvider.java:110
  not identical
be.wacken.planner.MusicBrainzMetadataProvider.RateLimitedMusicBrainzHttpClient#read(java.io.InputStream)  app/src/main/java/be/wacken/planner/MusicBrainzMetadataProvider.java:174
  possible duplicates:
    be.wacken.planner.SpotifyMetadataProvider.UrlConnectionSpotifyHttpClient#read(java.io.InputStream)  app/src/main/java/be/wacken/planner/SpotifyMetadataProvider.java:182
    be.wacken.planner.SupabaseAuthClient#read(java.io.InputStream)  app/src/main/java/be/wacken/planner/SupabaseAuthClient.java:139
    be.wacken.planner.SupabaseFestivalPlanningRatingClient#read(java.io.InputStream)  app/src/main/java/be/wacken/planner/SupabaseFestivalPlanningRatingClient.java:164
    be.wacken.planner.SupabaseMasterDataClient#read(java.io.InputStream)  app/src/main/java/be/wacken/planner/SupabaseMasterDataClient.java:374
    be.wacken.planner.SupabasePersonalBandRatingClient#read(java.io.InputStream)  app/src/main/java/be/wacken/planner/SupabasePersonalBandRatingClient.java:166
    be.wacken.planner.SupabaseRatingClient#read(java.io.InputStream)  app/src/main/java/be/wacken/planner/SupabaseRatingClient.java:156
    be.wacken.planner.SupabaseScheduleLockClient#read(java.io.InputStream)  app/src/main/java/be/wacken/planner/SupabaseScheduleLockClient.java:126
    be.wacken.planner.WikidataMetadataProvider.UrlConnectionWikidataHttpClient#read(java.io.InputStream)  app/src/main/java/be/wacken/planner/WikidataMetadataProvider.java:193
    be.wacken.planner.WikipediaMetadataProvider.UrlConnectionWikimediaHttpClient#read(java.io.InputStream)  app/src/main/java/be/wacken/planner/WikipediaMetadataProvider.java:185
    be.wacken.planner.YouTubeMetadataProvider.UrlConnectionYouTubeHttpClient#read(java.io.InputStream)  app/src/main/java/be/wacken/planner/YouTubeMetadataProvider.java:126
  not identical
be.wacken.planner.MusicBrainzMetadataProvider#search(java.lang.String)  app/src/main/java/be/wacken/planner/MusicBrainzMetadataProvider.java:49
  possible duplicates:
    be.wacken.planner.WikidataMetadataProvider#search(java.lang.String)  app/src/main/java/be/wacken/planner/WikidataMetadataProvider.java:55
    be.wacken.planner.WikipediaMetadataProvider#search(java.lang.String)  app/src/main/java/be/wacken/planner/WikipediaMetadataProvider.java:52
  not identical
be.wacken.planner.persistence.RoomBandRepository#toRow(Band)  app/src/main/java/be/wacken/planner/persistence/RoomBandRepository.java:46
  possible duplicates:
    be.wacken.planner.application.ReviewFestivalCsvUseCase#bandFields(Band)  application/src/main/java/be/wacken/planner/application/ReviewFestivalCsvUseCase.java:219
  not identical
be.wacken.planner.persistence.RoomFestivalAfterRatingRepository#domain(be.wacken.planner.persistence.RoomFestivalAfterRating)  app/src/main/java/be/wacken/planner/persistence/RoomFestivalAfterRatingRepository.java:60
  possible duplicates:
    be.wacken.planner.persistence.RoomPersonalBandRatingHistoryRepository#toDomain(be.wacken.planner.persistence.RoomPersonalBandRatingEvent)  app/src/main/java/be/wacken/planner/persistence/RoomPersonalBandRatingHistoryRepository.java:139
    be.wacken.planner.persistence.RoomPersonalBandRatingRepository#domain(be.wacken.planner.persistence.RoomPersonalBandRating)  app/src/main/java/be/wacken/planner/persistence/RoomPersonalBandRatingRepository.java:16
  not identical
be.wacken.planner.persistence.RoomFestivalLineupRepository#saveAllForFestival(java.lang.String,java.util.List<FestivalLineupEntry>)  app/src/main/java/be/wacken/planner/persistence/RoomFestivalLineupRepository.java:20
  possible duplicates:
    be.wacken.planner.persistence.RoomFestivalLineupRepository#replaceAll(java.util.List<FestivalLineupEntry>)  app/src/main/java/be/wacken/planner/persistence/RoomFestivalLineupRepository.java:43
    be.wacken.planner.persistence.RoomFoodOptionRepository#replaceAll(java.util.List<FoodOption>)  app/src/main/java/be/wacken/planner/persistence/RoomFoodOptionRepository.java:23
    be.wacken.planner.persistence.RoomStageRepository#replaceAll(java.util.List<Stage>)  app/src/main/java/be/wacken/planner/persistence/RoomStageRepository.java:24
  not identical
be.wacken.planner.persistence.RoomFestivalLineupRepository#findByFestival(java.lang.String)  app/src/main/java/be/wacken/planner/persistence/RoomFestivalLineupRepository.java:28
  possible duplicates:
    be.wacken.planner.persistence.RoomFestivalLineupRepository#findAll()  app/src/main/java/be/wacken/planner/persistence/RoomFestivalLineupRepository.java:36
    be.wacken.planner.persistence.RoomRealRatingRepository#findAll()  app/src/main/java/be/wacken/planner/persistence/RoomRealRatingRepository.java:30
    be.wacken.planner.application.FestivalAfterRatingView#findAll()  application/src/main/java/be/wacken/planner/application/FestivalAfterRatingView.java:15
    be.wacken.planner.application.FestivalPlanningRatingView#findAll()  application/src/main/java/be/wacken/planner/application/FestivalPlanningRatingView.java:26
    be.wacken.planner.infrastructure.FileBackedRatingRepository#findAll()  infrastructure/src/main/java/be/wacken/planner/infrastructure/FileBackedRatingRepository.java:37
  not identical
be.wacken.planner.persistence.RoomFestivalPlanningRatingRepository#saveSynced(SavedFestivalPlanningRating)  app/src/main/java/be/wacken/planner/persistence/RoomFestivalPlanningRatingRepository.java:27
  possible duplicates:
    be.wacken.planner.persistence.RoomFestivalPlanningRatingRepository#discardPendingRating(SavedFestivalPlanningRating)  app/src/main/java/be/wacken/planner/persistence/RoomFestivalPlanningRatingRepository.java:55
  not identical
be.wacken.planner.persistence.RoomFestivalPlanningRatingRepository#findPending(java.lang.String,java.lang.String)  app/src/main/java/be/wacken/planner/persistence/RoomFestivalPlanningRatingRepository.java:31
  possible duplicates:
    be.wacken.planner.persistence.RoomRatingRepository#findPending(java.lang.String,java.lang.String)  app/src/main/java/be/wacken/planner/persistence/RoomRatingRepository.java:57
be.wacken.planner.persistence.RoomFestivalPlanningRatingRepository#findAll()  app/src/main/java/be/wacken/planner/persistence/RoomFestivalPlanningRatingRepository.java:74
  possible duplicates:
    be.wacken.planner.persistence.RoomRatingRepository#findAll()  app/src/main/java/be/wacken/planner/persistence/RoomRatingRepository.java:71
be.wacken.planner.persistence.RoomPersonalBandRatingHistoryRepository#save(PersonalBandRatingEvent)  app/src/main/java/be/wacken/planner/persistence/RoomPersonalBandRatingHistoryRepository.java:28
  possible duplicates:
    be.wacken.planner.persistence.RoomPersonalBandRatingHistoryRepository#saveSynced(PersonalBandRatingEvent)  app/src/main/java/be/wacken/planner/persistence/RoomPersonalBandRatingHistoryRepository.java:42
  not identical
be.wacken.planner.persistence.RoomRatingRepository#findByUserAndBand(java.lang.String,Band)  app/src/main/java/be/wacken/planner/persistence/RoomRatingRepository.java:65
  possible duplicates:
    be.wacken.planner.persistence.RoomRealRatingRepository#findByUserAndBand(java.lang.String,Band)  app/src/main/java/be/wacken/planner/persistence/RoomRealRatingRepository.java:24
be.wacken.planner.persistence.RoomScheduleLockStore#findActiveLocks(java.lang.String)  app/src/main/java/be/wacken/planner/persistence/RoomScheduleLockStore.java:48
  possible duplicates:
    be.wacken.planner.persistence.RoomScheduleLockStore#findPendingSelections(java.lang.String)  app/src/main/java/be/wacken/planner/persistence/RoomScheduleLockStore.java:59
  not identical
be.wacken.planner.persistence.RoomStageDistanceRepository#findBetween(Stage,Stage)  app/src/main/java/be/wacken/planner/persistence/RoomStageDistanceRepository.java:33
  possible duplicates:
    be.wacken.planner.infrastructure.FileBackedStageDistanceRepository#findBetween(Stage,Stage)  infrastructure/src/main/java/be/wacken/planner/infrastructure/FileBackedStageDistanceRepository.java:43
  not identical
be.wacken.planner.ScheduleActivity#hourGridLine()  app/src/main/java/be/wacken/planner/ScheduleActivity.java:483
  possible duplicates:
    be.wacken.planner.ScheduleActivity#halfHourNotch()  app/src/main/java/be/wacken/planner/ScheduleActivity.java:499
be.wacken.planner.ScheduleActivity#hourGridLineLayout(be.wacken.planner.ScheduleCalendarLayout,int)  app/src/main/java/be/wacken/planner/ScheduleActivity.java:489
  possible duplicates:
    be.wacken.planner.ScheduleActivity#halfHourNotchLayout(int)  app/src/main/java/be/wacken/planner/ScheduleActivity.java:505
    be.wacken.planner.ScheduleActivity#halfHourGridLineLayout(be.wacken.planner.ScheduleCalendarLayout,int)  app/src/main/java/be/wacken/planner/ScheduleActivity.java:519
  not identical
be.wacken.planner.ScheduleActivity#stars(int)  app/src/main/java/be/wacken/planner/ScheduleActivity.java:882
  possible duplicates:
    be.wacken.planner.ScheduleBlockContent#stars(int)  app/src/main/java/be/wacken/planner/ScheduleBlockContent.java:61
be.wacken.planner.ScheduleCalendarLayout#topOffsetMinutes(TimelineSlot)  app/src/main/java/be/wacken/planner/ScheduleCalendarLayout.java:112
  possible duplicates:
    be.wacken.planner.ScheduleCalendarLayout#topOffsetMinutes(ScheduleDecisionCandidate)  app/src/main/java/be/wacken/planner/ScheduleCalendarLayout.java:121
be.wacken.planner.ScheduleCalendarLayout#durationMinutes(TimelineSlot)  app/src/main/java/be/wacken/planner/ScheduleCalendarLayout.java:116
  possible duplicates:
    be.wacken.planner.ScheduleCalendarLayout#durationMinutes(ScheduleDecisionCandidate)  app/src/main/java/be/wacken/planner/ScheduleCalendarLayout.java:129
be.wacken.planner.SettingsActivity#exportRatingLifecycleCsv()  app/src/main/java/be/wacken/planner/SettingsActivity.java:265
  possible duplicates:
    be.wacken.planner.SettingsActivity#exportRatingsCsv()  app/src/main/java/be/wacken/planner/SettingsActivity.java:298
  not identical
be.wacken.planner.SpotifyMetadataProvider#imageFrom(JSONObject)  app/src/main/java/be/wacken/planner/SpotifyMetadataProvider.java:108
  possible duplicates:
    be.wacken.planner.WikidataMetadataProvider#label(JSONObject)  app/src/main/java/be/wacken/planner/WikidataMetadataProvider.java:123
  not identical
be.wacken.planner.SpotifyMetadataProvider#nestedString(JSONObject,java.lang.String,java.lang.String)  app/src/main/java/be/wacken/planner/SpotifyMetadataProvider.java:120
  possible duplicates:
    be.wacken.planner.WikipediaMetadataProvider#nestedString(JSONObject,java.lang.String,java.lang.String)  app/src/main/java/be/wacken/planner/WikipediaMetadataProvider.java:142
    be.wacken.planner.YouTubeMetadataProvider#nestedString(JSONObject,java.lang.String,java.lang.String)  app/src/main/java/be/wacken/planner/YouTubeMetadataProvider.java:90
be.wacken.planner.SpotifyMetadataProvider#normalize(java.lang.String)  app/src/main/java/be/wacken/planner/SpotifyMetadataProvider.java:135
  possible duplicates:
    be.wacken.planner.application.MusicLinkEvidence#text(java.lang.String)  application/src/main/java/be/wacken/planner/application/MusicLinkEvidence.java:21
be.wacken.planner.SupabaseArchivedRatingCorrectionGateway#text(JSONObject,java.lang.String)  app/src/main/java/be/wacken/planner/SupabaseArchivedRatingCorrectionGateway.java:110
  possible duplicates:
    be.wacken.planner.SupabaseRatingStateClient#text(JSONObject,java.lang.String)  app/src/main/java/be/wacken/planner/SupabaseRatingStateClient.java:73
  not identical
be.wacken.planner.SupabaseArchivedRatingCorrectionGateway#encode(java.lang.String)  app/src/main/java/be/wacken/planner/SupabaseArchivedRatingCorrectionGateway.java:118
  possible duplicates:
    be.wacken.planner.SupabaseRatingStateClient#encode(java.lang.String)  app/src/main/java/be/wacken/planner/SupabaseRatingStateClient.java:79
be.wacken.planner.SupabaseAuthClient#requestJson(java.lang.String,java.lang.String,java.lang.String,java.lang.String)  app/src/main/java/be/wacken/planner/SupabaseAuthClient.java:100
  possible duplicates:
    be.wacken.planner.SupabaseAuthClient#requestArray(java.lang.String,java.lang.String,java.lang.String)  app/src/main/java/be/wacken/planner/SupabaseAuthClient.java:109
  not identical
be.wacken.planner.SupabaseAuthClient#errorMessage(java.lang.String,int)  app/src/main/java/be/wacken/planner/SupabaseAuthClient.java:153
  possible duplicates:
    be.wacken.planner.SupabaseAuthenticatedRequest#errorMessage(java.lang.String,int)  app/src/main/java/be/wacken/planner/SupabaseAuthenticatedRequest.java:86
  not identical
be.wacken.planner.SupabaseBandRepository#findByName(java.lang.String)  app/src/main/java/be/wacken/planner/SupabaseBandRepository.java:33
  possible duplicates:
    be.wacken.planner.SupabaseStageRepository#findByName(java.lang.String)  app/src/main/java/be/wacken/planner/SupabaseStageRepository.java:25
be.wacken.planner.SupabaseFestivalArchiveGateway.Snapshot#Snapshot(Festival,java.util.List<FestivalAfterRating>,java.util.List<PersonalBandRating>)  app/src/main/java/be/wacken/planner/SupabaseFestivalArchiveGateway.java:40
  possible duplicates:
    be.wacken.planner.application.BandMetadataSearchResult#BandMetadataSearchResult(java.lang.String,java.util.List<be.wacken.planner.application.BandMetadataProposal>,java.util.List<java.lang.String>)  application/src/main/java/be/wacken/planner/application/BandMetadataSearchResult.java:10
be.wacken.planner.SupabaseFestivalBandCatalog#SupabaseFestivalBandCatalog(be.wacken.planner.SupabaseMasterDataClient,java.util.function.Consumer<java.util.List<ConfirmedBandAlias>>)  app/src/main/java/be/wacken/planner/SupabaseFestivalBandCatalog.java:15
  possible duplicates:
    be.wacken.planner.SupabaseFestivalCreationGateway#SupabaseFestivalCreationGateway(be.wacken.planner.SupabaseMasterDataClient,java.util.function.Consumer<be.wacken.planner.SupabaseFestivalCreationGateway.Snapshot>)  app/src/main/java/be/wacken/planner/SupabaseFestivalCreationGateway.java:20
be.wacken.planner.SupabaseFestivalPlanningRatingClient#bandIdFor(Band)  app/src/main/java/be/wacken/planner/SupabaseFestivalPlanningRatingClient.java:112
  possible duplicates:
    be.wacken.planner.SupabasePersonalBandRatingClient#bandIdFor(Band)  app/src/main/java/be/wacken/planner/SupabasePersonalBandRatingClient.java:114
    be.wacken.planner.SupabaseRatingClient#bandIdFor(Band)  app/src/main/java/be/wacken/planner/SupabaseRatingClient.java:99
be.wacken.planner.SupabaseFestivalPlanningRatingClient#bandNamesById()  app/src/main/java/be/wacken/planner/SupabaseFestivalPlanningRatingClient.java:125
  possible duplicates:
    be.wacken.planner.SupabaseMasterDataClient#namesById(java.lang.String)  app/src/main/java/be/wacken/planner/SupabaseMasterDataClient.java:307
    be.wacken.planner.SupabasePersonalBandRatingClient#bandNamesById()  app/src/main/java/be/wacken/planner/SupabasePersonalBandRatingClient.java:127
    be.wacken.planner.SupabaseRatingClient#bandNamesById()  app/src/main/java/be/wacken/planner/SupabaseRatingClient.java:117
    be.wacken.planner.SupabaseScheduleLockClient#parseLocks(java.lang.String)  app/src/main/java/be/wacken/planner/SupabaseScheduleLockClient.java:79
  not identical
be.wacken.planner.SupabaseFestivalPlanningRatingClient#request(java.lang.String,java.lang.String,java.lang.String,boolean)  app/src/main/java/be/wacken/planner/SupabaseFestivalPlanningRatingClient.java:140
  possible duplicates:
    be.wacken.planner.SupabasePersonalBandRatingClient#request(java.lang.String,java.lang.String,java.lang.String,boolean)  app/src/main/java/be/wacken/planner/SupabasePersonalBandRatingClient.java:142
    be.wacken.planner.SupabaseRatingClient#request(java.lang.String,java.lang.String,java.lang.String,boolean)  app/src/main/java/be/wacken/planner/SupabaseRatingClient.java:132
    be.wacken.planner.SupabaseScheduleLockClient#request(java.lang.String,java.lang.String,java.lang.String,boolean)  app/src/main/java/be/wacken/planner/SupabaseScheduleLockClient.java:102
be.wacken.planner.SupabaseFestivalPlanningRatingClient#send(java.lang.String,java.lang.String,java.lang.String,be.wacken.planner.AuthSession,boolean)  app/src/main/java/be/wacken/planner/SupabaseFestivalPlanningRatingClient.java:144
  possible duplicates:
    be.wacken.planner.SupabaseMasterDataClient#send(java.lang.String,java.lang.String,java.lang.String,be.wacken.planner.AuthSession,boolean)  app/src/main/java/be/wacken/planner/SupabaseMasterDataClient.java:352
    be.wacken.planner.SupabasePersonalBandRatingClient#send(java.lang.String,java.lang.String,java.lang.String,be.wacken.planner.AuthSession,boolean)  app/src/main/java/be/wacken/planner/SupabasePersonalBandRatingClient.java:146
    be.wacken.planner.SupabaseRatingClient#send(java.lang.String,java.lang.String,java.lang.String,be.wacken.planner.AuthSession,boolean)  app/src/main/java/be/wacken/planner/SupabaseRatingClient.java:136
    be.wacken.planner.SupabaseScheduleLockClient#send(java.lang.String,java.lang.String,java.lang.String,be.wacken.planner.AuthSession,boolean)  app/src/main/java/be/wacken/planner/SupabaseScheduleLockClient.java:106
  not identical
be.wacken.planner.SupabaseFestivalPlanningRatingClient#pullGroupRatings(be.wacken.planner.AuthSession)  app/src/main/java/be/wacken/planner/SupabaseFestivalPlanningRatingClient.java:62
  possible duplicates:
    be.wacken.planner.SupabasePersonalBandRatingClient#pullUserEvents(be.wacken.planner.AuthSession)  app/src/main/java/be/wacken/planner/SupabasePersonalBandRatingClient.java:69
  not identical
be.wacken.planner.SupabaseFestivalPlanningRatingClient#parseRatings(java.lang.String,java.util.Map<java.lang.String,java.lang.String>)  app/src/main/java/be/wacken/planner/SupabaseFestivalPlanningRatingClient.java:93
  possible duplicates:
    be.wacken.planner.SupabasePersonalBandRatingClient#parseEvents(java.lang.String,java.util.Map<java.lang.String,java.lang.String>)  app/src/main/java/be/wacken/planner/SupabasePersonalBandRatingClient.java:85
  not identical
be.wacken.planner.SupabaseMasterDataClient#foodOptions()  app/src/main/java/be/wacken/planner/SupabaseMasterDataClient.java:129
  possible duplicates:
    be.wacken.planner.SupabaseMasterDataClient#festivals()  app/src/main/java/be/wacken/planner/SupabaseMasterDataClient.java:142
    be.wacken.planner.SupabaseMasterDataClient#bands()  app/src/main/java/be/wacken/planner/SupabaseMasterDataClient.java:54
    be.wacken.planner.SupabaseMasterDataClient#stages()  app/src/main/java/be/wacken/planner/SupabaseMasterDataClient.java:67
  not identical
be.wacken.planner.persistence.RoomBandRepository#optional(java.lang.String)  app/src/main/java/be/wacken/planner/persistence/RoomBandRepository.java:66
  possible duplicates:
    be.wacken.planner.SupabaseMasterDataClient#optional(java.lang.String)  app/src/main/java/be/wacken/planner/SupabaseMasterDataClient.java:393
    be.wacken.planner.SupabasePersonalBandRatingClient#optional(java.lang.String)  app/src/main/java/be/wacken/planner/SupabasePersonalBandRatingClient.java:180
    be.wacken.planner.application.ImportFestivalCsvUseCase#optional(java.lang.String)  application/src/main/java/be/wacken/planner/application/ImportFestivalCsvUseCase.java:216
be.wacken.planner.SupabaseRatingStateClient#afterRatings()  app/src/main/java/be/wacken/planner/SupabaseRatingStateClient.java:36
  possible duplicates:
    be.wacken.planner.SupabaseRatingStateClient#currentRatings()  app/src/main/java/be/wacken/planner/SupabaseRatingStateClient.java:48
  not identical
be.wacken.planner.SyncingFestivalLineupRepository#SyncingFestivalLineupRepository(be.wacken.planner.SyncingFestivalLineupRepository.Cache,be.wacken.planner.SyncingFestivalLineupRepository.Source)  app/src/main/java/be/wacken/planner/SyncingFestivalLineupRepository.java:13
  possible duplicates:
    be.wacken.planner.SyncingFestivalRepository#SyncingFestivalRepository(be.wacken.planner.SyncingFestivalRepository.Cache,FestivalRepository)  app/src/main/java/be/wacken/planner/SyncingFestivalRepository.java:13
    be.wacken.planner.infrastructure.SyncedBandRepository#SyncedBandRepository(BandRepository,BandRepository)  infrastructure/src/main/java/be/wacken/planner/infrastructure/SyncedBandRepository.java:14
    be.wacken.planner.infrastructure.SyncedFoodOptionRepository#SyncedFoodOptionRepository(FoodOptionRepository,FoodOptionRepository)  infrastructure/src/main/java/be/wacken/planner/infrastructure/SyncedFoodOptionRepository.java:13
    be.wacken.planner.infrastructure.SyncedPerformanceRepository#SyncedPerformanceRepository(PerformanceRepository,PerformanceRepository)  infrastructure/src/main/java/be/wacken/planner/infrastructure/SyncedPerformanceRepository.java:13
    be.wacken.planner.infrastructure.SyncedRatingRepository#SyncedRatingRepository(RatingRepository,RatingRepository)  infrastructure/src/main/java/be/wacken/planner/infrastructure/SyncedRatingRepository.java:16
    be.wacken.planner.infrastructure.SyncedStageDistanceRepository#SyncedStageDistanceRepository(StageDistanceRepository,StageDistanceRepository)  infrastructure/src/main/java/be/wacken/planner/infrastructure/SyncedStageDistanceRepository.java:15
    be.wacken.planner.infrastructure.SyncedStageRepository#SyncedStageRepository(StageRepository,StageRepository)  infrastructure/src/main/java/be/wacken/planner/infrastructure/SyncedStageRepository.java:14
be.wacken.planner.SyncingFestivalRepository#save(Festival)  app/src/main/java/be/wacken/planner/SyncingFestivalRepository.java:27
  possible duplicates:
    be.wacken.planner.infrastructure.SyncedBandRepository#save(Band)  infrastructure/src/main/java/be/wacken/planner/infrastructure/SyncedBandRepository.java:23
    be.wacken.planner.infrastructure.SyncedFoodOptionRepository#save(FoodOption)  infrastructure/src/main/java/be/wacken/planner/infrastructure/SyncedFoodOptionRepository.java:22
    be.wacken.planner.infrastructure.SyncedPerformanceRepository#save(Performance)  infrastructure/src/main/java/be/wacken/planner/infrastructure/SyncedPerformanceRepository.java:22
    be.wacken.planner.infrastructure.SyncedStageDistanceRepository#save(StageDistance)  infrastructure/src/main/java/be/wacken/planner/infrastructure/SyncedStageDistanceRepository.java:24
    be.wacken.planner.infrastructure.SyncedStageRepository#save(Stage)  infrastructure/src/main/java/be/wacken/planner/infrastructure/SyncedStageRepository.java:23
be.wacken.planner.SyncingPersonalBandRatingHistoryRepository#SyncingPersonalBandRatingHistoryRepository(be.wacken.planner.PersonalBandRatingSyncLocalStore,be.wacken.planner.SupabasePersonalBandRatingRemote,be.wacken.planner.AuthSession)  app/src/main/java/be/wacken/planner/SyncingPersonalBandRatingHistoryRepository.java:15
  possible duplicates:
    be.wacken.planner.SyncingRatingRepository#SyncingRatingRepository(be.wacken.planner.RatingSyncLocalStore,be.wacken.planner.SupabaseRatingRemote,be.wacken.planner.AuthSession)  app/src/main/java/be/wacken/planner/SyncingRatingRepository.java:17
be.wacken.planner.VerifiedMusicLinksProvider#entity(java.lang.String)  app/src/main/java/be/wacken/planner/VerifiedMusicLinksProvider.java:113
  possible duplicates:
    be.wacken.planner.WikidataMetadataProvider#entityDetails(java.lang.String)  app/src/main/java/be/wacken/planner/WikidataMetadataProvider.java:101
  not identical
be.wacken.planner.VerifiedMusicLinksProvider#relationships(JSONObject,java.lang.String)  app/src/main/java/be/wacken/planner/VerifiedMusicLinksProvider.java:131
  possible duplicates:
    be.wacken.planner.VerifiedMusicLinksProvider#musicRelationships(JSONObject,boolean)  app/src/main/java/be/wacken/planner/VerifiedMusicLinksProvider.java:139
  not identical
be.wacken.planner.WikidataMetadataProvider#scoreFor(java.lang.String,java.lang.String)  app/src/main/java/be/wacken/planner/WikidataMetadataProvider.java:94
  possible duplicates:
    be.wacken.planner.WikipediaMetadataProvider#scoreFor(java.lang.String,java.lang.String)  app/src/main/java/be/wacken/planner/WikipediaMetadataProvider.java:150
be.wacken.planner.application.ActiveFestivalAfterRatingRepository#findAll()  application/src/main/java/be/wacken/planner/application/ActiveFestivalAfterRatingRepository.java:23
  possible duplicates:
    be.wacken.planner.application.ActiveFestivalRatingRepository#findAll()  application/src/main/java/be/wacken/planner/application/ActiveFestivalRatingRepository.java:38
  not identical
be.wacken.planner.application.ApplyBandMetadataProposalsUseCase#ApplyBandMetadataProposalsUseCase(BandRepository)  application/src/main/java/be/wacken/planner/application/ApplyBandMetadataProposalsUseCase.java:13
  possible duplicates:
    be.wacken.planner.application.EnrichBandMetadataFromCatalogUseCase#EnrichBandMetadataFromCatalogUseCase(BandRepository)  application/src/main/java/be/wacken/planner/application/EnrichBandMetadataFromCatalogUseCase.java:13
    be.wacken.planner.application.ImportInitialBandListUseCase#ImportInitialBandListUseCase(BandRepository)  application/src/main/java/be/wacken/planner/application/ImportInitialBandListUseCase.java:12
be.wacken.planner.application.BandDetailItem#personRatingSummary()  application/src/main/java/be/wacken/planner/application/BandDetailItem.java:41
  possible duplicates:
    be.wacken.planner.application.BandListItem#personRatingSummary()  application/src/main/java/be/wacken/planner/application/BandListItem.java:46
    be.wacken.planner.application.ScheduleDecisionCandidate#personRatingSummary()  application/src/main/java/be/wacken/planner/application/ScheduleDecisionCandidate.java:54
be.wacken.planner.application.BandMetadataProviderCandidate#BandMetadataProviderCandidate(java.lang.String,java.util.Optional<java.lang.String>,java.util.Optional<java.lang.String>,java.util.Optional<java.lang.String>,java.util.Optional<java.lang.String>,java.util.Optional<java.lang.String>,int)  application/src/main/java/be/wacken/planner/application/BandMetadataProviderCandidate.java:14
  possible duplicates:
    be.wacken.planner.domain.Band#Band(java.lang.String,java.util.Optional<java.lang.String>,java.util.Optional<java.lang.String>,java.util.Optional<java.lang.String>,java.util.Optional<java.lang.String>)  domain/src/main/java/be/wacken/planner/domain/Band.java:18
  not identical
be.wacken.planner.application.EffectiveRatingResolver#EffectiveRatingResolver(RatingRepository)  application/src/main/java/be/wacken/planner/application/EffectiveRatingResolver.java:13
  possible duplicates:
    be.wacken.planner.application.RateBandUseCase#RateBandUseCase(RatingRepository)  application/src/main/java/be/wacken/planner/application/RateBandUseCase.java:13
    be.wacken.planner.application.RateRealBandUseCase#RateRealBandUseCase(RealRatingRepository)  application/src/main/java/be/wacken/planner/application/RateRealBandUseCase.java:13
be.wacken.planner.application.EnrichBandMetadataFromCatalogUseCase#firstPresent(java.util.Optional<java.lang.String>,java.util.Optional<java.lang.String>)  application/src/main/java/be/wacken/planner/application/EnrichBandMetadataFromCatalogUseCase.java:48
  possible duplicates:
    be.wacken.planner.application.ReviewFestivalCsvUseCase#keep(java.util.Optional<java.lang.String>,java.util.Optional<java.lang.String>)  application/src/main/java/be/wacken/planner/application/ReviewFestivalCsvUseCase.java:221
be.wacken.planner.application.ExportRatingLifecycleCsvUseCase#ExportRatingLifecycleCsvUseCase(BandRepository,FestivalRepository,FestivalLineupRepository,FestivalPlanningRatingRepository,FestivalAfterRatingRepository,PersonalBandRatingRepository,PersonalBandRatingHistoryRepository,RatingCorrectionRepository)  application/src/main/java/be/wacken/planner/application/ExportRatingLifecycleCsvUseCase.java:19
  possible duplicates:
    be.wacken.planner.application.ExportRatingsCsvUseCase#ExportRatingsCsvUseCase(BandRepository,PerformanceRepository,RatingRepository,RealRatingRepository)  application/src/main/java/be/wacken/planner/application/ExportRatingsCsvUseCase.java:32
    be.wacken.planner.application.GenerateSharedScheduleUseCase#GenerateSharedScheduleUseCase(PerformanceRepository,RatingRepository,StageDistanceRepository,PerformanceConflictDetector,PerformanceConflictResolver)  application/src/main/java/be/wacken/planner/application/GenerateSharedScheduleUseCase.java:51
    be.wacken.planner.application.ImportFestivalCsvUseCase#ImportFestivalCsvUseCase(BandRepository,StageRepository,PerformanceRepository,StageDistanceRepository,FoodOptionRepository,java.util.function.Consumer<java.lang.Runnable>)  application/src/main/java/be/wacken/planner/application/ImportFestivalCsvUseCase.java:33
    be.wacken.planner.application.LinkImportedBandUseCase#LinkImportedBandUseCase(FestivalRepository,FestivalLineupRepository,BandRepository)  application/src/main/java/be/wacken/planner/application/LinkImportedBandUseCase.java:21
    be.wacken.planner.application.ListActiveFestivalBandsUseCase#ListActiveFestivalBandsUseCase(FestivalRepository,FestivalLineupRepository,PerformanceRepository,RatingRepository,java.lang.String)  application/src/main/java/be/wacken/planner/application/ListActiveFestivalBandsUseCase.java:30
    be.wacken.planner.application.ListArchivedFestivalBandsUseCase#ListArchivedFestivalBandsUseCase(FestivalLineupRepository,FestivalPlanningRatingRepository,PersonalBandRatingHistoryRepository,RealRatingRepository,PerformanceRepository)  application/src/main/java/be/wacken/planner/application/ListArchivedFestivalBandsUseCase.java:35
    be.wacken.planner.application.ListBandsUseCase#ListBandsUseCase(BandRepository,PerformanceRepository,RatingRepository,java.lang.String)  application/src/main/java/be/wacken/planner/application/ListBandsUseCase.java:26
    be.wacken.planner.application.RecordFestivalAfterRatingUseCase#RecordFestivalAfterRatingUseCase(FestivalRepository,FestivalLineupRepository,FestivalAfterRatingRepository,java.time.Clock)  application/src/main/java/be/wacken/planner/application/RecordFestivalAfterRatingUseCase.java:14
    be.wacken.planner.application.RecordPersonalBandRatingUseCase#RecordPersonalBandRatingUseCase(FestivalRepository,PersonalBandRatingHistoryRepository,RealRatingRepository,java.time.Clock)  application/src/main/java/be/wacken/planner/application/RecordPersonalBandRatingUseCase.java:24
    be.wacken.planner.application.ReviewFestivalCsvUseCase#ReviewFestivalCsvUseCase(BandRepository,StageRepository,PerformanceRepository,StageDistanceRepository,FoodOptionRepository,FestivalRepository,FestivalLineupRepository,java.util.function.Consumer<java.lang.Runnable>)  application/src/main/java/be/wacken/planner/application/ReviewFestivalCsvUseCase.java:39
    be.wacken.planner.application.ReviewImportedBandLinksUseCase#ReviewImportedBandLinksUseCase(FestivalRepository,FestivalLineupRepository,BandRepository)  application/src/main/java/be/wacken/planner/application/ReviewImportedBandLinksUseCase.java:22
    be.wacken.planner.application.ShowBandDetailUseCase#ShowBandDetailUseCase(BandRepository,RatingRepository,RealRatingRepository,FestivalRepository,PersonalBandRatingHistoryRepository)  application/src/main/java/be/wacken/planner/application/ShowBandDetailUseCase.java:34
    be.wacken.planner.application.ViewBandRatingContextUseCase#ViewBandRatingContextUseCase(BandRepository,FestivalRepository,FestivalLineupRepository,FestivalPlanningRatingRepository,FestivalAfterRatingRepository,PersonalBandRatingRepository)  application/src/main/java/be/wacken/planner/application/ViewBandRatingContextUseCase.java:14
  not identical
be.wacken.planner.application.ExportRatingsCsvUseCase#groupRatings(Band)  application/src/main/java/be/wacken/planner/application/ExportRatingsCsvUseCase.java:89
  possible duplicates:
    be.wacken.planner.application.GenerateSharedScheduleUseCase#personRatingsFor(Band)  application/src/main/java/be/wacken/planner/application/GenerateSharedScheduleUseCase.java:229
    be.wacken.planner.application.ListArchivedFestivalBandsUseCase#personRatingsFor(java.lang.String,Band)  application/src/main/java/be/wacken/planner/application/ListArchivedFestivalBandsUseCase.java:111
    be.wacken.planner.application.ListBandsUseCase#personRatingsFor(Band)  application/src/main/java/be/wacken/planner/application/ListBandsUseCase.java:83
    be.wacken.planner.application.ShowBandDetailUseCase#personRatingsFor(be.wacken.planner.domain.Band)  application/src/main/java/be/wacken/planner/application/ShowBandDetailUseCase.java:89
  not identical
be.wacken.planner.application.FestivalBandCatalog.Snapshot#Snapshot(java.util.List<CanonicalBand>,java.util.List<ConfirmedBandAlias>)  application/src/main/java/be/wacken/planner/application/FestivalBandCatalog.java:11
  possible duplicates:
    be.wacken.planner.application.MusicLinkLookupProvider.Lookup#Lookup(java.util.List<be.wacken.planner.application.MusicLinkEvidence>,java.util.List<java.lang.String>,boolean)  application/src/main/java/be/wacken/planner/application/MusicLinkLookupProvider.java:10
be.wacken.planner.application.ImportFestivalCsvResult#ImportFestivalCsvResult(boolean,java.util.List<java.lang.String>)  application/src/main/java/be/wacken/planner/application/ImportFestivalCsvResult.java:8
  possible duplicates:
    be.wacken.planner.domain.PerformanceConflictResolver.Candidate#Candidate(be.wacken.planner.domain.Performance,java.util.List<be.wacken.planner.domain.Rating>,be.wacken.planner.domain.GroupDecision,int,int)  domain/src/main/java/be/wacken/planner/domain/PerformanceConflictResolver.java:114
be.wacken.planner.application.ListActiveFestivalBandsUseCase#performanceFor(java.util.List<Performance>,Band)  application/src/main/java/be/wacken/planner/application/ListActiveFestivalBandsUseCase.java:62
  possible duplicates:
    be.wacken.planner.application.ListArchivedFestivalBandsUseCase#performanceFor(java.util.List<Performance>,Band)  application/src/main/java/be/wacken/planner/application/ListArchivedFestivalBandsUseCase.java:80
be.wacken.planner.application.MusicLinks#normalize(java.util.Optional<java.lang.String>)  application/src/main/java/be/wacken/planner/application/MusicLinks.java:15
  possible duplicates:
    be.wacken.planner.domain.Band#normalize(java.util.Optional<java.lang.String>)  domain/src/main/java/be/wacken/planner/domain/Band.java:33
be.wacken.planner.application.RateBandUseCase#rateBand(java.lang.String,Band,int)  application/src/main/java/be/wacken/planner/application/RateBandUseCase.java:17
  possible duplicates:
    be.wacken.planner.application.RateRealBandUseCase#rateBand(java.lang.String,Band,int)  application/src/main/java/be/wacken/planner/application/RateRealBandUseCase.java:17
be.wacken.planner.application.RenameActiveFestivalUseCase#RenameActiveFestivalUseCase(FestivalRepository)  application/src/main/java/be/wacken/planner/application/RenameActiveFestivalUseCase.java:14
  possible duplicates:
    be.wacken.planner.application.ShowFestivalStartUseCase#ShowFestivalStartUseCase(FestivalRepository)  application/src/main/java/be/wacken/planner/application/ShowFestivalStartUseCase.java:13
be.wacken.planner.application.SearchBandMetadataUseCase#addCandidateProposals(java.lang.String,java.lang.String,java.lang.String,Band,be.wacken.planner.application.SearchBandMetadataUseCase.OptionalSource,java.util.Set<be.wacken.planner.application.BandMetadataField>,java.util.List<be.wacken.planner.application.BandMetadataProposal>,int)  application/src/main/java/be/wacken/planner/application/SearchBandMetadataUseCase.java:128
  possible duplicates:
    be.wacken.planner.application.SearchBandMetadataUseCase#addCandidateProposals(java.lang.String,java.lang.String,java.lang.String,be.wacken.planner.application.BandMetadataProviderCandidate,be.wacken.planner.application.SearchBandMetadataUseCase.OptionalSource,java.util.Set<be.wacken.planner.application.BandMetadataField>,java.util.List<be.wacken.planner.application.BandMetadataProposal>,int)  application/src/main/java/be/wacken/planner/application/SearchBandMetadataUseCase.java:144
be.wacken.planner.domain.Band#requireName(java.lang.String,java.lang.String)  domain/src/main/java/be/wacken/planner/domain/Band.java:26
  possible duplicates:
    be.wacken.planner.domain.Festival#requireText(java.lang.String,java.lang.String)  domain/src/main/java/be/wacken/planner/domain/Festival.java:40
    be.wacken.planner.domain.FestivalLineupEntry#requireText(java.lang.String,java.lang.String)  domain/src/main/java/be/wacken/planner/domain/FestivalLineupEntry.java:12
    be.wacken.planner.domain.PersonalBandRatingEvent#requireText(java.lang.String,java.lang.String)  domain/src/main/java/be/wacken/planner/domain/PersonalBandRatingEvent.java:24
    be.wacken.planner.domain.SavedFestivalPlanningRating#requireText(java.lang.String,java.lang.String)  domain/src/main/java/be/wacken/planner/domain/SavedFestivalPlanningRating.java:14
be.wacken.planner.infrastructure.FileBackedBandRepository#save(Band)  infrastructure/src/main/java/be/wacken/planner/infrastructure/FileBackedBandRepository.java:20
  possible duplicates:
    be.wacken.planner.infrastructure.FileBackedStageDistanceRepository#save(StageDistance)  infrastructure/src/main/java/be/wacken/planner/infrastructure/FileBackedStageDistanceRepository.java:21
    be.wacken.planner.infrastructure.FileBackedStageRepository#save(Stage)  infrastructure/src/main/java/be/wacken/planner/infrastructure/FileBackedStageRepository.java:20
  not identical
be.wacken.planner.infrastructure.FileBackedBandRepository#writeBands(java.util.List<Band>)  infrastructure/src/main/java/be/wacken/planner/infrastructure/FileBackedBandRepository.java:32
  possible duplicates:
    be.wacken.planner.infrastructure.FileBackedPerformanceRepository#writePerformances(java.util.List<Performance>)  infrastructure/src/main/java/be/wacken/planner/infrastructure/FileBackedPerformanceRepository.java:33
    be.wacken.planner.infrastructure.FileBackedStageDistanceRepository#writeDistances(java.util.List<StageDistance>)  infrastructure/src/main/java/be/wacken/planner/infrastructure/FileBackedStageDistanceRepository.java:33
  not identical
be.wacken.planner.infrastructure.FileBackedBandRepository#loadBandsByName()  infrastructure/src/main/java/be/wacken/planner/infrastructure/FileBackedBandRepository.java:54
  possible duplicates:
    be.wacken.planner.infrastructure.FileBackedStageRepository#loadStagesByName()  infrastructure/src/main/java/be/wacken/planner/infrastructure/FileBackedStageRepository.java:48
  not identical
be.wacken.planner.infrastructure.FileBackedFoodOptionRepository#writeFoodOptions(java.util.List<FoodOption>)  infrastructure/src/main/java/be/wacken/planner/infrastructure/FileBackedFoodOptionRepository.java:30
  possible duplicates:
    be.wacken.planner.infrastructure.FileBackedStageRepository#writeStages(java.util.List<Stage>)  infrastructure/src/main/java/be/wacken/planner/infrastructure/FileBackedStageRepository.java:32
be.wacken.planner.infrastructure.FileBackedRatingRepository#loadRatingsByKey()  infrastructure/src/main/java/be/wacken/planner/infrastructure/FileBackedRatingRepository.java:45
  possible duplicates:
    be.wacken.planner.infrastructure.FileBackedStageDistanceRepository#loadDistancesByStagePair()  infrastructure/src/main/java/be/wacken/planner/infrastructure/FileBackedStageDistanceRepository.java:56
  not identical
be.wacken.planner.infrastructure.SyncedBandRepository#replaceAll(java.util.List<Band>)  infrastructure/src/main/java/be/wacken/planner/infrastructure/SyncedBandRepository.java:29
  possible duplicates:
    be.wacken.planner.infrastructure.SyncedFoodOptionRepository#replaceAll(java.util.List<FoodOption>)  infrastructure/src/main/java/be/wacken/planner/infrastructure/SyncedFoodOptionRepository.java:28
    be.wacken.planner.infrastructure.SyncedPerformanceRepository#replaceAll(java.util.List<Performance>)  infrastructure/src/main/java/be/wacken/planner/infrastructure/SyncedPerformanceRepository.java:28
    be.wacken.planner.infrastructure.SyncedStageDistanceRepository#replaceAll(java.util.List<StageDistance>)  infrastructure/src/main/java/be/wacken/planner/infrastructure/SyncedStageDistanceRepository.java:30
    be.wacken.planner.infrastructure.SyncedStageRepository#replaceAll(java.util.List<Stage>)  infrastructure/src/main/java/be/wacken/planner/infrastructure/SyncedStageRepository.java:29
```

## Appendix D — repeat scan output and reproduction

```text
ok
created 0, refreshed 109, matched 0, findings 99
java · info · JAVA_METHOD_REFERENCES ×4: 111 deferred method references are source references, not invocations.
java · info · JAVA_MISSING_EXTERNAL_TYPES ×1: app/src/main/java/be/wacken/planner/MusicLinksReviewActivity.java:120: 4260 symbol and package references are unresolved (project dependencies and generated sources are not loaded). Most frequently missing packages: be.wacken.planner.domain, LinearLayout, be.wacken.planner.application, androidx.room, android.widget.
java · info · JAVA_SOURCE_SET ×1: Main sources only. No annotation processing, synthetic declarations, callback binding or framework runtime dispatch inference.
java · info · JAVA_UNRESOLVED_CALLS ×4: 2683 calls have no supported source implementation target (virtual dispatch, external code or generated declarations).
java · warning · compiler.err.invalid.mref ×3: app/src/main/java/be/wacken/planner/AppRepositories.java:109: invalid method reference
java · warning · compiler.err.method.does.not.override.superclass ×224: app/src/main/java/be/wacken/planner/ArchivedBandDetailActivity.java:202: method does not override or implement a method from a supertype
java · warning · compiler.err.prob.found.req ×36: app/src/main/java/be/wacken/planner/FestivalBandReviewDialog.java:42: incompatible types: cannot infer type arguments for java.util.ArrayList<>
java · warning · compiler.err.unexpected.lambda ×1: app/src/main/java/be/wacken/planner/AppRepositories.java:224: lambda expression not expected here
Run groma lint to review the findings.
```

```sh
groma --version
groma scanner list --max-count 100
groma scan
groma lint --max-count 200
groma view --plain --max-count 200
groma view wacken --plain --max-count 300
groma view application/src/main/java/be/wacken/planner/application/ViewBandRatingContextUseCase.java
git ls-files groma
```

The temporary static map was exported to `/private/tmp/wacken-groma-map-20261020` and inspected locally. The pre-scan snapshot and temporary machine-readable inventory are local investigation evidence, not a tracked architecture baseline.

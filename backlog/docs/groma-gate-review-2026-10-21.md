# Architecture gate baseline review — 21 October 2026

The user approved the standard TASK233 proposal. ADR0022 records the decision.
The baseline preserves95 components,294 unique source files and13 scenario flows.
All current88 duplicate candidate groups were individually classified below using
the original99-group audit and TASK240 helper dispositions. Group order/count is
not a policy identity: each JSON entry hashes sorted full source paths, method
signatures and scanner-normalized operation tokens. Line numbers, operation
positions and diagram storage paths are excluded.

The table abbreviates member names for readability; the versioned JSON retains
full signatures and identities. Changes to a member body, signature or group
membership require review even when the total group count is unchanged.
Resolved groups are reported for baseline cleanup. Intentional file moves and
combines require a reviewed candidate baseline, not automatic acceptance.

The first real clean-checkout check found11 stale TASK227 references after earlier
combines. They were corrected through Backlog CLI. Existing owned source files
must also appear in fresh scanner evidence, preventing a partial scan from
silently passing. Operation/token/invocation evidence decreases and increased
known unresolved/deferred facts are surfaced for review. Diagnostic categories
remain scanner limitations, not application compiler errors.

Exactly two TASK228 historical authored rows are accepted stale-endpoint
exceptions. Both full ordered endpoint pairs, rationale and user acceptance are
stored in the baseline; no source ownership or flow use is permitted for the
deleted importer. There are no accepted component islands. These exceptions do
not approve new tooling changes or arbitrary dangling links.

## Current duplicate dispositions

| Stable identity prefix | Members | Reviewed disposition |
| --- | --- | --- |
| `5d86ea38afe408a2` | AddFestivalAction.java:execute; ArchiveFestivalAction.java:execute | Operation-specific worker/UI callbacks retain distinct result and failure contracts. |
| `480b98ef6296446a` | ArchivedBandDetailActivity.java:ratingSection; ArchivedBandDetailActivity.java:realRatingSection | Planning and after-rating display differ in meaning, reset behavior and colour. |
| `0ad65a533c88e08a` | ArchivedBandDetailActivity.java:runningOrderSection; BandDetailActivity.java:runningOrderSection | Tiny Activity wrappers delegate to the shared detail presenter; lifecycle routing remains local. |
| `aafa2801b25ef378` | ArchivedBandDetailActivity.java:loadImage; BandDetailActivity.java:loadImage | Tiny image wrappers delegate to the bounded lifecycle-safe shared loader. |
| `0c6e0da7ce49e11b` | ArchivedBandDetailActivity.java:valueExtra; BandDetailActivity.java:valueExtra | Per-screen Intent fallback preserves navigation ownership. |
| `2dfc1fb5d6ea45de` | ArchivedFestivalActivity.java:headerRow; MainActivity.java:tableHeader | Header presentation belongs to approved 3.0 lineup work TASK207/211. |
| `bc9813139015c5d2` | ArchivedFestivalActivity.java:row; MainActivity.java:tableRow | Active/archive row presentation belongs to TASK207/211. |
| `e364d5e7265728d8` | ArchivedFestivalActivity.java:cell; MainActivity.java:cell | Cell visual styling remains in TASK207/211; parameter ordering differs. |
| `9bd7e8d74637c1af` | ArchivedFestivalActivity.java:headerCell; MainActivity.java:headerCell | Header cell styling belongs to TASK207/211. |
| `63f3c293e46628e9` | ArchivedFestivalActivity.java:bandNameCell; MainActivity.java:bandNameCell | Band-name cell styling belongs to TASK207/211. |
| `aecc1b31d0cc7f46` | ArchivedFestivalActivity.java:rowActions; MainActivity.java:rowActions | Active/archive action availability and routing must remain distinct until TASK207/211. |
| `65f540cb3122fa54` | ArchivedFestivalActivity.java:iconButton; MainActivity.java:iconButton | Icon styling belongs to TASK207/211 without sharing lifecycle actions. |
| `b196546b482382d6` | ArchivedFestivalActivity.java:bandsByName; MainActivity.java:bandsByName | Small exact-name projection preserves canonical identity semantics. |
| `069c9add20525bc6` | ArchivedFestivalActivity.java:dp; BandLinkReviewActivity.java:dp; BandMetadataReviewActivity.java:dp; ImportCsvActivity.java:dp; LoginActivity.java:dp; MainActivity.java:dp; MusicLinksReviewActivity.java:dp; ScheduleActivity.java:dp; SettingsActivity.java:dp | Trivial density conversion stays with screen UI ownership until approved 3.0 work. |
| `57d3984a0394d8cb` | ArchivedRatingCorrectionDialog.java:ArchivedRatingCorrectionDialog; AuthSession.java:AuthSession; RoomFestivalAfterRating.java:RoomFestivalAfterRating; RoomPersonalBandRating.java:RoomPersonalBandRating; RoomPersonalBandRatingEvent.java:RoomPersonalBandRatingEvent | Unrelated constructor field assignments are boilerplate, not shared business behavior. |
| `5cb7bd721333af26` | BandDetailPresentation.java:infoLine; BandDetailPresentation.java:groupRatings; BandDetailPresentation.java:sectionTitle; BandLinkReviewActivity.java:renderRows; BandMetadataReviewActivity.java:taskPanelTitle; BandMetadataReviewActivity.java:taskLine; BandMetadataReviewActivity.java:messageRow; LocalCsvImportReviewDialog.java:label; MusicLinksReviewActivity.java:label; ScheduleActivity.java:sectionTitle; ScheduleActivity.java:stageHeader; ScheduleActivity.java:message; SettingsActivity.java:infoText | Mixed text/view factories retain screen-specific roles; shared detail primitives already extracted. |
| `2cda0bed1950a58f` | BandLinkReviewActivity.java:actionButton; BandMetadataReviewActivity.java:actionButton; SettingsActivity.java:actionButton | Button styling differs by review/action owner and belongs to TASK210/211. |
| `53f840d6a8564a43` | BandLinkReviewActivity.java:content; BandMetadataReviewActivity.java:content | Review-screen shells host distinct candidate workflows; TASK210/211 owns visual reuse. |
| `6ac9a0a88ba875a6` | BandMetadataReviewActivity.java:metadataProviders; VerifiedMusicLinksProvider.java:VerifiedMusicLinksProvider | General metadata ranking and corroborated music identity collection have different trust contracts. |
| `4c340a0bfaf166dd` | FestivalStartScreenContent.java:FestivalStartScreenContent; RoomBandAlias.java:RoomBandAlias; RoomFestivalPlanningRating.java:RoomFestivalPlanningRating; RoomScheduleLock.java:RoomScheduleLock | Unrelated immutable screen/persistence row constructors are boilerplate. |
| `3563defa3cbc9b78` | LocalCsvImportReviewDialog.java:notifyUi; MusicLinksReviewActivity.java:notifyUi | Trivial notification callbacks have distinct retained-session/lifecycle owners. |
| `d07553bcfd5eb1c1` | MainActivity.java:loadCurrentSession; SettingsActivity.java:loadCurrentSession | Session lookup remains with each navigation owner. |
| `231bf9daf7115c3b` | MainActivity.java:redirectToLogin; SettingsActivity.java:redirectToLogin | Login navigation flags/lifecycle remain with the owning screen. |
| `ea0e01d12ec5acff` | MainActivity.java:startSyncAnimation; SettingsActivity.java:startSyncAnimation | Each screen owns animation and busy/destruction state. |
| `d97066da8cf09197` | MusicBrainzMetadataProvider.java:get; WikidataMetadataProvider.java:get; WikipediaMetadataProvider.java:get; YouTubeMetadataProvider.java:get | Provider HTTP calls retain distinct host/throttling/header/error contracts. |
| `2b0b8b3bb360c7eb` | MusicBrainzMetadataProvider.java:read; SpotifyMetadataProvider.java:read; SupabaseAuthClient.java:read; SupabaseHttpTransport.java:read; SupabaseMasterDataClient.java:read; WikidataMetadataProvider.java:read; WikipediaMetadataProvider.java:read; YouTubeMetadataProvider.java:read | Small UTF8 stream loops remain in adapters with distinct bounds and resource lifetimes; rating transport already extracted. |
| `2a6aedebcd5c5408` | MusicBrainzMetadataProvider.java:search; WikidataMetadataProvider.java:search; WikipediaMetadataProvider.java:search | Provider response schemas and ranking evidence are distinct. |
| `47cf427889ab765a` | RoomBandRepository.java:toRow; ReviewFestivalCsvUseCase.java:bandFields | Persistence row mapping and CSV review comparison tuples serve different boundaries. |
| `85071f1981a70d06` | RoomFestivalAfterRatingRepository.java:domain; RoomPersonalBandRatingHistoryRepository.java:toDomain; RoomPersonalBandRatingRepository.java:domain | After-state, history and established-opinion mapping preserve different provenance and time semantics. |
| `843140cf061025a7` | RoomFestivalLineupRepository.java:saveAllForFestival; RoomFestivalLineupRepository.java:findByFestival; RoomFestivalLineupRepository.java:findAll; RoomFestivalLineupRepository.java:replaceAll; RoomFoodOptionRepository.java:replaceAll; RoomRealRatingRepository.java:findAll; RoomStageRepository.java:replaceAll; FestivalAfterRatingView.java:findAll; FestivalPlanningRatingView.java:findAll; MemberRatingProjection.java:forFestivalBand; FileBackedRatingRepository.java:findAll | Mixed query/list/replacement projections retain distinct festival scope, tables and transaction semantics. |
| `1d6a4da3f9158a59` | RoomFestivalPlanningRatingRepository.java:saveSynced; RoomFestivalPlanningRatingRepository.java:discardPendingRating | Synced insert and exact stale-protected pending discard are different operations. |
| `a297b87c8a93347d` | RoomFestivalPlanningRatingRepository.java:findPending; RoomRatingRepository.java:findPending | Planning and legacy pending queries expose different domain contracts. |
| `82a689bf81877750` | RoomFestivalPlanningRatingRepository.java:findAll; RoomRatingRepository.java:findAll | Planning and legacy all-row queries expose different identities and tables. |
| `2046d311c0eaaa39` | RoomPersonalBandRatingHistoryRepository.java:save; RoomPersonalBandRatingHistoryRepository.java:saveSynced | PENDING and SYNCED history writes differ materially for offline behavior. |
| `bd5feb881d6b2a27` | RoomRatingRepository.java:findByUserAndBand; RoomRealRatingRepository.java:findByUserAndBand | Planning and real-rating stores use different domain ports and tables. |
| `9653780fa46f2674` | RoomScheduleLockStore.java:findActiveLocks; RoomScheduleLockStore.java:findPendingSelections | Active locks exclude delete operations; pending selections use a different DAO predicate. |
| `dde445585b59d5e8` | RoomStageDistanceRepository.java:findBetween; FileBackedStageDistanceRepository.java:findBetween | Room/TSV adapters implement the same small distance rule without creating cross-adapter coupling. |
| `7497bfff5f9102e2` | ScheduleActivity.java:hourGridLine; ScheduleActivity.java:halfHourNotch | Grid line versus notch styling belongs to TASK209 while preserving geometry. |
| `859e80e0e6e4001d` | ScheduleActivity.java:hourGridLineLayout; ScheduleActivity.java:halfHourNotchLayout; ScheduleActivity.java:halfHourGridLineLayout | Line/notch layout widths and offsets represent different grid elements. |
| `f8ff81537cb84c93` | ScheduleActivity.java:stars; ScheduleBlockContent.java:stars | Schedule stars remain schedule-owned; positive-member summaries have a separate tested contract. |
| `3bc9e1d32bb5fdbe` | ScheduleCalendarLayout.java:topOffsetMinutes; ScheduleCalendarLayout.java:topOffsetMinutes | Typed layout overloads delegate one geometry rule. |
| `79e1581b09fe0af6` | ScheduleCalendarLayout.java:durationMinutes; ScheduleCalendarLayout.java:durationMinutes | Typed duration overloads delegate one minimum-duration rule. |
| `5f8a97a9a25ddcd9` | SettingsActivity.java:exportRatingLifecycleCsv; SettingsActivity.java:exportRatingsCsv | Legacy and lifecycle CSV are separate contracts; share mechanics only if independently justified. |
| `3aac219f49ba4425` | SpotifyMetadataProvider.java:imageFrom; WikidataMetadataProvider.java:label | Spotify image arrays and Wikidata labels are unrelated JSON schemas. |
| `717c7b735760c01c` | SpotifyMetadataProvider.java:nestedString; WikipediaMetadataProvider.java:nestedString; YouTubeMetadataProvider.java:nestedString | Small optional JSON accessors remain with provider response schema ownership. |
| `61eef75cf750ad4e` | SpotifyMetadataProvider.java:normalize; MusicLinkEvidence.java:text | Trivial null/trim in evidence and provider values does not justify an outward dependency. |
| `2a921d94bd828954` | SupabaseArchivedRatingCorrectionGateway.java:text; SupabaseRatingStateClient.java:text | Required identity-string parsing retains adapter-specific response and failure context. |
| `4743fa3dafaf5555` | SupabaseArchivedRatingCorrectionGateway.java:encode; SupabaseRatingStateClient.java:encode | Trivial UTF8 URL encoding remains with each endpoint contract. |
| `0d13ea3855413ecb` | SupabaseAuthClient.java:requestJson; SupabaseAuthClient.java:requestArray | JSONObject and JSONArray request parsing deliberately return different shapes. |
| `76f277daa44f3401` | SupabaseBandRepository.java:findByName; SupabaseStageRepository.java:findByName | Exact-name queries target different aggregates. |
| `dfb144df15c9c023` | SupabaseCanonicalBandLookup.java:namesById; SupabaseMasterDataClient.java:namesById; SupabaseScheduleLockClient.java:parseLocks | Canonical lookup projection, master-data join and schedule-lock parsing are distinct schemas. |
| `04783e324310eb35` | SupabaseFestivalArchiveGateway.java:Snapshot; BandMetadataSearchResult.java:BandMetadataSearchResult | Defensive copying in unrelated immutable result/snapshot constructors is boilerplate. |
| `3a696984b34a08c2` | SupabaseFestivalBandCatalog.java:SupabaseFestivalBandCatalog; SupabaseFestivalCreationGateway.java:SupabaseFestivalCreationGateway | Gateway/catalog constructors bind distinct collaboration contracts. |
| `6fd0e9ae2e05e45f` | SupabaseFestivalPlanningRatingClient.java:bandLookup; SupabasePersonalBandRatingClient.java:bandLookup; SupabaseRatingClient.java:bandLookup | Tiny lookup factories bind expected-user request callbacks; canonical identity policy is already shared. |
| `734079d69605a1b0` | SupabaseFestivalPlanningRatingClient.java:request; SupabasePersonalBandRatingClient.java:request; SupabaseRatingClient.java:request | Tiny request facades delegate to the common authenticated transport while retaining adapter contexts. |
| `fba54f48d5caeacc` | SupabaseFestivalPlanningRatingClient.java:pullGroupRatings; SupabasePersonalBandRatingClient.java:pullUserEvents | Group planning and own historical event pulls have different ownership/merge rules. |
| `8965c2b4171d043d` | SupabaseFestivalPlanningRatingClient.java:parseRatings; SupabasePersonalBandRatingClient.java:parseEvents | Planning snapshot and dated-event parsers preserve different response/domain contracts. |
| `56f2abd12e559d7c` | SupabaseHttpTransport.java:send; SupabaseMasterDataClient.java:send | Rating transport and master-data transport retain distinct body/header/bounds/compatibility contracts. |
| `a62e67e76c4cb449` | SupabaseMasterDataClient.java:foodOptions; SupabaseMasterDataClient.java:festivals; SupabaseMasterDataClient.java:bands; SupabaseMasterDataClient.java:stages | Master-data list readers parse distinct fields and lifecycle meanings. |
| `e99e04f1af04da88` | RoomBandRepository.java:optional; SupabaseMasterDataClient.java:optional; SupabasePersonalBandRatingClient.java:optional | Trivial optional string conversion retains each response/persistence schema context. |
| `9dcadb69957b11b2` | SupabaseRatingStateClient.java:afterRatings; SupabaseRatingStateClient.java:currentRatings | After-rating operations and established-opinion snapshots retain distinct scope/provenance fields. |
| `22e2fb0f72f915b6` | SyncingFestivalLineupRepository.java:SyncingFestivalLineupRepository; SyncingFestivalRepository.java:SyncingFestivalRepository; SyncedBandRepository.java:SyncedBandRepository; SyncedFoodOptionRepository.java:SyncedFoodOptionRepository; SyncedPerformanceRepository.java:SyncedPerformanceRepository; SyncedRatingRepository.java:SyncedRatingRepository; SyncedStageDistanceRepository.java:SyncedStageDistanceRepository; SyncedStageRepository.java:SyncedStageRepository | Repository decorator constructors are unrelated typed assignments. |
| `820dc62ab02840ee` | SyncingFestivalRepository.java:save; SyncedBandRepository.java:save; SyncedFoodOptionRepository.java:save; SyncedPerformanceRepository.java:save; SyncedStageDistanceRepository.java:save; SyncedStageRepository.java:save | Typed source-first saves preserve aggregate-specific repository boundaries. |
| `fd30a9337c8f62cf` | SyncingPersonalBandRatingHistoryRepository.java:SyncingPersonalBandRatingHistoryRepository; SyncingRatingRepository.java:SyncingRatingRepository | History sync and planning sync constructors have different owner/session contracts. |
| `a425264c4f05ccd6` | VerifiedMusicLinksProvider.java:entity; WikidataMetadataProvider.java:entityDetails | General Wikidata proposals and corroborated music identity collection have separate trust policies. |
| `02da146b79ab30ee` | VerifiedMusicLinksProvider.java:relationships; VerifiedMusicLinksProvider.java:musicRelationships | Declared homepage extraction and canonical music-profile extraction have different filters. |
| `69826a5647c5b08d` | WikidataMetadataProvider.java:scoreFor; WikipediaMetadataProvider.java:scoreFor | Provider scoring uses different evidence; ranking is not identity verification. |
| `6923c78021de9e74` | ActiveFestivalAfterRatingRepository.java:findAll; ActiveFestivalRatingRepository.java:findAll | Active planning and after-state views expose separate festival-bound rating contracts. |
| `2f9bf54dfcefb766` | ApplyBandMetadataProposalsUseCase.java:ApplyBandMetadataProposalsUseCase; EnrichBandMetadataFromCatalogUseCase.java:EnrichBandMetadataFromCatalogUseCase; ImportInitialBandListUseCase.java:ImportInitialBandListUseCase | Metadata application/catalog enrichment/initial import constructors are unrelated port assignments. |
| `0a0c64b301064c6b` | BandMetadataProviderCandidate.java:BandMetadataProviderCandidate; Band.java:Band | Band and provider-candidate construction normalize different value contracts. |
| `7ad1261983ab8c72` | EffectiveRatingResolver.java:EffectiveRatingResolver; RateBandUseCase.java:RateBandUseCase; RateRealBandUseCase.java:RateRealBandUseCase | Use-case constructors bind distinct planning/read/real-rating ports. |
| `c67635217be8dd18` | EnrichBandMetadataFromCatalogUseCase.java:firstPresent; ReviewFestivalCsvUseCase.java:keep | Small optional enrichment helpers preserve existing-first semantics within their owners. |
| `6e2b57f277d166bf` | ExportRatingLifecycleCsvUseCase.java:ExportRatingLifecycleCsvUseCase; ExportRatingsCsvUseCase.java:ExportRatingsCsvUseCase; GenerateSharedScheduleUseCase.java:GenerateSharedScheduleUseCase; LinkImportedBandUseCase.java:LinkImportedBandUseCase; ListActiveFestivalBandsUseCase.java:ListActiveFestivalBandsUseCase; ListArchivedFestivalBandsUseCase.java:ListArchivedFestivalBandsUseCase; ListBandsUseCase.java:ListBandsUseCase; RecordFestivalAfterRatingUseCase.java:RecordFestivalAfterRatingUseCase; RecordPersonalBandRatingUseCase.java:RecordPersonalBandRatingUseCase; ReviewFestivalCsvUseCase.java:ReviewFestivalCsvUseCase; ReviewImportedBandLinksUseCase.java:ReviewImportedBandLinksUseCase; ShowBandDetailUseCase.java:ShowBandDetailUseCase; ViewBandRatingContextUseCase.java:ViewBandRatingContextUseCase | Application orchestration constructors wire independent use cases; no generic service superclass. |
| `5fe11b84c65f6652` | FestivalBandCatalog.java:Snapshot; MusicLinkLookupProvider.java:Lookup | Immutable identity/evidence snapshot collection copying is boilerplate. |
| `7262ee7902aa4239` | ImportFestivalCsvResult.java:ImportFestivalCsvResult; PerformanceConflictResolver.java:Candidate | Validation result and schedule candidate collection copying are unrelated value invariants. |
| `c3590d507483623e` | ListActiveFestivalBandsUseCase.java:performanceFor; ListArchivedFestivalBandsUseCase.java:performanceFor | Active/archive performance projection remains bounded to each workflow. |
| `c20b3500ef9cf997` | ListBandsUseCase.java:personRatingsFor; ShowBandDetailUseCase.java:personRatingsFor | Small member projection call sites share the existing tested projection implementation. |
| `1bb99ff94d176d7a` | MusicLinks.java:normalize; Band.java:normalize | Optional profile normalization in domain/application does not justify boundary leakage. |
| `ff04f82d7ad63d63` | RateBandUseCase.java:rateBand; RateRealBandUseCase.java:rateBand | Planning and legacy real-rating commands use distinct ports and behavior despite similar validation shells. |
| `6885b3afc32734bb` | RenameActiveFestivalUseCase.java:RenameActiveFestivalUseCase; ShowFestivalStartUseCase.java:ShowFestivalStartUseCase | Festival use-case constructors bind distinct commands/read responsibilities. |
| `8a91d449dc951dba` | SearchBandMetadataUseCase.java:addCandidateProposals; SearchBandMetadataUseCase.java:addCandidateProposals | Typed metadata overloads adapt distinct candidate sources to one proposal workflow. |
| `821d6b44d103626a` | Band.java:requireName; Festival.java:requireText; FestivalLineupEntry.java:requireText; PersonalBandRatingEvent.java:requireText; SavedFestivalPlanningRating.java:requireText | Small required-name checks retain aggregate-specific error messages and invariants. |
| `df06489832eb4346` | FileBackedBandRepository.java:save; FileBackedStageDistanceRepository.java:save; FileBackedStageRepository.java:save | TSV save/update mechanics preserve aggregate-specific key and format semantics. |
| `8d86a49cfadba485` | FileBackedBandRepository.java:writeBands; FileBackedPerformanceRepository.java:writePerformances; FileBackedStageDistanceRepository.java:writeDistances | TSV writers preserve distinct schemas, headers, ordering and atomic-file contracts. |
| `99a17d3d6a9498be` | FileBackedBandRepository.java:loadBandsByName; FileBackedStageRepository.java:loadStagesByName | Band/stage TSV loaders retain separate validation/error contexts. |
| `94903af450e8d457` | FileBackedFoodOptionRepository.java:writeFoodOptions; FileBackedStageRepository.java:writeStages | Food/stage TSV writers have distinct schema ownership. |
| `dd847743fd98aefa` | FileBackedRatingRepository.java:loadRatingsByKey; FileBackedStageDistanceRepository.java:loadDistancesByStagePair | Rating/distance keyed TSV loaders use different keys, parsing and defaults. |
| `b9f6b2c13d3261bb` | SyncedBandRepository.java:replaceAll; SyncedFoodOptionRepository.java:replaceAll; SyncedPerformanceRepository.java:replaceAll; SyncedStageDistanceRepository.java:replaceAll; SyncedStageRepository.java:replaceAll | Typed cache decorators preserve port-specific source/cache ordering without generic aggregate abstractions. |

## Sources and review limits

- [Original audit and comparisons](groma-audit-2026-10-20.md).
- [Helper dispositions](groma-helper-dispositions-2026-10-21.md).
- [Scanner confidence](groma-scanner-confidence-2026-10-21.md).
- [Tool comparison](architecture-tool-assessment-2026-10-21.md).
- [Reviewed machine baseline](../../tools/groma/architecture-baseline.json).

The automated validator protects reviewed knowledge, not the truth of arbitrary
prose or diagrams. Human source/composition and rendered-browser review remain
required when architecture meaning changes. TASK241 retains application, native,
database and upgrade validation; this gate makes no new live authorization claim.

# Rating architecture island review — 21 October 2026

TASK-229 addresses the rating/archive/correction/sync/export islands shown in the screenshot. The pre-change current map had47 disconnected components. Source and composition review showed these rating records are used; no rating production source was deleted. Missing authored relationships and file-shaped modelling caused the isolation. Java scanner evidence does not resolve ordinary calls, generated Room queries or all supplied callbacks.

## Responsibility disposition

| Surviving ID | Responsibility | Group |
| --- | --- | --- |
| `activefestivalafterratingrepository` | Active festival after-rating compatibility: Reads only the current active festival. | Festival rating context |
| `festivalafterratingrepository` | Festival after-rating contract: Stores member and festival assessments. | Festival rating context |
| `personalbandratingrepository` | Established personal band opinion contract: Reads archive-established opinions. | Festival rating context |
| `viewbandratingcontextusecase` | Selected festival rating context: Separates established and festival values. | Festival rating context |
| `festivalplanningratingview` | Selected festival planning view: Keeps detail edits festival-bound. | Festival rating context |
| `festivalafterratingview` | Selected festival after-rating view: Reads one explicit festival. | Festival rating context |
| `recordfestivalafterratingusecase` | Record festival after-rating: Validates and records explicit assessments. | Festival rating context |
| `ratingstatesync` | After-rating state synchronization: Pushes pending and pulls own snapshots. | Rating synchronization |
| `ratingstatesyncstore` | Rating sync cache port: Preserves pending operations during merge. | Rating synchronization |
| `roomfestivalafterratingrepository` | Room festival after-rating cache: Persists pending assessments and durable resets. | Rating synchronization |
| `roompersonalbandratingrepository` | Room established opinion reader: Maps retained established provenance. | Rating synchronization |
| `roomratingstatedao` | Room rating state queries: Separates pending after-state and established snapshots. | Rating synchronization |
| `supabaseratingstateclient` | Authenticated rating state client: Saves operations and reads own state. | Rating synchronization |
| `archivefestivalaction` | Archive screen worker: Runs supplied archive command. | Festival archive and correction |
| `festivalarchivegateway` | Atomic festival archive port: Commits archive and promotion. | Festival archive and correction |
| `supabasefestivalarchivegateway` | Authenticated festival archive gateway: Validates archive before cache publication. | Festival archive and correction |
| `roomfestivalarchivecache` | Room archive publication: Publishes acknowledged archive atomically. | Festival archive and correction |
| `archivedratingcorrectiondialog` | Reviewed archived correction dialog: Confirms own reviewed edits. | Festival archive and correction |
| `archivedratingcorrectionusecase` | Correct own archived rating: Validates target and reviewed ownership. | Festival archive and correction |
| `archivedratingcorrectiongateway` | Archived correction review contract: Carries reviewed target and revision. | Festival archive and correction |
| `supabasearchivedratingcorrectiongateway` | Authenticated correction gateway: Reviews and commits audited corrections. | Festival archive and correction |
| `roomarchivedratingcorrectioncache` | Room correction publication: Preserves newer pending edits. | Festival archive and correction |
| `ratingcorrectionrepository` | Correction audit contract: Retains immutable change history. | Festival archive and correction |
| `roomratingcorrectionrepository` | Room correction audit cache: Stores acknowledged immutable audits. | Festival archive and correction |
| `recoverpendingplanningratingusecase` | Recover orphaned pending planning rating: Requires explicit safe recovery. | Pending rating recovery and export |
| `missingfestivalplanningratingexception` | Missing festival pending context: Carries retained pending rating. | Pending rating recovery and export |
| `missingfestivalratingdialog` | Review orphaned pending rating: Offers keep move or discard. | Pending rating recovery and export |
| `exportratinglifecyclecsvusecase` | Own rating lifecycle CSV export: Exports separate rating roles. | Pending rating recovery and export |
| `exportratingcorrectionscsvusecase` | Own correction audit CSV export: Exports original and correction dates. | Pending rating recovery and export |

## Consolidations

Only map ownership was combined; Java layers and runtime contracts remain unchanged. Values form their protocol and persistence rows/DAO serve their adapter. Shared RoomRatingStateDao stays separate because both after-state and established readers use it.

- `festivalafterrating` → `festivalafterratingrepository`.
- `personalbandrating` → `personalbandratingrepository`.
- `archivedratingkind`, `archivedratingreview` → `archivedratingcorrectiongateway`.
- `bandratingcontext` → `viewbandratingcontextusecase`.
- `ratingcorrection` → `ratingcorrectionrepository`.
- `roomfestivalafterrating` → `roomfestivalafterratingrepository`.
- `roompersonalbandrating` → `roompersonalbandratingrepository`.
- `roomratingcorrection`, `roomratingcorrectiondao` → `roomratingcorrectionrepository`.

## Verified interactions and workflows

Exact source endpoints were checked against BandDetailActivity, ArchivedBandDetailActivity, MainActivity, SettingsActivity, AppRepositories, application use cases/ports, Room adapters and Supabase clients. Composition-provided Supplier, gateway lambda, request callback and remote/cache dispatch are explicitly labelled; these are not inferred merely from imports. The shared CSV input/result component is also connected to its active reviewed use case, and canonical Band is connected to its context resolver.

- **Edit selected festival planning:** The attendee edits planning for the selected active festival. The detail view stays pinned even if the active festival changes; it validates activity and lineup before delegating to the configured pending-capable planning repository. Planning never changes after-state or established opinions.
- **Record and sync festival after-rating:** The attendee records or resets after-state for one member/festival/band. Zero is durable reset, missing is absence; neither promotes an established opinion. Room saves pending operation and nonzero dated history atomically. A later AppRepositories sync pushes explicit operations, skips uncertain legacy facts, acknowledges exact operations and merges complete own snapshots while preserving pending edits.
- **Archive and establish personal opinions:** MainActivity runs the supplied archive use case on a worker. AppRepositories gateway callback first syncs pending ratings and locks and refuses unresolved work. V016 atomically archives and promotes each member final nonempty after-rating; missing/reset retain previous established opinions. After validated acknowledgement, own complete snapshots and festival status publish together under a fresh pending guard. Other-device unsent edits remain pending and require explicit archived correction, rather than silent overwrite.
- **Correct own archived assessment:** The attendee reviews an exact own archived target/revision, explicitly confirms planning zero-to-five or after one-to-five, and submits a stable operation identity. V017 commits immutable audit and only updates established opinion if this festival is still its current source; planning or older-source changes never promote. Stale reviews require reload. Original recorded date and correction time remain distinct. Newer local pending work blocks cache publication; uncertain remote outcomes retry the same operation.
- **Review orphaned pending planning:** A sync error carries the exact orphaned pending planning fact. The user may keep it, explicitly move it to active Wacken2026 only when no local or remote target exists, or separately confirm discard. Owner and unchanged-fact checks prevent stale recovery; all failures preserve pending work.
- **Export own lifecycle and correction audit:** Settings exposes legacy ratings CSV, lifecycle version2 and correction audit as separate actions/contracts. Lifecycle records retain established provenance, exact festival planning/after-state, history and audit, preserving missing versus zero and unknown dates. Audit export separates original recorded and correction times; neither export chooses a latest event as an established rating.

## Limits and verification

SQL V014/V016/V017 and ADR0016 support the RPC and ownership descriptions; the Java scanner does not inspect SQL or prove deployed authorization. No live database changes or new security behavior were introduced. Existing TASK241 release gates retain runtime/upgrade responsibility. Authored stale arrows to the removed full CSV importer remain the separately documented TASK228 tooling blocker; reducing islands does not resolve those stale rows.

Eight CLI combines absorb10 file-shaped records, leaving96 components and the same294 unique existing source files. After reviewed collaborations, only six music-discovery components remain disconnected, owned by TASK230. Six new scenario flows preserve the five existing flows. Two scans create0/refreshed96/findings88. All changed architecture is written through the pinned CLI.


# Festival administration architecture evidence

TASK-228 describes existing behavior after TASK-227 restored runtime containment.
No application, schema, policy, network implementation or test-strategy change.

## Scope dispositions

All25 creation/import/identity/deletion records from the original audit AppendixA
are accounted for below. Three CLI combines preserve295 unique source files:
canonical selection/alias values share one identity contract, alias row/DAO/
repository share one cache responsibility, and marker row/DAO share cache
initialization. Independently meaningful ports, orchestration, UI and adapters
remain separate. These are components inside Android, not new runtimes.
TASK-227 references were updated through Backlog CLI for the five replaced IDs.

| Original record | Current owner | Disposition |
| --- | --- | --- |
| addfestivalaction | addfestivalaction | Festival creation worker; retained independent responsibility |
| canonicalband | canonicalband | Reviewed canonical band identity; retained independent responsibility |
| confirmedbandalias | canonicalband | Reviewed canonical band identity; combined implementation/value contract |
| deletefestivaldialog | deletefestivaldialog | Festival deletion confirmation; retained independent responsibility |
| deletefestivalusecase | deletefestivalusecase | Festival deletion use case; retained independent responsibility |
| festivalbandcatalog | festivalbandcatalog | Festival identity review port; retained independent responsibility |
| festivalbandreviewdialog | festivalbandreviewdialog | Festival identity confirmation; retained independent responsibility |
| festivalcreationgateway | festivalcreationgateway | Atomic festival creation port; retained independent responsibility |
| festivalcsvreader | festivalcsvreader | Festival CSV parsing; retained independent responsibility |
| festivalscopedperformancerepository | festivalscopedperformancerepository | Festival-scoped performance view; retained independent responsibility |
| localcsvimportreviewdialog | localcsvimportreviewdialog | Local CSV acceptance review; retained independent responsibility |
| reviewedfestivalband | canonicalband | Reviewed canonical band identity; combined implementation/value contract |
| reviewfestivalbandsusecase | reviewfestivalbandsusecase | Festival identity proposals; retained independent responsibility |
| reviewfestivalcsvusecase | reviewfestivalcsvusecase | Local five-file import review; retained independent responsibility |
| roombandalias | roombandaliasrepository | Confirmed alias cache; combined implementation/value contract |
| roombandaliasdao | roombandaliasrepository | Confirmed alias cache; combined implementation/value contract |
| roombandaliasrepository | roombandaliasrepository | Confirmed alias cache; retained independent responsibility |
| roomfestivalcachemarker | roomfestivalcachemarker | Festival cache initialization marker; retained independent responsibility |
| roomfestivalcachemarkerdao | roomfestivalcachemarker | Festival cache initialization marker; combined implementation/value contract |
| roomfestivalcreationcache | roomfestivalcreationcache | Acknowledged creation cache; retained independent responsibility |
| roomfestivaldeletion | roomfestivaldeletion | Festival cache deletion; retained independent responsibility |
| supabasefestivalbandcatalog | supabasefestivalbandcatalog | Remote identity review catalog; retained independent responsibility |
| supabasefestivalcreationgateway | supabasefestivalcreationgateway | Shared festival creation adapter; retained independent responsibility |
| supabasefestivaldeletiongateway | supabasefestivaldeletiongateway | Shared festival deletion adapter; retained independent responsibility |
| tsvimporttransaction | tsvimporttransaction | TSV and Room import transaction; retained independent responsibility |

Creation/identity, local import and deletion use separate sibling groups.
Festival performance scoping and cache initialization retain their own explained
responsibilities. Future rating curation can use these existing owners without
moving their Java code into a storage container.

## Source evidence index

- screen: [ImportCsvActivity.java](../../app/src/main/java/be/wacken/planner/ImportCsvActivity.java)
- home: [MainActivity.java](../../app/src/main/java/be/wacken/planner/MainActivity.java)
- archive: [ArchivedFestivalActivity.java](../../app/src/main/java/be/wacken/planner/ArchivedFestivalActivity.java)
- composition: [AppRepositories.java](../../app/src/main/java/be/wacken/planner/AppRepositories.java)
- proposal: [ReviewFestivalBandsUseCase.java](../../application/src/main/java/be/wacken/planner/application/ReviewFestivalBandsUseCase.java)
- catalog: [FestivalBandCatalog.java](../../application/src/main/java/be/wacken/planner/application/FestivalBandCatalog.java)
- remoteCatalog: [SupabaseFestivalBandCatalog.java](../../app/src/main/java/be/wacken/planner/SupabaseFestivalBandCatalog.java)
- identity: [CanonicalBand.java](../../domain/src/main/java/be/wacken/planner/domain/CanonicalBand.java)
- reviewed: [ReviewedFestivalBand.java](../../domain/src/main/java/be/wacken/planner/domain/ReviewedFestivalBand.java)
- aliases: [ConfirmedBandAlias.java](../../domain/src/main/java/be/wacken/planner/domain/ConfirmedBandAlias.java)
- dialog: [FestivalBandReviewDialog.java](../../app/src/main/java/be/wacken/planner/FestivalBandReviewDialog.java)
- worker: [AddFestivalAction.java](../../app/src/main/java/be/wacken/planner/AddFestivalAction.java)
- create: [AddFestivalUseCase.java](../../application/src/main/java/be/wacken/planner/application/AddFestivalUseCase.java)
- createPort: [FestivalCreationGateway.java](../../application/src/main/java/be/wacken/planner/application/FestivalCreationGateway.java)
- remoteCreate: [SupabaseFestivalCreationGateway.java](../../app/src/main/java/be/wacken/planner/SupabaseFestivalCreationGateway.java)
- http: [SupabaseMasterDataClient.java](../../app/src/main/java/be/wacken/planner/SupabaseMasterDataClient.java)
- cache: [RoomFestivalCreationCache.java](../../app/src/main/java/be/wacken/planner/RoomFestivalCreationCache.java)
- db: [WackenDatabase.java](../../app/src/main/java/be/wacken/planner/persistence/WackenDatabase.java)
- aliasCache: [RoomBandAliasRepository.java](../../app/src/main/java/be/wacken/planner/persistence/RoomBandAliasRepository.java)
- localDialog: [LocalCsvImportReviewDialog.java](../../app/src/main/java/be/wacken/planner/LocalCsvImportReviewDialog.java)
- local: [ReviewFestivalCsvUseCase.java](../../application/src/main/java/be/wacken/planner/application/ReviewFestivalCsvUseCase.java)
- transaction: [TsvImportTransaction.java](../../infrastructure/src/main/java/be/wacken/planner/infrastructure/TsvImportTransaction.java)
- parser: [FestivalCsvReader.java](../../application/src/main/java/be/wacken/planner/application/FestivalCsvReader.java)
- decorator: [SyncedBandRepository.java](../../infrastructure/src/main/java/be/wacken/planner/infrastructure/SyncedBandRepository.java)
- files: [FileBackedBandRepository.java](../../infrastructure/src/main/java/be/wacken/planner/infrastructure/FileBackedBandRepository.java)
- room: [RoomBandRepository.java](../../app/src/main/java/be/wacken/planner/persistence/RoomBandRepository.java)
- deleteDialog: [DeleteFestivalDialog.java](../../app/src/main/java/be/wacken/planner/DeleteFestivalDialog.java)
- delete: [DeleteFestivalUseCase.java](../../application/src/main/java/be/wacken/planner/application/DeleteFestivalUseCase.java)
- remoteDelete: [SupabaseFestivalDeletionGateway.java](../../app/src/main/java/be/wacken/planner/SupabaseFestivalDeletionGateway.java)
- deleteCache: [RoomFestivalDeletion.java](../../app/src/main/java/be/wacken/planner/persistence/RoomFestivalDeletion.java)
- festivalStore: [RoomFestivalRepository.java](../../app/src/main/java/be/wacken/planner/persistence/RoomFestivalRepository.java)
- marker: [RoomFestivalCacheMarkerDao.java](../../app/src/main/java/be/wacken/planner/persistence/RoomFestivalCacheMarkerDao.java)
- scoped: [FestivalScopedPerformanceRepository.java](../../app/src/main/java/be/wacken/planner/FestivalScopedPerformanceRepository.java)
- performances: [SyncedPerformanceRepository.java](../../infrastructure/src/main/java/be/wacken/planner/infrastructure/SyncedPerformanceRepository.java)

The names below abbreviate these exact endpoints. Relationships are authored
from method calls, instantiated values or explicitly bound runtime callbacks.
They are not inferred solely from imports. In particular, ImportCsvActivity
binds the creation Supplier and confirmation Consumer; AppRepositories binds
creation/deletion cache Consumers, the TSV/Room transaction and concrete
implementations of application ports.

| Source → target | Collaboration |
| --- | --- |
| screen → proposal | Builds identity proposals |
| proposal → catalog | Loads canonical review data |
| composition → remoteCatalog | Supplies fresh review catalog |
| remoteCatalog → http | Fetches canonical identity snapshots |
| remoteCatalog → aliasCache | Publishes confirmed alias cache |
| remoteCatalog → identity | Builds stable catalog identities |
| proposal → aliases | Matches confirmed alias keys |
| screen → dialog | Requests identity confirmation |
| dialog → reviewed | Captures explicit identity choices |
| dialog → screen | Returns confirmed creation choices |
| screen → worker | Dispatches reviewed creation |
| worker → create | Runs supplied creation command |
| create → createPort | Invokes atomic creation port |
| create → remoteCreate | Invokes configured creation adapter |
| composition → remoteCreate | Supplies group creation adapter |
| remoteCreate → http | Submits atomic reviewed creation |
| remoteCreate → cache | Publishes acknowledged creation |
| composition → cache | Binds creation cache callback |
| cache → db | Commits acknowledged creation cache |
| cache → aliasCache | Stores acknowledged alias snapshot |
| aliasCache → db | Transacts confirmed alias cache |
| aliasCache → aliases | Restores confirmed alias provenance |
| screen → localDialog | Opens local row review |
| localDialog → local | Reviews and applies choices |
| composition → local | Supplies local import scope |
| local → parser | Parses selected CSV rows |
| screen → parser | Reads uploaded lineup names |
| composition → transaction | Binds rollback and Room transaction |
| local → transaction | Runs supplied local transaction |
| transaction → db | Invokes supplied Room transaction |
| local → decorator | Publishes accepted master data |
| decorator → files | Writes selected TSV source |
| decorator → room | Publishes local cached bands |
| home → deleteDialog | Confirms active festival deletion |
| archive → deleteDialog | Confirms archived festival deletion |
| deleteDialog → composition | Requests selected festival deletion |
| composition → delete | Supplies deletion workflow |
| delete → remoteDelete | Invokes configured deletion gateway |
| remoteDelete → http | Deletes shared festival first |
| remoteDelete → deleteCache | Clears acknowledged local festival |
| composition → deleteCache | Binds deletion cache callback |
| deleteCache → db | Transacts scoped cache deletion |
| deleteCache → marker | Marks empty cache initialized |
| festivalStore → marker | Guards initial festival seeding |
| composition → scoped | Supplies selected festival view |
| scoped → performances | Filters configured performance source |

## Reviewed group-wide creation

The create-reviewed-group-festival flow traces selection, fresh catalog review,
explicit nonexact identity/final confirmation, worker command, application port,
configured gateway, authenticated RPC, owned database transaction, acknowledged
snapshot and one Room publication. V018 delegates to the existing V015 atomic
creation function. Shared canonical metadata, complete lineup, confirmed alias
choices and once-only per-member established-rating planning seeds commit
together. After-ratings begin empty. The response contains only the creator's
planning data. Group/member/festival/lineup acknowledgement is validated.
Conflicting identities/aliases require fresh review; cancel writes nothing.
A remote commit followed by cache failure is reported distinctly and retried
with the same command identity without resetting later edits.

## Local five-file review

The existing import-festival-csv flow ID now describes the current reviewed
local path. Accepted changes are explicit, invalid rows cannot be accepted,
missing companion files retain baseline, combined references/overlaps are
validated and stale snapshots are rejected inside the supplied transaction.
Canonical bands, metadata, other festivals, ratings/history and pending work
remain. TSV backups accompany the Room transaction; failure restores files,
and failed restoration retains recovery evidence. No shared-source CSV import
API is implied; later sync may refresh local master data.

The legacy ImportFestivalCsvUseCase remains a separate direct application API
with its existing code ownership, rather than being presented as the current
Android screen flow.

## Deletion and cache scope

The delete-selected-group-festival flow starts at active home; the archived
screen has the same dialog collaboration. Explicit confirmation precedes
application selection and authenticated remote deletion/absence verification.
Only acknowledged remote success invokes local cleanup. Room detaches retained
history provenance, removes the selected festival's state and marks the cache
initialized. Canonical catalog and established personal band ratings remain.
The initialization marker prevents an intentionally empty cache being reseeded.
Remote failure retains cache; post-commit local failure reports sync recovery.
FestivalScopedPerformanceRepository separately filters the chosen festival,
rejects cross-festival writes and refuses global replacement.

## Backend coverage and pending correction

SQL V011/V015/V018 and adapter/composition code support the backend descriptions.
The Java scanner does not inspect SQL/RLS, prove production authorization or
execute remote flows. Existing runtime/database verification belongs to
TASK-241; no new production permission review is claimed here.

A verified stale authored edge from ImportCsvActivity to the legacy
ImportFestivalCsvUseCase remains pending the narrow tooling extension in
[the approval proposal](task-228-stale-relationship-approval.md).
After correcting the local flow, the CLI still reports
`only draft relationships can be removed`. An unused Java import is not proof
of an active collaboration. TASK-228 remains incomplete until that correction
can be made through an approved CLI operation.

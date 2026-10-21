---
id: TASK-227
title: 'DOC: Restore truthful runtime and data boundaries in the architecture map'
status: Done
assignee:
  - '@codex'
created_date: '2026-10-20 06:25'
updated_date: '2026-10-21 10:16'
labels:
  - groma-audit
  - release-2.35
milestone: m-3
dependencies:
  - TASK-226
references:
  - wacken
  - apprepositories
  - wackendatabase
  - android-application
  - on-device-cache
  - shared-planner-database
  - activefestivalafterratingrepository
  - addfestivalaction
  - addfestivalusecase
  - archivedfestivalactivity
  - archivedratingcorrectiondialog
  - archivedratingcorrectiongateway
  - archivedratingcorrectionusecase
  - archivedratingkind
  - archivedratingreview
  - archivefestivalaction
  - authsessionstore
  - band
  - banddetailactivity
  - bandimageloader
  - bandlinkreviewactivity
  - bandmetadatareviewactivity
  - bandratingcontext
  - bandrepository
  - canonicalband
  - deletefestivaldialog
  - deletefestivalusecase
  - exportratingcorrectionscsvusecase
  - exportratinglifecyclecsvusecase
  - exportratingscsvusecase
  - festival
  - festivalafterrating
  - festivalafterratingrepository
  - festivalafterratingview
  - festivalarchivegateway
  - festivalbandcatalog
  - festivalbandreviewdialog
  - festivalcreationgateway
  - festivalcsvreader
  - festivalplanningratingview
  - festivalscopedperformancerepository
  - filebackedstorage
  - findbandmusiclinksusecase
  - generatesharedscheduleusecase
  - importcsvactivity
  - importfestivalcsvusecase
  - listactivefestivalbandsusecase
  - localcsvimportreviewdialog
  - loginactivity
  - mainactivity
  - memberratingprojection
  - missingfestivalplanningratingexception
  - missingfestivalratingdialog
  - musicbrainzmetadataprovider
  - musiclinkevidence
  - musiclinklookupprovider
  - musiclinksreviewactivity
  - officialmusicpageclient
  - performance
  - performanceconflictresolver
  - personalbandrating
  - personalbandratingrepository
  - ratebandusecase
  - rating
  - ratingcorrection
  - ratingcorrectionrepository
  - ratingstatesync
  - ratingstatesyncstore
  - recordfestivalafterratingusecase
  - recoverpendingplanningratingusecase
  - reviewfestivalbandsusecase
  - reviewfestivalcsvusecase
  - reviewimportedbandlinksusecase
  - roomarchivedratingcorrectioncache
  - roombandaliasrepository
  - roomfestivalafterrating
  - roomfestivalafterratingrepository
  - roomfestivalarchivecache
  - roomfestivalcachemarker
  - roomfestivalcreationcache
  - roomfestivaldeletion
  - roompersonalbandrating
  - roompersonalbandratingrepository
  - roomratingcorrection
  - roomratingcorrectiondao
  - roomratingcorrectionrepository
  - roomratingstatedao
  - scheduleactivity
  - searchbandmetadatausecase
  - settingsactivity
  - spotifymetadataprovider
  - supabasearchivedratingcorrectiongateway
  - supabaseauthclient
  - supabaseauthenticatedrequest
  - supabasefestivalarchivegateway
  - supabasefestivalbandcatalog
  - supabasefestivalcreationgateway
  - supabasefestivaldeletiongateway
  - supabasemasterdataclient
  - supabaseratingclient
  - supabaseratingstateclient
  - syncedbandrepository
  - syncingratingrepository
  - tsvimporttransaction
  - verifiedmusiclinksprovider
  - viewbandratingcontextusecase
  - wackentheme
  - wikidatametadataprovider
  - wikipediametadataprovider
  - youtubemetadataprovider
  - supabase
documentation:
  - backlog/docs/groma-audit-2026-10-20.md
modified_files:
  - backlog/docs/groma-container-limitations-2026-10-20.md
  - README.md
  - tools/groma/container-fix.patch
  - tools/groma/pin.json
  - scripts/setup-groma.sh
  - scripts/groma.sh
  - backlog/decisions/0021-pinned-groma-container-curation-fix.md
  - groma/systems/wacken/containers/android-application/container.md
  - groma/systems/wacken/containers/on-device-cache/container.md
  - groma/systems/wacken/containers/shared-planner-database/container.md
  - >-
    groma/systems/wacken/containers/android-application/components/activefestivalafterratingrepository.md
  - groma/systems/wacken/components/activefestivalafterratingrepository.md
  - >-
    groma/systems/wacken/containers/android-application/components/addfestivalaction.md
  - groma/systems/wacken/components/addfestivalaction.md
  - >-
    groma/systems/wacken/containers/android-application/components/addfestivalusecase.md
  - groma/systems/wacken/components/addfestivalusecase.md
  - >-
    groma/systems/wacken/containers/android-application/components/apprepositories.md
  - groma/systems/wacken/components/apprepositories.md
  - >-
    groma/systems/wacken/containers/android-application/components/archivedfestivalactivity.md
  - groma/systems/wacken/components/archivedfestivalactivity.md
  - >-
    groma/systems/wacken/containers/android-application/components/archivedratingcorrectiondialog.md
  - groma/systems/wacken/components/archivedratingcorrectiondialog.md
  - >-
    groma/systems/wacken/containers/android-application/components/archivedratingcorrectiongateway.md
  - groma/systems/wacken/components/archivedratingcorrectiongateway.md
  - >-
    groma/systems/wacken/containers/android-application/components/archivedratingcorrectionusecase.md
  - groma/systems/wacken/components/archivedratingcorrectionusecase.md
  - >-
    groma/systems/wacken/containers/android-application/components/archivedratingkind.md
  - groma/systems/wacken/components/archivedratingkind.md
  - >-
    groma/systems/wacken/containers/android-application/components/archivedratingreview.md
  - groma/systems/wacken/components/archivedratingreview.md
  - >-
    groma/systems/wacken/containers/android-application/components/archivefestivalaction.md
  - groma/systems/wacken/components/archivefestivalaction.md
  - >-
    groma/systems/wacken/containers/android-application/components/authsessionstore.md
  - groma/systems/wacken/components/authsessionstore.md
  - groma/systems/wacken/containers/android-application/components/band.md
  - groma/systems/wacken/components/band.md
  - >-
    groma/systems/wacken/containers/android-application/components/banddetailactivity.md
  - groma/systems/wacken/components/banddetailactivity.md
  - >-
    groma/systems/wacken/containers/android-application/components/bandimageloader.md
  - groma/systems/wacken/components/bandimageloader.md
  - >-
    groma/systems/wacken/containers/android-application/components/bandlinkreviewactivity.md
  - groma/systems/wacken/components/bandlinkreviewactivity.md
  - >-
    groma/systems/wacken/containers/android-application/components/bandmetadatareviewactivity.md
  - groma/systems/wacken/components/bandmetadatareviewactivity.md
  - >-
    groma/systems/wacken/containers/android-application/components/bandratingcontext.md
  - groma/systems/wacken/components/bandratingcontext.md
  - >-
    groma/systems/wacken/containers/android-application/components/bandrepository.md
  - groma/systems/wacken/components/bandrepository.md
  - >-
    groma/systems/wacken/containers/android-application/components/canonicalband.md
  - groma/systems/wacken/components/canonicalband.md
  - >-
    groma/systems/wacken/containers/android-application/components/confirmedbandalias.md
  - groma/systems/wacken/components/confirmedbandalias.md
  - >-
    groma/systems/wacken/containers/android-application/components/deletefestivaldialog.md
  - groma/systems/wacken/components/deletefestivaldialog.md
  - >-
    groma/systems/wacken/containers/android-application/components/deletefestivalusecase.md
  - groma/systems/wacken/components/deletefestivalusecase.md
  - >-
    groma/systems/wacken/containers/android-application/components/exportratingcorrectionscsvusecase.md
  - groma/systems/wacken/components/exportratingcorrectionscsvusecase.md
  - >-
    groma/systems/wacken/containers/android-application/components/exportratinglifecyclecsvusecase.md
  - groma/systems/wacken/components/exportratinglifecyclecsvusecase.md
  - >-
    groma/systems/wacken/containers/android-application/components/exportratingscsvusecase.md
  - groma/systems/wacken/components/exportratingscsvusecase.md
  - groma/systems/wacken/containers/android-application/components/festival.md
  - groma/systems/wacken/components/festival.md
  - >-
    groma/systems/wacken/containers/android-application/components/festivalafterrating.md
  - groma/systems/wacken/components/festivalafterrating.md
  - >-
    groma/systems/wacken/containers/android-application/components/festivalafterratingrepository.md
  - groma/systems/wacken/components/festivalafterratingrepository.md
  - >-
    groma/systems/wacken/containers/android-application/components/festivalafterratingview.md
  - groma/systems/wacken/components/festivalafterratingview.md
  - >-
    groma/systems/wacken/containers/android-application/components/festivalarchivegateway.md
  - groma/systems/wacken/components/festivalarchivegateway.md
  - >-
    groma/systems/wacken/containers/android-application/components/festivalbandcatalog.md
  - groma/systems/wacken/components/festivalbandcatalog.md
  - >-
    groma/systems/wacken/containers/android-application/components/festivalbandreviewdialog.md
  - groma/systems/wacken/components/festivalbandreviewdialog.md
  - >-
    groma/systems/wacken/containers/android-application/components/festivalcreationgateway.md
  - groma/systems/wacken/components/festivalcreationgateway.md
  - >-
    groma/systems/wacken/containers/android-application/components/festivalcsvreader.md
  - groma/systems/wacken/components/festivalcsvreader.md
  - >-
    groma/systems/wacken/containers/android-application/components/festivalplanningratingview.md
  - groma/systems/wacken/components/festivalplanningratingview.md
  - >-
    groma/systems/wacken/containers/android-application/components/festivalscopedperformancerepository.md
  - groma/systems/wacken/components/festivalscopedperformancerepository.md
  - >-
    groma/systems/wacken/containers/android-application/components/filebackedstorage.md
  - groma/systems/wacken/components/filebackedstorage.md
  - >-
    groma/systems/wacken/containers/android-application/components/findbandmusiclinksusecase.md
  - groma/systems/wacken/components/findbandmusiclinksusecase.md
  - >-
    groma/systems/wacken/containers/android-application/components/generatesharedscheduleusecase.md
  - groma/flows/build-the-shared-calendar.md
  - groma/systems/wacken/components/generatesharedscheduleusecase.md
  - >-
    groma/systems/wacken/containers/android-application/components/importcsvactivity.md
  - groma/flows/import-festival-csv.md
  - groma/systems/wacken/components/importcsvactivity.md
  - >-
    groma/systems/wacken/containers/android-application/components/importfestivalcsvusecase.md
  - groma/systems/wacken/components/importfestivalcsvusecase.md
  - >-
    groma/systems/wacken/containers/android-application/components/listactivefestivalbandsusecase.md
  - groma/systems/wacken/components/listactivefestivalbandsusecase.md
  - >-
    groma/systems/wacken/containers/android-application/components/localcsvimportreviewdialog.md
  - groma/systems/wacken/components/localcsvimportreviewdialog.md
  - >-
    groma/systems/wacken/containers/android-application/components/loginactivity.md
  - groma/systems/wacken/components/loginactivity.md
  - >-
    groma/systems/wacken/containers/android-application/components/mainactivity.md
  - groma/flows/rate-a-festival-band.md
  - groma/systems/wacken/components/mainactivity.md
  - >-
    groma/systems/wacken/containers/android-application/components/memberratingprojection.md
  - groma/systems/wacken/components/memberratingprojection.md
  - >-
    groma/systems/wacken/containers/android-application/components/missingfestivalplanningratingexception.md
  - groma/systems/wacken/components/missingfestivalplanningratingexception.md
  - >-
    groma/systems/wacken/containers/android-application/components/missingfestivalratingdialog.md
  - groma/systems/wacken/components/missingfestivalratingdialog.md
  - >-
    groma/systems/wacken/containers/android-application/components/musicbrainzmetadataprovider.md
  - groma/systems/wacken/components/musicbrainzmetadataprovider.md
  - >-
    groma/systems/wacken/containers/android-application/components/musiclinkevidence.md
  - groma/systems/wacken/components/musiclinkevidence.md
  - >-
    groma/systems/wacken/containers/android-application/components/musiclinklookupprovider.md
  - groma/systems/wacken/components/musiclinklookupprovider.md
  - >-
    groma/systems/wacken/containers/android-application/components/musiclinksreviewactivity.md
  - groma/systems/wacken/components/musiclinksreviewactivity.md
  - >-
    groma/systems/wacken/containers/android-application/components/officialmusicpageclient.md
  - groma/systems/wacken/components/officialmusicpageclient.md
  - >-
    groma/systems/wacken/containers/android-application/components/performance.md
  - groma/systems/wacken/components/performance.md
  - >-
    groma/systems/wacken/containers/android-application/components/performanceconflictresolver.md
  - groma/systems/wacken/components/performanceconflictresolver.md
  - >-
    groma/systems/wacken/containers/android-application/components/personalbandrating.md
  - groma/systems/wacken/components/personalbandrating.md
  - >-
    groma/systems/wacken/containers/android-application/components/personalbandratingrepository.md
  - groma/systems/wacken/components/personalbandratingrepository.md
  - >-
    groma/systems/wacken/containers/android-application/components/ratebandusecase.md
  - groma/systems/wacken/components/ratebandusecase.md
  - groma/systems/wacken/containers/android-application/components/rating.md
  - groma/systems/wacken/components/rating.md
  - >-
    groma/systems/wacken/containers/android-application/components/ratingcorrection.md
  - groma/systems/wacken/components/ratingcorrection.md
  - >-
    groma/systems/wacken/containers/android-application/components/ratingcorrectionrepository.md
  - groma/systems/wacken/components/ratingcorrectionrepository.md
  - >-
    groma/systems/wacken/containers/android-application/components/ratingstatesync.md
  - groma/systems/wacken/components/ratingstatesync.md
  - >-
    groma/systems/wacken/containers/android-application/components/ratingstatesyncstore.md
  - groma/systems/wacken/components/ratingstatesyncstore.md
  - >-
    groma/systems/wacken/containers/android-application/components/recordfestivalafterratingusecase.md
  - groma/systems/wacken/components/recordfestivalafterratingusecase.md
  - >-
    groma/systems/wacken/containers/android-application/components/recoverpendingplanningratingusecase.md
  - groma/systems/wacken/components/recoverpendingplanningratingusecase.md
  - >-
    groma/systems/wacken/containers/android-application/components/reviewedfestivalband.md
  - groma/systems/wacken/components/reviewedfestivalband.md
  - >-
    groma/systems/wacken/containers/android-application/components/reviewfestivalbandsusecase.md
  - groma/systems/wacken/components/reviewfestivalbandsusecase.md
  - >-
    groma/systems/wacken/containers/android-application/components/reviewfestivalcsvusecase.md
  - groma/systems/wacken/components/reviewfestivalcsvusecase.md
  - >-
    groma/systems/wacken/containers/android-application/components/reviewimportedbandlinksusecase.md
  - groma/systems/wacken/components/reviewimportedbandlinksusecase.md
  - >-
    groma/systems/wacken/containers/android-application/components/roomarchivedratingcorrectioncache.md
  - groma/systems/wacken/components/roomarchivedratingcorrectioncache.md
  - >-
    groma/systems/wacken/containers/android-application/components/roombandalias.md
  - groma/systems/wacken/components/roombandalias.md
  - >-
    groma/systems/wacken/containers/android-application/components/roombandaliasdao.md
  - groma/systems/wacken/components/roombandaliasdao.md
  - >-
    groma/systems/wacken/containers/android-application/components/roombandaliasrepository.md
  - groma/systems/wacken/components/roombandaliasrepository.md
  - >-
    groma/systems/wacken/containers/android-application/components/roomfestivalafterrating.md
  - groma/systems/wacken/components/roomfestivalafterrating.md
  - >-
    groma/systems/wacken/containers/android-application/components/roomfestivalafterratingrepository.md
  - groma/systems/wacken/components/roomfestivalafterratingrepository.md
  - >-
    groma/systems/wacken/containers/android-application/components/roomfestivalarchivecache.md
  - groma/systems/wacken/components/roomfestivalarchivecache.md
  - >-
    groma/systems/wacken/containers/android-application/components/roomfestivalcachemarker.md
  - groma/systems/wacken/components/roomfestivalcachemarker.md
  - >-
    groma/systems/wacken/containers/android-application/components/roomfestivalcachemarkerdao.md
  - groma/systems/wacken/components/roomfestivalcachemarkerdao.md
  - >-
    groma/systems/wacken/containers/android-application/components/roomfestivalcreationcache.md
  - groma/systems/wacken/components/roomfestivalcreationcache.md
  - >-
    groma/systems/wacken/containers/android-application/components/roomfestivaldeletion.md
  - groma/systems/wacken/components/roomfestivaldeletion.md
  - >-
    groma/systems/wacken/containers/android-application/components/roompersonalbandrating.md
  - groma/systems/wacken/components/roompersonalbandrating.md
  - >-
    groma/systems/wacken/containers/android-application/components/roompersonalbandratingrepository.md
  - groma/systems/wacken/components/roompersonalbandratingrepository.md
  - >-
    groma/systems/wacken/containers/android-application/components/roomratingcorrection.md
  - groma/systems/wacken/components/roomratingcorrection.md
  - >-
    groma/systems/wacken/containers/android-application/components/roomratingcorrectiondao.md
  - groma/systems/wacken/components/roomratingcorrectiondao.md
  - >-
    groma/systems/wacken/containers/android-application/components/roomratingcorrectionrepository.md
  - groma/systems/wacken/components/roomratingcorrectionrepository.md
  - >-
    groma/systems/wacken/containers/android-application/components/roomratingstatedao.md
  - groma/systems/wacken/components/roomratingstatedao.md
  - >-
    groma/systems/wacken/containers/android-application/components/scheduleactivity.md
  - groma/systems/wacken/components/scheduleactivity.md
  - >-
    groma/systems/wacken/containers/android-application/components/searchbandmetadatausecase.md
  - groma/systems/wacken/components/searchbandmetadatausecase.md
  - >-
    groma/systems/wacken/containers/android-application/components/settingsactivity.md
  - groma/systems/wacken/components/settingsactivity.md
  - >-
    groma/systems/wacken/containers/android-application/components/spotifymetadataprovider.md
  - groma/systems/wacken/components/spotifymetadataprovider.md
  - >-
    groma/systems/wacken/containers/android-application/components/supabasearchivedratingcorrectiongateway.md
  - groma/systems/wacken/components/supabasearchivedratingcorrectiongateway.md
  - >-
    groma/systems/wacken/containers/android-application/components/supabaseauthclient.md
  - groma/systems/wacken/components/supabaseauthclient.md
  - >-
    groma/systems/wacken/containers/android-application/components/supabaseauthenticatedrequest.md
  - groma/systems/wacken/components/supabaseauthenticatedrequest.md
  - >-
    groma/systems/wacken/containers/android-application/components/supabasefestivalarchivegateway.md
  - groma/systems/wacken/components/supabasefestivalarchivegateway.md
  - >-
    groma/systems/wacken/containers/android-application/components/supabasefestivalbandcatalog.md
  - groma/systems/wacken/components/supabasefestivalbandcatalog.md
  - >-
    groma/systems/wacken/containers/android-application/components/supabasefestivalcreationgateway.md
  - groma/systems/wacken/components/supabasefestivalcreationgateway.md
  - >-
    groma/systems/wacken/containers/android-application/components/supabasefestivaldeletiongateway.md
  - groma/systems/wacken/components/supabasefestivaldeletiongateway.md
  - >-
    groma/systems/wacken/containers/android-application/components/supabasemasterdataclient.md
  - groma/systems/wacken/components/supabasemasterdataclient.md
  - >-
    groma/systems/wacken/containers/android-application/components/supabaseratingclient.md
  - groma/systems/wacken/components/supabaseratingclient.md
  - >-
    groma/systems/wacken/containers/android-application/components/supabaseratingstateclient.md
  - groma/systems/wacken/components/supabaseratingstateclient.md
  - >-
    groma/systems/wacken/containers/android-application/components/syncedbandrepository.md
  - groma/systems/wacken/components/syncedbandrepository.md
  - >-
    groma/systems/wacken/containers/android-application/components/syncingratingrepository.md
  - groma/systems/wacken/components/syncingratingrepository.md
  - >-
    groma/systems/wacken/containers/android-application/components/tsvimporttransaction.md
  - groma/systems/wacken/components/tsvimporttransaction.md
  - >-
    groma/systems/wacken/containers/android-application/components/verifiedmusiclinksprovider.md
  - groma/systems/wacken/components/verifiedmusiclinksprovider.md
  - >-
    groma/systems/wacken/containers/android-application/components/viewbandratingcontextusecase.md
  - groma/systems/wacken/components/viewbandratingcontextusecase.md
  - >-
    groma/systems/wacken/containers/android-application/components/wackendatabase.md
  - groma/systems/wacken/components/wackendatabase.md
  - >-
    groma/systems/wacken/containers/android-application/components/wackentheme.md
  - groma/systems/wacken/components/wackentheme.md
  - >-
    groma/systems/wacken/containers/android-application/components/wikidatametadataprovider.md
  - groma/systems/wacken/components/wikidatametadataprovider.md
  - >-
    groma/systems/wacken/containers/android-application/components/wikipediametadataprovider.md
  - groma/systems/wacken/components/wikipediametadataprovider.md
  - >-
    groma/systems/wacken/containers/android-application/components/youtubemetadataprovider.md
  - groma/systems/wacken/components/youtubemetadataprovider.md
  - groma/externals/supabase.md
  - groma/scanners.json
  - groma/config.json
  - .gitattributes
priority: high
type: docs
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## User story
As a developer, I want runtime and storage boundaries visible so I can understand how the installed app and shared backend fit together.

## Scope
After the baseline is tracked, inspect actual runtime/storage ownership and curate the missing C4 container level using Groma. Explain Android process, local Room storage and owned data versus the external Supabase platform as supported by evidence and tool capabilities. Gradle modules and visual groups must not be treated as independent runtimes. No deployed architecture change.

## Audit reference
[Groma architecture audit — 20 October 2026](../docs/groma-audit-2026-10-20.md)
Repository path: backlog/docs/groma-audit-2026-10-20.md
Findings: F2. Investigation: TASK-225.

## Boundaries and impacts
Preserve ADR-0016: shared canonical metadata, member-specific established band rating, independent festival planning/after-ratings, one-time creation snapshots and source-aware archive promotion/correction. Follow Groma CLI-only curation and Backlog CLI-only task maintenance.
Business requirements impact: none planned unless an approved behaviour change is explicitly required; record the actual impact on completion.
README and diagram impacts are assessed for the affected workflow. ADR impact: none assumed; approval is required for significant changes.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given the rendered map, a reader can identify the internal software system, Android runtime boundary and local/shared storage ownership without interpreting groups as containers.
- [x] #2 Every existing component has truthful containment or an explicitly documented tool limitation; IDs, file ownership and useful authored meaning are preserved and no fake source file or boundary is invented.
- [x] #3 Groma CLI constraints on described-component moves are investigated safely; blocked operations are documented with a supported remedy or explicit unresolved decision, without stripping meaning or directly editing Groma Markdown.
- [x] #4 Two rescans and a fresh-checkout comparison preserve the approved structure, source links and existing flows; README and the affected architecture diagrams explain the boundaries.
- [x] #5 Architecture impact is assessed before implementation; significant boundary, dependency, persistence or test-strategy changes receive explicit human approval before coding and an ADR is recorded when required.
- [x] #6 README, business requirements, diagram and ADR impacts use canonical delivery-governance wording; test evidence, manual verification, finalized plan and remaining risks are recorded before merge.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Reproduced both published0.7.2 restrictions and obtained explicit standard-tooling approval.
2. Added red/green declaration/relocation regressions; reused rename link rewriting, retained validation and container/combine limits; updated tool contract/guides. Full upstream checks passed using supported environment controls.
3. Pinned upstream source, patch, Bun and unchanged Java scanner; added reproducible build/verified wrapper and accepted ADR0021. Fresh source build and disposable map migration preserved all semantics.
4. Declared three true containers; moved111 Java components through CLI; preserved295 sources,54 prior connections and3 flows; added one hosted-database connection. Config migration preserves scanner and disables updates.
5. Passed two real scans, clean clone2769bfe with129 exact files after two scans, browser source/container inspection and fresh established-key signed Android build.
Deviation: local SDK lacks SwiftParser, so upstream tests use supported matching prebuilt Swift-package hook; Node22 type-stripping flag required. Internal source-free storage arrows remain a documented CLI limitation; no fake source/edge added. Tooling approval and ADR complete; no application architecture or task233 CI implementation.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary
Restored truthful Groma containment using the explicitly approved local tool fix. Three stable containers represent Android runtime, owned Room cache and owned shared planner database. All111 Java components moved under Android through the CLI. Added a source-supported Supabase platform to owned database relationship. Preserved every original ID, meaning, group, source owner, connection and flow.

## Acceptance criteria validation
1. Rendered hierarchy and details show three truthful containers; storage descriptions distinguish ownership from adapters/framework execution.
2–3.295 unique sources retain original owners;54 original connections and3 flows survive. Tool limitations reproduced on published0.7.2, then repaired with reviewed pinned patch without clearing meaning, fake sources or manual map edits.
4. Disposable migration passed first. Main map passed two scans. Clean clone of2769bfe passed two scans with all129 map/config files byte-identical; rendered composition source link opens actual AppRepositories.java.
5. Human explicitly approved standard tooling proposal in this chat; ADR0021 records the change.
6. Finalized plan, evidence, impacts and risks below. Workflow curation remains separate tasks228–230.

## How to test
### Automated tests
Pinned upstream base b8b325fd44f9e78107d271ac794a6e40321dcf53; Bun1.4.3; patch SHA2560ff82c8eec4af88396c169d0f807f7550d06ef230d65602959ebabec1345862d.
Two new regressions failed on unmodified source and passed after fix. Existing negative parent/kind/collision/loadability validation retained.
Full upstream bun run check PASS:24 Node,903 Bun passed,62 optional-runtime skips,0failures; typecheck/lint pass with one existing warning. NODE_OPTIONS=--experimental-strip-types for Node22; GROMA_TEST_SWIFT_PACKAGE uses matching published0.2.2 via supported hook because local SDK lacks SwiftParser. Initial SDK/sandbox failures are recorded in report.
Fresh upstream checkout independently reproduced build; executable SHA256540abf35bc1c2052f24375ead74f9ad892d55a911823e60d4a5dbbace69a2b2d. Wrapper validates source pin, patch and local executable digest.
sh scripts/groma.sh scan twice: created0/refreshed111/findings88. Clean clone2769bfe:129 map/config files byte-identical after two scans and no Groma diff.
Semantic comparison:295 unique existing source paths; all111 original components unchanged except parent;54 original connections and3 flows retained; one database-hosting connection added.
Scanner configuration exact Java0.2.2 source/patterns preserved; automatic updates disabled.
sh -n scripts/groma.sh scripts/setup-groma.sh and git diff HEAD^ --check PASS. Scoped .gitattributes permits space-prefixed blank unified-patch context lines; upstream source lint still runs.
Deleted previous APK; ./gradlew assembleRelease --rerun-tasks PASS. apksigner v1/v2 established certificate ede2ae97735a305900e87bc0fdc021c03382db4e30435f2349a96a3fdcc7545f verifies.
APK SHA256d4eaab3aede97b5dde294b212ffa227c7b04ead1c6c5ad6757d6e079026c56bb; intermediate2.34/code42, not published2.35.
No Android behavior source changed; previous application/domain/BDD/native71 regression evidence remains applicable. Final milestone gates remain241.

### Manual validation
Reviewed source-supported runtime/storage boundaries and upstream patch. Browser exported map shows three containers and Android111 components; inspected Room ownership prose and opened relocated composition source through How it is built. No device or production backend manual testing claimed.

## TDD / BDD / approval-test evidence
Focused red/green fixes, preservation tests with concept/file links and flow endpoints, existing negative validations, disposable map characterization then main-map comparison and repeated clean-checkout scans. No new application behavior.

## Architecture impact
Architecture-significant change: tooling curation capability and pinned build.
Approval received: user i approve,21October2026, for standard proposal.
ADR impact: created ADR0021 for reviewed declaration, safe component moves and reproducible local tool ownership. TASK233 test-strategy approval remains separate.

## README impact
README impact: updated current tool build/launcher, config pin, runtime containment, historical baseline commands and remaining coverage limits.

## Business requirements impact
Business requirements impact: none, because existing runtime/storage and behavior are documented without changing product scope, policy, persistence or workflow.

## Diagram impact
Diagram impact: updated Groma container hierarchy, relocated component paths/links and shared-database hosting relationship; existing README C4 remains accurate.

## Commits / logical change list
2769bfe: pinned tool patch, build/verified launcher, ADR0021, README and CLI-only container migration.
Final evidence commit: clean-checkout/signed-build report, task closure and patch-context whitespace attributes.

## Risks and follow-up
Local patch maintenance until upstream equivalent release; binary reports upstream0.7.2 so use wrapper/pin/digest. No promise of cross-host identical binary bytes. Internal software edges still require exact source endpoints: no fabricated Room-storage arrow; README C4/prose explains interaction. Java-only SQL/generated/manifest gaps and88 duplicate candidates remain explicit. Tasks228–230 own workflow curation;233 requires separate approval;241/242 still gate2.35 publication. Unrelated user changes preserved.
<!-- SECTION:NOTES:END -->

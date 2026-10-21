---
id: TASK-227
title: 'DOC: Restore truthful runtime and data boundaries in the architecture map'
status: In Progress
assignee:
  - '@codex'
created_date: '2026-10-20 06:25'
updated_date: '2026-10-21 10:09'
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
  - confirmedbandalias
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
  - reviewedfestivalband
  - reviewfestivalbandsusecase
  - reviewfestivalcsvusecase
  - reviewimportedbandlinksusecase
  - roomarchivedratingcorrectioncache
  - roombandalias
  - roombandaliasdao
  - roombandaliasrepository
  - roomfestivalafterrating
  - roomfestivalafterratingrepository
  - roomfestivalarchivecache
  - roomfestivalcachemarker
  - roomfestivalcachemarkerdao
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
- [ ] #1 Given the rendered map, a reader can identify the internal software system, Android runtime boundary and local/shared storage ownership without interpreting groups as containers.
- [x] #2 Every existing component has truthful containment or an explicitly documented tool limitation; IDs, file ownership and useful authored meaning are preserved and no fake source file or boundary is invented.
- [x] #3 Groma CLI constraints on described-component moves are investigated safely; blocked operations are documented with a supported remedy or explicit unresolved decision, without stripping meaning or directly editing Groma Markdown.
- [ ] #4 Two rescans and a fresh-checkout comparison preserve the approved structure, source links and existing flows; README and the affected architecture diagrams explain the boundaries.
- [x] #5 Architecture impact is assessed before implementation; significant boundary, dependency, persistence or test-strategy changes receive explicit human approval before coding and an ADR is recorded when required.
- [ ] #6 README, business requirements, diagram and ADR impacts use canonical delivery-governance wording; test evidence, manual verification, finalized plan and remaining risks are recorded before merge.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Human approved standard tooling remedy on2026-10-21: explicit verified-container declaration and preservation-safe described-component relocation in a disposable pinned upstream checkout.
2. Add failing focused tests, implement the smallest fix and run upstream checks; retain negative validations and authored meaning/links.
3. Pin source/patch/artifact identity, document approved tooling ADR and reproducibility; verify the real map in a disposable checkout with two scans and semantic/clean-checkout comparisons before migration.
4. Migrate only through the validated CLI, record each changed map path/reference, inspect rendered map/source links, complete validation and fresh signed build before Done.
Tooling architecture change explicitly approved by user. No app/domain/schema changes or scanner upgrades. TASK-233 test-strategy approval remains separate.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Confirmed in disposable baseline checkout: groma accept android-application exits 1 (no scan match); moving apprepositories exits 1 because it has authored meaning. Experiment left the release map untouched. See backlog/docs/groma-container-limitations-2026-10-20.md for source-supported runtime/storage boundaries and unresolved supported-tooling remedy. Container acceptance/relocation is not complete; do not waive dependent release criteria.

README now documents existing runtime/storage boundaries with C4 Mermaid and explicitly states Groma containment remains unresolved. README impact: updated current runtime/storage explanation and tool limits. Business requirements impact: none, because only existing architecture is described. Diagram impact: updated README C4 container view; Groma unchanged. ADR impact: none, because deployed architecture and existing decisions are unchanged. AC1/4/6 remain open pending supported Groma containment remedy and final validation.

2026-10-21 recheck: found repository-local Groma 0.7.2, while plain groma still resolves global 0.6.6. Earlier global checks missed the local installation. Explicitly invoked local binary in disposable baseline worktree: accept android-application still exits 1 (no scan match), and edit apprepositories --parent android-application still exits 1 (authored meaning). Local structure guide retains restrictions and now refers to config.json. Evidence report updated; no release-map/package/config migration performed. Same containment blockers remain.

Unblock attempt 2026-10-21: npm registry confirms latest published Groma remains0.7.2; local0.7.2 still rejects container acceptance and described-component relocation in the disposable baseline. Open upstream issue170 independently documents both restrictions: https://github.com/MrLesk/groma.md/issues/170 . Evidence report now includes minimal/standard/maximum remedies and a concrete recommended source-fix design with preservation/negative tests and clean-checkout controls. No tool source or release map changed. Explicit tooling-change approval is required before implementing the recommended option; dependent release criteria remain open.

Human approval received in this chat: i approve, for the standard tooling fix described in groma-container-limitations-2026-10-20.md. Proceeding in a disposable checkout before any release-map migration.

Approved fix implemented and verified: two new regressions red on upstream0.7.2, then green; full check Node24/24, Bun903 passed/62 optional skips/0 failures using supported prebuilt Swift test package and Node type-stripping flag. Fresh upstream checkout reproduced the pinned build. Main map now declares Android, Room and shared planner database; all111 components moved through CLI with295 unique source files, exact original meaning/groups/ownership,54 original connections and3 flows retained. One source-supported Supabase-to-owned-database connection added. Scanner remainsJava0.2.2; config migration preserves patterns and disables automatic updates. Two scans and rendered container/source-link inspection passed. Preparing clean-checkout and final signed-build closure.
<!-- SECTION:NOTES:END -->

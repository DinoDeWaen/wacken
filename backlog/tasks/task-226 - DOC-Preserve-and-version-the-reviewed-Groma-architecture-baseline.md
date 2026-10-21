---
id: TASK-226
title: 'DOC: Preserve and version the reviewed Groma architecture baseline'
status: Done
assignee:
  - '@codex'
created_date: '2026-10-20 06:24'
updated_date: '2026-10-20 19:28'
labels:
  - groma-audit
  - release-2.35
milestone: m-3
dependencies: []
references:
  - wacken
documentation:
  - backlog/docs/groma-audit-2026-10-20.md
modified_files:
  - backlog/docs/groma-baseline-2026-10-20.sha256
  - README.md
  - groma/actors/festival-attendee.md
  - groma/actors/festival-data-admin.md
  - groma/externals/musicbrainz.md
  - groma/externals/spotify.md
  - groma/externals/supabase.md
  - groma/externals/wikidata.md
  - groma/externals/wikipedia.md
  - groma/externals/youtube.md
  - groma/flows/build-the-shared-calendar.md
  - groma/flows/import-festival-csv.md
  - groma/flows/rate-a-festival-band.md
  - groma/index.md
  - groma/project.md
  - groma/scanners.json
  - groma/systems/wacken/components/activefestivalafterratingrepository.md
  - groma/systems/wacken/components/addfestivalaction.md
  - groma/systems/wacken/components/addfestivalusecase.md
  - groma/systems/wacken/components/androidjavacompatibility.md
  - groma/systems/wacken/components/apprepositories.md
  - groma/systems/wacken/components/archivedfestivalactivity.md
  - groma/systems/wacken/components/archivedratingcorrectiondialog.md
  - groma/systems/wacken/components/archivedratingcorrectiongateway.md
  - groma/systems/wacken/components/archivedratingcorrectionusecase.md
  - groma/systems/wacken/components/archivedratingkind.md
  - groma/systems/wacken/components/archivedratingreview.md
  - groma/systems/wacken/components/archivefestivalaction.md
  - groma/systems/wacken/components/authsessionstore.md
  - groma/systems/wacken/components/band.md
  - groma/systems/wacken/components/banddetailactivity.md
  - groma/systems/wacken/components/bandlinkreviewactivity.md
  - groma/systems/wacken/components/bandmetadatareviewactivity.md
  - groma/systems/wacken/components/bandratingcontext.md
  - groma/systems/wacken/components/bandrepository.md
  - groma/systems/wacken/components/canonicalband.md
  - groma/systems/wacken/components/confirmedbandalias.md
  - groma/systems/wacken/components/deletefestivaldialog.md
  - groma/systems/wacken/components/deletefestivalusecase.md
  - groma/systems/wacken/components/exportratingcorrectionscsvusecase.md
  - groma/systems/wacken/components/exportratinglifecyclecsvusecase.md
  - groma/systems/wacken/components/exportratingscsvusecase.md
  - groma/systems/wacken/components/festival.md
  - groma/systems/wacken/components/festivalafterrating.md
  - groma/systems/wacken/components/festivalafterratingrepository.md
  - groma/systems/wacken/components/festivalafterratingview.md
  - groma/systems/wacken/components/festivalarchivegateway.md
  - groma/systems/wacken/components/festivalbandcatalog.md
  - groma/systems/wacken/components/festivalbandreviewdialog.md
  - groma/systems/wacken/components/festivalcreationgateway.md
  - groma/systems/wacken/components/festivalcsvreader.md
  - groma/systems/wacken/components/festivalplanningratingview.md
  - groma/systems/wacken/components/festivalscopedperformancerepository.md
  - groma/systems/wacken/components/filebackedstorage.md
  - groma/systems/wacken/components/findbandmusiclinksusecase.md
  - groma/systems/wacken/components/generatesharedscheduleusecase.md
  - groma/systems/wacken/components/importcsvactivity.md
  - groma/systems/wacken/components/importfestivalcsvusecase.md
  - groma/systems/wacken/components/listactivefestivalbandsusecase.md
  - groma/systems/wacken/components/localcsvimportreviewdialog.md
  - groma/systems/wacken/components/loginactivity.md
  - groma/systems/wacken/components/mainactivity.md
  - groma/systems/wacken/components/missingfestivalplanningratingexception.md
  - groma/systems/wacken/components/missingfestivalratingdialog.md
  - groma/systems/wacken/components/musicbrainzmetadataprovider.md
  - groma/systems/wacken/components/musiclinkevidence.md
  - groma/systems/wacken/components/musiclinklookupprovider.md
  - groma/systems/wacken/components/musiclinksreviewactivity.md
  - groma/systems/wacken/components/officialmusicpageclient.md
  - groma/systems/wacken/components/pendingplanningratingrecovery.md
  - groma/systems/wacken/components/performance.md
  - groma/systems/wacken/components/performanceconflictresolver.md
  - groma/systems/wacken/components/personalbandrating.md
  - groma/systems/wacken/components/personalbandratingrepository.md
  - groma/systems/wacken/components/ratebandusecase.md
  - groma/systems/wacken/components/rating.md
  - groma/systems/wacken/components/ratingcorrection.md
  - groma/systems/wacken/components/ratingcorrectionrepository.md
  - groma/systems/wacken/components/ratingstatesync.md
  - groma/systems/wacken/components/ratingstatesyncstore.md
  - groma/systems/wacken/components/recordfestivalafterratingusecase.md
  - groma/systems/wacken/components/recoverpendingplanningratingusecase.md
  - groma/systems/wacken/components/reviewedfestivalband.md
  - groma/systems/wacken/components/reviewfestivalbandsusecase.md
  - groma/systems/wacken/components/reviewfestivalcsvusecase.md
  - groma/systems/wacken/components/reviewimportedbandlinksusecase.md
  - groma/systems/wacken/components/roomarchivedratingcorrectioncache.md
  - groma/systems/wacken/components/roombandalias.md
  - groma/systems/wacken/components/roombandaliasdao.md
  - groma/systems/wacken/components/roombandaliasrepository.md
  - groma/systems/wacken/components/roomfestivalafterrating.md
  - groma/systems/wacken/components/roomfestivalafterratingrepository.md
  - groma/systems/wacken/components/roomfestivalarchivecache.md
  - groma/systems/wacken/components/roomfestivalcachemarker.md
  - groma/systems/wacken/components/roomfestivalcachemarkerdao.md
  - groma/systems/wacken/components/roomfestivalcreationcache.md
  - groma/systems/wacken/components/roomfestivaldeletion.md
  - groma/systems/wacken/components/roompersonalbandrating.md
  - groma/systems/wacken/components/roompersonalbandratingrepository.md
  - groma/systems/wacken/components/roomratingcorrection.md
  - groma/systems/wacken/components/roomratingcorrectiondao.md
  - groma/systems/wacken/components/roomratingcorrectionrepository.md
  - groma/systems/wacken/components/roomratingstatedao.md
  - groma/systems/wacken/components/scheduleactivity.md
  - groma/systems/wacken/components/searchbandmetadatausecase.md
  - groma/systems/wacken/components/settingsactivity.md
  - groma/systems/wacken/components/spotifymetadataprovider.md
  - groma/systems/wacken/components/supabasearchivedratingcorrectiongateway.md
  - groma/systems/wacken/components/supabaseauthclient.md
  - groma/systems/wacken/components/supabaseauthenticatedrequest.md
  - groma/systems/wacken/components/supabasefestivalarchivegateway.md
  - groma/systems/wacken/components/supabasefestivalbandcatalog.md
  - groma/systems/wacken/components/supabasefestivalcreationgateway.md
  - groma/systems/wacken/components/supabasefestivaldeletiongateway.md
  - groma/systems/wacken/components/supabasemasterdataclient.md
  - groma/systems/wacken/components/supabaseratingclient.md
  - groma/systems/wacken/components/supabaseratingstateclient.md
  - groma/systems/wacken/components/syncedbandrepository.md
  - groma/systems/wacken/components/syncingratingrepository.md
  - groma/systems/wacken/components/tsvimporttransaction.md
  - groma/systems/wacken/components/verifiedmusiclinksprovider.md
  - groma/systems/wacken/components/viewbandratingcontextusecase.md
  - groma/systems/wacken/components/wackendatabase.md
  - groma/systems/wacken/components/wackentheme.md
  - groma/systems/wacken/components/wikidatametadataprovider.md
  - groma/systems/wacken/components/wikipediametadataprovider.md
  - groma/systems/wacken/components/youtubemetadataprovider.md
  - groma/systems/wacken/system.md
  - backlog/docs/groma-baseline-verification-2026-10-20.md
priority: high
type: docs
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## User story
As a maintainer, I want the existing architecture knowledge available in every checkout so it can be reviewed and cannot silently disappear.

## Scope
Review the existing untracked groma directory, preserve authored knowledge, commit the intended baseline and document pinned tooling, Java-only coverage and clean-checkout verification. Exclude unrelated user changes and generated exports. No structural curation or application refactoring in this task.

## Audit reference
[Groma architecture audit — 20 October 2026](../docs/groma-audit-2026-10-20.md)
Repository path: backlog/docs/groma-audit-2026-10-20.md
Findings: F1. Investigation: TASK-225.

## Boundaries and impacts
Preserve ADR-0016: shared canonical metadata, member-specific established band rating, independent festival planning/after-ratings, one-time creation snapshots and source-aware archive promotion/correction. Follow Groma CLI-only curation and Backlog CLI-only task maintenance.
Business requirements impact: none planned unless an approved behaviour change is explicitly required; record the actual impact on completion.
README and diagram impacts are assessed for the affected workflow. ADR impact: none assumed; approval is required for significant changes.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given a clean checkout of the completed commit, the approved Groma project, scanner configuration, IDs, ownership, groups, descriptions, authored relationships and flows are available without copying local files.
- [x] #2 Given two consecutive scans, owned files do not get duplicate components and reviewed authored meaning, groups and ownership survive; any tool-created difference is explained.
- [x] #3 The baseline has an automated semantic inventory or equivalent reproducible verification and recorded fresh-worktree evidence; a negative control detects missing architecture files.
- [x] #4 README documents architecture location, exact tool/scanner versions, scan/lint commands and the distinction between scanning and curation.
- [x] #5 Architecture impact is assessed before implementation; significant boundary, dependency, persistence or test-strategy changes receive explicit human approval before coding and an ADR is recorded when required.
- [x] #6 README, business requirements, diagram and ADR impacts use canonical delivery-governance wording; test evidence, manual verification, finalized plan and remaining risks are recorded before merge.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Preserved all 126 existing Groma files verbatim in commit 4630970, excluding exports and unrelated local changes.
2. Added sorted SHA-256 characterization manifest; intact-copy verification passed and missing-flow negative control failed as expected.
3. Verified two scans in working checkout and two in a fresh detached worktree at 4630970: zero created components, exact paths/hashes unchanged, fresh status clean.
4. Documented CLI 0.6.6, Java scanner 0.2.2, scan/lint commands and coverage limits in README and baseline evidence report.
5. Deleted the previous local release APK, rebuilt with JDK21 and existing stable key using assembleRelease --rerun-tasks, and verified signature/metadata/digest.
Treatment: minimal documentation preservation; exact-content characterization only. No application code, architecture boundaries, dependencies or future test strategy changed; approval/ADR not required. No scope deviations. Known islands, missing containers and 99 lint candidates intentionally preserved for TASK-227–233.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary
Preserved the existing authored Groma map in version control without rewriting its knowledge. Commit 4630970 contains the 126-file baseline, SHA-256 inventory and README commands; the follow-up evidence report documents reproduction.

## Acceptance criteria validation
1. Fresh detached checkout at 4630970 contains the entire project/configuration, IDs, ownership, groups, descriptions, 42 authored relationships and three flows without copying local files.
2. Two scans in the working checkout and two in the fresh checkout each reported created 0, refreshed 109, matched 0, findings 99. All hashes and the exact path set remained unchanged.
3. Reproducible SHA-256 verification passed; deleting a flow only in a disposable copy made verification exit 1. Fresh worktree ended completely clean.
4. README documents location, exact tool/scanner versions, scan/lint commands, scanning versus curation and Java-only limits.
5. Architecture assessed before changes: preservation only; no significant change or approval/ADR required.
6. Final plan, report, validation and canonical impacts recorded here.

## How to test
### Automated tests
Follow backlog/docs/groma-baseline-verification-2026-10-20.md at commit 4630970. SHA-256 checks and exact path-set comparisons passed before/after each scan. Negative control detected missing rate-a-festival-band.md. git diff --check passed. Groma lint --max-count 200 exited 1 with existing review candidates; this is recorded, not claimed as passing lint.
Required per-ticket fresh build: JDK21 ./gradlew assembleRelease --rerun-tasks succeeded, 53 tasks executed. Signing credentials loaded privately from existing keychain; no key generated. apksigner verify --verbose --print-certs passed v1/v2. aapt: be.wacken.planner, version 2.34/code42, minSdk23/target36. APK SHA256: 1a96cf8901e9e6e887b3ea180ed2769c05b5e4bdb706d2ecbbff543edb0728fb. Signer SHA256: ede2ae97735a305900e87bc0fdc021c03382db4e30435f2349a96a3fdcc7545f. APK: app/build/outputs/apk/release/app-release.apk. Not a 2.35 publication.
### Manual validation
Reviewed architecture file inventory: Markdown/JSON only, no exports/caches/binaries. Groma overview resolves users/system/externals and three flows. Unrelated user changes excluded. No visual layout changes introduced.

## TDD / BDD / approval-test evidence
Exact-content characterization protects the existing map. Given an intact baseline, verification passes; given a missing flow, it fails. Given two rescans of a fresh checkout, all records and ownership remain unchanged. No application behavior changed; unit/native/SQL suites not rerun for documentation preservation. Full release gates remain TASK-241.

## Architecture impact
- Architecture-significant change: no; no deployed architecture, dependencies, schema or ongoing test strategy changed.
- Approval received: not required for preserving existing documentation.
- ADR: ADR impact: none, because existing architecture decisions are unchanged.

## README impact
README impact: updated architecture map location, pinned tooling, verification commands and limitations.

## Business requirements impact
Business requirements impact: none, because no public behavior, business rule or product scope changed.

## Diagram impact
Diagram impact: updated version control to preserve the existing Groma map verbatim; no diagram content changed.

## Commits / logical change list
4630970: preserve Groma baseline and verification manifest; follow-up: evidence report and finalized task record.

## Risks and follow-up
Known zero-container structure, 72 islands, two empty records and 99 lint groups remain for TASK-227–233. Hash preservation does not imply semantic completeness. Java scanner limitations remain documented. Historical manifest must be interpreted at baseline commit after intentional curation. No merge, tag or release publication performed.
<!-- SECTION:NOTES:END -->

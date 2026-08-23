---
id: task-179
title: 'DEF: Imported known bands are not linked when names differ slightly'
status: In Progress
assignee:
  - '@codex'
created_date: '2026-08-23 09:33'
updated_date: '2026-08-23 14:02'
labels:
  - defect
  - bands
  - import
  - metadata
  - settings
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Business value: Known bands should reuse existing identity, ratings, history, and metadata when future festival CSV uploads use case differences, punctuation differences, aliases, or alternate spellings.

Observed defect: after uploading the Summer Breeze band CSV, some bands that already existed from Wacken were not linked. Example: Wacken data contains `Any given Day`, while the new festival CSV contains `Any Given Day`; the current exact, case-sensitive match creates a separate band instead of reusing the existing band and metadata.

Scope: add a Settings/Admin band-linking workflow for imported active-festival lineup entries that were created as new or remain unlinked from likely existing bands. The workflow shows a review table with the uploaded/searched band on the left, editable search term, candidate dropdown next to it, and per-band user approval. If multiple matches exist, the user selects one. `No match` is a valid answer and leaves/creates the separate band. Confirmed links use the existing app band catalog as the golden source and must reuse the existing band metadata, ratings, and personal history.

Metadata enrichment scope: add a Settings/Admin fetch-metadata action for bands missing picture, biography/text, Spotify, or YouTube metadata. The app first copies missing metadata from a confirmed existing golden-source band. If still missing, it may search external music metadata sources and stage proposed metadata for user approval. Existing non-empty metadata must not be overwritten automatically.

Recommended external-source strategy: use MusicBrainz first for canonical artist identity, aliases, and official URL relationships; then use Wikidata/Wikipedia summaries for neutral text and images where the artist identity is unambiguous; use Spotify Web API for Spotify artist URL/images when configured; use YouTube Data API for official channel candidates when configured. All external results require user confirmation before updating the golden-source band record.

Out of scope: automatic unreviewed fuzzy merges, overwriting existing metadata, merging ratings without a confirmed band link, multiple independent groups, and final Summer Breeze schedule/performance import.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given an active festival lineup contains a band name that does not exactly match an existing band, when the user opens the Settings/Admin link-bands action, then the band appears in a review table with its imported name, editable search term, candidate dropdown, and a no-match option.
- [x] #2 Given likely matches are found in the existing band database, when one candidate is selected and approved for a row, then the lineup entry links to the selected existing band and uses that band's current metadata, ratings, and personal history.
- [x] #3 Given multiple possible matches exist, then the user must explicitly choose one or choose no match; the app does not merge automatically.
- [x] #4 Given no candidate is suitable, when the user chooses no match, then the uploaded band remains a separate band without blocking the rest of the review.
- [x] #5 Given a band is missing picture, biography/text, Spotify, or YouTube metadata, when the user runs fetch metadata, then existing golden-source metadata is applied first for missing fields only.
- [ ] #6 Given metadata is still missing after checking the own database, when external lookup is configured and run, then proposed MusicBrainz/Wikidata/Wikipedia/Spotify/YouTube metadata is shown for user approval before being saved to the golden-source band record.
- [x] #7 Existing non-empty band metadata is not overwritten unless a future task explicitly adds reviewed replace behavior.
- [x] #8 Automated tests cover case-insensitive or fuzzy candidate discovery with the Any Given Day example, user-confirmed linking, no-match handling, and no-overwrite metadata enrichment.
- [x] #9 Architecture impact is assessed before implementation; if alias storage, lineage/audit, external API clients, persistence schema, or Supabase contracts must change, explicit approval is requested before coding and ADR impact is recorded.
- [x] #10 Business requirements and README impact are recorded using delivery-governance wording when implemented.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Implement the own-database linking flow first: find active-festival lineup entries whose uploaded/current band name has likely existing catalog matches such as case-insensitive `Any Given Day` -> `Any given Day`.
2. Add application tests for candidate discovery, user-confirmed linking, no-match/no-op behavior, and metadata preservation from the golden-source band.
3. Add Settings/Admin source-regression coverage for link-bands and fetch-metadata actions.
4. Add the minimal Settings workflow for reviewed linking using existing repositories and no automatic merge.
5. Assess and request architecture approval before adding any external metadata lookup client, credential, application port, persistence schema, alias storage, Supabase contract, or online write path.
6. If approved, add a reviewed external metadata proposal flow behind an application boundary; if not approved, close only the internal-linking portion and leave online lookup as a follow-up.
7. Run relevant validation, update task notes, and close only if all accepted scope is complete.

Architecture impact: internal candidate discovery and relinking through existing repositories is not architecture-significant. External metadata fetching is architecture-significant because it introduces external integration boundaries and likely application ports/adapters/credentials.
Approval needed before coding external lookup: yes.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Progress update before external metadata approval gate:

- Implemented reviewed own-database imported-band linking from Settings/Admin. Candidate rows show the imported name, editable search term, dropdown candidates, and explicit No match option. Confirmed links replace the active-festival lineup entry with the selected golden-source band.
- Implemented own-catalog metadata enrichment for missing fields only. Existing non-empty metadata is preserved; external lookup remains unimplemented until architecture approval is received.
- Added application tests for the Any Given Day case-insensitive candidate, manual search, confirmed link, no-match handling, and no-overwrite metadata enrichment.
- Added Android source regression tests for Settings actions and the band-link review controls.
- Updated README and business requirements for the implemented local admin behavior.

Validation so far:
- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :application:test :app:testDebugUnitTest --tests be.wacken.planner.application.ImportedBandLinkingUseCaseTest --tests be.wacken.planner.SettingsActivityRegressionTest --tests be.wacken.planner.BandLinkReviewActivityRegressionTest

Architecture gate:
- External metadata lookup is architecture-significant because it adds external API contracts, provider adapters, configuration/credential behavior, and an ADR. Approval requested before coding AC #6.
<!-- SECTION:NOTES:END -->

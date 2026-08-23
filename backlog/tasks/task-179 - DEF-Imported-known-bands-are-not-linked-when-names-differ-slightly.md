---
id: task-179
title: 'DEF: Imported known bands are not linked when names differ slightly'
status: To Do
assignee: []
created_date: '2026-08-23 09:33'
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
- [ ] #1 Given an active festival lineup contains a band name that does not exactly match an existing band, when the user opens the Settings/Admin link-bands action, then the band appears in a review table with its imported name, editable search term, candidate dropdown, and a no-match option.
- [ ] #2 Given likely matches are found in the existing band database, when one candidate is selected and approved for a row, then the lineup entry links to the selected existing band and uses that band's current metadata, ratings, and personal history.
- [ ] #3 Given multiple possible matches exist, then the user must explicitly choose one or choose no match; the app does not merge automatically.
- [ ] #4 Given no candidate is suitable, when the user chooses no match, then the uploaded band remains a separate band without blocking the rest of the review.
- [ ] #5 Given a band is missing picture, biography/text, Spotify, or YouTube metadata, when the user runs fetch metadata, then existing golden-source metadata is applied first for missing fields only.
- [ ] #6 Given metadata is still missing after checking the own database, when external lookup is configured and run, then proposed MusicBrainz/Wikidata/Wikipedia/Spotify/YouTube metadata is shown for user approval before being saved to the golden-source band record.
- [ ] #7 Existing non-empty band metadata is not overwritten unless a future task explicitly adds reviewed replace behavior.
- [ ] #8 Automated tests cover case-insensitive or fuzzy candidate discovery with the Any Given Day example, user-confirmed linking, no-match handling, and no-overwrite metadata enrichment.
- [ ] #9 Architecture impact is assessed before implementation; if alias storage, lineage/audit, external API clients, persistence schema, or Supabase contracts must change, explicit approval is requested before coding and ADR impact is recorded.
- [ ] #10 Business requirements and README impact are recorded using delivery-governance wording when implemented.
<!-- AC:END -->

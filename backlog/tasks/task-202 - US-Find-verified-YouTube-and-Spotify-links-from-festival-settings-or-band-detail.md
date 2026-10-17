---
id: TASK-202
title: >-
  US: Find verified YouTube and Spotify links from festival settings or band
  detail
status: To Do
assignee: []
created_date: '2026-10-17 08:11'
updated_date: '2026-10-17 08:12'
labels:
  - administration
  - metadata
  - bands
  - ux
milestone: m-0
dependencies:
  - TASK-179.1
  - TASK-179.5
  - TASK-179.6
priority: medium
type: enhancement
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## User story
As a festival admin, I want to find the official YouTube channel and Spotify artist profile using a band's name, either for the selected festival from Settings/Admin beside Link bands or for one band from its detail page, so that I can verify and approve missing music links.

## Scope
Extend the existing TASK-179 reviewed metadata framework/providers. Use the stored band name by default and allow editing the search term without renaming the band. Festival mode searches missing links for that festival's lineup; band mode searches only the selected band.

Show candidate identity, canonical channel/artist URL, verification evidence and source links before approval. Establish identity using official band/label website cross-links, official platform artist/channel information and corroborating authoritative music-identity sources as appropriate. Search rank, a name match, a badge or a generated URL alone is not verification. Unresolved candidates stay unverified and missing fields stay empty.

The Spotify equivalent is an artist profile, not a playlist/user account. YouTube results must be band channels, not videos/fan accounts. Preserve existing nonempty metadata, lineup identity, ratings and history. No automatic merging, unreviewed saves, guessed links or unrelated enrichment. Follow existing provider boundaries and obtain approval before significant integration, configuration or persistence changes. Preserve TASK-199's empty-link behavior.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given a selected festival, when an admin triggers Find music links from Settings/Admin beside Link bands, then that lineup's missing YouTube and/or Spotify fields are searched with per-band progress and results.
- [ ] #2 Given a band detail page, when the action is triggered, then only that band is searched using its name; editing the search term does not rename it or change its identity.
- [ ] #3 Candidate review shows band identity, provider, canonical channel/artist URL, clickable verification sources and a verification outcome/rationale that distinguishes this band from namesakes.
- [ ] #4 Ambiguous or unsupported matches remain unverified and cannot be saved as verified links. The user can refine the search, select an identity-verified candidate or reject all; no match/rejection leaves missing fields empty.
- [ ] #5 Only explicit approval saves verified results to missing golden-source fields and syncs them to other members. Existing links, lineup membership, ratings and history remain unchanged.
- [ ] #6 Offline use, missing provider configuration, rate limiting, errors and cancellation give clear per-band status, keep the UI responsive and preserve saved data; retry processes unfinished work without false success.
- [ ] #7 Application/provider/adapter and native Android tests cover both entry points, festival scoping, edited names, verified/ambiguous candidates, evidence, approval/rejection, no match, no overwrite, retry and JSON null metadata; manual verification steps are recorded.
- [ ] #8 README documents both actions, verification/source rules and provider configuration. Business requirements impact and Diagram impact use canonical delivery-governance wording when implemented.
- [ ] #9 Architecture impact is assessed before coding; significant changes to integrations, credentials, evidence persistence, API or schema require explicit approval and ADR handling.
<!-- AC:END -->

---
id: task-179.1
title: 'US: Add reviewed band metadata search framework'
status: To Do
assignee: []
created_date: '2026-08-23 14:11'
labels:
  - metadata
  - architecture
  - bands
  - settings
dependencies: []
parent_task_id: task-179
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Business value: admins need a safe framework for finding missing band metadata without overwriting trusted catalog data or coupling provider details into the UI.

User story: As an admin, I want a reviewed metadata search framework that checks the own band catalog first and then gathers provider proposals, so that missing band pictures, biographies, Spotify links, and YouTube links can be filled only after approval.

Scope: define the metadata proposal workflow, provider boundary, Settings/Admin entry point, proposal review behavior, and no-overwrite save behavior. The framework may include a fake or empty provider for tests but does not need real MusicBrainz, Wikidata, Wikipedia, Spotify, or YouTube API calls; those are separate stories.

Out of scope: provider-specific HTTP clients, credential provisioning, automatic unreviewed metadata saves, overwriting non-empty metadata, Supabase schema changes, and alias storage unless separately approved.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given a band is missing one or more metadata fields, when metadata search runs, then proposals are produced only for missing fields and existing non-empty metadata is not overwritten.
- [ ] #2 Given the own band catalog contains trusted metadata for a likely confirmed band match, when metadata search runs, then own-catalog values are preferred before any external provider proposal.
- [ ] #3 Given provider proposals are available, when the admin reviews them, then each proposed field can be accepted or rejected before the golden-source band record is changed.
- [ ] #4 Given no provider is configured or no proposal is suitable, when the admin rejects or skips results, then the band remains unchanged and the workflow can continue for other bands.
- [ ] #5 Architecture impact is assessed before implementation; because this introduces metadata provider boundaries, explicit approval is requested before coding and an ADR is created or updated if approved.
- [ ] #6 Automated tests cover proposal generation, own-catalog priority, approval-required saving, no-match/no-proposal handling, and no-overwrite behavior.
- [ ] #7 README, business requirements, diagram, and ADR impact are recorded using delivery-governance wording.
<!-- AC:END -->

---
id: task-29
title: Generate Wacken bands CSV from site
status: Done
assignee:
  - '@codex'
created_date: '2026-05-15 20:08'
updated_date: '2026-05-15 20:11'
labels:
  - data
  - import
  - mvp1
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## Task: Generate Wacken bands CSV from site

**As a** user
**I want** a bands.csv generated from the Wacken line-up site
**So that** I can import the current band list into the MVP APK for rating

### Notes
- Source should be the Wacken line-up site/feed where possible.
- MVP supports early band-only import; stages and performances can remain empty until running order exists.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Generated bands.csv follows the documented MVP bands.csv schema
- [x] #2 CSV includes current band names from the Wacken source and available music metadata where discoverable
- [x] #3 Generated file is placed in the repo for Android import
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Fetched the official Wacken bandlist JSON feed discovered in the project docs.
2. Transformed concert entries into the documented early bands.csv schema with stable ids, names, slugs, source ids, country, links, images, Spotify ids, and first-time flag.
3. Added empty companion CSV files for stages, performances, distances, and food so the Android import screen can be filled cleanly before the running order exists.
4. Validated generated CSV shape and row count.
5. Closed task and committed generated files.

Architecture impact: none; generated data files only.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Implementation notes:
- Generated data/wacken-2026/bands.csv from https://www.wacken.com/fileadmin/Json/bandlist-concert.json.
- Generated 164 band rows using the documented early bands.csv schema.
- Included available YouTube URLs, Spotify artist ids, homepage/Facebook/Instagram links, image URLs, country, source ids, slug, and first-time flag.
- Added header-only companion files for stages.csv, performances.csv, distances.csv, and food.csv because the final running order is not active in the feed.
- Collapsed biography whitespace to one line because the current MVP Android importer is line-based.

Validation package:
- Data validation: 164 rows, 17 columns, no duplicate band ids, no blank ids/names, 158 YouTube values, 155 Spotify artist values.
- Automated checks run: JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew test.
- README impact: no update needed; generated data includes SOURCE.md.
- Diagram impact: no diagram update needed.
- ADR impact: no ADR needed.
- Approval status: no architecture approval required.
- Risks: Wacken lineup can change; regenerate from the source feed when needed.
<!-- SECTION:NOTES:END -->

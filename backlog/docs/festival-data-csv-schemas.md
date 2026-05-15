# Festival Data CSV Schemas

Date: 2026-05-15

Related task: task-16

## Purpose

These schemas define the MVP import contracts for Wacken Planner 2026 festival data. They separate early band-rating data from the final running-order data because the Wacken lineup may be available before stages and times are final.

The schemas are intentionally simple CSV files with stable ids. Display names may change, but ids should remain stable once imported.

## Common Rules

- Encoding: UTF-8.
- Header row: required.
- Delimiter: comma.
- Quote fields using standard CSV quoting when values contain commas, quotes, or line breaks.
- Boolean values: `true` or `false`.
- Date/time values: ISO-8601 local date-time, for example `2026-07-30T18:00:00`.
- Time zone: not stored in the CSV for MVP. Wacken local time is assumed by the importer.
- Empty optional fields are allowed.
- Required ids must be unique inside their file.
- Cross-file references use ids, not display names.

Open product point:

- Multiple festival days are expected by the domain, but the exact final Wacken 2026 day/date model still needs confirmation from final running-order data. The MVP schema supports multiple days through `festival_day_id` and local date-times.

## Import Set A: Early Band-Only Rating Import

Use this shape before final stage/time data is available. It is enough to start rating bands.

### `bands.csv`

| Column | Required | Description | Validation |
| --- | --- | --- | --- |
| `band_id` | Yes | Stable internal id, preferably Wacken artist uid or a generated slug. | Unique, non-blank. |
| `name` | Yes | Band display name. | Non-blank. |
| `slug` | No | Wacken path segment or app slug. | Unique when present. |
| `source` | No | Source label such as `wacken-json` or `manual`. | Optional. |
| `source_id` | No | Wacken `artist.uid` or feed `uid`. | Optional, unique with `source` when present. |
| `country` | No | Display country name. | Optional. |
| `subtitle` | No | Special set label or subtitle. | Optional. |
| `biography_html` | No | Short HTML biography from source. | Optional; render safely or strip before display. |
| `image_url` | No | Main image URL or path. | Optional. |
| `thumbnail_url` | No | Thumbnail URL or path. | Optional. |
| `youtube_url` | No | Main YouTube URL, usually Wacken `externalMediaSource`. | Optional; must be URL-like when present. |
| `spotify_artist_id` | No | Spotify artist id. | Optional. |
| `spotify_album_id` | No | Spotify album id. | Optional. |
| `homepage_url` | No | Band homepage. | Optional; must be URL-like when present. |
| `facebook_url` | No | Facebook URL. | Optional; must be URL-like when present. |
| `instagram_url` | No | Instagram URL. | Optional; must be URL-like when present. |
| `first_time` | No | Whether source marks first Wacken appearance. | Empty, `true`, or `false`. |

Minimum valid early import row:

```csv
band_id,name
5th-avenue,5th Avenue
```

## Import Set B: Final Running-Order Import

Use this shape once stages and times are available.

### `stages.csv`

| Column | Required | Description | Validation |
| --- | --- | --- | --- |
| `stage_id` | Yes | Stable stage id. | Unique, non-blank. |
| `name` | Yes | Stage display name. | Non-blank. |
| `subtitle` | No | Sponsor or stage subtitle. | Optional. |
| `latitude` | No | Decimal latitude. | Optional; required only if map distance calculations are introduced. |
| `longitude` | No | Decimal longitude. | Optional; required only if map distance calculations are introduced. |
| `sort_order` | No | Display order. | Optional integer. |

### `performances.csv`

| Column | Required | Description | Validation |
| --- | --- | --- | --- |
| `performance_id` | Yes | Stable performance id. | Unique, non-blank. |
| `band_id` | Yes | References `bands.band_id`. | Must exist in `bands.csv`. |
| `stage_id` | Yes | References `stages.stage_id`. | Must exist in `stages.csv`; unknown stages fail import. |
| `festival_day_id` | Yes | Day identifier such as `thu`, `fri`, `sat`. | Non-blank. |
| `start_at` | Yes | Local Wacken start date/time. | ISO-8601 local date-time. |
| `end_at` | Yes | Local Wacken end date/time. | ISO-8601 local date-time; must be after `start_at`. |
| `performance_type` | No | Type such as `Concert` or `Meet & Greet`. | MVP scheduler should import only `Concert` unless user confirms other types. |
| `title` | No | Optional event title. | Optional. |
| `source_id` | No | Source event id. | Optional; useful for updates. |

Overlap validation:

- For each `stage_id`, no two imported performances may overlap.
- Overlap means `a.start_at < b.end_at` and `b.start_at < a.end_at`.
- Back-to-back performances where one ends exactly when the next starts are allowed.

### `distances.csv`

| Column | Required | Description | Validation |
| --- | --- | --- | --- |
| `from_stage_id` | Yes | References `stages.stage_id`. | Must exist in `stages.csv`. |
| `to_stage_id` | Yes | References `stages.stage_id`. | Must exist in `stages.csv`. |
| `walking_minutes` | Yes | Walking duration between stages. | Integer `0` or greater. |
| `distance_meters` | No | Physical distance when known. | Optional integer `0` or greater. |
| `bidirectional` | No | Whether this row applies both directions. | Empty, `true`, or `false`; default `true`. |
| `source` | No | Source label, for example `manual`. | Optional. |

Validation:

- A row must not reference unknown stages.
- Duplicate `from_stage_id` + `to_stage_id` pairs are invalid after expanding bidirectional rows.
- MVP travel feasibility uses `walking_minutes`.

### `food.csv`

| Column | Required | Description | Validation |
| --- | --- | --- | --- |
| `food_id` | Yes | Stable food option id. | Unique, non-blank. |
| `name` | Yes | Food option display name. | Non-blank. |
| `near_stage_id` | No | Main nearby stage reference. | Optional; must exist in `stages.csv` when present. |
| `walking_minutes_from_stage` | No | Walking minutes from `near_stage_id`. | Optional integer `0` or greater. |
| `category` | No | Food category. | Optional. |
| `notes` | No | Free-form notes. | Optional. |
| `latitude` | No | Decimal latitude. | Optional. |
| `longitude` | No | Decimal longitude. | Optional. |

Validation:

- Unknown `near_stage_id` values fail import.
- Food options are optional for MVP scheduling. If no nearby food option exists, the app does not need to suggest a substitute.

## Validation Error Mapping

| Requirement | Fields used | Error shape |
| --- | --- | --- |
| Missing band reference | `performances.band_id` -> `bands.band_id` | `performances.csv row N references unknown band_id X`. |
| Unknown stage in performance | `performances.stage_id` -> `stages.stage_id` | `performances.csv row N references unknown stage_id X`. |
| Unknown stage in distance | `distances.from_stage_id`, `distances.to_stage_id` -> `stages.stage_id` | `distances.csv row N references unknown stage_id X`. |
| Unknown stage in food | `food.near_stage_id` -> `stages.stage_id` | `food.csv row N references unknown stage_id X`. |
| Duplicate ids | Each file's id column | `{file} row N duplicates id X`. |
| Invalid rating metadata links | `youtube_url`, social URL fields | `{file} row N has invalid URL in column X`. |
| Invalid time range | `performances.start_at`, `performances.end_at` | `performances.csv row N end_at must be after start_at`. |
| Stage overlap | `performances.stage_id`, `start_at`, `end_at` | `performances.csv rows N and M overlap on stage_id X`. |
| Invalid walking minutes | `distances.walking_minutes` | `distances.csv row N walking_minutes must be 0 or greater`. |

## Task-6 Implementation Fit

Task-6 is still sufficient for the first parser implementation if it imports:

- `bands.csv`
- `stages.csv`
- `performances.csv`
- `distances.csv`
- `food.csv`

Task-6 should be updated to depend on task-16 and to use this document as the schema source.

Separate follow-up implementation is still needed for:

- Importing Wacken JSON metadata directly from `bandlist-concert.json`.
- Storing rich band metadata beyond the current `Band(name)` domain model.
- User-reviewed data-grid updates for scraped/imported changes.
- Confirming the final Wacken 2026 day/date source once the running order is published.

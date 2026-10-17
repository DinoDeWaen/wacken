# Festival Day And Performance Time Model

Date: 2026-05-15

Related task: task-19

## Scope

Wacken Planner 2026 must support performances across multiple festival days and produce a day-based timeline/PDF.

The model below applies to:

- CSV import.
- Conflict detection.
- Travel feasibility.
- Lunch planning.
- Timeline generation and PDF export.

## Festival Day Structure

The app supports multiple festival days.

Each festival day has:

| Field | Meaning |
| --- | --- |
| `festival_day_id` | Stable id used in imports and scheduling, for example `wed`, `thu`, `fri`, `sat`. |
| `display_name` | User-facing label, for example `Thursday`. |
| `starts_at` | Optional local date/time boundary for the festival day. |
| `ends_at` | Optional local date/time boundary for the festival day. |

MVP import currently requires `festival_day_id` on every performance row. The day id is the grouping key for day-based timelines, while `start_at` and `end_at` are the authoritative values for ordering, overlap detection, and travel checks.

If final Wacken data provides official festival-day ids and date boundaries, use those. If not, the importer may derive `festival_day_id` from local performance date and admin-reviewed mapping.

## Date/Time Format

CSV date/time values use ISO-8601 local date-time:

```text
2026-07-30T18:00:00
```

Rules:

- Store and parse performance times as local Wacken festival times.
- Do not include a timezone offset in CSV fields for MVP.
- Assume Europe/Berlin / local Wacken time when rendering, comparing, or exporting.
- If a future source includes offsets or UTC timestamps, convert them into local Wacken time before storing in the domain model.

Current code note:

- The domain `Performance` model uses `LocalDateTime`.
- The CSV import use case parses `start_at` and `end_at` with `LocalDateTime.parse`.

## Ordering And Overlaps

Within a day:

- Sort timeline slots by `start_at`.
- A performance is valid only when `end_at` is after `start_at`.
- Two performances overlap when `a.start_at < b.end_at` and `b.start_at < a.end_at`.
- Back-to-back performances where one ends exactly when the next starts do not overlap.

Across days:

- Performances should be grouped by `festival_day_id` for display.
- Scheduling calculations should still use actual `start_at` / `end_at` values, not only day labels.

## Midnight-Crossing Performances

A performance that starts before midnight and ends after midnight must use the real end date.

Example:

```csv
performance_id,band_id,stage_id,festival_day_id,start_at,end_at
p-night,band-1,faster,thu,2026-07-30T23:30:00,2026-07-31T01:00:00
```

Expected behavior:

- The row belongs to the `thu` festival timeline because `festival_day_id` is `thu`.
- Sorting uses `2026-07-30T23:30:00`.
- The duration is 90 minutes because `end_at` is on the next calendar date.
- Overlap and travel calculations use the full date/time range.
- PDF display may show `23:30-01:00` under Thursday, optionally with a `+1` day marker if needed for clarity.

Do not represent midnight crossings as `end_at` earlier than `start_at`.

## Implications For Existing Stories

Task-6 CSV import:

- Already uses ISO local date-times.
- Should eventually validate `festival_day_id` against a known festival-days file or configuration when that exists.
- Should keep accepting next-calendar-day `end_at` for midnight-crossing performances.

Future schedule generation:

- Must group by `festival_day_id`.
- Must sort and compare by `LocalDateTime`.
- Must account for cross-midnight durations.
- Must avoid choosing a next performance when travel feasibility crosses midnight incorrectly.

Future timeline/PDF:

- Must render per-day sections.
- Must show cross-midnight slots clearly.
- Should use local Wacken time labels, not UTC.

Open follow-up:

- Confirm official Wacken 2026 festival-day names, dates, and boundaries once the final running order is published.
- Add a `festival_days.csv` or equivalent configuration only if final import needs explicit day metadata beyond `festival_day_id`.

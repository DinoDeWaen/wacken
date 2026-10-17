# Wacken Visual Design System

## Purpose

This document is the visual source of truth for Wacken Planner 2026. It
standardizes the app's dark, metal, festival-ready presentation across band
overview, band detail, group schedule, schedule decision detail, settings, sync,
login, and import/admin flows.

The target feel is professional, uniform, heavy-metal, and practical during
festival use. The UI should scan quickly on a phone in sunlight, a tent, a dark
field, or with weak connectivity.

## Current Visual Audit

### Band Overview

Strengths:

- Compact list density supports fast rating work.
- Dark background, light text, and icon actions already fit the app direction.
- Primary navigation is compact with settings, schedule, and sync-exit icons.

Gaps:

- The page title is muted while other screens use amber, so the hierarchy feels
  inconsistent.
- Buttons and icon actions use hard-coded local colors, which makes future
  screen changes drift.
- Table rows are utilitarian but not yet premium; row spacing, headers, and
  selected/active states should use the same tokens as schedule panels.

### Band Detail

Strengths:

- Own rating, group rating, running order, links, image, and biography are all
  present.
- Group ratings are in the right information context and no longer clutter the
  overview.
- The screen uses the dark app background instead of a default Android surface.

Gaps:

- The top section has several similarly weighted elements, so rating, reset,
  group ratings, running order, and links compete.
- Biography copy is centered, which hurts readability for longer text.
- Image, rating, and metadata need a consistent panel layout on narrow and wide
  screens.

### Group Schedule

Strengths:

- Stage rows with horizontal time columns are the correct mental model for
  overlapping acts.
- Rating borders, scratched skipped acts, lock icons, day filters, and threshold
  filters make the schedule powerful.
- Fixed stage labels on the left improve horizontal scrolling.

Gaps:

- The schedule has advanced visual states that need an always-available legend.
- Filter controls look like raw controls instead of deliberate segmented
  schedule tools.
- The current red/gold/grey states are good, but they should be named tokens
  rather than local constants.

### Schedule Decision Detail

Strengths:

- The dialog uses the dark Wacken scheme and shows chosen act, alternatives,
  stars, stage, status, walking details, and per-person ratings.
- Manual selection is available from the decision context.

Gaps:

- Candidate rows need stronger hierarchy: chosen act, tie, lost alternative,
  lock, and select action should be visually distinct.
- Walking-time evidence should be scannable without becoming a long sentence.
- The close and select actions should follow the same button system as the rest
  of the app.

### Settings

Strengths:

- Group, import, sync, and rating allocation are correctly separated from the
  overview.
- Manual sync has animated feedback.

Gaps:

- The screen is a vertical list of full-width buttons with mixed colors.
- Rating allocation, sync state, account/group identity, and admin actions need
  clearer section grouping.
- Success, pending, and failure messages should use consistent status panels.

### Sync Feedback

Strengths:

- Startup sync can use the full Dino Metal splash.
- Reactivation sync is less intrusive and can run over the current view.
- The app favors cached usage instead of blocking the UI.

Gaps:

- Offline, syncing, pending changes, and failed sync need one visual language.
- Sync messages should be short, stable, and action-oriented.
- The splash and overlay should use the same status tokens as settings.

### Login

Strengths:

- The flow is simple and uses dark background, amber action, and muted helper
  text.

Gaps:

- Inputs use raw rectangular styling and do not share panel/button tokens.
- Error and progress messaging should match the global status system.
- The screen could carry more of the premium metal identity without becoming a
  landing page.

### Import/Admin

Strengths:

- The admin workflow is straightforward and protects ratings during import.
- File selection and result messages are visible.

Gaps:

- The screen uses a different near-black palette from the main app.
- File rows, validation results, and success/failure messages need the same
  panel and status treatment as settings.
- Large validation errors need a readable list treatment.

## Design Tokens

### Color Palette

Use the palette below as named UI tokens. New UI should not introduce unrelated
screen-local colors unless the task explicitly updates this system.

| Token | Hex | Use |
| --- | --- | --- |
| `void` | `#0B0F10` | Full-screen splash, deep overlays |
| `stage-black` | `#121819` | Main app background |
| `iron-panel` | `#20282A` | Default panels, cards, inactive controls |
| `steel-panel` | `#263033` | Elevated panels and selected schedule blocks |
| `grid-steel` | `#434B4E` | Dividers, schedule grid, input borders |
| `text-primary` | `#DCE0E1` | Main text |
| `text-muted` | `#A2A9AB` | Secondary text and metadata |
| `text-faint` | `#697174` | Disabled or background time labels |
| `wacken-gold` | `#FFD24A` | Must-see 5-star, day title, premium emphasis |
| `flame-amber` | `#FFC72C` | Section headings and warnings |
| `metal-red` | `#FF3B6B` | Primary action, 4-star border, critical accent |
| `blood-red` | `#7A1F2F` | Dangerous or exit-related surfaces |
| `steel-grey` | `#AAB3B7` | Optional 2-3-star border and neutral status |
| `success-green` | `#1ED760` | Link/sync success only |

Rating colors:

| Rating | Meaning | Border | Fill |
| --- | --- | --- | --- |
| 5 star | Must see | `wacken-gold` | `#2F2A18` |
| 4 star | Strong choice | `metal-red` | `steel-panel` |
| 2-3 star | Optional or weak choice | `steel-grey` | `iron-panel` |
| 1 star | Veto | none | none; do not show as selected or lost alternative |

### Typography

Use the platform typeface until a deliberate app font decision is made.

| Role | Size | Weight | Use |
| --- | --- | --- | --- |
| Screen title | 26-28sp | Bold | Page title, not inside compact panels |
| Section title | 18-20sp | Bold | Major content sections |
| Row title | 16sp | Bold | Band names and schedule block names |
| Body | 15sp | Regular | Biography and explanatory text |
| Metadata | 12-13sp | Bold or regular | Time, stage, group rating, sync state |
| Dense label | 10-11sp | Bold | Schedule time range and compact legends |

Rules:

- Do not scale font size with viewport width.
- Prefer single-line truncation for schedule blocks and overview rows.
- Long prose such as biographies should be left-aligned, not centered.
- Reserve all-caps for small labels only when it improves scanning.

### Spacing And Shape

- Base spacing unit: 4dp.
- Screen padding: 16dp on phone.
- Panel padding: 10-12dp for dense schedule/rows, 16dp for settings/detail
  panels.
- Control height: 40-44dp for icon buttons, 44-48dp for primary actions.
- Corners: 6dp for panels/cards/blocks, 8dp maximum unless Android native
  controls require otherwise.
- Avoid cards inside cards. Use full-width sections or repeated item panels.

### Buttons And Controls

- Primary action: metal-red background with white text, used for the main
  forward action.
- Premium/confirmation action: wacken-gold background with black text, used
  sparingly for import/confirm states.
- Secondary action: iron-panel background, grid-steel border, text-primary.
- Dangerous/exit action: blood-red background with white text.
- Icon-only actions must have content descriptions and stable 40-44dp square
  dimensions.
- Filters should use segmented controls or toggles, not unrelated button styles.

### Panels, Rows, And Dividers

- Default panel: iron-panel fill, grid-steel border.
- Elevated panel: steel-panel fill, metal-red or wacken-gold border when state
  matters.
- Overview rows should alternate subtly between iron-panel and steel-panel but
  keep text-primary and text-muted stable.
- Dividers should be low contrast and never brighter than content.

### Icons

- Prefer familiar symbols for compact actions: settings, calendar, exit, reset,
  lock, tie, sync, YouTube, Spotify.
- Every icon-only button needs a content description.
- Do not use icon symbols as decorative filler.

### Status And Feedback

Use consistent status panels:

| State | Visual |
| --- | --- |
| Syncing | steel-panel surface, animated metal mark, muted text |
| Offline cached | iron-panel surface, steel-grey border, short cached-data message |
| Pending changes | iron-panel surface, flame-amber border/text |
| Success | iron-panel surface, success-green accent only |
| Warning | iron-panel surface, flame-amber accent |
| Error | iron-panel surface, metal-red accent and specific next action |

Messages must be short and actionable. For festival use, avoid long exception
strings in primary UI; keep diagnostics in logs.

## Screen Standards

### Overview

- Use `stage-black` background, `text-primary` rows, and `text-muted` metadata.
- Keep the table/list dense and stable; row height should not shift during
  rating updates.
- Use shared icon button tokens for settings, schedule, sync-exit, YouTube, and
  Spotify.
- Do not show per-person group ratings in the overview.

### Band Detail

- Top section should be a clear detail panel: band title, image when available,
  own rating, Reset, group ratings, and running order.
- Own rating is the primary interactive control.
- Group ratings appear directly under own rating.
- Running order and links are secondary.
- Biography is left-aligned, readable body text below the primary panel.

### Schedule

- Keep stages fixed on the left and time horizontally scrollable.
- Stage rows use the agreed priority order, with major adjacent stages grouped
  logically.
- Schedule blocks show `HH:mm-HH:mm`, band name, rating stars, and optional lost
  alternative only when space permits.
- Use the rating color tokens for borders and fills.
- Scratched blocks mean lower-rated visible acts that lose an overlap conflict;
  equal ratings do not scratch each other.
- A legend must explain lock, tie, scratched, gold, red, grey, and filters.

### Schedule Decision Detail

- Use a dark panel, never a default white dialog.
- Chosen act appears first and visually stronger than alternatives.
- Ties are first in the lost/alternative evidence and use a clear tie icon.
- Candidate rows show stage, time, stars, per-person ratings, and walking-time
  evidence in predictable order.
- The select action is visually secondary unless the row is the best next
  action.

### Settings

- Group related actions into sections: Account/group, Ratings, Sync, Admin.
- Rating allocation should be a compact stats row, not a paragraph.
- Sync state should show last result, pending work, and offline status when
  available.
- Import is an admin action and should not visually compete with daily-use sync.

### Sync And Startup

- Use the full Dino Metal image only during startup sync.
- Use the smaller moving metal sync animation over the current view for later
  sync events.
- Never block cached reading unless the app has no cached data for the required
  screen.
- Show pending/offline states persistently enough that users trust what they are
  seeing.

### Login

- Keep the screen direct: product name, sign-in inputs, sign-in action, status.
- Use shared input and button styling.
- Do not turn login into a marketing page.

### Import/Admin

- Use the main app palette.
- Each selected file should show name, expected schema, and read/validation
  status.
- Success and validation failures use the global status panel rules.
- Long validation lists should be readable and scrollable.

## Festival Readability Rules

- Critical information must survive glare: high text contrast, stable spacing,
  strong but limited accents.
- Schedule scanning must work at arm's length: time, band, rating, and conflict
  state are first-class.
- Weak network must not look like app failure: cached, syncing, pending, and
  offline are separate visible states.
- Touch targets should stay at least 40dp where possible.
- Avoid dense paragraphs in primary workflows; use compact labels and panels.

## Implementation Priorities

1. Create shared Android visual tokens and component helpers so screens stop
   drifting.
2. Apply the token system to band overview and band detail, including a clearer
   detail header and left-aligned biography.
3. Add schedule legend and polish schedule controls/states.
4. Apply settings/login/import/sync status panels.
5. Revisit typography and image treatment after the shared tokens are in place.

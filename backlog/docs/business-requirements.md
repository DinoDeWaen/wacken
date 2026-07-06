# Business Requirements

This document is the business source of truth for Wacken Planner 2026. It is filled from `project.md` and clarified by user input. Requirements not stated or clearly clarified are listed under `Open questions` instead of being invented.

## Read Me First

Use this section for most tasks. Read deeper sections only when the active task touches that area.

### Current Product Scope

Wacken Planner 2026 is an Android app for one shared friend group to rate Wacken bands and prepare a conflict-aware festival schedule. The current implementation focuses on band import, band listing, band detail, 1-5 ratings with unrated state, local cache behavior, Supabase-backed lifecycle master-data sync, Supabase Auth, shared rating sync, and MVP2 schedule planning. MVP3 extends the product toward festival-field use with rating export, post-show real ratings, and stronger no-Wi-Fi operation.

### Current Implemented Capabilities

- Android band overview and detail screens, with generic Metal Battle placeholder entries hidden from rating lists.
- 1-5 band ratings with default unrated value of `0`, including clearing a prior rating back to unrated.
- Room local cache for fast app reads, offline continuity, and queued pending shared-data changes.
- Supabase Auth for user identity.
- Supabase Postgres/Flyway backend for central master data, group membership, and ratings.
- Supabase sync for bands, stages, performances, stage distances, food options, group ratings, and group schedule locks on app start, overview reactivation, manual sync, and close, with cached app data shown before lifecycle sync completes.
- CSV/TSV fallback import path for local/admin data work.
- Wacken-inspired overview/detail presentation with music links, imported metadata where available, and metal-themed sync feedback.
- Compact per-person star details on the band detail and schedule decision detail screens when shared group ratings are available.
- MVP2 group decision rules, conflict resolution, timeline generation, group-wide locked manual schedule choices, and Android schedule viewing for the current shared group.
- The canonical current shared group is named `Sofie and Dino`, and existing app users must be members so their ratings participate in MVP2 planning.
- Android share-sheet invite text for the single shared `Sofie and Dino` group, using provisioned Supabase accounts and no token/deep-link flow.
- Planned MVP3 capabilities include exporting ratings to CSV from settings, recording a separate real post-show rating on band detail, and using cached/imported data without Wi-Fi during the festival.

### Current Non-Goals

- Multiple independent groups in the current version.
- Play Store distribution before a later delivery phase.
- Business logic in Android UI, Activities, or Fragments.
- Android instrumentation tests unless a future task makes them meaningful.

### Business Rules Index

| Area | Rules | Read when |
| --- | --- | --- |
| Ratings and group decisions | BR-001 to BR-021 | Rating, veto, effective rating, group decision, conflict resolution |
| Schedule timeline | BR-022 to BR-032, BR-073 to BR-074 | Scheduling, walking-time context, conflict alternatives |
| Festival data import | BR-033 to BR-046 | CSV import, Supabase master data, Room cache, admin data, band-only imports |
| Overview, detail, and app state | BR-047 to BR-072 | Wacken UI, metadata, music links, loading, sync feedback, settings, calendar schedule, returning to app context |
| MVP3 field use and export | BR-077 to BR-080 | Rating export, real post-show ratings, and no-Wi-Fi festival use |

### Requirement Drift Markers

- Supabase Auth, Supabase Postgres, Flyway migrations, Room cache, and rating sync are now part of the implemented system.
- Older notes that describe authentication, backend sync, or production persistence as future-only must be treated as superseded unless a newer task or ADR says otherwise.
- `README.md` describes current setup and architecture. This file describes business rules, product scope, workflows, non-goals, and open questions.

## Requirement Boundary Rule

This document describes business behavior, terminology, outcomes, constraints, and externally meaningful rules.

It must not be read as approval to expose internal domain configuration, technical policy selection, infrastructure concerns, framework choices, or implementation details through public callers.

Preferred interpretation:

- Users and external systems provide business data through stable public contracts.
- The domain/application core owns business classification, policy selection, invariants, and rule execution.
- Public API changes, caller-provided domain policy, or externally configured business rules require explicit architecture approval.

Project-specific boundary notes:

- Business logic must not live in Android UI, Activities, or Fragments.
- Domain, application, infrastructure, and UI concerns must remain strictly separated.
- Business rules for ratings, vetoes, conflicts, walking-time context, and manual group choices belong in the domain/application core, not in UI or infrastructure code.

## Business goals

- Goal 1: Enable a group of friends to combine individual band preferences into one shared festival schedule.
- Goal 2: Prevent schedules that are confusing or unusable because overlapping performances are not handled clearly.
- Goal 3: Respect strong preferences, must-see ratings, vetoes, walking-time context, and manual group choices when producing the schedule.
- Goal 4: Make the final in-app schedule clear enough to use during the festival.
- Goal 5: Get the band rating feature working first so the group can begin scoring the lineup before final stage times are available.
- Goal 6: Support reliable festival data import so bands, stages, performances, distances, and food options can be planned from validated file-based CSV uploads or proposed website-scraped changes.
- Goal 7: Present the band overview with a Wacken-inspired visual style so early rating feels like a festival lineup experience rather than a technical data table.
- Goal 8: Keep the rating workflow responsive and stateful so users understand when data is loading and can return to the same app context after switching apps or opening music links.
- Goal 9: Keep persistence backend-replaceable by treating MVP TSV storage as a backend-like source and using a local device cache for fast app reads.
- Goal 10: Let users take their ratings and festival experience data home by exporting ratings and recording the real rating after seeing a band.
- Goal 11: Keep the app useful at Wacken when Wi-Fi or mobile data is unavailable, as long as the needed festival and group data has already been cached or imported.

## Product context

Product name: Wacken Planner 2026.

Wacken Planner 2026 is an Android application for Wacken Open Air 2026. It helps a group of friends rate bands and automatically compute a shared, conflict-aware festival schedule.

The app considers individual preferences, group veto rules, overlapping performances, stage distances where available, walking-time context, and group-wide manual schedule choices.

Target users:

- Primary user: festival attendee who rates bands and uses the generated schedule.
- Secondary user: one shared friend group planning a shared festival schedule.
- External system/user: Wacken line-up pages and validated CSV festival data sources for bands, stages, performances, distances, and food options.
- Admin/support user: admin who imports, reviews, validates, and updates festival data.

Business problem:

- Groups need a practical way to turn individual band preferences into one shared plan.
- Manually planning around overlaps, vetoes, and walking time is error-prone.
- Without schedule feasibility rules, the group may choose performances that cannot realistically be attended together.

Desired outcome:

- Users can rate bands on a shared 1-5 scale, with `0` reserved for unrated bands.
- The app can propose a shared schedule that respects decision rules, conflicts, walking-time context, and manual group choices.
- The final timeline is clear, day-based, and usable on Android during the festival.

Constraints:

- Primary platform is Android.
- Java and Gradle are required.
- Debug APK output is required for local installation.
- A web app is optional for MVP only if it significantly simplifies early delivery.
- Domain-Driven Design and Clean Architecture are mandatory.
- TDD is mandatory for new behavior.

Existing integration:

| System | Purpose | Direction | Notes |
| --- | --- | --- | --- |
| Wacken line-up website | Provides current band list and later detailed lineup data when available. | Inbound | Initial import may scrape the official band list and propose changes for user validation. The official pages inspected were the Wacken band list and artist detail URLs, but the dynamic artist-card data still needs schema investigation. |
| Festival CSV files | Provide bands, stages, performances, distances, and food data. | Inbound | CSVs are uploaded through Android file selection. They must be validated for missing references, overlaps, and unknown stages once final stage and time data is available. |
| MVP TSV source | Acts as the current backend-like persistence source for imported festival data and ratings. | Internal source | The app caches data locally in Room and writes through to TSV files so the TSV source can later be replaced by a backend API without changing domain behavior. |
| Supabase Postgres | Central backend database for shared ratings and admin-managed festival data. | Backend | Schema is managed through Flyway migrations. Bands can be uploaded from the repository CSV through an idempotent backend import script. |
| CI artifact storage | Stores downloadable APK artifacts. | Outbound | CI must produce a clearly versioned debug APK artifact. |

## Non-goals

- Non-goal 1: Do not make a web app the primary delivery target.
- Non-goal 2: Do not put business logic in Android UI, Activities, or Fragments.
- Non-goal 3: Do not build Play Store distribution before the later optional delivery phase.
- Non-goal 4: Do not implement UI tests where they are not meaningful; features start with domain tests and application tests.
- Non-goal 5: Do not support multiple independent groups in the current version; multi-group support is deferred to next year.

## Delivery roadmap

The delivery roadmap captures the planned or completed increments for the current application:

| Increment | Outcome | Status |
| --- | --- | --- |
| MVP 1 | Establish the Android foundation, CI, domain model, band listing, first-priority band ratings, initial lineup import, and unit test setup. | Not specified in `project.md` |
| MVP 2 | Enable one-group planning with a decision engine, conflict resolution, and timeline generation. | Not specified in `project.md` |
| MVP 3 | Improve festival-field usefulness with rating CSV export, post-show real ratings, and no-Wi-Fi cached operation. | Planned |

Future production direction, not implemented:

- Free distribution through Play Store or an internal testing track.
- Web app, only if it significantly simplifies early delivery.
- Android instrumentation tests as a later-phase option for the QA suite.
- Multiple independent groups for next year's version.

Release assumptions:

- MVP delivery produces a debug APK for local installation.
- CI artifacts are the MVP delivery target.
- CI must fail when required automated tests fail.

## Business capabilities

| Capability | Description | Priority | MVP |
| --- | --- | --- | --- |
| Band rating | Let users rate bands on the 1-5 preference scale, with `0` reserved for unrated bands. This is the first product priority. | Must | MVP 1 |
| Band listing | Show bands in a responsive Wacken-themed overview with alphabetical sorting by band name, rating, stage, time information when available, loading feedback when needed, and same-row music-link actions. | Must | MVP 1 |
| Band detail view | Show band details in a style inspired by the official Wacken band detail screen, with English biography when available, image metadata when available, editable own rating stars, read-only group member ratings when available, schedule information, and optional YouTube and Spotify links. | Must | MVP 1 |
| Initial lineup import | Import or scrape the current Wacken band list before final stages and times are available. Band-only imports must be visible and rateable. English biography and image metadata are meaningful UI inputs when available. | Must | MVP 1 |
| Festival data import | Import validated CSV files for bands, stages, performances, distances, and food options through Android file upload once final lineup data is available. Re-import updates festival master data while preserving user ratings. | Must | MVP 1 |
| Data review grid | Propose scraped or imported data changes in a user-validated data grid, with line-by-line validation. | Should | MVP 1 |
| One-group planning | Combine ratings from one shared friend group into shared decisions. | Must | MVP 2 |
| Friend invites | Allow friends to join the shared group through the most useful shareable invite format for Android. | Should | MVP 2 |
| Decision engine | Decide whether the group should go, optionally go, or not go to a band. | Must | MVP 2 |
| Conflict resolution | Resolve overlapping performances according to the authoritative group rules. | Must | MVP 2 |
| Timeline generation | Generate a day-based shared festival timeline. | Must | MVP 2 |
| App navigation and settings | Keep primary actions compact and move secondary/admin actions into a settings page. | Must | MVP 2 |
| Calendar schedule view | Show the group schedule as a day-filtered calendar with fixed stages on the left and horizontally scrollable time columns across the top. | Must | MVP 2 |
| Manual schedule selection | Let the group choose an alternative act for a conflict and update the visible schedule result. | Must | MVP 2 |
| Schedule visual polish | Keep calendar, decision detail, and sync/startup feedback consistent with the Wacken dark heavy-metal presentation. | Must | MVP 2 |
| Walking-time schedule visibility | Apply the agreed MVP walking-time defaults and show movement time in the group schedule where known. | Must | MVP 2 |
| Rating export | Export all locally available band ratings to a CSV file from settings. | Must | MVP 3 |
| Post-show real rating | Let a user record a separate real rating after seeing a band, without changing the planning rating used for group scheduling. | Must | MVP 3 |
| No-Wi-Fi field mode | Keep cached lineup, band details, own ratings, real ratings, and generated schedules usable without Wi-Fi or mobile data after initial data is available. | Must | MVP 3 |

## Domain context

The bounded context is `Festival Group Scheduling`.

In scope:

- Rating bands for Wacken Open Air 2026.
- Combining individual ratings into group decisions.
- Supporting one shared group for the current version.
- Resolving overlapping performances.
- Showing walking-time context between selected performances.
- Producing a clear day-based in-app timeline.
- Importing and validating festival data from CSV files or proposed website-scraped changes.

Out of scope:

- Play Store distribution for the MVP.
- Primary web application delivery.
- Multiple independent groups in the current version.
- Business logic in Android UI components.
- Production persistence, API, messaging, authentication, or payment behavior; none is specified in `project.md`.

## Key workflows

### Workflow 1: Import or propose festival data

An admin uploads CSV files or asks the app to scrape the Wacken line-up website. CSV file upload is the MVP import path. Later scraped or reviewed changes may be proposed in a grid so the user can validate updates line by line.

```gherkin
Scenario: Import festival data from selected CSV files
  Given the admin has selected CSV files for bands, stages, performances, distances, and food
  When the import is started
  Then the app validates the selected files before updating festival master data
  And existing band ratings are preserved
```

### Workflow 2: Rate bands

A festival attendee rates bands using the shared 1-5 scale.

```gherkin
Scenario: Rate a band
  Given a listed band with performance information
  When a user rates the band from 1 to 5
  Then the rating is available for group decision rules
```

### Workflow 3: Review band details

A festival attendee reviews band information in a detail screen inspired by the official Wacken band detail screen, with rating stars and optional music links.

```gherkin
Scenario: Review and rate a band
  Given a band is available in the lineup
  When a user opens the band detail screen
  Then the user sees band information, English biography when available, image when available, rating stars, schedule status, and optional YouTube and Spotify links when available
```

### Workflow 4: Browse the Wacken-themed band overview

A festival attendee browses the imported lineup in a visual overview inspired by Wacken styling.

```gherkin
Scenario: Browse imported bands
  Given bands have been imported
  When the user opens the band overview
  Then each band is shown in a responsive Wacken-themed overview sorted by band name
  And the user can see loading feedback if the list is not ready immediately
  And the user can open band details from the overview
```

### Workflow 4a: Return to the previous app context

A festival attendee can leave the app, open music links, and return without losing the Wacken Planner screen they were using.

```gherkin
Scenario: Return to the previous Wacken Planner screen
  Given the user is using the band overview or a band detail screen
  When the user leaves the app and later returns
  Then the same Wacken Planner screen is restored
  And no unrelated browser or new-tab screen is shown as the app context
```

### Workflow 5: Compute a shared schedule

The app combines group ratings, conflicts, walking-time context, and manual group choices to produce a timeline.

```gherkin
Scenario: Generate a conflict-aware group schedule
  Given a group has rated bands for a festival day
  And performances may overlap or require walking between stages
  When the schedule is generated
  Then the timeline respects group decision rules, conflicts, walking-time context, and manual group choices
```

### Workflow 6: Use the calendar schedule

The group reviews a clear per-day calendar schedule for use during the festival.

```gherkin
Scenario: View calendar festival schedule
  Given a shared schedule has been generated
  When a user views the daily calendar schedule
  Then each festival day is shown with hour lines
  And selected performances are shown as blocks with band, stage, and rating stars
```

### Workflow 7: Export ratings

A user exports rating data from settings so it can be reviewed outside the app.

```gherkin
Scenario: Export all locally available band ratings
  Given the app has locally available band and rating data
  When the user exports ratings from settings
  Then a CSV file is created through Android file sharing or saving
  And every locally known band is represented
  And planning ratings, real post-show ratings, group member ratings where locally cached, and schedule metadata are included where available
```

### Workflow 8: Record a real post-show rating

After seeing a band, a user records how good the performance really was.

```gherkin
Scenario: Rate a band after seeing it
  Given a user has opened a band detail screen
  When the user sets a real post-show rating
  Then the real rating is saved separately from the planning rating
  And the group schedule decision rules are not recalculated from the real rating
```

### Workflow 9: Use the app without Wi-Fi

During the festival, a user can keep using cached app data without a network connection.

```gherkin
Scenario: Use cached festival data offline
  Given the app has previously synced or imported festival data
  And the device has no Wi-Fi or mobile data connection
  When the user opens the app
  Then cached band details, ratings, real ratings, and group schedule remain usable
  And edits are saved locally and queued for later sync where sync is applicable
```

```gherkin
Scenario: Inspect and change a schedule conflict choice
  Given a selected performance has alternatives
  When a user opens the performance block detail
  Then the chosen act and all alternatives are shown with stage and rating stars
  And the user can select an alternative as the act the group is going to
  And the visible schedule result changes to that selection
```

## Business rules

| Rule ID | Rule | Example | Priority |
| --- | --- | --- | --- |
| BR-001 | Ratings must use the 1-5 scale, with `0` reserved for unrated bands. | `1` means veto; `5` means must see; `0` means no rating has been given. | Must |
| BR-001a | A user can clear their own band rating back to unrated. | Clearing a previous score stores the local value as `0` and removes that user's explicit Supabase rating contribution for the band after sync. | Must |
| BR-002 | A rating of `1` means veto. | A band rated `1` by a user counts as a veto in group decision rules. | Must |
| BR-003 | A rating of `2` means OK or indifferent. | A band with max rating `2` is optional. | Must |
| BR-004 | A rating of `3` means like, fine to miss. | A band with max rating `3` can be selected unless vetoed. | Must |
| BR-005 | A rating of `4` means want to see. | A band with max rating `4` goes unless it has 2 or more vetoes. | Must |
| BR-006 | A rating of `5` means must see. | Any band with at least one `5` is preferred by the decision rules. | Must |
| BR-007 | If any group member rates a band `5`, the single-band decision is `GO`. | One must-see rating is enough to go. | Must |
| BR-008 | If the maximum rating is `4`, the single-band decision is `GO` unless there are 2 or more vetoes. | Two vetoes block a band whose highest rating is `4`. | Must |
| BR-009 | If the maximum rating is `3`, the single-band decision is `GO` unless there is any veto. | One veto blocks a band whose highest rating is `3`. | Must |
| BR-010 | Reserved for future use. | No active MVP2 lunch-window requirement. | Could |
| BR-011 | If the maximum rating is `2`, the single-band decision is `OPTIONAL`. | A group that is only indifferent may optionally attend. | Must |
| BR-012 | If the maximum rating is `0`, the band has no ratings yet and is treated as unrated. | A fully unrated band is not treated as vetoed. | Must |
| BR-013 | For overlapping performances, prefer any band with a `5`. | A must-see band wins over lower-rated alternatives. | Must |
| BR-013a | For schedule conflict resolution and alternatives, two performances count as overlapping only when their middle 30-minute windows overlap. | If two acts only overlap at the start or end of their full performance time, they can both appear in the schedule. | Must |
| BR-014 | If overlapping performances both have at least one `5`, choose the one that reduces travel time by being closest to the previous selected band or best positioned for the next selected band. | Between two must-see conflicts, choose the option that makes the route more feasible. | Must |
| BR-015 | If overlapping options only have ratings of `4`, choose the band with the most `4` ratings. | Two want-to-see ratings beat one want-to-see rating. | Must |
| BR-016 | If overlapping options with ratings of `4` tie, choose the band with fewer vetoes. | One veto beats two vetoes. | Must |
| BR-017 | If overlapping options with ratings of `4` remain tied, choose the shortest travel distance. | The closer stage wins after rating and veto tie-breakers. | Must |
| BR-018 | If overlapping options only have ratings of `3`, the result is `OPTIONAL` and the band with the most `3` ratings is chosen. | More like/fine-to-miss ratings decide optional choices. | Must |
| BR-019 | If overlapping options only have ratings of `2`, the result is `OPTIONAL`. | Indifferent choices do not become mandatory. | Must |
| BR-020 | An unrated band member rating defaults to `0`. | If someone has not rated a band, that missing rating is stored as unrated and does not count as a veto. | Must |
| BR-021 | If all overlapping options are vetoed, no performance is selected. | All-vetoed conflicts produce no selection. | Must |
| BR-022 | The schedule must show walking-time context between consecutive selected performances when known. | A move between stage groups shows 15 minutes; a move within a nearby group shows 5 minutes. | Must |
| BR-023 | Walking-time context uses walking minutes by default. | Known imported distances can be used; otherwise MVP nearby-stage defaults apply. | Must |
| BR-024 | Walking time may become a user setting later. | A future user can tune travel assumptions to their walking speed. | Could |
| BR-025 | Visible overlap marking may account for walking time between nearby performances. | If overlap plus required movement makes two visible acts impractical, the lower-rated visible act can be scratched. | Must |
| BR-026 | When choosing between equal `4`-rated options, prefer the one closest to the previous `5`-rated performance when distance context is available. | Travel proximity to a previous must-see band breaks the tie. | Must |
| BR-027 | Reserved for future use. | No active MVP2 requirement. | Could |
| BR-028 | Reserved for future use. | No active MVP2 requirement. | Could |
| BR-029 | Reserved for future use. | No active MVP2 requirement. | Could |
| BR-030 | Reserved for future use. | No active MVP2 requirement. | Could |
| BR-031 | Each timeline slot must show selected band, winner rating stars, stage, time range, walking time to next stage, and lost alternative with rating stars where applicable. | Users can understand the plan, why the winner was selected, and whether the closest rejected option is still worth considering. | Must |
| BR-032 | A lost alternative is the second-highest band: the performance that lost to the selected performance and could still be chosen manually if preferred. | If the winning band is not good enough, the user can inspect the runner-up. | Must |
| BR-032a | If the lost alternative tied the winner on all existing conflict criteria before the final input-order fallback, it must be marked as a tied alternative and shown first after the chosen act in decision details. | Users can see when the app chose between equal options rather than a clearly better winner. | Must |
| BR-033 | CSV import validation must detect missing references, overlaps, and unknown stages. | An imported performance cannot reference an unknown stage without validation feedback. | Must |
| BR-034 | Scraped or imported data changes should be proposed in a data grid for line-by-line user validation. | The user can accept or reject each proposed band or performance update. | Should |
| BR-035 | The initial lineup import may contain only bands before final stage and time data is available. | Early rating can start from a band list without performance times. | Must |
| BR-036 | The band rating screen should follow the official Wacken band detail style where practical, adding rating stars and optional YouTube and Spotify links. | A band detail page shows band information plus star rating controls. | Must |
| BR-037 | The current version supports one shared group, not multiple independent groups. | All ratings belong to "my group" for now. | Must |
| BR-038 | Multi-group support is deferred to next year. | Separate friend groups are not part of the current scope. | Must |
| BR-038a | Existing app users must belong to the `Sofie and Dino` shared group for MVP2. | Ratings from Sofie, Dino, and any existing signed-in users participate in one shared schedule. | Must |
| BR-038b | The MVP2 invite action must share onboarding text for the single `Sofie and Dino` group without secrets or token links. | A friend receives instructions to install the APK, sign in with a provisioned Supabase account, and sync ratings into the shared group. | Must |
| BR-039 | Reserved for future use. | No active MVP2 requirement. | Could |
| BR-040 | MVP CSV import must be file-upload based in Android, not paste-text based. | The user selects `bands.csv` and companion CSV files with the Android document picker. | Must |
| BR-041 | The import screen must show which CSV files were selected before import. | After selecting `bands.csv`, the screen shows the chosen file name. | Must |
| BR-042 | A successful CSV import updates festival master data for bands, stages, performances, distances, and food. | Re-importing a newer Wacken band list replaces the stored lineup data. | Must |
| BR-043 | Re-importing festival master data must preserve existing ratings. | If a user rated 5th Avenue as `5`, importing updated CSV files must not erase that rating. | Must |
| BR-044 | Ratings are user/group preference data, not festival master data. | Updating lineup CSVs must not clear or overwrite rating records. | Must |
| BR-045 | Band-only imports must be visible in the band overview before performance times are available. | A Wacken lineup CSV without stages/times still shows bands for rating. | Must |
| BR-046 | Bands without performance data must be clearly marked as not scheduled yet. | A band imported without a performance shows `Not scheduled yet` and `TBA`. | Must |
| BR-047 | The band overview must use a Wacken-inspired visual style with dark presentation and amber/yellow emphasis where practical. | Band cards look festival-themed rather than like raw form controls. | Should |
| BR-047a | The app should follow one professional dark metal visual design system across daily-use and admin screens. | Overview, band detail, schedule, schedule decision detail, settings, sync feedback, login, and import/admin flows use consistent colors, typography, spacing, buttons, panels, icons, and status states while remaining readable in festival conditions. | Should |
| BR-048 | The Wacken-themed band overview must keep the rating workflow reachable. | Tapping a band card opens the band detail/rating screen. | Must |
| BR-049 | The band overview must feel responsive and must not leave the user staring at a blank or frozen screen. | If loading or rendering is not immediate, a loading indication appears quickly. | Must |
| BR-050 | The band overview must sort bands alphabetically by band name unless the user explicitly chooses another sort later. | 5th Avenue appears before Airbourne regardless of import order or performance time. | Must |
| BR-050a | Generic Metal Battle placeholder acts must be hidden from rating lists. | Imported entries whose names are generic Metal Battle placeholders, such as `Metal Battle` or `Metal Battle tba.`, are preserved in master data but are not shown in the band overview or counted in rating allocation summaries. Real named bands that only mention Metal Battle are not hidden by this rule. | Must |
| BR-051 | Band detail biography should prefer English source text when available and render it as readable text. | If `biography` and `biographyDe` both exist in imported source data, the detail screen shows the English text without raw HTML tags or entities. | Must |
| BR-052 | Band detail should show available band image metadata and must not show broken image placeholders. | If a band has an image URL, it is shown; if not, no broken image is displayed. | Must |
| BR-053 | Band detail layout should place the image and primary band controls together, with the explanation below, while remaining usable on phone screens. | Image is left and rating/stage/time/actions are right when space allows; narrow screens stack cleanly. | Must |
| BR-054 | Returning to the app must restore the user’s last Wacken Planner screen instead of showing an unrelated browser or new-tab state. | Returning from another app brings the user back to the same overview or detail screen. | Must |
| BR-054a | Returning from band detail must keep the selected overview band row visible after refresh. | A user opens Skyline, changes or reviews the rating, goes back, and sees the Skyline row instead of being moved to the top of the band list. | Must |
| BR-055 | External music links must not destroy or replace the app’s internal navigation state. | Opening YouTube or Spotify and returning restores the previous Wacken Planner context. | Must |
| BR-056 | Authenticated Supabase calls must renew expired access tokens when the refresh token is still valid. | Master-data sync and rating sync continue after normal JWT expiry; if refresh token renewal is explicitly rejected because the session is invalid, the local session is cleared and the user returns to login. If renewal cannot reach Supabase because the device is offline or the network fails, the local session is preserved so cached data remains usable. | Must |
| BR-057 | The signed-in app must sync Supabase master data, group ratings, and group schedule locks when the app starts or is reactivated without blocking cached app usage. | A user opening the app on a second Android device sees locally cached data immediately while Supabase refreshes in the background; when sync succeeds, shared group ratings and manual schedule locks refresh without needing to force-close the first device. | Must |
| BR-058 | The app must provide a close action that attempts Supabase sync before closing. | Tapping close pushes local pending ratings and pulls group ratings before the app exits; if sync fails, local ratings remain available and the app stays open with a clear failure message. | Must |
| BR-059 | Sync operations must show clear Wacken/metal-themed feedback and prevent conflicting sync/close actions while in progress. | Startup, reactivation, manual sync, and close sync show visible progress instead of a blank or frozen screen. | Must |
| BR-060 | Band detail and schedule decision detail should show available per-person group ratings as compact read-only detail under the user's or candidate's main rating. | Detail screens can show each available person rating as small stars without changing the main editable rating workflow, while the band overview stays clean. | Should |
| BR-061 | Primary app navigation must use compact icon actions for high-frequency destinations. | The overview shows a cog for settings, a calendar icon for the group schedule, and an exit action for sync-and-exit. | Must |
| BR-062 | Secondary and admin-style actions must live on the settings page. | Group/invite actions, lineup import, and manual sync are moved out of the primary overview action area and into settings. | Must |
| BR-063 | The group schedule must be shown as a day-filtered calendar. | The user chooses one festival day, and that view shows only that day's fixed stage rows, horizontally scrollable time axis, and performance blocks. | Must |
| BR-063a | The calendar schedule must use stage rows so overlapping or near-overlapping selected acts remain readable. Louder and Harder must be adjacent and shown before other stage rows when present. | If Louder, Harder, Faster, and Wackinger acts overlap, each act appears in its stage lane instead of being drawn on top of the others. | Must |
| BR-064 | Calendar performance blocks must summarize the selected act. | A block shows compact `HH:mm-HH:mm` time range as its first line, then band name and rating stars; the stage is provided by the stage row rather than repeated inside the block. | Must |
| BR-064a | The calendar time axis must keep hour markers clear and half-hour markers subtle. | The top axis labels only full hours, uses full vertical lines for hour columns across all stage rows, and uses a small notch with an unlabeled dotted vertical line across all stage rows for half-hour columns. | Must |
| BR-064b | Calendar performance blocks must visually highlight rating strength and skipped visible overlaps. | A 5-star selected act uses a gold border, a 2-star selected act uses a light grey border, other selected acts keep the red accent border, and a lower-rated visible block whose raw overlap plus walking time beyond the nearby-stage 5-minute allowance exceeds 15 minutes shows broad light diagonal scratch bands in the border color family. Equal ratings do not bar each other automatically. | Must |
| BR-065 | Opening a calendar performance block must show the conflict detail. | The detail shows the chosen act and all alternatives, with each band's stage and rating stars. | Must |
| BR-066 | A user can select an alternative as the act the group is going to for the visible schedule screen. | Choosing an alternative updates the local visible schedule result so that act becomes selected for that conflict. | Must |
| BR-067 | Manual schedule choices must not silently change the underlying rating rules. | Selecting an alternative changes the local visible schedule choice, but the original ratings and generated decision evidence remain visible. | Must |
| BR-068 | MVP2 manual schedule choices are group-wide locked overrides with offline-first local persistence. | A signed-in group member can lock a conflict winner for the shared group; the lock is saved locally immediately, queued for Supabase sync when offline, survives schedule regeneration and app restart, is shown with a lock icon, and remains until another group member changes or clears it. Ratings and generated decision evidence are not changed by the lock. | Must |
| BR-069 | Calendar schedule days must include late-night festival time through 02:00 before the next festival day starts. | A performance ending at 02:00 appears in the intended festival day view instead of being hidden by a midnight cutoff. | Must |
| BR-070 | Calendar schedule day headings must include the weekday name. | A day heading shows a weekday such as Monday or Tuesday together with the festival date. | Must |
| BR-071 | Schedule decision details must use the Wacken dark/metal visual scheme. | Opening a performance block shows chosen act and alternatives on a dark themed surface with readable light text and accent colors, not a default white dialog. | Must |
| BR-072 | Splash and sync feedback should use a stronger heavy-metal visual style. | Startup, reactivation, manual sync, and close sync feedback feel visually aligned with Wacken/metal branding rather than generic loading. | Should |
| BR-073 | MVP walking-time defaults must include named nearby-stage groups. | Harder, Faster, and Louder are 5 minutes apart from each other; Headbangers Stage and W:E:T Stage are 5 minutes apart from each other; travel between these groups or to other stages defaults to 15 minutes unless the stage is the same. | Must |
| BR-074 | The group schedule must show walking time between consecutive selected acts when known. | If the group moves between nearby stages in the same walking group, the schedule shows 5 minutes; if it moves between stage groups, the schedule shows 15 minutes. | Must |
| BR-075 | The group schedule can locally hide barred overlapping acts. | A user can toggle the schedule view so visible blocks and decision-detail candidates marked as barred/scratched because they lose an overlap to a higher-rated visible act are hidden without changing ratings, generated decisions, manual choices, persistence, or sync data. | Must |
| BR-076 | The group schedule can locally hide acts at or below a selected star threshold. | A user can choose an inclusive threshold such as 2 stars, and visible blocks plus decision-detail candidates rated at or below that threshold are hidden without changing ratings, generated decisions, manual choices, persistence, or sync data. | Must |
| BR-077 | MVP3 settings must provide a CSV export for all locally known bands and ratings. | The export contains one row per locally known band and includes stable band identity, display name, planning rating, real post-show rating, available group member ratings, stage/date/time where known, and schedule status where known. | Must |
| BR-078 | A real post-show rating is separate from the planning rating. | A user can rate a band `5` for planning but later give the actual performance a real rating of `3`; the group schedule still uses the planning rating unless a future requirement says otherwise. | Must |
| BR-079 | Real post-show ratings use the same visible 1-5 scale with `0`/empty as unrated. | A band not yet seen has no real rating; setting or resetting the real rating must not clear the planning rating. | Must |
| BR-080 | The app must remain useful without Wi-Fi or mobile data after festival data has been cached or imported. | A user at Wacken with no connection can open cached lineup, band detail, ratings, real ratings, settings, and group schedule without being logged out only because Supabase cannot be reached; changes are stored locally and queued for later sync when sync is supported. | Must |

## Data and terminology

| Concept | Description | Important fields | Invariants |
| --- | --- | --- | --- |
| Band | A musical act at Wacken Open Air 2026. | Name, optional English biography, optional image metadata, optional music links, optional schedule status | Can be associated with one or more performances; can also exist before performances are known. |
| Performance | A scheduled band appearance. | Band, stage, time range | Must reference known band and stage data. |
| Stage | A festival podium or location where performances happen. | Name or identifier | Must be known before performances can reference it. |
| Distance | Travel information between stages. | From stage, to stage, walking minutes by default | Used to show walking-time context and support tie-breaking. |
| User | A festival attendee who can rate bands. | User identity details are not finalized. | Can provide ratings; missing ratings default to `0` unrated. |
| Group of friends | The single shared group for the current version. | Members are not fully specified. | Group decisions use all member ratings; multiple groups are deferred. |
| Rating | A user's preference for a band. | Value from 1 to 5 when explicit; `0` only represents unrated/no explicit rating. | Must use the defined rating scale. |
| Food option | Imported festival food master data. | Name and optional location metadata | Preserved for future planning but not active in MVP2 schedule behavior. |
| Schedule | A conflict-aware festival plan. | Day, slots, selected performances, alternatives, walking time | Must respect decision rules, conflicts, walking-time context, and manual group choices. |
| Timeline slot | One visible item in the daily schedule. | Selected band, stage, time range, walking time, lost alternative | Must be clear enough for in-app timeline display. |
| Festival master data | Imported lineup data used for planning. | Bands, stages, performances, distances, food options | Can be replaced by a successful CSV re-import. Must not include user ratings. |
| User rating data | Group preference data for bands. | User/group identity, band, rating value | Must be preserved when festival master data is updated. |
| Band overview row | Visual representation of a band in the overview. | Band name, schedule status, effective rating, optional music-link actions | Must open band details, support the rating workflow, and keep same-row actions clear. |

## Business object model

| Object | Responsibility | Relationships |
| --- | --- | --- |
| Festival | Provides the overall Wacken Open Air 2026 planning context. | Contains bands, stages, performances, distances, and food options. |
| Band | Represents an act that users can rate and potentially attend. | Has performances; receives ratings from users. |
| Performance | Represents when and where a band plays. | Belongs to a band and a stage; may overlap other performances. |
| Stage | Represents a festival location or podium. | Has performances; has distances to other stages. |
| User | Represents an attendee participating in planning. | Provides ratings; may belong to a group. |
| Group | Represents the current single friend group making shared decisions. | Contains users and their ratings; produces group decisions. Multiple independent groups are deferred. |
| Rating | Represents a user's preference or veto for a band. | Belongs to a user and band; feeds decision rules. |
| Real post-show rating | Represents a user's actual assessment after seeing a band. | Belongs to a user and band; does not feed planning decision rules unless a future requirement explicitly changes that. |
| Schedule | Represents the selected day-by-day plan. | Contains timeline slots, selected performances, alternatives, and walking-time context. |
| Timeline Slot | Represents a selected schedule entry. | Shows performance, travel time, and the lost alternative. |
| Food Option | Represents imported festival food master data. | Reserved for future planning behavior. |
| Festival Master Data | Represents imported source data for the festival. | Is updated by CSV import; excludes ratings. |
| Band Overview Row | Represents a visible, tappable band summary. | Opens band detail and displays effective rating, schedule status, and optional music-link actions. |
| Settings page | Represents secondary app actions and operational controls. | Contains group/invite, import, and manual sync actions. |
| Rating export | Represents a generated CSV snapshot of locally available band and rating data. | Is created from cached app data and can be shared or saved through Android. |
| Calendar schedule day | Represents one selected festival day in calendar form. | Contains stage rows, a horizontal time axis, and time-positioned performance blocks, including late-night festival time through 02:00 before the next festival day starts. |
| Performance block | Represents a selected scheduled act in the calendar view. | Shows time range, band, rating stars, and opens the conflict detail; the stage is provided by the row label. |
| Manual schedule choice | Represents a group-locked selected act for a conflict. | Overrides the visible selected act for that conflict without changing ratings or generated decision evidence. |
| Walking-time segment | Represents movement time between consecutive selected schedule entries. | Uses known stage distance data or MVP walking-time defaults and is shown in the schedule where known. |

## Future domain events

No domain events are specified in `project.md`.

| Event | Meaning | Notes |
| --- | --- | --- |
| None specified | No business-significant domain events are defined in `project.md`. | Add events only when future requirements need audit, integrations, notifications, projections, or reporting. |

## Reporting or audit needs

MVP3 adds a lightweight reporting need: users can export a CSV snapshot of locally available band and rating data from the settings screen.

The current output format is a day-based in-app calendar schedule. Printable export is outside the active MVP2 scope.

## Edge cases

- A band has any `5` rating.
- A band with maximum rating `4` has 2 or more vetoes.
- A band with maximum rating `3` has any veto.
- Overlapping performances include multiple bands with `5` ratings.
- Overlapping must-see performances have different travel impact from the previous or next selected band.
- Overlapping performances with ratings of `4` tie on count.
- Overlapping performances with ratings of `4` tie on count and veto count.
- A group member has not rated a band.
- Walking time makes a visible overlap impractical.
- All overlapping options are vetoed.
- CSV data contains missing references.
- CSV data contains overlaps.
- CSV data references unknown stages.
- CSV re-import changes existing festival master data after ratings already exist.
- CSV re-import removes a band that has an existing rating.
- CSV file selection is incomplete or a selected file cannot be read.
- Band import exists before any performances are available.
- Band overview data loading or rendering takes longer than expected.
- The user taps repeatedly while the band overview is still loading.
- The user leaves the app and returns while on overview or detail.
- The user opens YouTube or Spotify and then returns to Wacken Planner.
- Imported band metadata contains English and German biography text.
- Imported band metadata has no image, no biography, or missing music links.
- Scraped website data proposes a change that the user wants to reject.
- A user exports ratings while offline.
- A user exports before all group member ratings have synced.
- A band has a planning rating but no real post-show rating.
- A user resets a real post-show rating without changing the planning rating.
- The app starts with no network and no locally cached festival data.
- The app starts with no network after a successful previous sync/import.

## Open questions

- What exact final running-order fields will Wacken publish for stages and performance times, and will they require schema changes beyond the current documented CSV contracts?
- What exact fields can be scraped from the Wacken band list and band detail pages, and what are the legal/technical constraints for scraping those pages?
- How should the proposed data grid be implemented in the Android app, and which validation states should each row support?
- How are users identified inside the one current shared group, and where are their ratings stored or shared?
- What exact token/deep-link format should future self-service friend invites use beyond the current plain-text MVP2 share instructions?
- What exact festival dates should be used for weekday display, and what date/time format should performances use?
- Should a later shared-decision workflow add audit history, owner/admin-only permissions, or richer reset flows for group-wide manual schedule locks?
- Should MVP3 real post-show ratings sync to Supabase for the group, or remain personal/local until a later release?

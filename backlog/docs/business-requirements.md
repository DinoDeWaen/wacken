# Business Requirements

This document is the business source of truth for Wacken Planner 2026. It is filled from `project.md` and clarified by user input. Requirements not stated or clearly clarified are listed under `Open questions` instead of being invented.

## Read Me First

Use this section for most tasks. Read deeper sections only when the active task touches that area.

### Current Product Scope

Wacken Planner 2026 is an Android app for one shared friend group to rate Wacken bands and prepare a conflict-aware festival schedule. The current implementation focuses on band import, band listing, band detail, 1-5 ratings with unrated state, local cache behavior, Supabase-backed lifecycle master-data sync, Supabase Auth, and shared rating sync.

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

### Current Non-Goals

- Multiple independent groups in the current version.
- Play Store distribution before a later delivery phase.
- Business logic in Android UI, Activities, or Fragments.
- Android instrumentation tests unless a future task makes them meaningful.

### Business Rules Index

| Area | Rules | Read when |
| --- | --- | --- |
| Ratings and group decisions | BR-001 to BR-021 | Rating, veto, effective rating, group decision, conflict resolution |
| Travel, lunch, and timeline | BR-022 to BR-032, BR-073 to BR-074 | Scheduling, walking time, lunch, food suggestions, printable timeline |
| Festival data import | BR-033 to BR-046 | CSV import, Supabase master data, Room cache, admin data, band-only imports |
| Overview, detail, and app state | BR-047 to BR-072 | Wacken UI, metadata, music links, loading, sync feedback, settings, calendar schedule, returning to app context |

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
- Business rules for ratings, vetoes, conflicts, travel feasibility, and lunch belong in the domain/application core, not in UI or infrastructure code.

## Business goals

- Goal 1: Enable a group of friends to combine individual band preferences into one shared festival schedule.
- Goal 2: Prevent schedules that are impossible because of overlapping performances or infeasible travel between stages.
- Goal 3: Respect strong preferences, must-see ratings, vetoes, and lunch constraints when producing the schedule.
- Goal 4: Make the final schedule clear enough to use during the festival and suitable for printing.
- Goal 5: Get the band rating feature working first so the group can begin scoring the lineup before final stage times are available.
- Goal 6: Support reliable festival data import so bands, stages, performances, distances, and food options can be planned from validated file-based CSV uploads or proposed website-scraped changes.
- Goal 7: Present the band overview with a Wacken-inspired visual style so early rating feels like a festival lineup experience rather than a technical data table.
- Goal 8: Keep the rating workflow responsive and stateful so users understand when data is loading and can return to the same app context after switching apps or opening music links.
- Goal 9: Keep persistence backend-replaceable by treating MVP TSV storage as a backend-like source and using a local device cache for fast app reads.

## Product context

Product name: Wacken Planner 2026.

Wacken Planner 2026 is an Android application for Wacken Open Air 2026. It helps a group of friends rate bands and automatically compute a shared, conflict-aware festival schedule.

The app considers individual preferences, group veto rules, stage distances, walking-time feasibility, lunch time constraints, and PDF schedule output.

Target users:

- Primary user: festival attendee who rates bands and uses the generated schedule.
- Secondary user: one shared friend group planning a shared festival schedule.
- External system/user: Wacken line-up pages and validated CSV festival data sources for bands, stages, performances, distances, and food options.
- Admin/support user: admin who imports, reviews, validates, and updates festival data.

Business problem:

- Groups need a practical way to turn individual band preferences into one shared plan.
- Manually planning around overlaps, vetoes, travel time, and lunch constraints is error-prone.
- Without schedule feasibility rules, the group may choose performances that cannot realistically be attended together.

Desired outcome:

- Users can rate bands on a shared 1-5 scale, with `0` reserved for unrated bands.
- The app can propose a shared schedule that respects decision rules, conflicts, travel feasibility, and lunch.
- The final timeline is clear, day-based, and exportable as a PDF.

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
| MVP 3 | Add stage distance, travel feasibility, lunch logic, and food suggestions. | Not specified in `project.md` |
| MVP 4 | Improve the user experience with printable schedule output, visual hierarchy, and performance optimization. | Not specified in `project.md` |

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
| Walking-time schedule visibility | Apply the agreed MVP walking-time defaults and show movement time in the group schedule where known. | Must | MVP 3 |
| Travel feasibility | Exclude or re-evaluate options that cannot be reached in time between stages. | Must | MVP 3 |
| Lunch planning | Insert lunch into the schedule and consider food options near relevant stages. | Must | MVP 3 |
| Food suggestions | Show food options close to the previous and next stages. | Must | MVP 3 |
| PDF schedule export | Produce a clear printable timeline as a PDF. | Must | MVP 4 |

## Domain context

The bounded context is `Festival Group Scheduling`.

In scope:

- Rating bands for Wacken Open Air 2026.
- Combining individual ratings into group decisions.
- Supporting one shared group for the current version.
- Resolving overlapping performances.
- Checking travel feasibility between stages.
- Planning lunch during the 12:00-14:00 lunch window.
- Showing nearby food options.
- Producing a day-based printable timeline.
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

The app combines group ratings, conflicts, travel feasibility, and lunch constraints to produce a timeline.

```gherkin
Scenario: Generate a conflict-aware group schedule
  Given a group has rated bands for a festival day
  And performances may overlap or require travel between stages
  When the schedule is generated
  Then the timeline respects group decision rules, conflicts, travel feasibility, and lunch constraints
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
| BR-010 | If the maximum rating is `3` during the 12:00-14:00 lunch window, the single-band decision is `OPTIONAL`. | A liked-but-missable lunch-window performance is optional. | Must |
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
| BR-022 | A performance is invalid if `previousEndTime + travelTime > nextStartTime`. | A group cannot attend the next band if travel makes arrival late. | Must |
| BR-023 | Travel feasibility uses walking minutes by default. | A 15-minute walk must fit between the previous end time and next start time. | Must |
| BR-024 | Walking time may become a user setting later. | A future user can tune travel assumptions to their walking speed. | Could |
| BR-025 | If a performance is infeasible, conflict resolution must be re-run excluding infeasible options. | The schedule chooses only from reachable options. | Must |
| BR-026 | When choosing between equal `4`-rated options, prefer the one closest to the previous `5`-rated performance. | Travel proximity to a previous must-see band breaks the tie. | Must |
| BR-027 | The schedule must start with a lunch block during 12:00-14:00. | A daily timeline visibly includes lunch in the lunch window. | Must |
| BR-028 | Lunch timing is expected to be refined later with user input. | A later version can let users tune lunch placement or duration. | Should |
| BR-029 | Lunch planning must show food options close to the previous stage and close to the next stage when such options exist. | Food suggestions are relevant to the user's route. | Must |
| BR-030 | If no food option is close to the previous or next stage, the app does not need to show a substitute suggestion. | No nearby food means no nearby-food recommendation. | Must |
| BR-031 | Each timeline slot must show selected band, winner rating stars, stage, time range, travel time to next stage, lost alternative with rating stars, and lunch where applicable. | Users can understand the plan, why the winner was selected, and whether the closest rejected option is still worth considering. | Must |
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
| BR-039 | Printable timeline output must be PDF. | The generated festival plan can be exported as a PDF. | Must |
| BR-040 | MVP CSV import must be file-upload based in Android, not paste-text based. | The user selects `bands.csv` and companion CSV files with the Android document picker. | Must |
| BR-041 | The import screen must show which CSV files were selected before import. | After selecting `bands.csv`, the screen shows the chosen file name. | Must |
| BR-042 | A successful CSV import updates festival master data for bands, stages, performances, distances, and food. | Re-importing a newer Wacken band list replaces the stored lineup data. | Must |
| BR-043 | Re-importing festival master data must preserve existing ratings. | If a user rated 5th Avenue as `5`, importing updated CSV files must not erase that rating. | Must |
| BR-044 | Ratings are user/group preference data, not festival master data. | Updating lineup CSVs must not clear or overwrite rating records. | Must |
| BR-045 | Band-only imports must be visible in the band overview before performance times are available. | A Wacken lineup CSV without stages/times still shows bands for rating. | Must |
| BR-046 | Bands without performance data must be clearly marked as not scheduled yet. | A band imported without a performance shows `Not scheduled yet` and `TBA`. | Must |
| BR-047 | The band overview must use a Wacken-inspired visual style with dark presentation and amber/yellow emphasis where practical. | Band cards look festival-themed rather than like raw form controls. | Should |
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
| BR-056 | Authenticated Supabase calls must renew expired access tokens when the refresh token is still valid. | Master-data sync and rating sync continue after normal JWT expiry; if refresh fails, the local session is cleared and the user returns to login. | Must |
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

## Data and terminology

| Concept | Description | Important fields | Invariants |
| --- | --- | --- | --- |
| Band | A musical act at Wacken Open Air 2026. | Name, optional English biography, optional image metadata, optional music links, optional schedule status | Can be associated with one or more performances; can also exist before performances are known. |
| Performance | A scheduled band appearance. | Band, stage, time range | Must reference known band and stage data. |
| Stage | A festival podium or location where performances happen. | Name or identifier | Must be known before performances can reference it. |
| Distance | Travel information between stages. | From stage, to stage, walking minutes by default | Used to determine travel feasibility. |
| User | A festival attendee who can rate bands. | User identity details are not finalized. | Can provide ratings; missing ratings default to `0` unrated. |
| Group of friends | The single shared group for the current version. | Members are not fully specified. | Group decisions use all member ratings; multiple groups are deferred. |
| Rating | A user's preference for a band. | Value from 1 to 5 when explicit; `0` only represents unrated/no explicit rating. | Must use the defined rating scale. |
| Food option | A food location or option used for lunch planning. | Location near stages is implied but not specified in detail | Used for suggestions near previous and next stages when nearby options exist. |
| Schedule | A conflict-aware festival plan. | Day, slots, lunch block, selected performances, alternatives, travel time | Must respect decision rules, conflicts, travel feasibility, and lunch constraints. |
| Timeline slot | One visible item in the daily schedule. | Selected band, stage, time range, travel time, lost alternative | Must be clear enough for timeline display and PDF export. |
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
| Schedule | Represents the selected day-by-day plan. | Contains timeline slots and lunch blocks; can be exported as PDF. |
| Timeline Slot | Represents a selected schedule entry. | Shows performance, travel time, and the lost alternative. |
| Food Option | Represents a lunch option. | Is suggested based on proximity to previous and next stages. |
| Festival Master Data | Represents imported source data for the festival. | Is updated by CSV import; excludes ratings. |
| Band Overview Row | Represents a visible, tappable band summary. | Opens band detail and displays effective rating, schedule status, and optional music-link actions. |
| Settings page | Represents secondary app actions and operational controls. | Contains group/invite, import, and manual sync actions. |
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

No reporting or audit needs are specified.

The current in-app output format is a day-based calendar schedule. A clear, printable day-based PDF timeline remains a later export requirement.

## Edge cases

- A band has any `5` rating.
- A band with maximum rating `4` has 2 or more vetoes.
- A band with maximum rating `3` has any veto.
- A band with maximum rating `3` occurs during the 12:00-14:00 lunch window.
- Overlapping performances include multiple bands with `5` ratings.
- Overlapping must-see performances have different travel impact from the previous or next selected band.
- Overlapping performances with ratings of `4` tie on count.
- Overlapping performances with ratings of `4` tie on count and veto count.
- A group member has not rated a band.
- Travel time makes the next performance infeasible.
- Conflict resolution must be re-run after excluding infeasible performances.
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
- Food options need to be found near both previous and next stages.
- No food option is close to the previous or next stage.

## Open questions

- What exact final running-order fields will Wacken publish for stages and performance times, and will they require schema changes beyond the current documented CSV contracts?
- What exact fields can be scraped from the Wacken band list and band detail pages, and what are the legal/technical constraints for scraping those pages?
- How should the proposed data grid be implemented in the Android app, and which validation states should each row support?
- How are users identified inside the one current shared group, and where are their ratings stored or shared?
- What exact token/deep-link format should future self-service friend invites use beyond the current plain-text MVP2 share instructions?
- How long is the initial lunch block inside 12:00-14:00 before user-configurable lunch behavior is added?
- What exact festival dates should be used for weekday display, and what date/time format should performances use?
- Should a later shared-decision workflow add audit history, owner/admin-only permissions, or richer reset flows for group-wide manual schedule locks?

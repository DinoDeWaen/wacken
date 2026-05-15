# Business Requirements

This document is the business source of truth for Wacken Planner 2026. It is filled from `project.md` and clarified by user input. Requirements not stated or clearly clarified are listed under `Open questions` instead of being invented.

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
- Goal 6: Support reliable festival data import so bands, stages, performances, distances, and food options can be planned from validated CSV files or proposed website-scraped changes.

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

- Users can rate bands on a shared 0-4 scale.
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
| Festival CSV files | Provide bands, stages, performances, distances, and food data. | Inbound | CSVs must be validated for missing references, overlaps, and unknown stages once final stage and time data is available. |
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
| Band rating | Let users rate bands on the 0-4 preference scale. This is the first product priority. | Must | MVP 1 |
| Band listing | Show bands with stage and time information. | Must | MVP 1 |
| Band detail view | Show band details in a style inspired by the official Wacken band detail screen, with rating stars and optional YouTube and Spotify links. | Must | MVP 1 |
| Initial lineup import | Import or scrape the current Wacken band list before final stages and times are available. | Must | MVP 1 |
| Festival data import | Import validated CSV files for bands, stages, performances, distances, and food options once final lineup data is available. | Must | MVP 1 |
| Data review grid | Propose scraped or imported data changes in a user-validated data grid, with line-by-line validation. | Should | MVP 1 |
| One-group planning | Combine ratings from one shared friend group into shared decisions. | Must | MVP 2 |
| Friend invites | Allow friends to join the shared group through the most useful shareable invite format for Android. | Should | MVP 2 |
| Decision engine | Decide whether the group should go, optionally go, or not go to a band. | Must | MVP 2 |
| Conflict resolution | Resolve overlapping performances according to the authoritative group rules. | Must | MVP 2 |
| Timeline generation | Generate a day-based shared festival timeline. | Must | MVP 2 |
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

An admin imports CSV files or asks the app to scrape the Wacken line-up website. The app proposes data changes in a grid so the user can validate updates line by line.

```gherkin
Scenario: Review proposed festival data updates
  Given the app has found lineup changes from CSV input or the Wacken website
  When the admin reviews the proposed changes in a data grid
  Then the admin can validate updates line by line before they affect planning
```

### Workflow 2: Rate bands

A festival attendee rates bands using the shared 0-4 scale.

```gherkin
Scenario: Rate a band
  Given a listed band with performance information
  When a user rates the band from 0 to 4
  Then the rating is available for group decision rules
```

### Workflow 3: Review band details

A festival attendee reviews band information in a detail screen inspired by the official Wacken band detail screen, with rating stars and optional music links.

```gherkin
Scenario: Review and rate a band
  Given a band is available in the lineup
  When a user opens the band detail screen
  Then the user sees band information, rating stars, and optional YouTube and Spotify links when available
```

### Workflow 4: Compute a shared schedule

The app combines group ratings, conflicts, travel feasibility, and lunch constraints to produce a timeline.

```gherkin
Scenario: Generate a conflict-aware group schedule
  Given a group has rated bands for a festival day
  And performances may overlap or require travel between stages
  When the schedule is generated
  Then the timeline respects group decision rules, conflicts, travel feasibility, and lunch constraints
```

### Workflow 5: Use the printable timeline

The group reviews a clear per-day timeline for use during the festival.

```gherkin
Scenario: View printable festival timeline
  Given a shared schedule has been generated
  When a user views the daily timeline
  Then each slot shows the selected band, stage, time range, travel time, lost alternative, and lunch where applicable
```

## Business rules

| Rule ID | Rule | Example | Priority |
| --- | --- | --- | --- |
| BR-001 | Ratings must use the 0-4 scale. | `0` means veto; `4` means must see. | Must |
| BR-002 | A rating of `0` means veto. | A band rated `0` by a user counts as a veto in group decision rules. | Must |
| BR-003 | A rating of `1` means OK or indifferent. | A band with max rating `1` is optional. | Must |
| BR-004 | A rating of `2` means like, fine to miss. | A band with max rating `2` can be selected unless vetoed. | Must |
| BR-005 | A rating of `3` means want to see. | A band with max rating `3` goes unless it has 2 or more vetoes. | Must |
| BR-006 | A rating of `4` means must see. | Any band with at least one `4` is preferred by the decision rules. | Must |
| BR-007 | If any group member rates a band `4`, the single-band decision is `GO`. | One must-see rating is enough to go. | Must |
| BR-008 | If the maximum rating is `3`, the single-band decision is `GO` unless there are 2 or more vetoes. | Two vetoes block a band whose highest rating is `3`. | Must |
| BR-009 | If the maximum rating is `2`, the single-band decision is `GO` unless there is any veto. | One veto blocks a band whose highest rating is `2`. | Must |
| BR-010 | If the maximum rating is `2` during the 12:00-14:00 lunch window, the single-band decision is `OPTIONAL`. | A liked-but-missable lunch-window performance is optional. | Must |
| BR-011 | If the maximum rating is `1`, the single-band decision is `OPTIONAL`. | A group that is only indifferent may optionally attend. | Must |
| BR-012 | If the maximum rating is `0`, the single-band decision is `DO NOT GO`. | A fully vetoed band is not selected. | Must |
| BR-013 | For overlapping performances, prefer any band with a `4`. | A must-see band wins over lower-rated alternatives. | Must |
| BR-014 | If overlapping performances both have at least one `4`, choose the one that reduces travel time by being closest to the previous selected band or best positioned for the next selected band. | Between two must-see conflicts, choose the option that makes the route more feasible. | Must |
| BR-015 | If overlapping options only have ratings of `3`, choose the band with the most `3` ratings. | Two want-to-see ratings beat one want-to-see rating. | Must |
| BR-016 | If overlapping options with ratings of `3` tie, choose the band with fewer vetoes. | One veto beats two vetoes. | Must |
| BR-017 | If overlapping options with ratings of `3` remain tied, choose the shortest travel distance. | The closer stage wins after rating and veto tie-breakers. | Must |
| BR-018 | If overlapping options only have ratings of `2`, the result is `OPTIONAL` and the band with the most `2` ratings is chosen. | More like/fine-to-miss ratings decide optional choices. | Must |
| BR-019 | If overlapping options only have ratings of `1`, the result is `OPTIONAL`. | Indifferent choices do not become mandatory. | Must |
| BR-020 | An unrated band member rating defaults to `1`. | If someone has not rated a band, that missing rating counts as OK / indifferent. | Must |
| BR-021 | If all overlapping options are vetoed, no performance is selected. | All-vetoed conflicts produce no selection. | Must |
| BR-022 | A performance is invalid if `previousEndTime + travelTime > nextStartTime`. | A group cannot attend the next band if travel makes arrival late. | Must |
| BR-023 | Travel feasibility uses walking minutes by default. | A 15-minute walk must fit between the previous end time and next start time. | Must |
| BR-024 | Walking time may become a user setting later. | A future user can tune travel assumptions to their walking speed. | Could |
| BR-025 | If a performance is infeasible, conflict resolution must be re-run excluding infeasible options. | The schedule chooses only from reachable options. | Must |
| BR-026 | When choosing between equal `3`-rated options, prefer the one closest to the previous `4`-rated performance. | Travel proximity to a previous must-see band breaks the tie. | Must |
| BR-027 | The schedule must start with a lunch block during 12:00-14:00. | A daily timeline visibly includes lunch in the lunch window. | Must |
| BR-028 | Lunch timing is expected to be refined later with user input. | A later version can let users tune lunch placement or duration. | Should |
| BR-029 | Lunch planning must show food options close to the previous stage and close to the next stage when such options exist. | Food suggestions are relevant to the user's route. | Must |
| BR-030 | If no food option is close to the previous or next stage, the app does not need to show a substitute suggestion. | No nearby food means no nearby-food recommendation. | Must |
| BR-031 | Each timeline slot must show selected band, stage, time range, travel time to next stage, lost alternative, and lunch where applicable. | Users can understand the plan and the closest rejected option. | Must |
| BR-032 | A lost alternative is the second-highest band: the performance that lost to the selected performance and could still be chosen manually if preferred. | If the winning band is not good enough, the user can inspect the runner-up. | Must |
| BR-033 | CSV import validation must detect missing references, overlaps, and unknown stages. | An imported performance cannot reference an unknown stage without validation feedback. | Must |
| BR-034 | Scraped or imported data changes should be proposed in a data grid for line-by-line user validation. | The user can accept or reject each proposed band or performance update. | Should |
| BR-035 | The initial lineup import may contain only bands before final stage and time data is available. | Early rating can start from a band list without performance times. | Must |
| BR-036 | The band rating screen should follow the official Wacken band detail style where practical, adding rating stars and optional YouTube and Spotify links. | A band detail page shows band information plus star rating controls. | Must |
| BR-037 | The current version supports one shared group, not multiple independent groups. | All ratings belong to "my group" for now. | Must |
| BR-038 | Multi-group support is deferred to next year. | Separate friend groups are not part of the current scope. | Must |
| BR-039 | Printable timeline output must be PDF. | The generated festival plan can be exported as a PDF. | Must |

## Data and terminology

| Concept | Description | Important fields | Invariants |
| --- | --- | --- | --- |
| Band | A musical act at Wacken Open Air 2026. | Name | Can be associated with one or more performances. |
| Performance | A scheduled band appearance. | Band, stage, time range | Must reference known band and stage data. |
| Stage | A festival podium or location where performances happen. | Name or identifier | Must be known before performances can reference it. |
| Distance | Travel information between stages. | From stage, to stage, walking minutes by default | Used to determine travel feasibility. |
| User | A festival attendee who can rate bands. | User identity details are not finalized. | Can provide ratings; missing ratings default to `1`. |
| Group of friends | The single shared group for the current version. | Members are not fully specified. | Group decisions use all member ratings; multiple groups are deferred. |
| Rating | A user's preference for a band. | Value from 0 to 4 | Must use the defined rating scale. |
| Food option | A food location or option used for lunch planning. | Location near stages is implied but not specified in detail | Used for suggestions near previous and next stages when nearby options exist. |
| Schedule | A conflict-aware festival plan. | Day, slots, lunch block, selected performances, alternatives, travel time | Must respect decision rules, conflicts, travel feasibility, and lunch constraints. |
| Timeline slot | One visible item in the daily schedule. | Selected band, stage, time range, travel time, lost alternative | Must be clear enough for timeline display and PDF export. |

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

## Future domain events

No domain events are specified in `project.md`.

| Event | Meaning | Notes |
| --- | --- | --- |
| None specified | No business-significant domain events are defined in `project.md`. | Add events only when future requirements need audit, integrations, notifications, projections, or reporting. |

## Reporting or audit needs

No reporting or audit needs are specified.

The required output format is a clear, printable day-based PDF timeline.

## Edge cases

- A band has any `4` rating.
- A band with maximum rating `3` has 2 or more vetoes.
- A band with maximum rating `2` has any veto.
- A band with maximum rating `2` occurs during the 12:00-14:00 lunch window.
- Overlapping performances include multiple bands with `4` ratings.
- Overlapping must-see performances have different travel impact from the previous or next selected band.
- Overlapping performances with ratings of `3` tie on count.
- Overlapping performances with ratings of `3` tie on count and veto count.
- A group member has not rated a band.
- Travel time makes the next performance infeasible.
- Conflict resolution must be re-run after excluding infeasible performances.
- All overlapping options are vetoed.
- CSV data contains missing references.
- CSV data contains overlaps.
- CSV data references unknown stages.
- Scraped website data proposes a change that the user wants to reject.
- Food options need to be found near both previous and next stages.
- No food option is close to the previous or next stage.

## Open questions

- What exact CSV schemas are required for bands, stages, performances, distances, and food? This needs investigation against the Wacken line-up website and later final running-order data.
- What exact fields can be scraped from the Wacken band list and band detail pages, and what are the legal/technical constraints for scraping those pages?
- How should the proposed data grid be implemented in the Android app, and which validation states should each row support?
- How are users identified inside the one current shared group, and where are their ratings stored or shared?
- What exact shareable format should link-based friend invites use when the invite feature is implemented?
- How long is the initial lunch block inside 12:00-14:00 before user-configurable lunch behavior is added?
- What data source should provide walking minutes between stages before this becomes a user setting?
- Are there multiple festival days, and what date/time format should performances use?

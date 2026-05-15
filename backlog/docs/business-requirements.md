# Business Requirements

This document is the business source of truth for Wacken Planner 2026. It is filled from `project.md`. Requirements not stated or clearly implied by `project.md` are listed under `Open questions` instead of being invented.

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
- Goal 5: Support reliable festival data import so bands, stages, performances, distances, and food options can be planned from validated CSV files.

## Product context

Product name: Wacken Planner 2026.

Wacken Planner 2026 is an Android application for Wacken Open Air 2026. It helps a group of friends rate bands and automatically compute a shared, conflict-aware festival schedule.

The app considers individual preferences, group veto rules, stage distances, travel time feasibility, lunch time constraints, and printable schedule output.

Target users:

- Primary user: festival attendee who rates bands and uses the generated schedule.
- Secondary user: group of friends planning a shared festival schedule.
- External system/user: validated CSV festival data source for bands, stages, performances, distances, and food options.
- Admin/support user: admin who uploads or configures CSV festival data for the MVP.

Business problem:

- Groups need a practical way to turn individual band preferences into one shared plan.
- Manually planning around overlaps, vetoes, travel time, and lunch constraints is error-prone.
- Without schedule feasibility rules, the group may choose performances that cannot realistically be attended together.

Desired outcome:

- Users can rate bands on a shared 0-4 scale.
- The app can propose a shared schedule that respects decision rules, conflicts, travel feasibility, and lunch.
- The final timeline is clear, day-based, and printable.

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
| Festival CSV files | Provide bands, stages, performances, distances, and food data. | Inbound | CSVs must be validated for missing references, overlaps, and unknown stages. |
| CI artifact storage | Stores downloadable APK artifacts. | Outbound | CI must produce a clearly versioned debug APK artifact. |

## Non-goals

- Non-goal 1: Do not make a web app the primary delivery target.
- Non-goal 2: Do not put business logic in Android UI, Activities, or Fragments.
- Non-goal 3: Do not build Play Store distribution before the later optional delivery phase.
- Non-goal 4: Do not implement UI tests where they are not meaningful; features start with domain tests and application tests.

## Delivery roadmap

The delivery roadmap captures the planned or completed increments for the current application:

| Increment | Outcome | Status |
| --- | --- | --- |
| MVP 1 | Establish the Android foundation, CI, domain model, CSV import, band listing, band ratings, and unit test setup. | Not specified in `project.md` |
| MVP 2 | Enable group planning with friend invites, a decision engine, conflict resolution, and timeline generation. | Not specified in `project.md` |
| MVP 3 | Add stage distance, travel feasibility, lunch logic, and food suggestions. | Not specified in `project.md` |
| MVP 4 | Improve the user experience with printable schedule output, visual hierarchy, and performance optimization. | Not specified in `project.md` |

Future production direction, not implemented:

- Free distribution through Play Store or an internal testing track.
- Web app, only if it significantly simplifies early delivery.
- Android instrumentation tests as a later-phase option for the QA suite.

Release assumptions:

- MVP delivery produces a debug APK for local installation.
- CI artifacts are the MVP delivery target.
- CI must fail when required automated tests fail.

## Business capabilities

| Capability | Description | Priority | MVP |
| --- | --- | --- | --- |
| Festival data import | Import validated CSV files for bands, stages, performances, distances, and food options. | Must | MVP 1 |
| Band listing | Show bands with stage and time information. | Must | MVP 1 |
| Band rating | Let users rate bands on the 0-4 preference scale. | Must | MVP 1 |
| Group planning | Combine ratings from a group of friends into shared decisions. | Must | MVP 2 |
| Friend invites | Allow friends to join a planning group through link-based invites. | Must | MVP 2 |
| Decision engine | Decide whether the group should go, optionally go, or not go to a band. | Must | MVP 2 |
| Conflict resolution | Resolve overlapping performances according to the authoritative group rules. | Must | MVP 2 |
| Timeline generation | Generate a day-based shared festival timeline. | Must | MVP 2 |
| Travel feasibility | Exclude or re-evaluate options that cannot be reached in time between stages. | Must | MVP 3 |
| Lunch planning | Insert lunch into the schedule and consider food options near relevant stages. | Must | MVP 3 |
| Food suggestions | Show food options close to the previous and next stages. | Must | MVP 3 |
| Printable schedule | Produce a clear printable timeline. | Must | MVP 4 |

## Domain context

The bounded context is `Festival Group Scheduling`.

In scope:

- Rating bands for Wacken Open Air 2026.
- Combining individual ratings into group decisions.
- Resolving overlapping performances.
- Checking travel feasibility between stages.
- Planning lunch during the 12:00-14:00 lunch window.
- Showing nearby food options.
- Producing a day-based printable timeline.
- Importing and validating festival data from CSV files.

Out of scope:

- Play Store distribution for the MVP.
- Primary web application delivery.
- Business logic in Android UI components.
- Production persistence, API, messaging, authentication, or payment behavior; none is specified in `project.md`.

## Key workflows

### Workflow 1: Import festival data

An admin uploads or configures CSV files so the app has the required festival data for planning.

```gherkin
Scenario: Import valid festival data
  Given validated CSV files for bands, stages, performances, distances, and food
  When the admin imports the festival data
  Then the app can use the data for band listing, rating, and schedule planning
```

### Workflow 2: Rate bands

A festival attendee rates bands using the shared 0-4 scale.

```gherkin
Scenario: Rate a band
  Given a listed band with performance information
  When a user rates the band from 0 to 4
  Then the rating is available for group decision rules
```

### Workflow 3: Compute a shared schedule

The app combines group ratings, conflicts, travel feasibility, and lunch constraints to produce a timeline.

```gherkin
Scenario: Generate a conflict-aware group schedule
  Given a group has rated bands for a festival day
  And performances may overlap or require travel between stages
  When the schedule is generated
  Then the timeline respects group decision rules, conflicts, travel feasibility, and lunch constraints
```

### Workflow 4: Use the printable timeline

The group reviews a clear per-day timeline for use during the festival.

```gherkin
Scenario: View printable festival timeline
  Given a shared schedule has been generated
  When a user views the daily timeline
  Then each slot shows the selected band, stage, time range, travel time, lost alternatives, and lunch where applicable
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
| BR-014 | If overlapping options only have ratings of `3`, choose the band with the most `3` ratings. | Two want-to-see ratings beat one want-to-see rating. | Must |
| BR-015 | If overlapping options with ratings of `3` tie, choose the band with fewer vetoes. | One veto beats two vetoes. | Must |
| BR-016 | If overlapping options with ratings of `3` remain tied, choose the shortest travel distance. | The closer stage wins after rating and veto tie-breakers. | Must |
| BR-017 | If overlapping options only have ratings of `2`, the result is `OPTIONAL` and the band with the most `2` ratings is chosen. | More like/fine-to-miss ratings decide optional choices. | Must |
| BR-018 | If overlapping options only have ratings of `1`, the result is `OPTIONAL`. | Indifferent choices do not become mandatory. | Must |
| BR-019 | If all overlapping options are vetoed, no performance is selected. | All-vetoed conflicts produce no selection. | Must |
| BR-020 | A performance is invalid if `previousEndTime + travelTime > nextStartTime`. | A group cannot attend the next band if travel makes arrival late. | Must |
| BR-021 | If a performance is infeasible, conflict resolution must be re-run excluding infeasible options. | The schedule chooses only from reachable options. | Must |
| BR-022 | When choosing between equal `3`-rated options, prefer the one closest to the previous `4`-rated performance. | Travel proximity to a previous must-see band breaks the tie. | Must |
| BR-023 | The schedule must insert a lunch block during 12:00-14:00. | A daily timeline visibly includes lunch. | Must |
| BR-024 | Lunch planning must show food options close to the previous stage and close to the next stage. | Food suggestions are relevant to the user's route. | Must |
| BR-025 | Each timeline slot must show selected band, stage, time range, travel time to next stage, lost alternatives, and lunch where applicable. | Users can understand the plan and trade-offs. | Must |
| BR-026 | CSV import validation must detect missing references, overlaps, and unknown stages. | An imported performance cannot reference an unknown stage without validation feedback. | Must |

## Data and terminology

| Concept | Description | Important fields | Invariants |
| --- | --- | --- | --- |
| Band | A musical act at Wacken Open Air 2026. | Name | Can be associated with one or more performances. |
| Performance | A scheduled band appearance. | Band, stage, time range | Must reference known band and stage data. |
| Stage | A festival podium or location where performances happen. | Name or identifier | Must be known before performances can reference it. |
| Distance | Travel information between stages. | From stage, to stage, travel time or distance | Used to determine travel feasibility. |
| User | A festival attendee who can rate bands. | User identity is not specified in `project.md` | Can provide ratings. |
| Group of friends | A set of users planning together. | Members are not specified in `project.md` | Group decisions use all member ratings. |
| Rating | A user's preference for a band. | Value from 0 to 4 | Must use the defined rating scale. |
| Food option | A food location or option used for lunch planning. | Location near stages is implied but not specified in detail | Used for suggestions near previous and next stages. |
| Schedule | A conflict-aware festival plan. | Day, slots, lunch block, selected performances, alternatives, travel time | Must respect decision rules, conflicts, travel feasibility, and lunch constraints. |
| Timeline slot | One visible item in the daily schedule. | Selected band, stage, time range, travel time, lost alternatives | Must be clear enough for timeline display and printing. |

## Business object model

| Object | Responsibility | Relationships |
| --- | --- | --- |
| Festival | Provides the overall Wacken Open Air 2026 planning context. | Contains bands, stages, performances, distances, and food options. |
| Band | Represents an act that users can rate and potentially attend. | Has performances; receives ratings from users. |
| Performance | Represents when and where a band plays. | Belongs to a band and a stage; may overlap other performances. |
| Stage | Represents a festival location or podium. | Has performances; has distances to other stages. |
| User | Represents an attendee participating in planning. | Provides ratings; may belong to a group. |
| Group | Represents friends making shared decisions. | Contains users and their ratings; produces group decisions. |
| Rating | Represents a user's preference or veto for a band. | Belongs to a user and band; feeds decision rules. |
| Schedule | Represents the selected day-by-day plan. | Contains timeline slots and lunch blocks. |
| Timeline Slot | Represents a selected schedule entry. | Shows performance, travel time, and lost alternatives. |
| Food Option | Represents a lunch option. | Is suggested based on proximity to previous and next stages. |

## Future domain events

No domain events are specified in `project.md`.

| Event | Meaning | Notes |
| --- | --- | --- |
| None specified | No business-significant domain events are defined in `project.md`. | Add events only when future requirements need audit, integrations, notifications, projections, or reporting. |

## Reporting or audit needs

No reporting or audit needs are specified in `project.md`.

The only output requirement currently specified is a clear, printable day-based timeline.

## Edge cases

- A band has any `4` rating.
- A band with maximum rating `3` has 2 or more vetoes.
- A band with maximum rating `2` has any veto.
- A band with maximum rating `2` occurs during the 12:00-14:00 lunch window.
- Overlapping performances include multiple bands with `4` ratings.
- Overlapping performances with ratings of `3` tie on count.
- Overlapping performances with ratings of `3` tie on count and veto count.
- Travel time makes the next performance infeasible.
- Conflict resolution must be re-run after excluding infeasible performances.
- All overlapping options are vetoed.
- CSV data contains missing references.
- CSV data contains overlaps.
- CSV data references unknown stages.
- Food options need to be found near both previous and next stages.

## Open questions

- If overlapping performances both have at least one `4`, what tie-breakers should decide between them?
- What exact CSV schemas are required for bands, stages, performances, distances, and food?
- How should validation feedback be presented when CSV import detects missing references, overlaps, or unknown stages?
- How are users identified, and how does a group store or share member ratings?
- What is the expected behavior when a group member has not rated a band?
- What exact format should link-based friend invites use?
- How long is the lunch block, and should users be allowed to choose a specific lunch time inside 12:00-14:00?
- What unit and data source should travel feasibility use: walking minutes, distance, or both?
- What should happen if no food option is close to the previous or next stage?
- What printable timeline format is required: Android print flow, PDF, image, or another format?
- What does "lost alternatives" include when several performances are rejected for different reasons?
- Are there multiple festival days, and what date/time format should performances use?

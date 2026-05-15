# Walking-Time And Lunch Defaults

Date: 2026-05-15

Related task: task-20

## Walking-Time Defaults

Travel feasibility uses walking minutes by default.

Source of walking-minute data:

- MVP source: `distances.csv` imported by the admin.
- Column: `walking_minutes`.
- Interpretation: expected walking duration from `from_stage_id` to `to_stage_id`.
- Unit: whole minutes.
- Validation: integer `0` or greater.

The imported walking minutes are the source of truth for MVP scheduling. The app should not calculate walking time from coordinates unless a future task adds map/distance logic.

Default behavior:

- If `from_stage_id` equals `to_stage_id`, walking time is `0`.
- If a distance between two different stages is missing, travel feasibility for that transition is unknown and should be treated as blocked or requiring admin/user review.
- If a distance row is marked bidirectional in CSV, the importer may create both directions. If not expanded, the schedule engine must only use the direction that exists.

Travel feasibility rule:

```text
previous.end_at + walking_minutes <= next.start_at
```

If this is false, the next performance is infeasible after the previous performance and must be excluded or trigger conflict re-evaluation.

Later scope:

- User-configurable walking speed.
- Accessibility/personal pace profiles.
- Coordinate-based distance calculation.
- Crowd-buffer or safety-margin settings.

## Lunch Defaults

Lunch must happen inside the 12:00-14:00 local Wacken window.

Initial default:

- Duration: 30 minutes.
- Placement: earliest feasible 30-minute gap inside 12:00-14:00 after applying must-see and travel-feasible performances.
- If no gap exists, insert lunch at 12:00-12:30 as a visible conflict requiring user review.

Rationale:

- 30 minutes is the smallest practical default that gives the scheduler a concrete block without over-constraining the first version.
- Earliest feasible placement keeps the behavior deterministic.
- User-configurable lunch duration and preferred lunch time are later scope.

Lunch timeline behavior:

- Daily timelines should show lunch as a timeline slot.
- Lunch must use local Wacken time.
- Lunch should not silently remove a must-see performance. If lunch conflicts with must-see choices, the schedule should show the conflict or require user review.

## Food Suggestions

Food suggestions use imported food options and nearby-stage data.

Default behavior:

- Suggest food options close to the previous stage and close to the next stage when such options exist.
- Use `food.near_stage_id` and `food.walking_minutes_from_stage` when available.
- If no food option is close to the previous or next stage, show no substitute suggestion.

No-substitute rule:

```text
No nearby food means no nearby-food recommendation.
```

The app should not invent generic food suggestions or send the user to unrelated stages.

## Follow-Up Scheduling Tasks

Implementation tasks needed later:

- Add travel feasibility service using `StageDistanceRepository`.
- Add lunch insertion service using the default 30-minute lunch block.
- Add food suggestion use case based on previous/next stages.
- Add schedule re-evaluation when a selected performance becomes infeasible due to travel or lunch.
- Add user settings for walking speed, lunch duration, and preferred lunch time after MVP defaults are proven.

Architecture notes:

- Travel/lunch scheduling rules belong in domain/application logic, not Android UI.
- User-configurable walking/lunch settings may require an ADR if they introduce durable settings storage or sync.

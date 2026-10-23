# Design validation and handover
2026-10-17 · TASK-201

23 October 2026 handover: native TASK-204–212 are complete. See the
[implemented 3.0 contract](../../backlog/docs/release-3.0-design-contract.md),
[actual native evidence](../../backlog/docs/release-3.0-native-validation-2026-10-23.md)
and [release notes](../../releases/v3.0.md). The original qualitative review below
is preserved; its pending native statements are historical. Physical/outdoor and
human spoken-feedback limits remain explicit in the native record.

## Actual reviewer feedback
The user requested a combined Ironline dark / Field Notes light appearance choice, reviewed the paired HTML flow and toggle, and replied **“validated”**, then requested implementation planning in release 3.0.

This is one qualitative approval of the paired proposal. Per-task completion times, error counts and separate scores for all five original styles were not supplied and are not inferred. The reviewer exercised the HTML independently; this is not an observed native usability session.

## Accessibility assessment of every direction
All share semantic labeled controls, a 1–5 radio group, textual status/error/recovery, visible keyboard-focus styles, 48 CSS px target intent and color-independent schedule labels. Browser CSS px are not native dp.

| Style | Measured minimum text / boundary contrast | Reading / scaling assessment | Remaining validation |
|---|---|---|---|
| Ironline | 7.31 / 4.28 | Proportional body, 16px base, flat rows, 200% option, wrapped names. | Narrow/200% browser geometry and native focus/TalkBack not fully tested. |
| Field Notes | 5.62 / 3.70 | Original editorial serif body/long rows; paired light mode uses shared Arial body and serif headings. | Serif-heading readability and actual daylight benefit need device review. |
| Pulse | 7.55 / 4.08 | System type, card grouping and space trade density for recognition; text wraps. | Increased scrolling and large-font card layouts need measurement. |
| Rivet | 6.61 / 4.03 | Proportional body, monospace headings/metadata, dense register. | Small metadata/scanning under movement needs device review. |
| Backstage | 7.48 / 4.39 | Large serif headings and featured card; shelf reflows to one column. | Most scrolling; long names and large text need full layout review. |

Passing token contrast does not certify composited disabled states, focus adjacency or rendered text. Actual 320/390px geometry, screen-reader task completion, keyboard focus recovery, sunlight/glare and physical target measurements remain unresolved. The HTML prototype is the review artifact, not an Android conformance claim.

Automated checks run: four appearance preference cases pass after initial red; JavaScript syntax passes; all 80 palette pairs pass; diff whitespace checks pass. Full browser suite was prepared but did not complete because browser launch was blocked/interrupted. No screenshot automation success is claimed.

Agent in-app browser checks: opening/rating Abbie Falls; dark→light retains planning 4; Settings System follows device dark; unfinished music search survives theme switch; saved Light preference returns on re-entry. Initial comparison loaded all five frame accessibility trees. These are smoke checks, not independent user outcome metrics.

## Release 3.0 implementation handover
Milestone m-2, release 3.0:
- TASK-204: accepted design contract and planned requirements.
- TASK-205: System/Dark/Light preference and state retention.
- TASK-206: shared labeled navigation and return behavior.
- TASK-207: lineup presentation.
- TASK-208: accessible detail and independent ratings.
- TASK-209: schedule, legend and alternatives.
- TASK-210: Settings/Admin and verified music review.
- TASK-211: festival lifecycle, import, auth and support states.
- TASK-212: native accessibility, device and regression validation.
- TASK-213: signed 3.0 release and upgrade validation.

The design spike is accepted for planning. TASK-212 owns native verification before release; unresolved research observations are not represented as resolved implementation defects. TASK-202 remains in 2.34 for actual verified source discovery.

README impact: updated design review/handover information.
Business requirements impact: none, because native implementation belongs to 3.0; TASK-204 will document approved planned requirements distinctly from shipped behavior.
Diagram impact: none, because this handover changes no architecture.
ADR impact: none, because user design approval is not an architecture decision.


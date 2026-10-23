# UX audit and research — 2.34
TASK-201 · Historical audit, 17 October 2026. The paired proposal was subsequently
approved and implemented in 3.0. [The current design contract](../../backlog/docs/release-3.0-design-contract.md)
and [native evidence](../../backlog/docs/release-3.0-native-validation-2026-10-23.md)
supersede conflicting guidance here. Findings below refer to their stated old
source baseline, not current defects.

## Evidence and scope
Audit baseline: published **v2.33.5**, commit **1928e82f64fe9724a69e7f33cc72c8fb2b9cb2fa**. Source was inspected in the existing fix-festival-creation worktree because the original checkout contains an older app. Source paths below are relative to that baseline, under app/src/main/java/be/wacken/planner/ unless specified otherwise. Verify a finding against that tag, not against an older branch.

Evidence labels: **Observed** = source-backed property or supplied screenshot; **Risk** = plausible usability consequence requiring device/user validation; **Proposal** = research-informed design; **Assumption** = unmeasured hypothesis. Severity describes potential user impact, not measured frequency. This is a source/heuristic audit, not a completed native usability session.

Prior work: TASK-125 UX review and TASK-130 visual design system already improved grouping, detail information, schedule legend and sync feedback. Retain those improvements. Current design-system guidance remains authoritative until a redesign is approved.

## Current journeys and findings
| ID / priority | Reproduce / source evidence at v2.33.5 | Observation and affected user | Proposal / benefit | Effort, risk and validation |
|---|---|---|---|---|
| A1 / High | Band overview music shortcuts; MainActivity.java:706–717 | Observed: icons have 26dp layout size. Risk: users miss small targets while moving or with motor limitations. | Use at least 48dp hit areas; keep artwork smaller if desired. Easier intentional selection. | Small; row density trade-off. Verify bounds and actual Android taps. |
| A2 / High | Schedule day/threshold/legend controls; ScheduleActivity.java:212–216,275–279,335–336 | Observed: 34–36dp control heights and 11–12sp labels. Risk: inaccurate selection and difficult reading. | Increase targets and allow text growth without hiding selected filters. | Small–medium; less calendar area. Native font-scale and selection checks. |
| A3 / High | Planning rating on band detail; RatingStarsView.java, touch listener and custom star children | Observed: custom group maps touch x-position to rating; individual star values have no evident radio semantics. Risk: assistive input may not expose each value or meaning. Native TalkBack failure is not established. | Expose 1–5 and clear with explicit meaning; separate planning from real rating. | Medium; preserve zero/unrated and independent real ratings. Test TalkBack, keyboard and reset. |
| A4 / High | Primary button theme; WackenTheme.java:72–80 and RED/WHITE constants | Observed: white on #FF3B6B calculates 3.45:1. Below 4.5 for normal-size text; large text threshold is 3. Not every use is therefore a failure. | Change foreground/background tokens for applicable small text. | Small; inspect actual font sizes and all button states. Palette calculation alone is insufficient. |
| A5 / Medium | Overview header MainActivity.java:319–362 | Observed: festival lifecycle actions share prominent space with daily actions. Risk: rare destructive/admin actions compete with rating/navigation. | Group management in Settings, retain deliberate confirmation and clear active-festival context. | Medium; discoverability and BR-061 navigation implications. Review with current users before changing. |
| A6 / Medium | Band detail sections BandDetailActivity.java:130–165 | Observed: planning, real rating, history, running order and links share a long page. Risk: first action competes with secondary information. | Prioritize planning; disclose After the show separately; keep group ratings read-only on detail. | Medium; concealment may reduce real-rating discovery. Test both rating tasks. |
| A7 / Medium | SettingsActivity.java:80–150; BandMetadataReviewActivity.java:45 onward | Observed: grouped Admin and reviewed enrichment framework already exist. Proposed band-specific and festival music-only discovery entries are TASK-202 scope. | Place festival discovery beside Link bands and single-band discovery beside links; show identity evidence before approval. | TASK-202; namesake risk high. Preserve existing values and empty unknown links; test reject, no match, offline and approval. |
| A8 / Medium | MainActivity.java:509–514; supplied festival failure screenshots | Observed: persistent sync summary exists. Screenshots show raw null / database constraint messages from earlier festival failures. Those failure causes were fixed in 2.33.4/2.33.5; not reproduced as current defects. Risk: technical errors do not explain recovery. | Retain persistent status and distinguish device-saved, group-saved, pending and retry. Errors should name the action and recovery without claiming success. | Medium; align wording to actual atomic-save guarantees. Test failed writes, retry and cached-data preservation. |
| A9 / Review | ScheduleActivity.java:180,286–308 and 613–631 | Observed: legend, locks, alternatives and filters already exist; time/alternative text reaches 10–11sp. | Keep legend and decision explanation; add readable labels and validate dense time-grid scaling. | Medium; a two-dimensional calendar may scroll horizontally; do not shrink all text to fit. Test choosing and retaining a lock. |
| A10 / Review | Archive/Add festival entry, active-festival lifecycle in MainActivity | Existing workflow supports no-active festival and archived history. Source review cannot prove discoverability or recovery on a phone. | Explicit no-active-festival entry with history, Add festival and confirmation; preview pending/error states. | Small–medium; preserve archive read-only rules and atomic creation. Native import regression tests remain required. |

Strengths to preserve: cache-first launch/background refresh (MainActivity.java:145–156), grouped Settings, separate real ratings, read-only group detail, left-aligned biography (BandDetailActivity.java:330), existing schedule legend, exact-name band reuse, atomic festival creation and absent unknown music links.

## State coverage in the HTML
All five styles share the same fixtures and logic. Study controls expose Online, Offline/pending, Sync error/retry, Refreshing cached data and No active festival. Empty search results, absent music metadata, ambiguous lookup, no verified match, evidence approval, archive confirmation and simulated festival submission can be inspected. Schedule and candidate data are illustrative. These mocks do not demonstrate real sync durability, CSV parsing, provider identity verification or backend transactions.

## Primary guidance
All sources accessed **2026-10-17**. Guidance is applied proportionately; WCAG is a web standard and is used here as a measurable prototype reference, not a claim of Android certification.

| Source | Guidance used / application |
|---|---|
| [Android accessibility](https://developer.android.com/guide/topics/ui/accessibility/apps) and [Views guidance](https://developer.android.com/guide/topics/ui/accessibility/views/apps-views) | 48×48dp Android targets, useful labels, sufficient contrast. Prototype aims at 48 CSS px; dp and CSS px are distinct units. |
| [WCAG text contrast](https://www.w3.org/WAI/WCAG22/Understanding/contrast-minimum.html) | Normal text at least 4.5:1; large text 3:1. Palette checks use the stricter 4.5 for all enabled text tokens. |
| [WCAG non-text contrast](https://www.w3.org/WAI/WCAG22/Understanding/non-text-contrast.html) | Relevant control boundaries and visual indicators need contrast; token check uses 3:1. |
| [Resize text](https://www.w3.org/WAI/WCAG22/Understanding/resize-text.html) and [reflow](https://www.w3.org/WAI/WCAG22/Understanding/reflow.html) | Examine 200% text and 320 CSS px layouts; preserve reading order. A two-dimensional schedule has a limited reflow exception. |
| [Focus not obscured](https://www.w3.org/WAI/WCAG22/Understanding/focus-not-obscured-minimum.html) | Check visible keyboard focus and that scrolling/navigation do not completely hide it. |
| [Status messages](https://www.w3.org/WAI/WCAG22/Understanding/status-messages.html) | Announce save/pending/error feedback without unnecessary focus movement. |
| [Target size minimum](https://www.w3.org/WAI/WCAG22/Understanding/target-size-minimum.html) | Web AA minimum is 24 CSS px with exceptions. This study deliberately aims above it at 48 CSS px; it does not mislabel 48 as the WCAG AA requirement. |
| [Material Android bottom navigation](https://github.com/material-components/material-components-android/blob/master/docs/components/BottomNavigation.md) | Three top destinations can use icons with labels. The proposal needs review against the app's current compact-header rule. |
| [Nielsen Norman Group usability heuristics](https://www.nngroup.com/articles/ten-usability-heuristics/) | Visibility of status, clear recovery, recognition and user control inform wording and hierarchy; they do not prove that users prefer any style. |

## Proposals requiring review
Field Notes explores a light theme despite the current dark-metal requirement BR-047. All variants explore labeled bottom navigation despite the compact header actions of BR-061. These are research alternatives, not approved business-rule changes. No production requirement was rewritten to make a mock compliant.

Preserve alphabetical band ordering (BR-050), group detail placement (BR-060), independent planning/real ratings (BR-078), atomic festival creation (BR-087) and empty unknown metadata. Reuse the existing reviewed metadata boundary for TASK-202 after the normal implementation gate.

## Proposed small follow-up stories
These are proposals for review, not additional active tasks.

1. **Accessible rating input** — Given an unrated band, when selecting 4 through touch, keyboard or TalkBack, then announce Strong choice and persist 4; clearing returns 0; the real rating stays independent. Cover target geometry and native accessibility actions.
2. **Readable schedule controls** — Given 200% native font size, when filtering a day and threshold, then labels and selected states remain readable, targets reach 48dp and legend/decision controls remain reachable.
3. **Clear save and recovery status** — Given a failed group write, when opening the affected action, then distinguish local pending state from completed shared state, show a usable retry and retain cached data. Validate failure and retry against real adapters.
4. **Reviewed music discovery** — Implement TASK-202 from the two proposed entry points, with verified identity evidence, no guessed links, explicit approval and rejection that preserves existing metadata.
5. **Navigation and hierarchy pilot** — After selecting a style and approving any BR-061 change, implement one overview/detail slice. Validate find/rate/return and post-show-rating discovery with current users before widening the redesign.

Prioritize rating semantics, targets and contrast before decorative changes. Next validate state wording and metadata review; select navigation and styling after user feedback. Effort estimates are relative and require implementation inspection.


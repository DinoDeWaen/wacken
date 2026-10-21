# Release 3.0 paired appearance and flow contract

TASK-204 · Approved design baseline: 17 October 2026 · Native delivery planned
in TASK-205–213. This contract specifies intended 3.0 behavior. The released
2.35.1 app does **not** yet implement this appearance/navigation redesign.

## Authority and evidence

The user reviewed the [paired flow](../../design/release-2.34/themes.html),
requested Ironline dark plus Field Notes light, and replied “validated”. The
[validation record](../../design/release-2.34/validation-review.md) records one
qualitative approval, not measured completion times or a native accessibility
pass. The paired overrides in [styles.css](../../design/release-2.34/styles.css)
take precedence over the five original concepts. Prototype ratings, bands,
search results and provider evidence are examples, never production data.

This contract supersedes conflicting dark-only, icon-only, smaller-target and
essential-text truncation guidance in the earlier visual design system for 3.0.
Current runtime behavior remains documented in README until native tasks ship.
Keep Android Views and current domain/application/infrastructure boundaries.
New architecture-significant changes still require explicit approval and an ADR.

## Appearance and shared tokens

| Role | Ironline dark | Field Notes light |
| --- | --- | --- |
| Background | `#101418` | `#F4EFE3` |
| Panel | `#1A2228` | `#FFFDF5` |
| Raised/selected surface | `#24303A` | `#E7DFD0` |
| Main text | `#F3F4EC` | `#20241F` |
| Secondary text | `#B4C1C8` | `#51574E` |
| Accent/primary fill | `#FFD25A` | `#3B553C` |
| Text on accent | `#191710` | `#FFFFFF` |
| Destructive/error text | `#FFABB2` | `#9C2733` |
| Control boundary/divider | `#81949F` | `#6C7368` |

Both appearances share hierarchy, section order, row geometry, navigation,
control semantics and proportional body type. Map Arial body to the Android
platform sans-serif; use platform serif only for light screen/section headings.
Do not apply the original Field Notes serif body, 100px rows or different layout.
Use a 4dp spacing rhythm, 16dp normal screen/section padding, 6dp corners and
8dp control separation. Narrow layouts may reduce outer padding to 14dp as in
the paired prototype; reflow controls before compressing their touch targets.

Use sp for text with Android font scaling: roughly 28–32sp screen titles,
18–20sp section headings, 16sp body/row titles and 14sp secondary labels. The
hierarchy is shared across appearances; essential text must wrap at 200% rather
than be hidden by a fixed height. Long prose is left-aligned. Dense calendar
geometry may scroll in two dimensions, but full detail remains accessible.

Every interactive target is at least 48dp in both dimensions, with visible
keyboard focus and a meaningful accessibility label/selected state. The five
rating choices may wrap or stack at large fonts instead of shrinking. Preserve
essential names, stage/date/time, state and recovery actions. Enabled normal text
must reach 4.5:1 contrast, large text and meaningful control boundaries 3:1;
verify actual Android rendering, focus and compositing in TASK-212. The prototype
80-pair token check is evidence about CSS tokens only, not native conformance.
Disabled controls still explain their state; never rely on color alone.

## Preference and state retention (TASK-205)

Default to **System** when no valid device-local preference exists. System
follows device light/dark changes; explicit **Dark**/**Light** overrides them
and survives app restart. Settings exposes all three choices. The header action
is labeled with its next action, “Switch to light” or “Switch to dark”, and
selects the opposite **explicit** appearance. Return to System through Settings.
All open screens, dialogs and system-bar styling resolve the same preference.
No backend/account preference synchronization is introduced.

Changing appearance is a presentation action: preserve destination, active or
archived festival/band, filters, row/calendar scroll, disclosure state, entered
search/form text, reviewed selections and pending work. No rating, history,
metadata approval, import, archive or sync write may be triggered by restyling
or Activity recreation. Persist durable operations through existing stores;
restore transient UI state or clearly offer safe recovery for an interrupted
operation. Never replay a write merely because its screen is rebuilt.

## Navigation (TASK-206)

Main destinations always appear in order **Bands · Schedule · Settings**, with
visible names and selected/current state exposed to accessibility services.
Icons supplement words. Avoid accumulating duplicate destinations on repeated
selection. Detail/review Back returns to the initiating band row or Settings
context, retaining query/filter/scroll. Returning from music apps, file picking
or sharing keeps the same context. Keep manual sync, sync/close and festival
management reachable; do not make a theme action replace those actions.
When no active festival exists, Bands shows the archive/add-next entry state;
Schedule explains that an active festival is needed and offers a useful return.
Archived detail stays an archived subflow, not a silently editable active band.

## Ratings and lifecycle invariants

[ADR0016](../decisions/0016-band-catalog-and-archive-promoted-personal-ratings.md)
and the implemented 2.34 lifecycle govern both appearances:

| Context | Required presentation and allowed action |
| --- | --- |
| Established band opinion | Label the current member-specific band rating and source/history context; it is not the latest arbitrary history event or the selected festival planning value. Normal detail editing does not directly change it. |
| Selected-festival planning | Primary editable section, labeled for that festival; 0 = Unrated, 1 = Veto, 2–3 = existing optional/weak meanings, 4 = Strong choice, 5 = Must see. Read-only per-member planning details stay in detail/decision views, not the overview. |
| After the show | Separate disclosure and own selected-festival after-rating/reset. Changing it leaves planning/group schedule and the established opinion unchanged until authorized archive promotion. Retain history semantics and recovery-review flags. |
| Creation | Explicit festival name and reviewed canonical identities; atomic one-time copy from each current member’s own established rating to their own planning snapshot. Missing opinions stay unrated and after-ratings start empty. Replays never reset later edits. |
| Archive | Sync pending work first, then atomically archive and promote nonempty after-ratings. Missing/reset values retain established opinions. Only acknowledged full success enters the archive state; pending/conflict/failure retains cached work and recovery guidance. |
| Archived correction | Archive detail is read-only by default. Explicit own correction uses reviewed revision and confirmation, preserves original and correction dates/audit, and updates established opinion only when source provenance still permits it. Older corrections never replace newer sources or planning snapshots. |

One canonical band can have different established/planning/after values across
members/festivals. Show the selected scope explicitly. Never fall back to another
festival, another member or a latest-event heuristic to fill an unknown value.
Reset is distinct from a missing response; a local save is distinct from shared
acknowledgement. Theme and navigation work cannot change these policies.

## Screen and dialog coverage inventory

Every row receives both appearances, keyboard/focus/48dp/200% checks where
interactive, and loading/empty/error/offline/pending states wherever meaningful.
“Not applicable” needs a concrete reason in TASK-212 evidence, not silent omission.
TASK-205 supplies preference and shared tokens; TASK-206 supplies navigation.
Implementation tasks below own screen-specific retention and semantics;
TASK-212 validates the integrated matrix before TASK-213 publication.

| Native surface / handoff | State and retention coverage | Owner |
| --- | --- | --- |
| `MainActivity` active lineup | Alphabetic actual data, long names, rating meaning, real stage/date/time, search/filter/no results, cache while loading, empty festival, sync failure, offline/pending; open/return same row; known music handoff only. | TASK-207 |
| `BandDetailActivity` | Planning-first, established context, After the show disclosure, independent ratings/reset, group read-only details, biography/image loading/failure, unknown links, discovery entry; band/scroll/disclosures/pending retention. | TASK-208 |
| `ScheduleActivity` calendar | Actual stage rows/time/day cutoff, day/barred/threshold filters, viewport retention, loading/empty/offline/error, accessible color-independent legend, walking context, locks/ties/scratched overlap; keep necessary 2D scrolling. | TASK-209 |
| Schedule decision and information dialogs | Chosen act/alternatives, tie/lost reasons, stage/time/stars/member details, walking evidence, locked/shared selection and error/retry; clear close/selection focus and no false saved status. | TASK-209 |
| `SettingsActivity` | Appearance, account/group, ratings/allocation, sync and Admin sections; adjacent Link bands/music discovery; export/invite/manual sync/close reachable; accurate saved/pending/error feedback. | TASK-210; support actions TASK-211 |
| `BandLinkReviewActivity`, `BandMetadataReviewActivity` | Edited search, selected scope/results, source links, ambiguous/no match, evidence/selection retention, cancel/retry/provider configuration/rate-limit/offline/error; explicit approved missing-field writes only. | TASK-210 |
| `MusicLinksReviewActivity` | Band and festival entry points, canonical candidate URL, identity/rationale/clickable sources, verified/unverified, progress/cancel/reject/no match/retry; never save unverified or overwrite existing metadata. | TASK-210 |
| `ArchivedFestivalActivity`, `ArchivedBandDetailActivity` | Read-only list/detail, retained history even without lineup, established/planning/after labels and dated source context, empty archive/loading/error/offline/pending recovery. | TASK-211; rating context TASK-208 |
| `ArchivedRatingCorrectionDialog` and confirmation | Own kind/value/revision/dates, explicit confirmation, busy/conflict/reload/error/cancel states, no duplicate event on theme/recreation. | TASK-211; rating semantics TASK-208 |
| Add-festival entry/form and `FestivalBandReviewDialog` | Explicit name/CSV choice, reviewed aliases/canonical identity, selections/confirmation, validation/replay/conflict/rollback/retry, no fabricated links; once-only carry-over and no false group success. | TASK-211 |
| Rename dialog in `SettingsActivity` | Existing name/edit/cancel/confirm, busy/error/acknowledgement and retained draft; no write on restyle. | TASK-211 |
| Archive confirmation/progress and `DeleteFestivalDialog` | Group-wide action scope, pending-work checks, confirm/cancel, busy/failed/acknowledged outcome; retain data guarantees and avoid duplicate operations. | TASK-211 |
| `ImportCsvActivity`, `LocalCsvImportReviewDialog` | Five-file selection/schema/status, row review defaults and selections, long validation errors, atomic apply/rollback, stale review/reload/retry/cancel; retain URI grant or explain safe reselection. | TASK-211 |
| `MissingFestivalRatingDialog` and discard confirmation | Original pending scope/value, review/reassign/recovery or explicit discard; keep pending work until acknowledged action. | TASK-211 |
| `LoginActivity` and session recovery | Retained nonsecret input, password handled safely, sign-in progress/failure/retry, cached work kept, return destination; no new auth/security behavior. | TASK-211 |
| Startup splash, background sync overlay/status, toast/status feedback | Startup image versus nonblocking later sync, cached view, refreshing/pending/offline/failed/retry/shared-saved; do not replace readable state with animation. | TASK-211 |
| Android file picker, share/export/invite targets and external music browser/app | Theme owned by Android/target app; style the app-side launcher/return/status, retain context, handle cancellation/denial/missing target accurately. No promise to restyle another app. | TASK-211; music return TASK-206/207/208 |

## State wording and accessible feedback

| Condition | Required meaning / next action |
| --- | --- |
| Initial loading | Explain what is loading; show cached content if available. |
| Refreshing cache | “Refreshing” with existing content; no blocking full-screen reload for later sync. |
| Offline cached | “Offline · showing cached data”; editing stays local where supported. |
| Pending local write | “Saved on this device · pending sync”; do not call it shared-saved. |
| Shared acknowledgement | “Synced”/“Saved to group” only after the corresponding acknowledgement. |
| Sync/request failure | Explain the failed action, keep data, offer accurate Retry/recovery; avoid raw exception strings as primary copy. |
| Empty list / no search result | Distinguish no festival, empty lineup, no matching query and no verified provider match; supply relevant next action. |
| Read-only / unknown | Say “Archived · read-only”, “Unrated” or “Link not available” as appropriate; never invent data. |
| Cancel / denied / partial failure | Say what was retained and what needs explicit retry; cancellation is not success. |

Announce important save/error/progress changes without stealing focus on every
refresh. Rating controls announce value, meaning, selected state and scope;
selected navigation and toggles expose their state independently of color.

## Validation and release handover

TASK-205–211 add focused unit/native regressions as each slice lands. TASK-212
checks both appearances, System overrides, restart/recreation and device changes,
small/large screens, 200% fonts, target geometry, contrast, focus/keyboard and
TalkBack. Exercise cached/offline, pending/retry, provider failures, forms,
external returns and all lifecycle/rating distinctions. Capture actual screenshots
and device/API details; distinguish automation, human review and unverified
observations. Do not infer sunlight usability or timed performance from CSS.

TASK-213 requires the current signed GitHub channel, exact latest-stable in-place
upgrade, retained login/cache/pending/member/festival/history/correction data,
green ArchUnit/Groma/business/native/database gates, fresh signed APK, checksums,
source tag and downloaded published-asset verification. Existing signing-key
and downgrade constraints remain. No new providers, backend theme sync,
multi-festival/group scope or scheduling/rating rule changes are authorized here.

# TASK-209 schedule presentation review

The existing schedule responsibility retains its ten exact source files and
all previous collaborations/flows.96 components,299 uniquely owned Java sources
and13 flows remain. CLI descriptions explain native selectors, local filter and
viewport restoration, waiting for cached locks before restoring a decision,
large-font geometry and wrapping review actions. One supported collaboration,
ScheduleActivity→WackenTheme (Styles calendar controls), records actual shared
styling calls. The visual-theme overview now includes focused selector panels.
No runtime, data boundary, business policy or dependency was added.

The rendered export `/private/tmp/wacken-209-map` was inspected in Chromium:
schedule overview, source tab and actual caller/generator/navigation/theme
relationships agree with source. The calendar remains essential two-dimensional
content; its surrounding controls/dialogs wrap. Native89 tests independently
validate compilation, filters/viewport/empty results/recreation and200% decision
selection with preserved personal values. Unit/domain tests retain tie, scratch,
walking allowance and day-cutoff rules.

## Exact duplicate dispositions

There are81 findings:79 identities/dispositions are unchanged. Two replacements
were reviewed against exact source members and tokens before acceptance:

- `1a81dbb768dd` replaces `89412423aac9`:11 role-specific TextView builders.
  Schedule stageHeader drops out after adopting14sp full stage names, multiline
  behavior and lane alignment. SectionTitle uses shared contextual heading and
  left alignment. Remaining detail/link/metadata/CSV/music/schedule/Settings
  label builders retain their own role semantics; common palette/heading
  primitives already centralize styling. Retain intentional native widget
  construction; remaining screen work belongs210–211.
- `727eb7e0c1ad` replaces `859e80e0e6e4`:the same three schedule grid layout
  methods (hourGridLineLayout,halfHourNotchLayout,halfHourGridLineLayout) now use
  font-scaled calendarDp. Their different widths/heights/offsets represent
  distinct hour-line, notch and half-hour-line geometry. Keep short explicit
  assemblies using shared scale/offset helpers rather than a flag-driven builder.

Exact member lists and dispositions are recorded in the baseline. Pins and the
two existing TASK228 stale-arrow exceptions compare exactly equal; no new
exception is accepted.

## Scanner confidence

Android missing-framework override warnings increase238→245 for the two Activity
lifecycle overrides, two adapter view overrides, two selection callbacks and
native accessibility delegate override. Other warning categories/counts and
225 deferred method-reference facts are unchanged. Missing external-type facts
4306→4349 and unresolved-call facts9186→9279 reflect native Spinner/adapter,
drawable and accessibility/lifecycle APIs; the scanner lacks Android framework
types. Actual Android compilation/instrumentation/lint independently pass.
Coverage changes2376→2393 operations,1483→1490 tokenized bodies and12037→12138
invocations. Source ownership, relationships and diagnostics remain gated by
two repeatable clean-checkout scans with the pinned Java scanner.

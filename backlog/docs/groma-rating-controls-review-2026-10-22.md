# TASK-208 scoped rating presentation review

The existing Band detail screens responsibility now also owns the small shared
`RatingValueText` vocabulary used by its native rating choices and the lineup.
Groma CLI combined the empty scanned helper record before describing it. No
component/runtime/data boundary was added:96 components,299 owned Java sources,
13 flows. The shared native control already belonged to this detail responsibility.
The visual-theme overview now explains selected/focused native surfaces alongside
the existing approved preference/controller behavior.

## Meaning and collaborations

Active detail is planning-first with a retained After the show disclosure.
Existing application writers still own member/festival checks and independent
planning/after/history semantics. The control reports scoped values and selected
state through native Buttons; it stacks at large fonts and disables archived
children. The plain display vocabulary does not validate ratings or decide
archive promotion. Canonical-band discovery preserves the pre-existing
no-festival entry and is absent in captured archived context. Both behaviors are
covered by the native music-entry and read-only regressions.

Five additional supported file collaborations were authored through CLI:

| Source | Target | Meaning |
| --- | --- | --- |
| MainActivity | RatingStarsView | Renders planning choices using its existing save callback. |
| ArchivedFestivalActivity | RatingStarsView | Renders disabled read-only planning values. |
| LineupPresentation | RatingValueText | Formats the same planning meaning without duplicating vocabulary. |
| RatingStarsView | WackenTheme | Styles native selected/focused rating choices. |
| BandDetailPresentation | WackenTheme | Styles shared band information. |

No existing row or flow was removed. Rendered export
`/private/tmp/wacken-208-final-map` shows the detail overview, its exact seven
source files and the actual caller/style collaborations. Source/native tests
distinguish allowed canonical lookup, captured archived context and the separate
rating roles; arrows do not grant write authority.

## Duplicate dispositions

There are81 findings:79 identities/dispositions are exactly unchanged, and two
are reviewed replacements rather than a count allowance:

| Replacement identity | Prior identity | Evidence and disposition |
| --- | --- | --- |
| `89412423aac9` | `b1bb15d1cf48` | The TextView-building group now has12 members. `groupRatings` drops out because it has an explicit read-only planning accessibility description; sectionTitle uses the shared contextual heading and left alignment. Other members remain screen-specific label builders across detail/link/metadata/CSV/music/schedule/Settings. Shared palette/heading primitives already centralize style; a cross-screen generic builder would hide role-specific labels/semantics. Retain intentional Android widget construction; remaining screen layout work stays209–211. |
| `2da5b48aef12` | `480b98ef6296` | The two archived planning/after section constructors now pass explicit distinct scope labels to the shared disabled native control and use wrapping dimensions. They retain different values/roles and group-planning context. The shared control/section primitives already remove mechanical duplication; keep the two short role assemblies rather than conflating planning and after semantics. |

Every replacement lists its exact members and this evidence in the baseline.
No new exception is accepted; pins and the two existing TASK228 stale-arrow
exceptions compare exactly equal.

## Scanner confidence

All compiler-warning categories/counts remain unchanged. The one extra deferred
method reference is the exact festival-name display projection (224→225).
Missing Android/external-type facts grow4270→4306 and unresolved-call facts
9095→9186 for native Buttons, drawable states, scoped display and disclosure
binding; the scanner does not load Android framework types. Actual native
compilation, unit, instrumentation and lint gates are independent evidence.
Coverage grows2360→2376 operations,1471→1483 tokenized bodies and
11905→12037 invocations; all299 sources are uniquely owned.

The external candidate was reviewed against source, the rendered map and exact
semantic/identity/evidence changes before updating the normal baseline. Normal
validation still requires two repeatable scans and rejects unreviewed source,
ownership, role, collaboration, duplicate or diagnostic drift.

# TASK-207 lineup presentation review

The existing Festival home screen now owns `LineupPresentation.java` as an
internal display helper alongside its Activity and existing presentation/scroll
helpers. Name search, the Unrated filter and planning labels operate on the
already scoped ordered display items; they do not select a member/festival,
change rating rules or access persistence. The temporary scanned helper record
was combined through Groma CLI before adding meaning. Existing home identity,
group, collaborators and all13 flows remain. Scope is96 components/298 sources.

Rendered export `/private/tmp/wacken-207-map` was inspected: the home role,
its seven exact source files and existing collaborations are visible. The
overview now describes wrapping real data, retained local filters, known-only
music actions and cached/offline recovery. No runtime/data boundary changed.

## Duplicate identities

The candidate has81 identities, all unchanged identities from the prior88 with
their exact existing review dispositions. There are no new identities. These
seven pairs disappeared because the corresponding compressed home-table helper
was removed; the archived implementation is retained for TASK-211:

| Removed identity prefix | Home helper / archived equivalent |
| --- | --- |
| `2dfc1fb5d6ea` | tableHeader / headerRow |
| `65f540cb3122` | iconButton |
| `944243ce1eb7` | fixed, single-line cell |
| `a3efb60a885f` | fixed-height tableRow / row |
| `aecc1b31d0cc` | compressed rowActions |
| `f05e8cff569c` | headerCell |
| `f91943fb91e1` | single-line bandNameCell |

This is source-supported deletion of obsolete presentation helpers, not a
finding-count allowance. The new wrapping rows retain actual performance and
planning values and existing use-case writes, characterized by native tests.

## Scanner evidence

Pins and exact exceptions are unchanged. Only the home component's overview
and source ownership change; all authored rows and flows compare equal.
The override warning category grows235→238 for the three TextWatcher callbacks:
the scanner has no Android framework types. Actual Android compilation, unit,
native and lint gates pass. Other warning categories/counts stay unchanged.
Missing external-type facts decrease4274→4267; unresolved-call facts grow
9066→9092 with Android input/list binding. Deferred references remain224.
Coverage grows2351→2360 operations,1470→1471 tokenized bodies and
11862→11903 invocations. Removed helpers are accounted for above; no source
or operation evidence is silently discarded. All298 sources are uniquely owned.

The candidate was generated outside the repository and reviewed against source,
the rendered map and exact identity/evidence deltas before updating the normal
baseline. The standard gate must still pass two repeatable scans with no new
island, dangling collaboration, unowned source or unreviewed finding.

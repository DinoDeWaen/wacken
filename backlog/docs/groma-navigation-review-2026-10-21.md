# TASK-206 primary navigation review

This increment keeps the approved Android Views/presentation composition and
adds one stateless shared primary-destination responsibility. It introduces no
new framework, module, stored navigation model or business-policy owner.

## Source and architecture changes

`MainNavigation.java` builds the Bands/Schedule/Settings bar around the three
primary Activity contents, exposes selected state and reorders existing Activity
instances through Android Intents. Current destination selection is a no-op.
The code does not read or write member/festival data. Stable content/list IDs
allow Android hierarchy restoration; subflow Back remains Activity-owned.

The Groma scan created `mainnavigation`, then CLI operations assigned it to
Android screens and described its role as **Primary destination navigation**.
It stays independently meaningful from visual-theme preference ownership.
Current scope is96 components,297 uniquely owned main Java sources and13 flows.
No actor, external system or runtime/data container changed.

Seven current rows were authored through the CLI after reading source:

| Source | Target | Supported claim |
| --- | --- | --- |
| MainActivity | MainNavigation | Wraps the cached home content with primary destinations. |
| ScheduleActivity | MainNavigation | Wraps calendar content, including asynchronous rerenders. |
| SettingsActivity | MainNavigation | Wraps Settings while retaining its own subflow launchers. |
| MainNavigation | MainActivity | Opens/reorders Bands; selecting current Bands does nothing. |
| MainNavigation | ScheduleActivity | Opens/reorders Schedule without a duplicate instance. |
| MainNavigation | SettingsActivity | Opens/reorders Settings without a duplicate instance. |
| MainNavigation | WackenTheme | Uses shared contextual button/surface styling. |

These claims describe actual UI callbacks and Intent targets, rather than
assuming that every import is a runtime collaboration. Existing authored
relationships and all13 scenario flows remain; no relationship was deleted or
new dangling/island exception accepted. Exported map review uses
`/private/tmp/wacken-206-map`; the navigation component shows its exact source
and the three callers, three destinations and theme helper in rendered details.

## Duplicate and scanner review

All88 duplicate identities and their individual prior dispositions are unchanged.
No new candidate is accepted and no count allowance substitutes for identity.
Tool/scanner/work-source pins and the two exact accepted TASK228 stale arrows
are unchanged.

Compiler-warning categories and counts are unchanged. Exact source-scanner
evidence grows with the new Android View/Intent code: missing external type
facts4258→4274 and unresolved call facts9055→9066. The scanner does not load
Android dependencies; real Android compilation/unit/native tests remain the
authority for those types. Fresh coverage increases2345→2351 operations,
1467→1470 tokenized bodies and11833→11862 invocations; no supported source or
operation evidence was silently lost. The new source is owned exactly once.

The external baseline candidate is accepted only for these reviewed changes;
ordinary gate execution still requires two repeatable scans, no unexplained
islands and no unreviewed source/semantic/diagnostic drift. Source/native tests
also distinguish current-selection no-op, existing-instance reuse, correct Back,
recreation and return through the real package launcher intent.

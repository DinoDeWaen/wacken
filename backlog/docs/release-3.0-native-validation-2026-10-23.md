# Release 3.0 native validation — 23 October2026

TASK-212 validates the existing Android Views implementation against the approved
paired design contract. Publication and the supported signed upgrade remain
TASK-213. The evidence below is development evidence, not a claim that3.0 is
already published.

## Device and capture provenance

Only the owned disposable `appearance205` emulator was used: Android 16/API 36,
`google/sdk_gphone64_arm64/emu64a:16/BE2A.250530.026.D1/13818094:user/release-keys`.
Normal1080×2400 pixels at420dpi is approximately411dp wide; narrow640×960 at320dpi
is320×480dp. Each audited Activity uses an actual pre-creation fontScale 2 override.
This avoids relying on a shell font setting that may not update a live Activity.

[Capture manifest](../../design/release-3.0/native/evidence.json) contains exact
source/build hashes, device settings, real TalkBack service/version and native
role/value/focus evidence, plus hashes of44 actual PNGs. The paired audit captures
bands, scoped planning/after disclosure, Settings, schedule filters/calendar and
decision/actions, add-festival draft, music review and archived read-only rows.
The test checks the actual foreground app before capturing. No HTML/mockup image
is substituted for Android output.

## Accessibility and visual findings

The installed TalkBack 16.0.0.738667889 service was enabled with touch exploration;
the audit preserves accessibility services while UiAutomation is attached. Native
targets expose meaningful labels, selected values and roles, accept accessibility
focus, and have at least 48dp measured and reachable bounds. Real Tab/Enter checks
exercise visible keyboard focus and the after-rating disclosure. Opening or
restyling these controls does not create an after-rating or duplicate pending work.

Agent inspection of actual normal/narrow images covers planning focus, disclosure,
schedule selectors, full decision facts/actions, editable forms, evidence scope
and archived context. Content can be scrolled to read complete facts; the calendar
retains its intentional horizontal viewport. Editable single-line inputs retain
their complete native text/accessible value even when only part is visible.

Found and fixed during validation:

- Android 15+ enforced edge-to-edge let scrolling text and targets pass beneath
  status/navigation bars. A failing native bounds regression preceded shared
  content-host system-bar/cutout/keyboard insets. The exposed host background now
  follows the selected palette; image review caught and fixed its initial white
  background with white dark-mode system icons.
- Narrow200% primary navigation split the Schedule label. A native intact-label
  regression protects full-width stacked destinations below360dp at fontScale1.5+;
  other configurations retain the horizontal bar.
- Audit-only failures exposed an incorrect detail navigation assumption, shell
  argument quoting and the Accessibility Suite notification overlay. The harness
  uses the actual detail disclosure, direct shell commands and guarded observed
  notification denial on the test emulator. Failed captures are retained for
  diagnosis. Display/accessibility settings are restored after success/failure.

## Measured palette contrast

The independent JVM luminance calculation has black/white21:1 and equal-color1:1
reference tests. Every text/muted/accent/error foreground is checked on all three
approved surfaces, plus meaningful boundaries and primary action text.

| Palette | Lowest text ratio | Lowest boundary ratio | Primary text/action |
| --- | ---: | ---: | ---: |
| Ironline dark | 7.315:1 | 4.279:1 | 12.480:1 |
| Field Notes light | 5.619:1 | 3.698:1 | 8.233:1 |

These exceed the contract 4.5:1 text and 3:1 boundary thresholds. This measures
specified native colors; it is not full WCAG certification or a claim about every
antialiased pixel, native pressed state or TalkBack focus overlay. See the
[WCAG contrast method](https://www.w3.org/WAI/WCAG22/Understanding/contrast-minimum.html)
and [Android inset guidance](https://developer.android.com/develop/ui/views/layout/edge-to-edge).

## Regression and delivery gates

Final local full native suites pass99/99 each with initial Dark and initial Light,
without failures or skips. Their source record is `229682d`, with the same final
production files as `5cc6a8c`. CI on implementation/evidence head
`d3a9ac86f22f0fa378425eabbb069dffb315f760` passes all four jobs, including99 native
cases each on API23 and API36:
[final CI run](https://github.com/DinoDeWaen/wacken/actions/runs/39917868083).
The ordinary native suite supports API23; optional actual TalkBack automation
requires API24+. The capture manifest and44 committed image hashes were verified
again; both capture runs have identical debug APK hashes and restored settings.

The existing outcome suites cover find/rate/return; independent established,
planning and after values; archive read-only/correction review; schedule decisions
and explicit locks; identity/music/metadata proposal approval and cancellation;
CSV/forms, uncertain request recovery, System/theme transitions, recreation and
offline pending writes. Screenshots supplement these assertions.

Real PostgreSQL/PostgREST regressions passed the unchanged ADR0016 lifecycle:
band4 → planning4 → planning5/after3 → archive band3 → next planning3, with current
and older corrections, durable reset, replay, original dates and separate members.
Creation tests passed identity/alias/duplicate/replay/conflict rollback and
metadata/rating preservation. Backend behavior is palette-independent; paired
native presentation runs complement these actual server-policy tests.

The clean signed checkpoint at source `5cc6a8cacb31b7233e28691c5b5af906c14185b9`
passed `test qaTest :app:lintDebug assembleRelease --rerun-tasks`. Domain63,
application163, infrastructure29, QA3 and Android257 per debug/release JVM variants
passed. The16 PostgREST persistence cases in each variant remain intentionally
skipped without local fixture credentials and pass through the dedicated
`verify-festival-creation.sh` integration runner;
this is not a claim of zero JVM skips. ArchUnit/BDD/coverage gates are included.
Checkpoint APK SHA256 `de487df9a6a17ed72089be673242144ea3f43f953f0d9cb831401812155b7291`;
stable signing certificate SHA256
`ede2ae97735a305900e87bc0fdc021c03382db4e30435f2349a96a3fdcc7545f`.
This checkpoint is unpublished; TASK-213 builds the final versioned artifact.

## Architecture review

Safe-area fitting belongs to existing Android visual-theme ownership under
ADR0024. No module, dependency, schema, API, policy or flow boundary changes.
The CLI-authored overview and rendered theme details/source links were inspected.
There remain96 components,299 uniquely owned Java sources,13 flows and83 exact
duplicate groups. Existing duplicate identities/dispositions, pins and historical
exceptions compare exactly equal. The native inset listener adds one scanner
operation; it and adaptive navigation add fourteen invocations in total:
coverage2457/1515/12580. Nine additional external-type and fourteen unresolved-call
facts match Android WindowInsets/View/Configuration APIs missing from the scanner
classpath; no warning category/count increases.
Actual Android compilation and native bounds/palette regressions independently
validate the calls. The final two clean scans pass against the individually reviewed baseline.
Only the theme/navigation overviews change; source ownership, authored arrows and
flows compare exactly equal.

## Limits and follow-up

The user approved the qualitative paired prototype on 17 October 2026. No new human
study, spoken TalkBack comprehension, physical Switch Access, real phone, sunlight,
gloves, wet-screen or one-handed observation was performed. Automated real-service
focus and keyboard checks do not establish those outcomes. Switch Access hardware
is unavailable in the emulator; this report makes no hardware pass claim. Carry
these limits into release notes and physical-device review. No brightness or
outdoor usability guarantee is added.

An initial full native run lost the emulator after7/99 cases; it is an incomplete
run, not a pass. Docker was initially stopped; creation integration was rerun with
the required Java21 runtime after an unrelated default-JDK task-creation failure.
Final successful replacements, not interrupted attempts, count toward closure.

README impact: updated native commands, evidence and implemented-development scope.
Business requirements impact: updated actual validation scope, inset behavior and
honest field-validation limits; rating/snapshot policy is unchanged.
Diagram impact: updated CLI-owned theme responsibility; C4 boundaries/flows unchanged.
ADR impact: none, because this is native presentation fitting and existing-strategy
validation within the approved ADR0024 and ADR0016 decisions.

# TASK-205 appearance baseline review

The user explicitly approved the standard presentation controller in ADR0024.
This review covers that implementation, not a general relaxation of the gate.

## Ownership and meaning

The only semantic element change is `wackentheme`: its existing source and two
new files (`AppearancePreference.java`, `AppearanceController.java`) implement
one shared appearance responsibility in the Android application. New scanned
file-shaped records were combined into that owner through the Groma CLI, then
its overview was updated through the CLI to describe preference resolution,
in-place bindings and cleanup. The existing MainActivity-to-theme collaboration
remains valid. No authored relationship, scenario flow or runtime boundary was
removed. There are95 components,296 owned main sources and13 flows. No new
island or dangling-row exception is accepted; the two exact prior TASK228 stale
arrows remain reported. Test/resources/preferences are outside Java-only scan
coverage; native tests and source review establish their behavior.

Exported the map to `/private/tmp/wacken-205-map` and reviewed the rendered
overview; hierarchy still shows Android application and the two data stores,
with Android visual theme inside Android screens. The component source/role
inspection uses `groma view wackentheme` and the exported details. Browser
captures are local review evidence, not new source-of-truth files.

## Changed duplicate identities

Final candidate:88 groups, seven retired identities and seven replacements.
All unchanged identities keep their existing individual rationales. Method
bodies listed below were read after the semantic-binding changes; no count-only
allowance or new blank/automatic rationale is accepted.

| New identity | Source assessment and disposition |
| --- | --- |
| `35d83e11d7c48900e0df9ace8494c2ee2a8dce78ade64db83e16bd895ba54315` | Schedule hour-grid and half-hour-notch factories intentionally create independently sized calendar marks. Their only common body is constructing a View and binding the grid token. Geometry is supplied by different callers. Retain named meanings; calendar work is TASK209. |
| `82ba2760961c176d477d7a56c10fbd588a79476c5831ac20e072cb80bc78d36c` | Imported-band linking and metadata proposal content builders share a scroll/title/status/rows skeleton but have different use cases, review states and actions. Appearance attach/bind replaces static style calls without merging identity or approval policy. Retain separate screens; presentation work is TASK210. |
| `944243ce1eb7726c381fd923dfec3c29dcacd5ace7398945dca79a85933355db` | Active and archived text cells share text/weight/padding setup; archive read-only versus active editable behavior remains owned by their callers. Binding the same token is intentional. Retain current factories; TASK207/211 own list layout changes. |
| `a3efb60a885f733358653f6392ddb57fce83343e03352684cd82677807cd6587` | Active and archived row builders both bind an explicitly supplied surface token. Archive padding/layout and active ListView row ownership differ. Neither helper selects a festival or writes ratings; do not combine lifecycle logic to remove visual similarity. |
| `f05e8cff569cd5a7c75179755ad6d5cf2b1f37881691795a77c090a279ae19f7` | Active and archived header-cell setup retains the same typography/panel motif while calling differently shaped local cell factories. The dynamic panel binding is common style, not a duplicated business decision. Retain with the corresponding list screen. |
| `f91943fb91e1656740cd4ff09ca0543fb3fe212014873c9e6f5d4eef1b1049aa` | Band-name cells deliberately share a geometry/token motif across active/archive lists. The added binding changes identity tokens, not scope/editability; do not pull lifecycle behavior into the theme controller. TASK207/211 own reflow work. |
| `b1bb15d1cf481e2ff253f1b45d97dc8f99bb0a1816a46a4cfab57ff416b3ff4c` | Thirteen label/status/heading builders across detail, linking, metadata, CSV review, music review, schedule and Settings share TextView construction and semantic text binding. Their surrounding data, spacing, gravity, wording and failure/recovery roles differ. The common theme operation is already centralized; keep context-specific assembly and review it in TASK208–211 rather than introduce a generic screen framework. |

## Scanner evidence review

Compared exact pinned Java0.2.2 observations with the previous accepted baseline:

- Override warnings225→235: nine controller callbacks implement Android
  ActivityLifecycleCallbacks/ComponentCallbacks and Login adds the existing
  Activity retained-instance override. Android supertypes are unavailable to the
  source scanner. Actual Android compilation and native callback tests pass.
- Type-inference warnings36→37: the new warning is
  `AppearanceController.java:31`, inferring the WeakHashMap<View,...> binding map
  without the Android View type. The real Android compiler resolves it.
- Missing external type facts4182→4258 and unresolved call facts9031→9055 grow
  with the added Android controller/binding API usage. Existing category set and
  scanner/tool pins are unchanged. These are still analysis limitations; no
  diagnostic is relabeled as a compiler pass or broadly ignored.
- Fresh observation counts increase with the new helpers; no supported source
  or operation evidence is silently dropped. Two-scan repeatability remains a
  required gate against the accepted candidate.

The external proposal is reviewed and copied only after these dispositions.
Future identity/ownership/pin/diagnostic changes must fail and receive a new
review. Native/ArchUnit/business tests remain separate from scanner confidence.

# Architecture approval request

Task: TASK-205 — Choose System, Ironline dark or Field Notes light appearance

Status: standard option explicitly approved by the user on21 October2026
(“Standard”). [ADR0024](../decisions/0024-shared-native-appearance-controller.md)
records the accepted decision. TASK-204 design contract is completed separately.

## Proposed architectural change

Establish one shared, Android presentation-only appearance controller used by
the current Activities/dialogs through composition. It resolves System/Dark/Light,
owns the local preference adapter and palette notifications, and refreshes
explicit semantic view bindings. Keep existing Android Views and direct
`android.app.Activity`; no new UI framework or base-Activity inheritance tree.

This introduces shared lifecycle/persistent-UI-preference integration for future
screen work, which is the architecture decision requiring approval. The prior
prototype validation approved what the user sees, not this implementation choice.

## Why it is needed

`WackenTheme` and most Activities currently copy static final color constants;
`styles.xml` supplies a fixed Material Light window theme. Recreating Activities
alone will not dynamically update those colors. Several forms have no complete
Bundle restoration, while music review and CSV review already retain in-flight
sessions. Rebuilding those flows on every appearance change risks lost input,
duplicate requests and inconsistent dialogs. The approved contract requires
in-place changes and safe independent Activity recreation.

## Alternatives considered

1. **Minimal option:** duplicate preference/configuration callbacks and palette
   application in each Activity. Reuse current SharedPreferences and Views.
   Smallest shared abstraction, but eleven lifecycle implementations and dialog
   handling can drift; every new screen repeats the same retention risks.
2. **Standard option (recommended):** one composable appearance controller plus
   context-resolved palette/semantic helpers in the existing presentation area.
   Each screen keeps ownership of its form/session state and operations. Explicit
   bindings update views in place; no automatic rebuild or write on theme change.
   Device-local SharedPreferences follows the existing private-store pattern.
3. **Maximum option:** migrate to AppCompat/Material or Compose and a new shared
   navigation/state framework. This adds dependencies and migration scope; it is
   unnecessary for the approved Views redesign and is not recommended.

## Recommended option

Choose standard, with these implementation constraints:

- A pure appearance preference/palette policy handles invalid/missing values,
  device-mode resolution, opposite explicit toggle and next-action label. Pin
  palettes to TASK-204 and test enabled contrast combinations.
- A private device-local adapter stores only `system`, `dark` or `light`.
  No account/backend synchronization, Room table or database migration.
- The controller attaches/detaches with each Activity, resolves on initial
  creation/resume/configuration change and notifies attached presentation
  bindings. Background/returning Activities resolve the current preference.
  No global mutable palette copied into static Activity constants.
- Extend existing `WackenTheme`/component helpers with explicit roles such as
  text, surface, accent, error and heading. Replace hardcoded color copies as
  the affected native slices land; do not guess roles by traversing colors.
- Header and Settings act on the same preference. Normal theme changes update
  existing views/dialogs and system bars, preserving focus, scroll, input and
  retained review selections; they do not call use cases or repositories.
- Keep selected-festival/band, drafts, disclosures and review state owned by the
  corresponding screen. Use stable view IDs/Bundle and the already used retained
  session pattern where appropriate. Do not retain an Activity in a long-lived
  worker; disconnect old callbacks. Persisted pending work stays in existing
  stores. Process-loss recovery must be explicit and must not replay a write.
- Test pure resolution/persistence contracts and actual native System changes,
  explicit overrides, restart/recreation, inputs/scroll/disclosures, independent
  ratings and pending-event counts. Existing business/native/database tests stay
  required. Groma changes must use its CLI and reviewed baseline proposals.
- Record accepted ADR0024 after approval; implement sequentially under
  TASK-205–211, with TASK-212 integrated evidence before TASK-213 publication.

## Impact

- **Domain model:** none. ADR0016 rating/source rules remain authoritative.
- **Application layer:** none. No preference/theme dependency in JVM inner layers.
- **Ports/adapters:** presentation-local preference adapter only; business ports
  and backend APIs unchanged.
- **Infrastructure:** existing private SharedPreferences on the Android side;
  no Room/backend schema, provider or runtime dependency change.
- **Tests:** pure UI-policy tests and native lifecycle/retention/contrast checks;
  existing ArchUnit/Groma checks continue and intentional ownership/token changes
  receive explicit review, not automatic baseline acceptance.
- **README:** describe implemented preference/state behavior as slices complete.
- **Diagrams:** update Groma ownership/collaboration through CLI if affected;
  runtime C4 application/data boundaries remain unchanged.
- **ADR:** accepted ADR0024 for shared presentation lifecycle/preference ownership
  after approval. No framework-migration decision is implied.

## Approval needed

The user approved the standard option before production coding. TASK-206–213
depend on the appearance foundation and are handled sequentially, not marked
In Progress together.

This approval requirement comes from [AGENTS.md](../../AGENTS.md),
[architecture decision rules](architecture-decision-rules.md) and
[delivery governance](delivery-governance.md): architecture-significant changes,
including shared ownership/lifecycle and persistent-state design, require
explicit human approval before implementation. It is not a request to reapprove
the already validated visual design or unchanged rating lifecycle.

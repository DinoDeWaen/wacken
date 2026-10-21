# TASK-233: Reviewed architecture validation gate

## Status

Proposed; no validator or CI implementation has started. TASK-228 is closed with
the two exact stale authored relationships explicitly accepted by the user on
21 October 2026. The user also reviewed the corrected map after TASK-229/230.

## Architecture approval request

Task: TASK-233 — prevent architecture islands and unreviewed Groma drift in CI.

### Proposed architectural change

Add a development-only architecture check to local verification and the existing
CI workflow. Use the exact Groma source/patch, Bun and Java scanner versions
already pinned by TASK-227. Reuse the pinned Groma readers and scanner evidence
instead of maintaining a second architecture Markdown parser. Inspect raw
relationships as well as the annotated model: Groma can omit dangling file rows
from its resolved model, so checking only rendered relationships is insufficient.

Keep the check outside Android/domain/application modules. It never rewrites
Groma-owned architecture or accepts a new baseline during ordinary validation.

### Why it is needed

The map previously had47 disconnected component records even though most had
runtime callers. Source review and CLI curation resolved those records. A
repeatable check should catch missing ownership, unexplained islands, lost
collaborations and scanner degradation while retaining human review of meaning.

### Alternatives considered

1. **Minimal:** retain manual scans and a nonblocking report. Low implementation
   cost, but it would not meet the task requirement to reject drift in CI.
2. **Standard, recommended:** a standalone semantic validator, reviewed baseline
   and exact exception records, fixture tests and a blocking CI step.
3. **Maximum:** modify Groma lifecycle/reconciliation, build an architecture policy
   framework or automatically infer missing collaborations. This adds tool scope
   and can manufacture false meaning; excluded from this proposal.

### Recommended option

Implement the standard option with these concrete checks:

- Validate unique element IDs, one owner per included source, existing source
  files, required system/container/component containment, and meaningful role
  descriptions. Configuration exclusions remain explicit and versioned.
- Validate raw source/target endpoints and exact task references, plus resolved
  flow steps. Only actor/external IDs and supported software source endpoints are
  accepted according to the pinned format. Historical references outside active
  architecture work are reported separately from invalid current Groma IDs;
  ordinary contextual URLs/doc references must not be misclassified as IDs.
- Reject new unexplained disconnected responsibilities. Initial curated state
  has zero component islands; future implementation-detail exceptions require
  an exact identity, rationale and review evidence.
- Detect removal of reviewed authored collaborations, recreated components and
  unexplained ownership/containment changes using semantic identities, not
  diagram positions, counts or line numbers. Legitimate file moves/combines are
  accepted only with an explicit reviewed baseline update preserving meaning.
- Run two scans in a disposable clean checkout and compare normalized records,
  ownership, authored relationships and flows; never scan or restore fixtures
  destructively in the developer checkout. Verify exact tool/scanner pins and
  preserve the existing scanner scope.
- Baseline current duplicate candidates with stable member/source/symbol/content
  identities tied to the original99-group audit and TASK-240 dispositions. The
  current88 finding groups are not an allow-by-count rule. Reordering and source
  line shifts must not change identity; new/changed candidates require review.
- Surface scanner diagnostic categories and degraded coverage against the
  reviewed confidence report. Missing scanner/evidence or changed pins fail;
  known unresolved framework/generated/cross-module limitations are explicitly
  reported as scanner limitations, not Android compiler failures.
- Emit actionable diagnostics naming the violated element/file/relationship,
  plus a machine-readable report. Normal runs cannot regenerate or approve the
  baseline automatically. PR evidence must explain any intentional update.

### Accepted TASK-228 exception

Exactly two historical authored rows may reference the deleted importer:

| Source | Target |
| --- | --- |
| `app/src/main/java/be/wacken/planner/ImportCsvActivity.java` | `application/src/main/java/be/wacken/planner/application/ImportFestivalCsvUseCase.java` |
| `application/src/main/java/be/wacken/planner/application/ImportFestivalCsvUseCase.java` | `domain/src/main/java/be/wacken/planner/domain/Band.java` |

Each exception records TASK-228, the user acceptance, pinned-tool limitation and
the evidence report. The deleted file must not regain Code ownership, no flow
may use these rows, and no other dangling endpoint is allowed. The report calls
them accepted stale claims rather than valid collaborations. If the tool later
supports safe removal and the rows disappear, remove obsolete exceptions in the
same reviewed change. This does not approve the deferred Groma tooling extension.

### Validation before completion

Write failing fixtures before implementing each rule. Include positive cases
for the curated baseline, harmless ordering/line shifts and reviewed file
moves/combines; negative cases for disconnected roles, missing sources, duplicate
ownership, lost authored rows, invalid task references, recreated components,
broken flows, expanded exceptions, new duplicate identities and missing/degraded
scanner evidence. Validate that temporary fixture restoration and clean-checkout
checks leave the developer tree unchanged. Run the existing relevant regression
suite and repository-required fresh stable-key signed APK build.

The validator cannot prove that prose or an arrow is true. Source/composition and
rendered browser review remain required; a clean automated report is supporting
evidence, not architecture approval.

### Impact

- Domain model, application, ports/adapters and production infrastructure: no
  runtime, schema, permission, API, dependency or signing change.
- Development tooling: standalone validator/tests, explicit baseline/exception
  data and a blocking step in `.github/workflows/ci.yml`. CI installs exact Bun,
  patched Groma and Java scanner versions; no floating scanner update.
- Tests: development test-strategy change, with positive/negative semantic
  fixtures and clean-checkout/two-scan reproducibility.
- README: local/CI invocation, baseline review and diagnostic interpretation.
- Delivery guidance: required PR evidence for intentional architecture drift
  and exception updates; no automated baseline acceptance.
- Diagrams: existing application boundaries remain; CLI-authored map remains
  the source of architecture meaning.
- ADR: create ADR0022 only after explicit approval, recording the gate scope,
  accepted limitations and human review requirement.

### Approval needed

Approve the standard option before validator, tests or CI implementation.
TASK-233 AC6 explicitly requires "Architecture/test-strategy approval is obtained
before implementing this gate." AC7 and delivery-governance.md impose the same
approval gate for changes to future test strategy. This proposal requests that
specific decision; permission to close TASK-228 does not change its scope.

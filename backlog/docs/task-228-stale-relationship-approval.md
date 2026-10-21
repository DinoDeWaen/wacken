# TASK-228: Remove a verified stale authored relationship

## Architecture approval request

Task: TASK-228 — connect festival creation, CSV review and canonical identity.

### Proposed architectural change

Extend the approved local Groma patch with an explicit flag to remove one current
**authored** relationship after source review. Keep default draft-only removal,
reject inferred/derived relationship removal, and retain the existing guard
against removal while a flow uses that relationship. Validate the whole change
before writing. No bulk deletion or application-code change.

### Why it is needed

The current map links ImportCsvActivity.java to ImportFestivalCsvUseCase.java
and the import-festival-csv flow uses that edge. Source review confirms the
Activity only retains an unused import of that class; it calls
ReviewFestivalCsvUseCase through LocalCsvImportReviewDialog instead.
The flow can be corrected through the CLI, but removeRelation currently refuses
all stable authored relationships even once no flow uses them.

Leaving that arrow would falsely connect a legacy use case to the current
workflow. Renaming its label would not establish an actual collaboration.

### Alternatives considered

1. Minimal: document the stale arrow and leave TASK-228 incomplete pending an
   upstream correction.
2. Standard, recommended: explicit current-authored removal in the pinned CLI,
   with exact endpoints, preserved flow guard and positive/negative tests.
3. Maximum: redesign relationship lifecycle or broader reconciliation. Out of scope.

### Impact

- Domain model/application/ports/infrastructure: no deployed change.
- Tooling: one explicit correction operation; unrelated authored meaning/rows,
  IDs, source ownership and flows remain intact.
- Tests: red/green stable-row removal; default refusal, derived-row refusal,
  used-by-flow refusal and unrelated-row preservation; full upstream checks.
- README: updated patch pin/build instructions and corrected workflow links.
- Diagrams: correct the local-review flow before removing the stale edge.
- ADR: extend ADR0021 with explicit user approval and exact patch/build evidence.

### Approval needed

Approve the standard narrow extension before implementation. AGENTS.md requires
explicit human approval for architecture-significant tooling changes. This is
additional to the earlier approval for container declaration/component moves.

# ADR0022: Reviewed Groma architecture validation gate

## Status

Accepted on21 October2026. The user selected **standard** for the concrete
TASK-233 proposal, then requested assessment of ArchUnit or better alternatives.
This approves the development-only gate and test-strategy change described in
the proposal; it does not approve the separately deferred Groma relation-removal
extension or change application architecture.

## Context

The architecture map had47 disconnected component records despite real runtime
callers. TASK229/230 corrected source-backed ownership and collaborations, leaving
95 components,294 unique owned sources and13 flows. A scanner refresh alone does
not verify authored meaning or prevent lost architecture knowledge. TASK228
closed with exactly two documented stale authored rows accepted by the user
because the pinned CLI cannot remove current authored relationships.

## Decision

Add a standalone semantic validator and positive/negative fixture suite, invoked
locally and in CI. Use the already pinned Groma source/patch, Bun1.4.3,
Java scanner0.2.2 and configured Backlog1.53.0. Reuse Groma readers, raw relationship
parser, model and fresh scanner evidence; inspect raw rows because the resolved
model can omit dangling file relationships.

Validate ownership/source existence and scanner coverage, containment and useful
documentation, current architecture task references, exact flow relationships,
new unexplained islands, lost authored meaning and semantic drift. Baseline
duplicate candidates by full source/member/normalized-token identities, never
line numbers, finding order or total count. Every accepted duplicate has an
individual rationale and review evidence.

Perform scans only in a disposable clean Git checkout; compare curated semantics
before scanning and after two scans. Reject newly created/recreated components,
lost observations, failed/conflicting scans, changed tool/scanner pins and
diagnostic degradation. Emit human and JSON diagnostics. Known unresolved Java
framework/generated references are scanner limitations, not application compile
errors.

The two TASK228 stale rows are exact exceptions with recorded user acceptance,
rationale and evidence. The deleted source may not regain ownership or appear in
a flow; no other dangling endpoint is accepted. Report these as stale claims,
not valid interactions. New exceptions and intentional baseline updates require
source/browser review and PR evidence. Ordinary validation never accepts or
rewrites a baseline; proposals are written outside the repository for review.

Manual semantic review remains mandatory. An automated green result cannot prove
that a responsibility, arrow, public workflow or SQL authorization claim is true.

## Consequences

- CI catches structural/semantic knowledge loss while retaining intentional
  duplication and the explicit historical tooling limitation.
- Validation uses additional development runtime/build time, but no application
  dependency, schema, permission, runtime boundary or signing contract changes.
- New findings and legitimate moves/combines need explicit baseline review.
  Harmless ordering and line shifts do not change stable identities.
- The pinned reader source is coupled deliberately to the exact Groma revision.
  Tool upgrades require compatibility tests and a reviewed baseline update.
- ArchUnit is a useful future complement for executable Java dependency rules.
  It does not replace map integrity checks, and this task adds no ArchUnit or
  graph-platform dependency. See the separate tool assessment.

## Alternatives considered

1. Manual/nonblocking reports: retain meaning review but fail to reject drift.
2. Standard semantic gate: chosen by the user.
3. Broader Groma lifecycle changes or a graph-policy platform: unnecessary scope.
4. ArchUnit/jdeps alone: useful bytecode evidence but cannot cover map artifacts.

## Links

- [Approved proposal](../docs/task-233-architecture-gate-proposal.md)
- [Tool assessment](../docs/architecture-tool-assessment-2026-10-21.md)
- [Scanner confidence](../docs/groma-scanner-confidence-2026-10-21.md)
- [Accepted festival-map limitation](../docs/groma-festival-administration-evidence-2026-10-21.md)
- [Existing tool pin decision](0021-pinned-groma-container-curation-fix.md)


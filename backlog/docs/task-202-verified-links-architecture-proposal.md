# Architecture approval request: TASK-202 verified music links
Status: Proposed, awaiting explicit human approval. Date: 2026-10-17.

## Proposed architectural change
Extend the accepted ADR-0012 reviewed-metadata boundary with source-evidence facts and an application-owned identity-verification result. Existing candidate records currently carry a name, URL and confidence score; those do not establish band identity. Add a band/festival-scoped music-only orchestration path with edited search terms that never rename the band.

Provider adapters supply retrieved identity facts, canonical platform identifiers and source links. The application decides whether evidence establishes the band identity and gates approval; the Android UI cannot turn a name match into a verified result. Add a bounded HTTPS cross-link retrieval adapter for official band/label pages where existing authoritative relationships are insufficient. Reject unsafe/nonpublic destinations and unbounded redirects/body sizes. A ranking score, platform badge or matching name never establishes verification.

Retain existing optional YouTube/Spotify configuration and MusicBrainz/Wikidata integrations. No new provider account, credential storage, backend schema or durable evidence store in the recommended slice. Evidence is available in the current review; restarting a lost review requires a fresh lookup. Save missing golden-source fields only after explicit approval using the existing synced band repository, rechecking identity/scope and no-overwrite rules.

## Why it is needed
Current YouTube exact-name search reports confidence 95 without identity evidence. Current generic proposal saving checks missing fields but not verification. Existing review searches all catalog metadata and has no single-band edited-term or selected-festival music-only scope. Simply adding two buttons would not satisfy TASK-202.

## Alternatives considered
1. Minimal: expose existing generic lookup and leave identity checking entirely to users. Rejected: no enforceable verified/unverified save boundary and unrelated enrichment.
2. Standard (recommended): extend existing provider/proposal boundary with evidence and application verification; scoped music-only review, in-memory evidence, existing missing-field sync. Obtain verified evidence or leave links empty.
3. Maximum: new hosted discovery service, secret custody, durable verification audit and queued/resumable backend jobs. Defer: larger integration, schema and operations footprint.

## Impact
- Domain model: preserve Band, ratings, history and lineup identity. Application owns evidence sufficiency and canonical-link validation.
- Application layer: typed evidence/result and scoped lookup/apply behavior, progress/cancel/retry, no-match and ambiguity.
- Ports/adapters: extend the existing metadata provider contract compatibly; evidence retrieval remains in adapters. Preserve the generic metadata path.
- Infrastructure: existing synced repository and provider configuration; no migration or new service. External retrieval is bounded and cancellation-aware.
- Tests: failing BDD/use-case/adapter tests before implementation; scope, namesakes, invalid URLs, evidence mismatch, no overwrite, failed sync, rate limit/cancel/retry and native entry-point wiring. Reuse existing Android instrumentation setup.
- README: entry points, evidence requirements, provider configuration and restart/retry behavior.
- Business requirements: document implemented verified-link workflow and retained metadata/history invariants after implementation.
- Diagrams: update only if this extends a documented integration flow.
- ADR: add an accepted evidence-boundary decision or amend ADR-0012 after approval.

## Approval needed
Approve the standard option, choose a different option, or reject. Repository AGENTS.md, architecture-decision-rules.md and delivery-governance.md require explicit approval before provider-contract/integration changes. Approval of the 3.0 visual design does not by itself approve this integration change.


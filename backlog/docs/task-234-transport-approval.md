# Architecture approval request

Task: TASK-234 — Bound rating and schedule network waits without losing pending changes

## Proposed architectural change

Add one package-private `SupabaseHttpTransport` in the existing Android adapter
package, `be.wacken.planner`. It owns only HttpURLConnection mechanics: connection
and read timeout configuration, method/headers, optional JSON body, response
status/body extraction, stream closure and disconnect in `finally`.

Production defaults match the existing master-data client: **15,000 ms connect**
and **30,000 ms read**. A package-private connection factory and timeout constructor
allow deterministic tests and controlled loopback stalls. They are adapter test
seams, not domain policy supplied by UI callers. No new library or package/module
is introduced. Public client constructors continue using production defaults.

`SupabaseAuthenticatedRequest` continues to own fresh sessions, member binding
and the single expired-JWT refresh/retry. Clients retain their endpoint building,
request payloads, upsert choices, parsing and operation-specific failures.
The transport does not acknowledge pending writes or mutate caches/sessions.

## Why it is needed

The four send methods in SupabaseRatingClient, SupabasePersonalBandRatingClient,
SupabaseFestivalPlanningRatingClient and SupabaseScheduleLockClient currently
set neither timeout. SupabaseMasterDataClient already sets 15s/30s. Repeated
mechanics have drifted. Sharing those mechanics makes the timeout/resource
behavior testable without exposing transport concerns to domain/application.

## Alternatives considered

1. **Minimal:** add timeout calls separately in the four existing send methods.
   Smallest production diff, but duplicates resource handling and makes controlled
   transport tests more awkward; existing divergence can recur.
2. **Standard (recommended):** shared package-private transport in the existing
   adapter package, existing auth wrapper preserved, no network framework.
3. **Maximum:** introduce a networking framework, full cancellation/deadline
   orchestration and a new transport abstraction across all integrations.
   Broader dependency and lifecycle impact exceeds this task.

## Recommended option

Standard. Characterize request contracts first, write failing timeout/resource
tests, implement the transport, migrate the four affected clients, then run the
existing sync/auth regressions. Sharing the existing master-data send mechanics
is allowed only if its characterized contract remains identical. Authentication
transport changes, provider HTTP clients and image loading remain outside this
task.

Connect/read timeouts bound connection establishment and inactive socket reads;
they are not a total sync deadline, DNS deadline, upload deadline or defense
against an indefinitely slow trickle. Tests and README must say what is actually
bounded. A retry can perform two bounded attempts. Do not claim every sync
finishes within 45 seconds.

## Impact

- **Domain model:** unchanged; no HTTP dependencies or external policy objects.
- **Application layer:** unchanged; pending changes remain acknowledged only
  after successful remote operations.
- **Ports/adapters:** shared internal HTTP mechanics; existing remote interfaces,
  operation identity, member/group checks and business mappings retained.
- **Infrastructure:** Android HttpURLConnection only; no schema, platform config,
  production credentials, public API or new dependency.
- **Tests:** characterize methods/headers/body/upsert/status/error contracts;
  verify connect/read configuration before I/O, stream/disconnect cleanup and
  local response stalls; retain auth refresh/retry and pending/cache tests.
  Run Android API compatibility lint and relevant native validation.
- **README:** describe precise timeout/retry behavior and cached/pending retention.
- **Diagrams:** update Groma ownership and source-supported client-to-transport
  collaborations through CLI. No runtime boundary changes.
- **ADR:** add an accepted ADR after explicit approval, recording this narrow
  transport ownership and timeout policy.

## Approval needed

Approve the standard shared internal transport, choose the minimal timeout-only
option, or reject. No implementation starts before approval.

This gate comes from AGENTS.md and `backlog/docs/delivery-governance.md`:
“Before implementing an architecture-significant change, stop and request
approval.” TASK-234 AC4 explicitly requires approval and ADR handling for a new
shared transport boundary. The task existing or a general instruction to start
the milestone is not approval of this particular design.

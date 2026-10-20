# ADR 0020: Bounded Supabase adapter transport

## Status

Accepted — explicitly approved by the user on 20 October 2026:
“task 234 go for standard proposal”.

## Context

Rating, festival planning, personal history and schedule-lock clients duplicated
HttpURLConnection send/read code without explicit timeouts. The master-data
client already used a 15-second connect timeout and 30-second read timeout.
On inactive connections the four other clients could retain a sync worker
without a configured timeout. Pending local changes must survive such failures.

## Decision

Use one package-private `SupabaseHttpTransport` in the existing Android adapter
package for those four clients. Configure **15,000 ms connect** and **30,000 ms
read** before I/O, close owned streams and disconnect in `finally` on success
and failure. Keep existing HTTP methods, UTF-8 bodies, headers, optional upsert
preference and status/body response shape. Empty error streams remain empty
responses for existing operation-specific error handling.

The transport accepts a package-private connection factory and timeout values
for controlled tests. Normal client constructors use production defaults.
UI and domain/application callers do not select transport policy.

`SupabaseAuthenticatedRequest` still owns session acquisition, member-binding
checks and the single rejected-JWT refresh/retry. Each client still owns its
endpoints, serialized business payloads, canonical identity mapping and response
parsing. Sync repositories acknowledge pending writes only after successful
remote calls. A timeout propagates as IOException and must not clear pending
edits or a valid cached session.

This adds no framework, Gradle module, public API or database change. Auth HTTP,
master-data HTTP and metadata/image HTTP are not migrated by this task; the
master-data client retains its existing matching timeout values.

## Consequences

Positive:

- A reviewed timeout/resource policy covers the four formerly unbounded paths.
- Request contract tests and controlled local stalls need no live Supabase.
- Domain/application layers remain independent of HTTP.

Limits and trade-offs:

- Connect/read timeouts bound connection establishment and inactive reads, not
  total sync duration, DNS resolution, blocked uploads or indefinitely slow
  trickles. Do not claim a 45-second overall deadline.
- JWT refresh can cause a second bounded attempt, and sync can send multiple
  requests. A timeout after an accepted write can leave an operation pending;
  existing upsert/event identities handle retry semantics.
- This is not general cancellation or lifecycle scheduling. Such changes need
  their own scope and approval.

## Alternatives considered

- Add timeout setters to each duplicated method: smaller initial diff, but
  leaves the demonstrated mechanics drift and cleanup duplication.
- Shared internal transport (chosen): one small adapter owner with controlled
  tests, existing auth/business policies preserved.
- New network framework and full deadline/cancellation orchestration: broader
  dependency and lifecycle change than this defect requires.

## Links

- TASK-234
- [Approved proposal](../docs/task-234-transport-approval.md)
- [Offline-first sync boundary](0010-offline-first-sync-boundary.md)

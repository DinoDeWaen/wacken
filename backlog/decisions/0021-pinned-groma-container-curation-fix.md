# ADR 0021: Pinned Groma container curation fix

## Status

Accepted — user explicitly approved the standard tooling proposal on 21 October
2026: “i approve”. Scope is TASK-227; TASK-233 CI/test-strategy approval is separate.

## Context

Groma 0.6.6 and the latest published 0.7.2 reject acceptance of source-free
containers and relocation of described components. Upstream issue170 documents
the same failure. Android execution and owned Room/PostgreSQL storage boundaries
are established by manifest, composition, SQL and existing ADRs. Inventing Java
entry points, clearing descriptions or directly editing architecture Markdown
would destroy truthful evidence.

## Decision

Use a repository-pinned, locally built Groma0.7.2 patch. Upstream commit,
patch SHA256, Bun1.4.3 and unchanged Java scanner0.2.2 are recorded in
[the pin](../../tools/groma/pin.json). The setup script builds a fresh upstream
checkout with that exact patch; the launcher checks the pin, patch and locally
recorded executable digest before execution. Generated tool sources/binaries
stay in ignored .local/groma. Global and user-installed npm packages are not
replaced. Builds are identified by their executable digest; identical bytes
across different build hosts/paths are not promised.

The patch adds explicit `accept <container> --declared` for reviewed existing
runtime/storage containers. Ordinary component acceptance still requires source
evidence. This is a standard C4 Container with stable lifecycle in ordinary OKF
Markdown, without a new metadata field or scanner-verification claim.

Component moves preserve descriptions, technology, group, identity and source
references. They reuse existing rename link rewriting to rebase outgoing and
repoint incoming relationship/flow links, then validate the complete result
before writing. Unsupported semantic link forms still fail validation.
Container moves and combines retain their existing restrictions; this patch
does not generalize them.

Use `sh scripts/groma.sh` for this map. Groma0.7.2 converts scanners.json to
config.json through its supported configuration migration; retain the exact Java
scanner selection/patterns and explicitly disable automatic updates. No new
scanner or application dependency is introduced.

All Java components execute inside the Android application, including domain,
application, Room adapters and TSV adapters. Storage containers do not own their
Java adapter code. The shared database owns SQL/RPC/RLS behavior that the Java
scanner cannot read; Auth/PostgREST hosting remains an external Supabase system.
Readable container descriptions and authored relationships distinguish those
responsibilities without fabricating source ownership.

## Validation and consequences

Two regressions failed before the patch and passed afterward. The full upstream
check passed: Node24/24; Bun903 passed,62 optional runtime skips,0 failures;
typecheck and lint passed with one existing warning. Node22 used its supported
experimental type-stripping flag. Swift tests used the matching published
scanner0.2.2 package through the existing GROMA_TEST_SWIFT_PACKAGE hook because
the local SDK lacks SwiftParser. No Swift package is enabled for Wacken.

In a disposable Wacken clone, all111 components moved with exact semantic IDs,
prose, metadata, groups, source references, authored connections and flows
preserved. Three existing containers were explicitly declared. Two scans created
nothing and preserved the semantic snapshot. The patched binary was reproduced
from a second clean upstream checkout before use on the release map.

The project owns maintenance of this small patch until an upstream release
provides equivalent behavior. Scanner limitations and duplicate candidates
remain visible. No deployed Android, database schema, runtime dependency,
business rule or CI test-strategy change is authorized by this decision.

## Alternatives

Waiting for upstream leaves the milestone blocked. Another architecture tool
would require a larger migration. A local pinned patch is the smallest approved
change that preserves the existing map and enables the required curation.

## Links

- TASK-227
- [Tool limitations and approved proposal](../docs/groma-container-limitations-2026-10-20.md)
- [Upstream issue170](https://github.com/MrLesk/groma.md/issues/170)

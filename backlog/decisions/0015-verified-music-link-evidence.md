# ADR 0015: Verified music-link evidence within reviewed metadata
## Status
Accepted — the user explicitly approved the standard TASK-202 proposal on 2026-10-17.

## Context
ADR-0012 candidates carry names, URLs and ranking scores. Those facts cannot establish identity or distinguish namesakes. TASK-202 adds music-only single-band and selected-festival discovery with explicit approval of verified missing fields.

## Decision
Keep the existing reviewed-metadata architecture and missing-field synced Band repository. Add typed source evidence and application-owned verification for the music-only flow. Existing general enrichment remains separate. Reuse existing MusicBrainz/Wikidata HTTP integration and optional Spotify/YouTube configuration; adapters collect facts, the application evaluates them.

Verify the artist identity using an authoritative artist identifier corroborated by a second source, a suitable musical-group identity, matching canonical name/alias, and profile links actually retrieved from an official artist website declared by both sources. Multiple matching artist identities remain ambiguous until explicitly refined to a specific identifier. Rank/name/badge alone is never enough. Invalid or nonunique profiles remain empty.

Official-site retrieval uses bounded HTTPS, public address checks, restricted redirects, request/body limits and cancellation. Review evidence is transient and must be fetched again after process loss; no evidence audit schema, hosted service or new secrets are introduced. Saving is separate, rechecks verification and current missing fields, and uses the existing remote-first repository so a failed remote save does not falsely succeed locally.

## Consequences
- Stronger evidence can mean no verified result for poorly documented bands; leave unknown links empty.
- Existing providers remain useful and existing general metadata behavior is protected by characterization tests.
- A scoped application use case owns ambiguity, allowed fields, progress and retry; Android owns presentation and lifecycle only.
- Network adapters and identity policy remain independently testable without live providers.
- Persisted evidence/audit or a hosted lookup service would require a future decision.

## Alternatives considered
1. Expose existing ranked results and ask users to check everything: rejected; no enforceable verification gate.
2. Evidence extension in existing boundaries: accepted.
3. Hosted discovery, credential service and durable evidence jobs: deferred as unnecessary scope.

## Links
- TASK-202; [reviewed proposal](../docs/task-202-verified-links-architecture-proposal.md).
- [ADR 0012](0012-reviewed-band-metadata-search-framework.md).
- API research accessed 2026-10-17: [MusicBrainz API](https://musicbrainz.org/doc/MusicBrainz_API), [rate limits](https://musicbrainz.org/doc/MusicBrainz_API/Rate_Limiting), [Wikidata music identifiers](https://www.wikidata.org/wiki/Wikidata:WikiProject_Music/Identifiers), [Spotify artist reference](https://developer.spotify.com/documentation/web-api/reference/get-an-artist).


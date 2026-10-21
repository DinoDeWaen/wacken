# Music discovery island review — 21 October 2026

TASK-230 resolves the six remaining disconnected music records. Every record has runtime callers: Settings and band detail open MusicLinksReviewActivity, which composes FindBandMusicLinksUseCase with VerifiedMusicLinksProvider; the provider uses existing source HTTP clients and OfficialMusicPageClient. No production source is unused within this reviewed scope.

## Responsibility disposition

| Surviving ID | Responsibility |
| --- | --- |
| `musiclinksreviewactivity` | Verified music link review screen: Reviews missing links before explicit save. |
| `findbandmusiclinksusecase` | Verify and approve missing music links: Owns identity verification and approval. |
| `musiclinklookupprovider` | Music identity evidence contract: Returns facts for application verification. |
| `verifiedmusiclinksprovider` | Music identity evidence collector: Collects corroborating public source facts. |
| `officialmusicpageclient` | Bounded official artist page reader: Retrieves public HTTPS evidence. |

MusicLinkEvidence and its Page value are now owned by musiclinklookupprovider: they are retrieved facts in the lookup protocol, not an independent runtime component. The five surviving responsibilities are grouped as Verified music discovery. All294 source files remain uniquely owned; no application code, trust policy, provider or dependency changed.

## Evidence and trust boundary

The use case requires matching MusicBrainz/Wikidata artist identifiers, Group identity/name or aliases, a website declared by both sources, and a unique linked canonical platform profile. It re-evaluates raw evidence and current missing fields on approval. General metadata ranking and optional Spotify/YouTube fallback do not verify identity. Ambiguity, missing evidence or conflicting known profiles prevent save.

VerifiedMusicLinksProvider collects facts using existing MusicBrainz/Wikidata nested HTTP clients and a composed PageLoader callback. OfficialMusicPageClient reads public HTTPS HTML only, checks every redirect destination, limits payload to512KiB and redirects to3, applies5-second connect/read timeouts and checks interruption. No cookies, credentials, scripts or automatic redirect handling are involved. The declared Official artist websites external system records this actual public interaction.

MusicLinksReviewActivity retains completed reviews across rotation and cancellation/retry. Edited terms clear old selections and never rename the band. A selected verified identity and explicit acknowledgement precede save. Search need not require configured optional credentials; unavailable providers and HTTP429/source errors remain visible/retryable. Cancelling lookup preserves existing saved data; saving cannot be cancelled from its search control. Public lookup is not claimed to auto-cancel on account changes. The captured AppRepositories writer binds authenticated shared writes to the expected user; account mismatch fails, and cache publication follows source success.

## Reviewed source-backed paths

- **Review festival missing music links:** Festival admin opens Settings for the active festival. Search selects only lineup bands with missing music fields and never renames them from edited search terms. MusicBrainz/Wikidata corroboration and jointly declared public official-site anchors are facts; the application requires Group identity/name, matching artist ID and a unique canonical platform profile. Optional Spotify/YouTube fallback is explicitly unverified. The user checks source evidence, selects a verified identity and explicitly saves missing links; existing fields and ratings stay unchanged. Cancellation/errors retain saved data and completed reviews for retry. Source-first account-bound save must succeed before cache publication.
- **Review one band music evidence:** Band detail opens the same reviewed workflow scoped to one canonical band name. Search terms refine identity only. Unverified or ambiguous results cannot be saved; public lookup may continue across rotation, while cancellation stops unfinished work. The retained repository uses its expected account for shared writes, so account mismatch or save failure must remain unconfirmed. Approval re-evaluates raw evidence/current links and delegates missing-field writes; search ranking and user acknowledgement alone are not verification.

SettingsActivity.java:144 and BandDetailActivity.java:294 establish both entry points. MusicLinksReviewActivity constructs the use case/provider and calls search/approve. FindBandMusicLinksUseCase.verify/approve own verification, then ApplyBandMetadataProposalsUseCase.apply fills missing fields through BandRepository. AppRepositories.java:145–149 binds the remote source to the captured user, and SyncedBandRepository.save writes source before cache. No ratings/history/lineup write port is used by this music workflow. The source-first adapter is described as existing behavior, without inventing an atomic remote/cache transaction.

## Verification and limitations

Current model contains95 components,294 unique existing source files and13 resolvable flows. Component degree check reports zero disconnected components, down from47 before TASK229/230. Two scans preserve the curated ownership/groups/flows and create0/refreshed95/findings88. Degree is only a completeness check; each authored interaction was separately reviewed from source/composition. Java-only scanner limitations and TASK228 stale authored arrows to the deleted importer remain documented. No live network/provider or database authorization validation is claimed by these documentation tasks; existing regression tests and TASK241 retain runtime validation.


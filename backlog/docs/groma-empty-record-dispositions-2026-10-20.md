# Empty Groma record dispositions

TASK-231, 20 October 2026. Compared with baseline commit `4630970` and its
126-file SHA-256 inventory.

| Record | Evidence | Disposition |
| --- | --- | --- |
| `pendingplanningratingrecovery` | Empty body; no description, group, Code ownership, relationship or flow reference; no corresponding source in available Git history | Removed with `groma remove pendingplanningratingrecovery` |
| `androidjavacompatibility` | Empty body; no description, group, Code ownership, relationship or flow reference; no corresponding source in available Git history | Removed with `groma remove androidjavacompatibility` |

The historical origin remains unknown. No useful authored content existed to
rehome and no task architecture reference pointed to either ID. Historical audit
text and TASK-226 modified-file paths describe the earlier baseline and remain
valid historical evidence; they are not live element references.

`RecoverPendingPlanningRatingUseCase.java` retains its independent source-owning
`recoverpendingplanningratingusecase` record. The obsolete recovery placeholder
was not merged or renamed into it. Supported Android compatibility remains
documented in the README and implemented by the existing Gradle desugaring
dependency; deleting an empty map record changes neither implementation.

## Verification

- Two consecutive Groma 0.6.6 / Java scanner 0.2.2 scans each reported
  `created 0, refreshed 109, matched 0, findings 99`; neither ID was recreated.
- Compared all remaining files with the baseline SHA-256 manifest: exactly the
  two specified empty files are absent; all **124 survivors are byte-for-byte
  unchanged**. Thus every surviving ID, description, group, relationship, flow
  and source ownership declaration is preserved.
- Independently checked unique/existing Code ownership, every relative Markdown
  link in the map and bare-ID architecture references in active Backlog tasks.
  Historical path/URL/folded prose references are not treated as Groma IDs.
- Groma refused no removal: both records were unused and source-free. No
  application files, scanner configuration or schema changed.

The historical 126-file manifest must continue to be checked at baseline commit
`4630970`, as its README instructions state. It intentionally reports the two
missing records in the cleaned checkout; do not regenerate historical evidence
to hide deliberate removals. Full semantic CI enforcement belongs to TASK-233.

README impact: none, because cleanup does not change runtime behaviour and the
existing historical-baseline instructions already scope verification to its commit.

Business requirements impact: none, because application capabilities and business
rules are unchanged.

Diagram impact: updated Groma to remove two obsolete source-free records; all
surviving map content is preserved.

ADR impact: none, because no architecture-significant decision was introduced.

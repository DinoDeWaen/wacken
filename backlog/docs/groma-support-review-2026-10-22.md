# TASK-211 lifecycle and support presentation review

Existing screen/dialog/theme responsibilities retain96 components,299 uniquely
owned Java sources and13 flows. Twelve CLI overviews explain actual retained
sign-in state, scrollable forms, read-only archived rows, explicit archive scope,
rename drafts and safe interrupted-request/correction recovery. Eight actual
style collaborations and ArchivedFestivalActivity→RatingValueText were authored;
no existing relationship, containment or flow was removed. The rendered
`/private/tmp/wacken-211-map` theme/source/caller details were inspected in Chromium.
Actual source confirms each styling call, and the existing application/gateway
boundaries still own authentication, identity, lifecycle and rating policy.

The theme controller weakly owns visible dialogs only to dismiss windows on
Activity destruction and report open-dialog recovery context. Restyling updates
native text/panels/buttons without invoking or replacing business listeners.
Sign-in uses the same acknowledged client/store and Main destination, with one
retained in-memory request and password draft; no password enters Bundle state.
Archive confirmation adds explicit festival/group scope before the unchanged
atomic workflow. Existing local CSV/correction review semantics are preserved;
restoring uncertain requests never resubmits a write.

Removed obsolete archived table cell/header/name/icon/row geometry code is no
longer used by the wrapping read-only list. No source file or component was
removed, and no runtime/data boundary was added. Native98 tests passed before
final archive-return/recovery refinements; final41 affected native cases pass
after them, including all six new support cases. Clean compilation independently
caught and corrected an extracted local member-identity reference before closure.

## Exact duplicate review

There are83 groups:79 exact prior identities/dispositions are unchanged. Three
prior groups become four reviewed groups, with exact members recorded:

| Identity | Prior identity | Evidence and disposition |
| --- | --- | --- |
| `5f0de89c71e1` | `90ae4f4a8dc3`,`0f609ee281e3` | The previous four/six label-builder groups coalesce after CSV labels adopt16sp, with archived text and lineupText also matching. Twelve small role-specific detail/metadata/CSV/lineup/music/schedule/Settings builders retain their text/layout meanings; common semantic text/heading/panel styling is already shared. Keep explicit screen assemblies rather than a flag-heavy universal builder. |
| `cff67dd19a31` | `b4874b5924a0` | SignInState adds a fourth tiny notifyUi callback to metadata/CSV/music retained owners. Each invokes its own detachable observer; retain owner-local lifecycle notification instead of a shared event bus. |
| `bbf671e002cb` | new exact pair | Archived/music column() methods each construct one vertical LinearLayout. This trivial widget setup does not justify coupling separate workflows to a generic layout factory; retain the two tiny owner-local builders. |
| `ef8ae0b890aa` | new exact pair | Metadata/Login save only scrollY then call Activity super. Keep lifecycle ownership local; a shared base Activity would obscure the distinct memory-only credential and retained metadata behavior. |

This is individual source/token review, not a count allowance. Pins and the two
existing TASK228 stale-row exceptions compare exactly equal; no new isolated
component or stale relationship exception was accepted.

## Scanner confidence

Override warnings252→259 correspond to archive save/resume, archived-detail save,
login save/destroy and two native adapter view overrides. A new exact diagnostic,
`compiler.err.non-static.cant.be.ref` count2, points to super.getView and
super.getDropDownView in WackenTheme's anonymous ArrayAdapter inside static
reviewOptions. Android ArrayAdapter is absent from the scanner classpath, so its
superclass resolution is incomplete. Actual Android javac compiles these methods
and native identity/correction selector tests execute them successfully; this is
an explicitly investigated scanner limitation, not an ignored compiler failure.
All other existing warning categories/counts remain unchanged.

Deferred source references227→228 add Dialog.isShowing. External-type facts
4408→4458 and unresolved calls9428→9609 grow with native dialog/adapter/lifecycle
APIs. The separate extraction error was fixed to a captured member-id field before
acceptance; final diagnostics are from the corrected clean source. Coverage grows
2426→2456 operations,1501→1515 tokenized bodies and12326→12568 invocations.
Two repeatable pinned clean scans remain mandatory for the normal gate.

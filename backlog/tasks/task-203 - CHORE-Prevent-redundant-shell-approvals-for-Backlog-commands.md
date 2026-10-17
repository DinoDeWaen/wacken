---
id: TASK-203
title: 'CHORE: Prevent redundant shell approvals for Backlog commands'
status: Done
assignee:
  - '@codex'
created_date: '2026-10-17 08:13'
updated_date: '2026-10-17 08:15'
labels: []
dependencies: []
modified_files:
  - backlog/docs/agent-execution-guardrails.md
priority: medium
type: chore
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Correct the agent invocation that turned approved Backlog CLI commands into opaque shell approval requests. Preserve shared task locks, Git integration and sandbox protections; document safe quoting and validate against the requested 2.34 stories.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 A direct approved Backlog create/edit invocation works with multiline text and apostrophes without a new generic shell approval.
- [x] #2 Both requested tasks exist once, retain their full criteria and belong to release 2.34.
- [x] #3 The canonical execution guardrails document the cause and safe invocation; no lock disabling, permission broadening or Git configuration change is introduced.
- [x] #4 Validation evidence and documentation/architecture impact are recorded.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Completed minimal invocation/documentation repair. Confirmed legitimate shared Backlog locking and existing prefix approvals. Reproduced that login:false alone did not fix apostrophe quote-splicing; safely double-quoted direct arguments did. Completed TASK-201/TASK-202 in release 2.34 and verified exact multiline/apostrophe round-trip. Updated the execution guide, checked formatting, and preserved existing user config/permissions/locks. No product, architecture, dependency or source-code changes; no application build required.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Corrected the agent command construction that caused redundant generic shell approval requests during Backlog task creation. Direct non-login Backlog calls with safely double-quoted text reuse the existing narrowly scoped create/edit approval. Changing the login setting alone was insufficient: apostrophe quote-splicing still caused the full shell command to be treated as opaque.

Backlog's shared creation lock is legitimate. Installed v1.53.0 places it in .git/backlog.md/locks/create so worktrees share task numbering. The initial sandbox attempt failed with EPERM there. No ownership repair, lock removal, lock disabling, Git mode change or broad permission change was needed.

## Acceptance criteria validation

- AC1: direct create made TASK-202 and TASK-203; a full multiline TASK-202 description with apostrophes was edited through the approved direct CLI in 0.118 seconds with no new generic shell approval. A read-back comparison confirmed exact preservation, real newlines and apostrophes.
- AC2: TASK-201 (UX audit, five styles, clickable mockups and validation) and TASK-202 (verified music-link discovery from festival Settings/Admin and band detail) each exist once with nine acceptance criteria, status To Do and milestone m-0. Milestone release 2.34 now shows seven pending tasks.
- AC3: agent-execution-guardrails.md documents direct invocation, safe quoting, existing-prefix escalation for protected lock writes, and waiting for mutation completion. It preserves shared locks and sandbox/Git protections.
- AC4: this validation package records the cause, correction and limits.

## How to test

### Automated tests

Actual CLI round trips served as regression checks: task creation, task edit with the original multiline/apostrophe description, exact read-back comparison, task reads, milestone list and git diff --check all passed. No application code changed; application tests and an APK rebuild would not validate this invocation-only documentation correction.

### Manual validation

Both requested stories were read back through Backlog CLI and checked for full criteria and milestone assignment. Existing Backlog config remains remoteOperations=true, checkActiveBranches=true, filesystemOnly=false and autoCommit=false. Existing user edits to config, task archive/milestone files and other files remain untouched.

## TDD / BDD / approval-test evidence

Before correction: EPERM at the shared task lock, followed by an opaque zsh -lc approval request for a long apostrophe-escaped command. A no-login switch alone still prompted for the full zsh -c string.
After correction: the same full story description round-tripped with direct safely quoted arguments under the existing Backlog prefix approval. CLI source inspection confirmed the lock's purpose and location; installed v1.53.0 was not upgraded or patched.

## Architecture impact

- Architecture-significant change: none; agent invocation and documentation only.
- Approval received: user requested root-cause resolution.
- ADR impact: none, because no product boundary, dependency, integration or permission architecture changed.

## README impact

README impact: none, because app setup/public commands are unchanged; the canonical agent execution guide contains the correction.

## Business requirements impact

Business requirements impact: none, because this correction changes agent execution only. TASK-201/TASK-202 capture the requested future product work.

## Diagram impact

Diagram impact: none, because no application flow or runtime boundary changed.

## Commits / logical change list

One documentation change plus CLI-created TASK-201/TASK-202/TASK-203 records. Only those new records and the execution guide are staged.

## Risks and follow-up

Legitimate new command permissions may still require approval. This fixes unnecessary generic shell prompts for the existing approved Backlog commands, not all permission prompts. Do not disable shared locks or broaden sandbox permissions. Mutations must complete before another mutation begins; a returned running session is not success.

## Modified files

backlog/docs/agent-execution-guardrails.md
<!-- SECTION:NOTES:END -->

## Final Summary

<!-- SECTION:FINAL_SUMMARY:BEGIN -->
Fixed unnecessary shell approval fallback through safe direct Backlog command construction; existing lock and sandbox protection remain enabled. Verified multiline/apostrophe round-trip. TASK-201 and TASK-202 are complete To Do stories in release 2.34. Guidance saved in agent-execution-guardrails.md.
<!-- SECTION:FINAL_SUMMARY:END -->

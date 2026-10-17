---
id: TASK-201
title: 'SPIKE: Audit app UX and validate five visual styles with mockups'
status: Done
assignee:
  - '@codex'
created_date: '2026-10-17 07:50'
updated_date: '2026-10-17 09:23'
labels:
  - ux
  - research
  - design
  - accessibility
milestone: m-0
dependencies: []
modified_files:
  - design/release-2.34/verify.cjs
  - design/release-2.34/index.html
  - design/release-2.34/styles.css
  - design/release-2.34/prototype.js
  - design/release-2.34/compare.html
  - design/release-2.34/review-guide.md
  - design/release-2.34/contrast.cjs
  - design/release-2.34/research.md
  - design/release-2.34/contrast-results.json
  - design/release-2.34/README.md
  - README.md
  - design/release-2.34/appearance.test.cjs
  - design/release-2.34/appearance.js
  - design/release-2.34/themes.html
  - design/release-2.34/validation-review.md
priority: medium
type: spike
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## Business value
Choose understandable, accessible festival-app interactions and a visual direction before investing in a redesign.

## Research question
How can Wacken Planner improve its everyday and festival-field user experience, and which of five distinct visual styles best supports those improvements?

## Scope and deliverables
Audit the current implemented app, including band overview/detail and ratings, schedule, festival lifecycle, Settings/Admin and band linking/music-link discovery, sync/offline/pending states, errors and recovery. Build on the earlier TASK-125 UX review and TASK-130 metal design system; identify what still applies and what current evidence challenges.

Research current authoritative UX/accessibility guidance, including Android/Material platform guidance and WCAG, and cite primary sources and access dates for recommendations. Separate observed problems, research-supported recommendations and assumptions needing user validation.

Propose a prioritized set of experience improvements and five meaningfully distinct, named visual styles. The styles must vary beyond palette (typography, information hierarchy/density, component treatment and interaction presentation), remain usable for a music-festival app and preserve business rules. For fair comparison, mock the same representative screens and journeys in all five styles using realistic band/festival data.

Deliver viewable mockups and clickable prototypes, an accessibility/usability comparison, a user-review exercise with recorded results or explicit unvalidated assumptions, and a recommendation with trade-offs. No production redesign or automatic style selection in this spike; implementation is proposed as follow-up work after review.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 An evidence-based audit covers the current core journeys and loading/empty/error/offline/pending states, with observed issues, affected users, severity and reproducible screen references; prior UX/design-system work is assessed rather than discarded.
- [x] #2 Recommendations cite current authoritative primary sources with links and access dates, explain their application to this app, and distinguish observed evidence from assumptions; the output includes prioritized improvements with expected benefit, effort/risk and validation criteria.
- [x] #3 Exactly five distinct named styles are proposed, each with rationale, typography, palette, spacing, component examples, icon/navigation treatment and accessibility considerations; they differ in hierarchy and presentation rather than only colors.
- [x] #4 Each style has a comparable mockup set for band overview, band detail/rating, schedule and Settings/Admin including music-link discovery/review, plus representative feedback states; include long band names, absent music metadata and offline/pending data.
- [x] #5 Clickable prototypes for all five styles let a reviewer compare the same key journeys, including finding/rating a band and starting/reviewing music-link discovery; mockups are saved as shareable review artifacts with clear navigation and labels.
- [x] #6 Every style is checked against cited platform/accessibility guidance for contrast, readable text/font scaling, touch targets, focus/screen-reader labels, color-independent meaning and error feedback; record measurable findings and unresolved issues.
- [x] #7 A consistent scenario-based validation exercise compares task completion, navigation clarity, mistakes/recovery, readability and user preference across all styles. Record actual reviewer feedback and limitations; do not present heuristic review or mock data as completed user testing.
- [x] #8 The final comparison recommends a style or shortlist with trade-offs, prioritizes UX changes, identifies remaining validation questions and proposes small testable follow-up stories for human review before production implementation.
- [x] #9 README impact, Business requirements impact, Diagram impact and ADR impact use canonical delivery-governance wording; approved scope/design changes are reflected in the appropriate source of truth. This spike does not itself authorize architecture or production UI changes.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Standard design/research spike for release 2.34. 1. Audit the latest published V2.33.5 UI source and prior TASK-125/TASK-130 findings, recording code-backed observations and gaps requiring device/user evidence. 2. Research current primary Android/Material, WCAG and usability guidance with citations/access dates. 3. Build five distinct standalone prototype styles under design/release-2.34, sharing realistic sample content and comparable overview, detail, schedule, settings and music-link review journeys plus feedback states. 4. Validate real interactions, contrast, targets, keyboard access, 320px layout and 200% text using bundled browser tooling; save screenshots/contact sheets and a reproducible review exercise. 5. Document priorities, comparative trade-offs, recommended shortlist and follow-up story proposals; keep actual user feedback pending until the user evaluates the prototypes. No production UI/domain/schema/dependency change or final style choice; no architecture approval/ADR required for the study. Current design-system/business rules stay implemented source of truth; research variants and any proposed exceptions are explicitly labeled. Update README with study/review entry point and task validation evidence.

Initial HTML review increment completed. Syntax and palette checks pass; in-app browser smoke review performed. Deviation: full automated browser run did not complete; user accepted directly reviewing HTML. Actual human feedback and remaining layout/native accessibility validation are pending; keep task In Progress.

User review direction: combine Ironline dark and Field Notes light with shared geometry, hierarchy and navigation. Add dedicated HTML flow review with an instant light/dark toggle and System/Dark/Light preference in Settings; preserve in-progress demo state, selected band, search and scroll across switches. Test preference resolution and manually validate rating/music review continuity. This authorizes prototype iteration only, not Android implementation.

Finalized five-style accessibility assessment with measured contrast and explicit unresolved device/browser findings; recorded the actual user validation of the paired flow without fabricated task metrics. Handed native implementation and remaining device validation to release 3.0 TASK-204–213. No production changes or APK replacement for this static study; signed builds remain part of implementation/release gates.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary
Five interactive HTML directions prepared for release 2.34: Ironline, Field Notes, Pulse, Rivet and Backstage. Shared overview/detail/rating, schedule, Settings/Admin and simulated music-link review plus offline/pending/error/loading/empty fixtures. Added current-source audit, primary guidance, recommendation and review exercise. User accepted HTML review format.

## Acceptance criteria validation
AC 1-5, 8, 9: deliverables in design/release-2.34.
AC 6 partial: palette passes; full layout/native accessibility checks pending.
AC 7 pending: actual human reviewer count zero. Task stays In Progress.
Baseline: published v2.33.5 commit 1928e82f64fe9724a69e7f33cc72c8fb2b9cb2fa from existing release worktree; reassessed TASK-125/130.

## How to test
### Automated tests
node --check design/release-2.34/prototype.js: PASS.
node design/release-2.34/contrast.cjs: PASS, 80 token pairs: 65 text >=4.5:1 and 15 boundary >=3:1. Raw results in contrast-results.json.
git diff --check: PASS.
verify.cjs supplied but NOT completed: cached browser missing; installed Chrome sandbox launch blocked; escalated request interrupted. No full browser/layout/screenshot pass claimed.

### Manual validation
HTTP 200 at localhost:8234. In-app browser confirmed overview renders, Abbie Falls opens, planning 4 updates to Strong choice, band discovery opens and search renders simulated candidate with Save disabled. Comparison page loaded five frame accessibility trees; first visible narrow-screen panel inspected. This is agent smoke review, not user testing. Native TalkBack, 200% layout, physical targets, glare, one-handed use and full visual review pending.

## TDD / BDD / approval-test evidence
Prototype scenarios encoded in verify.cjs before HTML implementation; missing entry point was an artifact-level initial failure, not a production regression test. Full browser red/green cycle incomplete. Palette assertions pass. Production behavior unchanged; no APK tests for this static study.

## Architecture impact
Architecture-significant change: no; standalone study, no production schema/API/dependency/module changes.
Approval received: user authorized milestone/design work and HTML review format.
ADR impact: none, because no architecture-significant decision or production implementation is made.

## README impact
README impact: updated project entry point with prototype, comparison, review exercise and preview command.

## Business requirements impact
Business requirements impact: none, because these are proposals awaiting review. Light theme and labeled bottom navigation are explicit alternatives to current rules, not approved implementation.

## Diagram impact
Diagram impact: none, because no production architecture or flow changes.

## Commits / logical change list
1. Shared HTML/CSS/JS prototypes and comparison.
2. Browser test script plus passing palette checks and raw results.
3. Source audit, primary research, review exercise and README link.

## Risks and follow-up
Sample evidence does not verify real channels. TASK-202 remains separate implementation. Heuristic shortlist Ironline/Pulse; no preference winner. Complete layout/accessibility validation and record user review before closing TASK-201 or implementing production redesign. Existing user changes preserved.

## Paired appearance iteration — 2026-10-17
User feedback: combine Ironline dark and Field Notes light as a light/dark choice; requested HTML flow and toggle review. Added themes.html with shared layout/hierarchy, header toggle and Settings System/Dark/Light radios. The preference follows real prefers-color-scheme in System mode and is remembered in local browser storage when available. Theme changes update appearance without replacing forms, preserving in-progress input, selected band, ratings, expanded sections and scroll.

Validation: appearance.test.cjs first failed before module creation, then all 4 cases passed (system in both modes, manual overrides, invalid stored preference, device changes). JavaScript syntax and 80 contrast-token assertions pass. In-app browser confirmed planning 4 survives dark-to-light, Settings System follows current dark system appearance, unfinished festival search Abbie survives switching, and navigating to themes.html without URL overrides restores saved Light preference. Remaining full layout/native accessibility and actual user task-completion review are pending. HTML is available on localhost:8234/themes.html; no Android behavior was implemented.

README impact: updated root and study entry points with paired flow, toggle behavior and review steps.
Business requirements impact: none, because the user authorized prototype iteration for review; production requirements remain unchanged until implementation is approved.
Diagram impact: none, because no production architecture or flow changes.
ADR impact: none, because static design iteration makes no architecture-significant change.
Task remains In Progress awaiting flow review; original five-style artifacts retained as research references.

## Final acceptance and handover — 2026-10-17
User validated the paired HTML flow and requested release 3.0 implementation planning. Actual feedback and limits are recorded in design/release-2.34/validation-review.md. AC6 is an assessment with unresolved findings, not a native accessibility pass; AC7 records qualitative user acceptance without invented timings/completion rates. All spike deliverables complete, native implementation and validation tracked in TASK-204–213 (m-2). Existing test and canonical impact evidence above remains applicable. No APK was rebuilt for this static HTML research-only task; future production release gates remain required.
<!-- SECTION:NOTES:END -->

---
id: TASK-201
title: 'SPIKE: Audit app UX and validate five visual styles with mockups'
status: To Do
assignee: []
created_date: '2026-10-17 07:50'
labels:
  - ux
  - research
  - design
  - accessibility
milestone: m-0
dependencies: []
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
- [ ] #1 An evidence-based audit covers the current core journeys and loading/empty/error/offline/pending states, with observed issues, affected users, severity and reproducible screen references; prior UX/design-system work is assessed rather than discarded.
- [ ] #2 Recommendations cite current authoritative primary sources with links and access dates, explain their application to this app, and distinguish observed evidence from assumptions; the output includes prioritized improvements with expected benefit, effort/risk and validation criteria.
- [ ] #3 Exactly five distinct named styles are proposed, each with rationale, typography, palette, spacing, component examples, icon/navigation treatment and accessibility considerations; they differ in hierarchy and presentation rather than only colors.
- [ ] #4 Each style has a comparable mockup set for band overview, band detail/rating, schedule and Settings/Admin including music-link discovery/review, plus representative feedback states; include long band names, absent music metadata and offline/pending data.
- [ ] #5 Clickable prototypes for all five styles let a reviewer compare the same key journeys, including finding/rating a band and starting/reviewing music-link discovery; mockups are saved as shareable review artifacts with clear navigation and labels.
- [ ] #6 Every style is checked against cited platform/accessibility guidance for contrast, readable text/font scaling, touch targets, focus/screen-reader labels, color-independent meaning and error feedback; record measurable findings and unresolved issues.
- [ ] #7 A consistent scenario-based validation exercise compares task completion, navigation clarity, mistakes/recovery, readability and user preference across all styles. Record actual reviewer feedback and limitations; do not present heuristic review or mock data as completed user testing.
- [ ] #8 The final comparison recommends a style or shortlist with trade-offs, prioritizes UX changes, identifies remaining validation questions and proposes small testable follow-up stories for human review before production implementation.
- [ ] #9 README impact, Business requirements impact, Diagram impact and ADR impact use canonical delivery-governance wording; approved scope/design changes are reflected in the appropriate source of truth. This spike does not itself authorize architecture or production UI changes.
<!-- AC:END -->

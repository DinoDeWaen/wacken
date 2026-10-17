---
id: task-170
title: 'SPIKE: Investigate Wacken website scraping for future imports'
status: To Do
assignee: []
created_date: '2026-08-19 11:02'
labels:
  - future
  - spike
  - import
  - research
dependencies: []
priority: low
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Business value: The team can decide whether website scraping is worth adding or whether validated CSV remains the safer import path.

As a maintainer, I want a short technical and legal feasibility assessment for Wacken lineup scraping so that future import work starts from facts.

Scope: source fields, stability, terms/legal constraints, sample data shape, and a recommendation.

Out of scope: production scraping, background jobs, and Android data-grid implementation.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given the Wacken website is evaluated, then the available lineup fields and missing fields are documented.
- [ ] #2 Given legal, reliability, or rate-limit risks are identified, then the recommendation explains whether scraping should proceed or stay out of scope.
- [ ] #3 Given a sample parser or fixture is feasible within the spike, then it is captured as non-production evidence only.
- [ ] #4 The spike ends with a clear recommendation and follow-up stories only when justified.
- [ ] #5 README and business requirements impact are recorded using delivery-governance wording when implemented.
<!-- AC:END -->

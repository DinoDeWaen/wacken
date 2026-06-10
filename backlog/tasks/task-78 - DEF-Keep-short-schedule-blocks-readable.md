---
id: task-78
title: 'DEF: Keep short schedule blocks readable'
status: To Do
assignee: []
created_date: '2026-06-10 17:04'
labels:
  - defect
  - ui
  - schedule
  - mvp2
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Short schedule overview blocks can clip or hide content because the block tries to show band, stars, stage, time, GO/OPTIONAL status, lost alternative, and walking time inside a small height. Longer events look acceptable, but short events lose important information.

As a festival attendee, I want short schedule blocks to remain readable so that every act can be inspected without clipped text or missing elements.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given a short performance block is shown in the group schedule, when the available block height cannot fit all schedule details, then the block still shows the band name, rating stars, stage, and walking information without clipped text.
- [ ] #2 Given the schedule overview is shown, when a performance block is rendered, then GO or OPTIONAL is not shown inside the block.
- [ ] #3 Given a performance block has a start and end time, when the calendar is rendered, then the time range is shown on the time scale next to the block rather than inside the block.
- [ ] #4 Given a performance block is longer, when it renders with extra vertical space, then the layout remains consistent with short blocks and does not reintroduce unnecessary GO/OPTIONAL text.
- [ ] #5 Given a block has a lost alternative, when there is not enough space in the overview block, then the overview keeps the block readable and the full alternative detail remains available by tapping the block.
- [ ] #6 Automated tests or focused compile checks protect the layout behavior, and installed-device visual validation is documented.
- [ ] #7 Business requirements and README impact are recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->

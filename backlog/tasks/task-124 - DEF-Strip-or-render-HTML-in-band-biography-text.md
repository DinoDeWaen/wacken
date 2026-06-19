---
id: task-124
title: 'DEF: Strip or render HTML in band biography text'
status: To Do
assignee: []
created_date: '2026-06-19 05:57'
labels:
  - defect
  - ui
  - content
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Imported band biography text can still show raw HTML tags and entities such as br tags and non-breaking-space entities on the band detail screen. The app should render or sanitize this content so users see readable plain text.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given a biography contains HTML tags, when the band detail screen renders it, then raw tags are not visible.
- [ ] #2 Given a biography contains HTML entities, when the band detail screen renders it, then entities are decoded or converted to normal readable spacing.
- [ ] #3 A regression test or focused UI/formatting validation covers the formatter behavior.
<!-- AC:END -->

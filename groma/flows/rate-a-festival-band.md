---
type: Groma Flow
title: Rate a festival band
groma:
  id: rate-a-festival-band
---

An attendee changes a planning rating from the active lineup; the use case saves it through the configured repository.

## Steps

| From | To | Action |
| --- | --- | --- |
| [From](../actors/festival-attendee.md) | [To](../systems/wacken/components/mainactivity.md) | Choose a band rating |
| [From](../systems/wacken/components/mainactivity.md) | [To](../systems/wacken/components/ratebandusecase.md) | Save the planning rating |

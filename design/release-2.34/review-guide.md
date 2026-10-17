# Release 2.34 — design review
Date: 2026-10-17 · TASK-201 · Review status: awaiting user feedback.

## Open the HTML
Open [index.html](index.html) in a browser or use the local preview at http://127.0.0.1:8234/index.html while the server is running. [Compare all five](compare.html) places the same band list in five interactive frames. Each frame has working navigation.

The prototype uses sample data and simulated evidence. It does not call a music provider, upload CSV data, update the app, or verify any actual channel. Refreshing resets the example. The review notes download is local; share it in chat yourself.

## Fair comparison
First skim all five directions. Then do these tasks in each. Rotate the starting style between reviewers to reduce order bias. Use the same device and text size. Allow breaks; five full runs can be tiring. If time is limited, record a first impression of all five, then complete all tasks in two shortlisted styles and label the others untested.

1. Find Abbie Falls. Set the planning rating to 4, then clear it. Explain the difference between planning and the post-show rating.
2. Find music links from the band. Search, inspect the sample identity evidence, return, acknowledge the evidence and approve. Explain when changes become saved.
3. Open Settings. Start festival music-link discovery beside Link bands. Reject the candidates. Check that existing links were not changed.
4. Open Schedule. Explain the locked choice using the legend; inspect its alternative, then keep the current choice.
5. Select Offline in the study controls. Rate a band. Explain where the change is saved and what remains pending.
6. Select Sync error, then Retry. Select No active festival, inspect archived festivals and the Add festival entry point. No real festival will be created.
7. Try 200% text and a narrow window. Read the longest band name, the rating meaning and the review evidence. Try keyboard Tab, radio arrow keys and Space.

Also change Lookup result fixture to Ambiguous and No verified match. Neither should offer an approval path that creates guessed links.

## Record observations
Do not infer completion from preference. Record mistakes and help needed, including failures. Optional time measurements are useful only if the same tasks and measurement method are used.

| Style | Tasks completed / attempted | Help or wrong turns | Saved vs pending understood? | Readability / 200% | Ease 1–5 | Preference reason |
|---|---|---|---|---|---|---|
| Ironline | Pending | | | | | |
| Field Notes | Pending | | | | | |
| Pulse | Pending | | | | | |
| Rivet | Pending | | | | | |
| Backstage | Pending | | | | | |

Record device, browser, viewport, text size, date, reviewer and previous app familiarity. Ask: Which action was hardest to find? Which information can be removed? Which style would you use during a festival and why?

## Results and limits
Actual reviewer sample: **0**. No task completion rates, preference winner or time improvement have been established. The initial recommendation is a heuristic shortlist, not a user study result.

Browser labels and a simulated phone do not validate native Android TalkBack, Switch Access, real font scaling, sunlight/glare, gloves, battery or one-handed reach. These require a later device review after a direction is selected. Do not infer outdoor readability from palette alone.

## Decision after review
Share a preferred direction, a second choice, and anything to combine or change. Confirm any light-theme or navigation departure separately before production implementation. TASK-201 stays In Progress until feedback and resulting changes are recorded; TASK-202 is the separate implementation story for actual verified channel discovery.


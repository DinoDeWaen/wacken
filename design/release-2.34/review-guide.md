# Release 2.34 — design review
Date: 2026-10-17 · TASK-201 · Review status: awaiting user feedback.

## Paired light/dark flow review

The user requested Ironline dark and Field Notes light as one shared flow. [Open themes.html](themes.html) for the current iteration. This records a design preference; measured usability results remain pending.

1. Open Abbie Falls, set planning to 4, and toggle Light in the app header. Confirm the band, rating and scroll position stay.
2. Toggle Dark again. Open Find music links, type a search, and switch theme before submitting. Confirm the text stays. Inspect sample evidence and reject or approve in this demo.
3. In Settings → Appearance try Light, Dark and System default. A header switch selects a manual preference. System follows the actual device appearance; no simulated system toggle is used.
4. Reload to check the appearance preference is remembered. Reload resets sample ratings and other demo data; it does not clear real app data.
5. Review Settings → festival music discovery, Schedule → legend/alternative, and the Offline example in both modes. Try 200% text. Note any clipping, unclear labels or controls that seem to move.

Share any flow problems, preferred default and whether light-mode serif headings remain readable. Original five-style exercise follows for reference.

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

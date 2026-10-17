# Release 2.34 — five interactive design directions
TASK-201 · In Progress · Ready for initial human review, 2026-10-17.

## View and validate
[Open the interactive study](index.html) · [Compare all five](compare.html) · [Review exercise](review-guide.md) · [Evidence-based audit](research.md)

These are standalone HTML/CSS/JavaScript files, with no build or production connection. Open index.html directly in a browser, or serve this directory from the repository root:

```sh
python3 -m http.server 8234 --bind 127.0.0.1 --directory design/release-2.34
```

Then visit http://127.0.0.1:8234/index.html. Keep styles.css and prototype.js beside the HTML. The comparison page stacks on narrow screens and shows columns on wider screens. All five embedded previews are interactive; use the full study for state and text-size controls.

Every direction contains Bands, Band detail/rating, Schedule, Settings/Admin and music-link review. Offline/pending, sync error/retry, loading, empty festival, unknown links, ambiguous identity and no-match examples are selectable. Download review notes locally and share them in chat. A refresh resets the demo.

## The five directions
| Direction | Rationale and hierarchy | Type / palette | Spacing, components, icons and navigation | Trade-off / accessibility consideration |
|---|---|---|---|---|
| **Ironline** | Evolve the current metal identity; flat alphabetical rows and planning-first detail. | Arial; charcoal #101418, amber #FFD25A, off-white #F3F4EC. | 16px base section space; 6px corners; separators, labeled 3-destination navigation and familiar symbols. | Least visual departure. Larger targets reduce compactness. First recommendation to review. |
| **Field Notes** | Festival notebook; numbered editorial rows, open sections and generous reading rhythm. | Georgia; paper #F4EFE3, forest #3B553C, ink #20241F. | 20px sections; 2px corners; dashed row dividers; serif navigation labels with common destination symbols. | Light theme conflicts with the current dark-theme rule until approved. Outdoor benefit is unmeasured. |
| **Pulse** | Group information into distinct cards; emphasize approachable rating and recovery actions. | System sans; deep violet #151225, lavender #D2B7FF, text #F6F1FF. | 18px sections; 24px rounded cards; broad controls; labeled navigation and rounded selected state. | Easier visual grouping is a hypothesis; fewer bands fit on screen. Shortlist against Ironline. |
| **Rivet** | Planning register; explicit state, square controls and stronger numeric emphasis. | Arial body, monospace titles/metadata; navy #0C151B, mint #74DCD0, text #E6F5FA. | 14px sections; square corners; outlined rating badges and compact rows; labeled utilitarian navigation. | Higher information density, less warmth. Monospace is limited to headings/metadata to protect long-form reading. |
| **Backstage** | Music editorial; prominent title, featured first act and a card shelf where space permits. | Georgia headings, Arial body; brown-black #171311, peach #F4B997, text #FFF3DF. | 22px sections; 12px corners; editorial header, selected navigation tabs and common symbols; shelf reflows to a list. | Strong personality, most scrolling. Oversized type and long names need human readability review. |

The narrow-screen section space is 14px so controls can reflow. All share labeled Bands / Schedule / Settings destinations, semantic rating radio controls, visible state words and an evidence-before-approval path. Common navigation makes the initial style comparison fair. It is a proposed departure from BR-061, not an approved app change. Decorative symbols supplement labels; native icons would use the established Android icon system.

Exactly five styles are proposed; no style is selected for production.

## Initial recommendation
Start by comparing **Ironline and Pulse** through the full tasks. Ironline retains the existing identity with less migration risk; Pulse tests whether clearer grouping offsets reduced density. Keep Rivet as the density reference, Field Notes as the light-theme research alternative, and Backstage as the expressive reference.

This is a heuristic shortlist. Actual reviewer count is zero and no performance or preference winner is established. Prioritize target sizes, rating semantics, contrast and trustworthy save/recovery wording regardless of the chosen appearance. See the audit for prioritized changes and small follow-up story proposals.

## Validation so far
- JavaScript syntax check passes.
- Dependency-free palette test passes **80 pairs** across all five styles: 65 text pairs at least 4.5:1 and 15 boundary pairs at least 3:1.
- Browser smoke review through the in-app browser confirmed the overview renders, Abbie Falls opens, planning 4 updates to Strong choice, and the band music-review search renders a simulated candidate with Save disabled before evidence approval. Comparison page loaded all five frame accessibility trees; its first visible narrow-screen panel was inspected.
- Full Playwright interaction/layout suite is supplied in verify.cjs but **has not completed**. First launch found an incomplete cached Chromium installation; the installed Chrome launch was sandbox-blocked, and the subsequent permission request was interrupted. No automated layout or screenshot pass is claimed.
- Actual user validation remains pending. Native TalkBack, keyboard focus recovery, 200% layout, physical target size, sunlight and one-handed use remain unverified. CSS intent and labels alone do not prove accessibility.

| Style | Lowest enabled text-token contrast | Lowest boundary-token contrast |
|---|---:|---:|
| Ironline | 7.31:1 | 4.28:1 |
| Field Notes | 5.62:1 | 3.70:1 |
| Pulse | 7.55:1 | 4.08:1 |
| Rivet | 6.61:1 | 4.03:1 |
| Backstage | 7.48:1 | 4.39:1 |

Measured from CSS tokens, not rendered pixels. Disabled opacity, anti-aliasing, focus adjacency and other compositing are outside the calculation. [Raw results](contrast-results.json).

### Reproducible checks
```sh
node --check design/release-2.34/prototype.js
node design/release-2.34/contrast.cjs
```

With Playwright installed in your environment and the server running:

```sh
node design/release-2.34/verify.cjs
```

Optional environment variables: PLAYWRIGHT_MODULE (absolute package path), CHROMIUM_PATH (existing browser executable), PROTOTYPE_URL, DESIGN_OUTPUT. No new package or browser is installed by the scripts. The browser suite exercises rating/clear, unknown links, approval gates, festival rejection, ambiguous/no-match, offline pending and schedule recovery across five styles; it also checks 320/390 CSS px at 100/200% and is intended to produce 25 screen captures plus a comparison. Those outputs are pending.

## Scope and delivery impacts
README impact: updated the project entry point with this design study and review commands.
Business requirements impact: none, because these are explicitly unapproved design alternatives; current product rules remain authoritative.
Diagram impact: none, because the study changes no architecture or production flow.
ADR impact: none, because no architecture-significant decision or production implementation is made.

No production Android, persistence, API or dependency changes. TASK-201 stays In Progress for full validation and actual reviewer feedback. Actual channel verification and saving are separate TASK-202 work.


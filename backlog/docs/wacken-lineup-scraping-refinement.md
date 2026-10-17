# Wacken Lineup Scraping Refinement

Date: 2026-05-15

Related task: task-15

## Sources Inspected

- Official band list page: https://www.wacken.com/en/line-up/bands/#/bandfilter
- Official artist-detail route example: https://www.wacken.com/en/line-up/bands/#/bandfilter/5th-avenue#artistdetail
- Discovered JSON feed: https://www.wacken.com/fileadmin/Json/bandlist-concert.json
- Imprint and data-protection page: https://www.wacken.com/en/rechtliches/imprint/
- `robots.txt`: https://www.wacken.com/robots.txt

## Findings

The band list page is a TYPO3 page with a Vue-based band filter. The static HTML does not contain the rendered band cards or artist detail content. It declares a JavaScript configuration value:

```text
t3vue.ajaxUrls.list = 'https://www.wacken.com/fileadmin/Json/bandlist-concert.json'
```

The discovered JSON feed is the best current source for early band import. On 2026-05-15 it returned 164 concert entries.

## Available Band Fields

The JSON entries currently expose these top-level fields:

| Field | Meaning / use |
| --- | --- |
| `uid` | Wacken entry id for the band-list item. |
| `artist.uid` | Artist id. |
| `artist.title` | Band name. |
| `artist.pathSegment` | Slug used for detail routing. |
| `artist.country[]` | Country metadata with localized names and ids. |
| `artist.events[]` | Event/performance references when available. Can include historical or non-2026 events, so it must be filtered carefully. |
| `biography` / `biographyDe` | HTML biography text. |
| `detailLink` | Detail URL path, currently German path values in the JSON. |
| `externalMediaSource` | Usually a YouTube URL or channel URL used by the official detail view. |
| `externalMediaRatio` | Ratio for the embedded media. |
| `images[]` / `thumbnail` | Main image and processed thumbnail paths. |
| `networkuri[]` | Social/homepage links. Current observed social titles: `Homepage`, `Facebook`, `Instagram`, `Youtube`. |
| `performance[]` | Performance categories, for example `Concert`. This is not the final running order. |
| `spotifyartist` | Spotify artist id when available. |
| `spotifyalbum` | Spotify album id when available. Rare in the current feed. |
| `subtitle` | Optional subtitle, for example special set labels. |
| `firsttime` | Whether the band is marked as a first-time appearance. |
| `festival.runningOrderActive` | Indicates whether the running order is active for the festival feed. |

Observed coverage on 2026-05-15:

- 164 concert entries.
- 158 entries with `externalMediaSource`.
- 155 entries with `spotifyartist`.
- 1 entry with `spotifyalbum`.
- 60 entries with at least one `artist.events[]` value.

## YouTube And Spotify

YouTube is available in two forms:

- `externalMediaSource`, usually the main official embedded media URL.
- `networkuri[]` entries whose `socialnetwork.title` is `Youtube`.

Spotify is available as ids rather than full links:

- `spotifyartist`: Spotify artist id.
- `spotifyalbum`: Spotify album id.

Recommended app behavior:

- Store YouTube as a URL when `externalMediaSource` points to YouTube, and optionally store a separate `youtubeNetworkUrl` when `networkuri` contains a `Youtube` entry.
- Store Spotify ids separately from URLs. The UI can later construct links such as `https://open.spotify.com/artist/{spotifyartist}` or `https://open.spotify.com/album/{spotifyalbum}`.
- Treat all links as optional. Missing or blank links must not render empty controls.

## Dynamic Page And Scraping Approach

The official hash routes such as `#/bandfilter/5th-avenue#artistdetail` are client-side routes. Fetching them returns the same page shell, not a separate static artist-detail document.

Recommended technical approach:

1. Prefer the JSON feed over browser scraping for MVP 1.
2. Parse the feed as JSON, not HTML.
3. Import only stable early-rating metadata first: band id, name, slug, biography, image/thumbnail, optional YouTube URL, optional Spotify ids, homepage/social links, first-time flag, and subtitle.
4. Do not treat `artist.events[]` as authoritative final WOA 2026 schedule data until the lineup is final and `festival.runningOrderActive` / event festival ids are verified.
5. Use CSV or a reviewed data grid as the source of truth for final stages/times if the JSON feed remains mixed with historical events or incomplete schedule data.

## Legal / Approval Constraints

No `robots.txt` file was available at `https://www.wacken.com/robots.txt`; the server returned a 404 response on 2026-05-15.

The imprint/data-protection page identifies WOA Festival GmbH as the responsible party and describes normal website logging, cookies, and embedded YouTube processing. It does not provide explicit permission for automated scraping.

Before implementing unattended scraping against the official site:

- Get product-owner approval that this private planning app may fetch the JSON feed.
- Keep requests low volume and cache the feed locally after user-triggered refreshes.
- Do not bypass consent, authentication, rate limits, or technical protection.
- Do not copy or republish biographies/images beyond what is needed for private planning unless rights are clarified.
- Prefer a user-reviewed import flow that shows proposed changes before updating app data.

## Follow-Up Implementation Impacts

- Extend the early band import shape to accept Wacken JSON fields, not just a list of names.
- Add optional band metadata storage for image, biography, YouTube URL, Spotify artist/album ids, homepage/social links, subtitle, and source ids.
- Keep final performance CSV schemas separate from early JSON metadata until the final running order is verified.
- Update task-16 CSV schema refinement to include optional music-link fields and a source id/slug mapping.

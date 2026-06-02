# One-Group Identity And Rating Sharing

Date: 2026-05-15

Related task: task-18

## Scope

The current product supports one shared group only. Multi-group support is explicitly out of scope for this year.

Group name for MVP:

```text
my group
```

The app may show this label in UI, exports, and share text. It must not ask users to create or switch groups in the current version.

## Member Representation

For MVP, a group member is represented by a stable local display name.

| Field | Required | Meaning |
| --- | --- | --- |
| `member_id` | Yes | Stable internal id derived from or assigned for the display name. |
| `display_name` | Yes | Name shown in rating/group views. |
| `active` | Yes | Whether this member participates in group decisions. |

Authentication, accounts, passwords, and remote user profiles are not part of MVP.

Recommended first implementation:

- Let the owner add member names manually.
- Store ratings keyed by `member_id` or normalized display name.
- Treat the current local user as one member of `my group`.

Current code note:

- Existing `RatingRepository` methods use `userName`. Until member ids are added, use a stable display name string consistently.

## Rating Ownership

Each rating belongs to one member and one band.

| Field | Meaning |
| --- | --- |
| `member_id` / `userName` | Rating owner. |
| `band_id` or band name | Rated band. |
| `rating` | Explicit value from 1 to 5; 0 is reserved for unrated/no explicit rating. |
| `updated_at` | Future sync/export field; not required by current repository. |

Default behavior:

- If a member has not rated a band, the effective rating is `0` unrated.
- The default must not be stored as an explicit rating unless the user actively saves it.
- If the member changes the rating, the explicit saved rating replaces the default for future reads.

## Rating Exchange For MVP

Because no backend sync is specified, MVP rating sharing should use explicit import/export instead of live synchronization.

Recommended MVP exchange model:

1. Each member rates bands locally or one person enters ratings for each member.
2. The app can export member ratings as a small shareable file or text payload.
3. The group owner imports received ratings into `my group`.
4. Conflicts are resolved by latest imported value only if `updated_at` exists; otherwise the importer should show a review row before replacing an existing rating.

Suggested rating export shape:

```json
{
  "type": "wacken-planner-ratings",
  "version": 1,
  "group": "my group",
  "member": {
    "displayName": "Dino"
  },
  "ratings": [
    {
      "bandId": "5th-avenue",
      "rating": 4,
      "updatedAt": "2026-05-15T14:00:00"
    }
  ]
}
```

This can be shared as:

- A `.json` file through Android Sharesheet.
- A text attachment in chat apps.
- Later, a QR code or deep link if the payload is small enough.

## Friend Invite Format

Most useful Android-first invite format:

- Use Android Sharesheet with human-readable text plus an app deep link.
- Include a fallback explanation for users who do not have the app installed.

Recommended future link:

```text
wackenplanner://join?group=my-group&invite={token}
```

Recommended shared text:

```text
Join my Wacken Planner group: my group
wackenplanner://join?group=my-group&invite={token}
```

MVP limitation:

- Without backend storage or durable shared group ids, the invite token cannot securely resolve shared state.
- Therefore friend invites should be deferred until group storage/sync is designed.
- For MVP 1 and early MVP 2, use manual member entry plus rating import/export.

## Follow-Up Implementation Needs

Implementation tasks needed later:

- Add a `GroupMember` model once group decision logic needs multiple member ratings.
- Add import/export use cases for member ratings.
- Add review-grid support for replacing existing member ratings.
- Add Android Sharesheet export/import flows.
- Add deep-link invite handling only after storage/sync is approved.

Architecture/ADR needs:

- Durable rating storage requires architecture approval and likely an ADR.
- Backend sync, shared invite tokens, or multi-device collaboration require architecture approval and an ADR.
- Multi-group support is a next-year feature and should not be hidden inside the MVP one-group model.

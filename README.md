# Wacken Planner 2026

## Context
- Android app for Wacken Open Air 2026 helping friends rate bands and build a shared, conflict-aware schedule.
- Respects shared ratings, veto rules, overlapping performances, walking-time context, and manual group choices when proposing timelines.

## Basic Functionality (MVP 1)
- Import festival data (bands, stages, performances, distances, food) from validated CSV files by selecting files in the Android import screen.
- Show locally cached lineup, planning ratings, real post-show ratings, schedule data, and manual schedule locks first, while Supabase sync refreshes Room in the background.
- List bands in a compact dark table with Band, Rating, Stage, Date, and Time columns.
- Hide generic Metal Battle placeholder acts from rating lists and rating allocation counts while preserving imported master data.
- Show compact read-only per-person star details on band detail and schedule decision detail screens when group ratings are available.
- Let users rate bands on a 1-5 scale (1 = veto, 5 = must-see), with 0 reserved for unrated bands/no filled stars, and clear a previous rating back to unrated.
- Let users record a separate real post-show rating on band detail after seeing a band, with its own 1-5 scale and reset-to-unrated behavior; real ratings are local/offline-first and do not affect group schedule decisions.
- Store every real post-show rating as personal band rating history with festival and created-date context, sync that history to Supabase, and show it on band detail and archived festival history screens.
- Export all locally cached band rating data from Settings as a CSV file for Android share/save targets, including planning ratings, real post-show ratings, cached group member ratings, and schedule metadata where known.
- Start on the active festival band list, with `Wacken Open Air 2026` seeded as the default active festival. The top action row includes **Archive**; archiving the active festival immediately moves the app to a no-active-festival start state that lists archived festivals as read-only, opens archived festivals as read-only band lists with band detail screens, and shows the next add-festival entry point.
- Add the next festival from a band CSV after the active festival is archived; the festival name starts empty and must be filled in explicitly. Exact band-name matches reuse existing band records, new names create new band records, and known bands get festival planning ratings prefilled from the user's latest personal band rating.
- Save rating and manual schedule-lock changes locally immediately, queue them as pending offline operations, and sync them with Supabase in the background on start/reactivation, on manual sync, or before close.
- Open available YouTube and Spotify links from overview rows and band detail screens.
- Show readable imported English band biography/explanation and available band image metadata on the detail screen when source data provides it, without leaking raw HTML tags.
- Return from band detail to the same overview band row after refresh so users can continue rating without losing their place.
- Sign in with Supabase Auth so ratings can be associated with a user and the shared Wacken planning group.
- Keep the band overview focused with compact settings, schedule, and sync-exit icon actions; settings contains group invite, lineup import, active-festival rename, reviewed imported-band linking, own-catalog metadata enrichment, rating export, and manual Supabase sync.
- Share plain-text invite instructions for the single shared `Sofie and Dino` planning group through Android's share sheet.
- Generate and view an MVP2 day-filtered calendar schedule with fixed stage labels on the left and horizontally scrollable time columns across the top, using shared ratings, middle-30-minute conflict rules, optional decisions, winner stars, and lost-alternative stars; the view can locally hide barred overlapping acts or acts at/below a selected star threshold; tapping a performance block opens the chosen act and alternatives, and alternatives can be selected as Supabase-synced locked group winners.

## Architecture
- Clean Architecture with DDD boundaries: domain, application, infrastructure, and Android UI modules.
- Business rules live in the domain; application orchestrates use cases; infrastructure provides adapters (e.g., CSV, persistence); Android module handles presentation only.
- Dependencies point inward only; UI never contains business logic.
- ADR: [`0001-initial-android-clean-architecture-scaffold.md`](backlog/decisions/0001-initial-android-clean-architecture-scaffold.md).
- ADR: [`0003-github-actions-ci-and-apk-artifact.md`](backlog/decisions/0003-github-actions-ci-and-apk-artifact.md).
- ADR: [`0005-food-and-stage-repository-ports-for-csv-import.md`](backlog/decisions/0005-food-and-stage-repository-ports-for-csv-import.md).
- ADR: [`0006-mvp-file-backed-local-persistence.md`](backlog/decisions/0006-mvp-file-backed-local-persistence.md) (superseded).
- ADR: [`0007-room-local-cache-with-tsv-backend-source.md`](backlog/decisions/0007-room-local-cache-with-tsv-backend-source.md).
- ADR: [`0008-supabase-postgres-flyway-migrations.md`](backlog/decisions/0008-supabase-postgres-flyway-migrations.md).
- ADR: [`0009-supabase-group-schedule-winner-locks.md`](backlog/decisions/0009-supabase-group-schedule-winner-locks.md).
- ADR: [`0010-offline-first-sync-boundary.md`](backlog/decisions/0010-offline-first-sync-boundary.md).
- ADR: [`0011-post-wacken-festival-rating-model.md`](backlog/decisions/0011-post-wacken-festival-rating-model.md) defines the accepted post-MVP3 festival archive, reusable band, planning rating, and personal rating history direction.
- CSV schemas: [`festival-data-csv-schemas.md`](backlog/docs/festival-data-csv-schemas.md).
- Visual design system: [`visual-design-system.md`](backlog/docs/visual-design-system.md).
- Release process: [`release-process.md`](backlog/docs/release-process.md).
- MVP 1 UAT checklist and sample import files: [`mvp1-android-uat-checklist.md`](backlog/docs/mvp1-android-uat-checklist.md), [`samples/mvp1`](samples/mvp1).
- MVP 2 UAT checklist: [`mvp2-android-uat-checklist.md`](backlog/docs/mvp2-android-uat-checklist.md).
- V2.29 release notes: [`releases/v2.29.md`](releases/v2.29.md).
- V2.28 release notes: [`releases/v2.28.md`](releases/v2.28.md).
- V2.27 release notes: [`releases/v2.27.md`](releases/v2.27.md).
- V2.26 release notes: [`releases/v2.26.md`](releases/v2.26.md).
- V2.25 release notes: [`releases/v2.25.md`](releases/v2.25.md).
- V2.24 release notes: [`releases/v2.24.md`](releases/v2.24.md).
- V2.23 release notes: [`releases/v2.23.md`](releases/v2.23.md).
- V2.22 release notes: [`releases/v2.22.md`](releases/v2.22.md).
- V2.21 release notes: [`releases/v2.21.md`](releases/v2.21.md).
- V2.20 release notes: [`releases/v2.20.md`](releases/v2.20.md).
- V2.19 release notes: [`releases/v2.19.md`](releases/v2.19.md).
- V2.18 release notes: [`releases/v2.18.md`](releases/v2.18.md).
- V2.17 release notes: [`releases/v2.17.md`](releases/v2.17.md).
- V2.16 release notes: [`releases/v2.16.md`](releases/v2.16.md).
- V2.15 release notes: [`releases/v2.15.md`](releases/v2.15.md).
- V2.14 release notes: [`releases/v2.14.md`](releases/v2.14.md).
- V2.13 release notes: [`releases/v2.13.md`](releases/v2.13.md).
- V2.12 release notes: [`releases/v2.12.md`](releases/v2.12.md).
- V2.11 release notes: [`releases/v2.11.md`](releases/v2.11.md).
- V2.10 release notes: [`releases/v2.10.md`](releases/v2.10.md).
- V2.9 release notes: [`releases/v2.9.md`](releases/v2.9.md).
- V2.8 release notes: [`releases/v2.8.md`](releases/v2.8.md).
- V2.7 release notes: [`releases/v2.7.md`](releases/v2.7.md).
- V2.6 release notes: [`releases/v2.6.md`](releases/v2.6.md).
- V2.5 release notes: [`releases/v2.5.md`](releases/v2.5.md).
- V2.4 release notes: [`releases/v2.4.md`](releases/v2.4.md).
- V2.3 release notes: [`releases/v2.3.md`](releases/v2.3.md).
- V2.2 release notes: [`releases/v2.2.md`](releases/v2.2.md).
- V2.1 release notes: [`releases/v2.1.md`](releases/v2.1.md).
- V2.0 release notes: [`releases/v2.0.md`](releases/v2.0.md).
- V1.1 release notes: [`releases/v1.1.md`](releases/v1.1.md).
- V1.0 release notes: [`releases/v1.0.md`](releases/v1.0.md).

### Module Map

```mermaid
flowchart LR
    app["app\nAndroid UI/bootstrap"]
    infrastructure["infrastructure\nSource adapters and sync decorators"]
    room["app persistence\nRoom cache adapters"]
    application["application\nUse cases and ports"]
    domain["domain\nBusiness rules and model"]

    app --> infrastructure
    app --> room
    app --> application
    app --> domain
    infrastructure --> application
    infrastructure --> domain
    room --> domain
    application --> domain
```

Current modules:

| Module | Type | Responsibility |
| --- | --- | --- |
| `domain` | Java library | Business concepts and rules. Must not depend on Android. |
| `application` | Java library | Use cases and ports. Depends on `domain`; must not depend on Android. |
| `infrastructure` | Java library | TSV backend-like source adapters, in-memory test adapters, and sync/write-through decorators. Depends inward on `application` and `domain`. |
| `app` | Android application | Android UI/bootstrap, APK packaging, and Room local-cache adapters. |

Current repositories cover festivals, festival lineups, bands, stages, performances, stage distances, food options, festival planning ratings, personal band rating history, real post-show latest ratings, and group schedule locks. The app reads lineup and mutable shared data from Room first. Supabase is the primary master-data and shared-data sync backend; the CSV/TSV path remains as an explicit fallback/import tool. Real post-show ratings are recorded as personal band rating events for sync and history while the latest real rating remains available locally for the band detail control.

The implemented post-MVP3 slice adds explicit festival lifecycle and rating history state. Room stores the seeded active `Wacken Open Air 2026` festival, archived festival status, festival lineup entries, festival-scoped planning ratings, and personal band rating events. Flyway migrations `V009__festivals.sql` and `V010__festival_lineups_and_rating_history.sql` create the matching Supabase contracts.

### Technologies
- Language: Java
- Build: Gradle (Android)
- Local cache: AndroidX Room
- File sharing: AndroidX Core `FileProvider` for Settings CSV export
- Backend database: Supabase Postgres, managed by Flyway SQL migrations
- Testing: JUnit 5, Mockito/fakes, JaCoCo coverage gates, and a dedicated `qaTest` scenario suite
- Output: Debug APK via `./gradlew assembleDebug`

### C4: Level 1 (System Context)

```mermaid
C4Context
    title Wacken Planner 2026 - System Context
    Person(attendee, "Attendee", "Rates bands and views schedule")
    Person(admin, "Admin", "Imports festival data via CSV")
    System_Boundary(app, "Wacken Planner 2026") {
        System(mobile, "Android App", "Band listing, ratings, schedule")
    }
    System_Ext(supabase, "Supabase Postgres", "Central bands, schedule metadata, auth, groups, ratings")
    System_Ext(csvSource, "Festival CSV Files", "Fallback import files for bands, stages, performances, distances, food")
    Rel(attendee, mobile, "Rates bands, views lineup and schedule")
    Rel(admin, mobile, "Manages/imports validated festival datasets")
    Rel(supabase, mobile, "Provides authenticated master-data sync", "HTTPS/PostgREST")
    Rel(csvSource, mobile, "Provides fallback festival datasets", "CSV")
```

### C4: Level 2 (Container)

```mermaid
C4Container
    title Wacken Planner 2026 - Container View
    Person(attendee, "Attendee")
    Person(admin, "Admin")
    System_Ext(supabase, "Supabase Postgres", "Central master data and shared group data")
    System_Ext(csvSource, "Festival CSV Files", "Validated fallback master data")

    System_Boundary(app, "Wacken Planner 2026") {
        Container(ui, "Android UI", "Java", "Screens for band list, ratings, imports, schedule")
        Container(appsvc, "Application Layer", "Java", "Use cases for listing, rating, imports")
        Container(domain, "Domain", "Java", "Entities, value objects, decision rules")
        Container(infra, "Infrastructure", "Java", "Adapters: Supabase source, TSV fallback source, and sync decorators")
        ContainerDb(cache, "Room Local Cache", "SQLite/Room", "Fast local app cache for imported data and ratings")
        ContainerDb(tsv, "TSV Fallback Source", "App-private TSV files", "Fallback import source")
    }

    Rel(attendee, ui, "Rates bands, views lineup")
    Rel(admin, ui, "Triggers CSV imports")
    Rel(ui, appsvc, "Invokes use cases")
    Rel(appsvc, domain, "Uses domain rules and models")
    Rel(appsvc, infra, "Accesses repository adapters")
    Rel(infra, cache, "Reads from and writes through to local cache")
    Rel(infra, supabase, "Syncs master data", "HTTPS/PostgREST")
    Rel(infra, tsv, "Uses only for explicit fallback CSV import")
    Rel(csvSource, infra, "Supplies fallback import files", "CSV")
```

## Setup and Run
1. Clone: `git clone git@github.com:DinoDeWaen/wacken.git` and `cd wacken`.
2. Ensure JDK 21 and Android SDK are installed. The current Gradle and Android plugin stack is validated with JDK 21 and Android SDK 36.
3. Create `local.properties` if needed:

   ```properties
   sdk.dir=/absolute/path/to/Android/sdk
   ```

4. Run tests: `./gradlew test` (include QA suite task when available).
5. Run QA scenarios: `./gradlew qaTest`.
6. Build debug APK: `./gradlew assembleDebug` (artifact in `app/build/outputs/apk/debug/`).

Useful module checks:

```bash
JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew test
JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew qaTest
JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew assembleDebug
```

Useful focused checks:

```bash
./gradlew :domain:compileJava :application:compileJava
./gradlew :domain:test :application:test
./gradlew :domain:jacocoTestCoverageVerification :application:jacocoTestCoverageVerification
./gradlew qaTest
```

Before building official signed release APKs, verify the local release keystore:

```bash
scripts/ensure-release-keystore.sh
```

The script keeps the keystore in `.local/release/`, which is ignored by git,
and updates the macOS keychain `WACKEN_RELEASE_STORE_FILE` path when needed.

## Backend Database

Supabase Postgres is the planned central backend for shared ratings and admin
managed festival data. Flyway migrations live in:

```text
backend/flyway/sql
```

Local credentials must stay outside git. Create `.env.supabase.local` from the
template:

```bash
cp backend/flyway/env.template .env.supabase.local
```

Fill in the local password in `.env.supabase.local`. This file is ignored by
git. Do not commit database passwords, service-role keys, or other secrets.

Run Flyway:

```bash
backend/flyway/run-flyway.sh info
backend/flyway/run-flyway.sh migrate
backend/flyway/run-flyway.sh info
```

The script uses locally installed `flyway` when available and falls back to
Docker otherwise. Band import uses local `psql`; set `PSQL_BIN` if `psql` is
installed but not on `PATH`.

Upload the generated Wacken bands CSV to Supabase:

```bash
backend/flyway/import-bands.sh
backend/flyway/verify-bands-import.sh
backend/flyway/verify-auth-setup.sh
```

The band import is idempotent. Re-running it upserts the CSV rows and marks
bands missing from the CSV as inactive rather than deleting rows.

Admin master-data import for the full CSV set:

```bash
backend/flyway/import-master-data.sh
```

By default this imports:

```text
data/wacken-2026/bands.csv
data/wacken-2026/stages.csv
data/wacken-2026/performances.csv
data/wacken-2026/distances.csv
data/wacken-2026/food.csv
```

Override individual files with `BANDS_CSV`, `STAGES_CSV`,
`PERFORMANCES_CSV`, `DISTANCES_CSV`, and `FOOD_CSV`. The importer records a
row in `public.import_batches`, validates references, duplicate ids, invalid
performance times, stage overlaps, negative walking minutes, and food/stage
references, then applies the import in one transaction. Failed imports update
their import batch to `failed` and leave existing master data unchanged.

The Android band overview reads from Room and automatically syncs from Supabase
when the app starts and whenever the overview is reactivated after returning
from another screen or app. Use **Sync from Supabase** to retry manually. Use
**Sync & close** to push/pull Supabase data before closing the app. Each sync
pulls central bands, stages, performances, stage distances, and food options
from Supabase into Room and pushes/pulls festival planning ratings, personal
band rating events, and group schedule locks. Rating changes and rating clears
are stored locally first with pending sync metadata; when Supabase accepts the
rating change it is marked synced. Clearing a planning rating deletes that
explicit user/group/festival/band rating row in Supabase, so future group pulls
no longer count the previous score. If sync fails, existing cached Room data and
pending ratings remain available and the app shows a stale-data message. A
Wacken/metal sync overlay is shown while startup, reactivation, manual, or close
sync is running. The CSV import screen remains available for fallback/local
import work and writes through the TSV fallback source plus Room cache; it is no
longer the primary app data source.

Rating scale migration: app database version 3 migrates old local explicit
ratings from the previous 0-4 scale to the new 1-5 explicit scale by adding 1
to stored rating rows. Flyway migration `V005__rating_scale_1_to_5.sql` applies
the same conversion in Supabase and then constrains backend explicit ratings to
1-5. TSV fallback rating files have no schema version; clear or regenerate old
`ratings.tsv` fallback data before release if it contains ratings written
before this migration.

### Supabase Auth

The Android app uses Supabase Auth email/password sign-in and compiles only the
public Supabase URL and anon key into `BuildConfig`. Keep database passwords and
service-role keys out of the app and out of git.

Required Supabase project settings:

- Enable the Email provider in Authentication.
- Create or invite app users through Supabase Authentication.
- Run Flyway migrations so the `auth.users` trigger creates a matching
  `public.profiles` row and assigns new users to the shared `Sofie and Dino`
  group.

The current MVP uses one shared planning group, `Sofie and Dino`:

```text
00000000-0000-0000-0000-000000000001
```

Migration `V006__sofie_dino_group_backfill.sql` keeps that canonical group id,
sets the group name to `Sofie and Dino`, and idempotently adds every existing
Supabase auth user to the group.

Signed-in users can use **Share group invite** from the Android overview to open
the Android share sheet with plain-text onboarding instructions. The shared text
explains that friends need the APK and a provisioned Supabase account, that this
version has one shared planning group only, and that ratings participate after
sync. It does not include passwords, API keys, service-role credentials, invite
tokens, or deep links.

Group role data is stored in `public.group_members.role`. To promote a user for
future admin workflows, update that user's membership role to `admin` or `owner`
and, when platform-wide access is required, set `public.profiles.is_admin`.
Ratings remain protected by RLS: an authenticated user can read/write private
ratings only when they belong to the rating's group. Rating inserts and updates
also require `ratings.user_id = auth.uid()`, so a request cannot write another
member's rating even when the caller belongs to the same group.

The app stores Supabase access and refresh tokens locally. Authenticated
Supabase requests refresh an expired access token before sending the request and
retry once when Supabase rejects a request because the JWT expired. If the
refresh token is invalid, expired, or revoked, the local session is cleared and
the user is sent back to login. If token refresh or an authenticated Supabase
request fails because Supabase cannot be reached, the stored session is kept so
cached data remains usable and pending local edits can sync later.

Coverage gates:

- `domain`: 80% minimum instruction coverage.
- `application`: 70% minimum instruction coverage.

## Troubleshooting

If Gradle fails while creating `:application:test` with `Type T not present`, check the active Java version:

```bash
java -version
```

This project is currently validated with JDK 21. On macOS, run Gradle with JDK 21 explicitly:

```bash
JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew test
JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew qaTest
JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew assembleDebug
```

The failure has been reproduced with Java 25 as the launcher JVM.

## CI

GitHub Actions runs on push and pull request.

The CI workflow:

- Sets up JDK 21 and the Android SDK.
- Runs `./gradlew test`, including the current JUnit 5 tests and JaCoCo coverage gates.
- Runs `./gradlew qaTest` for MVP listing/rating scenario coverage.
- Runs `./gradlew assembleDebug`.
- Publishes `app/build/outputs/apk/debug/app-debug.apk` as a versioned artifact named `wacken-planner-0.1.<run-number>-debug-apk`.

## Development Notes
- Follow TDD: write failing tests, implement, refactor.
- Keep commits small and focused; push incrementally for review visibility.
- Ways of working: [`development-ways-of-working.md`](backlog/docs/development-ways-of-working.md).
- Testing strategy: [`testing-strategy.md`](backlog/docs/testing-strategy.md).
- Architecture guidelines: [`architecture-guidelines.md`](backlog/docs/architecture-guidelines.md).
- ADR rules: [`architecture-decision-rules.md`](backlog/docs/architecture-decision-rules.md).
- Diagramming guidelines: [`diagramming-guidelines.md`](backlog/docs/diagramming-guidelines.md).

# MVP 2 Android UAT Checklist

Date: 2026-06-07

Related task: task-59

## Purpose

Validate the MVP2 debug APK for one shared Wacken planning group. MVP2 focuses
on shared ratings, group decisions, conflict resolution, generated schedule
viewing, rating clears, invite instructions, and returning from detail to the
selected overview row.

## Prerequisites

- Supabase Flyway schema is current through `V006`.
- Existing Supabase users are members of the `Sofie and Dino` group.
- At least two provisioned Supabase app users are available.
- The APK under test is `app/build/outputs/apk/debug/app-debug.apk`.
- Android version metadata is `versionCode 3`, `versionName 2.0`.

## Build

1. Run `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew assembleDebug`.
2. Install `app/build/outputs/apk/debug/app-debug.apk` on one or more Android devices or emulators.
3. Open Wacken Planner 2026 and sign in with a provisioned Supabase account.

## UAT Steps

### 1. Startup Sync And Shared Group

Expected result:

- The metal sync overlay appears while startup sync runs.
- The signed-in overview loads central band and schedule data from Supabase.
- The subtitle shows the signed-in account.

### 2. Shared Rating Sync

1. On device/user A, rate a scheduled band `5`.
2. Tap `Sync from Supabase`.
3. On device/user B, open or reactivate the app.

Expected result:

- Device/user B sees the updated group rating after sync.
- The generated schedule can use the rating in group decisions.

### 3. Rating Clear

1. On device/user A, open a rated band detail screen.
2. Tap the `0` clear rating action.
3. Return to the overview.
4. Sync both device/user A and device/user B.

Expected result:

- Device/user A sees no filled stars for that band.
- Device/user B no longer sees the old explicit rating after sync.
- The generated schedule no longer counts the cleared rating.

### 4. Return To Selected Overview Row

1. Scroll down the band overview.
2. Open a band detail screen.
3. Change or clear the rating.
4. Return to the overview.

Expected result:

- The overview returns to the row for the opened band instead of the top of the list.
- The row shows the changed rating state.

### 5. Invite Share Action

1. Tap `Share group invite`.
2. Choose any text-capable target in the Android share sheet.

Expected result:

- The shared text names the `Sofie and Dino` group.
- The shared text instructs the recipient to install the APK, sign in with a provisioned Supabase account, and sync ratings.
- The shared text does not include passwords, service-role keys, API keys, invite tokens, or deep links.
- The shared text does not imply creating or switching groups.

### 6. Generated Schedule

Create or verify representative group ratings that cover:

- A band with at least one `5` rating.
- A band with maximum rating `4` and fewer than two vetoes.
- A band with maximum rating `4` and two vetoes.
- A band with maximum rating `3` during the lunch window.
- Overlapping performances where a winner and lost alternative are visible.
- A fully unrated band.

Expected result:

- Must-see ratings produce `GO` decisions.
- Two vetoes block a `4`-rated band.
- Lunch-window `3` ratings produce `OPTIONAL` decisions.
- Overlapping performances choose the expected winner.
- The schedule shows lost alternatives where conflicts were resolved.
- Unrated bands do not behave like vetoed bands.

### 7. Sync And Close

1. Change a rating.
2. Tap `Sync & close`.

Expected result:

- The metal sync overlay appears.
- Pending ratings are pushed before the app closes when Supabase is reachable.
- If sync fails, the app remains open and pending local ratings remain available.

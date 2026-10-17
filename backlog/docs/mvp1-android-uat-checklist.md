# MVP 1 Android UAT Checklist

Date: 2026-05-15

Related task: task-25

## Sample Data

Use the valid CSV files in `samples/mvp1/`:

- `bands.csv`
- `stages.csv`
- `performances.csv`
- `distances.csv`
- `food.csv`

Use `samples/mvp1/invalid-performances.csv` only for the invalid import feedback test. Keep the other four CSV fields populated with the valid sample files.

## Build

1. Run `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew assembleDebug`.
2. Install `app/build/outputs/apk/debug/app-debug.apk` on an Android device or emulator.
3. Open Wacken Planner 2026.

## UAT Steps

### 1. Empty State

Expected result:

- Before importing data, the band list shows `No bands imported yet.`
- The `Import lineup CSV files` button is visible.

### 2. Valid Import

1. Tap `Import lineup CSV files`.
2. Choose each valid `samples/mvp1/*.csv` file with its matching file button.
3. Tap `Import selected files`.

Expected result:

- The screen shows `Import successful. Existing ratings were preserved.`
- No validation errors are shown.

### 3. Imported Band List

1. Tap `Back to band list`.

Expected result:

- `5th Avenue` is visible with stage `Faster`, time `2026-07-30T18:00 - 2026-07-30T19:00`, and rating `1 (default)`.
- `Midnight Skyline` is visible with stage `Harder`, time `2026-07-30T19:30 - 2026-07-30T20:30`, and rating `1 (default)`.

### 4. Detail And Links

1. Tap `5th Avenue`.

Expected result:

- The detail screen shows `5th Avenue`.
- The selected rating is `1 (default)`.
- `YouTube` and `Spotify` buttons are visible.

1. Return to the band list.
2. Tap `Midnight Skyline`.

Expected result:

- The detail screen shows `Midnight Skyline`.
- Music link buttons are hidden because the sample has no links for this band.

### 5. Rating Save And List Refresh

1. Open `5th Avenue`.
2. Select the 4-star rating.
3. Return to the band list.

Expected result:

- `5th Avenue` now shows rating `4`.
- The default marker is not shown for `5th Avenue`.

### 6. Persistence

1. Fully close the app.
2. Reopen Wacken Planner 2026.

Expected result:

- Imported bands are still visible.
- `5th Avenue` still shows rating `4`.

### 7. Invalid Import Feedback

1. Tap `Import lineup CSV files`.
2. Choose valid `bands.csv`, `stages.csv`, `distances.csv`, and `food.csv`.
3. Choose `samples/mvp1/invalid-performances.csv` for the performances file.
4. Tap `Import selected files`.

Expected result:

- The app shows row-level errors:
  - `performances.csv row 2 references unknown band_id unknown-band`
  - `performances.csv row 2 references unknown stage_id unknown-stage`
- Existing imported data and ratings remain visible when returning to the band list.

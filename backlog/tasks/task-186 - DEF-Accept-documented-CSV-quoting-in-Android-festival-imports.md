---
id: TASK-186
title: 'DEF: Accept documented CSV quoting in Android festival imports'
status: Done
assignee:
  - '@codex'
created_date: '2026-10-14 06:38'
updated_date: '2026-10-15 18:01'
labels:
  - android
  - csv
  - import
milestone: m-1
dependencies: []
references:
  - backlog/docs/festival-data-csv-schemas.md
  - >-
    application/src/main/java/be/wacken/planner/application/ImportFestivalCsvUseCase.java
  - app/src/main/java/be/wacken/planner/ImportCsvActivity.java
  - importfestivalcsvusecase
  - importcsvactivity
modified_files:
  - >-
    application/src/test/java/be/wacken/planner/application/ImportFestivalCsvUseCaseTest.java
  - >-
    application/src/test/java/be/wacken/planner/application/FestivalCsvReaderTest.java
  - app/src/test/java/be/wacken/planner/ImportCsvActivityRegressionTest.java
  - >-
    application/src/main/java/be/wacken/planner/application/FestivalCsvReader.java
  - >-
    application/src/main/java/be/wacken/planner/application/ImportFestivalCsvUseCase.java
  - app/src/main/java/be/wacken/planner/ImportCsvActivity.java
priority: high
type: bug
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Business value: Admins can import valid festival CSV files without corrupting quoted band or other field values.

Current evidence: The CSV schema requires standard quoting for commas, quotes, and line breaks. ImportFestivalCsvUseCase and ImportCsvActivity split input into physical lines and use separate minimal splitLine parsers, so multiline and escaped-quote fields are mishandled. The completed TASK-6 explicitly deferred multiline support.

Scope: Make the Android/file import and application import paths honor the documented CSV contract, with consistent parsing and clear feedback for malformed input. Preserve existing validation and successful simple imports.

Out of scope: CSV export, backend admin script from TASK-43, and changes to the published CSV schema.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given a valid CSV field containing a comma, escaped quote, or line break, when the admin imports it through Android, then the complete intended value is retained.
- [x] #2 Given malformed CSV quoting, when import is attempted, then a clear error is shown and no festival master data is changed.
- [x] #3 Existing simple-file imports and validation for references, times, and overlaps continue to work.
- [x] #4 Automated tests cover quoted fields, malformed quoting, and both affected import paths.
- [x] #5 Architecture impact is assessed before implementation; if an architecture-significant parser dependency or boundary change is needed, explicit approval is obtained before coding and an ADR is created or updated.
- [x] #6 README and business requirements impact are recorded using the canonical wording from delivery-governance.md, and validation evidence is added before completion.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Treatment: standard focused fix across the two existing Android CSV import paths.
1. Added failing tests for quoted commas and doubled quotes, quoted line breaks, malformed quoting, no master-data writes on parse failure, band-only parsing, Activity delegation, and blank-line preservation.
2. Added FestivalCsvReader in the existing application module and routed ImportFestivalCsvUseCase and ImportCsvActivity band-only import through it. Malformed quoting returns a file/row error before repository writes.
3. Passed focused tests and ./gradlew test; deleted the prior local release APK, rebuilt a signed task-validation APK, verified v1/v2 signatures, and ran git diff --check. app:lintDebug remains failing on 14 pre-existing NewApi findings outside changed files; release lintVital passed.
Changed files: application/src/test/java/be/wacken/planner/application/ImportFestivalCsvUseCaseTest.java; application/src/test/java/be/wacken/planner/application/FestivalCsvReaderTest.java; app/src/test/java/be/wacken/planner/ImportCsvActivityRegressionTest.java; application/src/main/java/be/wacken/planner/application/FestivalCsvReader.java; application/src/main/java/be/wacken/planner/application/ImportFestivalCsvUseCase.java; app/src/main/java/be/wacken/planner/ImportCsvActivity.java.
Architecture: no new dependency, port, adapter, module, external contract, or persistence change; no architecture approval/ADR required. The published CSV schema already specifies this behavior, so README, business requirements, and diagrams need no change. The Android wiring test is source-level because the existing JVM setup has no Activity runner.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Both Android festival imports now use one standards-compliant CSV record reader. Quoted commas, doubled quotes, and line breaks are parsed consistently. Malformed quoting reports the file and physical row before any master-data write.

## Acceptance criteria validation

- AC1: Festival import and band-only import tests cover quoted values and multiline fields.
- AC2: Malformed input returns a clear file/row error and leaves prior master data intact; the add-festival Activity catches parser errors before its use case writes.
- AC3: Existing import, reference, overlap, rating preservation, and simple-file tests pass.
- AC4: Added reader, import use-case, and Android wiring regressions.
- AC5: Existing application module and Android dependency direction are unchanged; no significant architecture decision or ADR.
- AC6: Documentation impact and validation are recorded below.

## How to test

### Automated tests

- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :application:test :app:testDebugUnitTest` passed.
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew test` passed, including coverage gates.
- Signed `assembleRelease` passed after deleting the previous local release APK; release lintVital passed. `apksigner verify --verbose` confirmed v1 and v2 signatures. `git diff --check` passed.
- Local signed task-validation APK SHA-256: 319e407ba0573ae92e63cabb961ae3553e7684e3c0b690aa63b83c2615e30389.
- `:app:lintDebug` failed on 14 existing NewApi findings in untouched files; none is in the changed files.

### Manual validation

No device or emulator walkthrough was run. This local APK remains version 2.32; it is not the official Release 2.33 artifact.

## TDD / BDD / approval-test evidence

New parser tests initially failed to compile before the reader existed. The blank-line regression failed against the first implementation, then passed after correction. Existing import tests characterized preserved behavior.

## Architecture impact

- Architecture-significant change: no; parsing remains in the existing application module and Android calls it through the existing dependency.
- Approval received: not required.
- ADR impact: none, because no boundary, dependency, or persistence decision changed.

## README impact

README impact: none, because it already points to the CSV schema and does not describe the old parser limitation.

## Business requirements impact

Business requirements impact: none, because the parser now meets the existing documented CSV contract without changing product scope or rules.

## Diagram impact

Diagram impact: none, because components and their relationships remain the same.

## Commits / logical change list

- Added a shared CSV reader and tests.
- Reused it in festival and band-only Android import paths.
- Converted malformed quoting into user-visible import errors.

## Risks and follow-up

The Activity wiring regression checks source rather than launching an Activity, because the current JVM test setup has no Android Activity runner. Existing unrelated Android lint NewApi errors remain. TASK-187 addresses write failures after parsing and validation.
<!-- SECTION:NOTES:END -->

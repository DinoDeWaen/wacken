---
id: TASK-222
title: 'CI: Keep disposable database validation available during registry limits'
status: Done
assignee:
  - '@codex'
created_date: '2026-10-17 14:46'
updated_date: '2026-10-17 14:53'
labels:
  - ci
  - testing
  - release-2.34
milestone: m-0
dependencies: []
modified_files:
  - scripts/tests/test_test_images.py
  - scripts/test-container-images.sh
  - scripts/verify-festival-creation.sh
  - scripts/verify-rating-state.sh
  - .github/workflows/ci.yml
  - README.md
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a release owner, I need mandatory database and API tests to run reliably when the image registry limits downloads. Multiple GitHub runs failed before tests with ECR Data limit exceeded. Use verified matching official images and bounded image acquisition; retain every test and fail if neither source is available. No production deployment, database version or new runtime dependency.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given the primary image registry is unavailable or rate limited, then CI obtains the identical official image from a verified source and runs every existing database/API test.
- [x] #2 Given both image sources fail or image identity differs, then validation fails clearly rather than skipping tests.
- [x] #3 Automated fixture tests cover acquisition failures and identity enforcement; actual disposable database/API validation passes.
- [x] #4 README, business requirements, diagram and ADR impacts are documented with canonical wording.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Completed six red/green Docker acquisition tests; shared per-platform immutable official image helper with verified cache/two-source fallback; existing creation/rating runners and CI step. Verified matching registry manifests, actual forced ECR refusal→Docker Hub downloads, real SQL/HTTP suites, shell/diff checks and fresh signed JVM/QA/coverage/lint-vital task build. README updated. No architecture/runtime/version change; final GitHub source validation remains214.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Required disposable tests now use immutable verified official images with cache reuse and two-source acquisition. ECR refusal falls back once to the same platform digest on Docker Hub. The shared helper verifies the image digest and fails if neither source is verified. Both existing runners and every SQL/HTTP/skip guard remain enforced.

Root cause: ECR returned toomanyrequests: Data limit exceeded before any test ran (CI218 job117422950335). Repeated retries did not resolve availability. Read-only Docker registry manifests confirmed identical amd64/arm64 content across official sources for Postgres17.6.1.166 and PostgREST14.5. Exact digests are pinned in scripts/test-container-images.sh. Docker Hub's PostgREST repository is postgrest/postgrest, not supabase/postgrest.

## Acceptance criteria validation

1. Injected actual ECR refusal forced official Docker Hub downloads by identical pinned digest; full real rating/archive SQL and PostgREST gates passed.
2. Fake Docker tests prove both-source failure and wrong identity fail, rather than skipping validation.
3. Six acquisition regressions pass; real creation13 scenarios and rating/archive suites pass under pinned images. CI runs the acquisition regressions before database tests.
4. README and canonical impacts recorded.

## How to test

### Automated tests

- python3 scripts/tests/test_test_images.py:6 passed; cache, primary refusal, both failures, wrong digest, unsupported architecture and amd64 selection. All6 initially failed before helper implementation.
- JDK21 bash scripts/verify-festival-creation.sh:13 passed,0 skipped, plus SQL rollback/member assertions.
- bash scripts/verify-rating-state.sh: migration/RLS/reset/replay/archive/fault/concurrency passed.
- Actual forced fallback: temporary Docker wrapper refused only ECR inspect/pull and delegated all other operations to real Docker; Docker Hub downloaded the exact ARM64 digests and all rating/archive SQL/HTTP checks passed.
- bash -n on helper/both runners and git diff --check passed.
- Fresh :app:clean test qaTest :app:lintVitalRelease :app:assembleRelease passed with coverage gates. APK digest 054bf4ee9c83bf8062a9ae23f020307de100ca3c2bd1edbca970354d4b53ef89, stable certificate SHA256 ede2ae97735a305900e87bc0fdc021c03382db4e30435f2349a96a3fdcc7545f verified. Baseline2.33.5/code41 task artifact only.
- Native39-test suite passed immediately before this CI-only change under221; runtime code unchanged. Final-source CI remains214 gate.

### Manual validation

No production changes. Actual registry manifests and real Docker fallback verified; no separate human acceptance claimed.

## TDD / BDD / approval-test evidence

Six failure-handling tests red before helper existed, then green. Real injected registry refusal confirms user-observable CI recovery without test bypass.

## Architecture impact

Architecture-significant change: no, because the same official image contents/versions, disposable boundaries and required test strategy remain unchanged. No runtime dependency/service/secret introduced.
ADR impact: none, because this is test-image acquisition robustness within existing CI.

## README impact

README impact: updated immutable official image acquisition, fallback/failure behavior and fixture-test command.

## Business requirements impact

Business requirements impact: none, because test availability does not change product behavior or scope.

## Diagram impact

Diagram impact: none, because production architecture and test boundaries are unchanged.

## Commits / logical change list

Shared immutable image helper; six acquisition tests; existing runner integration; required CI step; README and release dependency.

## Risks and follow-up

Both registries may fail; validation intentionally fails clearly then. Unsupported daemon architectures fail rather than changing image/version. Final-source GitHub amd64 validation and remaining feature/upgrade/publication gates remain required214. No main merge or2.34 publication.
<!-- SECTION:NOTES:END -->

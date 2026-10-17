# 2.34 database rollout evidence

## Applied before APK publication

On2026-10-17, the existing Flyway process applied the reviewed/tested additive
V014–V018 to the configured Supabase database. The prior schema was013; all five
migrations succeeded and post-rollout `validate` reports18 valid migrations,
schema018, with no pending/failed migration. The separate private local backup
is `/private/tmp/wacken-214-before.dump` (mode restricted; never a release asset).

No production fixture import, schema reset or signing-key rotation occurred.

## Existing data preservation

Counts and logical content hashes match before/after rollout. New default revision
columns are excluded from the planning content hash; existing values are included.

| Existing data | Rows before/after | MD5 logical content before/after |
| --- | ---: | --- |
| Canonical bands |224|57c1a80d1e0191e0421263cc87a680f1|
| Festivals |2|14d00f84a4551f0576f7d614e50f7087|
| Lineup entries |228|04aea304de8fa076e6e981716fe92bfc|
| Festival planning |274|a719b5b53218f1176dd1cd399d359bae|
| Personal history events |36|71035815fe3978dd26a2b3eb478cc7d6|
| Legacy ratings |272|1823874dfe44564b42fb1e75ba5c83c6|
| Schedule locks |1|38214bca1304b9c96380f37876f232f9|

The migration backfill has **zero invalid established sources** when checked
against retained archived festivals, matching owner/band/event, known original
dates and source rating values. No active/unknown event became a source.

## Access verification

Direct production queries confirm RLS on new personal/after/operation/audit/alias
tables and no authenticated direct insert/update/delete grants. Archive operation
ledger also denies anonymous table access. Public creation, reviewed creation,
after-save, archive, review and correction RPCs are security invokers:
authenticated execute enabled, anonymous execute denied. Privileged transaction
functions remain in the private schema behind actor/member/target validation.

Supabase advisors add only two informational `rls_enabled_no_policy` notices for
the after/archive operation ledgers: intentional deny-by-default tables accessed
through the guarded private transactions, with no client table grants/policies.
No new advisor warning/error was introduced.

Pre-existing findings remain: mutable search path on the old timestamp helper,
Flyway metadata RLS, legacy membership/auth helper execution and disabled leaked
password protection. They are tracked in
[TASK224](../tasks/task-224%20-%20SEC-Review-existing-Supabase-helper-permissions-and-migration-metadata-exposure.md),
with explicit architecture/security approval required before changing those
boundaries. They were already present before2.34 and do not grant the new
rating/alias/audit tables arbitrary client writes. Relevant advisor guidance:
[function search paths](https://supabase.com/docs/guides/database/database-linter?lint=0011_function_search_path_mutable),
[public table RLS](https://supabase.com/docs/guides/database/database-linter?lint=0013_rls_disabled_in_public),
[public definer execution](https://supabase.com/docs/guides/database/database-linter?lint=0028_anon_security_definer_function_executable).

## Operational limits

New clients must use the atomic archive RPC; old status-only archive writes are
rejected to protect promotion/history. Update members to2.34 for the supported
archive/correction workflow. Exact-name creation keeps its published signature;
reviewed creation is separate. Offline/pending work remains on the device and is
not discarded when server writes are denied. Authenticated lifecycle, rollback,
RLS, retry and concurrency behavior is covered by the exact-source disposable
PostgreSQL/PostgREST gates; no real member credentials are embedded in the APK.

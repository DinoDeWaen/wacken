---
id: task-40
title: 'US-040: Add Supabase Auth and group membership model'
status: Done
assignee:
  - '@codex'
created_date: '2026-05-17 16:52'
updated_date: '2026-05-17 17:54'
labels:
  - backend
  - auth
  - supabase
  - groups
dependencies:
  - task-39
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a festival attendee, I want to sign in and belong to the shared Wacken planning group so that ratings can be linked to the right person and group.

Business value:
- Enables multiple people to contribute ratings safely.
- Provides the identity and group boundary needed for shared planning and admin access.

In scope:
- Configure the app/backend contract for Supabase Auth.
- Use the schema from task-39 for profiles, groups, and group_members.
- Support one default planning group for the current MVP while keeping the schema ready for more groups later.
- Define member/admin roles for group data access.
- Add app configuration for Supabase URL and anon key through non-secret build config.

Out of scope:
- Full invite UX.
- Multiple independent production groups.
- Admin data management screens.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given a user signs in, then the app can associate that user with a profile and group membership.
- [x] #2 Given the current MVP uses one shared planning group, then authenticated members can be assigned to that group without blocking future multi-group support.
- [x] #3 Given a user is not a group member, then RLS prevents access to private group ratings.
- [x] #4 Given a group admin exists, then admin/member role data is available for later admin workflows.
- [x] #5 Supabase URL and anon key are configured without committing service-role secrets or database passwords.
- [x] #6 Automated tests or documented validation prove the auth/group access assumptions.
- [x] #7 README documents auth setup and required Supabase project settings.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Inspect current Supabase schema, Android config, and app startup flow.
2. Add a Flyway migration for auth user profile creation and default Wacken 2026 group membership.
3. Configure non-secret Supabase URL and anon key through Android BuildConfig.
4. Add minimal Android email/password sign-in flow and local session storage.
5. Wire app startup to require sign-in before the band overview.
6. Add documented validation for RLS/group membership assumptions against Supabase.
7. Update README and task notes, run checks, migrate database, and commit task-40.

Architecture impact: uses approved Supabase backend strategy from task-39/ADR-0008; no new architecture approval expected unless implementation needs a new backend boundary decision.
Secret handling: only anon public key is compiled into Android; service role keys and DB passwords remain out of git.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Implementation notes:
- Added Flyway V004 to create/update profiles from Supabase auth.users and assign users to the default Wacken 2026 planning group. Existing auth users are backfilled into profiles and the default group.
- Added Android Supabase Auth configuration through BuildConfig using only the public Supabase URL and anon key. No DB password or service-role key is committed.
- Added minimal email/password LoginActivity, AuthSession/AuthSessionStore, SupabaseAuthClient, and startup guards for the band overview and detail screens.
- Ratings now use the signed-in Supabase user id instead of the previous local placeholder user.
- Added backend/flyway/verify-auth-setup.sh for repeatable auth/RLS validation.

Validation package:
- Flyway migrated Supabase Postgres to version 004.
- backend/flyway/run-flyway.sh info shows versions 001-004 successful.
- backend/flyway/verify-auth-setup.sh confirmed auth trigger=1, profiles insert policy=1, ratings RLS policies=4, default group=1. default_group_member_count was 0 because no Supabase Auth users exist yet in the project at validation time.
- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:compileDebugJavaWithJavac passed.
- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew test passed.
- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew qaTest passed.
- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew assembleDebug passed.

README impact: updated with Supabase Auth setup, public-key/secret handling, default group id, admin role guidance, and the auth verification script.
Diagram impact: no diagram update needed; this task wires auth into the already approved Supabase backend direction and does not change the documented high-level container shape.
ADR impact: no new ADR needed; implementation follows ADR-0008 and task-39 approval.
Approval status: no new architecture-significant decision introduced beyond the previously approved Supabase/Flyway direction.
Risks/follow-ups: manual sign-in still requires creating or inviting a Supabase Auth user with the Email provider enabled. Token refresh and remote ratings sync are expected in later DB integration tasks.
<!-- SECTION:NOTES:END -->

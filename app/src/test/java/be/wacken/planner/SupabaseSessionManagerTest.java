package be.wacken.planner;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public final class SupabaseSessionManagerTest {
    private static final long NOW = 1_000;
    private static final AuthSession EXPIRED = session("expired-access", "refresh", NOW - 10);
    private static final AuthSession FRESH = session("fresh-access", "fresh-refresh", NOW + 3_600);

    @Test
    public void refreshesExpiredSessionAndPersistsRenewedSession() throws Exception {
        FakeSessionRepository sessions = new FakeSessionRepository(EXPIRED);
        FakeAuthGateway auth = new FakeAuthGateway(FRESH);
        SupabaseSessionManager manager = new SupabaseSessionManager(sessions, auth, () -> NOW);

        AuthSession result = manager.requireFreshSession();

        assertEquals(FRESH.accessToken(), result.accessToken());
        assertEquals(FRESH.refreshToken(), sessions.saved.refreshToken());
        assertEquals(1, auth.refreshCount);
        assertFalse(sessions.cleared);
    }

    @Test
    public void reusesRenewedSessionForLaterRequests() throws Exception {
        FakeSessionRepository sessions = new FakeSessionRepository(EXPIRED);
        FakeAuthGateway auth = new FakeAuthGateway(FRESH);
        SupabaseSessionManager manager = new SupabaseSessionManager(sessions, auth, () -> NOW);

        manager.requireFreshSession();
        AuthSession secondRequestSession = manager.requireFreshSession();

        assertEquals(FRESH.accessToken(), secondRequestSession.accessToken());
        assertEquals(1, auth.refreshCount);
    }

    @Test
    public void clearsSessionWhenRefreshFails() {
        FakeSessionRepository sessions = new FakeSessionRepository(EXPIRED);
        FakeAuthGateway auth = new FakeAuthGateway(new IOException("invalid refresh token"));
        SupabaseSessionManager manager = new SupabaseSessionManager(sessions, auth, () -> NOW);

        assertThrows(AuthenticationRequiredException.class, manager::requireFreshSession);

        assertTrue(sessions.cleared);
        assertFalse(sessions.load().isPresent());
    }

    @Test
    public void refreshesAndRetriesOnceAfterExpiredJwtResponse() throws Exception {
        FakeSessionRepository sessions = new FakeSessionRepository(session("stale-access", "refresh", NOW + 3_600));
        FakeAuthGateway auth = new FakeAuthGateway(FRESH);
        SupabaseSessionManager manager = new SupabaseSessionManager(sessions, auth, () -> NOW);
        SupabaseAuthenticatedRequest request = new SupabaseAuthenticatedRequest(manager, "Supabase sync failed");
        List<String> accessTokens = new ArrayList<>();

        String body = request.execute(session -> {
            accessTokens.add(session.accessToken());
            if (accessTokens.size() == 1) {
                return new SupabaseAuthenticatedRequest.Response(401, "{\"message\":\"JWT expired\"}");
            }
            return new SupabaseAuthenticatedRequest.Response(200, "[{\"ok\":true}]");
        });

        assertEquals("[{\"ok\":true}]", body);
        assertEquals(List.of("stale-access", "fresh-access"), accessTokens);
        assertEquals(FRESH.accessToken(), sessions.load().accessToken());
        assertEquals(1, auth.refreshCount);
    }

    @Test
    public void refreshesAndRetriesWhenSupabaseReturnsJwtExpiredBodyWithUnexpectedStatus() throws Exception {
        FakeSessionRepository sessions = new FakeSessionRepository(session("stale-access", "refresh", NOW + 3_600));
        FakeAuthGateway auth = new FakeAuthGateway(FRESH);
        SupabaseSessionManager manager = new SupabaseSessionManager(sessions, auth, () -> NOW);
        SupabaseAuthenticatedRequest request = new SupabaseAuthenticatedRequest(manager, "Supabase sync failed");
        List<String> accessTokens = new ArrayList<>();

        String body = request.execute(session -> {
            accessTokens.add(session.accessToken());
            if (accessTokens.size() == 1) {
                return new SupabaseAuthenticatedRequest.Response(400, "{\"message\":\"JWT expired\"}");
            }
            return new SupabaseAuthenticatedRequest.Response(200, "[{\"ok\":true}]");
        });

        assertEquals("[{\"ok\":true}]", body);
        assertEquals(List.of("stale-access", "fresh-access"), accessTokens);
        assertEquals(1, auth.refreshCount);
    }

    @Test
    public void usesAlreadyRenewedSessionWhenAnotherRequestRefreshedFirst() throws Exception {
        FakeSessionRepository sessions = new FakeSessionRepository(FRESH);
        FakeAuthGateway auth = new FakeAuthGateway(session("unused", "unused", NOW + 3_600));
        SupabaseSessionManager manager = new SupabaseSessionManager(sessions, auth, () -> NOW);

        AuthSession result = manager.refreshAfterRejected(session("stale-access", "refresh", NOW + 3_600));

        assertEquals(FRESH.accessToken(), result.accessToken());
        assertEquals(0, auth.refreshCount);
    }

    private static AuthSession session(String accessToken, String refreshToken, long expiresAt) {
        return new AuthSession(accessToken, refreshToken, "user-1", "user@example.test", expiresAt, "group-1", "member");
    }

    private static final class FakeSessionRepository implements AuthSessionRepository {
        private AuthSession session;
        private AuthSession saved;
        private boolean cleared;

        private FakeSessionRepository(AuthSession session) {
            this.session = session;
        }

        @Override
        public AuthSession load() {
            return session;
        }

        @Override
        public void save(AuthSession session) {
            this.session = session;
            this.saved = session;
        }

        @Override
        public void clear() {
            this.session = new AuthSession("", "", "", "", 0, "", "");
            this.cleared = true;
        }
    }

    private static final class FakeAuthGateway implements SupabaseAuthGateway {
        private final AuthSession refreshedSession;
        private final IOException failure;
        private int refreshCount;

        private FakeAuthGateway(AuthSession refreshedSession) {
            this.refreshedSession = refreshedSession;
            this.failure = null;
        }

        private FakeAuthGateway(IOException failure) {
            this.refreshedSession = null;
            this.failure = failure;
        }

        @Override
        public AuthSession refresh(AuthSession session) throws IOException {
            refreshCount++;
            if (failure != null) {
                throw failure;
            }
            return refreshedSession;
        }
    }
}

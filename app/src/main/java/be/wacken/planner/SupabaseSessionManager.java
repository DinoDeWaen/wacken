package be.wacken.planner;

import java.io.IOException;
import java.util.function.LongSupplier;

final class SupabaseSessionManager {
    private static final long REFRESH_SKEW_SECONDS = 60;

    private final AuthSessionRepository sessions;
    private final SupabaseAuthGateway auth;
    private final LongSupplier nowEpochSeconds;

    SupabaseSessionManager(AuthSessionRepository sessions, SupabaseAuthGateway auth) {
        this(sessions, auth, () -> System.currentTimeMillis() / 1000L);
    }

    SupabaseSessionManager(AuthSessionRepository sessions, SupabaseAuthGateway auth, LongSupplier nowEpochSeconds) {
        this.sessions = sessions;
        this.auth = auth;
        this.nowEpochSeconds = nowEpochSeconds;
    }

    synchronized AuthSession requireFreshSession() throws IOException {
        AuthSession session = sessions.load();
        if (!session.isPresent()) {
            throw new AuthenticationRequiredException("Please sign in again.");
        }
        if (session.expiresAtOrBefore(nowEpochSeconds.getAsLong() + REFRESH_SKEW_SECONDS)) {
            return refresh(session);
        }
        return session;
    }

    synchronized AuthSession refreshAfterRejected(AuthSession rejectedSession) throws IOException {
        AuthSession currentSession = sessions.load();
        if (currentSession.isPresent() && !currentSession.accessToken().equals(rejectedSession.accessToken())) {
            return currentSession;
        }
        return refresh(currentSession.isPresent() ? currentSession : rejectedSession);
    }

    private AuthSession refresh(AuthSession session) throws IOException {
        try {
            AuthSession refreshed = auth.refresh(session);
            sessions.save(refreshed);
            return refreshed;
        } catch (IOException error) {
            sessions.clear();
            throw new AuthenticationRequiredException("Supabase session expired. Please sign in again.", error);
        }
    }
}

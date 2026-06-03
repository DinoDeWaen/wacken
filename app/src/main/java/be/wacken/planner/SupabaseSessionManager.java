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
            SupabaseDiagnostics.warn("auth", "session_missing", "stored_session=false", null);
            throw new AuthenticationRequiredException("Please sign in again.");
        }
        long refreshThreshold = nowEpochSeconds.getAsLong() + REFRESH_SKEW_SECONDS;
        if (session.expiresAtOrBefore(refreshThreshold)) {
            SupabaseDiagnostics.info(
                    "auth",
                    "session_near_expiry",
                    "expires_at=" + session.expiresAtEpochSeconds() + " refresh_threshold=" + refreshThreshold
            );
            return refresh(session);
        }
        SupabaseDiagnostics.info(
                "auth",
                "session_fresh",
                "expires_at=" + session.expiresAtEpochSeconds() + " refresh_threshold=" + refreshThreshold
        );
        return session;
    }

    synchronized AuthSession refreshAfterRejected(AuthSession rejectedSession) throws IOException {
        AuthSession currentSession = sessions.load();
        if (currentSession.isPresent() && !currentSession.accessToken().equals(rejectedSession.accessToken())) {
            SupabaseDiagnostics.info("auth", "session_already_refreshed", "current_session_present=true");
            return currentSession;
        }
        SupabaseDiagnostics.info("auth", "session_rejected_refresh_required", "current_session_present=" + currentSession.isPresent());
        return refresh(currentSession.isPresent() ? currentSession : rejectedSession);
    }

    private AuthSession refresh(AuthSession session) throws IOException {
        try {
            SupabaseDiagnostics.info(
                    "auth",
                    "refresh_start",
                    "session_present=" + session.isPresent() + " expires_at=" + session.expiresAtEpochSeconds()
            );
            AuthSession refreshed = auth.refresh(session);
            sessions.save(refreshed);
            SupabaseDiagnostics.info("auth", "refresh_success", "expires_at=" + refreshed.expiresAtEpochSeconds());
            return refreshed;
        } catch (IOException error) {
            sessions.clear();
            SupabaseDiagnostics.warn("auth", "refresh_failed_session_cleared", "session_cleared=true", error);
            throw new AuthenticationRequiredException("Supabase session expired. Please sign in again.", error);
        }
    }
}

package be.wacken.planner;

import android.content.Context;
import android.content.SharedPreferences;

final class AuthSessionStore {
    private static final String FILE_NAME = "supabase-session";
    private static final String ACCESS_TOKEN = "accessToken";
    private static final String REFRESH_TOKEN = "refreshToken";
    private static final String USER_ID = "userId";
    private static final String EMAIL = "email";
    private static final String EXPIRES_AT = "expiresAt";
    private static final String GROUP_ID = "groupId";
    private static final String GROUP_ROLE = "groupRole";

    private final SharedPreferences preferences;

    AuthSessionStore(Context context) {
        this.preferences = context.getApplicationContext().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
    }

    AuthSession load() {
        return new AuthSession(
                preferences.getString(ACCESS_TOKEN, ""),
                preferences.getString(REFRESH_TOKEN, ""),
                preferences.getString(USER_ID, ""),
                preferences.getString(EMAIL, ""),
                preferences.getLong(EXPIRES_AT, 0),
                preferences.getString(GROUP_ID, ""),
                preferences.getString(GROUP_ROLE, "")
        );
    }

    void save(AuthSession session) {
        preferences.edit()
                .putString(ACCESS_TOKEN, session.accessToken())
                .putString(REFRESH_TOKEN, session.refreshToken())
                .putString(USER_ID, session.userId())
                .putString(EMAIL, session.email())
                .putLong(EXPIRES_AT, session.expiresAtEpochSeconds())
                .putString(GROUP_ID, session.groupId())
                .putString(GROUP_ROLE, session.groupRole())
                .apply();
    }

    void clear() {
        preferences.edit().clear().apply();
    }
}

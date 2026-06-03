package be.wacken.planner;

final class AuthSession {
    private final String accessToken;
    private final String refreshToken;
    private final String userId;
    private final String email;
    private final long expiresAtEpochSeconds;
    private final String groupId;
    private final String groupRole;

    AuthSession(
            String accessToken,
            String refreshToken,
            String userId,
            String email,
            long expiresAtEpochSeconds,
            String groupId,
            String groupRole
    ) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.userId = userId;
        this.email = email;
        this.expiresAtEpochSeconds = expiresAtEpochSeconds;
        this.groupId = groupId;
        this.groupRole = groupRole;
    }

    String accessToken() {
        return accessToken;
    }

    String refreshToken() {
        return refreshToken;
    }

    String userId() {
        return userId;
    }

    String email() {
        return email;
    }

    long expiresAtEpochSeconds() {
        return expiresAtEpochSeconds;
    }

    String groupId() {
        return groupId;
    }

    String groupRole() {
        return groupRole;
    }

    boolean isPresent() {
        return accessToken != null && !accessToken.isBlank()
                && userId != null && !userId.isBlank()
                && groupId != null && !groupId.isBlank();
    }

    boolean expiresAtOrBefore(long epochSeconds) {
        return expiresAtEpochSeconds <= epochSeconds;
    }
}

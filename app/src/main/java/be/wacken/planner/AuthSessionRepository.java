package be.wacken.planner;

interface AuthSessionRepository {
    AuthSession load();

    void save(AuthSession session);

    void clear();
}

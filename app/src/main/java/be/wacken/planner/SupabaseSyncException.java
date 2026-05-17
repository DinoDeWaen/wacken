package be.wacken.planner;

final class SupabaseSyncException extends RuntimeException {
    SupabaseSyncException(String message, Throwable cause) {
        super(message, cause);
    }
}

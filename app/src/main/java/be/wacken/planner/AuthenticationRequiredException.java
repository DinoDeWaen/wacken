package be.wacken.planner;

import java.io.IOException;

final class AuthenticationRequiredException extends IOException {
    AuthenticationRequiredException(String message) {
        super(message);
    }

    AuthenticationRequiredException(String message, Throwable cause) {
        super(message, cause);
    }
}

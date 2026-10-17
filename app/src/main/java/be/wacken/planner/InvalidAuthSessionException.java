package be.wacken.planner;

import java.io.IOException;

final class InvalidAuthSessionException extends IOException {
    InvalidAuthSessionException(String message) {
        super(message);
    }
}

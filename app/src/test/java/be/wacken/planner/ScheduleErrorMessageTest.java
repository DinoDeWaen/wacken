package be.wacken.planner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public final class ScheduleErrorMessageTest {
    @Test
    public void noMessageExceptionDoesNotRenderRawNull() {
        String message = ScheduleErrorMessage.generationFailure(new NullPointerException());

        assertFalse(message.endsWith(": null"));
        assertFalse(message.contains("generated: null"));
        assertTrue(message.contains("Unexpected missing schedule data."));
        assertTrue(message.contains("Please sync from Supabase and try again."));
    }

    @Test
    public void usefulExceptionMessageIsPreserved() {
        String message = ScheduleErrorMessage.generationFailure(new IllegalStateException("Ratings cache unavailable."));

        assertTrue(message.contains("Ratings cache unavailable."));
    }

    @Test
    public void lockLoadFailureKeepsScheduleRecoveryClear() {
        String message = ScheduleErrorMessage.lockLoadFailure(new NullPointerException());

        assertTrue(message.contains("Locked schedule choices could not be synced."));
        assertFalse(message.contains("null"));
    }

    @Test
    public void networkOnMainThreadExceptionIsNotShownAsTechnicalUiText() {
        RuntimeException error = new RuntimeException() {
            @Override
            public String getMessage() {
                return null;
            }

            @Override
            public String toString() {
                return "android.os.NetworkOnMainThreadException";
            }
        };

        String message = ScheduleErrorMessage.lockLoadFailure(new android.os.NetworkOnMainThreadException());

        assertTrue(message.contains("Generated schedule is shown"));
        assertFalse(message.contains("NetworkOnMainThreadException"));
        assertFalse(ScheduleErrorMessage.lockLoadFailure(error).contains("null"));
    }

}

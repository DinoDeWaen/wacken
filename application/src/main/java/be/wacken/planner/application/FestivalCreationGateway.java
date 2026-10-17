package be.wacken.planner.application;

import java.util.List;
import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.Festival;

/** Persists a complete group-wide creation atomically, including exact-name band reuse
 * and each existing member's established personal-band-rating snapshot. Only the
 * creator's own planning snapshot is returned. Repeating the same command
 * returns the committed creation without overwriting later edits or history. */
@FunctionalInterface
public interface FestivalCreationGateway {
    AddFestivalResult create(String groupId, String userName, Festival festival, List<Band> bands);

    default AddFestivalResult createReviewed(String groupId, String userName, Festival festival,
            List<be.wacken.planner.domain.ReviewedFestivalBand> bands) {
        return AddFestivalResult.failure("Reviewed creation is unavailable. Update the app and database before retrying.");
    }
}

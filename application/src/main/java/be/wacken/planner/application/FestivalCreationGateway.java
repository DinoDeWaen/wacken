package be.wacken.planner.application;

import java.util.List;
import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.Festival;

/** Persists a complete group-wide creation atomically, including exact-name band reuse
 * and the creating user's latest personal-rating prefill. Repeating the same command
 * returns the committed creation without overwriting later edits or history. */
@FunctionalInterface
public interface FestivalCreationGateway {
    AddFestivalResult create(String groupId, String userName, Festival festival, List<Band> bands);
}

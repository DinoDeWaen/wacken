package be.wacken.planner.application;

import be.wacken.planner.domain.Festival;

/** Atomically archives and promotes each member's final nonempty after-ratings,
 * then publishes the acknowledged local view. Failures preserve pending work. */
@FunctionalInterface
public interface FestivalArchiveGateway {
    void archive(Festival festival);
}

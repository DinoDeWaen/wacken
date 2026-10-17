package be.wacken.planner.domain;

import java.util.List;
import java.util.Optional;

public interface PersonalBandRatingHistoryRepository {
    void save(PersonalBandRatingEvent event);

    List<PersonalBandRatingEvent> findByUserAndBand(String userName, Band band);

    /** All cached events owned by this member, including detached/deleted festival history. */
    default List<PersonalBandRatingEvent> findByUser(String userName) {
        return List.of();
    }

    default List<PersonalBandRatingEvent> findByUserAndFestival(String userName, String festivalId) {
        return List.of();
    }

    default Optional<PersonalBandRatingEvent> latestByUserAndBand(String userName, Band band) {
        return findByUserAndBand(userName, band)
                .stream()
                .max(java.util.Comparator.comparing(PersonalBandRatingEvent::createdAt));
    }
}

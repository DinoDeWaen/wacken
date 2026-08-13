package be.wacken.planner.domain;

import java.util.List;
import java.util.Optional;

public interface PersonalBandRatingHistoryRepository {
    void save(PersonalBandRatingEvent event);

    List<PersonalBandRatingEvent> findByUserAndBand(String userName, Band band);

    default Optional<PersonalBandRatingEvent> latestByUserAndBand(String userName, Band band) {
        return findByUserAndBand(userName, band)
                .stream()
                .max(java.util.Comparator.comparing(PersonalBandRatingEvent::createdAt));
    }
}

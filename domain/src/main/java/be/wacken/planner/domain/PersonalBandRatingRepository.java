package be.wacken.planner.domain;

import java.util.List;
import java.util.Optional;

/** Read established preferences; promotion writes belong to the archive/correction transaction. */
public interface PersonalBandRatingRepository {
    Optional<PersonalBandRating> findByUserAndBand(String userName, Band band);
    List<PersonalBandRating> findByUser(String userName);
}


package be.wacken.planner;

import java.util.List;

import be.wacken.planner.domain.PersonalBandRatingEvent;
import be.wacken.planner.domain.PersonalBandRatingHistoryRepository;

public interface PersonalBandRatingSyncLocalStore extends PersonalBandRatingHistoryRepository {
    void saveSynced(PersonalBandRatingEvent event);

    List<PersonalBandRatingEvent> findPending(String userName);
}

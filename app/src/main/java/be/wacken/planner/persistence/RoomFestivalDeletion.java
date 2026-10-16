package be.wacken.planner.persistence;

import be.wacken.planner.domain.Festival;

public final class RoomFestivalDeletion {
    private final WackenDatabase database;

    public RoomFestivalDeletion(WackenDatabase database) {
        this.database = database;
    }

    public void delete(Festival festival) {
        database.runInTransaction(() -> {
            String id = festival.id();
            database.personalBandRatingEvents().detachFestival(id, festival.name());
            database.festivalPlanningRatings().deleteByFestival(id);
            database.festivalLineups().deleteByFestival(id);
            database.performances().deleteByFestival(id);
            database.scheduleLocks().deleteByFestival(id);
            database.festivals().deleteById(id);
            database.festivalCacheMarker().markInitialized(new RoomFestivalCacheMarker(1));
        });
    }
}

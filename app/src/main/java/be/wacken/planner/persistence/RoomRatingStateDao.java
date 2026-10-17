package be.wacken.planner.persistence;

import androidx.room.*;
import java.util.List;

@Dao
public interface RoomRatingStateDao {
    @Insert(onConflict=OnConflictStrategy.REPLACE) void saveAfter(RoomFestivalAfterRating rating);
    @Insert(onConflict=OnConflictStrategy.REPLACE) void saveCurrent(RoomPersonalBandRating rating);
    @Query("SELECT * FROM festival_after_ratings WHERE userName=:user AND festivalId=:festival AND bandName=:band")
    RoomFestivalAfterRating after(String user,String festival,String band);
    @Query("SELECT * FROM festival_after_ratings WHERE userName=:user")
    List<RoomFestivalAfterRating> afterByUser(String user);
    @Query("SELECT * FROM festival_after_ratings WHERE festivalId=:festival")
    List<RoomFestivalAfterRating> afterByFestival(String festival);
    @Query("SELECT * FROM festival_after_ratings WHERE userName=:user AND syncStatus='PENDING'")
    List<RoomFestivalAfterRating> pending(String user);
    @Query("UPDATE festival_after_ratings SET syncStatus='SYNCED' WHERE userName=:user AND festivalId=:festival AND bandName=:band AND operationId=:operation")
    void acknowledge(String user,String festival,String band,String operation);
    @Query("DELETE FROM festival_after_ratings WHERE userName=:user AND syncStatus='SYNCED'")
    void clearSyncedAfter(String user);
    @Query("DELETE FROM festival_after_ratings WHERE festivalId=:festival")
    void deleteAfterFestival(String festival);
    @Query("SELECT * FROM personal_band_ratings WHERE userName=:user AND bandName=:band")
    RoomPersonalBandRating current(String user,String band);
    @Query("SELECT * FROM personal_band_ratings WHERE userName=:user")
    List<RoomPersonalBandRating> currentByUser(String user);
    @Query("DELETE FROM personal_band_ratings WHERE userName=:user")
    void clearCurrent(String user);
    @Query("SELECT EXISTS(SELECT 1 FROM personal_band_rating_events e WHERE e.bandName=:band AND "
            + "(e.createdAt<='1970-01-01T00:00:00Z' OR e.festivalId IS NULL)) OR EXISTS("
            + "SELECT 1 FROM real_ratings r WHERE r.bandName=:band AND NOT EXISTS("
            + "SELECT 1 FROM personal_band_rating_events e WHERE e.userName=r.userName AND e.bandName=r.bandName "
            + "AND e.createdAt>'1970-01-01T00:00:00Z' AND e.festivalId IS NOT NULL))")
    boolean hasUnresolvedLegacy(String band);
}

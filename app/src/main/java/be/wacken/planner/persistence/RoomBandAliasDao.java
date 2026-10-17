package be.wacken.planner.persistence;

import androidx.room.*;
import java.util.List;
@Dao
public interface RoomBandAliasDao {
    @Query("SELECT * FROM band_aliases ORDER BY aliasKey") List<RoomBandAlias> findAll();
    @Insert(onConflict=OnConflictStrategy.REPLACE) void save(RoomBandAlias alias);
    @Query("DELETE FROM band_aliases") void clear();
}

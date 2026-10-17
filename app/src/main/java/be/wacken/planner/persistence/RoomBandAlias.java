package be.wacken.planner.persistence;

import androidx.annotation.NonNull;
import androidx.room.*;

@Entity(tableName="band_aliases")
public final class RoomBandAlias {
    @PrimaryKey @NonNull public String aliasKey;
    @NonNull public String aliasName;
    @NonNull public String bandId;
    @NonNull public String bandName;
    @NonNull public String confirmedBy;
    @NonNull public String confirmedAt;
    public RoomBandAlias(@NonNull String aliasKey,@NonNull String aliasName,@NonNull String bandId,
                         @NonNull String bandName,@NonNull String confirmedBy,@NonNull String confirmedAt) {
        this.aliasKey=aliasKey;this.aliasName=aliasName;this.bandId=bandId;this.bandName=bandName;
        this.confirmedBy=confirmedBy;this.confirmedAt=confirmedAt;
    }
}

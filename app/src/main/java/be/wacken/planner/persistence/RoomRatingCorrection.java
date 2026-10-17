package be.wacken.planner.persistence;

import androidx.annotation.NonNull;
import androidx.room.*;

@Entity(tableName="rating_corrections",indices=@Index(value={"userName","festivalId","bandName"}))
public final class RoomRatingCorrection {
    @PrimaryKey @NonNull public String operationId;
    @NonNull public String userName;
    @NonNull public String festivalId;
    @NonNull public String festivalName;
    @NonNull public String bandName;
    @NonNull public String kind;
    public int previousValue;
    public int newValue;
    public long expectedRevision;
    public String originalRecordedAt;
    @NonNull public String correctedAt;
    public RoomRatingCorrection(@NonNull String operationId,@NonNull String userName,@NonNull String festivalId,
            @NonNull String festivalName,@NonNull String bandName,@NonNull String kind,
            int previousValue,int newValue,long expectedRevision,String originalRecordedAt,@NonNull String correctedAt) {
        this.operationId=operationId;this.userName=userName;this.festivalId=festivalId;this.festivalName=festivalName;
        this.bandName=bandName;this.kind=kind;this.previousValue=previousValue;this.newValue=newValue;
        this.expectedRevision=expectedRevision;this.originalRecordedAt=originalRecordedAt;this.correctedAt=correctedAt;
    }
}

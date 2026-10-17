package be.wacken.planner.persistence;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "bands")
public final class RoomBand {
    @PrimaryKey
    @NonNull
    public String name;
    public String biography;
    public String imageUrl;
    public String youtubeUrl;
    public String spotifyUrl;

    public RoomBand(@NonNull String name, String biography, String imageUrl, String youtubeUrl, String spotifyUrl) {
        this.name = name;
        this.biography = biography;
        this.imageUrl = imageUrl;
        this.youtubeUrl = youtubeUrl;
        this.spotifyUrl = spotifyUrl;
    }
}

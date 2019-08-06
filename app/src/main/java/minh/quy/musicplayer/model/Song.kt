package minh.quy.musicplayer.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull

@Entity(tableName = "songs")
class Song(
    @NotNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "song_id")
    var songId: Int,

    @ColumnInfo(name = "song_name")
    var songName: String,

    @ColumnInfo(name = "artist")
    var artist: String,

    @ColumnInfo(name = "duration")
    var duration: Int,

    @ColumnInfo(name = "favorite")
    var isFavorite: Boolean


) {
}
package minh.quy.musicplayer.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull

@Entity(tableName = "playlists_song")
class PlayListSong @JvmOverloads constructor(
    @ColumnInfo(name = "playlist_id")
    var playlistId: Int,

    @ColumnInfo(name="song_id")
    var songId: String,

    @NotNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "playlist_song_id")
    var id: Int? = null
)

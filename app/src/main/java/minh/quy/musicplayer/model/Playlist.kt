package minh.quy.musicplayer.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull

@Entity(tableName = "playlists")
class Playlist @JvmOverloads constructor(
    @ColumnInfo(name = "playlist_name")
    var name: String,

    @NotNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "playlist_id")
    var id: Int? = null
    ) {
    var isSelected: Boolean = false

}
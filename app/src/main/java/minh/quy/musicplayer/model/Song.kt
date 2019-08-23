package minh.quy.musicplayer.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull

class Song @JvmOverloads constructor(

    @ColumnInfo(name = "song_name")
    var songName: String,

    @ColumnInfo(name = "artist_name")
    var artistName: String,

    @ColumnInfo(name = "artist_id")
    var artistId: Long,

    @ColumnInfo(name = "album_id")
    var albumId: Long,

    @ColumnInfo(name = "album_name")
    var albumName: String,

    @ColumnInfo(name = "duration")
    var duration: Int,

    @ColumnInfo(name = "data_added")
    var dateAdded: Long,

    @ColumnInfo(name = "data")
    var data: String,

    @ColumnInfo(name = "favorite")
    var isFavorite: Int = 0,

    @ColumnInfo(name = "play_count")
    var playCount: Int = 0,

    @NotNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "song_id")
    var songId: Int? = null

) {

    var isSelected: Boolean? = null
    var avatar: String? = null
}
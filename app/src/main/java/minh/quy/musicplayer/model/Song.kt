package minh.quy.musicplayer.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull


class Song @JvmOverloads constructor(

    @ColumnInfo(name = "song_name")
    var songName: String?,

    @ColumnInfo(name = "artist_name")
    var artistName: String?,

    @ColumnInfo(name = "artist_id")
    var artistId: Long,

    @ColumnInfo(name = "album_id")
    var albumId: Long,

    @ColumnInfo(name = "album_name")
    var albumName: String?,

    @ColumnInfo(name = "duration")
    var duration: Int,

    @ColumnInfo(name = "data_added")
    var dateAdded: Long,

    @ColumnInfo(name = "data")
    var data: String?,

    @ColumnInfo(name = "favorite")
    var isFavorite: Int = 0,

    @ColumnInfo(name = "play_count")
    var playCount: Int = 0,

    @NotNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "song_id")
    var songId: Int? = null

) :Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readLong(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readValue(Int::class.java.classLoader) as? Int
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(songName)
        parcel.writeString(artistName)
        parcel.writeLong(artistId)
        parcel.writeLong(albumId)
        parcel.writeString(albumName)
        parcel.writeInt(duration)
        parcel.writeLong(dateAdded)
        parcel.writeString(data)
        parcel.writeInt(isFavorite)
        parcel.writeInt(playCount)
        parcel.writeValue(songId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Song> {
        override fun createFromParcel(parcel: Parcel): Song {
            return Song(parcel)
        }

        override fun newArray(size: Int): Array<Song?> {
            return arrayOfNulls(size)
        }
    }
}

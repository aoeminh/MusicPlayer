package minh.quy.musicplayer.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import minh.quy.musicplayer.dao.PlaylistDAO
import minh.quy.musicplayer.dao.SongDAO
import minh.quy.musicplayer.model.Playlist
import minh.quy.musicplayer.model.Song

@Database(entities = arrayOf(Playlist::class), version = 1)
abstract class MusicDatabase : RoomDatabase() {

    companion object {
        var musicDatabase: MusicDatabase? = null
        val DATABASE_NAME = "MusicDatabase"
        fun getInstanceDatabase(context: Context): MusicDatabase {
            if (musicDatabase == null) {
                musicDatabase = databaseBuilder(context, MusicDatabase::class.java, DATABASE_NAME)
                    .allowMainThreadQueries().build()
                return musicDatabase!!
            }
            return musicDatabase!!
        }
    }

    abstract fun getPlaylistDAO(): PlaylistDAO



}
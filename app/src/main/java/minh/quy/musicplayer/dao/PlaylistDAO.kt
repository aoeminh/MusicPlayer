package minh.quy.musicplayer.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import minh.quy.musicplayer.model.Playlist

@Dao
interface PlaylistDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPlaylist(playlist: Playlist): Long

    @Query("SELECT * FROM playlists")
    fun getAllPlaylist(): LiveData<MutableList<Playlist>>

    @Delete
    fun deletePlaylist(playlist: Playlist)
}
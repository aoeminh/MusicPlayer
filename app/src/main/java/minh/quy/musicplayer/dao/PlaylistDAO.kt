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

    @Query("UPDATE playlists SET playlist_name =:name WHERE playlist_id = :playlistId")
    fun updatePlaylist(name: String, playlistId: Int): Int
}
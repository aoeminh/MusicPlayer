package minh.quy.musicplayer.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import minh.quy.musicplayer.model.Playlist

@Dao
interface PlaylistDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPlaylist(playlist: Playlist): Long

    @Query("SELECT * FROM playlists")
    fun getAllPlaylist(): LiveData<List<Playlist>>

    
}
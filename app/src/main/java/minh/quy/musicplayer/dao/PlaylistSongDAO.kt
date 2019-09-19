package minh.quy.musicplayer.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import minh.quy.musicplayer.model.PlayListSong

@Dao
interface PlaylistSongDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSongIntoPlayList(playListSong: PlayListSong): Long

    @Query("SELECT * FROM playlists_song WHERE playlist_id LIKE :playlistId")
    fun getAllSongInPlaylist(playlistId: Int): PlayListSong

}
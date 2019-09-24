package minh.quy.musicplayer.contract

import minh.quy.musicplayer.database.MusicDatabase
import minh.quy.musicplayer.funtiontoolbar.FunctionToolbarPlaylist
import minh.quy.musicplayer.model.Playlist

interface IPlaylistPresenter {
    fun getAllPlaylist(musicDatabase: MusicDatabase?)
    fun inserNewPlaylist(playlist: Playlist)
    fun updatePlaylist(name: String, playlistId: Int)

}
package minh.quy.musicplayer.presenter

import android.util.Log
import minh.quy.musicplayer.contract.IPlaylistPresenter
import minh.quy.musicplayer.contract.IPlaylistView
import minh.quy.musicplayer.database.MusicDatabase
import minh.quy.musicplayer.database.MusicDatabase.Companion.musicDatabase
import minh.quy.musicplayer.model.Playlist

class PlaylistPresenter(val view: IPlaylistView) : IPlaylistPresenter {
    override fun getAllPlaylist(musicDatabase: MusicDatabase?) {
        view.onResponseAllPlaylist(musicDatabase?.getPlaylistDAO()?.getAllPlaylist())
    }

    override fun inserNewPlaylist(playlist: Playlist) {
        var resultCount = musicDatabase?.getPlaylistDAO()?.insertPlaylist(playlist)
        resultCount?.let {
            if (it > 0) {
                view.onResponseInserPlaylist(true)
            } else {
                view.onResponseInserPlaylist(false)
            }
        }
    }

    override fun updatePlaylist(name: String, playlistId: Int) {
        val result = musicDatabase?.getPlaylistDAO()?.updatePlaylist(name, playlistId)
    }
}
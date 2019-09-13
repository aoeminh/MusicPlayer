package minh.quy.musicplayer.contract

import androidx.lifecycle.LiveData
import minh.quy.musicplayer.model.Playlist

interface IPlaylistView {
    fun onResponseAllPlaylist(liveData: LiveData<MutableList<Playlist>>?)
    fun onResponseInserPlaylist(isSuccess: Boolean)
}
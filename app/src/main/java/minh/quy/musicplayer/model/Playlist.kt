package minh.quy.musicplayer.model

class Playlist(var id: Int,
               var name: String,
               var songList: MutableList<Song>) {
}
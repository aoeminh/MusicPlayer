package minh.quy.musicplayer.model

class Album @JvmOverloads constructor(
    var albummId: Long,
    var albumName: String,
    var songCount: Int,
    var songs: MutableList<Song> = arrayListOf(),
    var isSelected: Boolean = false
) {
}
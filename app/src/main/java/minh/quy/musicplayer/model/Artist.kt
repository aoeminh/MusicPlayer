package minh.quy.musicplayer.model

class Artist @JvmOverloads constructor(
    var artistId: Long,
    var artistName: String,
    var songCount: Int,
    var songs: MutableList<Song> = arrayListOf(),
    var isSelected: Boolean = false
) {
}
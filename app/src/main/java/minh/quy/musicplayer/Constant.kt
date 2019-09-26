package minh.quy.musicplayer

class Constant {

    enum class TypeListsong(var type: Int) {
        PLAYLIST(1),
        ARTIST(2),
        ALBUM(3)
    }
    companion object {
        val TIME_HIDE_SCROLL_BAR: Long = 5000
        val MAX_DEFAULT_COUNT = 7
    }
}
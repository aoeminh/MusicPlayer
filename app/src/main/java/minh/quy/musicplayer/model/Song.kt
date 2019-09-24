package minh.quy.musicplayer.model

class Song @JvmOverloads constructor(

    var songName: String?,

    var artistName: String?,

    var artistId: Long,

    var albumId: Long,

    var albumName: String?,

    var duration: Int,

    var dateAdded: Long,

    var data: String?,

    var songId: String? = null
    ,
    var isFavorite: Int = 0,

    var playCount: Int = 0,

    var isSelected: Boolean = false

)


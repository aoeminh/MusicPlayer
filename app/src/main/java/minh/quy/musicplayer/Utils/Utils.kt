package minh.quy.musicplayer.Utils

import minh.quy.musicplayer.R

object Utils {

    @JvmStatic
    fun convertSongDuration(time: Long): String? {
        if (time.toFloat() >= 3600000f) {
            val seconds = (time / 1000).toInt() % 60
            val minutes = (time / (1000 * 60) % 60).toInt()
            val hours = (time / (1000 * 60 * 60) % 24).toInt()
            return validateDate(hours, minutes, seconds)
        } else {
            val seconds = (time / 1000).toInt() % 60
            val minutes = (time / (1000 * 60) % 60).toInt()
            return validateDate(minutes, seconds)
        }
    }

    @JvmOverloads
    fun validateDate(min: Int, sec: Int, hour: Int = 0): String {
        var strHour = ""
        var strMin = ""
        var strSec = ""

        if (hour > 0 && hour < 10) {
            strHour = String.format("0%d", hour)

            if (min < 10) {
                strMin = String.format("0%d", min)
            } else {
                strMin = String.format("%d", min)
            }
        } else {
            strMin = String.format("%d", min)
            strHour = String.format("%d", hour)
        }

        if (sec < 10) {
            strSec = String.format("0%d", sec)
        } else {
            strSec = String.format("%d", sec)
        }

        if (hour != 0) {
            val stringDate = StringBuilder()
            return stringDate.append(strHour).append(":").append(strMin).append(":").append(strSec)
                .toString()
        } else {
            val stringDate = StringBuilder()
            return stringDate.append(strMin).append(":").append(strSec).toString()
        }
    }

    @JvmStatic
    fun getPositionDefaultImage(position: Int): Int {
        if (position % 7 == 0) {
            return 1
        } else {
            return position % 7 + 1
        }
    }

    @JvmStatic
    fun getDrawableIdDefaultImage(index: Int): Int {
        var drawableId = 0
        when (index) {
            1 -> {
                drawableId = R.drawable.album_art_1
            }
            2 -> {
                drawableId = R.drawable.album_art_2
            }
            3 -> {
                drawableId = R.drawable.album_art_3
            }
            4 -> {
                drawableId = R.drawable.album_art_4
            }
            5 -> {
                drawableId = R.drawable.album_art_5
            }
            6 -> {
                drawableId = R.drawable.album_art_6
            }
            7 -> {
                drawableId = R.drawable.album_art_7
            }
        }
        return drawableId
    }
}
package minh.quy.musicplayer.Utils

import kotlin.text.StringBuilder

object Utils {

    @JvmStatic
    fun convertSongDuration(time: Long): String? {
        if (time.toFloat() >= 3600000f) {
            val seconds = (time / 1000).toInt() % 60
            val minutes = (time / (1000 * 60) % 60).toInt()
            val hours = (time / (1000 * 60 * 60) % 24).toInt()
            return validateDate( hours, minutes, seconds)
        } else {
            val seconds = (time / 1000).toInt() % 60
            val minutes = (time / (1000 * 60) % 60).toInt()
            return validateDate( minutes, seconds)
        }
    }

    @JvmOverloads
    fun  validateDate( min: Int, sec: Int ,hour: Int = 0):String {
        var strHour = ""
        var strMin = ""
        var strSec = ""

        if(hour >0 && hour < 10){
            strHour = String.format("0%d", hour)

            if (min < 10) {
                strMin = String.format("0%d", min)
            } else {
                strMin = String.format("%d", min)
            }
        }else{
                strMin = String.format("%d", min)
            strHour = String.format("%d", hour)
        }

        if (sec < 10) {
            strSec = String.format("0%d", sec)
        } else {
            strSec = String.format("%d", sec)
        }

        if(hour != 0){
            val stringDate = StringBuilder()
            return stringDate.append(strHour).append(":").append(strMin).append(":").append(strSec).toString()
        }else{
            val stringDate = StringBuilder()
            return stringDate.append(strMin).append(":").append(strSec).toString()
        }
    }
}
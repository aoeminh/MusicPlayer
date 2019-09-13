package minh.quy.musicplayer.sharepreferences

import android.content.Context
import android.content.SharedPreferences

const val PREFERENT_NAME = "music.preferent"
const val EXTRA_REPEAT = "extra.repeat "
const val EXTRA_SUFFLE = "extra.suffle"
const val EXTRA_SONG_ID = "extra.song.id"

class UserPreferences() {

    companion object {
        var sharedPreferences: SharedPreferences? = null
        var instance: UserPreferences? = null

        fun getInstance(context: Context): UserPreferences? {
            if (instance == null) {
                instance = UserPreferences()

            }
            if (sharedPreferences == null) {
                sharedPreferences = context.getSharedPreferences(PREFERENT_NAME, Context.MODE_PRIVATE)
            }
            return instance
        }
    }

    fun getEdittor(): SharedPreferences.Editor? {
        return sharedPreferences?.edit()
    }

    fun saveRepeatMode(repeatMode: Int){
        getEdittor()?.putInt(EXTRA_REPEAT, repeatMode)?.commit()
    }

    fun saveSuffleMode(isSuffle: Boolean) {
        getEdittor()?.putBoolean(EXTRA_SUFFLE, isSuffle)?.commit()
    }

    fun getRepeatMode(): Int = sharedPreferences!!.getInt(EXTRA_REPEAT, 0)

    fun getSuffleMode(): Boolean = sharedPreferences!!.getBoolean(EXTRA_SUFFLE,false)

    fun saveSongId(songId: String){
        getEdittor()?.putString(EXTRA_SONG_ID,songId)?.commit()
    }

    fun getSongId(): String? = sharedPreferences?.getString(EXTRA_SONG_ID,"")

}
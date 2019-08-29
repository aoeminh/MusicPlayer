package minh.quy.musicplayer.sharepreferences

import android.content.Context
import android.content.SharedPreferences
import kotlin.coroutines.coroutineContext

class UserPreferences() {

    companion object {
        val PREFERENT_NAME = "music.preferent"
        val EXTRA_REPEAT = "extra.repeat"
        val EXTRA_SUFFLE = "extra.suffle"
        var sharedPreferences: SharedPreferences? = null
        var instance: UserPreferences? = null
        fun getInstance(context: Context): UserPreferences? {
            if (instance == null) {
                instance = UserPreferences()

            }
            if (sharedPreferences == null) {
                sharedPreferences =
                    context.getSharedPreferences(PREFERENT_NAME, Context.MODE_PRIVATE)
            }
            return instance
        }

        fun getEdittor(): SharedPreferences.Editor? {
            return sharedPreferences?.edit()
        }

    }

    fun saveRepeatMode(repeatMode: Int) {
        getEdittor()?.putInt(EXTRA_REPEAT, repeatMode)
    }

    fun saveSuffleMode(isSuffle: Boolean) {
        getEdittor()?.putBoolean(EXTRA_SUFFLE, isSuffle)
    }



}
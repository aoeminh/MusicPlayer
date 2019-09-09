package minh.quy.musicplayer.activity

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentManager
import layout.HomeFragment
import minh.quy.musicplayer.R
import minh.quy.musicplayer.adapter.BottomNavigationAdapter
import minh.quy.musicplayer.fragment.PlaySongFragment
import minh.quy.musicplayer.funtiontoolbar.FunctionToolbarPlaylist
import minh.quy.musicplayer.sharepreferences.UserPreferences

class MainActivity : BaseActivity() {


    enum class PositionNavigation(val position: Int) {
        PLAYLIST(0),
        SONGS(1),
        ARTIST(2),
        ALBUM(3),
        FOLDER(4),
    }

    var fragmentManager: FragmentManager = supportFragmentManager
    var isRepeatOne = false
    var isRepeatAll = true
    var isSuffle = false
    var currenRepeat = 0

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getRepeatAndSuffleMode()
        addHomeFragment()
        Log.d("minhnh", "onCreate")
    }

    override fun onBackPressed() {
        val count = fragmentManager.backStackEntryCount
        if (count > 0) {
            fragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        saveRepeatAndSuffleMode()
    }

    fun addHomeFragment() {
        val transaction = fragmentManager.beginTransaction()
        val homeFragment = HomeFragment()
        transaction.replace(R.id.frame_main, homeFragment, null)
        transaction.commit()
    }

    fun saveRepeatAndSuffleMode() {
        val userPreferences = UserPreferences.getInstance(applicationContext)
        userPreferences?.saveRepeatMode(currenRepeat)
        userPreferences?.saveSuffleMode(isSuffle)
    }

    fun getRepeatAndSuffleMode() {
        val userPreferences = UserPreferences.getInstance(applicationContext)
        currenRepeat = userPreferences!!.getRepeatMode()
        isSuffle = userPreferences.getSuffleMode()
        setRepeatMode()
    }

    fun setRepeatMode() {
        if (currenRepeat == PlaySongFragment.Repeat.REPEAT_ONE.value) {
            isRepeatOne = true
        } else if (currenRepeat == PlaySongFragment.Repeat.REPEAT_ALL.value) {
            isRepeatAll = true
        } else {
            isRepeatOne = false
            isRepeatAll = false
        }
    }

}

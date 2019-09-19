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
import android.content.Intent
import minh.quy.musicplayer.model.Playlist


class MainActivity : BaseActivity() {


    enum class PositionNavigation(val position: Int) {
        PLAYLIST(0),
        SONGS(1),
        ARTIST(2),
        ALBUM(3),
        FOLDER(4),
    }

    var fragmentManager: FragmentManager = supportFragmentManager
    var isFirstPlay = true
    var playlists: MutableList<Playlist> = arrayListOf()

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addHomeFragment()
    }

    override fun onBackPressed() {
        val count = fragmentManager.backStackEntryCount
        if (count > 0) {
            fragmentManager.popBackStack()
        } else {
            val homeIntent = Intent(Intent.ACTION_MAIN)
            homeIntent.addCategory(Intent.CATEGORY_HOME)
            homeIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(homeIntent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        saveSongDuration()

    }

    fun addHomeFragment() {
        val transaction = fragmentManager.beginTransaction()
        val homeFragment = HomeFragment()
        transaction.replace(R.id.frame_main, homeFragment, null)
        transaction.commit()
    }

    fun saveSongDuration() {
        val userPreferences = UserPreferences.getInstance(applicationContext)
        if (musicService?.mediaPlayer?.duration!! < 0) {
            userPreferences?.saveSongDuration(0)
        } else {
            userPreferences?.saveSongDuration(musicService?.mediaPlayer?.currentPosition!!.toLong())
        }
    }

}

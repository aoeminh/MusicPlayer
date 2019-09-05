package minh.quy.musicplayer.activity

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.FragmentManager
import com.google.android.material.navigation.NavigationView

import layout.HomeFragment
import minh.quy.musicplayer.R
import minh.quy.musicplayer.Utils.RequestPermission
import minh.quy.musicplayer.adapter.BottomNavigationAdapter
import minh.quy.musicplayer.fragment.*
import minh.quy.musicplayer.funtiontoolbar.FunctionToolbarPlaylist
import minh.quy.musicplayer.sharepreferences.UserPreferences
import java.lang.ref.WeakReference

class MainActivity : BaseActivity() {


    enum class PositionNavigation(val position: Int) {
        PLAYLIST(0),
        SONGS(1),
        ARTIST(2),
        ALBUM(3),
        FOLDER(4),
    }


    var tabSelected: Int = 0
    var bottomNavigationAdapter: BottomNavigationAdapter? = null
    var functionToolbarPlaylist: FunctionToolbarPlaylist? = null
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
        addHomeFragment()
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
        val userPreferences = UserPreferences.getInstance(applicationContext)
        userPreferences?.saveRepeatMode(currenRepeat)
        userPreferences?.saveSuffleMode(isSuffle)
    }

    fun addHomeFragment() {
        val transaction = fragmentManager.beginTransaction()
        val homeFragment = HomeFragment()
        transaction.add(R.id.frame_main, homeFragment, null)
        transaction.commit()
    }

    fun getRepeatAndSuffleMode() {

    }

}

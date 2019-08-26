package layout

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_main_content.*
import minh.quy.musicplayer.activity.MainActivity
import minh.quy.musicplayer.adapter.BottomNavigationAdapter
import minh.quy.musicplayer.fragment.*
import minh.quy.musicplayer.funtiontoolbar.FunctionToolbarPlaylist
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment(), NavigationView.OnNavigationItemSelectedListener {

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
    var mainActivity: MainActivity? = null
    var mContext: Context? = null
    var homeFragment: HomeFragment? = null

    override fun onAttachFragment(childFragment: Fragment?) {
        super.onAttachFragment(childFragment)
        mContext = context
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(activity is MainActivity){
            mainActivity = activity as MainActivity
        }

        if (parentFragment is HomeFragment){
            homeFragment = parentFragment as HomeFragment
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        val view1 = inflater.inflate(minh.quy.musicplayer.R.layout.fragment_home, container, false)
        return view1
    }

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setItemIconTintList()
        initViewPager()
        addTablayoutAction()
        setToolbar()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        when (tabSelected) {
            MainActivity.PositionNavigation.PLAYLIST.position -> {
                toolbar_main.menu.clear()
                toolbar_main.inflateMenu(minh.quy.musicplayer.R.menu.menu_toolbar_playlist)
            }
            MainActivity.PositionNavigation.SONGS.position -> {
                toolbar_main.menu.clear()
                toolbar_main.inflateMenu(minh.quy.musicplayer.R.menu.menu_toolbat_songs)
            }
            MainActivity.PositionNavigation.ARTIST.position, MainActivity.PositionNavigation.ALBUM.position -> {
                toolbar_main.menu.clear()
                toolbar_main.inflateMenu(minh.quy.musicplayer.R.menu.menu_toolbar_artist_and_album)
            }
            MainActivity.PositionNavigation.FOLDER.position -> {
                toolbar_main.menu.clear()
                toolbar_main.inflateMenu(minh.quy.musicplayer.R.menu.menu_toolbar_folder)
            }
        }

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            minh.quy.musicplayer.R.id.item_search_toolbar -> {
                Toast.makeText(activity, "Search", Toast.LENGTH_SHORT).show()
            }
            minh.quy.musicplayer.R.id.item_voice_toolbar -> {
                Toast.makeText(activity, "Voice", Toast.LENGTH_SHORT).show()
            }
            minh.quy.musicplayer.R.id.item_create_playlist -> {
                functionToolbarPlaylist?.createNewPlaylist()
            }
            minh.quy.musicplayer.R.id.item_equalizer -> {
                Toast.makeText(activity, "Equalizer", Toast.LENGTH_SHORT).show()
            }
            minh.quy.musicplayer.R.id.item_sort -> {
                Toast.makeText(activity, "Sort", Toast.LENGTH_SHORT).show()
            }
            minh.quy.musicplayer.R.id.item_sufftle -> {
                Toast.makeText(activity, "Sufftle", Toast.LENGTH_SHORT).show()
            }
            minh.quy.musicplayer.R.id.item_add_to_home_screen -> {
                Toast.makeText(activity, "Add", Toast.LENGTH_SHORT).show()
            }


        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        drawLayout.closeDrawer(GravityCompat.START)
        return true
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun setToolbar() {
        val activity = activity as AppCompatActivity?

        activity?.setSupportActionBar(toolbar_main)
        toolbar_main.getOverflowIcon()?.setColorFilter(
            activity!!.getColor(minh.quy.musicplayer.R.color.color_toolbar),
            PorterDuff.Mode.SRC_ATOP
        );
        toolbar_main.setNavigationIcon(minh.quy.musicplayer.R.drawable.ic_menu_white_24dp)
        activity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity?.supportActionBar?.setHomeButtonEnabled(true)
        activity?.supportActionBar?.setDisplayShowTitleEnabled(false);

        val toggle = ActionBarDrawerToggle(
            activity,
            drawLayout,
            toolbar_main,
            minh.quy.musicplayer.R.string.navigation_drawer_open,
            minh.quy.musicplayer.R.string.navigation_drawer_close
        )
        drawLayout.addDrawerListener(toggle)
        toggle.syncState()

        navigation_main.setNavigationItemSelectedListener(this)
    }

    fun setItemIconTintList() {
        navigation_main.itemIconTintList = null
    }

    fun initViewPager() {
        bottomNavigationAdapter = BottomNavigationAdapter(activity!!.supportFragmentManager)
        bottomNavigationAdapter?.addFragment(
            PlaylistFragment(),
            resources.getString(minh.quy.musicplayer.R.string.title_playlist)
        )
        bottomNavigationAdapter?.addFragment(
            SongsFragment(),
            resources.getString(minh.quy.musicplayer.R.string.title_songs)
        )
        bottomNavigationAdapter?.addFragment(
            ArtistFragment(),
            resources.getString(minh.quy.musicplayer.R.string.title_artist)
        )
        bottomNavigationAdapter?.addFragment(
            AlbumFragment(),
            resources.getString(minh.quy.musicplayer.R.string.title_album)
        )
        bottomNavigationAdapter?.addFragment(
            FolderFragment(),
            resources.getString(minh.quy.musicplayer.R.string.title_folder)
        )
        view_pager_main.adapter = bottomNavigationAdapter
        tablayout_main.setupWithViewPager(view_pager_main)
        setIconTablayout()
    }

    @SuppressLint("ResourceType")
    fun setIconTablayout() {
        tablayout_main.setSelectedTabIndicatorColor(Color.TRANSPARENT);
        tablayout_main.getTabAt(0)?.setIcon(
            ContextCompat.getDrawable(
                activity!!,
                minh.quy.musicplayer.R.drawable.playlist_selected_home
            )
        )
        tablayout_main.setTabTextColors(
            Color.parseColor(getResources().getString(minh.quy.musicplayer.R.color.color_text)),
            Color.parseColor(getResources().getString(minh.quy.musicplayer.R.color.playlist_tab_selected))
        )
        tablayout_main.getTabAt(1)?.setIcon(
            ContextCompat.getDrawable(
                activity!!,
                minh.quy.musicplayer.R.drawable.songs_unselected
            )
        )
        tablayout_main.getTabAt(2)?.setIcon(
            ContextCompat.getDrawable(
                activity!!,
                minh.quy.musicplayer.R.drawable.artist_unselected
            )
        )
        tablayout_main.getTabAt(3)?.setIcon(
            ContextCompat.getDrawable(
                activity!!,
                minh.quy.musicplayer.R.drawable.album_unselected
            )
        )
        tablayout_main.getTabAt(4)?.setIcon(
            ContextCompat.getDrawable(
                activity!!,
                minh.quy.musicplayer.R.drawable.folder_unselected
            )
        )
    }

    fun addTablayoutAction() {
        tablayout_main.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                setTabUnSelected(tab)
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                setTabSelected(tab)
            }
        })
    }


    @SuppressLint("ResourceType")
    fun setTabSelected(tab: TabLayout.Tab?) {
        val position = tab?.position
        when (position) {
            MainActivity.PositionNavigation.PLAYLIST.position -> {
                tabSelected = MainActivity.PositionNavigation.PLAYLIST.position
                tab.setIcon(minh.quy.musicplayer.R.drawable.playlist_selected_home)
                tablayout_main.setTabTextColors(
                    Color.parseColor(getResources().getString(minh.quy.musicplayer.R.color.color_text)),
                    Color.parseColor(getResources().getString(minh.quy.musicplayer.R.color.playlist_tab_selected))
                )

            }

            MainActivity.PositionNavigation.SONGS.position -> {
                tabSelected = MainActivity.PositionNavigation.SONGS.position
                toolbar_main.inflateMenu(minh.quy.musicplayer.R.menu.menu_toolbat_songs)
                tab.setIcon(minh.quy.musicplayer.R.drawable.songs_selected_home)
                tablayout_main.setTabTextColors(
                    Color.parseColor(getResources().getString(minh.quy.musicplayer.R.color.color_text)),
                    Color.parseColor(getResources().getString(minh.quy.musicplayer.R.color.songs_tab_selected))
                )
            }

            MainActivity.PositionNavigation.ARTIST.position -> {
                tabSelected = MainActivity.PositionNavigation.ARTIST.position
                tab.setIcon(minh.quy.musicplayer.R.drawable.artist_selected_home)
                tablayout_main.setTabTextColors(
                    Color.parseColor(getResources().getString(minh.quy.musicplayer.R.color.color_text)),
                    Color.parseColor(getResources().getString(minh.quy.musicplayer.R.color.artist_tab_selected))
                )
            }

            MainActivity.PositionNavigation.ALBUM.position -> {
                tabSelected = MainActivity.PositionNavigation.ALBUM.position
                tab.setIcon(minh.quy.musicplayer.R.drawable.album_selected_home)
                tablayout_main.setTabTextColors(
                    Color.parseColor(getResources().getString(minh.quy.musicplayer.R.color.color_text)),
                    Color.parseColor(getResources().getString(minh.quy.musicplayer.R.color.album_tab_selected))
                )
            }

            MainActivity.PositionNavigation.FOLDER.position -> {
                tabSelected = MainActivity.PositionNavigation.FOLDER.position
                tab.setIcon(minh.quy.musicplayer.R.drawable.folder_selected_home)
                tablayout_main.setTabTextColors(
                    Color.parseColor(getResources().getString(minh.quy.musicplayer.R.color.color_text)),
                    Color.parseColor(getResources().getString(minh.quy.musicplayer.R.color.folder_tab_selected))
                )
            }
        }
    }

    fun setTabUnSelected(tab: TabLayout.Tab?) {
        val position = tab?.position
        when (position) {
            PositionNavigation.PLAYLIST.position -> {
                tab.setIcon(minh.quy.musicplayer.R.drawable.playlist_unselected)
            }

            PositionNavigation.SONGS.position -> {
                tab.setIcon(minh.quy.musicplayer.R.drawable.songs_unselected)
            }

            PositionNavigation.ARTIST.position -> {
                tab.setIcon(minh.quy.musicplayer.R.drawable.artist_unselected)
            }

            PositionNavigation.ALBUM.position -> {
                tab.setIcon(minh.quy.musicplayer.R.drawable.album_unselected)
            }

            PositionNavigation.FOLDER.position -> {
                tab.setIcon(minh.quy.musicplayer.R.drawable.folder_unselected)
            }
        }
    }

    fun setFunctionPlaylist(functionToolbarPlaylist: FunctionToolbarPlaylist) {
        this.functionToolbarPlaylist = functionToolbarPlaylist
    }

}
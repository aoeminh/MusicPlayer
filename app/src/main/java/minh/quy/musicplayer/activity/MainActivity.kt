package minh.quy.musicplayer.activity

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_main_content.*
import minh.quy.musicplayer.R
import minh.quy.musicplayer.adapter.BottomNavigationAdapter
import minh.quy.musicplayer.fragment.*

class MainActivity : AppCompatActivity() {
    enum class PositionNavigation(val position: Int) {
        PLAYLIST(0),
        SONGS(1),
        ARTIST(2),
        ALBUM(3),
        FOLDER(4),
    }

    var tabSelected: Int = 0


    var bottomNavigationAdapter: BottomNavigationAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setItemIconTintList()
        initViewPager()
        addTablayoutAction()
        setToolbar()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        when (tabSelected) {
            PositionNavigation.PLAYLIST.position -> {
                toolbar_main.menu.clear()
                toolbar_main.inflateMenu(R.menu.menu_toolbar_playlist)
            }
            PositionNavigation.SONGS.position -> {
                toolbar_main.menu.clear()
                toolbar_main.inflateMenu(R.menu.menu_toolbat_songs)
            }
            PositionNavigation.ARTIST.position, PositionNavigation.ALBUM.position -> {
                toolbar_main.menu.clear()
                toolbar_main.inflateMenu(R.menu.menu_toolbar_artist_and_album)
            }
            PositionNavigation.FOLDER.position -> {
                toolbar_main.menu.clear()
                toolbar_main.inflateMenu(R.menu.menu_toolbar_folder)
            }
        }

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_search_toolbar -> {
                Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show()
            }
            R.id.item_voice_toolbar -> {
                Toast.makeText(this, "Voice", Toast.LENGTH_SHORT).show()
            }
            R.id.item_create_playlist -> {
                Toast.makeText(this, "Create new Playlist", Toast.LENGTH_SHORT).show()
            }
            R.id.item_equalizer -> {
                Toast.makeText(this, "Equalizer", Toast.LENGTH_SHORT).show()
            }
            R.id.item_sort -> {
                Toast.makeText(this, "Sort", Toast.LENGTH_SHORT).show()
            }
            R.id.item_sufftle -> {
                Toast.makeText(this, "Sufftle", Toast.LENGTH_SHORT).show()
            }
            R.id.item_add_to_home_screen -> {
                Toast.makeText(this, "Add", Toast.LENGTH_SHORT).show()
            }


        }
        return super.onOptionsItemSelected(item)
    }

    fun setToolbar() {
        setSupportActionBar(toolbar_main)
    }

    fun setItemIconTintList() {
        navigation_main.itemIconTintList = null
    }

    fun initViewPager() {
        bottomNavigationAdapter = BottomNavigationAdapter(supportFragmentManager)
        bottomNavigationAdapter?.addFragment(PlaylistFragment(), resources.getString(R.string.title_playlist))
        bottomNavigationAdapter?.addFragment(SongsFragment(), resources.getString(R.string.title_songs))
        bottomNavigationAdapter?.addFragment(ArtistFragment(), resources.getString(R.string.title_artist))
        bottomNavigationAdapter?.addFragment(AlbumFragment(), resources.getString(R.string.title_album))
        bottomNavigationAdapter?.addFragment(FolderFragment(), resources.getString(R.string.title_folder))
        view_pager_main.adapter = bottomNavigationAdapter
        tablayout_main.setupWithViewPager(view_pager_main)
        setIconTablayout()
    }

    @SuppressLint("ResourceType")
    fun setIconTablayout() {
        tablayout_main.setSelectedTabIndicatorColor(Color.TRANSPARENT);
        tablayout_main.getTabAt(0)?.setIcon(getDrawable(R.drawable.playlist_selected_home))
        tablayout_main.setTabTextColors(
            Color.parseColor(getResources().getString(R.color.color_text)),
            Color.parseColor(getResources().getString(R.color.playlist_tab_selected))
        )
        tablayout_main.getTabAt(1)?.setIcon(getDrawable(R.drawable.songs_unselected))
        tablayout_main.getTabAt(2)?.setIcon(getDrawable(R.drawable.artist_unselected))
        tablayout_main.getTabAt(3)?.setIcon(getDrawable(R.drawable.album_unselected))
        tablayout_main.getTabAt(4)?.setIcon(getDrawable(R.drawable.folder_unselected))
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
            PositionNavigation.PLAYLIST.position -> {
                tabSelected = PositionNavigation.PLAYLIST.position
                tab.setIcon(R.drawable.playlist_selected_home)
                tablayout_main.setTabTextColors(
                    Color.parseColor(getResources().getString(R.color.color_text)),
                    Color.parseColor(getResources().getString(R.color.playlist_tab_selected))
                )

            }

            PositionNavigation.SONGS.position -> {
                tabSelected = PositionNavigation.SONGS.position
                toolbar_main.inflateMenu(R.menu.menu_toolbat_songs)
                tab.setIcon(R.drawable.songs_selected_home)
                tablayout_main.setTabTextColors(
                    Color.parseColor(getResources().getString(R.color.color_text)),
                    Color.parseColor(getResources().getString(R.color.songs_tab_selected))
                )
            }

            PositionNavigation.ARTIST.position -> {
                tabSelected = PositionNavigation.ARTIST.position
                tab.setIcon(R.drawable.artist_selected_home)
                tablayout_main.setTabTextColors(
                    Color.parseColor(getResources().getString(R.color.color_text)),
                    Color.parseColor(getResources().getString(R.color.artist_tab_selected))
                )
            }

            PositionNavigation.ALBUM.position -> {
                tabSelected = PositionNavigation.ALBUM.position
                tab.setIcon(R.drawable.album_selected_home)
                tablayout_main.setTabTextColors(
                    Color.parseColor(getResources().getString(R.color.color_text)),
                    Color.parseColor(getResources().getString(R.color.album_tab_selected))
                )
            }

            PositionNavigation.FOLDER.position -> {
                tabSelected = PositionNavigation.FOLDER.position
                tab.setIcon(R.drawable.folder_selected_home)
                tablayout_main.setTabTextColors(
                    Color.parseColor(getResources().getString(R.color.color_text)),
                    Color.parseColor(getResources().getString(R.color.folder_tab_selected))
                )
            }
        }
    }

    fun setTabUnSelected(tab: TabLayout.Tab?) {
        val position = tab?.position
        when (position) {
            PositionNavigation.PLAYLIST.position -> {
                tab.setIcon(R.drawable.playlist_unselected)
            }

            PositionNavigation.SONGS.position -> {
                tab.setIcon(R.drawable.songs_unselected)
            }

            PositionNavigation.ARTIST.position -> {
                tab.setIcon(R.drawable.artist_unselected)
            }

            PositionNavigation.ALBUM.position -> {
                tab.setIcon(R.drawable.album_unselected)
            }

            PositionNavigation.FOLDER.position -> {
                tab.setIcon(R.drawable.folder_unselected)
            }
        }
    }


}

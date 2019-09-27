package minh.quy.musicplayer.fragment

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fast_scroller.*
import kotlinx.android.synthetic.main.fragment_album.*
import minh.quy.musicplayer.Constant
import minh.quy.musicplayer.R
import minh.quy.musicplayer.action.IOptionListener
import minh.quy.musicplayer.action.OnItemCommonClick
import minh.quy.musicplayer.adapter.AlbumAdapter
import minh.quy.musicplayer.decoration.ItemAlbumDecoration
import minh.quy.musicplayer.model.Song

class AlbumFragment : BaseFragment(), OnItemCommonClick, IOptionListener {

    var alBumAdater: AlbumAdapter? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_album, container, false)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initRecyclerView()

    }

    override fun onOptionClick(position: Int, view: View) {
        showPopupOption(position,view)
    }

    override fun onItemClick(postion: Int) {
        gotoListSongFragment(postion)
    }

    fun showPopupOption(position: Int, view: View) {
        val popUpMenu = PopupMenu(context!!, view)
        popUpMenu.menuInflater.inflate(R.menu.menu_option_album_and_artist, popUpMenu.menu)


        popUpMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.item_play_album_artist -> {
                    optionPlay(mainActivity.albumList[position].albummId)
                }
                R.id.item_play_next_album_artist -> {
                    optionPlaynext(mainActivity.albumList[position].albummId)
                }
                R.id.item_add_queue_album_artist -> {
                    optionAddToQueue(mainActivity.albumList[position].albummId)
                }
                R.id.item_add_to_playlist_album_artist -> {
                    gotoAddSongFragment(position)
                }
            }
            true
        })

        popUpMenu.show()
    }

    fun gotoAddSongFragment(position: Int) {
        val fragment = AddSongFragment.newInstance(
            mainActivity.albumList[position].albummId,
            Constant.TypeListsong.ALBUM.type
        )
        val transaction = mainActivity.fragmentManager.beginTransaction()
        transaction.replace(R.id.frame_main, fragment, null)
        transaction.addToBackStack(null)
        transaction.commit()

    }

    fun optionPlay(albumId: Long) {
        mainActivity.musicService?.songList?.clear()
        mainActivity.musicService?.songList?.addAll(getSongs(albumId))
        mainActivity.musicService?.setSongPosition(0)
        mainActivity.musicService?.playMusic()
    }

    fun optionPlaynext(albumId: Long) {
        mainActivity.musicService?.songList?.addAll(
            mainActivity.musicService?.songPos!! + 1,
            getSongs(albumId)
        )
        Toast.makeText(
            context,
            String.format(resources.getString(R.string.added_to_queue), getSongs(albumId).size),
            Toast.LENGTH_SHORT
        ).show()
    }

    fun optionAddToQueue(albumId: Long) {
        mainActivity.musicService?.songList?.addAll(
            mainActivity.musicService?.songList!!.size,
            getSongs(albumId)
        )

        Toast.makeText(
            context,
            String.format(resources.getString(R.string.added_to_queue), getSongs(albumId).size),
            Toast.LENGTH_SHORT
        ).show()
    }

    fun getSongs(albumId: Long): MutableList<Song> {
        val listSong: MutableList<Song> = arrayListOf()
        mainActivity.songlist.forEach {
            if (albumId == it.albumId) {
                listSong.add(it)
            }
        }
        return listSong
    }

    fun gotoListSongFragment(position: Int) {
        val fragment: ListSongFragment
        fragment =
            ListSongFragment.newInstance(
                mainActivity.albumList[position].albummId,
                mainActivity.albumList[position].albumName,
                Constant.TypeListsong.ALBUM.type
            )
        val transaction = mainActivity.fragmentManager.beginTransaction()
        transaction.replace(R.id.frame_main, fragment, null)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    fun initRecyclerView() {
        rv_album_list.apply {
            layoutManager = GridLayoutManager(contextBase, 2, RecyclerView.VERTICAL, false)
            alBumAdater = AlbumAdapter(contextBase!!)
            alBumAdater?.addAlbumList(mainActivity.albumList)
            alBumAdater?.setItemClick(this@AlbumFragment,this@AlbumFragment)
            adapter = alBumAdater
            addItemDecoration(ItemAlbumDecoration(40))
            addOnScrollListener(object : RecyclerView.OnScrollListener() {

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    hideScrollBar()
                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    showScrollBar()
                }
            })
        }
        fast_scroller?.setRecyclerView(rv_album_list)
    }

    fun hideScrollBar() {
        Handler().postDelayed({
            fast_scroller?.visibility = View.GONE
        }, Constant.TIME_HIDE_SCROLL_BAR)
    }

    fun showScrollBar() {
        fast_scroller?.visibility = View.VISIBLE
    }

    fun getAlbumList() {
        mainActivity.getAllAlbum()
        alBumAdater?.addAlbumList(mainActivity.albumList)
    }

}
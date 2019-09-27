package minh.quy.musicplayer.fragment

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fast_scroller.*
import kotlinx.android.synthetic.main.fragment_artist.*
import kotlinx.android.synthetic.main.popup_add_to_playlist.view.*
import minh.quy.musicplayer.Constant
import minh.quy.musicplayer.R
import minh.quy.musicplayer.action.IOptionListener
import minh.quy.musicplayer.action.OnItemCommonClick
import minh.quy.musicplayer.activity.MainActivity
import minh.quy.musicplayer.adapter.AddToPlaylistAdapter
import minh.quy.musicplayer.adapter.ArtistAdapter
import minh.quy.musicplayer.main
import minh.quy.musicplayer.model.PlayListSong
import minh.quy.musicplayer.model.Song

class ArtistFragment : BaseFragment(), IOptionListener, OnItemCommonClick {

    var artistAdapter: ArtistAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_artist, container, false)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initRecyclerview()
    }

    override fun onItemClick(postion: Int) {
        gotoSongFragment(postion)
    }

    override fun onOptionClick(position: Int, view: View) {
        showPopupOption(position, view)
    }

    fun showPopupOption(position: Int, view: View) {
        val popUpMenu = PopupMenu(context!!, view)
        popUpMenu.menuInflater.inflate(R.menu.menu_option_album_and_artist, popUpMenu.menu)


        popUpMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.item_play_album_artist -> {
                    optionPlay(mainActivity.artistList[position].artistId)
                }
                R.id.item_play_next_album_artist -> {
                    optionPlaynext(mainActivity.artistList[position].artistId)
                }
                R.id.item_add_queue_album_artist -> {
                    optionAddToQueue(mainActivity.artistList[position].artistId)
                }
                R.id.item_add_to_playlist_album_artist -> {
                    gotoAddSongFragment(position)
                }
            }
            true
        })

        popUpMenu.show()
    }

    fun optionPlay(artistId: Long) {
        mainActivity.musicService?.songList?.clear()
        mainActivity.musicService?.songList?.addAll(getSongs(artistId))
        mainActivity.musicService?.setSongPosition(0)
        mainActivity.musicService?.playMusic()
    }

    fun optionPlaynext(artistId: Long) {
        mainActivity.musicService?.songList?.addAll(
            mainActivity.musicService?.songPos!! + 1,
            getSongs(artistId)
        )

        Toast.makeText(
            context,
            String.format(resources.getString(R.string.added_to_queue), getSongs(artistId).size),
            Toast.LENGTH_SHORT
        ).show()
    }

    fun optionAddToQueue(artistId: Long) {
        mainActivity.musicService?.songList?.addAll(
            mainActivity.musicService?.songList!!.size,
            getSongs(artistId)
        )
        Toast.makeText(
            context,
            String.format(resources.getString(R.string.added_to_queue), getSongs(artistId).size),
            Toast.LENGTH_SHORT
        ).show()
    }

    fun gotoSongFragment(position: Int) {
        val fragment: ListSongFragment
        fragment =
            ListSongFragment.newInstance(
                mainActivity.artistList[position].artistName,
                mainActivity.artistList[position].artistName,
                Constant.TypeListsong.ARTIST.type
            )
        val transaction = mainActivity.fragmentManager.beginTransaction()
        transaction.replace(R.id.frame_main, fragment, null)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    fun gotoAddSongFragment(position: Int) {
        val fragment = AddSongFragment.newInstance(
            mainActivity.artistList[position].artistName,
            Constant.TypeListsong.ARTIST.type
        )
        val transaction = mainActivity.fragmentManager.beginTransaction()
        transaction.replace(R.id.frame_main, fragment, null)
        transaction.addToBackStack(null)
        transaction.commit()

    }

    fun initRecyclerview() {
        rv_artist_fragment.apply {
            layoutManager = LinearLayoutManager(contextBase, RecyclerView.VERTICAL, false)
            artistAdapter = ArtistAdapter(contextBase!!)
            artistAdapter?.addArtistList(mainActivity.artistList)
            artistAdapter?.setItemClick(this@ArtistFragment, this@ArtistFragment)
            adapter = artistAdapter
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
        fast_scroller?.setRecyclerView(rv_artist_fragment)
    }

    fun hideScrollBar() {
        Handler().postDelayed({
            fast_scroller?.visibility = View.GONE
        }, Constant.TIME_HIDE_SCROLL_BAR)
    }

    fun showScrollBar() {
        fast_scroller?.visibility = View.VISIBLE
    }

    fun getartistList() {
        mainActivity.getAllArtist()
        artistAdapter?.addArtistList(mainActivity.artistList)
    }

    fun getSongs(artistId: Long): MutableList<Song> {
        val listSong: MutableList<Song> = arrayListOf()
        mainActivity.songlist.forEach {
            if (artistId == it.artistId) {
                listSong.add(it)
            }
        }
        return listSong
    }
}

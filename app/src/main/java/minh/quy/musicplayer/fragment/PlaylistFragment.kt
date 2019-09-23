package minh.quy.musicplayer.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_playlist.*
import kotlinx.android.synthetic.main.popup_create_new_playlist.view.*
import layout.HomeFragment
import minh.quy.musicplayer.R
import minh.quy.musicplayer.action.IActionOption
import minh.quy.musicplayer.action.OnItemCommonClick
import minh.quy.musicplayer.adapter.PlaylistAdapter
import minh.quy.musicplayer.contract.IPlaylistView
import minh.quy.musicplayer.funtiontoolbar.FunctionToolbarPlaylist
import minh.quy.musicplayer.model.Playlist
import minh.quy.musicplayer.model.Song
import minh.quy.musicplayer.presenter.PlaylistPresenter


class PlaylistFragment : BaseFragment(), IPlaylistView, FunctionToolbarPlaylist, OnItemCommonClick, IActionOption {

    var adapterPlaylist: PlaylistAdapter? = null
    var presenter: PlaylistPresenter? = null
    var homeFragment: HomeFragment? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (parentFragment is HomeFragment) {
            homeFragment = parentFragment as HomeFragment
        }
        homeFragment?.setFunctionPlaylist(this)
        presenter = PlaylistPresenter(this)
        presenter?.getAllPlaylist(mainActivity.musicDatabase)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_playlist, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        Log.d("MinhNQ", "onResume PlaylistFragment")
    }

    override fun onResponseAllPlaylist(liveData: LiveData<MutableList<Playlist>>?) {
        liveData?.observe(this, Observer<MutableList<Playlist>> {
            Log.d("MinhNQ", it.size.toString())
            adapterPlaylist?.addPlaylists(it)
            mainActivity.playlists.clear()
            mainActivity.playlists.addAll(it)
        })
    }

    override fun onResponseInserPlaylist(isSuccess: Boolean) {
        if (isSuccess) {
            Toast.makeText(contextBase, getString(R.string.creat_new_playlist_success), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(contextBase, getString(R.string.create_playlist_fail), Toast.LENGTH_SHORT)
                .show()
        }

    }

    override fun createNewPlaylist() {
        showPopupCreatePlaylist()
    }

    override fun sortPlaylist() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun sufflePlaylist() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onItemClick(postion: Int) {
        var fragment =
            ListSongFragment.newInstance(mainActivity.playlists[postion].id!!, mainActivity.playlists[postion].name)
        var transaction = mainActivity.fragmentManager.beginTransaction()
        transaction.replace(R.id.frame_main, fragment, null)
        transaction.addToBackStack(null)
        transaction.commit()

    }

    override fun onOptionClick(position: Int, view: View) {
        val popUpMenu = PopupMenu(context!!, view)
        popUpMenu.menuInflater.inflate(R.menu.menu_item_playlist, popUpMenu.menu)


        popUpMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.item_play_playlist -> {
                    playPlayList(mainActivity.playlists[position].id!!)
                }
                R.id.item_play_next_playlist -> {
                    playNext(mainActivity.playlists[position].id!!)
                }
                R.id.item_add_queue_playlist -> {
                    addToQueue(mainActivity.playlists[position].id!!)
                }
                R.id.item_delete_playlist ->{
                    mainActivity.musicDatabase?.getPlaylistDAO()?.deletePlaylist(mainActivity.playlists[position])
                }

            }

            true
        })
    }

    private fun addToQueue(playlistId: Int) {
        mainActivity.musicService?.songList?.addAll(mainActivity.musicService?.songList!!.size, getSongs(playlistId))

    }

    private fun playNext(playlistId: Int) {
        mainActivity.musicService?.songList?.addAll(mainActivity.musicService?.songPos!! + 1, getSongs(playlistId))

    }

    fun initRecyclerView() {
        rv_playlist.apply {
            layoutManager = LinearLayoutManager(contextBase, RecyclerView.VERTICAL, false)
            adapterPlaylist = PlaylistAdapter(contextBase!!)
            adapterPlaylist?.addPlaylists(mainActivity.playlists)
            adapterPlaylist?.setItemClick(this@PlaylistFragment)
            adapterPlaylist?.setActionOption(this@PlaylistFragment)
            adapter = adapterPlaylist

        }
    }

    fun playPlayList(playlistId: Int) {

        mainActivity.musicService?.songList?.clear()
        mainActivity.musicService?.songList?.addAll(getSongs(playlistId))
        mainActivity.musicService?.setSongPosition(0)
        mainActivity.musicService?.playMusic()
    }

    fun getSongs(playlistId: Int): MutableList<Song> {
        val listSong = arrayListOf<Song>()
        val listPlaylistSong =
            mainActivity.musicDatabase?.getPlayListSongDAO()?.getAllSongInPlaylist(playlistId)
        listPlaylistSong?.forEach { playlistId ->
            mainActivity.songlist.forEach {
                if (playlistId.songId.equals(it.songId)) {
                    listSong.add(it)
                }
            }
        }

        return listSong
    }


    fun showPopupCreatePlaylist() {
        val alertDialogBuilder = AlertDialog.Builder(contextBase!!)
        val dialogView = layoutInflater.inflate(R.layout.popup_create_new_playlist, null)
        alertDialogBuilder.setView(dialogView)
        val alertDialog = alertDialogBuilder.create()
        dialogView.btn_create_playlist.setOnClickListener {
            if (!dialogView.edt_name_playlist.text.toString().isEmpty()) {
                presenter?.inserNewPlaylist(Playlist(dialogView.edt_name_playlist.text.toString()))
                Log.d("MinhNQ", " insert playlist")
                alertDialog.dismiss()
            } else {
                Toast.makeText(contextBase, "Enter playlist name", Toast.LENGTH_SHORT).show()
            }
        }

        dialogView.btn_cancle_playlist.setOnClickListener { alertDialog.dismiss() }
        alertDialog.show()

    }
}
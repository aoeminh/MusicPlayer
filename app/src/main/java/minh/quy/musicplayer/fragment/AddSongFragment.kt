package minh.quy.musicplayer.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_add_song.*
import kotlinx.android.synthetic.main.fragment_list_song.*
import kotlinx.android.synthetic.main.item_list_add_song_fragment.view.*
import kotlinx.android.synthetic.main.popup_add_to_playlist.view.*
import minh.quy.musicplayer.Constant
import minh.quy.musicplayer.Constant.*
import minh.quy.musicplayer.R
import minh.quy.musicplayer.action.OnItemCommonClick
import minh.quy.musicplayer.adapter.AddSongAdapter
import minh.quy.musicplayer.adapter.AddToPlaylistAdapter
import minh.quy.musicplayer.model.PlayListSong
import minh.quy.musicplayer.model.Song

class AddSongFragment : BaseFragment(), AddSongAdapter.onItemClick {

    companion object {
        val EXTRA_PLAYLIST_ID = "extra.position"
        val EXTRA_ARTIST_NAME = "extra.artist.name"
        val EXTRA_ALBUM_ID = "extra.album.id"
        val EXTRA_TYPE = "extra.type"

        fun newInstance(playlistId: Int, type: Int): AddSongFragment {
            val fragment = AddSongFragment()
            val bundle = Bundle()
            bundle.putInt(EXTRA_PLAYLIST_ID, playlistId)
            bundle.putInt(EXTRA_TYPE, type)
            fragment.arguments = bundle
            return fragment
        }

        fun newInstance(artistName: String, type: Int): AddSongFragment {
            val fragment = AddSongFragment()
            val bundle = Bundle()
            bundle.putString(EXTRA_ARTIST_NAME, artistName)
            bundle.putInt(EXTRA_TYPE, type)
            fragment.arguments = bundle
            return fragment
        }

        fun newInstance(albumId: Long, type: Int): AddSongFragment {
            val fragment = AddSongFragment()
            val bundle = Bundle()
            bundle.putLong(EXTRA_ALBUM_ID, albumId)
            bundle.putInt(EXTRA_TYPE, type)
            fragment.arguments = bundle
            return fragment
        }

    }

    var playlistId: Int? = null
    var songList: MutableList<Song?> = arrayListOf()
    var mAdapter: AddSongAdapter? = null
    var currentPlayListSong: MutableList<PlayListSong>? = arrayListOf()
    var type: Int? = null
    var alertDialog: AlertDialog? = null
    var addAdapter: AddToPlaylistAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            type = it.getInt(EXTRA_TYPE)
            when (type) {
                TypeListsong.PLAYLIST.type -> {
                    playlistId = it.getInt(EXTRA_PLAYLIST_ID, 0)
                    songList.addAll(mainActivity.songlist)
                    songList.forEach {
                        it?.isSelected = false
                    }
                    removeSongAlreadyExist()
                    // add view when last item has been cover by button add

                }
                TypeListsong.ARTIST.type -> {
                    val artistName = it.getString(EXTRA_ARTIST_NAME, "")
                    //get all song with artist name
                    mainActivity.songlist.forEach {
                        it.isSelected = false
                        if (artistName.equals(it.artistName)) {
                            songList.add(it)
                        }
                    }
                    showPlaylists()
                }

                TypeListsong.ALBUM.type -> {
                    val albumId = it.getLong(EXTRA_ALBUM_ID, 0)
                    mainActivity.songlist.forEach {
                        it.isSelected = false
                        if (albumId == it.albumId) {
                            songList.add(it)
                        }
                    }
                    showPlaylists()
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_song, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showHideView()
        initToolbar()
        initRecyclerView()
        setAction()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mainActivity.songlist.forEach {
            it.isSelected = it.songId.equals(mainActivity.musicService?.currenSongId)
        }
    }

    override fun onItemClick(position: Int, view: View) {

        if (songList[position]!!.isSelected) {
            songList[position]?.isSelected = false
            view.chk_add_song_fragment.isChecked = false
        } else {
            songList[position]?.isSelected = true
            view.chk_add_song_fragment.isChecked = true
        }
    }

    fun initToolbar() {
        val activity = activity as AppCompatActivity?
        toolbar_add_song.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        activity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity?.supportActionBar?.setHomeButtonEnabled(true)
        activity?.supportActionBar?.setDisplayShowTitleEnabled(false);
        toolbar_add_song?.setNavigationOnClickListener {
            goBack()
        }
    }


    fun initRecyclerView() {
        rv_add_song_fragment?.apply {
            layoutManager = LinearLayoutManager(context!!, RecyclerView.VERTICAL, false)
            mAdapter = AddSongAdapter(context, songList)
            mAdapter?.setItemClick(this@AddSongFragment)
            adapter = mAdapter
        }
    }

    fun removeSongAlreadyExist() {
        currentPlayListSong =
            mainActivity.musicDatabase?.getPlayListSongDAO()
                ?.getAllSongInPlaylist(playlistId!!)
        currentPlayListSong?.forEach { playslistSong ->
            if(!songList.isEmpty()){
                val songIterator = songList.listIterator()
                songIterator.forEach {
                    if (playslistSong.songId.equals(it?.songId)) {
                        songIterator.remove()
                    }
                }

            }
        }

        // add empty view when last item has been covered by buton add
        if(songList.size> 0 && songList.last() != null){
            songList.add(songList.size,null)
        }
    }


    fun setAction() {
        btn_add_to_play_list.setOnClickListener { view -> addToPlaylist() }
    }

    private fun addToPlaylist() {
        addSongIntoPlayList()
        goBack()
    }

    fun showPlaylists() {
        val builder = AlertDialog.Builder(context!!)
        val dialogView = layoutInflater.inflate(R.layout.popup_add_to_playlist, null)
        builder.setView(dialogView)
        alertDialog = builder.create()
        dialogView.rv_playlists_add_to_playlis.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            addAdapter = AddToPlaylistAdapter(context, mainActivity.playlists)
            addAdapter?.setItemClick(object : OnItemCommonClick {
                override fun onItemClick(postion: Int) {
                    playlistId = mainActivity.playlists[postion].id!!
                    removeSongAlreadyExist()
                    mAdapter?.notifyDataSetChanged()
                    showHideView()
                    alertDialog?.dismiss()
                }
            })
            adapter = addAdapter
        }
        alertDialog?.setCancelable(false)
        alertDialog?.show()
    }

    fun showHideView(){
        if (songList.size <= 0) {
            ln_no_song_add_fragment?.visibility = View.VISIBLE
            rv_add_song_fragment?.visibility = View.GONE
            btn_add_to_play_list?.visibility = View.GONE
        }else{
            rv_add_song_fragment?.visibility = View.VISIBLE
            ln_no_song_add_fragment?.visibility = View.GONE
            btn_add_to_play_list?.visibility = View.VISIBLE
        }
    }

    fun addSongIntoPlayList() {
        var numSongAdded = 0
        songList.forEach { song ->
            song?.let {
                if (song.isSelected) {
                    mainActivity.musicDatabase?.getPlayListSongDAO()?.insertSongIntoPlayList(
                        PlayListSong(playlistId!!, song.songId!!)
                    )
                    numSongAdded++
                }
            }
        }
        showNumberSongAdded(numSongAdded)
    }

    fun showNumberSongAdded(numSongAdded: Int) {
        if (numSongAdded > 1) {
            Toast.makeText(
                context!!,
                String.format(resources.getString(R.string.added_multi_song), numSongAdded),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                context!!,
                String.format(resources.getString(R.string.added_one_song), numSongAdded),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun goBack() {
        mainActivity.fragmentManager.popBackStack()
    }

    fun checkSongExist(songId: String): Boolean {
        var exist = false
        currentPlayListSong?.forEach {
            exist = songId.equals(it.songId)
            if (exist) return true
        }
        return false
    }

}
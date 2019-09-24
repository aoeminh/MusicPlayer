package minh.quy.musicplayer.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_add_song.*
import kotlinx.android.synthetic.main.item_list_add_song_fragment.view.*
import minh.quy.musicplayer.R
import minh.quy.musicplayer.adapter.AddSongAdapter
import minh.quy.musicplayer.model.PlayListSong
import minh.quy.musicplayer.model.Song

class AddSongFragment : BaseFragment(), AddSongAdapter.onItemClick {

    companion object {
        val EXTRA_PLAYLIST_ID = "extra.position"
        fun newInstance(playlistId: Int): AddSongFragment {
            val fragment = AddSongFragment()
            val bundle = Bundle()
            bundle.putInt(EXTRA_PLAYLIST_ID, playlistId)
            fragment.arguments = bundle
            return fragment
        }
    }

    var playlistId: Int? = null
    var songList: MutableList<Song> = arrayListOf()
    var mAdapter: AddSongAdapter? = null
    var currentPlayListSong: MutableList<PlayListSong>? = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        songList.addAll(mainActivity.songlist)
        songList.forEach {
            it.isSelected = false
        }
        arguments?.let {
            playlistId = it.getInt(EXTRA_PLAYLIST_ID, 0)
        }
        setSongAlreadyExist()
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

        if (songList[position].isSelected) {
            songList[position].isSelected = false
            view.chk_add_song_fragment.isChecked = false
        } else {
            songList[position].isSelected = true
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
            layoutManager = LinearLayoutManager(context!!, LinearLayoutManager.VERTICAL, false)
            mAdapter = AddSongAdapter(context, songList)
            mAdapter?.setItemClick(this@AddSongFragment)
            adapter = mAdapter
        }
    }

    fun setSongAlreadyExist() {
        currentPlayListSong =
            mainActivity.musicDatabase?.getPlayListSongDAO()
                ?.getAllSongInPlaylist(playlistId!!)
        currentPlayListSong?.forEach { playslistSong ->

            songList.forEach {
                if (playslistSong.songId.equals(it.songId)) {
                    it.isSelected = true
                }
            }
        }
    }

    fun setAction() {
        btn_add_to_play_list.setOnClickListener { view -> addToPlaylist() }
    }

    private fun addToPlaylist() {

        var numSongAdded = 0
        songList.forEach { song ->

            if (song.isSelected && !checkSongExist(song.songId!!)) {
                mainActivity.musicDatabase?.getPlayListSongDAO()?.insertSongIntoPlayList(
                    PlayListSong(playlistId!!, song.songId!!)
                )
                numSongAdded++
            }
        }


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

        goBack()

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
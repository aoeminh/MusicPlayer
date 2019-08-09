package minh.quy.musicplayer.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_songs.*
import minh.quy.musicplayer.R
import minh.quy.musicplayer.activity.MainActivity
import minh.quy.musicplayer.adapter.SongFragmentAdapter
import minh.quy.musicplayer.model.Album
import minh.quy.musicplayer.model.Song

class SongsFragment : Fragment() {

    lateinit var mainActivity: MainActivity
    var contextSong: Context? = null
    var songlist: MutableList<Song> = arrayListOf()
    var adapterSong: SongFragmentAdapter? = null
    var albumList: MutableList<Album> = arrayListOf()

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        contextSong = context
        if (activity is MainActivity) {
            mainActivity = activity as MainActivity
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        songlist = mainActivity.songlist
        albumList = mainActivity.albumList
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_songs, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("MinhNQ", songlist.size.toString())
        initRecyclerview()

    }

    fun initRecyclerview() {
        rv_song_fragment.apply {
            layoutManager = LinearLayoutManager(contextSong, RecyclerView.VERTICAL, false)
            adapterSong = SongFragmentAdapter(contextSong!!)
            adapterSong?.setlistSong(songlist)
            adapter = adapterSong
        }
    }


    override fun onResume() {
        super.onResume()
    }
}
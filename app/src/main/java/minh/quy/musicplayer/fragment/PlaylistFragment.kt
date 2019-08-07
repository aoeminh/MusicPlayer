package minh.quy.musicplayer.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_playlist.*
import kotlinx.android.synthetic.main.fragment_playlist.view.*
import minh.quy.musicplayer.R
import minh.quy.musicplayer.activity.MainActivity
import minh.quy.musicplayer.adapter.PlaylistAdapter
import minh.quy.musicplayer.model.Playlist

class PlaylistFragment : Fragment() {

    var adapterPlaylist: PlaylistAdapter? = null
    lateinit var mainActivity: MainActivity
    var playlists: MutableList<Playlist> = arrayListOf()

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (activity is MainActivity) {
            mainActivity = activity as MainActivity
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity.musicDatabase?.getPlaylistDAO()?.getAllPlaylist()?.observe(this, Observer<MutableList<Playlist>>() {
            Log.d("MinhNQ", it.size.toString())
            adapterPlaylist?.addPlaylists(it)
            playlists.clear()
            playlists.addAll(it)
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_playlist, container, false)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initRecyclerView()

    }

    fun initRecyclerView() {
        rv_playlist.apply {
            layoutManager = LinearLayoutManager(mainActivity, RecyclerView.VERTICAL, false)
            adapterPlaylist = PlaylistAdapter(mainActivity)
            adapterPlaylist?.addPlaylists(playlists)
            adapter = adapterPlaylist

        }


    }
}
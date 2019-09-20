package minh.quy.musicplayer.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_list_song.*
import minh.quy.musicplayer.R
import minh.quy.musicplayer.Utils.Utils
import minh.quy.musicplayer.action.OnItemCommonClick
import minh.quy.musicplayer.activity.MainActivity
import minh.quy.musicplayer.adapter.ListSongAdapter
import minh.quy.musicplayer.model.Song

class ListSongFragment : Fragment(), OnItemCommonClick {

    companion object {
        val EXTRA_ID = "extra.id"
        val EXTRA_NAME = "extra.name"

        fun newInstance(id: Int, title: String): ListSongFragment {
            var fragment = ListSongFragment()
            var bundle = Bundle()
            bundle.putInt(EXTRA_ID, id)
            bundle.putString(EXTRA_NAME, title)
            fragment.arguments = bundle
            return fragment
        }
    }

    var mAdapter: ListSongAdapter? = null
    var listSong: MutableList<Song> = arrayListOf()
    var mainActivity: MainActivity? = null
    var id: Int? = null
    var title: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (activity is MainActivity) {
            mainActivity = activity as MainActivity
        }
        arguments?.let {
            id = it.getInt(EXTRA_ID,0)
            title = it.getString(EXTRA_NAME,"")
        }

        var listPlaylistSong =
            mainActivity?.musicDatabase?.getPlayListSongDAO()?.getAllSongInPlaylist(id!!)
        listPlaylistSong?.forEach {
            getSong(it.songId)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list_song, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rv_list_song?.apply {
            layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
            mAdapter = ListSongAdapter(context,listSong)
            mAdapter?.setOnItemClick(this@ListSongFragment)
            adapter = mAdapter
        }
        img_list_song?.setImageResource(
            Utils.getDrawableIdDefaultImage(
                1
            )
        )
        val activity = activity as AppCompatActivity?
        toolbar_list_song.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        activity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity?.supportActionBar?.setHomeButtonEnabled(true)
        activity?.supportActionBar?.setDisplayShowTitleEnabled(false);
        toolbar_list_song?.title = title
        toolbar_list_song?.setNavigationOnClickListener{
        }

    }

    override fun onItemClick(postion: Int) {

    }


    fun getSong(songId: String){
        mainActivity?.songlist?.forEach{
            if(songId.equals(it.songId)){
               listSong.add(it)
            }
        }
    }
}
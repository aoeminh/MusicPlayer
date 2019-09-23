package minh.quy.musicplayer.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_list_song.*
import minh.quy.musicplayer.R
import minh.quy.musicplayer.Utils.Utils
import minh.quy.musicplayer.action.OnItemCommonClick
import minh.quy.musicplayer.activity.MainActivity
import minh.quy.musicplayer.adapter.ListSongAdapter
import minh.quy.musicplayer.model.Song
import minh.quy.musicplayer.service.PlayMusicService
import kotlin.random.Random

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
    var receiver: BroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (activity is MainActivity) {
            mainActivity = activity as MainActivity
        }
        arguments?.let {
            id = it.getInt(EXTRA_ID, 0)
            title = it.getString(EXTRA_NAME, "")
        }

        val listPlaylistSong =
            mainActivity?.musicDatabase?.getPlayListSongDAO()?.getAllSongInPlaylist(id!!)
        listPlaylistSong?.forEach {
            getSong(it.songId)
        }

        registUpdateSongSelected()

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
        if(listSong.size <=0){
            rv_list_song.visibility = View.GONE
            nested_scroll.visibility = View.VISIBLE
        }else{
            rv_list_song.visibility = View.VISIBLE
            nested_scroll.visibility = View.GONE
        }
        rv_list_song?.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            mAdapter = ListSongAdapter(context, listSong)
            mAdapter?.setOnItemClick(this@ListSongFragment)
            adapter = mAdapter
        }
        img_list_song?.setImageResource(
            Utils.getDrawableIdDefaultImage(
                Random.nextInt(1, 7)
            )
        )
        val activity = activity as AppCompatActivity?
        toolbar_list_song.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        activity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity?.supportActionBar?.setHomeButtonEnabled(true)
        activity?.supportActionBar?.setDisplayShowTitleEnabled(false);
        toolbar_list_song?.title = title
        toolbar_list_song?.setNavigationOnClickListener {
            mainActivity?.fragmentManager?.popBackStack()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        unregistUpdateSongSelected()
    }

    override fun onItemClick(postion: Int) {
        if (!listSong[postion].isSelected) {
            playSong(postion)
        }
        setSongQueue()
        gotoPlaySongFragment(postion)

    }

    fun setSongQueue() {
        mainActivity?.musicService?.songList?.clear()
        mainActivity?.musicService?.songList?.addAll(listSong)
    }

    fun playSong(postion: Int) {
        mainActivity?.musicService?.setSongs(listSong)
        mainActivity?.musicService?.setSongPosition(postion)
        mainActivity?.musicService?.playMusic()
    }

    fun getSong(songId: String) {
        mainActivity?.songlist?.forEach {
            if (songId.equals(it.songId)) {
                listSong.add(it)
            }
        }
    }

    fun gotoPlaySongFragment(postion: Int) {
        val fragment = PlaySongFragment.newInstance(postion)
        val transaction = mainActivity?.fragmentManager?.beginTransaction()
        transaction?.replace(R.id.frame_main, fragment, null)
        transaction?.addToBackStack(null)
        transaction?.commit()
    }

    fun registUpdateSongSelected() {
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    //update when next song from service
                    PlayMusicService.ACTION_UPDATE_VIEW -> mAdapter?.notifyDataSetChanged()
                }
            }
        }

        LocalBroadcastManager.getInstance(context!!)
            .registerReceiver(receiver!!, IntentFilter(PlayMusicService.ACTION_UPDATE_VIEW))

    }

    fun unregistUpdateSongSelected() {
        receiver?.let {
            LocalBroadcastManager.getInstance(context!!).unregisterReceiver(receiver!!)
        }
    }

}
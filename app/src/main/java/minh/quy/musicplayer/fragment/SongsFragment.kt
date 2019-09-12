package minh.quy.musicplayer.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fast_scroller.*
import kotlinx.android.synthetic.main.fragment_songs.*
import layout.HomeFragment
import minh.quy.musicplayer.Constant
import minh.quy.musicplayer.R
import minh.quy.musicplayer.Utils.RequestPermission
import minh.quy.musicplayer.action.OnItemCommonClick
import minh.quy.musicplayer.activity.MainActivity
import minh.quy.musicplayer.adapter.SongFragmentAdapter
import minh.quy.musicplayer.funtiontoolbar.FunctionToolbarPlaylist
import minh.quy.musicplayer.model.Album
import minh.quy.musicplayer.model.Song
import java.util.jar.Manifest

class SongsFragment : BaseFragment(), OnItemCommonClick {

    var songlist: MutableList<Song> = arrayListOf()
    var adapterSong: SongFragmentAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        songlist.addAll(mainActivity.songlist)
        registUpdateSongSelected()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_songs, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("MinhNQ", songlist.size.toString())
        songlist.clear()
        songlist.addAll(mainActivity.songlist)
        initRecyclerview()
    }

    override fun onItemClick(postion: Int) {
        playSong(postion)
        gotoPlaySongFragment(postion)
        setSongQueue()
        setSongSelected(songlist[postion].songId!!)
    }

    fun setSongSelected(songId: String){
        for(i in 0 until songlist.size){
            songlist[i].isSelected = songlist[i].songId.equals(songId)
        }
        adapterSong?.notifyDataSetChanged()

    }
    fun setSongQueue(){
        mainActivity.songsQueueList.clear()
        mainActivity.songsQueueList.addAll(songlist)
    }
    fun gotoPlaySongFragment(postion: Int){
        val fragment = PlaySongFragment.newInstance(postion)
        val transaction = mainActivity.fragmentManager.beginTransaction()
        transaction.replace(R.id.frame_main, fragment, null)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    fun playSong(postion: Int){
        mainActivity.musicService?.setSongs(songlist)
        mainActivity.musicService?.setSongPosition(postion)
        mainActivity.musicService?.playMusic()
    }

    fun initRecyclerview() {
        rv_song_fragment.apply {
            layoutManager = LinearLayoutManager(contextBase, RecyclerView.VERTICAL, false)
            adapterSong = SongFragmentAdapter(contextBase!!,songlist)
            adapterSong?.setOnItemClick(this@SongsFragment)
            adapter = adapterSong
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
        fast_scroller?.setRecyclerView(rv_song_fragment)
    }

    fun hideScrollBar() {
        Handler().postDelayed({
            fast_scroller?.visibility = View.GONE
        }, Constant.TIME_HIDE_SCROLL_BAR)
    }

    fun showScrollBar() {
        fast_scroller?.visibility = View.VISIBLE
    }


    fun registUpdateSongSelected() {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.let {
                    setSongSelected(it.getStringExtra(PlaySongFragment.EXTRA_SONG_ID)!!)
                }
            }
        }
        LocalBroadcastManager.getInstance(context!!)
            .registerReceiver(receiver, IntentFilter(PlaySongFragment.ACTION_UPDATE_SONG))

    }

}
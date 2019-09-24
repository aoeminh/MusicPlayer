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
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.bottom_playback.*
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
        initToolbar()
        initRecyclerView()
        initPlayback()
        setBigImage()
        setActionPlayback()

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


    fun setBigImage() {
        img_list_song?.setImageResource(
            Utils.getDrawableIdDefaultImage(
                Random.nextInt(1, 7)
            )
        )
    }

    fun initPlayback() {
        mainActivity?.musicService?.let {
            mainActivity?.musicService?.songPos?.let { position ->
                mainActivity?.musicService?.songList?.get(
                    position
                )?.let { song -> setDataForBottomPlayback(song) }

            }
        }
    }

    fun initRecyclerView() {
        if (listSong.size <= 0) {
            rv_list_song.visibility = View.GONE
            nested_scroll.visibility = View.VISIBLE
        } else {
            rv_list_song.visibility = View.VISIBLE
            nested_scroll.visibility = View.GONE
        }
        rv_list_song?.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            mAdapter = ListSongAdapter(context, listSong)
            mAdapter?.setOnItemClick(this@ListSongFragment)
            adapter = mAdapter
        }
    }

    fun initToolbar() {
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
                    PlayMusicService.ACTION_UPDATE_VIEW -> {
                        mAdapter?.notifyDataSetChanged()
                        setDataForBottomPlayback(mainActivity?.musicService?.songList?.get(getSongPositon())!!)
                    }
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

    private fun setDataForBottomPlayback(song: Song) {
        tv_artist_playback?.text = song.artistName
        tv_song_name_playback?.text = song.songName

        mainActivity?.musicService?.mediaPlayer?.let {
            if (mainActivity?.musicService?.mediaPlayer!!.isPlaying) {
                img_play_playback?.setImageResource(R.drawable.ic_pause_blue_24dp)
            } else {
                img_play_playback?.setImageResource(R.drawable.ic_play_arrow_blue_24dp)
            }
        }

    }

    fun setActionPlayback() {
        ctl_playback?.setOnClickListener { view -> gotoPlaySongFragment() }
        img_play_playback?.setOnClickListener { view -> actionBtnPlay() }
        img_song_queue_playback?.setOnClickListener { view -> showSongQueue() }
    }

    fun gotoPlaySongFragment() {
        if (mainActivity?.musicService?.songList!!.size <= 0) {
            mainActivity?.musicService?.songList!!.addAll(mainActivity!!.musicService?.songList!!)
        }
        val fragment = PlaySongFragment.newInstance(getSongPositon())
        val transaction = mainActivity!!.fragmentManager.beginTransaction()
        transaction.setCustomAnimations(
            R.anim.fragment_enter,
            R.anim.fragment_exit, R.anim.fragment_enter,
            R.anim.fragment_exit
        )
        transaction.replace(R.id.frame_main, fragment, null)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    fun actionBtnPlay() {
        if (mainActivity?.musicService?.songList!!.size <= 0) {
            mainActivity?.musicService?.songList!!.addAll(mainActivity!!.musicService?.songList!!)
        }
        if (mainActivity!!.isFirstPlay) {
            mainActivity?.musicService?.setSongPosition(getSongPositon())
            mainActivity?.musicService?.playMusic()
            mainActivity!!.isFirstPlay = false
        }
        if (mainActivity?.musicService?.mediaPlayer!!.isPlaying) {
            img_play_playback.setImageResource(R.drawable.ic_play_arrow_blue_24dp)
            mainActivity?.musicService?.mediaPlayer?.pause()
        } else {
            img_play_playback.setImageResource(R.drawable.ic_pause_blue_24dp)
            mainActivity?.musicService?.mediaPlayer?.start()
        }
    }

    fun showSongQueue() {
        val bottomSheetFragment = BottomSheetFragment.newInstance()
        bottomSheetFragment.show(mainActivity?.fragmentManager, "")
    }

    fun getSongPositon(): Int {
        for (i in 0 until mainActivity!!.musicService?.songList?.size!!) {
            if (mainActivity!!.musicService?.songList!![i].songId.equals(mainActivity?.musicService?.currenSongId)) {
                return i
            }
        }
        return 0
    }
}
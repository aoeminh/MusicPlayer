package minh.quy.musicplayer.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AlertDialogLayout
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_playsong.*
import kotlinx.android.synthetic.main.popup_add_to_playlist.view.*
import minh.quy.musicplayer.R
import minh.quy.musicplayer.Utils.Utils
import minh.quy.musicplayer.action.OnItemCommonClick
import minh.quy.musicplayer.activity.MainActivity
import minh.quy.musicplayer.adapter.AddToPlaylistAdapter
import minh.quy.musicplayer.model.PlayListSong
import minh.quy.musicplayer.model.Playlist
import minh.quy.musicplayer.service.PlayMusicService


class PlaySongFragment : Fragment(), SeekBar.OnSeekBarChangeListener, View.OnTouchListener,
    OnItemCommonClick {


    enum class Repeat(val value: Int) {
        NONE(0),
        REPEAT_ONE(1),
        REPEAT_ALL(2),
    }

    companion object {
        val EXTRA_POSITION = "extra.position"
        val EXTRA_SONG_ID = "extra.song.id"
        val ACTION_UPDATE_SONG = "action.update.song"
        fun newInstance(positin: Int): PlaySongFragment {
            val bundle = Bundle()
            val fragment = PlaySongFragment()
            bundle.putInt(EXTRA_POSITION, positin)
            fragment.arguments = bundle
            return fragment
        }
    }

    val LIMIT_Y_DIFFERENT_TOUCH = 300
    val LIMIT_X_PREVIOUS_SONG = 50
    val LIMIT_X_NEXT_SONG = -50

    var mainActivity: MainActivity? = null
    var mContext: Context? = null
    var mediaPlayer: MediaPlayer? = null
    var songPosition = 0
    var drawableIdDefaulImage = 0
    var isDown = false
    var downXPostion = 0
    var upXPosition = 0
    var downYPostion = 0
    var upYPosition = 0
    var receiver: BroadcastReceiver? = null
    var addAdapter: AddToPlaylistAdapter? = null
    var alertDialog: AlertDialog?= null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (activity is MainActivity) {
            mainActivity = activity as MainActivity
        }

        arguments?.let {
            songPosition = arguments!!.getInt(EXTRA_POSITION)
        }
        mediaPlayer = mainActivity?.musicService?.mediaPlayer
        registUpdateView()


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =
            inflater.inflate(R.layout.fragment_playsong, container, false)
        view.setOnTouchListener(this)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isVisible) {
            setDataForView()
        }
        setAction()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregistUpdateSongSelected()
    }

    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {

    }

    override fun onStartTrackingTouch(p0: SeekBar?) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        mediaPlayer?.seekTo(seekBar!!.progress)
    }

    override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {


        if (motionEvent?.action == MotionEvent.ACTION_DOWN) {
            if (!isDown) {
                downXPostion = motionEvent.x.toInt()
                downYPostion = motionEvent.y.toInt()
                isDown = true
            }
            return true
        }


        if (motionEvent?.action == MotionEvent.ACTION_UP) {
            isDown = false
            upXPosition = motionEvent.x.toInt()
            upYPosition = motionEvent.y.toInt()
            if (Math.abs(upYPosition.minus(downYPostion)) < LIMIT_Y_DIFFERENT_TOUCH) {
                if ((upXPosition.minus(downXPostion)) > LIMIT_X_PREVIOUS_SONG) {
                    actionPrevious()
                    return true
                }

                if ((upXPosition.minus(downXPostion)) <= LIMIT_X_NEXT_SONG) {
                    actionNext()
                    return true
                }
            }
        }

        return true
    }

    override fun onItemClick(postion: Int) {
        val playlistId = mainActivity!!.playlists[postion].id!!
        val songId = mainActivity?.musicService?.songList!![songPosition].songId!!

        //if song exist
        val listPlaylistSong =
            mainActivity?.musicDatabase?.getPlayListSongDAO()?.getAllSongInPlaylist(playlistId)
        listPlaylistSong?.forEach {
            if (songId.equals(it.songId)) {
                Toast.makeText(context, R.string.song_exists, Toast.LENGTH_SHORT).show()
                alertDialog?.dismiss()
                return
            }
        }

        val playlistSong = PlayListSong(
            playlistId,
            songId
        )

        val result =
            mainActivity?.musicDatabase?.getPlayListSongDAO()?.insertSongIntoPlayList(playlistSong)
        Toast.makeText(context, R.string.added_to_playlist, Toast.LENGTH_SHORT).show()
        alertDialog?.dismiss()
        Log.d("minh", result.toString())
    }

    fun setDataForView() {
        if (mainActivity!!.isFirstPlay) {
            mainActivity?.musicService?.setSongPosition(songPosition)
            mainActivity?.musicService?.playMusic()
            mediaPlayer?.seekTo(mainActivity?.musicService?.currentDuration!!.toInt())
            btn_play_and_pause_play_song?.setImageResource(R.drawable.ic_play_pause_white)
        }
        mainActivity!!.isFirstPlay = false
        val defaultPositionImage = Utils.getPositionDefaultImage(songPosition)
        drawableIdDefaulImage = Utils.getDrawableIdDefaultImage(defaultPositionImage)
        img_ava_song?.setImageResource(drawableIdDefaulImage)
        tv_song_name_play_song?.text =
            mainActivity?.musicService?.songList?.get(songPosition)?.songName
        tv_song_name_play_song?.isSelected
        tv_artist_name_play_song?.text =
            mainActivity?.musicService?.songList?.get(songPosition)?.artistName
        tv_total_time_song?.text =
            Utils.convertSongDuration(mainActivity?.musicService?.songList?.get(songPosition)?.duration!!.toLong())

        seekbar?.max = mainActivity?.musicService?.mediaPlayer!!.duration
        Log.d("minhse", seekbar?.max.toString())
        if (mediaPlayer!!.isPlaying) {
            btn_play_and_pause_play_song?.setImageResource(R.drawable.ic_play_pause_white)
        } else {
            btn_play_and_pause_play_song?.setImageResource(R.drawable.ic_play_play_white)
        }
        initRepeatBtn()
        initSuffleBtn()
        setBlurImageBackground()
        updateSeekbar()
    }

    fun setBlurImageBackground() {
        val displayMetrics = DisplayMetrics()
        activity?.getWindowManager()?.getDefaultDisplay()?.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels
        activity?.let {
            Utils.loadBlurImageBanner(
                activity!!,
                width,
                height,
                drawableIdDefaulImage,
                img_background
            )
        }
    }

    fun setAction() {
        btn_play_and_pause_play_song.setOnClickListener { view -> actionBtnPlay() }
        btn_next_play_song.setOnClickListener { view -> actionNext() }
        btn_previous_play_song.setOnClickListener { view -> actionPrevious() }
        btn_repeat_play_song.setOnClickListener { view -> actionRepeat() }
        seekbar.setOnSeekBarChangeListener(this)
        btn_suffle_play_song.setOnClickListener { view -> actionSuffle() }
        img_song_queue.setOnClickListener { view -> actionShowQueue() }
        img_back_play_song_fragment.setOnClickListener { view -> actionBack() }
        img_add_to_playlist.setOnClickListener { view -> actionAdd() }
    }

    private fun actionAdd() {
        val builder = AlertDialog.Builder(context!!)
        val dialogView = layoutInflater.inflate(R.layout.popup_add_to_playlist, null)
        builder.setView(dialogView)
        alertDialog = builder.create()
        dialogView.rv_playlists_add_to_playlis.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            addAdapter = AddToPlaylistAdapter(context, mainActivity?.playlists!!)
            addAdapter?.setItemClick(this@PlaySongFragment)
            adapter = addAdapter
        }

        alertDialog?.show()
    }

    private fun actionBack() {
        fragmentManager?.popBackStack()
    }

    private fun actionShowQueue() {
        showSongQueue()
    }

    fun showSongQueue() {
        val bottomSheetFragment = BottomSheetFragment.newInstance()
        bottomSheetFragment.show(mainActivity?.fragmentManager, "")
    }

    private fun actionSuffle() {
        if (mainActivity!!.musicService!!.isSuffle) {
            mainActivity?.musicService?.isSuffle = false
            Toast.makeText(mContext, getString(R.string.suffle_off), Toast.LENGTH_SHORT).show()
            setSuffleOff()
        } else {
            Toast.makeText(mContext, getString(R.string.suffle_on), Toast.LENGTH_SHORT).show()
            mainActivity?.musicService?.isSuffle = true
            setSuffleOn()
        }
    }

    private fun actionRepeat() {
        if (mainActivity?.musicService?.currenRepeat == Repeat.NONE.value) {
            repeatOne()
        } else if (mainActivity?.musicService?.currenRepeat == Repeat.REPEAT_ONE.value) {
            repeatAll()
        } else {
            noRepeat()
        }
        initRepeatBtn()
    }

    fun repeatOne() {
        Toast.makeText(mContext, getString(R.string.repeat_one), Toast.LENGTH_SHORT).show()
        mainActivity?.musicService?.isRepeatOne = true
        mainActivity!!.musicService!!.currenRepeat++
        mainActivity?.musicService?.isRepeatAll = false
        mediaPlayer?.isLooping = true
    }

    fun repeatAll() {
        Toast.makeText(mContext, getString(R.string.repeat_all), Toast.LENGTH_SHORT).show()
        mainActivity?.musicService?.isRepeatOne = false
        mainActivity!!.musicService!!.currenRepeat++
        mainActivity?.musicService?.isRepeatAll = true
        mediaPlayer?.isLooping = false
    }

    fun noRepeat() {
        Toast.makeText(mContext, getString(R.string.repeat_off), Toast.LENGTH_SHORT).show()
        mainActivity?.musicService?.isRepeatOne = false
        mainActivity?.musicService?.currenRepeat = 0
        mediaPlayer?.isLooping = false
        mainActivity?.musicService?.isRepeatAll = false
    }

    private fun actionPrevious() {
        mainActivity?.musicService?.actionPrevious()
    }

    private fun actionNext() {
        mainActivity?.musicService?.actionNext()
    }

    fun actionBtnPlay() {
        if (mediaPlayer!!.isPlaying) {
            handler.removeCallbacks(runnable)
        } else {
            handler.postDelayed(runnable, 500)
        }
        mainActivity?.musicService?.actionBtnPlay()
    }

    fun initRepeatBtn() {
        if (mainActivity?.musicService?.currenRepeat == Repeat.REPEAT_ONE.value) {
            setImageRepeatOneMode()
        } else if (mainActivity?.musicService?.currenRepeat == Repeat.REPEAT_ALL.value) {
            setImageRepeatAllMode()
        } else {
            setImageNoRepeatMode()
        }
    }

    fun setImageRepeatOneMode() {
        btn_repeat_play_song?.setImageResource(R.drawable.ic_play_repeat_one_white)
        btn_repeat_play_song?.setColorFilter(
            ActivityCompat.getColor(
                mContext!!,
                R.color.colorAccent
            )
        )
    }

    fun setImageRepeatAllMode() {
        btn_repeat_play_song?.setImageResource(R.drawable.ic_play_repeat_white)
        btn_repeat_play_song?.setColorFilter(
            ActivityCompat.getColor(
                mContext!!,
                R.color.colorAccent
            )
        )
    }

    fun setImageNoRepeatMode() {
        btn_repeat_play_song?.setImageResource(R.drawable.ic_play_repeat_white)
        btn_repeat_play_song?.setColorFilter(
            ActivityCompat.getColor(
                mContext!!,
                R.color.white
            )
        )
    }

    fun initSuffleBtn() {
        if (mainActivity!!.musicService!!.isSuffle) {
            setSuffleOn()
        } else {
            setSuffleOff()
        }
    }

    fun setSuffleOn() {
        btn_suffle_play_song?.setImageResource(R.drawable.ic_play_shuffle_white)
        btn_suffle_play_song?.setColorFilter(
            ActivityCompat.getColor(
                mContext!!,
                R.color.colorAccent
            )
        )
    }

    fun setSuffleOff() {
        btn_suffle_play_song?.setImageResource(R.drawable.ic_play_shuffle_white)
        btn_suffle_play_song?.setColorFilter(
            ActivityCompat.getColor(
                mContext!!,
                R.color.white
            )
        )
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable)
    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(runnable, 500)
    }

    fun updateSeekbar() {
        if(mediaPlayer!!.isPlaying){
            var currentPos = mediaPlayer?.currentPosition
            if (currentPos!! < 0) {
                currentPos = mainActivity?.musicService?.currentDuration!!.toInt()
                tv_realtime_song?.text =
                    Utils.convertSongDuration(mainActivity?.musicService?.currentDuration!!)
                seekbar?.progress = currentPos
            } else {
                tv_realtime_song?.text = Utils.convertSongDuration(currentPos.toLong())
                seekbar?.progress = currentPos
            }

            if (isVisible) {
                handler.postDelayed(runnable, 500)
            }
        }

    }

    var handler = Handler()
    var runnable: Runnable = Runnable {
        updateSeekbar()
    }

    fun setSongSelected(songId: String) {
        mainActivity?.musicService?.songList?.forEach {
            it.isSelected = false
        }
        for (i in 0 until mainActivity!!.musicService?.songList!!.size) {
            mainActivity?.musicService?.songList!![i].isSelected =
                mainActivity!!.musicService?.songList!![i].songId.equals(songId)
        }
        updateSongSelected(songId)
    }

    fun updateSongSelected(songId: String) {
        val intent = Intent(ACTION_UPDATE_SONG)
        intent.putExtra(EXTRA_SONG_ID, songId)
        LocalBroadcastManager.getInstance(mainActivity!!.applicationContext).sendBroadcast(intent)

    }

    fun registUpdateView() {
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    PlayMusicService.ACTION_UPDATE_VIEW -> {
                        songPosition = mainActivity?.musicService?.songPos!!
                        setDataForView()
                    }
                }
            }
        }

        val intentFilter = IntentFilter()
        intentFilter.addAction(PlayMusicService.ACTION_UPDATE_VIEW)
        LocalBroadcastManager.getInstance(context!!).registerReceiver(receiver!!, intentFilter)
    }

    fun unregistUpdateSongSelected() {
        receiver?.let {
            LocalBroadcastManager.getInstance(context!!).unregisterReceiver(receiver!!)
        }
    }
}
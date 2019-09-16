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
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.android.synthetic.main.fragment_playsong.*
import minh.quy.musicplayer.R
import minh.quy.musicplayer.Utils.Utils
import minh.quy.musicplayer.activity.MainActivity
import minh.quy.musicplayer.service.PlayMusicService
import kotlin.random.Random


class PlaySongFragment : Fragment(),
    MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener, View.OnTouchListener {


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
        mediaPlayer?.setOnCompletionListener(this)
        registItemBottomClick()


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

    override fun onCompletion(p0: MediaPlayer?) {
        mediaPlayer?.stop()
        mediaPlayer?.reset()
        Log.d("minhnh", "onCompletion")

        //repeat one mode
        if (mainActivity!!.isRepeatOne) {
            mainActivity?.musicService?.mediaPlayer?.isLooping = true
            playSong()
            return
        }

        // suffle modepaus
        if (mainActivity!!.isSuffle) {
            songPosition = Random.nextInt(mainActivity?.songsQueueList!!.size)
            playSong()
            if (isVisible) {
                setDataForView()
            }
            return
        }
        // repeat all mode or normal mode
        songPosition++
        if (songPosition > mainActivity?.songsQueueList!!.size - 1) {
            if (mainActivity!!.isRepeatAll) {
                songPosition = 0
                playSong()
                if (isVisible) {
                    setDataForView()
                }
            }
        } else {
            playSong()
            if (isVisible) {
                setDataForView()
            }
        }
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

    fun setDataForView() {
        if (mainActivity!!.isFirstPlay) {
            mainActivity?.musicService?.setSongPosition(songPosition)
            mainActivity?.musicService?.playMusic()
            mediaPlayer?.seekTo(mainActivity?.currentDuration!!.toInt())
            btn_play_and_pause_play_song?.setImageResource(R.drawable.ic_play_pause_white)
        }
        mainActivity!!.isFirstPlay = false
        val defaultPositionImage = Utils.getPositionDefaultImage(songPosition)
        drawableIdDefaulImage = Utils.getDrawableIdDefaultImage(defaultPositionImage)
        img_ava_song?.setImageResource(drawableIdDefaulImage)
        tv_song_name_play_song?.text = mainActivity?.songsQueueList?.get(songPosition)?.songName
        tv_song_name_play_song?.isSelected
        tv_artist_name_play_song?.text = mainActivity?.songsQueueList?.get(songPosition)?.artistName
        tv_total_time_song?.text =
            Utils.convertSongDuration(mainActivity?.songsQueueList?.get(songPosition)?.duration!!.toLong())

        seekbar?.max = mainActivity?.musicService?.mediaPlayer!!.duration
        Log.d("minhse", seekbar?.max.toString())
        if (mediaPlayer!!.isPlaying) {
            btn_play_and_pause_play_song?.setImageResource(R.drawable.ic_play_pause_white)
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
    }

    private fun actionShowQueue() {
        showSongQueue()
    }

    fun showSongQueue() {
        val bottomSheetFragment = BottomSheetFragment.newInstance()
        bottomSheetFragment.show(mainActivity?.fragmentManager, "")
    }

    private fun actionSuffle() {
        if (mainActivity!!.isSuffle) {
            mainActivity?.isSuffle = false
            Toast.makeText(mContext, getString(R.string.suffle_off), Toast.LENGTH_SHORT).show()
            setSuffleOff()
        } else {
            Toast.makeText(mContext, getString(R.string.suffle_on), Toast.LENGTH_SHORT).show()
            mainActivity?.isSuffle = true
            setSuffleOn()
        }
    }

    private fun actionRepeat() {
        if (mainActivity?.currenRepeat == Repeat.NONE.value) {
            repeatOne()
        } else if (mainActivity?.currenRepeat == Repeat.REPEAT_ONE.value) {
            repeatAll()
        } else {
            noRepeat()
        }
        initRepeatBtn()
    }

    fun repeatOne() {
        Toast.makeText(mContext, getString(R.string.repeat_one), Toast.LENGTH_SHORT).show()
        mainActivity?.isRepeatOne = true
        mainActivity!!.currenRepeat++
        mainActivity?.isRepeatAll = false
        mediaPlayer?.isLooping = true
    }

    fun repeatAll() {
        Toast.makeText(mContext, getString(R.string.repeat_all), Toast.LENGTH_SHORT).show()
        mainActivity?.isRepeatOne = false
        mainActivity!!.currenRepeat++
        mainActivity?.isRepeatAll = true
        mediaPlayer?.isLooping = false
    }

    fun noRepeat() {
        Toast.makeText(mContext, getString(R.string.repeat_off), Toast.LENGTH_SHORT).show()
        mainActivity?.isRepeatOne = false
        mainActivity?.currenRepeat = 0
        mediaPlayer?.isLooping = false
        mainActivity?.isRepeatAll = false
    }

    private fun actionPrevious() {
        if (songPosition == 0) {
            songPosition = 0
            mainActivity?.musicService?.setSongPosition(songPosition)
        } else {
            mainActivity?.musicService?.setSongPosition(songPosition - 1)
            songPosition--
        }
        btn_play_and_pause_play_song.setImageResource(R.drawable.ic_play_pause_white)
        playSong()
        setDataForView()
    }

    private fun actionNext() {
        if (songPosition < mainActivity?.songsQueueList!!.size - 1) {
            mainActivity?.musicService?.setSongPosition(songPosition + 1)
            songPosition++
        } else {
            mainActivity?.musicService?.setSongPosition(mainActivity?.songsQueueList!!.size - 1)
        }
        btn_play_and_pause_play_song.setImageResource(R.drawable.ic_play_pause_white)
        playSong()
        setDataForView()
    }

    fun actionBtnPlay() {
        if (mediaPlayer!!.isPlaying) {
            btn_play_and_pause_play_song.setImageResource(R.drawable.ic_play_play_white)
            mediaPlayer?.pause()
            handler.removeCallbacks(runnable)
        } else {
            btn_play_and_pause_play_song.setImageResource(R.drawable.ic_play_pause_white)
            mediaPlayer?.start()
            handler.postDelayed(runnable, 500)
        }
    }

    fun initRepeatBtn() {
        if (mainActivity?.currenRepeat == Repeat.REPEAT_ONE.value) {
            setImageRepeatOneMode()
        } else if (mainActivity?.currenRepeat == Repeat.REPEAT_ALL.value) {
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
        if (mainActivity!!.isSuffle) {
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

    fun playSong() {
        mainActivity?.musicService?.setSongPosition(songPosition)
        mainActivity?.songsQueueList!!.get(songPosition).songId?.let { setSongSelected(it) }
        mainActivity?.musicService?.playMusic()
        if (seekbar != null) {
            seekbar.progress = 0
        }
        updateSeekbar()
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
        var currentPos = mediaPlayer?.currentPosition
        if (currentPos!! < 0) {
            currentPos = mainActivity?.currentDuration!!.toInt()
            tv_realtime_song?.text = Utils.convertSongDuration(mainActivity?.currentDuration!!)
            seekbar?.progress = currentPos
        } else {
            tv_realtime_song?.text = Utils.convertSongDuration(currentPos.toLong())
            seekbar?.progress = currentPos
        }

        if (isVisible ) {
            handler.postDelayed(runnable, 500)
        }
    }

    var handler = Handler()
    var runnable: Runnable = Runnable {
        updateSeekbar()
    }

    fun setSongSelected(songId: String) {
        mainActivity?.songsQueueList?.forEach {
            it.isSelected = false
        }
        for (i in 0 until mainActivity!!.songsQueueList.size) {
            mainActivity?.songsQueueList!![i].isSelected =
                mainActivity!!.songsQueueList[i].songId.equals(songId)
        }
        updateSongSelected(songId)

    }

    fun updateSongSelected(songId: String) {
        val intent = Intent(ACTION_UPDATE_SONG)
        intent.putExtra(EXTRA_SONG_ID, songId)
        LocalBroadcastManager.getInstance(mainActivity!!.applicationContext).sendBroadcast(intent)

    }

    fun registItemBottomClick() {
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when(intent?.action){
                    BottomSheetFragment.ACITON_ITEM_BOTTOM_CLICK -> {
                        mainActivity?.currenSongId =
                            intent?.getStringExtra(BottomSheetFragment.EXTRA_SONG_ID)
                        mainActivity?.currenSongId?.let {
                            for (i in 0 until mainActivity?.songsQueueList?.size!!) {
                                if (mainActivity?.songsQueueList!![i].songId.equals(mainActivity?.currenSongId)) {
                                    songPosition = i
                                }
                            }
                        }
                        setDataForView()
                        playSong()
                    }
                    PlayMusicService.ACTION_UPDATE_VIEW -> {
                        songPosition = mainActivity?.musicService?.songPos!!
                        setDataForView()
                    }
                }

            }
        }

        val intentFilter = IntentFilter(BottomSheetFragment.ACITON_ITEM_BOTTOM_CLICK)
        LocalBroadcastManager.getInstance(context!!).registerReceiver(receiver!!, intentFilter)
    }

    fun unregistUpdateSongSelected() {
        receiver?.let {
            LocalBroadcastManager.getInstance(context!!).unregisterReceiver(receiver!!)
        }
    }
}
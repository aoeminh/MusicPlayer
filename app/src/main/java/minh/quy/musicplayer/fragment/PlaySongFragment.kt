package minh.quy.musicplayer.fragment

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_playsong.*
import minh.quy.musicplayer.R
import minh.quy.musicplayer.Utils.Utils
import minh.quy.musicplayer.activity.MainActivity
import kotlin.random.Random


class PlaySongFragment : Fragment(),
    MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener {


    enum class Repeat(val value: Int) {
        NONE(0),
        REPEAT_ONE(1),
        REPEAT_ALL(2),
    }

    companion object {
        val EXTRA_POSITION = "extra.position"
        fun newInstance(positin: Int): PlaySongFragment {
            val bundle = Bundle()
            val fragment = PlaySongFragment()
            bundle.putInt(EXTRA_POSITION, positin)
            fragment.arguments = bundle
            return fragment
        }
    }

    var mainActivity: MainActivity? = null
    var mContext: Context? = null
    var mediaPlayer: MediaPlayer? = null
    var songPosition = 0
    var drawableIdDefaulImage = 0

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

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =
            inflater.inflate(R.layout.fragment_playsong, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mediaPlayer?.setOnCompletionListener(this)
        isVisible
        Log.d("minhnh", "" + isVisible)
        if (isVisible) {
            setDataForView()
        }
        setAction()
    }

    override fun onCompletion(p0: MediaPlayer?) {
        mediaPlayer?.stop()
        mediaPlayer?.reset()
        Log.d("minhnh", "onCompletion")

        // suffle mode
        if (mainActivity!!.isSuffle) {
            songPosition = Random.nextInt(mainActivity?.songlist!!.size)
            mainActivity?.musicService?.setSongPosition(songPosition)
            playSong()
            if (isVisible) {
                setDataForView()
            }
            return
        }

        songPosition++
        if (songPosition > mainActivity?.songlist!!.size - 1) {
            if (mainActivity!!.isRepeatAll) {
                songPosition = 0
                mainActivity?.musicService?.setSongPosition(songPosition)
                playSong()
                if (isVisible) {
                    setDataForView()
                }
            }
        } else {
            mainActivity?.musicService?.setSongPosition(songPosition)
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

    fun setDataForView() {
        Log.d("minhnh", "setDataForView")
        val defaultPositionImage = Utils.getPositionDefaultImage(songPosition)
        drawableIdDefaulImage = Utils.getDrawableIdDefaultImage(defaultPositionImage)
        img_ava_song.setImageResource(drawableIdDefaulImage)
        tv_song_name_play_song.text = mainActivity?.songlist?.get(songPosition)?.songName
        tv_song_name_play_song.isSelected
        tv_artist_name_play_song.text = mainActivity?.songlist?.get(songPosition)?.artistName
        tv_total_time_song.text =
            Utils.convertSongDuration(mainActivity?.songlist?.get(songPosition)?.duration!!.toLong())

        seekbar.max = mainActivity?.musicService?.mediaPlayer!!.duration
        if (mediaPlayer!!.isPlaying) {
            btn_play_and_pause_play_song.setImageResource(R.drawable.ic_play_pause_white)
        }
        initRepeatBtn()
        setBlurImageBackground()
        updateSeekbar()
    }

    fun setBlurImageBackground() {
        val displayMetrics = DisplayMetrics()
        activity?.getWindowManager()?.getDefaultDisplay()?.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels
        Utils.loadBlurImageBanner(activity!!, width, height, drawableIdDefaulImage, img_background)
    }

    fun setAction() {
        btn_play_and_pause_play_song.setOnClickListener { view -> actionBtnPlay() }
        btn_next_play_song.setOnClickListener { view -> actionNext() }
        btn_previous_play_song.setOnClickListener { view -> actionPrevious() }
        btn_repeat_play_song.setOnClickListener { view -> actionRepeat() }
        seekbar.setOnSeekBarChangeListener(this)
        btn_suffle_play_song.setOnClickListener { view -> actionSuffle() }
    }

    private fun actionSuffle() {
        Log.d("minhnh", "actionSuffle")
        if (mainActivity!!.isSuffle) {
            Toast.makeText(mContext, getString(R.string.suffle_off), Toast.LENGTH_SHORT).show()
            mainActivity?.isSuffle = false
            btn_suffle_play_song?.setImageResource(R.drawable.ic_play_shuffle_white)
            btn_suffle_play_song?.setColorFilter(
                ActivityCompat.getColor(
                    mContext!!,
                    R.color.white
                )
            )
        } else {
            Toast.makeText(mContext, getString(R.string.suffle_on), Toast.LENGTH_SHORT).show()
            mainActivity?.isSuffle = true
            btn_suffle_play_song?.setImageResource(R.drawable.ic_play_shuffle_white)
            btn_suffle_play_song?.setColorFilter(
                ActivityCompat.getColor(
                    mContext!!,
                    R.color.colorAccent
                )
            )

        }

    }

    private fun actionRepeat() {
        Log.d("minhnh", "actionSuffle")
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
        if (songPosition < mainActivity?.songlist!!.size - 1) {
            mainActivity?.musicService?.setSongPosition(songPosition + 1)
            songPosition++
        } else {
            mainActivity?.musicService?.setSongPosition(mainActivity?.songlist!!.size - 1)
        }
        btn_play_and_pause_play_song.setImageResource(R.drawable.ic_play_pause_white)
        playSong()
        setDataForView()
    }

    fun actionBtnPlay() {
        if (mediaPlayer!!.isPlaying) {
            btn_play_and_pause_play_song.setImageResource(R.drawable.ic_play_play_white)
            mediaPlayer?.pause()
        } else {
            btn_play_and_pause_play_song.setImageResource(R.drawable.ic_play_pause_white)
            mediaPlayer?.start()
        }
    }

    fun initRepeatBtn() {
        if (mainActivity?.currenRepeat == Repeat.REPEAT_ONE.value) {
            btn_repeat_play_song.setImageResource(R.drawable.ic_play_repeat_one_white)
            btn_repeat_play_song.setColorFilter(
                ActivityCompat.getColor(
                    mContext!!,
                    R.color.colorAccent
                )
            )
        } else if (mainActivity?.currenRepeat == Repeat.REPEAT_ALL.value) {
            btn_repeat_play_song.setImageResource(R.drawable.ic_play_repeat_white)
            btn_repeat_play_song.setColorFilter(
                ActivityCompat.getColor(
                    mContext!!,
                    R.color.colorAccent
                )
            )
        } else {
            btn_repeat_play_song.setImageResource(R.drawable.ic_play_repeat_white)
            btn_repeat_play_song.setColorFilter(
                ActivityCompat.getColor(
                    mContext!!,
                    R.color.white
                )
            )
        }
    }

    fun playSong() {
        mainActivity?.musicService?.playMusic()
        if (seekbar != null) {
            seekbar.progress = 0
        }
        if (mainActivity!!.isRepeatOne) {
            mediaPlayer?.isLooping = true
        }
        updateSeekbar()
    }

    fun updateSeekbar() {
        val currentPos = mediaPlayer?.currentPosition
        tv_realtime_song?.text = Utils.convertSongDuration(currentPos!!.toLong())
        seekbar?.progress = currentPos
        handler.postDelayed(runnable, 500)
    }

    var handler = Handler()
    var runnable: Runnable = Runnable {
        updateSeekbar()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        Log.d("minhnh", "" + isVisibleToUser)
    }

}
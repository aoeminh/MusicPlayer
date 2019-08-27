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
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_playsong.*
import minh.quy.musicplayer.R
import minh.quy.musicplayer.Utils.Utils
import minh.quy.musicplayer.activity.MainActivity


class PlaySongFragment : Fragment(), MediaPlayer.OnPreparedListener,
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
    var currenRepeat = 0

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
        mediaPlayer = mainActivity?.musicService?.mediaPlayer
        mediaPlayer?.setOnPreparedListener(this)
        mediaPlayer?.setOnCompletionListener(this)
        setDataForView()
        setAction()
    }

    override fun onCompletion(p0: MediaPlayer?) {
        mediaPlayer?.stop()
        mediaPlayer?.reset()
    }

    override fun onPrepared(p0: MediaPlayer?) {
        Log.d("minhnh", "onPrepared")
        mediaPlayer?.start()

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
        tv_song_name_play_song.setText(mainActivity?.songlist?.get(songPosition)?.songName)
        tv_song_name_play_song.isSelected
        tv_artist_name_play_song.setText(mainActivity?.songlist?.get(songPosition)?.artistName)
        tv_total_time_song.setText(
            Utils.convertSongDuration(mainActivity?.songlist?.get(songPosition)?.duration!!.toLong())
        )
        seekbar.max = mainActivity?.musicService?.mediaPlayer!!.duration
        if (mediaPlayer!!.isPlaying) {
            btn_play_and_pause_play_song.setImageResource(R.drawable.ic_play_pause_white)
        }
        setBlurImageBackground()
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
    }

    private fun actionRepeat() {
        if (currenRepeat == Repeat.NONE.value) {
            currenRepeat++
            btn_repeat_play_song.setImageResource(R.drawable.ic_play_repeat_one_white)
            btn_repeat_play_song.setColorFilter(
                ActivityCompat.getColor(
                    mContext!!,
                    R.color.colorAccent
                )
            )
            mediaPlayer?.isLooping = true
        } else if (currenRepeat == Repeat.REPEAT_ONE.value) {
            currenRepeat++
            btn_repeat_play_song.setImageResource(R.drawable.ic_play_repeat_white)
            btn_repeat_play_song.setColorFilter(
                ActivityCompat.getColor(
                    mContext!!,
                    R.color.colorAccent
                )
            )

        } else {
            currenRepeat = 0
            btn_repeat_play_song.setImageResource(R.drawable.ic_play_repeat_white)
            btn_repeat_play_song.setColorFilter(ActivityCompat.getColor(mContext!!, R.color.white))

        }

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
        mainActivity?.musicService?.playMusic()
        setDataForView()
    }

    private fun actionNext() {
        if (songPosition < mainActivity?.songlist!!.size) {
            mainActivity?.musicService?.setSongPosition(songPosition + 1)
            songPosition++
        } else {
            mainActivity?.musicService?.setSongPosition(mainActivity?.songlist!!.size)
        }
        btn_play_and_pause_play_song.setImageResource(R.drawable.ic_play_pause_white)
        mainActivity?.musicService?.playMusic()
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

}
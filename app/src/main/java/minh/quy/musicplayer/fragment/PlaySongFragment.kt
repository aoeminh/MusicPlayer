package minh.quy.musicplayer.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_playsong.*
import minh.quy.musicplayer.Utils.Utils
import minh.quy.musicplayer.activity.MainActivity
import android.util.DisplayMetrics
import android.R


class PlaySongFragment : Fragment() {

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
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =
            inflater.inflate(minh.quy.musicplayer.R.layout.fragment_playsong, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setDataForView()
    }

    fun setDataForView(){
        val defaultPositionImage = Utils.getPositionDefaultImage(songPosition)
        drawableIdDefaulImage = Utils.getDrawableIdDefaultImage(defaultPositionImage)
        img_ava_song.setImageResource(drawableIdDefaulImage)
        tv_song_name_play_song.setText(mainActivity?.songlist?.get(songPosition)?.songName)
        tv_artist_name_play_song.setText(mainActivity?.songlist?.get(songPosition)?.artistName)
        tv_total_time_song.setText(
            Utils.convertSongDuration(mainActivity?.songlist?.get(songPosition)?.duration!!.toLong()))
        setBlurImageBackground()
    }

    fun setBlurImageBackground() {
        val displayMetrics = DisplayMetrics()
        activity?.getWindowManager()?.getDefaultDisplay()?.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels
        Utils.loadBlurImageBanner(activity!!, width, height, drawableIdDefaulImage, img_background)
    }
}
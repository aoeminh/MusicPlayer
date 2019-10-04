package minh.quy.musicplayer.fragment

import android.media.audiofx.Equalizer
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_equalizer.*
import minh.quy.musicplayer.R

class EqualizerFragment : BaseFragment() {

    companion object{
        fun newInstance() : EqualizerFragment{
            return EqualizerFragment()
        }
    }

    var numberFrequencyRange: Short? = null
    var lowEqualizerNumber: Short? = null
    var upperEqualizerNumber: Short? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_equalizer, container, false)
        mainActivity.musicService?.equalizer = Equalizer(
            0,
            mainActivity.musicService?.mediaPlayer?.audioSessionId!!
        )
        mainActivity.musicService?.equalizer?.enabled = true


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupEqualizerFxAndUI(view)

    }

    fun setupEqualizerFxAndUI(view: View) {
        //get number frequency bands supported by equalizer engine
        numberFrequencyRange = mainActivity.musicService?.equalizer?.numberOfBands

        //get level range to used in setting the band level
        // get lower limit of the range in milibels
        lowEqualizerNumber = mainActivity.musicService?.equalizer?.bandLevelRange?.get(0)
        // get upper limit of the range in milibels
        upperEqualizerNumber = mainActivity.musicService?.equalizer?.bandLevelRange?.get(1)

        for (i in 0 until numberFrequencyRange!! - 1) {
            val equalizerBandsIndex: Short = i.toShort()
            val frequencyHeader = TextView(context)
            frequencyHeader.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            frequencyHeader.gravity = Gravity.CENTER_HORIZONTAL
            frequencyHeader.text = String.format(
                resources.getString(
                    R.string.frequency_header,
                    mainActivity.musicService?.equalizer?.getCenterFreq(equalizerBandsIndex)?.div(
                        1000
                    )
                )
            )
            container_equalizer_fragment.addView(frequencyHeader)

        }

    }


}
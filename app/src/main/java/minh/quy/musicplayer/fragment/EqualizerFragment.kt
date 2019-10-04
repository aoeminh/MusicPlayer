package minh.quy.musicplayer.fragment

import android.media.audiofx.Equalizer
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_equalizer.*
import minh.quy.musicplayer.R
import org.w3c.dom.Text

class EqualizerFragment : BaseFragment() {

    companion object {
        fun newInstance(): EqualizerFragment {
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
        setupEqualizerFxAndUI()
        createEqualizer(view)

    }

    fun setupEqualizerFxAndUI() {
        //get number frequency bands supported by equalizer engine
        numberFrequencyRange = mainActivity.musicService?.equalizer?.numberOfBands

        //get level range to used in setting the band level
        // get lower limit of the range in milibels
        lowEqualizerNumber = mainActivity.musicService?.equalizer?.bandLevelRange?.get(0)
        // get upper limit of the range in milibels
        upperEqualizerNumber = mainActivity.musicService?.equalizer?.bandLevelRange?.get(1)

        for (i in 0 until numberFrequencyRange!!) {
            val equalizerBandsIndex: Short = i.toShort()
            val frequencyHeader = TextView(context)
            frequencyHeader.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            frequencyHeader.gravity = Gravity.CENTER_HORIZONTAL
            frequencyHeader.setTextColor(resources.getColor(R.color.color_text))
            frequencyHeader.text = String.format(
                resources.getString(
                    R.string.frequency_header,
                    mainActivity.musicService?.equalizer?.getCenterFreq(equalizerBandsIndex)?.div(
                        1000
                    )
                )
            )
            container_equalizer_fragment.addView(frequencyHeader)
            createSeekBarLayout(equalizerBandsIndex)
        }

    }

    fun createSeekBarLayout(equalizerIndex: Short) {
        val seekbarLayout = LinearLayout(context)
        seekbarLayout.layoutParams =
            ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100)
        seekbarLayout.orientation = LinearLayout.HORIZONTAL
        val lowerBandsText = creatLowerBands()
        val upperBandsText = createUpperBands()
        val seekBar = createSeekbar(equalizerIndex.toInt(), equalizerIndex)
        seekbarLayout.addView(lowerBandsText)
        seekbarLayout.addView(seekBar)
        seekbarLayout.addView(upperBandsText)
        container_equalizer_fragment.addView(seekbarLayout)
    }

    fun creatLowerBands(): TextView {
        val lowerBandsText = TextView(context)
        lowerBandsText.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        lowerBandsText.setTextColor(resources.getColor(R.color.color_text))
        lowerBandsText.text = String.format(
            resources.getString(
                R.string.frequency_lower,
                lowEqualizerNumber?.div(100)
            )
        )
        return lowerBandsText
    }

    fun createUpperBands(): TextView {
        val upperBandsText = TextView(context)
        upperBandsText.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        upperBandsText.setTextColor(resources.getColor(R.color.color_text))
        upperBandsText.text = String.format(
            resources.getString(
                R.string.frequency_upper,
                upperEqualizerNumber?.div(100)
            )
        )
        return upperBandsText
    }

    fun createSeekbar(id: Int, equalizerIndex: Short): SeekBar {
        val seekbar = SeekBar(context)
        seekbar.id = id
        val seekbarLayoutParam = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        seekbarLayoutParam.weight = 1F
        seekbar.layoutParams = seekbarLayoutParam
        seekbar.max = lowEqualizerNumber?.let { upperEqualizerNumber?.minus(it) }!!
        seekbar.progress =
            mainActivity.musicService?.equalizer?.getBandLevel(equalizerIndex)!!.toInt()
        seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                mainActivity.musicService?.equalizer?.setBandLevel(
                    equalizerIndex,
                    (progress + lowEqualizerNumber!!).toShort()
                )
                Toast.makeText(
                    context,
                    String.format(
                        resources.getString(R.string.frequency_lower),
                        (progress)
                    ),
                    Toast.LENGTH_SHORT
                ).show()

            }
        })

        return seekbar
    }

    fun createEqualizer(view1: View) {
        val equalizerPresetNames = arrayListOf<String>()

        //init equalizerPresetNames
        for (i in 0 until mainActivity.musicService?.equalizer?.numberOfPresets!!.minus(1)) {
            mainActivity.musicService?.equalizer?.getPresetName(i.toShort())?.let {
                equalizerPresetNames.add(
                    it
                )
            }
        }
        val equalizerSpinnerAdapter =
            ArrayAdapter<String>(
                context!!,
                android.R.layout.simple_spinner_item,
                equalizerPresetNames
            )
        equalizerSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner_equalizer.adapter = equalizerSpinnerAdapter

        spinner_equalizer.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                mainActivity.musicService?.equalizer?.usePreset(position.toShort())
                val numberFrequencyBands = mainActivity.musicService?.equalizer?.numberOfBands
                for (i in 0 until numberFrequencyBands!!) {
                    val seekBar = view1.findViewById<SeekBar>(i)
                    seekBar?.progress =
                        mainActivity.musicService?.equalizer?.getBandLevel(i.toShort())!!.minus(
                            lowEqualizerNumber!!
                        )
                }


            }

        }

    }

}
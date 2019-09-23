package minh.quy.musicplayer.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet_fragment.*
import minh.quy.musicplayer.R
import minh.quy.musicplayer.activity.MainActivity
import minh.quy.musicplayer.adapter.BottomSheetAdapter
import minh.quy.musicplayer.main
import minh.quy.musicplayer.service.PlayMusicService

class BottomSheetFragment() : BottomSheetDialogFragment(),
    BottomSheetAdapter.IFunctionBottomFragment {

    companion object {
        val ACITON_ITEM_BOTTOM_CLICK = "action.item.bottom.click"
        val EXTRA_SONG_ID = "action.song.id"
        fun newInstance(): BottomSheetFragment {
            val fragment = BottomSheetFragment()
            return fragment
        }
    }

    var mAdapter: BottomSheetAdapter? = null
    var mainActivity: MainActivity? = null
    var receiver: BroadcastReceiver? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (activity is MainActivity) {
            mainActivity = activity as MainActivity
        }
        registUpdateSongSelected()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_fragment, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAdapter = BottomSheetAdapter(context!!, mainActivity!!.musicService?.songList!!)
        mAdapter?.setOnItemCommonClick(this)
        rv_list_bottom_sheet.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = mAdapter
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        unregistUpdateSongSelected()
    }
    override fun chooseSong(position: Int) {
        playSong(position)
        mAdapter?.notifyDataSetChanged()

    }

    override fun deleteSong(position: Int) {
        if (!mainActivity!!.musicService?.songList?.get(position)?.isSelected!!) {
            mainActivity?.musicService?.songList?.removeAt(position)
            mAdapter?.notifyDataSetChanged()
        }
    }

    fun setSongSelected() {
        mAdapter?.notifyDataSetChanged()
    }


    fun registUpdateSongSelected() {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
               when(intent?.action){
                   //update when next song from service
                   PlayMusicService.ACTION_UPDATE_VIEW -> setSongSelected()
               }
            }
        }

        LocalBroadcastManager.getInstance(context!!)
            .registerReceiver(receiver, IntentFilter(PlayMusicService.ACTION_UPDATE_VIEW))

    }

    fun unregistUpdateSongSelected(){
        receiver?.let {
            LocalBroadcastManager.getInstance(context!!).unregisterReceiver(receiver!!)
        }
    }

    fun playSong(postion: Int) {
        mainActivity?.musicService?.setSongPosition(postion)
        mainActivity?.musicService?.playMusic()
    }


}
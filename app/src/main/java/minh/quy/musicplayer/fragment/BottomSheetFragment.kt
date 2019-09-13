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
        mAdapter = BottomSheetAdapter(context!!, mainActivity!!.songsQueueList)
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
        val intent = Intent(ACITON_ITEM_BOTTOM_CLICK)
        intent.putExtra(EXTRA_SONG_ID, mainActivity?.songsQueueList?.get(position)?.songId)
        LocalBroadcastManager.getInstance(context!!).sendBroadcast(intent)
        mAdapter?.notifyDataSetChanged()

    }

    override fun deleteSong(position: Int) {
        if (!mainActivity!!.songsQueueList.get(position).isSelected) {
            mainActivity?.songsQueueList?.removeAt(position)
            mAdapter?.notifyItemRemoved(position)
        }
    }

    fun setSongSelected(songId: String) {
        for (i in 0 until mainActivity?.songsQueueList!!.size) {
            mainActivity?.songsQueueList!![i].isSelected =
                mainActivity?.songsQueueList!![i].songId.equals(songId)
        }
        mAdapter?.notifyDataSetChanged()
    }


    fun registUpdateSongSelected() {
        LocalBroadcastManager.getInstance(context!!)
            .registerReceiver(receiver, IntentFilter(PlaySongFragment.ACTION_UPDATE_SONG))

    }

    fun unregistUpdateSongSelected(){
        LocalBroadcastManager.getInstance(context!!).unregisterReceiver(receiver)
    }

    val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                setSongSelected(it.getStringExtra(PlaySongFragment.EXTRA_SONG_ID)!!)
            }
        }
    }

}
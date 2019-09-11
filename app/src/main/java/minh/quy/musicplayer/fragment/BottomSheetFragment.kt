package minh.quy.musicplayer.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet_fragment.*
import minh.quy.musicplayer.R
import minh.quy.musicplayer.action.OnItemCommonClick
import minh.quy.musicplayer.activity.MainActivity
import minh.quy.musicplayer.adapter.BottomSheetAdapter
import minh.quy.musicplayer.model.Song

class BottomSheetFragment() : BottomSheetDialogFragment(),OnItemCommonClick {

    companion object {
        fun newInstance(): BottomSheetFragment {
            val fragment = BottomSheetFragment()
            return fragment
        }
    }

    var mAdapter: BottomSheetAdapter? = null
    var mainActivity: MainActivity? = null
    var onClickItemBottomSheet: OnClickItemBottomSheet? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (activity is MainActivity) {
            mainActivity = activity as MainActivity
        }
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
        rv_list_bottom_sheet.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = mAdapter
        }
    }

    override fun onItemClick(postion: Int) {
        onClickItemBottomSheet?.onClickItemBottomSheet(mainActivity!!.songsQueueList[postion])
    }

    fun setOnClickBottomSheet(onClickItemBottomSheet: OnClickItemBottomSheet){
        this.onClickItemBottomSheet = onClickItemBottomSheet
    }

    interface OnClickItemBottomSheet {
        fun onClickItemBottomSheet(song: Song)
    }


}
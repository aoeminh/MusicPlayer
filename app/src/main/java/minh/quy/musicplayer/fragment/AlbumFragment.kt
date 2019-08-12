package minh.quy.musicplayer.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_album.*
import minh.quy.musicplayer.R
import minh.quy.musicplayer.activity.MainActivity
import minh.quy.musicplayer.adapter.AlbumAdapter
import minh.quy.musicplayer.decoration.ItemAlbumDecoration

class AlbumFragment : Fragment() {

    var alBumAdater: AlbumAdapter? = null
    lateinit var alBumContext: Context
    var mainActivity: MainActivity? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        alBumContext = context!!
        if(activity is MainActivity){
            mainActivity = activity as MainActivity
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_album, container, false)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initRecyclerView()

    }

    fun initRecyclerView(){
        rv_album_list.apply {
            layoutManager = GridLayoutManager(alBumContext,2,RecyclerView.VERTICAL,false)
            alBumAdater = AlbumAdapter(alBumContext)
            alBumAdater?.addAlbumList(mainActivity!!.albumList)
            adapter = alBumAdater
            addItemDecoration(ItemAlbumDecoration(30))
        }
    }
}
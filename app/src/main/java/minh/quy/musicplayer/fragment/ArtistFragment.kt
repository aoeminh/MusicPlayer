package minh.quy.musicplayer.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_artist.*
import minh.quy.musicplayer.R
import minh.quy.musicplayer.activity.MainActivity
import minh.quy.musicplayer.adapter.ArtistAdapter

class ArtistFragment : Fragment() {

    var artistAdapter: ArtistAdapter? = null
    lateinit var artisFragmenttContext: Context
    var mainActivity: MainActivity? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        artisFragmenttContext = context!!
        if (activity is MainActivity) {
            mainActivity = activity as MainActivity
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_artist, container, false)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        rv_artist_fragment.apply {
            layoutManager = LinearLayoutManager(artisFragmenttContext,RecyclerView.VERTICAL,false)
            artistAdapter = ArtistAdapter(artisFragmenttContext)
            artistAdapter?.addArtistList(mainActivity!!.artistList)
            adapter = artistAdapter

        }
    }
}
package minh.quy.musicplayer.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_playlist.*
import kotlinx.android.synthetic.main.popup_create_new_playlist.view.*
import minh.quy.musicplayer.R
import minh.quy.musicplayer.activity.MainActivity
import minh.quy.musicplayer.adapter.PlaylistAdapter
import minh.quy.musicplayer.contract.IPlaylistView
import minh.quy.musicplayer.funtiontoolbar.FunctionToolbarPlaylist
import minh.quy.musicplayer.model.Playlist
import minh.quy.musicplayer.presenter.PlaylistPresenter


class PlaylistFragment : Fragment(), IPlaylistView, FunctionToolbarPlaylist {

    var adapterPlaylist: PlaylistAdapter? = null
    lateinit var mainActivity: MainActivity
    var playlists: MutableList<Playlist> = arrayListOf()
    var presenter: PlaylistPresenter? = null
    var contextPlaylist: Context? = null
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (activity is MainActivity) {
            mainActivity = activity as MainActivity
        }
        contextPlaylist = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = PlaylistPresenter(this)
        presenter?.getAllPlaylist(mainActivity.musicDatabase)
        mainActivity.setFunctionPlaylist(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_playlist, container, false)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initRecyclerView()

    }

    override fun onResponseAllPlaylist(liveData: LiveData<MutableList<Playlist>>?) {
        liveData?.observe(this, Observer<MutableList<Playlist>> {
            Log.d("MinhNQ", it.size.toString())
            adapterPlaylist?.addPlaylists(it)
            playlists.clear()
            playlists.addAll(it)
        })
    }

    override fun onResponseInserPlaylist(isSuccess: Boolean) {
        if(isSuccess){
            Toast.makeText(contextPlaylist,"Creat new Playlist success",Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(contextPlaylist,"Creat fail, please try again",Toast.LENGTH_SHORT).show()
        }

    }

    override fun createNewPlaylist() {
        showPopupCreatePlaylist()
    }

    override fun sortPlaylist() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun sufflePlaylist() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun initRecyclerView() {
        rv_playlist.apply {
            layoutManager = LinearLayoutManager(contextPlaylist, RecyclerView.VERTICAL, false)
            adapterPlaylist = PlaylistAdapter(contextPlaylist!!)
            adapterPlaylist?.addPlaylists(playlists)
            adapter = adapterPlaylist

        }
    }

    fun showPopupCreatePlaylist() {
        val alertDialogBuilder = AlertDialog.Builder(contextPlaylist!!)
        val dialogView = layoutInflater.inflate(R.layout.popup_create_new_playlist, null)
        alertDialogBuilder.setView(dialogView)
        val alertDialog = alertDialogBuilder.create()
        dialogView.btn_create_playlist.setOnClickListener {
            if (!dialogView.edt_name_playlist.text.toString().isEmpty()) {
                presenter?.inserNewPlaylist(Playlist(dialogView.edt_name_playlist.text.toString()))
//                musicDatabase?.getPlaylistDAO()?.insertPlaylist(Playlist(dialogView.edt_name_playlist.text.toString()))
                Log.d("MinhNQ", " insert playlist")
                alertDialog.dismiss()
            } else {
                Toast.makeText(contextPlaylist, "Enter playlist name", Toast.LENGTH_SHORT).show()
            }

        }

        dialogView.btn_cancle_playlist.setOnClickListener { alertDialog.dismiss() }
        alertDialog.show()

    }
}
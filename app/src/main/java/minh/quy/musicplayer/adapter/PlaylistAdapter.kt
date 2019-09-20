package minh.quy.musicplayer.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_list_playlist.view.*
import minh.quy.musicplayer.Constant.Companion.MAX_DEFAULT_COUNT
import minh.quy.musicplayer.R
import minh.quy.musicplayer.Utils.Utils
import minh.quy.musicplayer.action.OnItemCommonClick
import minh.quy.musicplayer.model.Playlist

class PlaylistAdapter(var context: Context) : RecyclerView.Adapter<PlaylistAdapter.ViewHolder>() {
    var listPlaylist: MutableList<Playlist> = arrayListOf()
    var currenImage = 1
    var onItemCommonClick: OnItemCommonClick? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_list_playlist, parent, false)
        return ViewHolder(view)

    }

    override fun getItemCount(): Int {
        return listPlaylist.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.item.tv_name_item_list_playlist.text = listPlaylist.get(position).name
        if (position < MAX_DEFAULT_COUNT) {
            currenImage = position + 1
        } else {
            currenImage = Utils.getPositionDefaultImage(position)
        }
        holder.item.ava_item_list_playlist.setImageResource(
            Utils.getDrawableIdDefaultImage(
                currenImage
            )
        )
        holder.item.setOnClickListener {
            onItemCommonClick?.onItemClick(position)
        }
    }

    fun addPlaylists(playlists: MutableList<Playlist>) {
        currenImage = 1
        this.listPlaylist.clear()
        this.listPlaylist.addAll(playlists)
        notifyDataSetChanged()
    }

    fun setItemClick(onItemCommonClick: OnItemCommonClick) {
        this.onItemCommonClick = onItemCommonClick;
    }

    inner class ViewHolder(var item: View) : RecyclerView.ViewHolder(item) {

    }
}
package minh.quy.musicplayer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_list_add_to_playlist.view.*
import minh.quy.musicplayer.R
import minh.quy.musicplayer.action.OnItemCommonClick
import minh.quy.musicplayer.model.Playlist

class AddToPlaylistAdapter(var context: Context, var playlists: MutableList<Playlist>) :
    RecyclerView.Adapter<AddToPlaylistAdapter.ViewHolder>() {

    var onItemCommonClick: OnItemCommonClick? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.item_list_add_to_playlist, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.item.tv_playlist_name_item_add.text = playlists[position].name
        holder.item.setOnClickListener {
            onItemCommonClick?.onItemClick(position)
        }
    }

    fun setItemClick(onItemCommonClick: OnItemCommonClick) {
        this.onItemCommonClick = onItemCommonClick
    }

    class ViewHolder(var item: View) : RecyclerView.ViewHolder(item) {
    }
}
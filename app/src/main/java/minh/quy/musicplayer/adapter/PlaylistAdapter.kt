package minh.quy.musicplayer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_list_playlist.view.*
import minh.quy.musicplayer.R
import minh.quy.musicplayer.model.Playlist

class PlaylistAdapter(var context: Context, var listPlaylist: MutableList<Playlist>? ) : RecyclerView.Adapter<PlaylistAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_list_playlist,parent,false)
        return ViewHolder(view)

    }

    override fun getItemCount(): Int {
        return listPlaylist!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.item.tv_name_item_list_playlist.text = listPlaylist?.get(position)?.name
        

    }


    inner  class ViewHolder(var item: View): RecyclerView.ViewHolder(item){

    }
}
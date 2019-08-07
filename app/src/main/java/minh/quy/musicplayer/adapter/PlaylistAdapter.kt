package minh.quy.musicplayer.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_list_playlist.view.*
import minh.quy.musicplayer.R
import minh.quy.musicplayer.model.Playlist

class PlaylistAdapter(var context: Context) : RecyclerView.Adapter<PlaylistAdapter.ViewHolder>() {
    var listPlaylist: MutableList<Playlist> = arrayListOf()
    var currenImage = 1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_list_playlist, parent, false)
        return ViewHolder(view)

    }

    override fun getItemCount(): Int {
        return listPlaylist.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.item.tv_name_item_list_playlist.text = listPlaylist.get(position).name
        Log.d("MinhNQ", "image " + currenImage)

        when (currenImage) {
            1 -> {
                holder.item.ava_item_list_playlist.setImageResource(R.drawable.album_art_1)
                currenImage++
            }
            2 -> {
                holder.item.ava_item_list_playlist.setImageResource(R.drawable.album_art_2)
                currenImage++
            }
            3 -> {
                holder.item.ava_item_list_playlist.setImageResource(R.drawable.album_art_3)
                currenImage++
            }
            4 -> {
                holder.item.ava_item_list_playlist.setImageResource(R.drawable.album_art_4)
                currenImage++
            }
            5 -> {
                holder.item.ava_item_list_playlist.setImageResource(R.drawable.album_art_5)
                currenImage++
            }
            6 -> {
                holder.item.ava_item_list_playlist.setImageResource(R.drawable.album_art_6)
                currenImage++
            }
            7 -> {
                holder.item.ava_item_list_playlist.setImageResource(R.drawable.album_art_7)
                currenImage = 1
            }
        }


    }

    fun addPlaylists(playlists: MutableList<Playlist>) {
        currenImage = 1
        this.listPlaylist.clear()
        this.listPlaylist.addAll(playlists)
        notifyDataSetChanged()
    }

    inner class ViewHolder(var item: View) : RecyclerView.ViewHolder(item) {

    }
}
package minh.quy.musicplayer.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_album_list.view.*
import minh.quy.musicplayer.R
import minh.quy.musicplayer.model.Album

class AlbumAdapter(val context: Context) : RecyclerView.Adapter<AlbumAdapter.ViewHolder>() {
    var albumList: MutableList<Album> = arrayListOf()
    var currenImage = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_album_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return albumList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.item.tv_album_name_item_album.text = albumList[position].albumName
        holder.item.tv_number_track_item_album.text = """${albumList[position].songCount} tracks"""
        holder.item.img_item_album_list.clipToOutline = true
        when (currenImage) {
            1 -> {
                holder.item.img_item_album_list.setImageResource(R.drawable.album_art_1)
                currenImage++
            }
            2 -> {
                holder.item.img_item_album_list.setImageResource(R.drawable.album_art_2)
                currenImage++
            }
            3 -> {
                holder.item.img_item_album_list.setImageResource(R.drawable.album_art_3)
                currenImage++
            }
            4 -> {
                holder.item.img_item_album_list.setImageResource(R.drawable.album_art_4)
                currenImage++
            }
            5 -> {
                holder.item.img_item_album_list.setImageResource(R.drawable.album_art_5)
                currenImage++
            }
            6 -> {
                holder.item.img_item_album_list.setImageResource(R.drawable.album_art_6)
                currenImage++
            }
            7 -> {
                holder.item.img_item_album_list.setImageResource(R.drawable.album_art_7)
                currenImage = 1
            }


        }
    }

    fun addAlbumList(albums: MutableList<Album>){
        this.albumList.clear()
        this.albumList.addAll(albums)
        notifyDataSetChanged()
    }
    inner class ViewHolder(val item: View) : RecyclerView.ViewHolder(item)
}
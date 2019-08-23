package minh.quy.musicplayer.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.futuremind.recyclerviewfastscroll.SectionTitleProvider
import kotlinx.android.synthetic.main.item_album_list.view.*
import minh.quy.musicplayer.Constant
import minh.quy.musicplayer.R
import minh.quy.musicplayer.Utils.Utils
import minh.quy.musicplayer.model.Album

class AlbumAdapter(val context: Context) : RecyclerView.Adapter<AlbumAdapter.ViewHolder>(),
    SectionTitleProvider {
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
        if (position < Constant.MAX_DEFAULT_COUNT) {
            currenImage = position + 1
        } else {
            currenImage = Utils.getDefaultImage(position)
        }
        holder.item.img_item_album_list.setImageResource(
            Utils.getDrawableIdDefaultImage(
                currenImage
            )
        )
    }

    override fun getSectionTitle(position: Int): String {
        return albumList[position].albumName.substring(0, 1)
    }

    fun addAlbumList(albums: MutableList<Album>) {
        this.albumList.clear()
        this.albumList.addAll(albums)
        notifyDataSetChanged()
    }

    inner class ViewHolder(val item: View) : RecyclerView.ViewHolder(item)
}
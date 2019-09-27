package minh.quy.musicplayer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_list_add_song_fragment.view.*
import minh.quy.musicplayer.Constant
import minh.quy.musicplayer.R
import minh.quy.musicplayer.Utils.Utils
import minh.quy.musicplayer.action.OnItemCommonClick
import minh.quy.musicplayer.model.Song

class AddSongAdapter(var context: Context, var listSong: MutableList<Song>?) :
    RecyclerView.Adapter<AddSongAdapter.ViewHolder>() {

    var onItemCommonClick: onItemClick? = null
    var currenImage = 1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_list_add_song_fragment, parent, false)
        return ViewHolder(view)

    }

    override fun getItemCount(): Int {
        return listSong?.size!!
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val song = listSong?.get(position)
        holder.itemView.chk_add_song_fragment.isChecked = song!!.isSelected

        holder.itemView.tv_song_name_add_song.text = song.songName
        holder.itemView.tv_artist_add_song.text = song.artistName
        holder.itemView.tv_song_duration.text =
            Utils.convertSongDuration(song.duration.toLong())
        holder.itemView.img_item_add_song_fragment.clipToOutline = true

        if (position < Constant.MAX_DEFAULT_COUNT) {
            currenImage = position + 1
        } else {
            currenImage = Utils.getPositionDefaultImage(position)
        }
        holder.itemView.img_item_add_song_fragment.setImageResource(
            Utils.getDrawableIdDefaultImage(
                currenImage
            )
        )
        holder.itemView.setOnClickListener {
            onItemCommonClick?.onItemClick(position, holder.itemView)
        }
        holder.itemView.chk_add_song_fragment.setOnClickListener {
            onItemCommonClick?.onItemClick(position, holder.itemView)
        }
    }

    fun setItemClick(onItemCommonClick: onItemClick) {
        this.onItemCommonClick = onItemCommonClick
    }

    interface onItemClick{
        fun onItemClick(position: Int, view: View)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
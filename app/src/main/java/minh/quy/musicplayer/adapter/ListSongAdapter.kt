package minh.quy.musicplayer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_song_fragment.view.*
import minh.quy.musicplayer.Constant.Companion.MAX_DEFAULT_COUNT
import minh.quy.musicplayer.R
import minh.quy.musicplayer.Utils.Utils
import minh.quy.musicplayer.action.IOptionListener
import minh.quy.musicplayer.action.OnItemCommonClick
import minh.quy.musicplayer.model.Song

class ListSongAdapter(var context: Context, var listsong: MutableList<Song>) :
    RecyclerView.Adapter<ListSongAdapter.ViewHolder>() {

    var onItemCommonClick: OnItemCommonClick? = null
    var currenImage = 1
    var iOptionListener: IOptionListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_song_fragment, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listsong.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val song = listsong.get(position)
        if (song.isSelected) {
            holder.item.tv_song_name.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.text_selected
                )
            )
            holder.item.tv_artist.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.text_selected
                )
            )
            holder.item.tv_song_duration.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.text_selected
                )
            )
        } else {
            holder.item.tv_song_name.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.color_text
                )
            )
            holder.item.tv_artist.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.color_text
                )
            )
            holder.item.tv_song_duration.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.color_text
                )
            )
        }
        holder.item.tv_song_name.text = song.songName
        holder.item.tv_artist.text = song.artistName
        holder.item.tv_song_duration.text =
            Utils.convertSongDuration(song.duration.toLong())
        holder.item.img_item_song_fragment.clipToOutline = true
        if (position < MAX_DEFAULT_COUNT) {
            currenImage = position + 1
        } else {
            currenImage = Utils.getPositionDefaultImage(position)
        }
        holder.item.img_item_song_fragment.setImageResource(
            Utils.getDrawableIdDefaultImage(
                currenImage
            )
        )
        holder.item.setOnClickListener {
            onItemCommonClick?.onItemClick(position)
        }

        holder.item.img_option_item_song.setOnClickListener {
            iOptionListener?.onOptionClick(position, it)
        }
    }

    fun setOptionListener(iOptionListener: IOptionListener){
        this.iOptionListener = iOptionListener
    }

    fun setOnItemClick(onItemCommonClick: OnItemCommonClick) {
        this.onItemCommonClick = onItemCommonClick
    }

    class ViewHolder(var item: View) : RecyclerView.ViewHolder(item) {

    }
}
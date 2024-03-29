package minh.quy.musicplayer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.futuremind.recyclerviewfastscroll.SectionTitleProvider
import kotlinx.android.synthetic.main.item_song_fragment.view.*
import minh.quy.musicplayer.Constant.Companion.MAX_DEFAULT_COUNT
import minh.quy.musicplayer.R
import minh.quy.musicplayer.Utils.Utils
import minh.quy.musicplayer.action.OnItemCommonClick
import minh.quy.musicplayer.model.Song

class SongFragmentAdapter(var context: Context, var songList: MutableList<Song>) :
    RecyclerView.Adapter<SongFragmentAdapter.ViewHolder>(), SectionTitleProvider {

    var currenImage = 1
    lateinit var onItemCommonClick: OnItemCommonClick

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_song_fragment, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return songList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val song = songList.get(position)
        if (song.isSelected) {
            holder.item.tv_song_name.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.text_selected
                )
            )
            holder.item.tv_artist_playback.setTextColor(
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
            holder.item.tv_artist_playback.setTextColor(
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
        holder.item.tv_artist_playback.text = song.artistName
        holder.item.tv_song_duration.text =
            Utils.convertSongDuration(song.duration.toLong())
        holder.item.img_play_playback.clipToOutline = true
        holder.setOnclickItem()
        if (position < MAX_DEFAULT_COUNT) {
            currenImage = position + 1
        } else {
            currenImage = Utils.getPositionDefaultImage(position)
        }
        holder.item.img_play_playback.setImageResource(
            Utils.getDrawableIdDefaultImage(
                currenImage
            )
        )
    }


    override fun getSectionTitle(position: Int): String {
        return songList[position].songName!!.substring(0, 1)
    }

    fun setlistSong(songs: MutableList<Song>) {
        this.songList.clear()
        this.songList.addAll(songs)
        notifyDataSetChanged()
    }

    fun setOnItemClick(onItemCommonClick: OnItemCommonClick) {
        this.onItemCommonClick = onItemCommonClick
    }

    inner class ViewHolder(val item: View) : RecyclerView.ViewHolder(item) {
        fun setOnclickItem() {
            item.setOnClickListener {
                onItemCommonClick.onItemClick(adapterPosition)
            }
        }
    }
}
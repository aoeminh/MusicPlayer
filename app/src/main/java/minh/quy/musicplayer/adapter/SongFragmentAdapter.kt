package minh.quy.musicplayer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.futuremind.recyclerviewfastscroll.SectionTitleProvider
import kotlinx.android.synthetic.main.item_song_fragment.view.*
import minh.quy.musicplayer.Constant.Companion.MAX_DEFAULT_COUNT
import minh.quy.musicplayer.R
import minh.quy.musicplayer.Utils.Utils
import minh.quy.musicplayer.action.OnItemCommonClick
import minh.quy.musicplayer.model.Song

class SongFragmentAdapter(var context: Context) :
    RecyclerView.Adapter<SongFragmentAdapter.ViewHolder>(), SectionTitleProvider {

    var songList: MutableList<Song> = arrayListOf()
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
        holder.item.tv_song_name.text = songList.get(position).songName
        holder.item.tv_artist.text = songList.get(position).artistName
        holder.item.tv_song_duration.text =
            Utils.convertSongDuration(songList.get(position).duration.toLong())
        holder.item.img_item_song_fragment.clipToOutline = true
        holder.setOnclickItem()
        if (position < MAX_DEFAULT_COUNT) {
            currenImage = position + 1
        } else {
            currenImage = Utils.getDefaultImage(position)
        }
        holder.item.img_item_song_fragment.setImageResource(
            Utils.getDrawableIdDefaultImage(
                currenImage
            )
        )
    }


    override fun getSectionTitle(position: Int): String {
        return songList[position].songName.substring(0, 1)
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
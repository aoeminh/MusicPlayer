package minh.quy.musicplayer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_album_list.view.*
import kotlinx.android.synthetic.main.item_song_fragment.view.*
import minh.quy.musicplayer.R
import minh.quy.musicplayer.Utils.Utils
import minh.quy.musicplayer.action.OnItemCommonClick
import minh.quy.musicplayer.model.Song

class SongFragmentAdapter(var context: Context): RecyclerView.Adapter<SongFragmentAdapter.ViewHolder>() {

    var songList: MutableList<Song> = arrayListOf()
    var currenImage = 1
    lateinit var onItemCommonClick: OnItemCommonClick

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view  = LayoutInflater.from(context).inflate(R.layout.item_song_fragment,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return songList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.item.tv_song_name.text = songList.get(position).songName
        holder.item.tv_artist.text = songList.get(position).artistName
        holder.item.tv_song_duration.text = Utils.convertSongDuration(songList.get(position).duration.toLong())
        holder.item.img_item_song_fragment.clipToOutline =true
        holder.setOnclickItem()
        when (currenImage) {
            1 -> {
                holder.item.img_item_song_fragment.setImageResource(R.drawable.album_art_1)
                currenImage++
            }
            2 -> {
                holder.item.img_item_song_fragment.setImageResource(R.drawable.album_art_2)
                currenImage++
            }
            3 -> {
                holder.item.img_item_song_fragment.setImageResource(R.drawable.album_art_3)
                currenImage++
            }
            4 -> {
                holder.item.img_item_song_fragment.setImageResource(R.drawable.album_art_4)
                currenImage++
            }
            5 -> {
                holder.item.img_item_song_fragment.setImageResource(R.drawable.album_art_5)
                currenImage++
            }
            6 -> {
                holder.item.img_item_song_fragment.setImageResource(R.drawable.album_art_6)
                currenImage++
            }
            7 -> {
                holder.item.img_item_song_fragment.setImageResource(R.drawable.album_art_7)
                currenImage = 1
            }
        }
    }

    fun setlistSong(songs: MutableList<Song>){
        this.songList.clear()
        this.songList.addAll(songs)
        notifyDataSetChanged()
    }

    fun setOnItemClick(onItemCommonClick: OnItemCommonClick){
        this.onItemCommonClick = onItemCommonClick
    }

    inner class ViewHolder( val item: View) : RecyclerView.ViewHolder(item){
        fun setOnclickItem(){
            item.setOnClickListener{
                onItemCommonClick.onItemClick(adapterPosition)
            }
        }
    }
}
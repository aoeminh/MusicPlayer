package minh.quy.musicplayer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_song_fragment.view.*
import minh.quy.musicplayer.R
import minh.quy.musicplayer.model.Song

class SongFragmentAdapter(var context: Context): RecyclerView.Adapter<SongFragmentAdapter.ViewHolder>() {

    var songList: MutableList<Song> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view  = LayoutInflater.from(context).inflate(R.layout.item_song_fragment,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return songList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.tv_song_name.text = songList.get(position).songName
        holder.itemView.tv_artist.text = songList.get(position).artistName
        holder.itemView.tv_song_duration.text = songList.get(position).duration.toString()
    }

    fun setlistSong(songs: MutableList<Song>){
        this.songList.clear()
        this.songList.addAll(songs)
        notifyDataSetChanged()
    }

    inner class ViewHolder( itemView: View) : RecyclerView.ViewHolder(itemView)
}
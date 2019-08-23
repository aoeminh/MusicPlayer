package minh.quy.musicplayer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.futuremind.recyclerviewfastscroll.SectionTitleProvider
import kotlinx.android.synthetic.main.item_artist_fragment.view.*
import minh.quy.musicplayer.Constant
import minh.quy.musicplayer.R
import minh.quy.musicplayer.Utils.Utils
import minh.quy.musicplayer.model.Artist

class ArtistAdapter(val context: Context) : RecyclerView.Adapter<ArtistAdapter.ViewHolder>(),
    SectionTitleProvider {

    var artistList: MutableList<Artist> = arrayListOf()
    var currenImage = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.item_artist_fragment, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return artistList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.item.tv_artist_name.text = artistList[position].artistName
        holder.item.tv_number_tracks_item_artist.text = artistList[position].songCount.toString()
        holder.item.img_item_artist_fragment.clipToOutline = true
        if (position < Constant.MAX_DEFAULT_COUNT) {
            currenImage = position + 1
        } else {
            currenImage = Utils.getDefaultImage(position)
        }
        holder.item.img_item_artist_fragment.setImageResource(
            Utils.getDrawableIdDefaultImage(
                currenImage
            )
        )
    }

    override fun getSectionTitle(position: Int): String {
        return artistList[position].artistName.substring(0, 1)
    }

    fun addArtistList(artists: MutableList<Artist>) {
        artistList.clear()
        artistList.addAll(artists)
        notifyDataSetChanged()
    }

    inner class ViewHolder(val item: View) : RecyclerView.ViewHolder(item)
}
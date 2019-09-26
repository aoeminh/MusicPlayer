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
import minh.quy.musicplayer.action.IOptionListener
import minh.quy.musicplayer.action.OnItemCommonClick
import minh.quy.musicplayer.model.Artist

class ArtistAdapter(val context: Context) : RecyclerView.Adapter<ArtistAdapter.ViewHolder>(),
    SectionTitleProvider {

    var artistList: MutableList<Artist> = arrayListOf()
    var currenImage = 1
    var onItemCommonClick: OnItemCommonClick? = null
    var iOptionListener: IOptionListener? = null

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
            currenImage = Utils.getPositionDefaultImage(position)
        }
        holder.item.img_item_artist_fragment.setImageResource(
            Utils.getDrawableIdDefaultImage(
                currenImage
            )
        )

        holder.item.setOnClickListener {
            onItemCommonClick?.onItemClick(position)
        }
        holder.item.img_option_item_artist_fragment.setOnClickListener {
            iOptionListener?.onOptionClick(position,it)
        }
    }

    override fun getSectionTitle(position: Int): String {
        return artistList[position].artistName.substring(0, 1)
    }

    fun addArtistList(artists: MutableList<Artist>) {
        artistList.clear()
        artistList.addAll(artists)
        notifyDataSetChanged()
    }

    fun setItemClick(onItemCommonClick: OnItemCommonClick, iOptionListener: IOptionListener) {
        this.iOptionListener = iOptionListener
        this.onItemCommonClick = onItemCommonClick
    }

    inner class ViewHolder(val item: View) : RecyclerView.ViewHolder(item)
}
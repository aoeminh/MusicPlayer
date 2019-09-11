package minh.quy.musicplayer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_list_bottom_sheet.view.*
import minh.quy.musicplayer.R
import minh.quy.musicplayer.action.OnItemCommonClick
import minh.quy.musicplayer.model.Song

class BottomSheetAdapter(var context: Context, var list: MutableList<Song>) :
    RecyclerView.Adapter<BottomSheetAdapter.ViewHolder>() {


    var onInteCommonClick: OnItemCommonClick? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.item_list_bottom_sheet, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.tv_song_name.text = list[position].songName
        holder.itemView.tv_artist.text = list[position].artistName
        holder.itemView.setOnClickListener {
            onInteCommonClick?.onItemClick(position)
        }
    }


    fun setOnItemCommonClick(onItemCommonClick: OnItemCommonClick){
        this.onInteCommonClick = onInteCommonClick
    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
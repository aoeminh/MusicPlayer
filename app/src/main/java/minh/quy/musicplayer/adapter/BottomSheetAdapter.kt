package minh.quy.musicplayer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_list_bottom_sheet.view.*
import minh.quy.musicplayer.R
import minh.quy.musicplayer.Utils.SongTouchHelper
import minh.quy.musicplayer.model.Song
import java.util.Collections.swap

import java.util.*


class BottomSheetAdapter(var context: Context, var list: MutableList<Song>) :
    RecyclerView.Adapter<BottomSheetAdapter.ViewHolder>(), SongTouchHelper.ActionCompletionContract {
    override fun onViewMoved(oldPosition: Int, newPosition: Int) {
        if (oldPosition < newPosition) {
            for (i in oldPosition until newPosition) {
                swap(list, i, i + 1)
            }
        } else {
            for (i in oldPosition downTo newPosition + 1) {
                swap(list, i, i - 1)
            }
        }
        notifyItemMoved(oldPosition, newPosition)
    }

    override fun onViewSwiped(position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    var functionBottomFragmen: IFunctionBottomFragment? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.item_list_bottom_sheet, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val song = list[position]
        if (song.isSelected) {
            holder.itemView.tv_song_name_item_bottom.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.text_selected
                )
            )
            holder.itemView.tv_artist_item_bottom.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.text_selected
                )
            )
        } else {
            holder.itemView.tv_song_name_item_bottom.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.color_text
                )
            )
            holder.itemView.tv_artist_item_bottom.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.color_text
                )
            )
        }
        holder.itemView.tv_song_name_item_bottom.text = list[position].songName
        holder.itemView.tv_artist_item_bottom.text = list[position].artistName
        holder.itemView.ctl_item_bottom.setOnClickListener {
            functionBottomFragmen?.chooseSong(position)
        }
        holder.itemView.img_delete_item_bottom.setOnClickListener {
            functionBottomFragmen?.deleteSong(position)
        }

    }


    fun setOnItemCommonClick(onItemCommonClick: IFunctionBottomFragment) {
        this.functionBottomFragmen = onItemCommonClick
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface IFunctionBottomFragment {
        fun chooseSong(position: Int)
        fun deleteSong(position: Int)
    }

}
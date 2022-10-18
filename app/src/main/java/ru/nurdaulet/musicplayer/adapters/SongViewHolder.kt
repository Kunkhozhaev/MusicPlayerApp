package ru.nurdaulet.musicplayer.adapters

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text
import ru.nurdaulet.musicplayer.R

class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val tvPrimary = itemView.findViewById<TextView>(R.id.tvPrimary)
    val tvSecondary = itemView.findViewById<TextView>(R.id.tvSecondary)
    val ivItemImage = itemView.findViewById<ImageView>(R.id.ivItemImage)
}
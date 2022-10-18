package ru.nurdaulet.musicplayer.adapters

import androidx.recyclerview.widget.AsyncListDiffer
import com.bumptech.glide.RequestManager
import ru.nurdaulet.musicplayer.R
import javax.inject.Inject

class SwipeSongAdapter : BaseSongAdapter(R.layout.swipe_item) {

    override val differ = AsyncListDiffer(this, diffCallback)

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[position]
        val text = "${song.title} - ${song.subtitle}"
        holder.tvPrimary.text = text
        
        holder.itemView.setOnClickListener {
            onItemClickListener?.let { click ->
                click(song)
            }
        }

    }

}
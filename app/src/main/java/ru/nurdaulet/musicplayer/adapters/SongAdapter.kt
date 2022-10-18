package ru.nurdaulet.musicplayer.adapters

import androidx.recyclerview.widget.AsyncListDiffer
import com.bumptech.glide.RequestManager
import ru.nurdaulet.musicplayer.R
import javax.inject.Inject

class SongAdapter @Inject constructor(
    private val glide: RequestManager
) : BaseSongAdapter(R.layout.list_item) {

    override val differ = AsyncListDiffer(this, diffCallback)

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[position]
        holder.tvPrimary.text = song.title
        holder.tvSecondary.text = song.subtitle
        glide.load(song.imageURL).into(holder.ivItemImage)

        holder.itemView.setOnClickListener {
            onItemClickListener?.let { click ->
                click(song)
            }
        }

    }

}
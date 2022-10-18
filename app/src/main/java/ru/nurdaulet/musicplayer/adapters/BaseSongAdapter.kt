package ru.nurdaulet.musicplayer.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.nurdaulet.musicplayer.data.entities.Song

abstract class BaseSongAdapter(
    private val layoutID: Int
) : RecyclerView.Adapter<SongViewHolder>() {

    protected val diffCallback = object : DiffUtil.ItemCallback<Song>() {
        override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.mediaID == newItem.mediaID
        }

        override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    protected abstract val differ: AsyncListDiffer<Song>

    var songs: List<Song>
        get() = differ.currentList
        set(value) = differ.submitList(value)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        return SongViewHolder(
            LayoutInflater.from(parent.context).inflate(
                layoutID,
                parent,
                false
            )
        )
    }

    protected var onItemClickListener: ((Song) -> Unit)? = null

    fun setItemClickListener(listener: (Song) -> Unit) {
        onItemClickListener = listener
    }

    override fun getItemCount(): Int {
        return songs.size
    }
}
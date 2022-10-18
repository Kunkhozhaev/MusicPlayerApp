package ru.nurdaulet.musicplayer.ui.fragments

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import ru.nurdaulet.musicplayer.R
import ru.nurdaulet.musicplayer.data.entities.Song
import ru.nurdaulet.musicplayer.data.other.Status
import ru.nurdaulet.musicplayer.data.other.Status.SUCCESS
import ru.nurdaulet.musicplayer.exoplayer.isPlaying
import ru.nurdaulet.musicplayer.exoplayer.toSong
import ru.nurdaulet.musicplayer.ui.viewmodels.MainViewModel
import ru.nurdaulet.musicplayer.ui.viewmodels.SongViewModel
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class SongFragment : Fragment(R.layout.fragment_song) {

    @Inject
    lateinit var glide: RequestManager

    private lateinit var tvSongName: TextView
    private lateinit var tvCurTime: TextView
    private lateinit var tvSongDuration: TextView
    private lateinit var ivSongImage: ImageView
    private lateinit var ivPlayPauseDetail: ImageView
    private lateinit var ivSkipPrevious: ImageView
    private lateinit var ivSkip: ImageView
    private lateinit var seekBar: SeekBar

    private lateinit var mainViewModel: MainViewModel
    private val songViewModel: SongViewModel by viewModels()

    private var curPlayingSong: Song? = null

    private var playbackState: PlaybackStateCompat? = null

    private var shouldUpdateSeekBar: Boolean = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        tvSongName = view.findViewById(R.id.tvSongName)
        tvCurTime = view.findViewById(R.id.tvCurTime)
        tvSongDuration = view.findViewById(R.id.tvSongDuration)
        ivSongImage = view.findViewById(R.id.ivSongImage)
        ivPlayPauseDetail = view.findViewById(R.id.ivPlayPauseDetail)
        ivSkipPrevious = view.findViewById(R.id.ivSkipPrevious)
        ivSkip = view.findViewById(R.id.ivSkip)
        seekBar = view.findViewById(R.id.seekBar)

        subscribeToObservers()

        ivPlayPauseDetail.setOnClickListener {
            curPlayingSong?.let {
                mainViewModel.playOrToggleSong(it, true)
            }
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if(p2) {
                    setCurPlayerTimeToTextView(p1.toLong(), tvCurTime)
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                shouldUpdateSeekBar = false
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                p0?.let {
                    mainViewModel.seekTo(it.progress.toLong())
                    shouldUpdateSeekBar = true
                }
            }
        })

        ivSkipPrevious.setOnClickListener {
            mainViewModel.skipToPreviousSong()
        }

        ivSkip.setOnClickListener {
            mainViewModel.skipToNextSong()
        }


    }

    private fun updateTitleAndSongImage(song: Song) {
        val title = "${song.title} - ${song.subtitle}"
        tvSongName.text = title
        glide.load(song.imageURL).into(ivSongImage)
    }

    private fun subscribeToObservers() {
        mainViewModel.mediaItems.observe(viewLifecycleOwner) {
            it?.let { result ->
                when(result.status) {
                    SUCCESS -> {
                        result.data?.let { songs ->
                            if(curPlayingSong == null && songs.isNotEmpty()) {
                                curPlayingSong = songs[0]
                                updateTitleAndSongImage(songs[0])
                            }
                        }

                    }
                    else -> Unit
                }
            }
        }

        mainViewModel.currentlyPlayingSong.observe(viewLifecycleOwner) {
            if(it == null) return@observe
            curPlayingSong = it.toSong()
            updateTitleAndSongImage(curPlayingSong!!)
        }

        mainViewModel.playbackState.observe(viewLifecycleOwner) {
            playbackState = it
            ivPlayPauseDetail.setImageResource(
                if(playbackState?.isPlaying == true) R.drawable.ic_pause else R.drawable.ic_play
            )
            seekBar.progress = it?.position?.toInt() ?: 0
        }

        songViewModel.curPlayerPosition.observe(viewLifecycleOwner) {
            if(shouldUpdateSeekBar) {
                seekBar.progress = it.toInt()
                setCurPlayerTimeToTextView(it, tvCurTime)
            }
        }

        songViewModel.curSongDuration.observe(viewLifecycleOwner) {
            seekBar.max = it.toInt()
            setCurPlayerTimeToTextView(it, tvSongDuration)
        }
    }

    private fun setCurPlayerTimeToTextView(millSecs: Long, tView: TextView) {
        val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
        tView.text = dateFormat.format(millSecs)

    }
}
package ru.nurdaulet.musicplayer.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import ru.nurdaulet.musicplayer.R
import ru.nurdaulet.musicplayer.adapters.SongAdapter
import ru.nurdaulet.musicplayer.data.other.Status
import ru.nurdaulet.musicplayer.ui.viewmodels.MainViewModel
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    lateinit var mainViewModel: MainViewModel

    private lateinit var allSongsProgressBar: ProgressBar
    private lateinit var rvAllSongs: RecyclerView

    @Inject
    lateinit var songAdapter: SongAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        allSongsProgressBar = view.findViewById(R.id.allSongsProgressBar)
        rvAllSongs = view.findViewById(R.id.rvAllSongs)

        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        setupRecyclerView()
        subscribeToObservers()

        songAdapter.setItemClickListener {
            mainViewModel.playOrToggleSong(it)
        }
    }

    private fun setupRecyclerView() = rvAllSongs.apply {
        adapter = songAdapter
        layoutManager = LinearLayoutManager(requireContext())

    }

    private fun subscribeToObservers() {
        mainViewModel.mediaItems.observe(viewLifecycleOwner) { result ->
            when(result.status){
                Status.SUCCESS -> {
                    allSongsProgressBar.isVisible = false
                    result.data?.let { songs ->
                        songAdapter.songs = songs
                    }
                }

                Status.ERROR -> Unit
                Status.LOADING -> allSongsProgressBar.isVisible = true
            }
        }
    }
}
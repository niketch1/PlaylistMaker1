package com.example.playlistmaker1.media.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker1.R
import com.example.playlistmaker1.databinding.FragmentFavoritesBinding
import com.example.playlistmaker1.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker1.media.ui.view_model.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlaylistsFragment : Fragment() {

    companion object {
        private const val PLAYLISTS = "playlists"

        fun newInstance(playlists: String) = PlaylistsFragment().apply {
            arguments = Bundle().apply {
                putString(PLAYLISTS, playlists)
            }
        }
    }

    private val playlistsViewModel: PlaylistsViewModel by viewModel {
        parametersOf(requireArguments().getString(PLAYLISTS))
    }

    private lateinit var binding: FragmentPlaylistsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.placeholderIconPlaylists.setImageResource(R.drawable.nothing_found)
        binding.placeholderMessagePlaylists.setText(R.string.no_one_playlist)
        binding.placeholderIconPlaylists.visibility = View.VISIBLE
        binding.placeholderMessagePlaylists.visibility = View.VISIBLE
        binding.newPlaylist.visibility = View.VISIBLE

        playlistsViewModel.observePlaylists().observe(viewLifecycleOwner) {

        }

        binding.newPlaylist.setOnClickListener{

        }
    }
}
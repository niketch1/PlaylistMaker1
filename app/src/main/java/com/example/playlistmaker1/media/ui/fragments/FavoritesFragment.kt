package com.example.playlistmaker1.media.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker1.R
import com.example.playlistmaker1.databinding.FragmentFavoritesBinding
import com.example.playlistmaker1.media.ui.view_model.FavoritesViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class FavoritesFragment : Fragment() {

    companion object {
        private const val FAVORITES_TRACK_DATA = "favorites_track_adapter"

        fun newInstance(favoritesTrackAdapter: String) = FavoritesFragment().apply {
            arguments = Bundle().apply {
                putString(FAVORITES_TRACK_DATA, favoritesTrackAdapter)
            }
        }
    }

    private val favoritesViewModel: FavoritesViewModel by viewModel {
        parametersOf(requireArguments().getString(FAVORITES_TRACK_DATA))
    }

    private lateinit var binding: FragmentFavoritesBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.placeholderIconFavorites.setImageResource(R.drawable.nothing_found)
        binding.placeholderMessageFavorites.setText(R.string.your_media_is_empty)
        binding.placeholderIconFavorites.visibility = View.VISIBLE
        binding.placeholderMessageFavorites.visibility = View.VISIBLE

        favoritesViewModel.observeFavorites().observe(viewLifecycleOwner) {

        }
    }
}
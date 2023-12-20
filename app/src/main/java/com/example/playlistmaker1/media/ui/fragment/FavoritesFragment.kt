package com.example.playlistmaker1.media.ui.fragment

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
        private const val FAVORITES = "favorites"

        fun newInstance(favoritesTrackAdapter: String) = FavoritesFragment().apply {
            arguments = Bundle().apply {
                putString(FAVORITES, favoritesTrackAdapter)
            }
        }
    }

    private val favoritesViewModel: FavoritesViewModel by viewModel {
        parametersOf(requireArguments().getString(FAVORITES))
    }

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
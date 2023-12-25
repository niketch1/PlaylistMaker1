package com.example.playlistmaker1.media.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.playlistmaker1.R
import com.example.playlistmaker1.creator.debounce
import com.example.playlistmaker1.databinding.FragmentFavoritesBinding
import com.example.playlistmaker1.media.ui.model.FavoriteState
import com.example.playlistmaker1.media.ui.view_model.FavoritesViewModel
import com.example.playlistmaker1.player.ui.activity.AudioplayerActivity
import com.example.playlistmaker1.search.domain.model.Track
import com.example.playlistmaker1.search.ui.fragment.SearchFragment
import com.example.playlistmaker1.search.ui.recycler_view.TrackAdapter
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

    private val trackAdapter = TrackAdapter {
        onTrackClickDebounce(it)
    }

    private val favoritesViewModel: FavoritesViewModel by viewModel {
        parametersOf(requireArguments().getString(FAVORITES))
    }

    private lateinit var onTrackClickDebounce: (Track) -> Unit
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
        binding.favoriteRecyclerView.adapter = trackAdapter

        //использование корутины с ФАЙЛОМ ДЕБАУНС
        onTrackClickDebounce = debounce<Track>(SearchFragment.CLICK_DEBOUNCE_DELAY_MILLIS, viewLifecycleOwner.lifecycleScope, false) { track ->
            navigateTo(AudioplayerActivity::class.java, track)
        }

        favoritesViewModel.observeFavorites().observe(viewLifecycleOwner) {
            render(it)
        }
    }

    private fun render(state: FavoriteState){
        when(state){
            is FavoriteState.Empty -> showEmpty()
            is FavoriteState.Content -> showContent(state.tracks)
        }
    }

    private fun showEmpty(){
        binding.placeholderIconFavorites.visibility = View.VISIBLE
        binding.placeholderMessageFavorites.visibility = View.VISIBLE
    }

    private fun showContent(trackList: List<Track>){
        binding.favoriteRecyclerView.visibility = View.VISIBLE
        trackAdapter.setTracks(trackList)
    }

    private fun navigateTo(clazz: Class<out AppCompatActivity>, track: Track) {
        val intent = Intent(requireContext(), clazz)
        intent.putExtra("TRACK", track)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
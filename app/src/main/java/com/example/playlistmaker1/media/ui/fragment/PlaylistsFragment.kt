package com.example.playlistmaker1.media.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker1.R
import com.example.playlistmaker1.creator.debounce
import com.example.playlistmaker1.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker1.media.domain.model.Playlist
import com.example.playlistmaker1.media.ui.model.PlaylistsGridState
import com.example.playlistmaker1.media.ui.recycler_view.PlaylistAdapter
import com.example.playlistmaker1.media.ui.view_model.PlaylistsViewModel
import com.example.playlistmaker1.search.ui.fragment.SearchFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    companion object {
        fun newInstance() = PlaylistsFragment()
    }

    private val playlistAdapter = PlaylistAdapter {
        onPlaylistClickDebounce(it)
    }
    private lateinit var onPlaylistClickDebounce: (Playlist) -> Unit
    private val playlistsViewModel by viewModel<PlaylistsViewModel>()
    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!
    private lateinit var playlistRecyclerView: RecyclerView
    private lateinit var newPlaylist: Button
    private lateinit var placeholderIconPlaylists: ImageView
    private lateinit var placeholderMessagePlaylists: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //использование корутины с ФАЙЛОМ ДЕБАУНС
        onPlaylistClickDebounce = debounce<Playlist>(SearchFragment.CLICK_DEBOUNCE_DELAY_MILLIS, viewLifecycleOwner.lifecycleScope, false) { playlist ->
            findNavController().navigate(R.id.action_mediaFragment_to_chosenPlaylistFragment,
                ChosenPlaylistFragment.createArgs(playlist.playlistId))
        }

        playlistRecyclerView = binding.playlistRecyclerView
        newPlaylist = binding.newPlaylist
        placeholderIconPlaylists = binding.placeholderIconPlaylists
        placeholderMessagePlaylists = binding.placeholderMessagePlaylists

        playlistRecyclerView.layoutManager = GridLayoutManager(requireContext(), /*Количество столбцов*/ 2) //ориентация по умолчанию — вертикальная
        playlistRecyclerView.adapter = playlistAdapter
        placeholderIconPlaylists.setImageResource(R.drawable.nothing_found)
        placeholderMessagePlaylists.setText(R.string.no_one_playlist)


        newPlaylist.visibility = View.VISIBLE

        playlistsViewModel.observePlaylists().observe(viewLifecycleOwner) {
            render(it)
        }

        newPlaylist.setOnClickListener{
            findNavController().navigate(R.id.action_mediaFragment_to_NewPlaylistFragment)
        }
    }

    private fun render(state: PlaylistsGridState){
        when(state){
            is PlaylistsGridState.Empty -> showEmpty()
            is PlaylistsGridState.Content -> showContent(state.playlists)
        }
    }

    private fun showEmpty(){
        placeholderIconPlaylists.visibility = View.VISIBLE
        placeholderMessagePlaylists.visibility = View.VISIBLE
        playlistRecyclerView.visibility = View.GONE
    }

    private fun showContent(playlists: List<Playlist>){
        placeholderIconPlaylists.visibility = View.GONE
        placeholderMessagePlaylists.visibility = View.GONE
        playlistRecyclerView.visibility = View.VISIBLE
        playlistAdapter.setPlaylistsGrid(playlists)
    }
    override fun onResume() {
        super.onResume()
        playlistsViewModel.fillData()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.example.playlistmaker1.search.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker1.R
import com.example.playlistmaker1.creator.debounce
import com.example.playlistmaker1.databinding.FragmentSearchBinding
import com.example.playlistmaker1.player.ui.activity.AudioplayerActivity
import com.example.playlistmaker1.search.domain.model.Track
import com.example.playlistmaker1.search.ui.model.TracksState
import com.example.playlistmaker1.search.ui.recycler_view.SearchedTrackAdapter
import com.example.playlistmaker1.search.ui.recycler_view.TrackAdapter
import com.example.playlistmaker1.search.ui.view_model.TracksSearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private val tracksSearchViewModel by viewModel<TracksSearchViewModel>()
    private lateinit var onTrackClickDebounce: (Track) -> Unit

    private val searchedTrackAdapter = SearchedTrackAdapter {
        showSearched(it)
    }
    private val trackAdapter = TrackAdapter {
        showSearched(it)
    }

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var progressBar: ProgressBar
    private lateinit var inputEditText: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewYouSearched: RecyclerView
    private lateinit var placeholderMessage: TextView
    private lateinit var placeholderIcon: ImageView
    private lateinit var buttonRefresh: Button
    private lateinit var buttonClearStory: Button
    private lateinit var youSearched: TextView
    private lateinit var clearTextButton: ImageView

    override fun onResume() {
        tracksSearchViewModel.isScreenPaused = false
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        tracksSearchViewModel.isScreenPaused = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //использование корутины с ФАЙЛОМ ДЕБАУНС
        onTrackClickDebounce = debounce<Track>(CLICK_DEBOUNCE_DELAY_MILLIS, viewLifecycleOwner.lifecycleScope, false) { track ->
            navigateTo(AudioplayerActivity::class.java, track)
        }

        progressBar = binding.progressBar
        inputEditText = binding.inputEditText
        recyclerView = binding.recyclerView
        recyclerViewYouSearched = binding.recyclerViewYouSearched
        placeholderMessage = binding.placeholderMessage
        placeholderIcon = binding.placeholderIcon
        buttonRefresh = binding.buttonRefresh
        buttonClearStory = binding.buttonClearStory
        youSearched = binding.youSearched
        clearTextButton = binding.clearIcon

        inputEditText.setText(tracksSearchViewModel.getLatestSearchText() ?: "")
        recyclerView.adapter = trackAdapter

        recyclerViewYouSearched.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerViewYouSearched.adapter = searchedTrackAdapter
        searchedTrackAdapter.setTracks(tracksSearchViewModel.getSavedTracks())

        youSearched.text = getString(R.string.youSearched)



        tracksSearchViewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
        tracksSearchViewModel.observeShowToast().observe(viewLifecycleOwner) { toast ->
            showToast(toast)
        }

        buttonClearStory.setOnClickListener {
            searchedTrackAdapter.setTracks(null)
            tracksSearchViewModel.editSavedTrackList(null)
            hideYouSearchedVisibility()
        }

        clearTextButton.setOnClickListener {
            inputEditText.setText("")
            trackAdapter.setTracks(null)
            tracksSearchViewModel.renderState(TracksState.Default)
            placeholderMessage.visibility = View.GONE
            placeholderIcon.visibility = View.GONE
            buttonRefresh.visibility = View.GONE
        }

        inputEditText.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && inputEditText.text.isEmpty()) {
                trackAdapter.setTracks(null)
                if (searchedTrackAdapter.isSearchedTrackListEmpty()) {
                    hideYouSearchedVisibility()
                } else {
                    seeYouSearchedVisibility()
                }
            } else {
                hideYouSearchedVisibility()
            }
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                tracksSearchViewModel.searchDebounce(s?.toString() ?: "")
                if (inputEditText.hasFocus() && s?.isEmpty() == true) {
                    trackAdapter.setTracks(null)
                    if (searchedTrackAdapter.isSearchedTrackListEmpty()) {
                        hideYouSearchedVisibility()
                    } else {
                        seeYouSearchedVisibility()
                    }
                } else {
                    hideYouSearchedVisibility()
                }
                clearTextButton.visibility = clearTextButtonVisibility(s)
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)
    }

    private fun hideYouSearchedVisibility(){
        youSearched.visibility = View.GONE
        recyclerViewYouSearched.visibility = View.GONE
        buttonClearStory.visibility = View.GONE
    }

    private fun seeYouSearchedVisibility(){
        youSearched.visibility = View.VISIBLE
        recyclerViewYouSearched.visibility = View.VISIBLE
        buttonClearStory.visibility = View.VISIBLE
    }

    private fun render(state: TracksState) {
        when (state) {
            is TracksState.Default-> showDefault()
            is TracksState.Loading -> showLoading()
            is TracksState.Content -> showContent(state.tracks)
            is TracksState.Error -> showError(getString(state.errorMessage))
            is TracksState.Empty -> showEmpty(getString(state.message))
        }
    }

    private fun showDefault(){}
    private fun showLoading(){
        progressBar.visibility = View.VISIBLE
        placeholderMessage.visibility = View.GONE
        placeholderIcon.visibility = View.GONE
        buttonRefresh.visibility = View.GONE
    }

    private fun showContent(tracks: List<Track>){
        recyclerView.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
        placeholderMessage.visibility = View.GONE
        placeholderIcon.visibility = View.GONE
        buttonRefresh.visibility = View.GONE

        trackAdapter.setTracks(tracks)
    }

    private fun showError(errorMessage: String){
        recyclerView.visibility = View.GONE
        progressBar.visibility = View.GONE
        placeholderMessage.visibility = View.VISIBLE
        placeholderIcon.visibility = View.VISIBLE
        placeholderMessage.text = errorMessage
        placeholderIcon.setImageResource(R.drawable.internet_connection)
        buttonRefresh.visibility = View.VISIBLE

        buttonRefresh.setOnClickListener {
            tracksSearchViewModel.search(tracksSearchViewModel.getLatestSearchText() ?: "")
        }
    }

    private fun showEmpty(message: String){
        recyclerView.visibility = View.GONE
        progressBar.visibility = View.GONE
        placeholderMessage.visibility = View.VISIBLE
        placeholderIcon.visibility = View.VISIBLE
        placeholderMessage.text = message
        placeholderIcon.setImageResource(R.drawable.nothing_found)
    }

    private fun showToast(additionalMessage: String) {
        Toast.makeText(requireContext(), additionalMessage, Toast.LENGTH_LONG)
            .show()
    }

    private fun clearTextButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun showSearched(track: Track) {
        searchedTrackAdapter.addTrack(track)
        tracksSearchViewModel.editSavedTrackList(searchedTrackAdapter.getCurrentTrackList())
        onTrackClickDebounce(track)
    }

    private fun navigateTo(clazz: Class<out AppCompatActivity>, track: Track) {
        val intent = Intent(requireContext(), clazz)
        intent.putExtra("TRACK", track)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        inputEditText.setText("")
        _binding = null
    }

    companion object {
        const val SEARCHED_TRACK_SIZE = 10
        const val TRACK_LIST_KEY = "trackListKey"
        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 1000L
        const val PLAYLIST_PREFERENCES = "playlistPreferences"
    }
}
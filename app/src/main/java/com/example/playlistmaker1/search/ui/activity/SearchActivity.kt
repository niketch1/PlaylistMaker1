package com.example.playlistmaker1.search.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker1.R
import com.example.playlistmaker1.search.ui.recycler_view.SearchedTrackAdapter
import com.example.playlistmaker1.search.ui.recycler_view.TrackAdapter
import com.example.playlistmaker1.player.ui.activity.AudioplayerActivity
import com.example.playlistmaker1.search.domain.model.Track
import com.example.playlistmaker1.search.ui.model.TracksState
import com.example.playlistmaker1.search.ui.view_model.TracksSearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

const val PLAYLIST_PREFERENCES = "playlistPreferences"


class SearchActivity : AppCompatActivity() {

    private var mainThreadHandler: Handler? = null
    private val viewModel: TracksSearchViewModel by viewModel()

    private val searchedTrackAdapter = SearchedTrackAdapter{
        showSearched(it)
    }
    private val trackAdapter = TrackAdapter{
        showSearched(it)
    }

    private lateinit var progressBar: ProgressBar
    private lateinit var inputEditText: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewYouSearched: RecyclerView
    private lateinit var placeholderMessage: TextView
    private lateinit var placeholderIcon: ImageView
    private lateinit var buttonRefresh: Button
    private lateinit var buttonClearStory: Button
    private lateinit var youSearched: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        inputEditText = findViewById(R.id.inputEditText)
        inputEditText.setText(viewModel.getLatestSearchText() ?: "")

        recyclerView = findViewById(R.id.recyclerView)
        recyclerViewYouSearched = findViewById(R.id.recyclerViewYouSearched)
        recyclerView.adapter = trackAdapter
        mainThreadHandler = Handler(Looper.getMainLooper())
        recyclerViewYouSearched.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerViewYouSearched.adapter = searchedTrackAdapter

        progressBar = findViewById(R.id.progressBar)
        placeholderMessage = findViewById(R.id.placeholderMessage)
        placeholderIcon = findViewById(R.id.placeholderIcon)
        buttonRefresh = findViewById(R.id.buttonRefresh)
        buttonClearStory = findViewById(R.id.buttonClearStory)
        youSearched = findViewById(R.id.youSearched)
        youSearched.text = getString(R.string.youSearched)

        searchedTrackAdapter.setTracks(viewModel.getSavedTracks())

        viewModel.observeState().observe(this) {
            render(it)
        }
        viewModel.observeShowToast().observe(this) { toast ->
            showToast(toast)
        }

        buttonClearStory.setOnClickListener {
            searchedTrackAdapter.setTracks(null)
            viewModel.editSavedTrackList(null)
            hideYouSearchedVisibility()
        }

        val back = findViewById<FrameLayout>(R.id.buttonBack)
        back.setOnClickListener {
            finish()
        }

        val clearButton = findViewById<ImageView>(R.id.clearIcon)
        clearButton.setOnClickListener {
            inputEditText.setText("")
            trackAdapter.setTracks(null)
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
                viewModel.searchDebounce(s?.toString() ?:"")
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
                clearButton.visibility = clearButtonVisibility(s)
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
            is TracksState.Loading -> showLoading()
            is TracksState.Content -> showContent(state.tracks)
            is TracksState.Error -> showError(state.errorMessage)
            is TracksState.Empty -> showEmpty(state.message)
        }
    }

    private fun showLoading(){
        progressBar.visibility = View.VISIBLE
    }

    private fun showContent(tracks: List<Track>){
        progressBar.visibility = View.GONE
        placeholderMessage.visibility = View.GONE
        placeholderIcon.visibility = View.GONE
        buttonRefresh.visibility = View.GONE

        trackAdapter.setTracks(tracks)
    }

    private fun showError(errorMessage: String){
        progressBar.visibility = View.GONE
        placeholderMessage.visibility = View.VISIBLE
        placeholderIcon.visibility = View.VISIBLE
        placeholderMessage.text = errorMessage
        placeholderIcon.setImageResource(R.drawable.internet_connection)
        buttonRefresh.visibility = View.VISIBLE

        buttonRefresh.setOnClickListener {
            viewModel.search(viewModel.getLatestSearchText() ?: "")
        }
    }

    private fun showEmpty(message: String){
        progressBar.visibility = View.GONE
        placeholderMessage.visibility = View.VISIBLE
        placeholderIcon.visibility = View.VISIBLE
        placeholderMessage.text = message
        placeholderIcon.setImageResource(R.drawable.nothing_found)
    }

    private fun showToast(additionalMessage: String) {
        Toast.makeText(this, additionalMessage, Toast.LENGTH_LONG)
            .show()
    }
    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private var isClickAllowed = true

    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            mainThreadHandler?.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY_MILLIS)
        }
        return current
    }
    private fun showSearched(track: Track) {
        searchedTrackAdapter.addTrack(track)
        viewModel.editSavedTrackList(searchedTrackAdapter.getCurrentTrackList())
        navigateTo(AudioplayerActivity::class.java, track)
    }

    private fun navigateTo(clazz: Class<out AppCompatActivity>, track: Track) {
        if (clickDebounce()) {
            val intent = Intent(this, clazz)
            intent.putExtra("TRACK", track)
            startActivity(intent)
        }
    }

    companion object {
        const val SEARCHED_TRACK_SIZE = 10
        const val TRACK_LIST_KEY = "trackListKey"
        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 1000L
    }
}
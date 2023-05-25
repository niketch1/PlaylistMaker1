package com.example.playlistmaker1

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


const val PLAYLIST_PREFERENCES = "playlistPreferences"


class SearchActivity : AppCompatActivity() {

    private var mainThreadHandler: Handler? = null

    private val iTunesBaseUrl = "http://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val ITunesService = retrofit.create(iTunesSearchAPI::class.java)
    var str : String = ""
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
    private lateinit var listener: SharedPreferences.OnSharedPreferenceChangeListener
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        mainThreadHandler = Handler(Looper.getMainLooper())

        inputEditText = findViewById(R.id.inputEditText)
        inputEditText.setText(str)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerViewYouSearched = findViewById(R.id.recyclerViewYouSearched)
        recyclerView.adapter = trackAdapter
        recyclerViewYouSearched.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerViewYouSearched.adapter = searchedTrackAdapter

        progressBar = findViewById(R.id.progressBar)
        placeholderMessage = findViewById(R.id.placeholderMessage)
        placeholderIcon = findViewById(R.id.placeholderIcon)
        buttonRefresh = findViewById(R.id.buttonRefresh)
        buttonClearStory = findViewById(R.id.buttonClearStory)
        youSearched = findViewById(R.id.youSearched)
        youSearched.text = getString(R.string.youSearched)


        sharedPreferences = getSharedPreferences(PLAYLIST_PREFERENCES, MODE_PRIVATE)
        val searchHistory = SearchHistory(sharedPreferences)
        searchHistory.reloadTracks(searchedTrackAdapter)

        buttonClearStory.setOnClickListener {
            searchedTrackAdapter.setTracks(null)
            youSearched.visibility = View.GONE
            recyclerViewYouSearched.visibility = View.GONE
            buttonClearStory.visibility = View.GONE
        }

        val back = findViewById<FrameLayout>(R.id.buttonBack)
        back.setOnClickListener {
            finish()
        }

        val clearButton = findViewById<ImageView>(R.id.clearIcon)
        clearButton.setOnClickListener {
            inputEditText.setText("")
            trackAdapter.setTracks(null)
            showMessage("", "")
        }

        inputEditText.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && inputEditText.text.isEmpty()){
                trackAdapter.setTracks(null)
                if(searchedTrackAdapter.isSearchedTrackListEmpty()){
                    youSearched.visibility = View.GONE
                    recyclerViewYouSearched.visibility = View.GONE
                    buttonClearStory.visibility = View.GONE
                } else{
                    youSearched.visibility = View.VISIBLE
                    recyclerViewYouSearched.visibility = View.VISIBLE
                    buttonClearStory.visibility = View.VISIBLE
                }
            } else {
                youSearched.visibility = View.GONE
                recyclerViewYouSearched.visibility = View.GONE
                buttonClearStory.visibility = View.GONE
            }
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchDebounce()
                if (inputEditText.hasFocus() && s?.isEmpty() == true) {
                    trackAdapter.setTracks(null)
                    if(searchedTrackAdapter.isSearchedTrackListEmpty()){
                        youSearched.visibility = View.GONE
                        recyclerViewYouSearched.visibility = View.GONE
                        buttonClearStory.visibility = View.GONE
                    } else{
                        youSearched.visibility = View.VISIBLE
                        recyclerViewYouSearched.visibility = View.VISIBLE
                        buttonClearStory.visibility = View.VISIBLE
                    }
                } else {
                    youSearched.visibility = View.GONE
                    recyclerViewYouSearched.visibility = View.GONE
                    buttonClearStory.visibility = View.GONE
                }
                clearButton.visibility = clearButtonVisibility(s)
                str = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)



        listener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            searchHistory.addToHistory(searchedTrackAdapter, key)
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }

    private val searchRunnable = Runnable { search() }
    private fun searchDebounce() {
        mainThreadHandler?.removeCallbacks(searchRunnable)
        mainThreadHandler?.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(ENTERED_TEXT,str)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        str = savedInstanceState.getString(ENTERED_TEXT,"")
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun search() {
        progressBar.visibility = View.VISIBLE
        trackAdapter.setTracks(null)
        ITunesService.search(inputEditText.text.toString())
            .enqueue(object : Callback<ITunesResponse> {
                override fun onResponse(
                    call: Call<ITunesResponse>,
                    response: Response<ITunesResponse>
                ) {
                    progressBar.visibility = View.GONE
                    if (response.code() == 200) {
                        if (response.body()?.results?.isNotEmpty() == true) {
                            trackAdapter.setTracks(response.body()?.results!!)
                            showMessage("", "")
                        }
                        else {
                            showMessage(getString(R.string.nothing_found), "")
                            placeholderIcon.setImageResource(R.drawable.nothing_found)
                        }
                    } else {
                        showMessage(getString(R.string.something_went_wrong), response.code().toString())
                        placeholderIcon.setImageResource(R.drawable.internet_connection)
                    }
                }

                override fun onFailure(call: Call<ITunesResponse>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    showMessage(getString(R.string.something_went_wrong), t.message.toString())
                    placeholderIcon.setImageResource(R.drawable.internet_connection)
                }

            })
    }

    private fun showMessage(text: String, additionalMessage: String) {
        if (text.isNotEmpty()) {
            placeholderMessage.visibility = View.VISIBLE
            placeholderIcon.visibility = View.VISIBLE
            placeholderMessage.text = text
            if (additionalMessage.isNotEmpty()) {
                Toast.makeText(applicationContext, additionalMessage, Toast.LENGTH_LONG)
                    .show()
            }

            if(text.equals(getString(R.string.something_went_wrong))) {
                buttonRefresh.visibility = View.VISIBLE
                buttonRefresh.setOnClickListener {
                    search()
                }
            }
        } else {
            placeholderMessage.visibility = View.GONE
            placeholderIcon.visibility = View.GONE
            buttonRefresh.visibility = View.GONE
        }
    }

    private var isClickAllowed = true

    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            mainThreadHandler?.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }
    private fun showSearched(track: Track) {
        val sharedPreferences = getSharedPreferences(PLAYLIST_PREFERENCES, MODE_PRIVATE)
        sharedPreferences.edit()
            .putString(NEW_TRACK_KEY, createJsonFromTrack(track))
            .apply()
        navigateTo(AudioplayerActivity::class.java)
    }

    override fun onStop() {
        super.onStop()
        sharedPreferences.edit()
            .putString(TRACK_LIST_KEY, searchedTrackAdapter.createJsonFromTrackList())
            .apply()
    }

    private fun createJsonFromTrack(track: Track): String {
        return Gson().toJson(track)
    }

    private fun navigateTo(clazz: Class<out AppCompatActivity>) {
        if (clickDebounce()) {
            val intent = Intent(this, clazz)
            startActivity(intent)
        }
    }
    companion object {
        const val ENTERED_TEXT = "ENTERED_TEXT"
        const val SEARCHED_TRACK_SIZE = 10
        const val NEW_TRACK_KEY = "newTrackKey"
        const val TRACK_LIST_KEY = "trackListKey"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}
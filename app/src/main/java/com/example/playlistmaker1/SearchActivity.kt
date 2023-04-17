package com.example.playlistmaker1

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


const val PLAYLIST_PREFERENCES = "playlistPreferences"
const val NEW_TRACK_KEY = "newTrackKey"
const val TRACK_LIST_KEY = "trackListKey"

class SearchActivity : AppCompatActivity() {

    val itemType = object : TypeToken<ArrayList<Track>>() {}.type

    private val iTunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val ITunesService = retrofit.create(iTunesSearchAPI::class.java)
    var str : String = ""
    private val tracks = ArrayList<Track>()
    private val searchedTracks = ArrayList<Track>()
    val searchedTrackAdapter = SearchedTrackAdapter()
    val trackAdapter = TrackAdapter{
        showSearched(it)
    }

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

        inputEditText = findViewById(R.id.inputEditText)
        inputEditText.setText(str)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerViewYouSearched = findViewById(R.id.recyclerViewYouSearched)
        trackAdapter.tracks = tracks
        searchedTrackAdapter.tracks = searchedTracks
        recyclerView.adapter = trackAdapter
        recyclerViewYouSearched.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerViewYouSearched.adapter = searchedTrackAdapter

        placeholderMessage = findViewById(R.id.placeholderMessage)
        placeholderIcon = findViewById(R.id.placeholderIcon)
        buttonRefresh = findViewById(R.id.buttonRefresh)
        buttonClearStory = findViewById(R.id.buttonClearStory)
        youSearched = findViewById(R.id.youSearched)
        youSearched.text = getString(R.string.youSearched)


        sharedPreferences = getSharedPreferences(PLAYLIST_PREFERENCES, MODE_PRIVATE)
        val searchedTracksAfterStopActivity = sharedPreferences.getString(TRACK_LIST_KEY, null)
        if (searchedTracksAfterStopActivity != null) {
            searchedTracks.clear()
            searchedTracks.addAll(createTrackListFromJson(searchedTracksAfterStopActivity))
            searchedTrackAdapter.notifyDataSetChanged()
        }

        buttonClearStory.setOnClickListener {
            searchedTracks.clear()
            searchedTrackAdapter.notifyDataSetChanged()
            youSearched.visibility = View.GONE
            recyclerViewYouSearched.visibility = View.GONE
            buttonClearStory.visibility = View.GONE
        }

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search()
                true
            }
            false
        }

        val back = findViewById<FrameLayout>(R.id.buttonBack)
        back.setOnClickListener {
            finish()
        }

        val clearButton = findViewById<ImageView>(R.id.clearIcon)
        clearButton.setOnClickListener {
            inputEditText.setText("")
            inputEditText.onEditorAction(EditorInfo.IME_ACTION_DONE)
            tracks.clear()
            trackAdapter.notifyDataSetChanged()
        }

        inputEditText.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && inputEditText.text.isEmpty()){
                tracks.clear()
                trackAdapter.notifyDataSetChanged()
                youSearched.visibility = View.VISIBLE
                recyclerViewYouSearched.visibility = View.VISIBLE
                buttonClearStory.visibility = View.VISIBLE
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
                if (inputEditText.hasFocus() && s?.isEmpty() == true) {
                    tracks.clear()
                    trackAdapter.notifyDataSetChanged()
                    youSearched.visibility = View.VISIBLE
                    recyclerViewYouSearched.visibility = View.VISIBLE
                    buttonClearStory.visibility = View.VISIBLE
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
            if (key == NEW_TRACK_KEY) {
                val track = sharedPreferences.getString(NEW_TRACK_KEY, null)
                var listHasBeenUpdated = false
                if (track != null) {
                    for (i in searchedTracks.indices) {
                        if (searchedTracks[i].trackId == createTrackFromJson(track).trackId) {
                            searchedTracks.add(0, createTrackFromJson(track))
                            searchedTracks.removeAt(i+1)
                            listHasBeenUpdated = true
                        }
                    }
                    if (!listHasBeenUpdated) {
                        searchedTracks.add(0, createTrackFromJson(track))
                    }
                    if(searchedTracks.size > SEARCHED_TRACK_SIZE) {
                        searchedTracks.removeAt(searchedTracks.size-1)
                    }
                     searchedTrackAdapter.notifyDataSetChanged()
                }
            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }

    companion object {
        const val ENTERED_TEXT = "ENTERED_TEXT"
        const val SEARCHED_TRACK_SIZE = 10
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
        ITunesService.search(inputEditText.text.toString())
            .enqueue(object : Callback<ITunesResponse> {
                override fun onResponse(
                    call: Call<ITunesResponse>,
                    response: Response<ITunesResponse>
                ) {

                    if (response.code() == 200) {
                        if (response.body()?.results?.isNotEmpty() == true) {
                            tracks.clear()
                            tracks.addAll(response.body()?.results!!)
                            trackAdapter.notifyDataSetChanged()
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
                    showMessage(getString(R.string.something_went_wrong), t.message.toString())
                    placeholderIcon.setImageResource(R.drawable.internet_connection)
                }

            })
    }

    private fun showMessage(text: String, additionalMessage: String) {
        if (text.isNotEmpty()) {
            placeholderMessage.visibility = View.VISIBLE
            placeholderIcon.visibility = View.VISIBLE
            tracks.clear()
            trackAdapter.notifyDataSetChanged()
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

    private fun showSearched(track: Track) {
        val sharedPreferences = getSharedPreferences(PLAYLIST_PREFERENCES, MODE_PRIVATE)
        sharedPreferences.edit()
            .putString(NEW_TRACK_KEY, createJsonFromTrack(track))
            .apply()
        Log.d("LISTENER", "нажал")
    }

    override fun onStop() {
        super.onStop()
        sharedPreferences.edit()
            .putString(TRACK_LIST_KEY, createJsonFromTrackList(searchedTracks))
            .apply()
        Log.d("LISTENER", "$(createJsonFromTrackList(searchedTracks))")
    }

    private fun createJsonFromTrack(track: Track): String {
        return Gson().toJson(track)
    }

    private fun createTrackFromJson(json: String): Track{
        return Gson().fromJson(json, Track::class.java)
    }

    private fun createJsonFromTrackList(trackList: ArrayList<Track>): String {
        return Gson().toJson(trackList)
    }

    private fun createTrackListFromJson(json: String): ArrayList<Track>{
        return Gson().fromJson(json, itemType)
    }
}
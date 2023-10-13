package com.example.playlistmaker1.search.ui.view_model

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker1.R
import com.example.playlistmaker1.creator.Creator
import com.example.playlistmaker1.search.domain.api.TracksInteractor
import com.example.playlistmaker1.search.domain.model.Track
import com.example.playlistmaker1.search.ui.model.SingleLiveEvent
import com.example.playlistmaker1.search.ui.model.TracksState
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class TracksSearchViewModel(
    application: Application,
    private val tracksInteractor: TracksInteractor,
): AndroidViewModel(application) {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()

        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val interactor = Creator.provideTracksInteractor(this[APPLICATION_KEY] as Application)
                TracksSearchViewModel(this[APPLICATION_KEY] as Application,interactor)
            }
        }
    }
    val presavedTracks : MutableList<Track> = mutableListOf()
    private val itemType = object : TypeToken<ArrayList<Track>>() {}.type

    init {
        val savedTracks = tracksInteractor.getSavedTracks()
        if (savedTracks != null) {
            presavedTracks.addAll(createTrackListFromJson(savedTracks))
        }
    }

    private var latestSearchText: String? = null

    private val stateLiveData = MutableLiveData<TracksState>()
    fun observeState(): LiveData<TracksState> = stateLiveData

    private val showToast = SingleLiveEvent<String>()
    fun observeShowToast(): LiveData<String> = showToast
    private val handler = Handler(Looper.getMainLooper())

    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText) {
            return
        }
        this.latestSearchText = changedText
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

        val searchRunnable = Runnable { search(changedText) }

        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(
            searchRunnable,
            SEARCH_REQUEST_TOKEN,
            postTime,
        )
    }

    fun search(searchText: String) {
        if (searchText.isNotEmpty()) {
            renderState(TracksState.Loading)
            tracksInteractor.searchTracks(searchText, object : TracksInteractor.TracksConsumer {
                override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                    val tracks = mutableListOf<Track>()
                    if (foundTracks != null) {
                        tracks.addAll(foundTracks)
                    }
                    when {
                        errorMessage != null -> {
                            renderState(
                                TracksState.Error(
                                    errorMessage = getApplication<Application>().getString(R.string.something_went_wrong)
                                )
                            )
                            showToast.postValue(errorMessage)
                        }

                        tracks.isEmpty() -> {
                            renderState(
                                TracksState.Empty(
                                    message = getApplication<Application>().getString(R.string.nothing_found)
                                )
                            )
                        }

                        else -> {
                            renderState(
                                TracksState.Content(
                                    tracks = tracks
                                )
                            )
                        }
                    }
                }
            })
        }
    }

    private fun renderState(tracksState: TracksState){
        stateLiveData.postValue(tracksState)
    }

    fun getSavedTracks() : MutableList<Track>{
        return presavedTracks
    }

    fun getLatestSearchText() : String?{
        return latestSearchText
    }

    fun editSavedTrackList(trackList: List<Track>?){
        if(trackList != null) tracksInteractor.saveTrackListToHistory(createJsonFromTrackList(trackList))
        else tracksInteractor.clearTrackListFromHistory()
    }

    override fun onCleared() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    private fun createJsonFromTrackList(trackList: List<Track>): String {
        return Gson().toJson(trackList)
    }

    private fun createTrackListFromJson(json: String): ArrayList<Track>{
        return Gson().fromJson(json, itemType)
    }
}
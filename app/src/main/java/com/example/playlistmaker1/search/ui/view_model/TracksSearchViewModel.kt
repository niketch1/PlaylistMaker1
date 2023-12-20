package com.example.playlistmaker1.search.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker1.R
import com.example.playlistmaker1.creator.debounce
import com.example.playlistmaker1.search.domain.api.TracksInteractor
import com.example.playlistmaker1.search.domain.model.Track
import com.example.playlistmaker1.search.ui.model.SingleLiveEvent
import com.example.playlistmaker1.search.ui.model.TracksState
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch


class TracksSearchViewModel(
    private val tracksInteractor: TracksInteractor,
): ViewModel() {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
    val presavedTracks : MutableList<Track> = mutableListOf()
    private val itemType = object : TypeToken<ArrayList<Track>>() {}.type
    var isScreenPaused: Boolean = false

    init {
        val savedTracks = tracksInteractor.getSavedTracks()
        savedTracks?.let {
            presavedTracks.addAll(createTrackListFromJson(savedTracks))
        }
    }

    private var latestSearchText: String? = null
    private var trackState: TracksState? = null

    private val stateLiveData = MutableLiveData<TracksState>()
    fun observeState(): LiveData<TracksState> = stateLiveData

    private val showToast = SingleLiveEvent<String>()
    fun observeShowToast(): LiveData<String> = showToast

    //использование корутины с ФАЙЛОМ ДЕБАУНС
    private val trackSearchDebounce = debounce<String>(SEARCH_DEBOUNCE_DELAY, viewModelScope, true) { changedText ->
        search(changedText)
    }

    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText || changedText == "") {
            return
        }
        trackSearchDebounce(changedText)
    }

    fun search(searchText: String) {
        if(trackState != null) stateLiveData.postValue(trackState!!)
        if (searchText.isNotEmpty()) {
            renderState(TracksState.Loading)

            viewModelScope.launch {
                tracksInteractor
                    .searchTracks(searchText)
                    .collect { pair ->
                        if(!isScreenPaused) processResult(pair.first, pair.second)
                    }
            }
        }
    }

    private fun processResult(foundTracks: List<Track>?, errorMessage: String?){
        val tracks = mutableListOf<Track>()
        if (foundTracks != null) {
            tracks.addAll(foundTracks)
        }
        when {
            errorMessage != null -> {
                renderState(
                     TracksState.Error(
                        errorMessage = R.string.something_went_wrong
                    )
                )
                showToast.postValue(errorMessage)
            }

            tracks.isEmpty() -> {
                renderState(
                    TracksState.Empty(
                        message = R.string.nothing_found
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

    fun renderState(tracksState: TracksState){
        trackState = tracksState
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

    private fun createJsonFromTrackList(trackList: List<Track>): String {
        return Gson().toJson(trackList)
    }

    private fun createTrackListFromJson(json: String): ArrayList<Track>{
        return Gson().fromJson(json, itemType)
    }


}
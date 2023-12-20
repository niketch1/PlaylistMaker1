package com.example.playlistmaker1.search.domain.api

import com.example.playlistmaker1.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface TracksInteractor {
    fun searchTracks(text: String): Flow<Pair<List<Track>?, String?>>
    fun getSavedTracks() : String?
    fun saveTrackListToHistory(jsonTrackList : String)
    fun clearTrackListFromHistory()
}
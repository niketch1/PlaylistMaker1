package com.example.playlistmaker1.search.domain.api

import com.example.playlistmaker1.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface TracksInteractor {
    fun searchTracks(text: String): Flow<Pair<List<Track>?, String?>>
    suspend fun getSavedTracks() : List<Track>?
    fun saveTrackListToHistory(jsonTrackList : String)
    fun clearTrackListFromHistory()
}
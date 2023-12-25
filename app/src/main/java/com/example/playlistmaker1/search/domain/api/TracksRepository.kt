package com.example.playlistmaker1.search.domain.api

import com.example.playlistmaker1.creator.Resource
import com.example.playlistmaker1.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface TracksRepository {

    fun searchTracks(text : String) : Flow<Resource<List<Track>>>
    suspend fun getSavedTracks() : List<Track>?
    fun saveTrackListToHistory(jsonTrackList : String)
    fun clearTrackListFromHistory()
}
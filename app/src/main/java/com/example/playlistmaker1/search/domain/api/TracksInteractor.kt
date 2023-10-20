package com.example.playlistmaker1.search.domain.api

import com.example.playlistmaker1.search.domain.model.Track

interface TracksInteractor {
    fun searchTracks(text: String, consumer: TracksConsumer)

    interface TracksConsumer{
        fun consume(foundTracks: List<Track>?, errorMessage: String?)
    }

    fun getSavedTracks() : String?
    fun saveTrackListToHistory(jsonTrackList : String)
    fun clearTrackListFromHistory()
}
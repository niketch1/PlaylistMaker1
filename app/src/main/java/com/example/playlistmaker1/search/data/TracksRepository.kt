package com.example.playlistmaker1.search.data

import com.example.playlistmaker1.creator.Resource
import com.example.playlistmaker1.search.domain.model.Track

interface TracksRepository {

    fun searchTracks(text : String) : Resource<List<Track>>
    fun getSavedTracks() : String?
    fun saveTrackListToHistory(jsonTrackList : String)
    fun clearTrackListFromHistory()
}
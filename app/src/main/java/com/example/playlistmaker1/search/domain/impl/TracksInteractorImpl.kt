package com.example.playlistmaker1.search.domain.impl


import com.example.playlistmaker1.creator.Resource
import com.example.playlistmaker1.search.domain.api.TracksRepository
import com.example.playlistmaker1.search.domain.api.TracksInteractor
import com.example.playlistmaker1.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TracksInteractorImpl(private val tracksRepository: TracksRepository): TracksInteractor {

    override fun searchTracks(text: String): Flow<Pair<List<Track>?, String?>> {
        return tracksRepository.searchTracks(text).map{ resource ->
            when(resource){
                is Resource.Success -> {Pair(resource.data, null)}
                is Resource.Error -> { Pair(null, resource.message)}
            }
        }
    }

    override fun getSavedTracks(): String? {
       return tracksRepository.getSavedTracks()
    }

    override fun saveTrackListToHistory(jsonTrackList: String) {
        tracksRepository.saveTrackListToHistory(jsonTrackList)
    }

    override fun clearTrackListFromHistory(){
        tracksRepository.clearTrackListFromHistory()
    }
}
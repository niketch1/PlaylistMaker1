package com.example.playlistmaker1.search.domain.impl


import com.example.playlistmaker1.creator.Resource
import com.example.playlistmaker1.search.domain.api.TracksRepository
import com.example.playlistmaker1.search.domain.api.TracksInteractor
import java.util.concurrent.Executors

class TracksInteractorImpl(private val tracksRepository: TracksRepository): TracksInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(text: String, consumer: TracksInteractor.TracksConsumer) {
        executor.execute {
            when(val resource = tracksRepository.searchTracks(text)){
                is Resource.Success -> {consumer.consume(resource.data, null)}
                is Resource.Error -> { consumer.consume(null, resource.message)}
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
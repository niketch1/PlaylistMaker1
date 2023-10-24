package com.example.playlistmaker1.search.data

import com.example.playlistmaker1.creator.Resource
import com.example.playlistmaker1.search.data.dto.ITunesResponse
import com.example.playlistmaker1.search.data.dto.TrackDto
import com.example.playlistmaker1.search.data.dto.TracksSearchRequest
import com.example.playlistmaker1.search.domain.model.Track

class TracksRepositoryImpl(
    private val networkClient : NetworkClient,
    private val searchHistory: SearchHistory,
) : TracksRepository{

    override fun searchTracks(text: String): Resource<List<Track>> {
        val response = networkClient.doRequest(TracksSearchRequest(text))
        return when(response.resultCode){
            -1 -> {
                Resource.Error("Проверьте подключение к интернету")
            }
            200 ->{
                Resource.Success((response as ITunesResponse).results.map{it.mapToDomain()})
            }
            else -> {
                Resource.Error(response.resultCode.toString())
            }
        }
    }

    override fun getSavedTracks(): String? {
        return searchHistory.reloadTracks()
    }

    override fun saveTrackListToHistory(jsonTrackList: String) {
        searchHistory.addToHistory(jsonTrackList)
    }

    override fun clearTrackListFromHistory() {
        searchHistory.clearHistory()
    }

    private fun TrackDto.mapToDomain(): Track {
        return Track(
            trackName = trackName,
            artistName = artistName,
            trackTimeMillis = trackTimeMillis,
            artworkUrl100 = artworkUrl100,
            trackId = trackId,
            collectionName = collectionName,
            releaseDate = releaseDate,
            primaryGenreName = primaryGenreName,
            country = country,
            previewUrl = previewUrl
        )
    }
}



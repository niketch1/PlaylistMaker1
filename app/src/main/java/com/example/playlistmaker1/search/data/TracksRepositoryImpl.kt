package com.example.playlistmaker1.search.data

import androidx.room.util.copy
import com.example.playlistmaker1.creator.Resource
import com.example.playlistmaker1.media.data.AppDatabase
import com.example.playlistmaker1.search.data.dto.ITunesResponse
import com.example.playlistmaker1.search.data.dto.TrackDto
import com.example.playlistmaker1.search.data.dto.TracksSearchRequest
import com.example.playlistmaker1.search.domain.api.TracksRepository
import com.example.playlistmaker1.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksRepositoryImpl(
    private val networkClient : NetworkClient,
    private val searchHistory: SearchHistory,
    private val appDatabase: AppDatabase,
) : TracksRepository {

    override fun searchTracks(text: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(TracksSearchRequest(text))
        when(response.resultCode){
            -1 -> {
                emit(Resource.Error("Проверьте подключение к интернету"))
            }
            200 ->{
                val data = (response as ITunesResponse).results.map{
                    if(getTrackIdList().contains(it.trackId)){
                        it.mapToDomain().copy(isFavorite = true)
                    }
                    else{
                        it.mapToDomain()
                    }
                }
                //data.map{track: Track ->  if(getTrackIdList().contains(track.trackId)) track.isFavorite = true}
                emit(Resource.Success(data))
            }
            else -> {
                emit(Resource.Error(response.resultCode.toString()))
            }
        }
    }

    override suspend fun getSavedTracks(): List<Track>? {
        val data = searchHistory.reloadTracks()?.map{
            if(getTrackIdList().contains(it.trackId)) it.copy(isFavorite = true)
            it
        }
            // data?.map{track: Track ->  if(getTrackIdList().contains(track.trackId)) track.isFavorite = true}
        return data
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

    private suspend fun getTrackIdList(): List<Int>{
        val idList = appDatabase.trackDao().getTracksIdList()
        return idList ?: emptyList()
    }
}



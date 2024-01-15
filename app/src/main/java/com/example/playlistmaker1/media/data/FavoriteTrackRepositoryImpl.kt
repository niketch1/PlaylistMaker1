package com.example.playlistmaker1.media.data

import com.example.playlistmaker1.media.data.converters.TrackDbConverter
import com.example.playlistmaker1.media.data.db.entity.TrackEntity
import com.example.playlistmaker1.media.domain.api.FavoriteTracksRepository
import com.example.playlistmaker1.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoriteTrackRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConverter: TrackDbConverter,
) : FavoriteTracksRepository {

    override suspend fun addTrackToFavorite(track: Track){
        appDatabase.trackDao().insertTrack(trackDbConverter.map(track))
    }

    override suspend fun deleteTrackFromFavorites(track: Track) {
        appDatabase.trackDao().deleteTrack(trackDbConverter.map(track))
    }

    override fun getFavoriteTrackList(): Flow<List<Track>> = flow{
        val tracks = appDatabase.trackDao().getTracks()
        emit(convertFromTrackEntity(tracks).map{
            it.copy(isFavorite = true)
        })
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>?): List<Track> {
        return tracks?.map { track -> trackDbConverter.map(track) } ?: emptyList()
    }
}
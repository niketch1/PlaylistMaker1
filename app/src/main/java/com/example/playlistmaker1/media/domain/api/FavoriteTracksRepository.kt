package com.example.playlistmaker1.media.domain.api

import com.example.playlistmaker1.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksRepository {

    suspend fun addTrackToFavorite(track: Track)
    suspend fun deleteTrackFromFavorites(track: Track)
    fun getFavoriteTrackList(): Flow<List<Track>>
}
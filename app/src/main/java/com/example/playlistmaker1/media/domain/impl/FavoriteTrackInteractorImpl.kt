package com.example.playlistmaker1.media.domain.impl

import com.example.playlistmaker1.media.domain.api.FavoriteTrackInteractor
import com.example.playlistmaker1.media.domain.api.FavoriteTracksRepository
import com.example.playlistmaker1.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class FavoriteTrackInteractorImpl(private val favoriteTracksRepository: FavoriteTracksRepository): FavoriteTrackInteractor {

    override suspend fun addTrackToFavorite(track: Track){
        favoriteTracksRepository.addTrackToFavorite(track)
    }

    override suspend fun deleteTrackFromFavorites(track: Track) {
        favoriteTracksRepository.deleteTrackFromFavorites(track)
    }

    override fun getFavoriteTrackList(): Flow<List<Track>>{
        return  favoriteTracksRepository.getFavoriteTrackList()
    }
}
package com.example.playlistmaker1.media.domain.impl

import com.example.playlistmaker1.media.domain.api.PlaylistInteractor
import com.example.playlistmaker1.media.domain.api.PlaylistRepository
import com.example.playlistmaker1.media.domain.model.Playlist
import com.example.playlistmaker1.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(private val playlistRepository: PlaylistRepository): PlaylistInteractor {

    override suspend fun createPlaylist(playlist: Playlist){
        playlistRepository.createPlaylist(playlist)
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist){
        playlistRepository.updatePlaylist(track, playlist)
    }

    override fun getPlaylists(): Flow<List<Playlist>>{
        return playlistRepository.getPlaylists()
    }
}
package com.example.playlistmaker1.media.domain.api

import com.example.playlistmaker1.media.domain.model.Playlist
import com.example.playlistmaker1.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {

    suspend fun createPlaylist(playlist: Playlist)

    suspend fun addTrackToPlaylist(track: Track, playlist: Playlist)

    fun getPlaylists(): Flow<List<Playlist>>
}
package com.example.playlistmaker1.media.domain.api

import com.example.playlistmaker1.media.domain.model.Playlist
import com.example.playlistmaker1.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {

    fun sharePlaylist(message: String)

    suspend fun deletePlaylist(playlist: Playlist)

    suspend fun createPlaylist(playlist: Playlist)

    suspend fun updatePlaylist(playlist: Playlist)

    suspend fun updateTrackListInPlaylist(track: Track, playlist: Playlist)

    fun getPlaylists(): Flow<List<Playlist>>

    fun getPlaylistById(playlistId: Int): Flow<Playlist>

    fun getTracksFromPlaylist(trackIdList: List<Int>): Flow<List<Track>>

    suspend fun deleteTrackFromPlaylist(trackId: Int, playlist: Playlist)
}
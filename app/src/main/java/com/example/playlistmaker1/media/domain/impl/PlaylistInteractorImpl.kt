package com.example.playlistmaker1.media.domain.impl

import com.example.playlistmaker1.media.domain.api.PlaylistInteractor
import com.example.playlistmaker1.media.domain.api.PlaylistRepository
import com.example.playlistmaker1.media.domain.model.Playlist
import com.example.playlistmaker1.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(private val playlistRepository: PlaylistRepository): PlaylistInteractor {

    override fun sharePlaylist(message: String){
        playlistRepository.sharePlaylist(message)
    }

    override suspend fun deletePlaylist(playlist: Playlist){
        playlistRepository.deletePlaylist(playlist)
    }

    override suspend fun createPlaylist(playlist: Playlist){
        playlistRepository.createPlaylist(playlist)
    }

    override suspend fun updatePlaylist(playlist: Playlist){
        playlistRepository.updatePlaylist(playlist)
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist){
        playlistRepository.updateTrackListInPlaylist(track, playlist)
    }

    override fun getPlaylists(): Flow<List<Playlist>>{
        return playlistRepository.getPlaylists()
    }

    override fun getPlaylistById(playlistId: Int): Flow<Playlist> {
        return playlistRepository.getPlaylistById(playlistId)
    }

    override fun getTracksFromPlaylist(trackIdList: List<Int>): Flow<List<Track>> {
        return playlistRepository.getTracksFromPlaylist(trackIdList)
    }

    override suspend fun deleteTrackFromPlaylist(trackId: Int, playlist: Playlist) {
        playlistRepository.deleteTrackFromPlaylist(trackId, playlist)
    }
}
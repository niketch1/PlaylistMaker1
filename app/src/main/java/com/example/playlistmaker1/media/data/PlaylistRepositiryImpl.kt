package com.example.playlistmaker1.media.data

import android.content.Intent
import android.util.Log
import com.example.playlistmaker1.App
import com.example.playlistmaker1.media.data.converters.PlaylistDbConverter
import com.example.playlistmaker1.media.data.db.entity.PlaylistEntity
import com.example.playlistmaker1.media.data.db.entity.TrackInPlaylistEntity
import com.example.playlistmaker1.media.domain.api.PlaylistRepository
import com.example.playlistmaker1.media.domain.model.Playlist
import com.example.playlistmaker1.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistRepositiryImpl (
    private val appDatabase: AppDatabase,
    private val playlistDbConverter: PlaylistDbConverter,
    ) : PlaylistRepository{

    val context = App.appContext
    val TAG = "MyApp"

        override fun sharePlaylist(message: String){
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, message)
            val chooserIntent = Intent.createChooser(shareIntent, null)
            chooserIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(chooserIntent)
        }

        override suspend fun createPlaylist(playlist: Playlist) {
            appDatabase.playlistDao().insertPlaylist(playlistDbConverter.map(playlist))
        }

        override suspend fun deletePlaylist(playlist: Playlist){
            appDatabase.playlistDao().deletePlaylist(playlistDbConverter.map(playlist))
            Log.d(TAG, "удалили плейлист")
            playlist.trackIdList.forEach{
                deleteTrackInPlaylistFromDb(it)
            }
        }

        override suspend fun updatePlaylist(playlist: Playlist) {
            appDatabase.playlistDao().updatePlaylist(playlistDbConverter.map(playlist))
        }

        override suspend fun updateTrackListInPlaylist(track: Track, playlist: Playlist) {
            playlist.trackIdList.add(track.trackId)
            appDatabase.playlistDao().updatePlaylist(playlistDbConverter.map(
                playlist.copy(numberOfTracks = playlist.numberOfTracks + 1)))
            addTrackInPlaylistToDb(track)
        }

        override fun getPlaylists(): Flow<List<Playlist>> = flow {
            val playlists = appDatabase.playlistDao().getPlaylists()
            emit(convertFromPlaylistEntity(playlists))
        }

        override fun getPlaylistById(playlistId: Int): Flow<Playlist> = flow {
            val playlist = appDatabase.playlistDao().getPlaylistById(playlistId)
            emit(playlistDbConverter.map(playlist))
        }

        override fun getTracksFromPlaylist(trackIdList: List<Int>): Flow<List<Track>> = flow {
            val resultTrackList = mutableListOf<TrackInPlaylistEntity>()
            val trackListFromDb = appDatabase.trackInPlaylistDao().getTracks()
            trackListFromDb?.forEach{
                if(trackIdList.contains(it.trackId)) resultTrackList.add(it)
            }
            emit(convertFromTrackInPlaylistEntity(resultTrackList))
        }

        override suspend fun deleteTrackFromPlaylist(trackId: Int, playlist: Playlist) {
            playlist.trackIdList.remove(trackId)
            appDatabase.playlistDao().updatePlaylist(playlistDbConverter.map(
                playlist.copy(numberOfTracks = playlist.numberOfTracks - 1)))
            deleteTrackInPlaylistFromDb(trackId)
        }

        private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>?): List<Playlist> {
            return playlists?.map { playlist -> playlistDbConverter.map(playlist) } ?: emptyList()
        }

        private fun convertFromTrackInPlaylistEntity(tracks: List<TrackInPlaylistEntity>?): List<Track> {
            return tracks?.map { track -> playlistDbConverter.map(track) } ?: emptyList()
        }

        private suspend fun addTrackInPlaylistToDb(track: Track){
            appDatabase.trackInPlaylistDao().insertTrackInPlaylist(playlistDbConverter.map(track))
        }

        private suspend fun deleteTrackInPlaylistFromDb(trackId: Int){
            Log.d(TAG, "зашли")
            val trackListFromDb = appDatabase.playlistDao().getTracksIdLists() ?: emptyList()
            if(trackListFromDb.isEmpty()) Log.d(TAG, "тут пусто")
            Log.d(TAG, trackListFromDb.toString())
            Log.d(TAG, "тут не пусто ваще")
            var flag = true
            val track = appDatabase.trackInPlaylistDao().getTrackById(trackId)
            Log.d(TAG, track.trackName)
            playlistDbConverter.map(trackListFromDb).forEach{
                if(it.contains(trackId)) flag = false
            }
            if(flag) appDatabase.trackInPlaylistDao().deleteTrackFromPlaylist(track)
        }
    }
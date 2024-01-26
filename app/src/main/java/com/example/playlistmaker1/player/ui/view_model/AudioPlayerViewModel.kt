package com.example.playlistmaker1.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker1.media.domain.api.FavoriteTrackInteractor
import com.example.playlistmaker1.media.domain.api.PlaylistInteractor
import com.example.playlistmaker1.media.domain.model.Playlist
import com.example.playlistmaker1.player.domain.api.AudioplayerInteractor
import com.example.playlistmaker1.player.ui.PlayStatus
import com.example.playlistmaker1.search.domain.model.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AudioPlayerViewModel(
    private val audioplayerInteractor: AudioplayerInteractor,
    private val favoriteTrackInteractor: FavoriteTrackInteractor,
    private val playlistInteractor: PlaylistInteractor,
) : ViewModel(){

    private var playStatusLiveData = MutableLiveData<PlayStatus>()
    private var timerJob: Job? = null

    init {
        fillData()
    }
    fun fillData() {
        viewModelScope.launch {
            playlistInteractor
                .getPlaylists()
                .collect{ list ->
                    processResult(list)
                }
        }
    }

    private val playlistsLiveData = MutableLiveData<List<Playlist>>()
    fun observePlaylists(): LiveData<List<Playlist>> = playlistsLiveData

    private val addedTrackInPlaylistLiveData = MutableLiveData<Pair<Boolean, String>>()
    fun observeAddedTrack(): LiveData<Pair<Boolean, String>> = addedTrackInPlaylistLiveData

    private fun processResult(playlistList: List<Playlist>){
        playlistsLiveData.postValue(playlistList)
    }

    fun preparePlayer(convertedTrack: Track) {
        audioplayerInteractor.preparePlayer(
            url = convertedTrack.previewUrl,
            onPreparedCallback = {
                playStatusLiveData.postValue(
                    PlayStatus(
                        progress = "00:00",
                        isPlaying = false,
                        prepared = true,
                        completed = false,
                        isFavorite = convertedTrack.isFavorite
                    )
                )
            },
            onCompletionCallback = {
                playStatusLiveData.postValue(
                    PlayStatus(
                        progress = "00:00",
                        isPlaying = false,
                        prepared = true,
                        completed = true,
                        isFavorite = convertedTrack.isFavorite
                    )
                )
            }
        )
    }

    fun getPlayStatusLiveData(): LiveData<PlayStatus> = playStatusLiveData

    fun addToPlaylist(track: Track, playlist: Playlist){
        val toast: String
        val alreadyAdded: Boolean
        if (track.trackId !in playlist.trackIdList) {
            viewModelScope.launch {
                playlistInteractor.addTrackToPlaylist(track, playlist)
            }
            toast = "Добавлено в плейлист " + playlist.playlistName
            alreadyAdded = false
        } else {
            toast = "Трек уже добавлен в плейлист " + playlist.playlistName
            alreadyAdded = true
        }
        addedTrackInPlaylistLiveData.value = Pair(alreadyAdded,toast)
    }


    fun onFavoriteClicked(track: Track): Track{
        return if(track.isFavorite) deleteFromFavorite(track)
        else addToFavorite(track)
    }
    fun addToFavorite(track: Track): Track{
        viewModelScope.launch {
            favoriteTrackInteractor.addTrackToFavorite(track)
            playStatusLiveData.value = getCurrentPlayStatus().copy(isFavorite = true)
        }
        return track.copy(isFavorite = true)
    }

    fun deleteFromFavorite(track: Track): Track{
        viewModelScope.launch {
            favoriteTrackInteractor.deleteTrackFromFavorites(track)
            playStatusLiveData.value = getCurrentPlayStatus().copy(isFavorite = false)
        }
        return track.copy(isFavorite = false)
    }

    fun playbackControl() {
        if(getCurrentPlayStatus().isPlaying) pausePlayer()
        else startPlayer()
    }

    private fun startPlayer() {
        playStatusLiveData.value = getCurrentPlayStatus().copy(isPlaying = true)
        audioplayerInteractor.startPlayer()
        startTimer()
    }

    fun pausePlayer() {
        if(getCurrentPlayStatus().isPlaying) {
            audioplayerInteractor.pausePlayer()
        }
        playStatusLiveData.value = getCurrentPlayStatus().copy(isPlaying = false)
        timerJob?.cancel()
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (getCurrentPlayStatus().isPlaying) {
                playStatusLiveData.value = getCurrentPlayStatus().copy(progress = audioplayerInteractor.transferCurrentTime())
                delay( DELAY_MILLIS_Activity)
            }
        }
    }

    private fun getCurrentPlayStatus(): PlayStatus {
        return playStatusLiveData.value ?: PlayStatus(progress = "00:00", isPlaying = false, prepared = false, completed = false, isFavorite = false)
    }

    fun stopPlayer() {
        audioplayerInteractor.stopPlayer()
        timerJob?.cancel()
    }

    companion object {
        const val DELAY_MILLIS_Activity = 300L
    }

}
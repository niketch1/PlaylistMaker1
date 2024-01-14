package com.example.playlistmaker1.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker1.media.domain.api.FavoriteTrackInteractor
import com.example.playlistmaker1.player.domain.api.AudioplayerInteractor
import com.example.playlistmaker1.player.ui.PlayStatus
import com.example.playlistmaker1.search.domain.model.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AudioPlayerViewModel(
    private val audioplayerInteractor: AudioplayerInteractor,
    private val favoriteTrackInteractor: FavoriteTrackInteractor,
) : ViewModel(){

    private var playStatusLiveData = MutableLiveData<PlayStatus>()
    private var timerJob: Job? = null
    var isFavorite: Boolean? = null

    fun preparePlayer(convertedTrack: Track) {
        isFavorite = convertedTrack.isFavorite
        audioplayerInteractor.preparePlayer(
            url = convertedTrack.previewUrl,
            onPreparedCallback = {
                playStatusLiveData.postValue(
                    PlayStatus(
                        progress = "00:00",
                        isPlaying = false,
                        prepared = true,
                        completed = false,
                        isFavorite = isFavorite!!
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
                        isFavorite = isFavorite!!
                    )
                )
            }
        )
    }

    fun getPlayStatusLiveData(): LiveData<PlayStatus> = playStatusLiveData

    fun onFavoriteClicked(track: Track){
        if(track.isFavorite) deleteFromFavorite(track)
        else addToFavorite(track)
    }
    fun addToFavorite(track: Track){
        viewModelScope.launch {
            favoriteTrackInteractor.addTrackToFavorite(track)
            track.isFavorite = true
            playStatusLiveData.value = getCurrentPlayStatus().copy(isFavorite = true)
        }
    }

    fun deleteFromFavorite(track: Track){
        viewModelScope.launch {
            favoriteTrackInteractor.deleteTrackFromFavorites(track)
            track.isFavorite = false
            playStatusLiveData.value = getCurrentPlayStatus().copy(isFavorite = false)
        }
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
        return playStatusLiveData.value ?: PlayStatus(progress = "00:00", isPlaying = false, prepared = false, completed = false, isFavorite = isFavorite!!)
    }

    override fun onCleared() {
        audioplayerInteractor.onDestroy()
    }

    companion object {
        const val DELAY_MILLIS_Activity = 300L
    }

}
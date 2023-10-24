package com.example.playlistmaker1.player.ui.view_model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker1.player.domain.api.AudioplayerInteractor
import com.example.playlistmaker1.player.ui.PlayStatus
import com.example.playlistmaker1.search.domain.model.Track

class AudioPlayerViewModel(
    private val audioplayerInteractor: AudioplayerInteractor,
) : ViewModel(){

    private var playStatusLiveData = MutableLiveData<PlayStatus>()
    private val handler = Handler(Looper.getMainLooper())

    fun preparePlayer(convertedTrack: Track) {
        audioplayerInteractor.preparePlayer(
            url = convertedTrack.previewUrl,
            onPreparedCallback = {
                playStatusLiveData.postValue(
                    PlayStatus(
                        progress = "00:00",
                        isPlaying = false,
                        prepared = true,
                        completed = false
                    )
                )
            },
            onCompletionCallback = {
                playStatusLiveData.postValue(
                    PlayStatus(
                        progress = "00:00",
                        isPlaying = false,
                        prepared = true,
                        completed = true
                    )
                )
            }
        )
    }

    fun getPlayStatusLiveData(): LiveData<PlayStatus> = playStatusLiveData

    fun playbackControl() {
        if(getCurrentPlayStatus().isPlaying) pausePlayer()
        else startPlayer()
    }

    private fun startPlayer() {
        playStatusLiveData.value = getCurrentPlayStatus().copy(isPlaying = true)
        handler.post(getCurrentTime())
        audioplayerInteractor.startPlayer()
    }

    fun pausePlayer() {
        if(getCurrentPlayStatus().isPlaying) {
            audioplayerInteractor.pausePlayer()
        }
        playStatusLiveData.value = getCurrentPlayStatus().copy(isPlaying = false)
        handler.removeCallbacks(getCurrentTime())
    }

    private fun getCurrentTime(): Runnable {
        return object : Runnable {
            override fun run() {
                if (getCurrentPlayStatus().isPlaying) {
                    playStatusLiveData.value = getCurrentPlayStatus().copy(progress = audioplayerInteractor.transferCurrentTime())
                    handler.postDelayed(this, DELAY_MILLIS_Activity)
                }
            }
        }
    }

    private fun getCurrentPlayStatus(): PlayStatus {
        return playStatusLiveData.value ?: PlayStatus(progress = "00:00", isPlaying = false, prepared = false, completed = false)
    }

    override fun onCleared() {
        handler.removeCallbacks(getCurrentTime())
        audioplayerInteractor.onDestroy()
    }

    companion object {
        const val DELAY_MILLIS_Activity = 300L
    }

}
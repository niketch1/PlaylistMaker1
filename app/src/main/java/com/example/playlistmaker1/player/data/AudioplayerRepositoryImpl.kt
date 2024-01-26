package com.example.playlistmaker1.player.data

import android.media.MediaPlayer
import com.example.playlistmaker1.player.domain.api.AudioplayerRepository

class AudioplayerRepositoryImpl(
    private val dataTimeFormatUtil: DataTimeFormatUtil,
    private val mediaPlayer: MediaPlayer) : AudioplayerRepository {

    override fun preparePlayer(url : String, onPreparedCallback: () -> Unit, onCompletionCallback: () -> Unit) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            onPreparedCallback()
        }
        mediaPlayer.setOnCompletionListener {
            onCompletionCallback()
        }
    }

    override fun transferCurrentTime() : String{
        return dataTimeFormatUtil.convertIntTimeToString(mediaPlayer.currentPosition)
    }

    override fun startPlayer() {
        mediaPlayer.start()
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
    }

    override fun stopPlayer() {
        mediaPlayer.stop()
        mediaPlayer.reset()
    }

}

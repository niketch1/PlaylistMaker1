package com.example.playlistmaker1.data

import android.media.MediaPlayer
import com.example.playlistmaker1.data.mapper.DataTimeFormatUtil
import com.example.playlistmaker1.domain.AudioplayerRepository

class AudioplayerRepositoryImpl : AudioplayerRepository{

    private var dataTimeFormatUtil = DataTimeFormatUtil()
    private val mediaPlayer = MediaPlayer()

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

    override fun onDestroy() {
        mediaPlayer.release()
    }

}

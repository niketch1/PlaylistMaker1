package com.example.playlistmaker1.player.domain.impl

import com.example.playlistmaker1.player.domain.api.AudioplayerInteractor
import com.example.playlistmaker1.player.domain.api.AudioplayerRepository

class AudioplayerInteractorImpl(private val repository: AudioplayerRepository) :
    AudioplayerInteractor {

    override fun preparePlayer(url: String, onPreparedCallback: () -> Unit, onCompletionCallback: () -> Unit) {
        return repository.preparePlayer(url, onPreparedCallback, onCompletionCallback)
    }

    override fun startPlayer() {
        return repository.startPlayer()
    }

    override fun pausePlayer() {
        return repository.pausePlayer()
    }

    override fun stopPlayer() {
        return repository.stopPlayer()
    }

    override fun transferCurrentTime(): String {
        return  repository.transferCurrentTime()
    }
}
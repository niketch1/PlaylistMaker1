package com.example.playlistmaker1.player.domain.impl

import com.example.playlistmaker1.player.domain.api.AudioplayerRepository
import com.example.playlistmaker1.player.domain.api.AudioplayerInteractor

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

    override fun onDestroy() {
        return repository.onDestroy()
    }

    override fun transferCurrentTime(): String {
        return  repository.transferCurrentTime()
    }
}
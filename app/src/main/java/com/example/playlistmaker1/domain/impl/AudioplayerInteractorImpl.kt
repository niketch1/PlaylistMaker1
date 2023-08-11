package com.example.playlistmaker1.domain.impl

import com.example.playlistmaker1.domain.AudioplayerInteractor
import com.example.playlistmaker1.domain.AudioplayerRepository

class AudioplayerInteractorImpl(private val repository: AudioplayerRepository) : AudioplayerInteractor{

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
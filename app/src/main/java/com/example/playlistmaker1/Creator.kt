package com.example.playlistmaker1

import com.example.playlistmaker1.data.AudioplayerRepositoryImpl
import com.example.playlistmaker1.domain.AudioplayerInteractor
import com.example.playlistmaker1.domain.AudioplayerRepository
import com.example.playlistmaker1.domain.impl.AudioplayerInteractorImpl

object Creator {
    private fun getAudioplayerRepository(): AudioplayerRepository {
        return AudioplayerRepositoryImpl()
    }

    fun provideAudioplayerInteractor(): AudioplayerInteractor {
        return AudioplayerInteractorImpl(getAudioplayerRepository())
    }
}
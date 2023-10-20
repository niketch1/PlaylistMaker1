package com.example.playlistmaker1.creator

import android.content.Context
import com.example.playlistmaker1.App
import com.example.playlistmaker1.player.domain.api.AudioplayerInteractor
import com.example.playlistmaker1.player.domain.impl.AudioplayerInteractorImpl
import com.example.playlistmaker1.player.data.AudioplayerRepository
import com.example.playlistmaker1.player.data.AudioplayerRepositoryImpl
import com.example.playlistmaker1.search.data.SearchHistory
import com.example.playlistmaker1.search.data.TracksRepository
import com.example.playlistmaker1.search.data.TracksRepositoryImpl
import com.example.playlistmaker1.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker1.search.domain.api.TracksInteractor
import com.example.playlistmaker1.search.domain.impl.TracksInteractorImpl
import com.example.playlistmaker1.search.ui.activity.PLAYLIST_PREFERENCES
import com.example.playlistmaker1.search.ui.activity.SearchActivity
import com.example.playlistmaker1.settings.data.SettingsRepository
import com.example.playlistmaker1.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker1.settings.domain.api.SettingsInteractor
import com.example.playlistmaker1.settings.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker1.sharing.data.ExternalNavigator
import com.example.playlistmaker1.sharing.data.ExternalNavigatorImpl
import com.example.playlistmaker1.sharing.domain.api.SharingInteractor
import com.example.playlistmaker1.sharing.domain.impl.SharingInteractorImpl

object Creator {
    private fun getAudioplayerRepository(): AudioplayerRepository {
        return AudioplayerRepositoryImpl()
    }

    fun provideAudioplayerInteractor(): AudioplayerInteractor {
        return AudioplayerInteractorImpl(getAudioplayerRepository())
    }

    private fun getExternalNavigator() : ExternalNavigator{
        return ExternalNavigatorImpl()
    }

    fun provideSharingInteractor() : SharingInteractor{
        return SharingInteractorImpl(getExternalNavigator())
    }

    private fun getSettingsRepository(app: App) : SettingsRepository{
        return SettingsRepositoryImpl(app)
    }

    fun provideSettingsInteractor(app: App) : SettingsInteractor {
        return SettingsInteractorImpl(getSettingsRepository(app))
    }

    private fun getTracksRepository(context: Context) : TracksRepository {
        return TracksRepositoryImpl(
            RetrofitNetworkClient(context),
            SearchHistory(context.getSharedPreferences(PLAYLIST_PREFERENCES, Context.MODE_PRIVATE))
        )
    }

    fun provideTracksInteractor(context: Context) : TracksInteractor {
        return TracksInteractorImpl(getTracksRepository(context))
    }
}
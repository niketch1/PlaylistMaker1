package com.example.playlistmaker1.di

import com.example.playlistmaker1.media.data.FavoriteTrackRepositoryImpl
import com.example.playlistmaker1.media.data.converters.TrackDbConverter
import com.example.playlistmaker1.media.domain.api.FavoriteTracksRepository
import com.example.playlistmaker1.player.domain.api.AudioplayerRepository
import com.example.playlistmaker1.player.data.AudioplayerRepositoryImpl
import com.example.playlistmaker1.search.domain.api.TracksRepository
import com.example.playlistmaker1.search.data.TracksRepositoryImpl
import com.example.playlistmaker1.settings.domain.api.SettingsRepository
import com.example.playlistmaker1.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker1.sharing.domain.api.ExternalNavigator
import com.example.playlistmaker1.sharing.data.ExternalNavigatorImpl
import org.koin.dsl.module

val repositoryModule = module {

    single<TracksRepository> {
        TracksRepositoryImpl(get(), get(), get())
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }

    single<ExternalNavigator> {
        ExternalNavigatorImpl()
    }

    factory<AudioplayerRepository>{
        AudioplayerRepositoryImpl(get(), get())
    }

    factory { TrackDbConverter() }

    single<FavoriteTracksRepository>{
        FavoriteTrackRepositoryImpl(get(), get())
    }
}
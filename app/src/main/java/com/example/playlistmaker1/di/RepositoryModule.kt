package com.example.playlistmaker1.di

import com.example.playlistmaker1.player.data.AudioplayerRepository
import com.example.playlistmaker1.player.data.AudioplayerRepositoryImpl
import com.example.playlistmaker1.search.data.TracksRepository
import com.example.playlistmaker1.search.data.TracksRepositoryImpl
import com.example.playlistmaker1.settings.data.SettingsRepository
import com.example.playlistmaker1.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker1.sharing.data.ExternalNavigator
import com.example.playlistmaker1.sharing.data.ExternalNavigatorImpl
import org.koin.dsl.module

val repositoryModule = module {

    single<TracksRepository> {
        TracksRepositoryImpl(get(), get())
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
}
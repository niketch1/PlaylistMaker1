package com.example.playlistmaker1.di

import com.example.playlistmaker1.media.domain.api.FavoriteTrackInteractor
import com.example.playlistmaker1.media.domain.impl.FavoriteTrackInteractorImpl
import com.example.playlistmaker1.player.domain.api.AudioplayerInteractor
import com.example.playlistmaker1.player.domain.impl.AudioplayerInteractorImpl
import com.example.playlistmaker1.search.domain.api.TracksInteractor
import com.example.playlistmaker1.search.domain.impl.TracksInteractorImpl
import com.example.playlistmaker1.settings.domain.api.SettingsInteractor
import com.example.playlistmaker1.settings.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker1.sharing.domain.api.SharingInteractor
import com.example.playlistmaker1.sharing.domain.impl.SharingInteractorImpl
import org.koin.dsl.module

val interactorModule = module {

    single<TracksInteractor> {
        TracksInteractorImpl(get())
    }

    single<SettingsInteractor> {
        SettingsInteractorImpl(get())
    }

    single<SharingInteractor> {
        SharingInteractorImpl(get())
    }

    factory<AudioplayerInteractor> {
        AudioplayerInteractorImpl(get())
    }

    single<FavoriteTrackInteractor>{
        FavoriteTrackInteractorImpl(get())
    }
}
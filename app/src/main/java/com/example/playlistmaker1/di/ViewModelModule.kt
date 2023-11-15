package com.example.playlistmaker1.di

import com.example.playlistmaker1.media.ui.view_model.FavoritesViewModel
import com.example.playlistmaker1.media.ui.view_model.PlaylistsViewModel
import com.example.playlistmaker1.player.ui.view_model.AudioPlayerViewModel
import com.example.playlistmaker1.search.ui.view_model.TracksSearchViewModel
import com.example.playlistmaker1.settings.ui.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        TracksSearchViewModel(get(), get())
    }

    viewModel {
        SettingsViewModel(get(), get())
    }

    viewModel {
        AudioPlayerViewModel(get())
    }

    viewModel{(favoritesTrackAdapter: String) ->
        FavoritesViewModel(favoritesTrackAdapter)
    }

    viewModel{(playlistsTrackAdapter: String) ->
        PlaylistsViewModel(playlistsTrackAdapter)
    }

}
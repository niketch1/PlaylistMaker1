package com.example.playlistmaker1.di

import android.content.Context
import android.media.MediaPlayer
import com.example.playlistmaker1.App
import com.example.playlistmaker1.player.data.DataTimeFormatUtil
import com.example.playlistmaker1.search.data.NetworkClient
import com.example.playlistmaker1.search.data.SearchHistory
import com.example.playlistmaker1.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker1.search.data.network.iTunesSearchAPI
import com.example.playlistmaker1.search.ui.fragment.SearchFragment.Companion.PLAYLIST_PREFERENCES
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    single<iTunesSearchAPI> {
        Retrofit.Builder()
            .baseUrl("http://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(iTunesSearchAPI::class.java)
    }

    single {
        androidContext()
            .getSharedPreferences(PLAYLIST_PREFERENCES, Context.MODE_PRIVATE)
    }

    single{ App() }

    single{ DataTimeFormatUtil()}

    factory{MediaPlayer()}

    single {
        SearchHistory(get())
    }

    single<NetworkClient> {
        RetrofitNetworkClient(get(), androidContext())
    }

}
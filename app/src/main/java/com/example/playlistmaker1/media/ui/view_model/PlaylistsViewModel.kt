package com.example.playlistmaker1.media.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PlaylistsViewModel(private val playlistsTrackAdapter: String) : ViewModel() {

    private val playlistsLiveData = MutableLiveData(playlistsTrackAdapter)
    fun observePlaylists(): LiveData<String> = playlistsLiveData
}
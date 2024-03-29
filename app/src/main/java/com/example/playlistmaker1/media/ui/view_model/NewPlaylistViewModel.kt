package com.example.playlistmaker1.media.ui.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker1.media.domain.api.PlaylistInteractor
import com.example.playlistmaker1.media.domain.model.Playlist
import kotlinx.coroutines.launch

class NewPlaylistViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {

    fun addPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistInteractor.createPlaylist(playlist)
        }
    }
}
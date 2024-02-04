package com.example.playlistmaker1.media.ui.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker1.media.domain.api.PlaylistInteractor
import com.example.playlistmaker1.media.domain.model.Playlist
import kotlinx.coroutines.launch

class ChosenPlaylistActivityViewModel(private val playlistInteractor: PlaylistInteractor,
    application: Application) : AndroidViewModel(application) {

    fun deletePlaylist(playlist: Playlist){
        viewModelScope.launch {
            playlistInteractor.deletePlaylist(playlist)
        }
        playlistLiveData.postValue(null)
    }

    private val playlistLiveData = MutableLiveData<Playlist?>()
    fun observeChosenPlaylist(): LiveData<Playlist?> = playlistLiveData

}
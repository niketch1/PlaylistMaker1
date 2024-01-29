package com.example.playlistmaker1.media.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker1.media.domain.api.PlaylistInteractor
import com.example.playlistmaker1.media.domain.model.Playlist
import com.example.playlistmaker1.media.ui.model.PlaylistsGridState
import kotlinx.coroutines.launch

class PlaylistsViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {

    init {
        fillData()
    }
    fun fillData() {
        viewModelScope.launch {
            playlistInteractor
                .getPlaylists()
                .collect{ list ->
                    processResult(list)
                }
        }
    }

    private val playlistsLiveData = MutableLiveData<PlaylistsGridState>()
    fun observePlaylists(): LiveData<PlaylistsGridState> = playlistsLiveData

    private fun processResult(playlistList: List<Playlist>){
        if(playlistList.isEmpty()) renderState(PlaylistsGridState.Empty)
        else renderState(PlaylistsGridState.Content(playlistList))
    }

    fun renderState(playlistsGridState: PlaylistsGridState){
        playlistsLiveData.postValue(playlistsGridState)
    }
}
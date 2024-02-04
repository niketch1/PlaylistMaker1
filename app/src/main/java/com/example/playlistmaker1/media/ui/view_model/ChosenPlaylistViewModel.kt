package com.example.playlistmaker1.media.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker1.media.domain.api.PlaylistInteractor
import com.example.playlistmaker1.media.domain.model.Playlist
import com.example.playlistmaker1.search.domain.model.Track
import kotlinx.coroutines.launch

class ChosenPlaylistViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {

    fun getPlaylistById(playlistId: Int) {
        viewModelScope.launch {
            playlistInteractor
                .getPlaylistById(playlistId)
                .collect{ playlist ->
                    processResultPlaylist(playlist)
                }
        }
    }

    fun getTracksFromPlaylist(trackIdList: List<Int>){
        viewModelScope.launch {
            playlistInteractor
                .getTracksFromPlaylist(trackIdList)
                .collect{ trackList ->
                    processResultTrackList(trackList)
                }
        }
    }

    fun deletePlaylist(playlist: Playlist){
        viewModelScope.launch {
            playlistInteractor.deletePlaylist(playlist)
        }
        playlistLiveData.postValue(null)
    }

    private val playlistLiveData = MutableLiveData<Playlist?>()
    fun observeChosenPlaylist(): LiveData<Playlist?> = playlistLiveData

    private val trackListLiveData = MutableLiveData<List<Track>>()
    fun observeTrackList(): LiveData<List<Track>> = trackListLiveData

    private fun processResultPlaylist(playlist: Playlist){
        getTracksFromPlaylist(playlist.trackIdList)
        playlistLiveData.postValue(playlist)
    }

    private fun processResultTrackList(trackList: List<Track>){
        trackListLiveData.postValue(trackList)
    }

    fun deleteTrackFromPlaylist(trackId: Int, playlist: Playlist){
        viewModelScope.launch {
            playlistInteractor.deleteTrackFromPlaylist(trackId, playlist)
            getPlaylistById(playlist.playlistId)
        }
    }

    fun sharePlaylist(message: String){
        playlistInteractor.sharePlaylist(message)
    }
}
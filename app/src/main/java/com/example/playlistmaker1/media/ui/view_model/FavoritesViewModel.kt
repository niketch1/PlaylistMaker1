package com.example.playlistmaker1.media.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker1.media.domain.api.FavoriteTrackInteractor
import com.example.playlistmaker1.media.ui.model.FavoriteState
import com.example.playlistmaker1.search.domain.model.Track
import com.example.playlistmaker1.search.ui.model.TracksState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class FavoritesViewModel(private val favoriteTrackInteractor: FavoriteTrackInteractor,) : ViewModel() {

    fun fillData() {
        viewModelScope.launch {
             favoriteTrackInteractor
                 .getFavoriteTrackList()
                 .collect{ list ->
                     list.map{
                         track -> track.isFavorite = true
                     }
                     processResult(list)
                 }
        }
    }

    private val favoritesLiveData = MutableLiveData<FavoriteState>()
    fun observeFavorites(): LiveData<FavoriteState> = favoritesLiveData

    private fun processResult(trackList: List<Track>){
        if(trackList.isEmpty()) renderState(FavoriteState.Empty)
        else renderState(FavoriteState.Content(trackList))
    }

    fun renderState(favoriteState: FavoriteState){
        favoritesLiveData.postValue(favoriteState)
    }
}
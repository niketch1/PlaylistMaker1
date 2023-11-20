package com.example.playlistmaker1.media.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FavoritesViewModel(private val favoritesTrackAdapter: String) : ViewModel() {

    private val favoritesLiveData = MutableLiveData(favoritesTrackAdapter)
    fun observeFavorites(): LiveData<String> = favoritesLiveData
}
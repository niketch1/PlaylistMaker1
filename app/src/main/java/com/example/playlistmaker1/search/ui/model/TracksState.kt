package com.example.playlistmaker1.search.ui.model

import com.example.playlistmaker1.search.domain.model.Track

sealed interface TracksState {

    object Loading : TracksState
    object Default : TracksState

    data class Content(
        val tracks: List<Track>
    ) : TracksState

    data class Error(
        val errorMessage: Int
    ) : TracksState

    data class Empty(
        val message: Int
    ) : TracksState

}
package com.example.playlistmaker1.media.ui.model

import com.example.playlistmaker1.search.domain.model.Track

sealed interface FavoriteState {

    object Empty : FavoriteState

    data class Content(
        val tracks: List<Track>
    ) : FavoriteState
}

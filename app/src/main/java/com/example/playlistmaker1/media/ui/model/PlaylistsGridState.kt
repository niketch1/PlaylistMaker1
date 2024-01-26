package com.example.playlistmaker1.media.ui.model

import com.example.playlistmaker1.media.domain.model.Playlist

sealed interface PlaylistsGridState {

    object Empty : PlaylistsGridState

    data class Content(
        val playlists: List<Playlist>
    ) : PlaylistsGridState
}
package com.example.playlistmaker1.presentation.mapper

import com.example.playlistmaker1.domain.models.Track
import com.google.gson.Gson

class TrackFromJson {

    fun createTrackFromJson(json: String?): Track {
        return Gson().fromJson(json, Track::class.java)
    }
}
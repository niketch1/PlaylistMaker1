package com.example.playlistmaker1.search.data.network

import com.example.playlistmaker1.search.data.dto.ITunesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface iTunesSearchAPI {

    @GET("/search?entity=song")
    suspend fun search(@Query("term") text: String) : ITunesResponse
}
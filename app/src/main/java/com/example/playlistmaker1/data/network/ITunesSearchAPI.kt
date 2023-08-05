package com.example.playlistmaker1.data.network

import com.example.playlistmaker1.data.dto.ITunesResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface iTunesSearchAPI {

    @GET("/search?entity=song")
    fun search(@Query("term") text: String) : Call<ITunesResponse>
}
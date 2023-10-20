package com.example.playlistmaker1.search.data

import com.example.playlistmaker1.search.data.dto.Response

interface NetworkClient {
    fun doRequest(dto: Any): Response

}
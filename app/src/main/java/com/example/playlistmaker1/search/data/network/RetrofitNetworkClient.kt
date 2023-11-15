package com.example.playlistmaker1.search.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.playlistmaker1.search.data.NetworkClient
import com.example.playlistmaker1.search.data.dto.Response
import com.example.playlistmaker1.search.data.dto.TracksSearchRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient (
    private val iTunesService: iTunesSearchAPI,
    private val context : Context) : NetworkClient{

    override fun doRequest(dto: Any): Response {
        if(isConnected() == false){
            return Response().apply{resultCode = -1}
        }
        if(dto !is TracksSearchRequest){
            return Response().apply{resultCode = 400}
        }

        val response = iTunesService.search(dto.text).execute()
        val body = response.body()
        return if(body != null) {
            body.apply { resultCode = response.code() }
        }else{
            Response().apply { resultCode = response.code() }
        }
    }


    private fun isConnected(): Boolean {
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
            }
        }
        return false
    }
}
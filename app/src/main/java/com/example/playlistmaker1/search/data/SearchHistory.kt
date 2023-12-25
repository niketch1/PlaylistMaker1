package com.example.playlistmaker1.search.data

import android.content.SharedPreferences
import com.example.playlistmaker1.search.domain.model.Track
import com.example.playlistmaker1.search.ui.fragment.SearchFragment.Companion.TRACK_LIST_KEY
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class SearchHistory(val sharedPrefs : SharedPreferences?){

    private val itemType = object : TypeToken<ArrayList<Track>>() {}.type
    fun reloadTracks(): List<Track>? {
        return createTrackListFromJson(sharedPrefs?.getString(TRACK_LIST_KEY, null))
    }
    fun addToHistory(jsonTrackList : String){
        sharedPrefs?.edit()
            ?.putString(TRACK_LIST_KEY, jsonTrackList)
            ?.apply()
    }
    fun clearHistory(){
        sharedPrefs?.edit()
            ?.clear()
            ?.apply()
    }

    private fun createTrackListFromJson(json: String?): ArrayList<Track>{
        return Gson().fromJson(json, itemType)
    }
}
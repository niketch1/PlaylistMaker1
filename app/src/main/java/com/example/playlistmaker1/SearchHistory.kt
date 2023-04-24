package com.example.playlistmaker1

import android.content.SharedPreferences
import com.example.playlistmaker1.SearchActivity.Companion.TRACK_LIST_KEY
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class SearchHistory(val sharedPrefs : SharedPreferences?){

    private val itemType = object : TypeToken<ArrayList<Track>>() {}.type

    fun reloadTracks(searchedTrackAdapter: SearchedTrackAdapter){
        val searchedTracksAfterStopActivity = sharedPrefs?.getString(TRACK_LIST_KEY, null)
        if (searchedTracksAfterStopActivity != null) {
            searchedTrackAdapter.setTracks(createTrackListFromJson(searchedTracksAfterStopActivity))
        }
    }

    fun addToHistory(searchedTrackAdapter: SearchedTrackAdapter, key : String){
        if (key == SearchActivity.NEW_TRACK_KEY) {
            val track = sharedPrefs?.getString(SearchActivity.NEW_TRACK_KEY, null)
            searchedTrackAdapter.addTrack(createTrackFromJson(track))
        }
    }

    private fun createTrackListFromJson(json: String): ArrayList<Track>{
        return Gson().fromJson(json, itemType)
    }

    private fun createTrackFromJson(json: String?): Track{
        return Gson().fromJson(json, Track::class.java)
    }
}
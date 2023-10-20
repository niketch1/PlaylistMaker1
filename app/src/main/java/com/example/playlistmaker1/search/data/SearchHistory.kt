package com.example.playlistmaker1.search.data

import android.content.SharedPreferences
import com.example.playlistmaker1.search.ui.activity.SearchActivity.Companion.TRACK_LIST_KEY


class SearchHistory(val sharedPrefs : SharedPreferences?){

    fun reloadTracks(): String? {
        return sharedPrefs?.getString(TRACK_LIST_KEY, null)
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
}
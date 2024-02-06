package com.example.playlistmaker1.creator

import android.content.Context
import com.example.playlistmaker1.R
import com.example.playlistmaker1.search.domain.model.Track
import java.text.SimpleDateFormat
import java.util.Locale

object DateTimeUtil {

    fun showSumTracksTime(trackList: List<Track>, context: Context): String{
        val totalTimeLong = trackList.sumOf { it.trackTimeMillis }
        val totalTimeString = SimpleDateFormat("mm", Locale.getDefault()).format(totalTimeLong.toInt())
        return context.resources.getQuantityString(R.plurals.plurals_minute, totalTimeString.toInt(), totalTimeString)
    }
}
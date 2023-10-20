package com.example.playlistmaker1.player.ui

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DateFormatUtil {

    fun convertLongTimeToString(timeMillis : Long) : String{
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(timeMillis)
    }

    fun convertYearToString(year : String) : String{
        val date = SimpleDateFormat("yyyy", Locale.getDefault()).parse(year)
        return SimpleDateFormat("yyyy", Locale.getDefault()).format(date as Date)
    }
}
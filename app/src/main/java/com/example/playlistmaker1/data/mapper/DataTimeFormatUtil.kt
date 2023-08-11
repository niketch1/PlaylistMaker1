package com.example.playlistmaker1.data.mapper

import java.text.SimpleDateFormat
import java.util.Locale

class DataTimeFormatUtil {

    fun convertIntTimeToString(timeMillis : Int) : String{
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(timeMillis)
    }
}

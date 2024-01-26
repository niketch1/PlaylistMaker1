package com.example.playlistmaker1.creator

fun getTextEnding(number: Int): String{

    val preLastDigit: Int = number % 100 / 10
    if(preLastDigit == 1) return "треков"

    return when(number % 10){
        1 -> "трек"
        2, 3, 4 -> "трека"
        else -> "треков"
    }
}
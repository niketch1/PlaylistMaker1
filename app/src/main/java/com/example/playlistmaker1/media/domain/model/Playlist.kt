package com.example.playlistmaker1.media.domain.model


data class Playlist(
    val playlistId: Int = 0,  // Идентификатор плейлиста
    val playlistName: String = "", // Название плейлиста
    val playlistDescription: String = "", // Описание плейлиста
    val imageFilePath: String = "", // путь к файлу изображения обложки
    val trackIdList: MutableList<Int> = mutableListOf(), // список идентификаторов треков
    val numberOfTracks: Int = 0, // количество треков
)

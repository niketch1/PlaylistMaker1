package com.example.playlistmaker1.media.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_table")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val playlistId: Int,  // Идентификатор плейлиста
    val playlistName: String, // Название плейлиста
    val playlistDescription: String, // Описание плейлиста
    val imageFilePath: String, // путь к файлу изображения обложки
    val trackIdList: String, // список идентификаторов треков
    val numberOfTracks: Int, // количество треков
    val addTime: Long, //Время добавления
)

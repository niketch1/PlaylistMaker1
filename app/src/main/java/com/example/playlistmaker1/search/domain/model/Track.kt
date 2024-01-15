package com.example.playlistmaker1.search.domain.model

import java.io.Serializable

data class Track (
    val trackId: Int, // Идентификация треков
    val trackName: String, // Название композиции
    val artistName: String, // Имя исполнителя
    val trackTimeMillis: Long, // Продолжительность трека
    val artworkUrl100: String, // Ссылка на изображение обложки
    val collectionName: String, // Название альбома
    val releaseDate: String, // год релиза
    val primaryGenreName: String, // Жанр трека
    val country: String, // Страна исполнителя
    val previewUrl: String, // Отрывок
    val isFavorite: Boolean = false
) : Serializable{

/*    fun copy(changedProperty : Boolean) : Track {
        return Track(trackId, trackName, artistName, trackTimeMillis, artworkUrl100, collectionName, releaseDate, primaryGenreName, country, previewUrl, changedProperty)
    }*/
}
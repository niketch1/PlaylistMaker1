package com.example.playlistmaker1.search.data.dto

class TrackDto (
        val trackName: String, // Название композиции
        val artistName: String, // Имя исполнителя
        val trackTimeMillis: Long, // Продолжительность трека
        val artworkUrl100: String, // Ссылка на изображение обложки
        val trackId: Int, // Идентификация треков
        val collectionName: String, // Название альбома
        val releaseDate: String, // год релиза
        val primaryGenreName: String, // Жанр трека
        val country: String, // Страна исполнителя
        val previewUrl: String // Отрывок
        )

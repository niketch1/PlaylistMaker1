package com.example.playlistmaker1.media.data.converters

import com.example.playlistmaker1.media.data.db.entity.PlaylistEntity
import com.example.playlistmaker1.media.data.db.entity.TrackInPlaylistEntity
import com.example.playlistmaker1.media.domain.model.Playlist
import com.example.playlistmaker1.search.domain.model.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PlaylistDbConverter {
    private val itemType = object : TypeToken<ArrayList<Int>>() {}.type

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlistId = playlist.playlistId,
            playlistName = playlist.playlistName,
            playlistDescription = playlist.playlistDescription,
            imageFilePath = playlist.imageFilePath,
            trackIdList = createJsonFromTrackIdList(playlist.trackIdList),
            numberOfTracks = playlist.numberOfTracks,
            addTime = System.currentTimeMillis()
        )
    }

    fun map(playlistEntity: PlaylistEntity): Playlist {
        return Playlist(
            playlistId = playlistEntity.playlistId,
            playlistName = playlistEntity.playlistName,
            playlistDescription = playlistEntity.playlistDescription,
            imageFilePath = playlistEntity.imageFilePath,
            trackIdList = createTrackIdListFromJson(playlistEntity.trackIdList),
            numberOfTracks = playlistEntity.numberOfTracks
        )
    }

    fun map(track: Track): TrackInPlaylistEntity{
        return TrackInPlaylistEntity(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl,
            addTime = System.currentTimeMillis()
        )
    }

    fun map(trackInPlaylistEntity: TrackInPlaylistEntity): Track{
        return Track(
            trackId = trackInPlaylistEntity.trackId,
            trackName = trackInPlaylistEntity.trackName,
            artistName = trackInPlaylistEntity.artistName,
            trackTimeMillis = trackInPlaylistEntity.trackTimeMillis,
            artworkUrl100 = trackInPlaylistEntity.artworkUrl100,
            collectionName = trackInPlaylistEntity.collectionName,
            releaseDate = trackInPlaylistEntity.releaseDate,
            primaryGenreName = trackInPlaylistEntity.primaryGenreName,
            country = trackInPlaylistEntity.country,
            previewUrl = trackInPlaylistEntity.previewUrl,
        )
    }

    fun map(stringList: List<String>): List<ArrayList<Int>>{
        val list = stringList.map {
            createTrackIdListFromJson(it)
        }
        return list
    }

    fun createJsonFromTrackIdList(trackIdList: List<Int>): String {
        return Gson().toJson(trackIdList)
    }

    fun createTrackIdListFromJson(json: String?): ArrayList<Int>{
        return Gson().fromJson(json, itemType)
    }
}
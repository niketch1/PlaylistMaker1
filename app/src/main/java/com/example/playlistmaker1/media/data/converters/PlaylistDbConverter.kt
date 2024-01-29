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
            playlist.playlistId,
            playlist.playlistName,
            playlist.playlistDescription,
            playlist.imageFilePath,
            createJsonFromTrackIdList(playlist.trackIdList),
            playlist.numberOfTracks,
            System.currentTimeMillis())
    }

    fun map(playlistEntity: PlaylistEntity): Playlist {
        return Playlist(
            playlistEntity.playlistId,
            playlistEntity.playlistName,
            playlistEntity.playlistDescription,
            playlistEntity.imageFilePath,
            createTrackIdListFromJson(playlistEntity.trackIdList),
            playlistEntity.numberOfTracks)
    }

    fun map(track: Track): TrackInPlaylistEntity{
        return TrackInPlaylistEntity(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl,
            System.currentTimeMillis())
    }

    private fun createJsonFromTrackIdList(trackIdList: List<Int>): String {
        return Gson().toJson(trackIdList)
    }

    private fun createTrackIdListFromJson(json: String?): ArrayList<Int>{
        return Gson().fromJson(json, itemType)
    }
}
package com.example.playlistmaker1.media.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker1.media.data.db.dao.PlaylistDao
import com.example.playlistmaker1.media.data.db.dao.TrackDao
import com.example.playlistmaker1.media.data.db.dao.TrackInPlaylistDao
import com.example.playlistmaker1.media.data.db.entity.PlaylistEntity
import com.example.playlistmaker1.media.data.db.entity.TrackEntity
import com.example.playlistmaker1.media.data.db.entity.TrackInPlaylistEntity

@Database(version = 7, entities = [TrackEntity::class, PlaylistEntity::class, TrackInPlaylistEntity::class])
abstract class AppDatabase : RoomDatabase(){

    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun trackInPlaylistDao(): TrackInPlaylistDao
}
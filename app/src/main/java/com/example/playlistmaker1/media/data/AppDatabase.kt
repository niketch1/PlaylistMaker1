package com.example.playlistmaker1.media.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker1.media.data.db.dao.TrackDao
import com.example.playlistmaker1.media.data.db.entity.TrackEntity

@Database(version = 5, entities = [TrackEntity::class])
abstract class AppDatabase : RoomDatabase(){

    abstract fun trackDao(): TrackDao
}
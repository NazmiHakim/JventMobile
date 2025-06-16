package com.example.jvent.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.jvent.model.EventEntity

@Database(
    entities = [EventEntity::class],
    version = 1
)
abstract class EventDatabase : RoomDatabase() {
    abstract val dao: EventDao
}
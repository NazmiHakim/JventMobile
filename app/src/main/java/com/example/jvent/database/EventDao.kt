package com.example.jvent.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.jvent.model.Event
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    // Returns a Flow, so the UI is automatically updated on data changes
    @Query("SELECT * FROM events ORDER BY createdAt DESC")
    fun getAllEvents(): Flow<List<Event>>

    // Inserts a list of events. Replaces on conflict.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(events: List<Event>)

    // Deletes all events from the table
    @Query("DELETE FROM events")
    suspend fun deleteAll()
}
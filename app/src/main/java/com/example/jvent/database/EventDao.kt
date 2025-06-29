package com.example.jvent.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.jvent.model.Event
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

@Dao
interface EventDao {

    @Query("SELECT * FROM events ORDER BY createdAt DESC")
    fun getAllEvents(): Flow<List<Event>>

    @Query("SELECT * FROM events WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavoriteEvents(): Flow<List<Event>>

    @Query("SELECT * FROM events WHERE id = :id")
    fun getEventById(id: String): Flow<Event?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(events: List<Event>)

    @Update
    suspend fun updateEvent(event: Event)

    @Query("DELETE FROM events")
    suspend fun deleteAll()

    @Transaction
    suspend fun refreshEvents(events: List<Event>) {
        val favoriteIds = getFavoriteEvents().first().map { it.id }
        deleteAll()
        val newEvents = events.map { event ->
            if (favoriteIds.contains(event.id)) {
                event.copy(isFavorite = true)
            } else {
                event
            }
        }
        insertAll(newEvents)
    }
}
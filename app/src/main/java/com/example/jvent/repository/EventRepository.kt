package com.example.jvent.repository

import android.util.Log
import com.example.jvent.database.EventDao
import com.example.jvent.model.Event
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class EventRepository(private val eventDao: EventDao) {

    private val firestore = FirebaseFirestore.getInstance()

    val allEvents: Flow<List<Event>> = eventDao.getAllEvents()

    /**
     * Fetches fresh events from Firestore, correctly maps the document ID to the event's id field,
     * and then updates the local Room database.
     */
    suspend fun refreshEvents() {
        try {
            val snapshot = firestore.collection("events").get().await()

            // This block correctly maps each document to an Event object including its ID
            val events = snapshot.documents.mapNotNull { document ->
                // Convert document to Event object
                val event = document.toObject(Event::class.java)
                // IMPORTANT: This line copies the event and inserts the document's ID
                event?.copy(id = document.id)
            }

            // Insert the corrected list into the database
            eventDao.insertAll(events)
            Log.d("EventRepository", "Events refreshed from Firestore and cached in Room.")
        } catch (e: Exception) {
            Log.e("EventRepository", "Error refreshing events", e)
        }
    }
}
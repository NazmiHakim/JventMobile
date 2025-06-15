package com.example.jvent.data.repository

import com.example.jvent.domain.repository.EventRepository
import com.example.jvent.model.Event
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class EventRepositoryImpl(
    private val firestore: FirebaseFirestore
) : EventRepository {

    override fun getEvents(): Flow<List<Event>> {
        return firestore.collection("events")
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(Event::class.java)
            }
    }

    override suspend fun getEventById(eventId: String): Event? {
        return try {
            val snapshot = firestore.collection("events").document(eventId).get().await()
            snapshot.toObject(Event::class.java)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun addEvent(event: Event) {
        firestore.collection("events").add(event).await()
    }

    override suspend fun updateEvent(event: Event) {
        firestore.collection("events").document(event.id).set(event).await()
    }

    override suspend fun deleteEvent(eventId: String) {
        firestore.collection("events").document(eventId).delete().await()
    }
}
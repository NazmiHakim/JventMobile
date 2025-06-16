package com.example.jvent.data.repository

import com.example.jvent.domain.repository.EventRepository
import com.example.jvent.local.EventDao
import com.example.jvent.model.Event
import com.example.jvent.model.toEntity
import com.example.jvent.model.toModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class EventRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val dao: EventDao
) : EventRepository {

    override fun getEvents(): Flow<List<Event>> {
        return firestore.collection("events")
            .snapshots()
            .map { snapshot ->
                val events = snapshot.toObjects(Event::class.java)
                dao.insertEvents(events.map { it.toEntity() })
                events
            }
    }

    override suspend fun getEventById(eventId: String): Event? {
        return try {
            val snapshot = firestore.collection("events").document(eventId).get().await()
            snapshot.toObject(Event::class.java)
        } catch (e: Exception) {
            dao.getEvents().map { it.firstOrNull { eventEntity -> eventEntity.id == eventId }?.toModel() }.toString()
            null
        }
    }

    override suspend fun addEvent(event: Event) {
        val docRef = firestore.collection("events").add(event).await()
        val newEvent = event.copy(id = docRef.id)
        dao.insertEvents(listOf(newEvent.toEntity()))
    }

    override suspend fun updateEvent(event: Event) {
        firestore.collection("events").document(event.id).set(event).await()
        dao.insertEvents(listOf(event.toEntity()))
    }

    override suspend fun deleteEvent(eventId: String) {
        firestore.collection("events").document(eventId).delete().await()
    }
}
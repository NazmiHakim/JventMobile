package com.example.jvent.repository

import android.util.Log
import com.example.jvent.database.EventDao
import com.example.jvent.model.Event
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class EventRepository(private val eventDao: EventDao) {

    private val firestore = FirebaseFirestore.getInstance()

    val allEvents: Flow<List<Event>> = eventDao.getAllEvents()

    init {
        listenForEventUpdates()
    }

    private fun listenForEventUpdates() {
        firestore.collection("events").addSnapshotListener { snapshots, e ->
            if (e != null) {
                Log.w("EventRepository", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshots != null) {
                val events = snapshots.toObjects(Event::class.java)
                CoroutineScope(Dispatchers.IO).launch {
                    eventDao.insertAll(events)
                    Log.d("EventRepository", "Events refreshed from Firestore and cached in Room.")
                }
            }
        }
    }
}
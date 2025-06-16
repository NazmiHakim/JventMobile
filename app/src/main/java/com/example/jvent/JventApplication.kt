package com.example.jvent

import android.app.Application
import com.example.jvent.database.EventDatabase
import com.example.jvent.repository.EventRepository

class JventApplication : Application() {
    // Lazily initialize database and repository
    val database by lazy { EventDatabase.getDatabase(this) }
    val repository by lazy { EventRepository(database.eventDao()) }
}
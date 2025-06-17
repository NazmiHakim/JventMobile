package com.example.jvent

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.example.jvent.database.EventDatabase
import com.example.jvent.repository.EventRepository

class JventApplication : Application() {
    // Lazily initialize database and repository
    private val database by lazy { EventDatabase.getDatabase(this) }
    val repository by lazy { EventRepository(database.eventDao()) }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        val name = "New Event"
        val descriptionText = "Notifications for new events"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("NEW_EVENT_CHANNEL_ID", name, importance).apply {
            description = descriptionText
        }
        // Register the channel with the system
        val notificationManager: NotificationManager =
            getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }
}
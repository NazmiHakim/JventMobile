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
        // Channel untuk event baru
        val name = "Event Baru"
        val descriptionText = "Notifikasi untuk event baru"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("NEW_EVENT_CHANNEL_ID", name, importance).apply {
            description = descriptionText
        }

        // Channel untuk pengingat
        val reminderChannelName = "Pengingat Event"
        val reminderChannelDesc = "Notifikasi untuk mengingatkan event yang akan datang"
        val reminderChannelImportance = NotificationManager.IMPORTANCE_DEFAULT
        val reminderChannel = NotificationChannel("REMINDER_CHANNEL_ID", reminderChannelName, reminderChannelImportance).apply {
            description = reminderChannelDesc
        }

        val notificationManager: NotificationManager =
            getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
        notificationManager.createNotificationChannel(reminderChannel)
    }
}
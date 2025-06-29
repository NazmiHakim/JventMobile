package com.example.jvent

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.example.jvent.database.EventDatabase
import com.example.jvent.repository.EventRepository
import com.google.firebase.crashlytics.FirebaseCrashlytics

class JventApplication : Application() {
    // Lazily initialize database and repository
    private val database by lazy { EventDatabase.getDatabase(this) }
    val repository by lazy { EventRepository(database.eventDao()) }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        FirebaseCrashlytics.getInstance().log("Aplikasi Jvent dimulai")
    }

    private fun createNotificationChannel() {
        val newEventChannelName = getString(R.string.new_event_channel_name)
        val newEventChannelDesc = getString(R.string.new_event_channel_desc)
        val newEventImportance = NotificationManager.IMPORTANCE_DEFAULT
        val newEventChannel = NotificationChannel("NEW_EVENT_CHANNEL_ID", newEventChannelName, newEventImportance).apply {
            description = newEventChannelDesc
        }

        // Channel untuk Pengingat Event
        val reminderChannelName = getString(R.string.reminder_channel_name)
        val reminderChannelDesc = getString(R.string.reminder_channel_desc)
        val reminderChannelImportance = NotificationManager.IMPORTANCE_HIGH
        val reminderChannel = NotificationChannel("REMINDER_CHANNEL_ID", reminderChannelName, reminderChannelImportance).apply {
            description = reminderChannelDesc
        }

        val notificationManager: NotificationManager =
            getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(newEventChannel)
        notificationManager.createNotificationChannel(reminderChannel)
    }
}
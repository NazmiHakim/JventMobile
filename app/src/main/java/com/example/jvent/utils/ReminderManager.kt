package com.example.jvent.utils

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.jvent.model.Event
import com.example.jvent.workers.NotificationWorker
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

object ReminderManager {

    fun setReminder(context: Context, event: Event) {
        val workManager = WorkManager.getInstance(context)
        val eventDateStr = event.dateTime
        val eventName = event.title
        val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val eventDate = try {
            format.parse(eventDateStr)
        } catch (e: Exception) {
            Log.e("ReminderManager", "Gagal mem-parsing tanggal: $eventDateStr", e)
            null
        }
        val currentTime = System.currentTimeMillis()

        if (eventDate != null) {
            val timeDiff = eventDate.time - currentTime
            if (timeDiff <= 0) {
                Log.w("ReminderManager", "Event sudah lewat, tidak ada pengingat yang diatur.")
                return
            }

            val countdowns = listOf(
                Triple(3, TimeUnit.DAYS, "3 hari"),
                Triple(1, TimeUnit.DAYS, "1 hari"),
                Triple(12, TimeUnit.HOURS, "12 jam"),
                Triple(1, TimeUnit.HOURS, "1 jam"),
                Triple(5, TimeUnit.MINUTES, "5 menit"),
                Triple(1, TimeUnit.MINUTES, "1 menit")
            )

            for ((value, unit, countdownText) in countdowns) {
                val delay = timeDiff - unit.toMillis(value.toLong())
                if (delay > 0) {
                    val data = Data.Builder()
                        .putString("EVENT_NAME", eventName)
                        .putString("COUNTDOWN", countdownText)
                        .build()

                    val reminderWorkRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
                        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                        .setInputData(data)
                        .addTag(event.id)
                        .build()

                    workManager.enqueue(reminderWorkRequest)
                }
            }
            Log.d("ReminderManager", "Pengingat diatur untuk event: ${event.title} dengan ID: ${event.id}")
        }
    }

    fun cancelReminder(context: Context, eventId: String) {
        WorkManager.getInstance(context).cancelAllWorkByTag(eventId)
        Log.d("ReminderManager", "Pengingat untuk event ID: $eventId dibatalkan.")
    }
}
package com.example.jvent.workers

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.jvent.MainActivity
import com.example.jvent.R

class NotificationWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val eventName = inputData.getString("EVENT_NAME")
        val countdown = inputData.getString("COUNTDOWN")

        if (eventName == null || countdown == null) {
            Log.e("NotificationWorker", "Gagal mendapatkan data event dari input.")
            return Result.failure()
        }

        Log.d("NotificationWorker", "Worker berjalan untuk event: $eventName, hitungan mundur: $countdown")
        sendNotification(eventName, countdown)

        return Result.success()
    }

    private fun sendNotification(eventName: String, countdown: String) {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notificationId = System.currentTimeMillis().toInt()
        val notification = NotificationCompat.Builder(applicationContext, "REMINDER_CHANNEL_ID")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(applicationContext.getString(R.string.event_reminder_title))
            .setContentText(applicationContext.getString(R.string.event_reminder_content, eventName, countdown))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(applicationContext)) {
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                Log.d("NotificationWorker", "Mengirim notifikasi untuk event: $eventName")
                notify(notificationId, notification.build())
            } else {
                Log.e("NotificationWorker", "Izin notifikasi tidak diberikan.")
            }
        }
    }
}